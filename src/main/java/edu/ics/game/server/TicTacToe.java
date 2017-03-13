package edu.ics.game.server;

public class TicTacToe extends Game {
	public TicTacToe() {
		super(3, 3);
	}

	public void play(int... args) {
		if (args.length >= 2) {
			int row = args[0];
			int column = args[1];

			if (this.isEmpty(row, column)) {
				this.board[row][column] = this.currentPlayer;
				if (this.threeInARow(row, column)) {
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

	private boolean threeInARow(int row, int column) {		
		int[][] arr = {{0, 1}, {1, 0}, {1, 1}, {-1, 1}};
		for (int[] directions : arr) {
			int _c = directions[0];
			int _r = directions[1];
			int count = 0;
			for (int r = row - 2 * _r, c = column - 2 * _c; (_r < 0 ? r >= row + 2 * _r : r <= row + 2 * _r) && (_c < 0 ? c >= column + 2 * _c : c <= column + 2 * _c ); r += _r, c += _c) {
				if (this.isInBounds(r, c) && this.board[r][c] == this.currentPlayer) {
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
		for (int r = 0; r < this.height; r++) {
			for (int c = 0; c < this.width; c++) {
				if (this.board[r][c] == -1) {
					return false;
				}
			}
		}
		return true;
	}
}
