package logic.ai;

import logic.gameplay.Board;

public interface AI {
	public Board.Decision makeMove(Board board);
}