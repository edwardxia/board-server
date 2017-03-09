package edu.ics.game.server;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
}
