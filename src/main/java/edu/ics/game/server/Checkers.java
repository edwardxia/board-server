package edu.ics.game.server;
import java.util.*;

public class Checkers extends Game {
	private final static int SIZE  = 8;
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
		this.redcheckers =12;
		this.blackcheckers =12;
		
		int i, j;
	    for (i=0;i<SIZE;i++)
		    for (j=0;j<SIZE;j++)
			this.board[j][i] = -1;

		for (i=1;i<SIZE;i+=2) {
		    this.board[1][i] = 0;
		    this.board[5][i] = 1;
		    this.board[7][i] = 1;
		}
		for (i=0;i<SIZE;i+=2) {
		    this.board[0][i] = 0;
		    this.board[2][i] = 0;
		    this.board[6][i] = 1;
		}
		int counter = 0;
		for (i=0; i<8; i++) {
			for (j=0; j<8; j++) {
				allMoves[counter] = new Coordinates(j,i);
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
		if (board[movefrom.row][movefrom.column]==currentPlayer && board[moveto.row][moveto.column]==-1) {
		    if (Math.abs(movefrom.column-moveto.column)==2) {
				if (currentPlayer == red && (moveto.row - movefrom.row == 2) && 
				    (board[(movefrom.row+moveto.row)/2][(movefrom.column+moveto.column)/2] == black || board[(movefrom.row+moveto.row)/2][(movefrom.column+moveto.column)/2] == blackKing))
					return true;
				if (currentPlayer == black && (moveto.row - movefrom.row == -2) && 
				    (board[(movefrom.row+moveto.row)/2][(movefrom.column+moveto.column)/2] == red || board[(movefrom.row+moveto.row)/2][(movefrom.column+moveto.column)/2] == redKing))
					return true;
				if (currentPlayer == red && board[movefrom.row][movefrom.column] == redKing && (moveto.row - movefrom.row == 2) && 
						(board[(movefrom.row+moveto.row)/2][(movefrom.column+moveto.column)/2] == black || board[(movefrom.row+moveto.row)/2][(movefrom.column+moveto.column)/2] == blackKing)) {
					return true;
				}
				if (currentPlayer == red && board[movefrom.row][movefrom.column] == redKing && (moveto.row - movefrom.row == -2) && 
						(board[(movefrom.row+moveto.row)/2][(movefrom.column+moveto.column)/2] == black || board[(movefrom.row+moveto.row)/2][(movefrom.column+moveto.column)/2] == blackKing)) {
					return true;
				}
				if (currentPlayer == black && board[movefrom.row][movefrom.column] == blackKing && (moveto.row - movefrom.row == 2) && 
						(board[(movefrom.row+moveto.row)/2][(movefrom.column+moveto.column)/2] == red || board[(movefrom.row+moveto.row)/2][(movefrom.column+moveto.column)/2] == redKing)) {
					return true;
				}
				if (currentPlayer == black && board[movefrom.row][movefrom.column] == blackKing && (moveto.row - movefrom.row == -2) && 
						(board[(movefrom.row+moveto.row)/2][(movefrom.column+moveto.column)/2] == red || board[(movefrom.row+moveto.row)/2][(movefrom.column+moveto.column)/2] == redKing)) {
					return true;
				}
		    }
	} return false;
    }
			    
    public boolean isSimpleMove(Coordinates movefrom, Coordinates moveto) {
		if (board[moveto.row][moveto.column]==-1) {
		    if (Math.abs(movefrom.column-moveto.column)==1) {
				if (board[movefrom.row][movefrom.column]==currentPlayer && (currentPlayer == red) && (moveto.row - movefrom.row == 1)){
					return true;}
				if (board[movefrom.row][movefrom.column]==currentPlayer && (currentPlayer == black) && (moveto.row - movefrom.row == -1))
					return true;
				if (board[movefrom.row][movefrom.column]==redKing && (currentPlayer == red) && (moveto.row - movefrom.row == 1)){
					return true;}
				if (board[movefrom.row][movefrom.column]==redKing &&(currentPlayer == red) && (moveto.row - movefrom.row == -1))
					return true;
				if (board[movefrom.row][movefrom.column]==blackKing && (currentPlayer == black) && (moveto.row - movefrom.row == 1)){
					return true;}
				if (board[movefrom.row][movefrom.column]==blackKing &&(currentPlayer == black) && (moveto.row - movefrom.row == -1))
					return true;
		    }
		} return false;
	} 
    
    public boolean checkJumpMoves() {
    	//is there a jump move
    	for (int i=0; allMoves.length>i; i++) {
    		for (int j=0; allMoves.length>j; j++) {
    			// Gets array indeces corresponding to the move, from parameters.
				if (board[allMoves[j].row][allMoves[j].column]==-1) {
				    // Checks case of a jump
				    if (Math.abs(allMoves[i].column-allMoves[j].column)==2) {
						if (board[allMoves[i].row][allMoves[i].column]==currentPlayer && currentPlayer == red && (allMoves[j].row - allMoves[i].row == 2) && 
						    (board[(allMoves[i].row+allMoves[j].row)/2][(allMoves[i].column+allMoves[j].column)/2] == black  || board[(allMoves[i].row+allMoves[j].row)/2][(allMoves[i].column+allMoves[j].column)/2] == blackKing))
							return true;
						if (board[allMoves[i].row][allMoves[i].column]==currentPlayer && currentPlayer == black && (allMoves[j].row - allMoves[i].row == -2) && 
						    (board[(allMoves[i].row+allMoves[j].row)/2][(allMoves[i].column+allMoves[j].column)/2] == red  || board[(allMoves[i].row+allMoves[j].row)/2][(allMoves[i].column+allMoves[j].column)/2] == redKing))
							return true;
						//redKing
						if (board[allMoves[i].row][allMoves[i].column]==redKing && currentPlayer == red && (allMoves[j].row - allMoves[i].row == 2) && 
								(board[(allMoves[i].row+allMoves[j].row)/2][(allMoves[i].column+allMoves[j].column)/2] == black  || board[(allMoves[i].row+allMoves[j].row)/2][(allMoves[i].column+allMoves[j].column)/2] == blackKing))
    							return true;
    					if (board[allMoves[i].row][allMoves[i].column]==redKing && currentPlayer == red && (allMoves[j].row - allMoves[i].row == -2) && 
    						    (board[(allMoves[i].row+allMoves[j].row)/2][(allMoves[i].column+allMoves[j].column)/2] == black || board[(allMoves[i].row+allMoves[j].row)/2][(allMoves[i].column+allMoves[j].column)/2] == blackKing))
    							return true;
						//blackKing
    					if (board[allMoves[i].row][allMoves[i].column]==blackKing && currentPlayer == black && (allMoves[j].row - allMoves[i].row == 2) && 
								(board[(allMoves[i].row+allMoves[j].row)/2][(allMoves[i].column+allMoves[j].column)/2] == red  || board[(allMoves[i].row+allMoves[j].row)/2][(allMoves[i].column+allMoves[j].column)/2] == redKing))
    							return true;
    					if (board[allMoves[i].row][allMoves[i].column]==blackKing && currentPlayer == black && (allMoves[j].row - allMoves[i].row == -2) && 
    						    (board[(allMoves[i].row+allMoves[j].row)/2][(allMoves[i].column+allMoves[j].column)/2] == red  || board[(allMoves[i].row+allMoves[j].row)/2][(allMoves[i].column+allMoves[j].column)/2] == redKing))
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
				if (board[allMoves[j].row][allMoves[j].column]==-1) {
					// Checks case of simple move
				    if (Math.abs(allMoves[i].column-allMoves[j].column)==1) {
						if (board[allMoves[i].row][allMoves[i].column]==currentPlayer && (currentPlayer == red) && (allMoves[j].row - allMoves[i].row == 1))
							return true;
						if (board[allMoves[i].row][allMoves[i].column]==currentPlayer && (currentPlayer == black) && (allMoves[j].row - allMoves[i].row == -1))
							return true;
						//redKing
						if (board[allMoves[i].row][allMoves[i].column]==redKing && (currentPlayer == red) && (allMoves[j].row - allMoves[i].row == 1))
							return true;
						if (board[allMoves[i].row][allMoves[i].column]==redKing && (currentPlayer == red) && (allMoves[j].row - allMoves[i].row == -1))
							return true;
						//blackKing
						if (board[allMoves[i].row][allMoves[i].column]==blackKing && (currentPlayer == black) && (allMoves[j].row - allMoves[i].row == 1))
							return true;
						if (board[allMoves[i].row][allMoves[i].column]==blackKing && (currentPlayer == black) && (allMoves[j].row - allMoves[i].row == -1))
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
			if (board[movefrom.row][movefrom.column] == redKing) {
				this.board[moveto.row][moveto.column] = redKing;
			}
			if (moveto.row == 7) {
				this.board[moveto.row][moveto.column] = redKing;
			} 
			if (moveto.row != 0 && board[movefrom.row][movefrom.column] != redKing){
				this.board[moveto.row][moveto.column] = red;
			}
		}
		else {
			if (board[movefrom.row][movefrom.column] == blackKing){
				this.board[moveto.row][moveto.column] = blackKing;
			}
			if (moveto.row == 0) {
				this.board[moveto.row][moveto.column] = blackKing;
			}
			if (moveto.row != 0  && board[movefrom.row][movefrom.column] != blackKing){
				this.board[moveto.row][moveto.column] = black;
			}		
		} // end while
    	this.board[movefrom.row][movefrom.column] = -1;
		if (Math.abs(moveto.column - movefrom.column) == 2) {
		    this.board[(movefrom.row+moveto.row)/2][(movefrom.column+moveto.column)/2] = -1;
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
