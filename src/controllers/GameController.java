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

}
