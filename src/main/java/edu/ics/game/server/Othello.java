package edu.ics.game.server;
import java.util.ArrayList;
import java.util.Arrays;

public class Othello extends Game {
	
	//tiles that need to be flipped
	ArrayList<Coordinates> flippableTiles = new ArrayList<Coordinates>();
	ArrayList<Coordinates> coords = new ArrayList<Coordinates>(Arrays.asList(new Coordinates(0,1), new Coordinates(1,1),
			new Coordinates(1,0), new Coordinates(1,-1), new Coordinates(0,-1),
			new Coordinates(-1,-1), new Coordinates(-1,0), new Coordinates(-1,1)));
	
	public Othello() {
		super(8, 8);
		
		//intialize board pieces
		this.board[3][3] = 0;
		this.board[3][4] = 1;
		this.board[4][3] = 1;
		this.board[4][4] = 0;
	}
		
	public void play(int... args) {
		if (args.length >= 2) {
			int x = args[0];	//column
			int y = args[1];	//row
			
			//check if player's move is valid and flip appropriate pieces if true
			if(isValid(y,x)){
				flip();
				this.board[y][x] = this.currentPlayer;
				
				//change player to other player to check if they have any valid moves
				this.next();
				if(!validMoves())	//if opponent doesn't have any valid moves return control back to current player
					this.next();
				
				//check for winner
				if(gameOver()){
					this.ended = true;
				}
			}			
		}
	}
	
	// Check if player move is valid
	private boolean isValid(int y, int x) {
		//if move wanted is not empty space or is out of bounds then return false
		if(this.board[y][x] != -1 || !this.isInBounds(y, x))
			return false;
		
		//place temp piece to test if move is valid
		this.board[y][x] = this.currentPlayer;
		
		for(int i = 0; i < this.coords.size(); i++){	//goes through each cardinal direction
			int row = y, col = x;	//temp variables for move checking
			int yDirection = this.coords.get(i).row;
			int xDirection = this.coords.get(i).column;
			
			row += yDirection;		//move position to first adjacent piece
			col += xDirection;
			
			if(this.isInBounds(row, col) && this.board[row][col] == ((this.currentPlayer + 1) % this.players)){	//if adjacent piece is in bounds and is other player

				while(this.board[row][col] == ((this.currentPlayer + 1) % this.players)){
					row += yDirection;		//move position to next adjacent piece
					col += xDirection;
					
					if(!this.isInBounds(row, col))		//if out of bounds, then not valid path and break to next cardinal direction
						break;
				}

				if(this.isInBounds(row, col) && this.board[row][col] == this.currentPlayer){		//if i am found then this is a valid move so we add tiles to flip
					while(true){
						row -= yDirection;
						col -= xDirection;
						
						if(row == y && col == x)
							break;
						
						this.flippableTiles.add(new Coordinates(row,col));
					}
				}
			}
		}
		
		this.board[y][x] = -1;		//remove temp piece used to test if move was valid
		
		if(this.flippableTiles.isEmpty())
			return false;
		return true;
    }
	
	private void flip() {
        for(int i = 0; i < this.flippableTiles.size(); i++)
        	this.board[this.flippableTiles.get(i).row][this.flippableTiles.get(i).column] = this.currentPlayer;
        this.flippableTiles.clear();
    }
	
	private boolean validMoves(){
		for(int y = 0; y < this.height; y++){
			for(int x = 0; x < this.width; x++){
				if(isValid(y,x)){
					this.flippableTiles.clear();
					return true;
				}
			}
		}
		return false;
	}
	
	private boolean gameOver(){
		//check if current player has any valid moves left
		if(validMoves()){
			return false;
		}
		
		//check if opponent has any valid moves left
		this.next();
		if(validMoves()){
			return false;
		}
		this.next();
		
		if(countPlayerOne() > countOpponent())
			this.winner = this.currentPlayer;
		else if(countPlayerOne() < countOpponent())
			this.winner = (this.currentPlayer + 1) % this.players;
		else{
			this.tie = 0;
			return true;
		}
			
		
		return true;
	}
	
	private int countPlayerOne(){
		int count = 0;
		for(int y = 0; y < this.height; y++){
			for(int x = 0; x < this.width; x++){
				if(this.board[y][x] == this.currentPlayer)
					count++;
			}
		}
		return count;
	}
	
	private int countOpponent(){
		int count = 0;
		for(int y = 0; y < this.height; y++){
			for(int x = 0; x < this.width; x++){
				if(this.board[y][x] == (this.currentPlayer + 1) % this.players)
					count++;
			}
		}
		return count;
	}
}
