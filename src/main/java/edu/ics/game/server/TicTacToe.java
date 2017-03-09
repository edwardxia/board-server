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
				this.board[x][y] = this.currentPlayer;
				if (this.threeInARow(x, y)) {
					this.winner = this.currentPlayer;
					this.status = GameStatus.ENDED;
				} else if (this.isFull()) {
					this.status = GameStatus.ENDED;
				} else {
					this.next();
				}
			}
		}
	}

	private boolean threeInARow(int x, int y) {		
		int[] arr = {0, 1};
		for (int _x : arr) {
			for (int _y: arr) {
				if (_x == 0 && _y == 0) {
					continue;
				}

				int count = 0;
				for (int i = x - 2 * _x, j = y - 2 * _y; i <= x + 2 * _x && j <= y + 2 * _y; i += _x, j += _y) {
					if (this.isInBounds(i, j) && this.board[i][j] == this.currentPlayer) {
						count++;
						if (count == 3) {
							return true;
						}
					}
				} 
			}
		}
		return false;
	}

	private boolean isFull() {
		for (int x = 0; x < this.width; x++) {
			for (int y = 0; y < this.height; y++) {
				if (this.board[x][y] == -1) {
					return false;
				}
			}
		}
		return true;
	}
}
