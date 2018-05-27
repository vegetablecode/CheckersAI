package controllers;

import java.awt.Point;

import app.Main;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import logic.ai.AI;
import logic.ai.impl.MinimaxMovement;
import logic.gameplay.Board;
import logic.gameplay.Move;
import logic.gameplay.Player;
import logic.gameplay.Board.*;

public class GameController {

	// game variables
	public static double total = 1;
	public static boolean multipleRoundsTest = false;
	int blackWin;
	int whiteWin;
	boolean turn;
	Player one;
	MinimaxMovement two;
	Board board;
	Player current;
	boolean isGameFinished;
	int numbOfRounds;
	int aiDepth;

	// util variables
	GraphicsContext gc;
	@FXML
	TextField depthField;
	@FXML
	TextField logField;

	// user interaction variables
	Point startPosition;
	Point endPosition;
	int xStart, yStart;
	int xEnd, yEnd;
	Status status;

	enum Status {
		PLAYERS_TURN, SELECTED, ADDITIONAL_MOVE
	}

	enum SetColor {
		WHITE, BLACK, SELECTED, EMPTY, WHITE_KING, BLACK_KING
	}

	@FXML
	Canvas boardCanvas;

	@FXML
	private void initialize() {
		// draw a board
		gc = boardCanvas.getGraphicsContext2D();
		drawBoard(gc);
		displayMessage("Click Restart to start a game!");
	}

	@FXML
	public void finishGame() {
		isGameFinished = true;
		attemptMove();
	}
	
	@FXML
	public void restartGame() {
		// get the depth
		aiDepth = 6; // default value
		int value = 0;
		if (depthField.getText().isEmpty()) {
			displayMessage("Cannot remove the node. There is no value entered.");
		} else {
			try {
				value = Integer.parseInt(depthField.getText());
				aiDepth = value;
			} catch (NumberFormatException e) {
				displayMessage("The value should be a type of integer!");
			}
		}
		
		// set the players
		one = new Player("Player 1", Player.Side.BLACK);
		two = new MinimaxMovement(Player.Side.WHITE, aiDepth);

		// set the game
		turn = true;
		blackWin = 0;
		whiteWin = 0;

		// set the board and current player
		board = new Board();
		current = one;
		if (!turn)
			current = two;

		isGameFinished = false;
		numbOfRounds = 0;

		// draw the board
		updateBoard(board);
		status = Status.PLAYERS_TURN;
		displayMessage("Depth of MiniMax: " + aiDepth);

	}

	@FXML
	public void mouseDetected() {
		// mouse position
		int xPos = Main.getMousePosition().x - 45;
		int yPos = Main.getMousePosition().y - 45;

		double fieldSize = 500 / 8;

		// get selected position
		if (xPos >= 0 && yPos >= 0 && xPos <= 500 && yPos <= 500) {
			if (status == Status.PLAYERS_TURN) {
				xStart = (int) (xPos / fieldSize);
				yStart = (int) (yPos / fieldSize);
				if (board.isPlayerOnThisField(yStart, xStart)) {
					drawPlayer(gc, xStart, yStart, SetColor.SELECTED);
					status = Status.SELECTED;
				}
			} else if (status == Status.ADDITIONAL_MOVE) {
				xStart = xEnd;
				yStart = yEnd;
				xEnd = (int) (xPos / fieldSize);
				yEnd = (int) (yPos / fieldSize);
				attemptMove();
			} else if (status == Status.SELECTED) {
				xEnd = (int) (xPos / fieldSize);
				yEnd = (int) (yPos / fieldSize);
				if ((xStart == xEnd) && (yStart == yEnd)) {
					updateBoard(board);
					status = Status.PLAYERS_TURN;
				} else {
					do {
						if(isGameFinished == false)
							attemptMove();
						else break;
					} while (current == two);
					status = Status.PLAYERS_TURN;
				}
			}
		}
	}

