package logic.gameplay;

import java.awt.*;

public class Move {

	private Point start;
	private Point end;

	public Move(int startRow, int startY, int endX, int endY) {
		start = new Point(startRow, startY);
		end = new Point(endX, endY);
	}

	public Move(Point start, Point end) {
		this.start = start;
		this.end = end;
	}

	public Point getStart() {
		return start;
	}

	public Point getEnd() {
		return end;
	}
	
	// - utils -
	@Override
	public boolean equals(Object move) {
		if (!(move instanceof Move))
			return false;
		Move x = (Move) move;
		if (this.getStart().equals(x.getStart()) && this.getEnd().equals(x.getEnd()))
			return true;
		return false;
	}

}
