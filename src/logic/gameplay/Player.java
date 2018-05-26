package logic.gameplay;

import java.util.List;
import java.util.Random;

public class Player {

	private Side side;
	private String name;

	public Player() {
	}

	public enum Side {
		BLACK, WHITE
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

	public Board.Decision makeMove(Move move, Board board) {
		return board.makeMove(move, side);
	}

	public Board.Decision makeRandomMove(Board board) {
		List<Move> moves = board.getAllValidMoves(side);
		Random rand = new Random();
		return board.makeMove(moves.get(rand.nextInt(moves.size())), side);
	}
	
}
