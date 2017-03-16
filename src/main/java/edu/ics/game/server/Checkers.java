package edu.ics.game.server;
import java.util.*;

public class Checkers extends Game {
	private int redcheckers; // Number of red checkers on the board
	private int blackcheckers; // Number of black checkers on the board
	private int red = 0;
	private int black = 1;
	private int redKing = 2;
	private int blackKing = 3;
	private ArrayList<Coordinates> mandatoryMoves = new ArrayList<Coordinates>();
	private Coordinates[] allMoves = new Coordinates[64];
	private Coordinates movefrom;
	private Coordinates moveto;

	protected Checkers() {
		super(8,8);
		this.redcheckers = 12;
		this.blackcheckers = 12;

		int column, row;
		for (column = 0; column < this.columns; column++) {
			for (row = 0; row < this.rows; row++) {
				this.board[column][row] = -1;
			}
		}

		for (row = this.rows - 3; row < this.rows; row++) {
			for (column = 0; column < this.columns; column += 1) {
				if ((row + column) % 2 == 1) {
					this.board[column][row] = 0;

				}
			} 
		}

		for (row = 0; row < 3; row++) {
			for (column = 0; column < this.columns; column += 1) {
				if ((row + column) % 2 == 1) {
					this.board[column][row] = 1;

				}
			} 
		}

		int counter = 0;
		for (row = 0; row < this.rows; row++) {
			for (column = 0; column < this.columns; column++) {
				allMoves[counter] = new Coordinates(column, row);
				counter++;
			}
		}
	}

	// This method executes one move.
	public void play(int... args){
		if (args.length >= 4){
			movefrom = new Coordinates(args[0], args[1]);
			moveto = new Coordinates(args[2], args[3]);
		}
		if (validMove(movefrom, moveto)) {
			if (mandatoryMoves.size() == 0){
				executeMove(movefrom, moveto);
				next();
			}
			if (mandatoryMoves.size() != 0) {
				executeMove(movefrom, moveto);
			}
		}
	}

	// Checks if a move is valid.
	public boolean validMove(Coordinates movefrom, Coordinates moveto) {
		if (!isInBounds(movefrom.column, movefrom.row)) {
			return false;
		}
		if (!isInBounds(moveto.column, moveto.row)) {
			return false;
		}
		if (checkJumpMoves()){
			if (mandatoryMoves.size() >0) {
				for(int i=0; i<mandatoryMoves.size(); i++){
					if(mandatoryMoves.get(i).row == moveto.row && mandatoryMoves.get(i).column == moveto.column){
						mandatoryMoves.clear();
						return true;
					}
				} return false;
			}
			if (isJump(movefrom, moveto)){
				if (currentPlayer == red){
					Coordinates newmove1 = new Coordinates(moveto.column+2, moveto.row+2);
					Coordinates newmove2 = new Coordinates(moveto.column-2, moveto.row+2);
					if (isInBounds(newmove1.column, newmove1.row) && isJump(moveto, newmove1)) {
						mandatoryMoves.add(newmove1);
					}
					if (isInBounds(newmove2.column, newmove2.row) && isJump(moveto, newmove2)) {
						mandatoryMoves.add(newmove2);
					}
				}
				if (currentPlayer == black){
					Coordinates newmove1 = new Coordinates(moveto.column-2, moveto.row-2);
					Coordinates newmove2 = new Coordinates(moveto.column+2, moveto.row-2);
					if (isInBounds(newmove1.column, newmove1.row) && isJump(moveto, newmove1)) {
						mandatoryMoves.add(newmove1);
					}
					if (isInBounds(newmove2.column, newmove2.row) && isJump(moveto, newmove2)) {
						mandatoryMoves.add(newmove2);
					}
				}
				return true;
			}
			return false; 
		}
		if (mandatoryMoves.size() == 0 && checkSimpleMoves()){
			if (isSimpleMove(movefrom, moveto)){
				return true;
			}
			return false;
		}
		return false;
	}

