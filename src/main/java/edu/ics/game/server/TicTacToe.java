package edu.ics.game.server;

public class TicTacToe extends Game {
	public TicTacToe() {
		super(3, 3);
	}

	public void play(int... args) {
		if (args.length >= 2) {
			int column = args[0];
			int row = args[1];
			
			if (!isInBounds(column, row)) {
				return;
			}

			if (this.isEmpty(column, row)) {
				this.board[column][row] = this.currentPlayer;
				if (this.threeInARow(column, row)) {
					this.winner = this.currentPlayer;
					this.ended = true;
				} else if (this.isFull()) {
					this.ended = true;
				} else {
					this.next();
				}
			}
		}
	}

	private boolean threeInARow(int column, int row) {		
		int[][] arr = {{0, 1}, {1, 0}, {1, 1}, {-1, 1}};
		for (int[] directions : arr) {
			int _c = directions[0];
			int _r = directions[1];
			int count = 0;
			for (int r = row - 2 * _r, c = column - 2 * _c; (_r < 0 ? r >= row + 2 * _r : r <= row + 2 * _r) && (_c < 0 ? c >= column + 2 * _c : c <= column + 2 * _c ); r += _r, c += _c) {
				if (this.isInBounds(c, r) && this.board[c][r] == this.currentPlayer) {
					count++;
					if (count == 3) {
						return true;
					}
				}
			}
		}
		return false;
	}

	private boolean isFull() {
		for (int c = 0; c < this.columns; c++) {
			for (int r = 0; r < this.rows; r++) {
				if (this.board[c][r] == -1) {
					return false;
				}
			}
		}
		return true;
	}
}
