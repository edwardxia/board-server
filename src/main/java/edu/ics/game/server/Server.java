package edu.ics.game.server;

import java.util.ArrayList;
import java.util.List;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIONamespace;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DataListener;
import com.corundumstudio.socketio.listener.DisconnectListener;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class Server {
	private SocketIOServer server;

	public Server() throws InterruptedException {

		Configuration config = new Configuration();
		config.setPort(3000);

		server = new SocketIOServer(config);

		server.addEventListener("chat", JsonNode.class, new DataListener<JsonNode>() {
			public void onData(SocketIOClient client, JsonNode data, AckRequest ackSender) throws Exception {
				if (data.hasNonNull("channel")) {
					String channel = data.get("channel").asText();
					if (channel.equals("global")) {
						server.getBroadcastOperations().sendEvent("chat", client, data);
					}
				}
			}
		});

		for (Class<? extends Game> gameClass : Game.AVAILABLE_GAMES) {
			SocketIONamespace namespace = server.addNamespace("/" + gameClass.getSimpleName());
			GameLobby lobby = new GameLobby(gameClass);

			namespace.addConnectListener(new ConnectListener() {
				public void onConnect(SocketIOClient client) {
					lobby.addPlayerByUUID(client.getSessionId());

					namespace.getBroadcastOperations().sendEvent("lobby", client, lobby.getState());
				}
			});

			namespace.addDisconnectListener(new DisconnectListener() {
				public void onDisconnect(SocketIOClient client) {
					List<String> roomNamesToLeave = new ArrayList<>();
					for (GameRoom room : lobby.getPlayerByUUID(client.getSessionId()).getRooms()) {
						roomNamesToLeave.add(room.getName());
					}
					for (String _roomName : roomNamesToLeave) {
						client.leaveRoom(_roomName);
						lobby.leaveRoom(client.getSessionId(), _roomName);

						// Update whoever still in the room
						GameRoom room = lobby.getRoomByName(_roomName);
						if (room != null) {
							namespace.getRoomOperations(_roomName).sendEvent("room", room.getState());
						}
					}

					lobby.removePlayerByUUID(client.getSessionId());

					namespace.getBroadcastOperations().sendEvent("lobby", lobby.getState());
				}
			});

			namespace.addEventListener("chat", JsonNode.class, new DataListener<JsonNode>() {
				public void onData(SocketIOClient client, JsonNode data, AckRequest ackSender) throws Exception {
					if (data.hasNonNull("channel")) {
						String channel = data.get("channel").asText();
						if (channel.equals("room")) {
							namespace.getRoomOperations(data.get("room").asText()).sendEvent("chat", client, data);
						} else if (channel.equals("lobby")) {
							namespace.getBroadcastOperations().sendEvent("chat", client, data);
						}
					}
				}				
			});

			namespace.addEventListener("name", JsonNode.class, new DataListener<JsonNode>() {
				public void onData(SocketIOClient client, JsonNode data, AckRequest ackRequest) throws Exception {
					String name = data.asText();

					lobby.getPlayerByUUID(client.getSessionId()).setName(name);
					namespace.getBroadcastOperations().sendEvent("lobby", lobby.getState());
				}
			});

			namespace.addEventListener("state", JsonNode.class, new DataListener<JsonNode>() {
				public void onData(SocketIOClient client, JsonNode data, AckRequest ackSender) throws Exception {
					if (data != null && data.isTextual()) {
						client.sendEvent("room", lobby.getRoomByName(data.asText()).getState());
					} else {
						client.sendEvent("lobby", lobby.getState());
					}
				}
			});

			namespace.addEventListener("join", JsonNode.class, new DataListener<JsonNode>() {
				public void onData(SocketIOClient client, JsonNode data, AckRequest ackRequest) throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
					String roomName = data.asText();

					GamePlayer player = lobby.getPlayerByUUID(client.getSessionId());
					// leave current room(s) before join a new one.
					List<String> roomNamesToLeave = new ArrayList<>();
					for (GameRoom room : player.getRooms()) {
						roomNamesToLeave.add(room.getName());
					}
					for (String _roomName : roomNamesToLeave) {
						client.leaveRoom(_roomName);
						lobby.leaveRoom(client.getSessionId(), _roomName);

						// Update whoever still in the room
						GameRoom room = lobby.getRoomByName(_roomName);
						if (room != null) {
							namespace.getRoomOperations(_roomName).sendEvent("room", room.getState());
						}
					}

					lobby.joinRoom(client.getSessionId(), roomName);
					client.joinRoom(roomName);

					namespace.getRoomOperations(roomName).sendEvent("room", lobby.getRoomByName(roomName).getState());

					namespace.getBroadcastOperations().sendEvent("lobby", lobby.getState());
				}
			});

			namespace.addEventListener("leave", JsonNode.class, new DataListener<JsonNode>() {
				public void onData(SocketIOClient client, JsonNode data, AckRequest ackRequest) {
					if (data != null && data.isTextual()) {
						String roomName = data.asText();

						client.leaveRoom(roomName);
						lobby.leaveRoom(client.getSessionId(), roomName);

						// Update whoever still in the room
						GameRoom room = lobby.getRoomByName(roomName);
						if (room != null) {
							namespace.getRoomOperations(roomName).sendEvent("room", room.getState());
						}
					} else {
						// leave all rooms.
						List<String> roomNamesToLeave = new ArrayList<>();
						for (GameRoom room : lobby.getPlayerByUUID(client.getSessionId()).getRooms()) {
							roomNamesToLeave.add(room.getName());
						}
						for (String _roomName : roomNamesToLeave) {
							client.leaveRoom(_roomName);
							lobby.leaveRoom(client.getSessionId(), _roomName);

							// Update whoever still in the room
							GameRoom room = lobby.getRoomByName(_roomName);
							if (room != null) {
								namespace.getRoomOperations(_roomName).sendEvent("room", room.getState());
							}						
						}
					}

					namespace.getBroadcastOperations().sendEvent("lobby", lobby.getState());
				}
			});

			namespace.addEventListener("ready", JsonNode.class, new DataListener<JsonNode>() {
				public void onData(SocketIOClient client, JsonNode data, AckRequest ackRequest) {
					GameRoom room = null;
					if (data != null && data.isTextual()) {
						String roomName = data.asText();
						room = lobby.getRoomByName(roomName);
					}
					if (room == null) {
						room = lobby.getPlayerByUUID(client.getSessionId()).getRooms().get(0);
					}
					if (room != null) {
						room.ready(lobby.getPlayerByUUID(client.getSessionId()));

						if (room.getStatus() == GameRoomPlayerStatus.PLAYING) {
							for (int i = 0; i < room.getPlayers().size(); i++) {
								ObjectNode roomState = (ObjectNode)room.getState();
								roomState.put("playerIndex", i);
								namespace.getClient(room.getPlayers().get(i).getUuid()).sendEvent("room", roomState);
							}
						} else {
							namespace.getRoomOperations(room.getName()).sendEvent("room", room.getState());
						}
					}

					namespace.getBroadcastOperations().sendEvent("lobby", lobby.getState());
				}
			});

			namespace.addEventListener("wait", JsonNode.class, new DataListener<JsonNode>() {
				public void onData(SocketIOClient client, JsonNode data, AckRequest ackRequest) {
					GameRoom room = null;
					if (data != null && data.isTextual()) {
						String roomName = data.asText();
						room = lobby.getRoomByName(roomName);
					}
					if (room == null) {
						room = lobby.getPlayerByUUID(client.getSessionId()).getRooms().get(0);
					}
					if (room != null) {
						room.wait(lobby.getPlayerByUUID(client.getSessionId()));
						namespace.getRoomOperations(room.getName()).sendEvent("room", room.getState());
					}

					namespace.getBroadcastOperations().sendEvent("lobby", lobby.getState());
				}
			});

			namespace.addEventListener("play", JsonNode.class, new DataListener<JsonNode>() {
				public void onData(SocketIOClient client, JsonNode data, AckRequest ackRequest) {
					if (data != null && data.hasNonNull("move")) {
						GameRoom room = null;
						if (data.hasNonNull("room")) {
							room = lobby.getRoomByName(data.get("room").asText());
						} else {
							room = lobby.getPlayerByUUID(client.getSessionId()).getRooms().get(0);
						}
						if (room != null) {
							int[] move = new int[data.withArray("move").size()];
							int idx = 0;
							for (JsonNode node : data.withArray("move")) {
								move[idx] = node.asInt();
								idx++;
							};
							room.play(lobby.getPlayerByUUID(client.getSessionId()), move);
														
							namespace.getRoomOperations(room.getName()).sendEvent("room", room.getState());
							room.turn(lobby.getPlayerByUUID(client.getSessionId()));	


							if (room.getStatus() != GameRoomPlayerStatus.PLAYING) {
								namespace.getBroadcastOperations().sendEvent("lobby", lobby.getState());
							}
						}
					}
				}
			});
		}

		server.start();
		Thread.sleep(Integer.MAX_VALUE);
		server.stop();
	}
}
