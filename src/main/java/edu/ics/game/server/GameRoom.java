package edu.ics.game.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class GameRoom {
	private Class<? extends Game> gameClass;
	private Game game = null;
	private String name;
	private Map<GamePlayer,GameRoomPlayerStatus> playerStatus = new HashMap<>();
	private List<GamePlayer> players = new ArrayList<>();

	public GameRoom(Class<? extends Game> gameClass, String name) {
		this.gameClass = gameClass;
		try {
			this.game = this.gameClass.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public JsonNode getState() {
		return this.getState(true);
	}

	public JsonNode getState(boolean includeGameState) {
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode state = mapper.createObjectNode();

		state.put("name", this.getName());
		state.put("status", this.getStatus().toString());

		ArrayNode playerStates = state.putArray("players");
		for (GamePlayer player : this.players) {
			ObjectNode playerState = mapper.createObjectNode();
			playerState.put("name", player.getName());
			playerState.put("status", this.playerStatus.get(player).toString());
			playerStates.add(playerState);
		}

		if (includeGameState && this.game != null) {
			state.set("game", this.game.getState());
		}

		return state;
	}

	public List<GamePlayer> getPlayers() {
		return this.players;
	}

	private void sortPlayersByStatus() {
		this.players.sort((e1, e2) -> this.playerStatus.get(e1).compareTo(this.playerStatus.get(e2)));
	}

	public void addPlayer(GamePlayer player) {
		this.players.add(player);
		if (this.getStatus() == GameRoomPlayerStatus.PLAYING || this.getPlayersReady() + this.getPlayersWaiting() >= this.getPlayersMax()) {
			this.playerStatus.put(player, GameRoomPlayerStatus.WATCHING);
		} else {
			this.playerStatus.put(player, GameRoomPlayerStatus.WAITING);
		}
		this.sortPlayersByStatus();
	}

	public void removePlayer(GamePlayer player) {
		if (this.getStatus() == GameRoomPlayerStatus.PLAYING && this.playerStatus.get(player) == GameRoomPlayerStatus.PLAYING) {
			for (GamePlayer _player : this.players) {
				if (this.playerStatus.get(_player) == GameRoomPlayerStatus.PLAYING) {
					this.playerStatus.put(_player, GameRoomPlayerStatus.WAITING);
				}
			}
		}

		this.players.remove(player);
		this.playerStatus.remove(player);
		this.sortPlayersByStatus();
	}

	public GameRoomPlayerStatus getStatus() {
		int waitCount = 0;
		int readyCount = 0;
		for (GamePlayer player: this.getPlayers()) {
			if (this.playerStatus.get(player) == GameRoomPlayerStatus.PLAYING) {
				return GameRoomPlayerStatus.PLAYING;
			} else if (this.playerStatus.get(player) == GameRoomPlayerStatus.READY) {
				readyCount++;
			} else if (this.playerStatus.get(player) == GameRoomPlayerStatus.WAITING) {
				waitCount++;
			}
		}
		if (readyCount >= this.getPlayersMin() && waitCount == 0) {
			return GameRoomPlayerStatus.READY;
		}
		return GameRoomPlayerStatus.WAITING;
	}

	public void ready(GamePlayer player) {
		if (this.getStatus() == GameRoomPlayerStatus.WAITING && this.playerStatus.get(player) == GameRoomPlayerStatus.WAITING) {
			this.playerStatus.put(player, GameRoomPlayerStatus.READY);
			this.sortPlayersByStatus();

			if (this.getStatus() == GameRoomPlayerStatus.READY) {
				this.play();
			}
		}
	}

	public void wait(GamePlayer player) {
		if (this.getStatus() == GameRoomPlayerStatus.WAITING && this.getPlayersWaiting() + this.getPlayersReady() < this.getPlayersMax()) {
			this.playerStatus.put(player, GameRoomPlayerStatus.WAITING);
			this.sortPlayersByStatus();
		}
	}

	private void play() {
		try {
			this.game = this.gameClass.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
		for (GamePlayer player : this.players) {
			if (this.playerStatus.get(player) == GameRoomPlayerStatus.READY) {
				this.playerStatus.put(player, GameRoomPlayerStatus.PLAYING);
			}
		}
		this.sortPlayersByStatus();
	}

	public void play(GamePlayer player, int... args) {
		if (this.getStatus() == GameRoomPlayerStatus.PLAYING && this.players.indexOf(player) == this.game.getCurrentPlayer()) {
			this.game.play(args);
			if (this.game.isEnded()) {
				for (GamePlayer _player : this.players) {
					if (this.playerStatus.get(_player) == GameRoomPlayerStatus.PLAYING) {
						this.playerStatus.put(_player, GameRoomPlayerStatus.WAITING);
					}
				}
				this.sortPlayersByStatus();
			}
		}
	}

	public int getPlayersMin() {
		try {
			return this.gameClass.getField("PLAYERS_MIN").getInt(null);
		} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
			e.printStackTrace();
			return -1;
		}
	}

	public int getPlayersMax() {
		try {
			return this.gameClass.getField("PLAYERS_MAX").getInt(null);
		} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
			e.printStackTrace();
			return -1;
		}
	}

	public int getPlayersReady() {
		int count = 0;
		for (GameRoomPlayerStatus status : this.playerStatus.values()) {
			if (status == GameRoomPlayerStatus.READY) {
				count++;
			}
		}
		return count;
	}

	public int getPlayersWaiting() {
		int count = 0;
		for (GameRoomPlayerStatus status : this.playerStatus.values()) {
			if (status == GameRoomPlayerStatus.WAITING) {
				count++;
			}
		}
		return count;
	}
}