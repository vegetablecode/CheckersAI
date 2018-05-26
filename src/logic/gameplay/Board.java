package logic.gameplay;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class Board {

	private Type[][] board;
	public final int SIZE = 8;

	private int numbOfWhitePieces;
	private int numbOfBlackPieces;
	private int numbOfWhiteKingPieces;
	private int numbOfBlackKingPieces;

	public enum Type {
		EMPTY, WHITE, BLACK, WHITE_KING, BLACK_KING
	}

	public enum Decision {
		COMPLETED, FAILED_MOVING_INVALID_PIECE, FAILED_INVALID_DESTINATION, ADDITIONAL_MOVE, GAME_ENDED
	}

	public Board() {
		setUpBoard();
	}

	public Board(Type[][] board) {
		numbOfWhitePieces = 0;
		numbOfBlackPieces = 0;
		numbOfBlackKingPieces = 0;
		numbOfWhiteKingPieces = 0;

		this.board = board;
		
		// place players on the fields
		for (int i = 0; i < SIZE; i++) {
			for (int j = 0; j < SIZE; j++) {
				Type piece = getPiece(i, j);
				if (piece == Type.BLACK)
					numbOfBlackPieces++;
				else if (piece == Type.BLACK_KING)
					numbOfBlackKingPieces++;
				else if (piece == Type.WHITE)
					numbOfWhitePieces++;
				else if (piece == Type.WHITE_KING)
					numbOfWhiteKingPieces++;
			}
		}
	}

	private void setUpBoard() {
		numbOfWhitePieces = 12;
		numbOfBlackPieces = 12;
		numbOfBlackKingPieces = 0;
		numbOfWhiteKingPieces = 0;
		
		board = new Type[SIZE][SIZE];
		
		for (int i = 0; i < board.length; i++) {
			int start = 0;
			if (i % 2 == 0)
				start = 1;

			Type pieceType = Type.EMPTY;
			if (i <= 2)
				pieceType = Type.WHITE;
			else if (i >= 5)
				pieceType = Type.BLACK;

			for (int j = start; j < board[i].length; j += 2) {
				board[i][j] = pieceType;
			}
		}
		
		setEmptyFields();
	}

	// set the type of fields without players to EMPTY
	private void setEmptyFields() {
		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board[i].length; j++) {
				if (board[i][j] == null)
					board[i][j] = Type.EMPTY;
			}
		}
	}

	// return true if player (user) is on the field
	public boolean isPlayerOnThisField(int x, int y) {
		if ((board[x][y] == Type.BLACK) || (board[x][y] == Type.BLACK_KING))
			return true;
		return false;
	}

	public int getBoardLength() {
		return board.length;
	}

	public int getBoardHeight(int x) {
		return board[x].length;
	}

	public Type getPiece(int x, int y) {
		return board[x][y];
	}

	public Type getPiece(Point point) {
		return board[point.x][point.y];
	}

	public Type[][] getBoard() {
		return board;
	}

	public int getNumbOfWhitePieces() {
		return numbOfWhiteKingPieces + numbOfWhitePieces;
	}

	public int getNumbOfBlackPieces() {
		return numbOfBlackKingPieces + numbOfBlackPieces;
	}

	public int getNumbOfWhiteKingPieces() {
		return numbOfWhiteKingPieces;
	}

	public int getNumbOfBlackKingPieces() {
		return numbOfBlackKingPieces;
	}

	public int getNumbOfWhiteNormalPieces() {
		return numbOfWhitePieces;
	}

	public int getNumbOfBlackNormalPieces() {
		return numbOfBlackPieces;
	}

	// return true if move is valid
	public Decision makeMove(Move move, Player.Side side) {
		if (move == null) {
			return Decision.GAME_ENDED;
		}
		Point start = move.getStart();
		int startX = start.x;
		int startY = start.y;
		Point end = move.getEnd();
		int endX = end.x;
		int endY = end.y;

		// check if player doesn't want to move an empty space
		if (!isMovingOwnPiece(startX, startY, side) || getPiece(startX, startY) == Type.EMPTY) {
			return Decision.FAILED_MOVING_INVALID_PIECE;
		}

		List<Move> possibleMoves = getValidMoves(startX, startY, side);
		Type currentType = getPiece(startX, startY);

		// check if the move is on a "possible moves" list
		if (possibleMoves.contains(move)) {
			boolean jumpMove = false;
			
			// if the move ends on the next field, then it's not a jump
			if (startX + 1 == endX || startX - 1 == endX) {
				board[startX][startY] = Type.EMPTY;
				board[endX][endY] = currentType;
			} else { // if not, then this is a jump
				jumpMove = true;
				
				board[startX][startY] = Type.EMPTY;
				board[endX][endY] = currentType;
				Point mid = findMidField(move);
				Type middle = getPiece(mid);
				
				// remove the jumped piece
				if (middle == Type.BLACK)
					numbOfBlackPieces--;
				else if (middle == Type.BLACK_KING)
					numbOfBlackKingPieces--;
				else if (middle == Type.WHITE)
					numbOfWhitePieces--;
				else if (middle == Type.WHITE_KING)
					numbOfWhiteKingPieces--;
				board[mid.x][mid.y] = Type.EMPTY;
			}
			
			// change black piece to king case
			if (endX == 0 && side == Player.Side.BLACK) {
				board[endX][endY] = Type.BLACK_KING;
				numbOfBlackPieces--;
				numbOfBlackKingPieces++;
			}
			
			// change white piece to king case
			else if (endX == SIZE - 1 && side == Player.Side.WHITE) {
				board[endX][endY] = Type.WHITE_KING;
				numbOfWhitePieces--;
				numbOfWhiteKingPieces++;
			}
			
			if (jumpMove) {
				List<Move> additional = getValidSkipMoves(endX, endY, side);
				if (additional.isEmpty())
					return Decision.COMPLETED;
				return Decision.ADDITIONAL_MOVE;
			}
			return Decision.COMPLETED;
		} else
			return Decision.FAILED_INVALID_DESTINATION;
	}

	// return a list of all valid moves
	public List<Move> getAllValidMoves(Player.Side side) {

		Type normal = side == Player.Side.BLACK ? Type.BLACK : Type.WHITE;
		Type king = side == Player.Side.BLACK ? Type.BLACK_KING : Type.WHITE_KING;

		List<Move> possibleMoves = new ArrayList<>();
		for (int i = 0; i < SIZE; i++) {
			for (int j = 0; j < SIZE; j++) {
				Type t = getPiece(i, j);
				if (t == normal || t == king)
					possibleMoves.addAll(getValidMoves(i, j, side));
			}
		}

		return possibleMoves;
	}

	public List<Move> getValidMoves(int posX, int posY, Player.Side side) {
		Type type = board[posX][posY];
		Point startPoint = new Point(posX, posY);
		if (type == Type.EMPTY)
			throw new IllegalArgumentException();

		List<Move> moves = new ArrayList<>();

		// if piece is not a king (2 possible moves)
		if (type == Type.WHITE || type == Type.BLACK) {
			// not a king (2 possible moves)
			int rowChange = type == Type.WHITE ? 1 : -1;

			int newRow = posX + rowChange;
			if (newRow >= 0 || newRow < SIZE) {
				int newCol = posY + 1;
				if (newCol < SIZE && getPiece(newRow, newCol) == Type.EMPTY)
					moves.add(new Move(startPoint, new Point(newRow, newCol)));
				newCol = posY - 1;
				if (newCol >= 0 && getPiece(newRow, newCol) == Type.EMPTY)
					moves.add(new Move(startPoint, new Point(newRow, newCol)));
			}

		} else { // if piece is a king (4 possible moves)
			int newX = posX + 1;
			if (newX < SIZE) {
				int newY = posY + 1;
				if (newY < SIZE && getPiece(newX, newY) == Type.EMPTY)
					moves.add(new Move(startPoint, new Point(newX, newY)));
				newY = posY - 1;
				if (newY >= 0 && getPiece(newX, newY) == Type.EMPTY)
					moves.add(new Move(startPoint, new Point(newX, newY)));
			}
			newX = posX - 1;
			if (newX >= 0) {
				int newCol = posY + 1;
				if (newCol < SIZE && getPiece(newX, newCol) == Type.EMPTY)
					moves.add(new Move(startPoint, new Point(newX, newCol)));
				newCol = posY - 1;
				if (newCol >= 0 && getPiece(newX, newCol) == Type.EMPTY)
					moves.add(new Move(startPoint, new Point(newX, newCol)));
			}

		}

		moves.addAll(getValidSkipMoves(posX, posY, side));
		return moves;
	}

	// get valid skip moves
	public List<Move> getValidSkipMoves(int posX, int posY, Player.Side side) {
		List<Move> move = new ArrayList<>();
		Point start = new Point(posX, posY);

		List<Point> possibilities = new ArrayList<>();

		if (side == Player.Side.WHITE && getPiece(posX, posY) == Type.WHITE) {
			possibilities.add(new Point(posX + 2, posY + 2));
			possibilities.add(new Point(posX + 2, posY - 2));
		} else if (side == Player.Side.BLACK && getPiece(posX, posY) == Type.BLACK) {
			possibilities.add(new Point(posX - 2, posY + 2));
			possibilities.add(new Point(posX - 2, posY - 2));
		} else if (getPiece(posX, posY) == Type.BLACK_KING || getPiece(posX, posY) == Type.WHITE_KING) {
			possibilities.add(new Point(posX + 2, posY + 2));
			possibilities.add(new Point(posX + 2, posY - 2));
			possibilities.add(new Point(posX - 2, posY + 2));
			possibilities.add(new Point(posX - 2, posY - 2));
		}

		for (int i = 0; i < possibilities.size(); i++) {
			Point temp = possibilities.get(i);
			Move tempMove = new Move(start, temp);
			if (temp.x < SIZE && temp.x >= 0 && temp.y < SIZE && temp.y >= 0 && getPiece(temp.x, temp.y) == Type.EMPTY
					&& isOpponentPiece(side, getPiece(findMidField(tempMove)))) {
				move.add(tempMove);
			}
		}
		return move;
	}

	// find the mid field (if piece jumps)
	private Point findMidField(Move move) {
		Point field = new Point((move.getStart().x + move.getEnd().x) / 2, (move.getStart().y + move.getEnd().y) / 2);
		return field;
	}

	// return true if the field contains the player's piece
	private boolean isMovingOwnPiece(int row, int col, Player.Side side) {
		Type pieceType = getPiece(row, col);
		if (side == Player.Side.BLACK && pieceType != Type.BLACK && pieceType != Type.BLACK_KING)
			return false;
		else if (side == Player.Side.WHITE && pieceType != Type.WHITE && pieceType != Type.WHITE_KING)
			return false;
		return true;
	}
	
	// return true if the piece is opponents
	private boolean isOpponentPiece(Player.Side current, Type opponentPiece) {
		if (current == Player.Side.BLACK && (opponentPiece == Type.WHITE || opponentPiece == Type.WHITE_KING))
			return true;
		if (current == Player.Side.WHITE && (opponentPiece == Type.BLACK || opponentPiece == Type.BLACK_KING))
			return true;
		return false;
	}

	// clone board
	public Board clone() {
		Type[][] newBoard = new Type[SIZE][SIZE];
		for (int i = 0; i < SIZE; i++) {
			for (int j = 0; j < SIZE; j++) {
				newBoard[i][j] = board[i][j];
			}
		}
		Board b = new Board(newBoard);
		return b;
	}
}