package logic;

import java.util.List;
import java.util.Random;

public class Player {
	
	private String name;
	private Side side;

	public enum Side{
		RED, WHITE;
	}
	
	public Player(String name, Side side) {
		this.name = name;
		this.side = side;
	}
	
	public Player(Side side) {
		this.name = side.toString();
		this.side = side;
	}
	
	public Side getSide() {
		return side;
	}
	
	public Board.Decision makeMove(Move move, Board board){
		return board.makeMove(move, board);
	}
	
	public Board.Decision makeRandomMove(Board board){
		List<Move> moves = board.getAllValidMoves(side);
        Random rand = new Random();
        return board.makeMove(moves.get(rand.nextInt(moves.size())), side);
	}
	
	public String toString() {
		return name + "/" + side;
	}
	
}
