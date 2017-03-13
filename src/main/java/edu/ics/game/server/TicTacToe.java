package edu.ics.game.server;

public class TicTacToe extends Game {
	public TicTacToe() {
		super(3, 3);
	}

	public void play(int... args) {
		if (args.length >= 2) {
			int x = args[0];
			int y = args[1];
			if (this.isEmpty(x, y)) {
				this.board[y][x] = this.currentPlayer;
				if (this.threeInARow(x, y)) {
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

	private boolean threeInARow(int x, int y) {		
		int[][] arr = {{0, 1}, {1, 0}, {1, 1}, {-1, 1}};
		for (int[] directions : arr) {
			int _x = directions[0];
			int _y = directions[1];
			int count = 0;
			for (int i = x - 2 * _x, j = y - 2 * _y; (_x < 0 ? i >= x + 2 * _x : i <= x + 2 * _x ) && (_y < 0 ? j >= y + 2 * _y : j <= y + 2 * _y); i += _x, j += _y) {
				if (this.isInBounds(i, j) && this.board[j][i] == this.currentPlayer) {
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
		for (int y = 0; y < this.height; y++) {
			for (int x = 0; x < this.width; x++) {
				if (this.board[y][x] == -1) {
					return false;
				}
			}
		}
		return true;
	}
}
