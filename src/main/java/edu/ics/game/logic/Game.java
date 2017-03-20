package edu.ics.game.logic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public abstract class Game {
	public static final List<Class<? extends Game>> AVAILABLE_GAMES = new ArrayList<>(Arrays.asList(
			Checkers.class,
			Othello.class,
			TicTacToe.class
			));


	public static int PLAYERS_MIN = 2;
	public static int PLAYERS_MAX = 2;

	protected int players = 2;
	protected int currentPlayer = 0;
	protected int winner = -1;

	protected boolean ended;

	protected int columns;
	protected int rows;
	protected int[][] board;

	protected Game() {}

	protected Game(int columns, int rows) {
		this.ended = false;
		this.columns = columns;
		this.rows = rows;
		this.board = new int[columns][rows];
		
		for (int column = 0; column < columns; column++) {
			for (int row = 0; row < rows; row++) {
				this.board[column][row] = -1;
			}
		}
	}

	public JsonNode getState() {
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode state = mapper.createObjectNode();

		state.put("name", this.getClass().getSimpleName());

		state.put("rows", this.rows);
		state.put("columns", this.columns);

		ArrayNode boardState = state.putArray("board");

		int[][] board = this.getBoard();
		for (int column = 0; column < this.columns; column++) {
			ArrayNode columnState = boardState.addArray();
			for (int row = 0; row < this.rows; row++) {
				columnState.add(board[column][row]);
			}
		}

		state.put("turn", this.currentPlayer);
		state.put("ended", this.ended);
		state.put("winner", this.winner);

		return state;
	};

	protected boolean isInBounds(int column, int row) {
		if (column >= 0 && column < this.columns && row >= 0 && row < this.rows) {
			return true;
		}
		return false;
	}

	protected boolean isEmpty(int column, int row) {
		return this.isInBounds(column, row) && this.board[column][row] == -1;
	}

	public boolean isEnded() {
		return ended;
	}

	public int[][] getBoard() {
		return this.board;
	}

	public int getCurrentPlayer() {
		return currentPlayer;
	}

	public int getNextPlayer() {
		return (this.currentPlayer + 1) % this.players;
	}

	protected void setCurrentPlayer(int currentPlayer) {
		this.currentPlayer = currentPlayer;
	}

	protected void next() {
		this.currentPlayer = this.getNextPlayer();
	}

	public abstract void play(int... args);
}
