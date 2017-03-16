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
			int column = args[0];
			int row = args[1];

			if (isValidMove(column, row)) {
				this.flip(column, row);
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

	private List<Coordinates> getFlipped(int column, int row) {
		List<Coordinates> flipped = new ArrayList<>();

		for (int _r = -1; _r <= 1; _r++) {
			for (int _c = -1; _c <= 1; _c++) {
				if (_r == 0 && _c == 0) {
					continue;
				}

				for (int r = row + _r, c = column + _c; this.isInBounds(c, r) && !this.isEmpty(c, r); r += _r, c += _c) {
					if (this.board[c][r] == this.currentPlayer) {
						for (r -= _r, c -= _c; r != row || c != column; r -= _r, c -= _c) {
							flipped.add(new Coordinates(c, r));
						}
						break;
					}

				}
			}
		}
		return flipped;
	}

	private boolean isValidMove(int column, int row) {
		if (!this.isInBounds(column, row) || !this.isEmpty(column, row)) {
			return false;
		}

		if (this.getFlipped(column, row).isEmpty()) {
			return false;
		}

		return true;
	}

	private boolean hasValidMoves() {
		for (int column = 0; column < this.columns; column++) {
			for (int row = 0; row < this.rows; row++) {
				if (isValidMove(column, row)) {
					return true;
				}
			}
		}
		return false;
	}

	private void flip(int column, int row) {
		List<Coordinates> flipped = this.getFlipped(column, row);

		for(int i = 0; i < flipped.size(); i++) {
			this.board[flipped.get(i).column][flipped.get(i).row] = this.currentPlayer;
		}

		this.board[column][row] = this.currentPlayer;
	}

	private int count(int player){
		int count = 0;
		for (int column = 0; column < this.columns; column++) {
			for (int row = 0; row < this.rows; row++) {
				if (this.board[column][row] == player) {
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
