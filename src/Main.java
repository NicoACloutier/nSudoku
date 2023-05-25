import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.GridPane;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import java.lang.System;

public class Main extends Application implements EventHandler<ActionEvent> {
	private long start;
	private BorderPane border;
	private Stage window;
	private ChoiceBox<Integer> nBox;
	private Button newBoard;
	private GridPane board;
	private Button check;
	private Button submit;
	private VBox options;
	private Board sudokuBoard;
	
	private static final int HEIGHT = 500;
	private static final int WIDTH = 700;
	private static final String NEW_BOARD_TEXT = "New board";
	private static final String TITLE = "nSudoku";
	private static final String CHECK_TEXT = "Check entries";
	private static final String SUBMIT_TEXT = "Submit entries";
	private static final int MAX_N = 5;
	private static final double TAB_PROPORTION = 0.15;
	
	public static int getHeight() { return Main.HEIGHT; }
	public static int getBoardWidth() { return (int) (Main.WIDTH * (1 - Main.TAB_PROPORTION)); }
	public static int getWidth() { return Main.WIDTH; }
	
	public static void main(String[] args) {
		launch(args);
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		window = primaryStage;
		window.setTitle(Main.TITLE);
		
		border = new BorderPane();
		
		options = new VBox();
		newBoard = new Button(Main.NEW_BOARD_TEXT);
		newBoard.setOnAction(this);
		check = new Button(Main.CHECK_TEXT);
		check.setOnAction(this);
		submit = new Button(Main.SUBMIT_TEXT);
		submit.setOnAction(this);
		nBox = new ChoiceBox<Integer>();
		Label label = new Label("");
		for (int i = 1; i <= Main.MAX_N; i++) { nBox.getItems().add(i); }
		options.getChildren().addAll(nBox, newBoard, check, submit, label);
		
		newBoard.setMaxWidth((int) (Main.TAB_PROPORTION * Main.WIDTH));
		newBoard.setMinWidth((int) (Main.TAB_PROPORTION * Main.WIDTH));
		nBox.setMaxWidth((int) (Main.TAB_PROPORTION * Main.WIDTH));
		nBox.setMinWidth((int) (Main.TAB_PROPORTION * Main.WIDTH));
		check.setMaxWidth((int) (Main.TAB_PROPORTION * Main.WIDTH));
		check.setMinWidth((int) (Main.TAB_PROPORTION * Main.WIDTH));
		submit.setMaxWidth((int) (Main.TAB_PROPORTION * Main.WIDTH));
		submit.setMinWidth((int) (Main.TAB_PROPORTION * Main.WIDTH));
		
		board = new GridPane();
		
		border.setLeft(options);
		border.setCenter(board);
		Scene scene = new Scene(border, Main.WIDTH, Main.HEIGHT);
		window.setScene(scene);
		window.show();
	}
	
	@Override
	public void handle(ActionEvent e) {
		if (e.getSource() == newBoard) {
			board = new GridPane();
			sudokuBoard = new Board(nBox.getValue(), board);
			border.setCenter(board);
			Label label = new Label("");
			options.getChildren().set(4, label);
			start = System.currentTimeMillis();
		}
		else if (e.getSource() == check) {
			boolean correct = sudokuBoard.checkBoard();
			Label label;
			if (correct) { label = new Label("All correct."); }
			else { label = new Label("At least one incorrect."); }
			options.getChildren().set(4, label);
		}
		else if (e.getSource() == submit) {
			if (!sudokuBoard.checkBoard() || sudokuBoard.hasNull()) {
				options.getChildren().set(4, new Label("Try again :("));
			}
			else {
				long time = (System.currentTimeMillis() - start) / 1000;
				Integer mins = (int) time / 60;
				Integer secs = (int) time & 60;
				String minuteString = String.valueOf(mins);
				String secondString = String.valueOf(secs);
				Label label = new Label("You did it! " + minuteString + ":" + secondString + " :)");
				label.setMaxWidth((int) (Main.TAB_PROPORTION * Main.WIDTH));
				label.setMinWidth((int) (Main.TAB_PROPORTION * Main.WIDTH));
				options.getChildren().set(4, label);
			}
		}
	}
}