	public void attemptMove() {
		displayMessage("");
		if (!isGameFinished) {
			// check if the move is valid
			Move move;
			Board.Decision decision = null;
			if (current instanceof AI) {
				decision = ((AI) current).makeMove(board);
				updateBoard(board);
				System.out.println(two.getAverageTimePerMove());
			} else {
				move = new Move(yStart, xStart, yEnd, xEnd);
				decision = current.makeMove(move, board);
			}

			if (decision == Board.Decision.FAILED_INVALID_DESTINATION
					|| decision == Board.Decision.FAILED_MOVING_INVALID_PIECE) {
				displayMessage("Move Failed");
				drawPlayer(gc, xStart, yStart, SetColor.EMPTY);
				drawPlayer(gc, xStart, yStart, SetColor.BLACK);
				status = Status.PLAYERS_TURN;
				// don't update anything
			} else if (decision == Board.Decision.COMPLETED) {
				numbOfRounds++;
				updateBoard(board);
				if (board.getNumbOfBlackPieces() == 0) {
					displayMessage("White wins with " + board.getNumbOfWhitePieces() + " pieces left");
					whiteWin++;
					isGameFinished = true;
				}
				if (board.getNumbOfWhitePieces() == 0) {
					displayMessage("Black wins with " + board.getNumbOfBlackPieces() + " pieces left");
					blackWin++;
					isGameFinished = true;
				}
				if (turn)
					current = two;
				else
					current = one;
				turn = !turn;
			} else if (decision == Board.Decision.ADDITIONAL_MOVE) {
				displayMessage("Additional Move");
				status = Status.ADDITIONAL_MOVE;
				drawPlayer(gc, xEnd, yEnd, SetColor.SELECTED);
			} else if (decision == Board.Decision.GAME_ENDED) {
				// current player cannot move
				if (current.getSide() == Player.Side.BLACK) {
					displayMessage("White wins");
					whiteWin++;

				} else {
					displayMessage("Black wins");
					blackWin++;
				}
				isGameFinished = true;
			}
		} else {
			displayMessage("Game finished after: " + numbOfRounds/2 + " rounds");
			if (one instanceof MinimaxMovement)
				System.out.println("Avg time per move: " + ((MinimaxMovement) one).getAverageTimePerMove());
		}
	}

	private void drawBoard(GraphicsContext gc) {
		// get board & fields size
		int boardSize = (int) boardCanvas.getWidth();
		int fieldSize = boardSize / 8;

		// clear the board
		gc.setFill(Color.WHITE);
		gc.fillRoundRect(0, 0, boardSize, boardSize, 0, 0);

		// draw the fields
		gc.setFill(Color.BLACK);

		for (int i = fieldSize; i < boardSize; i += 2 * fieldSize) {
			for (int j = 0; j < boardSize - fieldSize; j += 2 * fieldSize) {
				gc.fillRoundRect(i, j, fieldSize, fieldSize, 0, 0);
			}
		}
		for (int i = 0; i < boardSize - fieldSize; i += 2 * fieldSize) {
			for (int j = fieldSize; j < boardSize; j += 2 * fieldSize) {
				gc.fillRoundRect(i, j, fieldSize, fieldSize, 0, 0);
			}
		}
	}

	private void drawPlayer(GraphicsContext gc, int x, int y, SetColor setColor) {
		// get board & fields size #duplicate
		int boardSize = (int) boardCanvas.getWidth();
		int fieldSize = boardSize / 8;

		// calculate positions
		double pieceSize = (int) (fieldSize / 1.5);
		double margin = (fieldSize - pieceSize) / 2;
		int xPos = (int) ((x) * fieldSize + margin);
		int yPos = (int) ((y) * fieldSize + margin);

		switch (setColor) {
		case BLACK:
			gc.setFill(Color.RED);
			gc.setStroke(Color.RED);
			break;
		case WHITE:
			gc.setFill(Color.WHITE);
			gc.setStroke(Color.WHITE);
			break;
		case SELECTED:
			gc.setFill(Color.GRAY);
			gc.setStroke(Color.GRAY);
			break;
		case EMPTY:
			gc.setFill(Color.BLACK);
			gc.setStroke(Color.BLACK);
			xPos -= 3;
			yPos -= 3;
			pieceSize += 8;
			break;
		case WHITE_KING:
			gc.setFill(Color.WHITE);
			gc.setStroke(Color.GREEN);
			break;
		case BLACK_KING:
			gc.setFill(Color.BLACK);
			gc.setStroke(Color.GREEN);
			break;
		default:
			gc.setFill(Color.BLACK);
			gc.setStroke(Color.BLACK);
			xPos -= 3;
			yPos -= 3;
			pieceSize += 8;
			break;
		}

		gc.setLineWidth(5);
		gc.fillOval(xPos, yPos, pieceSize, pieceSize);
		gc.strokeOval(xPos, yPos, pieceSize, pieceSize);
	}

	private void updateBoard(Board board) {
		GraphicsContext gc = boardCanvas.getGraphicsContext2D();
		drawBoard(gc);
		for (int i = 0; i < board.getBoardLength(); i++) {
			for (int j = -1; j < board.getBoardHeight(i); j++) {
				if (j == -1) {}

				else if (board.getPiece(i, j) == Type.WHITE)
					drawPlayer(gc, j, i, SetColor.WHITE);
				else if (board.getPiece(i, j) == Type.BLACK)
					drawPlayer(gc, j, i, SetColor.BLACK);
				else if (board.getPiece(i, j) == Type.WHITE_KING)
					drawPlayer(gc, j, i, SetColor.WHITE_KING);
				else if (board.getPiece(i, j) == Type.BLACK_KING)
					drawPlayer(gc, j, i, SetColor.BLACK_KING);
			}
		}
	}
	
	private void displayMessage(String message) {
		logField.setText(message);
	}

}
