package edu.ics.game.server;
import java.util.ArrayList;
import java.util.List;

public class Othello extends Game {

	public Othello() {
		super(8, 8);

		// initialize board pieces
		this.board[3][3] = 0;
		this.board[3][4] = 1;
		this.board[4][3] = 1;
		this.board[4][4] = 0;
	}

	public void play(int... args) {
		if (args.length >= 2) {
			int row = args[0];
			int column = args[1];

			if (isValidMove(row, column)) {
				this.flip(row, column);
				this.next();
			}

			if (!hasValidMoves()) {	//if opponent doesn't have any valid moves return control back to current player
				this.next();

				if (!hasValidMoves()) {	//if current player also doesn't have any valid moves the game ends
					this.ended = true;

					int countCurrentPlayer = this.count(this.currentPlayer);
					int countOpponentPlayer = this.count(this.getOpponentPlayer());
					if (countCurrentPlayer > countOpponentPlayer) {
						this.winner = this.currentPlayer;
					} else if (countCurrentPlayer < countOpponentPlayer) {
						this.winner = this.getOpponentPlayer();
					}
				}
			}
		}
	}

	private List<Coordinates> getFlipped(int row, int column) {
		List<Coordinates> flipped = new ArrayList<>();

		for (int _r = -1; _r <= 1; _r++) {
			for (int _c = -1; _c <= 1; _c++) {
				if (_r == 0 && _c == 0) {
					continue;
				}
								
				for (int r = row + _r, c = column + _c; this.isInBounds(r, c) && !this.isEmpty(r, c); r += _r, c += _c) {
					if (this.board[r][c] == this.currentPlayer) {
						for (r -= _r, c -= _c; r != row || c != column; r -= _r, c -= _c) {
							flipped.add(new Coordinates(r, c));
						}
						break;
					}

				}
			}
		}
		return flipped;
	}

	private boolean isValidMove(int row, int column) {
		if (!this.isInBounds(row, column) || !this.isEmpty(row, column)) {
			return false;
		}

		if (this.getFlipped(row, column).isEmpty()) {
			return false;
		}

		return true;
	}

	private boolean hasValidMoves() {
		for (int y = 0; y < this.height; y++) {
			for (int x = 0; x < this.width; x++) {
				if (isValidMove(y, x)) {
					return true;
				}
			}
		}
		return false;
	}
	
	private void flip(int row, int column) {
		List<Coordinates> flipped = this.getFlipped(row, column);

		for(int i = 0; i < flipped.size(); i++) {
			this.board[flipped.get(i).row][flipped.get(i).column] = this.currentPlayer;
		}

		this.board[row][column] = this.currentPlayer;
	}

	private int count(int player){
		int count = 0;
		for (int y = 0; y < this.height; y++) {
			for (int x = 0; x < this.width; x++) {
				if (this.board[y][x] == player) {
					count++;
				}
			}
		}
		return count;
	}

	private int getOpponentPlayer(){
		return this.getNextPlayer();
	}
}
