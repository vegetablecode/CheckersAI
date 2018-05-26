package logic.ai.impl;

import java.util.List;
import java.util.Random;

import logic.ai.AI;
import logic.gameplay.Board;
import logic.gameplay.Move;
import logic.gameplay.Player;
import logic.gameplay.Board.Decision;
import logic.gameplay.Player.Side;

public class RandomMove extends Player implements AI {
	public RandomMove(String name, Side s) {
		super(name, s);
	}

	public RandomMove(Side s) {
		super("RandomAI", s);
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
