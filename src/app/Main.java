package app;

import java.awt.Point;
import java.io.IOException;

import javafx.application.Application;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class Main extends Application {
	
	private static int xPos, yPos;
	private static Scene scene;

    public static void main(String[] args) throws IOException{
        launch(args);
    }
    
    public static Point getMousePosition() {
    		return new Point(xPos, yPos);
    }

	@Override
	public void start(Stage primaryStage) throws Exception {
		FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/fxml/GameWindow.fxml"));
		
		Pane pane = loader.load();
		scene = new Scene(pane, 800, 600);
		
		primaryStage.setResizable(false);
		scene.getStylesheets().add("/styles/dark.css");
		primaryStage.setScene(scene);
		primaryStage.setTitle("CheckersAI");
		primaryStage.show();
		
		// mouse click position
		scene.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
		    @Override
		    public void handle(MouseEvent mouseEvent) {
		    		xPos = (int) mouseEvent.getSceneX();
		        yPos = (int) mouseEvent.getSceneY();
		    }
		});
		
	}
    
}
