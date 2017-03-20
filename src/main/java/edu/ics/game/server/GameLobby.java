package edu.ics.game.server;

import java.util.HashMap;
import java.util.UUID;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class GameLobby {
	private Class<? extends Game> gameClass;
	private HashMap<String,GameRoom> rooms = new HashMap<>();
	private HashMap<UUID,GamePlayer> players = new HashMap<>();

	public GameLobby(Class<? extends Game> gameClass) {
		this.gameClass = gameClass;
	}

	public Class<? extends Game> getGameClass() {
		return this.gameClass;
	}

	public JsonNode getState() {
		ObjectMapper mapper = new ObjectMapper();

		ObjectNode state = mapper.createObjectNode();

		state.put("name", this.gameClass.getSimpleName());

		ArrayNode rooms = state.putArray("rooms");
		for (GameRoom room : this.rooms.values()) {
			rooms.add(room.getState(false));
		}

		ArrayNode players = state.putArray("players");
		for (GamePlayer player : this.players.values()) {
			players.add(player.getState());
		}
		return state;
	}

	public GamePlayer addPlayerByUUID(UUID uuid) {
		GamePlayer player = new GamePlayer(uuid);
		this.players.put(uuid, player);
		return player;
	}

	public GamePlayer getPlayerByUUID(UUID uuid) {
		return this.players.get(uuid);
	}

	public void removePlayerByUUID(UUID uuid) {
		this.players.remove(uuid);
	}

	public GameRoom addRoomByName(String name) {
		GameRoom room = new GameRoom(this.gameClass, name);
		this.rooms.put(name, room);
		return room;
	}

	public GameRoom getRoomByName(String name) {
		return this.rooms.get(name);
	}

	public void removeRoomByName(String name) {
		this.rooms.remove(name);
	}

	public void joinRoom(UUID uuid, String name) {
		GamePlayer player = this.getPlayerByUUID(uuid);

		GameRoom room = this.getRoomByName(name);
		if (room == null) {
			room = this.addRoomByName(name);
		}

		room.addPlayer(player);
		player.enterRoom(room);
	}

	public void leaveRoom(UUID uuid, String name) {
		GamePlayer player = this.getPlayerByUUID(uuid);

		GameRoom room = this.getRoomByName(name);
		if (room != null) {
			room.removePlayer(player);
			player.leaveRoom(room);

			// Destroy the room if no one is in it.
			if (room.getPlayers().size() == 0) {
				this.removeRoomByName(name);
			}
		}
	}
}