	public boolean isJump(Coordinates movefrom, Coordinates moveto) {
		if (board[movefrom.column][movefrom.row]==currentPlayer && board[moveto.column][moveto.row]==-1) {
			if (Math.abs(movefrom.column-moveto.column)==2) {
				if (currentPlayer == red && (moveto.row - movefrom.row == 2) && 
						(board[(movefrom.column+moveto.column)/2][(movefrom.row+moveto.row)/2] == black || board[(movefrom.column+moveto.column)/2][(movefrom.row+moveto.row)/2] == blackKing))
					return true;
				if (currentPlayer == black && (moveto.row - movefrom.row == -2) && 
						(board[(movefrom.column+moveto.column)/2][(movefrom.row+moveto.row)/2] == red || board[(movefrom.column+moveto.column)/2][(movefrom.row+moveto.row)/2] == redKing))
					return true;
				if (currentPlayer == red && board[movefrom.column][movefrom.row] == redKing && (moveto.row - movefrom.row == 2) && 
						(board[(movefrom.column+moveto.column)/2][(movefrom.row+moveto.row)/2] == black || board[(movefrom.column+moveto.column)/2][(movefrom.row+moveto.row)/2] == blackKing)) {
					return true;
				}
				if (currentPlayer == red && board[movefrom.column][movefrom.row] == redKing && (moveto.row - movefrom.row == -2) && 
						(board[(movefrom.column+moveto.column)/2][(movefrom.row+moveto.row)/2] == black || board[(movefrom.column+moveto.column)/2][(movefrom.row+moveto.row)/2] == blackKing)) {
					return true;
				}
				if (currentPlayer == black && board[movefrom.column][movefrom.row] == blackKing && (moveto.row - movefrom.row == 2) && 
						(board[(movefrom.column+moveto.column)/2][(movefrom.row+moveto.row)/2] == red || board[(movefrom.column+moveto.column)/2][(movefrom.row+moveto.row)/2] == redKing)) {
					return true;
				}
				if (currentPlayer == black && board[movefrom.column][movefrom.row] == blackKing && (moveto.row - movefrom.row == -2) && 
						(board[(movefrom.column+moveto.column)/2][(movefrom.row+moveto.row)/2] == red || board[(movefrom.column+moveto.column)/2][(movefrom.row+moveto.row)/2] == redKing)) {
					return true;
				}
			}
		} return false;
	}

	public boolean isSimpleMove(Coordinates movefrom, Coordinates moveto) {
		if (board[moveto.column][moveto.row] == -1) {
			if (Math.abs(movefrom.column-moveto.column)==1) {
				if (board[movefrom.column][movefrom.row] == currentPlayer && (currentPlayer == red) && (moveto.row - movefrom.row == 1)){
					return true;}
				if (board[movefrom.column][movefrom.row] == currentPlayer && (currentPlayer == black) && (moveto.row - movefrom.row == -1))
					return true;
				if (board[movefrom.column][movefrom.row] == redKing && (currentPlayer == red) && (moveto.row - movefrom.row == 1)){
					return true;}
				if (board[movefrom.column][movefrom.row] == redKing &&(currentPlayer == red) && (moveto.row - movefrom.row == -1))
					return true;
				if (board[movefrom.column][movefrom.row] == blackKing && (currentPlayer == black) && (moveto.row - movefrom.row == 1)){
					return true;}
				if (board[movefrom.column][movefrom.row] == blackKing &&(currentPlayer == black) && (moveto.row - movefrom.row == -1))
					return true;
			}
		} return false;
	} 

	public boolean checkJumpMoves() {
		//is there a jump move
		for (int i=0; allMoves.length>i; i++) {
			for (int j=0; allMoves.length>j; j++) {
				// Gets array indeces corresponding to the move, from parameters.
				if (board[allMoves[j].column][allMoves[j].row] == -1) {
					// Checks case of a jump
					if (Math.abs(allMoves[i].column-allMoves[j].column)==2) {
						if (board[allMoves[i].column][allMoves[i].row]==currentPlayer && currentPlayer == red && (allMoves[j].row - allMoves[i].row == 2) && 
								(board[(allMoves[i].column+allMoves[j].column)/2][(allMoves[i].row+allMoves[j].row)/2] == black  || board[(allMoves[i].column+allMoves[j].column)/2][(allMoves[i].row+allMoves[j].row)/2] == blackKing))
							return true;
						if (board[allMoves[i].column][allMoves[i].row]==currentPlayer && currentPlayer == black && (allMoves[j].row - allMoves[i].row == -2) && 
								(board[(allMoves[i].column+allMoves[j].column)/2][(allMoves[i].row+allMoves[j].row)/2] == red  || board[(allMoves[i].column+allMoves[j].column)/2][(allMoves[i].row+allMoves[j].row)/2] == redKing))
							return true;
						//redKing
						if (board[allMoves[i].column][allMoves[i].row]==redKing && currentPlayer == red && (allMoves[j].row - allMoves[i].row == 2) && 
								(board[(allMoves[i].column+allMoves[j].column)/2][(allMoves[i].row+allMoves[j].row)/2] == black  || board[(allMoves[i].column+allMoves[j].column)/2][(allMoves[i].row+allMoves[j].row)/2] == blackKing))
							return true;
						if (board[allMoves[i].column][allMoves[i].row]==redKing && currentPlayer == red && (allMoves[j].row - allMoves[i].row == -2) && 
								(board[(allMoves[i].column+allMoves[j].column)/2][(allMoves[i].row+allMoves[j].row)/2] == black || board[(allMoves[i].column+allMoves[j].column)/2][(allMoves[i].row+allMoves[j].row)/2] == blackKing))
							return true;
						//blackKing
						if (board[allMoves[i].column][allMoves[i].row]==blackKing && currentPlayer == black && (allMoves[j].row - allMoves[i].row == 2) && 
								(board[(allMoves[i].column+allMoves[j].column)/2][(allMoves[i].row+allMoves[j].row)/2] == red  || board[(allMoves[i].column+allMoves[j].column)/2][(allMoves[i].row+allMoves[j].row)/2] == redKing))
							return true;
						if (board[allMoves[i].column][allMoves[i].row]==blackKing && currentPlayer == black && (allMoves[j].row - allMoves[i].row == -2) && 
								(board[(allMoves[i].column+allMoves[j].column)/2][(allMoves[i].row+allMoves[j].row)/2] == red  || board[(allMoves[i].column+allMoves[j].column)/2][(allMoves[i].row+allMoves[j].row)/2] == redKing))
							return true;
					}
				}
			}
		} return false;
	}

