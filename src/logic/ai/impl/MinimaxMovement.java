package logic.ai.impl;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import logic.ai.AI;
import logic.gameplay.Board;
import logic.gameplay.Move;
import logic.gameplay.Player;

public class MinimaxMovement extends Player implements AI {

	private int depth;
	private long totalTime;
	private double numbOfCalledMoves;
	private Point skippingPoint;

	public MinimaxMovement(String name, Side s) {
		super(name, s);
	}

	public MinimaxMovement(Side side, int depth) {
		super("MinimaxAlgorithm", side);
		this.depth = depth;
		this.totalTime = 0;
	}

	public Board.Decision makeMove(Board board) {
		numbOfCalledMoves++;
		long startTime = System.nanoTime();
		Move m = minimaxStart(board, depth, getSide(), true);
		totalTime += System.nanoTime() - startTime;
		Board.Decision decision = board.makeMove(m, getSide());
		if (decision == Board.Decision.ADDITIONAL_MOVE)
			skippingPoint = m.getEnd();
		return decision;
	}

	private Move minimaxStart(Board board, int depth, Side side, boolean maximizingPlayer) {
		double alpha = Double.NEGATIVE_INFINITY;
		double beta = Double.POSITIVE_INFINITY;

		List<Move> possibleMoves;
		if (skippingPoint == null)
			possibleMoves = board.getAllValidMoves(side);
		else {
			possibleMoves = board.getValidSkipMoves(skippingPoint.x, skippingPoint.y, side);
			skippingPoint = null;
		}

		List<Double> heuristics = new ArrayList<>();
		if (possibleMoves.isEmpty())
			return null;
		Board tempBoard = null;
		for (int i = 0; i < possibleMoves.size(); i++) {
			tempBoard = board.clone();
			tempBoard.makeMove(possibleMoves.get(i), side);
			heuristics.add(minimax(tempBoard, depth - 1, flipSide(side), !maximizingPlayer, alpha, beta));
		}

		double maxHeuristics = Double.NEGATIVE_INFINITY;

		Random rand = new Random();
		for (int i = heuristics.size() - 1; i >= 0; i--) {
			if (heuristics.get(i) >= maxHeuristics) {
				maxHeuristics = heuristics.get(i);
			}
		}
		
		for (int i = 0; i < heuristics.size(); i++) {
			if (heuristics.get(i) < maxHeuristics) {
				heuristics.remove(i);
				possibleMoves.remove(i);
				i--;
			}
		}
		
		return possibleMoves.get(rand.nextInt(possibleMoves.size()));
	}

	private double minimax(Board board, int depth, Side side, boolean maximizingPlayer, double alpha, double beta) {
		if (depth == 0) {
			return getHeuristic(board);
		}
		List<Move> possibleMoves = board.getAllValidMoves(side);

		double initial = 0;
		Board tempBoard = null;
		
		// maximizing
		if (maximizingPlayer) {
			initial = Double.NEGATIVE_INFINITY;
			for (int i = 0; i < possibleMoves.size(); i++) {
				tempBoard = board.clone();
				tempBoard.makeMove(possibleMoves.get(i), side);

				double result = minimax(tempBoard, depth - 1, flipSide(side), !maximizingPlayer, alpha, beta);

				initial = Math.max(result, initial);
				alpha = Math.max(alpha, initial);

				if (alpha >= beta)
					break;
			}
		}
		
		// minimizing
		else {
			initial = Double.POSITIVE_INFINITY;
			for (int i = 0; i < possibleMoves.size(); i++) {
				tempBoard = board.clone();
				tempBoard.makeMove(possibleMoves.get(i), side);

				double result = minimax(tempBoard, depth - 1, flipSide(side), !maximizingPlayer, alpha, beta);

				initial = Math.min(result, initial);
				alpha = Math.min(alpha, initial);

				if (alpha >= beta)
					break;
			}
		}

		return initial;
	}

	// calculate move value
	private double getHeuristic(Board b) {

		double kingWeight = 1.2;
		double result = 0;
		if (getSide() == Side.WHITE)
			result = b.getNumbOfWhiteKingPieces() * kingWeight + b.getNumbOfWhiteNormalPieces()
					- b.getNumbOfBlackKingPieces() * kingWeight - b.getNumbOfBlackNormalPieces();
		else
			result = b.getNumbOfBlackKingPieces() * kingWeight + b.getNumbOfBlackNormalPieces()
					- b.getNumbOfWhiteKingPieces() * kingWeight - b.getNumbOfWhiteNormalPieces();
		return result;

	}

	private Side flipSide(Side side) {
		if (side == Side.BLACK)
			return Side.WHITE;
		return Side.BLACK;
	}
	
	public String getAverageTimePerMove() {
		return totalTime / numbOfCalledMoves * Math.pow(10, -6) + " milliseconds";
	}
	
}
