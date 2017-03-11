package edu.ics.game.server;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public abstract class Game {
	public static final List<Class<? extends Game>> AVAILABLE_GAMES = new ArrayList<>(Arrays.asList(
			TicTacToe.class,
			Checkers.class,
			Othello.class
			));


	public static int PLAYERS_MIN = 2;
	public static int PLAYERS_MAX = 2;

	protected int players = 2;
	protected int currentPlayer = 0;
	protected int winner = -1;

	protected boolean ended;

	protected int width;
	protected int height;
	protected int[][] board;

	protected Game(int width, int height) {
		this.ended = false;
		this.width = width;
		this.height = height;
		this.board = new int[width][height];

		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				this.board[x][y] = -1;
			}
		}
	}

	public JsonNode getState() {
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode state = mapper.createObjectNode();

		state.put("name", this.getClass().getSimpleName());

		ArrayNode boardState = state.putArray("board");
		for (int x = 0; x < this.width; x++) {
			ArrayNode rowState = boardState.addArray();
			for (int y = 0; y < this.height; y++) {
				rowState.add(this.board[x][y]);
			}
		}

		state.put("ended", this.ended);
		state.put("winner", this.winner);

		return state;
	};

	protected boolean isInBounds(int x, int y) {
		if (x >= 0 && x < this.width && y >= 0 && y < this.height) {
			return true;
		}
		return false;
	}

	protected boolean isEmpty(int x, int y) {
		if (this.isInBounds(x, y) && this.board[x][y] == -1) {
			return true;
		}
		return false;
	}

	public boolean isEnded() {
		return ended;
	}

	public int getCurrentPlayer() {
		return currentPlayer;
	}

	protected void setCurrentPlayer(int currentPlayer) {
		this.currentPlayer = currentPlayer;
	}

	protected void next() {
		this.currentPlayer = (this.currentPlayer + 1) % this.players;
	}

	public abstract void play(int... args);
}
