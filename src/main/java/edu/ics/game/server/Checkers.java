package edu.ics.game.server;
import java.util.*;

public class Checkers extends Game {
	private List<List<CheckersPiece>> board; 
	private Coordinates selectedCoordinates = null;
	private boolean mandatoryMove = false;

	protected Checkers() {
		this.columns = 8;
		this.rows = 8;

		this.board = new ArrayList<>(this.columns);
		for (int column = 0; column < this.columns; column++) {
			this.board.add(column, new ArrayList<>(this.rows));
			for (int row = 0; row < this.rows; row++) {
				this.board.get(column).add(row, null);
			}
		}

		for (int column = 0; column < this.columns; column++) {
			for (int row = 0; row < rows; row++) {
				if ((row + column) % 2 == 1) {
					if (row < 3){
						this.setPiece(column, row, new CheckersPiece(1));
					} else if (row > 4) {
						this.setPiece(column, row, new CheckersPiece(0));
					}
				}
			} 
		}
	}

	public int[][] getBoard() {
		int[][] board = new int[8][8];
		for (int column = 0; column < this.columns; column += 1) {
			for (int row = 0; row < this.rows; row++) {
				CheckersPiece piece = this.getPiece(column, row);
				if (piece == null) {
					board[column][row] = -1;
				} else {
					board[column][row] = piece.getInt();
					if (this.selectedCoordinates != null && this.selectedCoordinates.column == column && this.selectedCoordinates.row == row) {
						board[column][row] += 4;
					}
				}
			} 
		} 		
		return board;
	}

	protected boolean isEmpty(int column, int row) {
		return this.isInBounds(column, row) && this.getPiece(column, row) == null;
	}

	protected void setPiece(int column, int row, CheckersPiece piece) {
		this.board.get(column).set(row, piece);
	}

	protected CheckersPiece getPiece(int column, int row) {
		return this.board.get(column).get(row);
	}

	protected CheckersPiece getPiece(Coordinates coordinates) {
		return this.getPiece(coordinates.column, coordinates.row);
	}

	public void play(int... args){
		if (args.length >= 2) {
			int column = args[0];
			int row = args[1];

			if (!isInBounds(column, row)) {
				return;
			}

			if (this.selectedCoordinates != null) {
				if (this.mandatoryMove || !this.select(column, row)) {
					if (this.move(this.selectedCoordinates.column, this.selectedCoordinates.row, column, row)) {
						if (this.count(this.getOpponentPlayer()) == 0) {
							this.ended = true;
							this.winner = this.getCurrentPlayer();
						} else {
							this.next();
						}
					}
				}
			} else {
				this.select(column, row);
			}
		}
	}

	private boolean select(int column, int row) {
		CheckersPiece piece = this.getPiece(column, row);
		if (piece != null && piece.getPlayerIndex() == currentPlayer) {
			this.selectedCoordinates = new Coordinates(column, row);
			return true;
		}
		return false;
	}

	private boolean move(int oldColumn, int oldRow, int newColumn, int newRow) {
		int _c = newColumn - oldColumn, _r = newRow - oldRow;
		if (Math.abs(_r) > 2 || Math.abs(_r) <=0 || Math.abs(_c) != Math.abs(_r) || !isEmpty(newColumn, newRow) || (mandatoryMove && Math.abs(_r) == 1)) {
			return false;
		}
		CheckersPiece piece = this.getPiece(oldColumn, oldRow);
		if (piece.getType() == CheckersPiece.MEN && ((piece.getPlayerIndex() == 0 && _r > 0) || (piece.getPlayerIndex() == 1 && _r < 0))) {
			return false;
		}
		if (Math.abs(_r) == 2) {
			CheckersPiece middlePiece = this.getPiece(oldColumn + _c / 2, oldRow + _r / 2);
			if (middlePiece == null || middlePiece.getPlayerIndex() == currentPlayer) {
				return false;
			}
			this.selectedCoordinates = null;
			this.setPiece(oldColumn, oldRow, null);
			this.setPiece(oldColumn + _c / 2, oldRow + _r / 2, null);
			this.setPiece(newColumn, newRow, piece);

			if (this.hasMandatoryMoves(newColumn, newRow)) {
				this.mandatoryMove = true;
				this.selectedCoordinates = new Coordinates(newColumn, newRow);
				return false;
			}

			this.mandatoryMove = false;
		} else if (Math.abs(_r) == 1) {
			this.selectedCoordinates = null;
			this.setPiece(oldColumn, oldRow, null);
			this.setPiece(newColumn, newRow, piece);
		}

		if ((piece.getPlayerIndex() == 0 && newRow == 0) || (piece.getPlayerIndex() == 1 && newRow == this.rows - 1)) {
			piece.promote();
		}
		return true;
	}

	private boolean hasMandatoryMoves(int column, int row) {
		int[] rowDirections = {}, columnDirections = {-1, 1};
		if (this.getPiece(column, row).getType() == CheckersPiece.KING) {
			rowDirections = new int[]{-1, 1};
		} else if (this.getPiece(column, row).getPlayerIndex() == 0) {
			rowDirections = new int[]{-1};
		} else if (this.getPiece(column, row).getPlayerIndex() == 1) {
			rowDirections = new int[]{1};
		}
		for (int _c : columnDirections) {
			for (int _r : rowDirections) {
				if (this.isInBounds(column + _c * 2, row + _r * 2) && this.getPiece(column + _c * 2, row + _r * 2) == null) {
					CheckersPiece piece = this.getPiece(column + _c, row + _r);
					if (piece != null && piece.getPlayerIndex() == this.getOpponentPlayer()) {
						return true;
					}
				}
			}	
		}
		return false;
	}

	private int count(int player){
		int count = 0;
		for (int column = 0; column < this.columns; column++) {
			for (int row = 0; row < this.rows; row++) {
				CheckersPiece piece = this.getPiece(column, row);
				if (piece != null && this.getPiece(column, row).getPlayerIndex() == player) {
					count++;
				}
			}
		}
		return count;
	}

	private int getOpponentPlayer(){
		return this.getNextPlayer();
	}

	public class CheckersPiece {
		public static final int MEN = 0;
		public static final int KING = 1;

		private int playerIndex;
		private int type;

		public CheckersPiece(int playerIndex) {
			this.playerIndex = playerIndex;
			this.type = MEN;
		}

		public void promote() {
			this.type = KING;
		}

		public int getPlayerIndex() {
			return playerIndex;
		}

		public int getType() {
			return type;
		}

		public int getInt() {
			return 2 * playerIndex + type;
		}
	}
}
