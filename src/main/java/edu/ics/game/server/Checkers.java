package edu.ics.game.server;

public class Checkers extends Game {
	private static final int MAN = 0;
	private static final int KING = 1;
	
	public Checkers() {
		super(8, 8);
	}
	
	public void play(int... args) {
		
	}

	private int getPiece(int player, int type) {
		return 2 * player + type;
	}
	
	private int getPlayerOfPiece(int piece) {
		return piece / 2;
	}
	
	private int getTypeOfPiece(int piece) {
		return piece % 2;
	}
}