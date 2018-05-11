package controllers;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class GameController {
	
	@FXML
	private Canvas boardCanvas;
	
	@FXML
	private void initialize() {
		// draw a board
		GraphicsContext gc = boardCanvas.getGraphicsContext2D();
		drawBoard(gc);
		drawPlayer(gc, 3, 2, true);
		drawPlayer(gc, 5, 0, true);
		drawPlayer(gc, 0, 7, false);
		drawPlayer(gc, 2, 7, false);
	}
	
	private void drawBoard(GraphicsContext gc) {
		// get board & fields size
		int boardSize = (int)boardCanvas.getWidth();
		int fieldSize = boardSize/8;
		
		// clear the board
		gc.setFill(Color.WHITE);
		gc.fillRoundRect(0, 0, boardSize, boardSize, 0, 0);
		
		// draw the fields
		gc.setFill(Color.BLACK);
		
        for(int i=fieldSize; i<boardSize; i+=2*fieldSize) {
        		for(int j=0; j<boardSize-fieldSize; j+=2*fieldSize) {
        			gc.fillRoundRect(i, j, fieldSize, fieldSize, 0, 0);
        		}
        }
        for(int i=0; i<boardSize-fieldSize; i+=2*fieldSize) {
    			for(int j=fieldSize; j<boardSize; j+=2*fieldSize) {
    				gc.fillRoundRect(i, j, fieldSize, fieldSize, 0, 0);
    			}
        }
	}
	
	private void drawPlayer(GraphicsContext gc, int x, int y, Boolean isRed) {
		// get board & fields size #duplicate
		int boardSize = (int)boardCanvas.getWidth();
		int fieldSize = boardSize/8;
		
		// calculate positions
		double pawnSize = (int) (fieldSize/1.5);
		double margin = (fieldSize-pawnSize)/2;
		int xPos = (int) ((x)*fieldSize+margin);
		int yPos = (int) ((y)*fieldSize+margin);
		
		if(isRed)
			gc.setFill(Color.RED);
		else
			gc.setFill(Color.WHITE);
		
		gc.fillOval(xPos, yPos, pawnSize, pawnSize);
	}

}
