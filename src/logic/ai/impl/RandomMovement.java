package logic.ai.impl;

import java.util.List;
import java.util.Random;

import logic.ai.AI;
import logic.gameplay.Board;
import logic.gameplay.Move;
import logic.gameplay.Player;

public class RandomMovement extends Player implements AI {
	public RandomMovement(String name, Side side) {
		super(name, side);
	}

	public RandomMovement(Side side) {
		super("RandomAI", side);
	}

	public Board.Decision makeMove(Board board) {
		Random rand = new Random();
		List<Move> moves = board.getAllValidMoves(getSide());
		if (moves.size() == 0)
			return Board.Decision.GAME_ENDED;
		Move m = moves.get(rand.nextInt(moves.size()));
		return board.makeMove(m, getSide());
	}

}
