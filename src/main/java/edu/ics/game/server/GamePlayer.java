package edu.ics.game.server;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class GamePlayer {
	private String name = "Anonymous";
	private UUID uuid;
	private List<GameRoom> rooms = new ArrayList<>();

	public GamePlayer(UUID uuid) {
		this.uuid = uuid;
	}

	public UUID getUuid() {
		return uuid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void enterRoom(GameRoom room) {
		this.rooms.add(room);
	}

	public List<GameRoom> getRooms() {
		return this.rooms;
	}

	public void leaveRoom(GameRoom room) {
		this.rooms.remove(room);
	}

	public JsonNode getState() {
		ObjectMapper mapper = new ObjectMapper();

		ObjectNode state = mapper.createObjectNode();
		state.put("name", this.getName());
		if (this.rooms.size() > 0) {
			this.rooms.sort((e1, e2) -> e1.getStatus().compareTo(e2.getStatus()));
			state.put("status", this.rooms.get(0).getStatus().toString());
		} else {
			state.put("status", GameRoomPlayerStatus.IDLE.toString());
		}

		return state;
	}
}