	public boolean checkSimpleMoves() {
		for (int i=0; allMoves.length>i; i++) {
			for (int j=0; allMoves.length>j; j++) {
				// Gets array indeces corresponding to the move, from parameters.
				if (board[allMoves[j].column][allMoves[j].row] == -1) {
					// Checks case of simple move
					if (Math.abs(allMoves[i].column-allMoves[j].column)==1) {
						if (board[allMoves[i].column][allMoves[i].row]==currentPlayer && (currentPlayer == red) && (allMoves[j].row - allMoves[i].row == 1))
							return true;
						if (board[allMoves[i].column][allMoves[i].row]==currentPlayer && (currentPlayer == black) && (allMoves[j].row - allMoves[i].row == -1))
							return true;
						//redKing
						if (board[allMoves[i].column][allMoves[i].row]==redKing && (currentPlayer == red) && (allMoves[j].row - allMoves[i].row == 1))
							return true;
						if (board[allMoves[i].column][allMoves[i].row]==redKing && (currentPlayer == red) && (allMoves[j].row - allMoves[i].row == -1))
							return true;
						//blackKing
						if (board[allMoves[i].column][allMoves[i].row]==blackKing && (currentPlayer == black) && (allMoves[j].row - allMoves[i].row == 1))
							return true;
						if (board[allMoves[i].column][allMoves[i].row]==blackKing && (currentPlayer == black) && (allMoves[j].row - allMoves[i].row == -1))
							return true;
					}
				}
			}
		} return false;
	}


	// Executes a move.
	public void executeMove(Coordinates movefrom, Coordinates moveto) {
		// Gets array indeces corresponding to the move, from parameters.
		// Change appropriate board elements and decrement redcheckers or
		// blackcheckers if necessary. 	
		if (currentPlayer == red) {
			if (board[movefrom.column][movefrom.row] == redKing) {
				this.board[moveto.column][moveto.row] = redKing;
			}
			if (moveto.row == 7) {
				this.board[moveto.column][moveto.row] = redKing;
			} 
			if (moveto.row != 0 && board[movefrom.column][movefrom.row] != redKing){
				this.board[moveto.column][moveto.row] = red;
			}
		} else {
			if (board[movefrom.column][movefrom.row] == blackKing){
				this.board[moveto.column][moveto.row] = blackKing;
			}
			if (moveto.row == 0) {
				this.board[moveto.column][moveto.row] = blackKing;
			}
			if (moveto.row != 0  && board[movefrom.column][movefrom.row] != blackKing){
				this.board[moveto.column][moveto.row] = black;
			}		
		} // end while
		this.board[movefrom.column][movefrom.row] = -1;
		if (Math.abs(moveto.column - movefrom.column) == 2) {
			this.board[(movefrom.column+moveto.column)/2][(movefrom.row+moveto.row)/2] = -1;
			if (currentPlayer == red)
				redcheckers--;
			else
				blackcheckers--;
		} 
	}

	public boolean gameOver() {
		//no move left, player who still has moves wins
		if (!checkSimpleMoves() && !checkJumpMoves()){
			if (currentPlayer == red) {
				this.winner = 1;
				this.ended = true;
				return true;}
			else if (currentPlayer == black) {
				this.winner = 0;
				this.ended = true;
				return true;}
		}
		//player has no checkers left
		if (blackcheckers == 0)
			this.winner = 0;
		this.ended = true;
		if (redcheckers == 0)
			this.winner = 1;
		this.ended = true;
		return (redcheckers == 0 || blackcheckers == 0);
	}

}
