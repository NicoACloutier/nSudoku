import javafx.scene.layout.GridPane;
import java.lang.Math;
import java.util.Collections;
import javafx.scene.control.Label;
import java.util.ArrayList;
import javafx.scene.control.ChoiceBox;
import java.lang.System;

public class Board {
	private Integer n;
	private Integer[][] board;
	private boolean[][] mask;
	private ChoiceBox<Integer>[][] boxes;
	
	private static double getLimit(Integer aN) {
		if (aN <= 4) { return 100 * aN; } //linear for smaller numbers
		else { return 100 * Math.pow(1.75, aN); } //for larger, exponential limit
	}
	
	//convert a board to masked board given a mask. 
	//`maskedBoard[i][j]` will be set to `null` for all `i` and `j` such that `aMask[i][j]`
	private static Integer[][] maskBoard(Integer[][] aBoard, boolean[][] aMask) {
		Integer[][] maskedBoard = new Integer[aBoard.length][aBoard.length];
		for (int i = 0; i < aBoard.length; i++) {
			for (int j = 0; j < aBoard[i].length; j++) {
				if (aMask[i][j]) { maskedBoard[i][j] = null; }
				else { maskedBoard[i][j] = aBoard[i][j]; }
			}
		}
		return maskedBoard;
	}
	
	//mask a board in place
	private static void maskBoardInPlace(Integer[][] aBoard, boolean[][] aMask) {
		for (int i = 0; i < aBoard.length; i++) {
			for (int j = 0; j < aBoard[i].length; j++) {
				if (aMask[i][j]) { aBoard[i][j] = null; }
				else { aBoard[i][j] = aBoard[i][j]; }
			}
		}
	}
	
	//get all of the possible numbers for a given cell
	private static ArrayList<Integer> makePossible(Integer[][] aBoard, int x, int y, Integer aN) {
		ArrayList<Integer> out = new ArrayList<Integer>();
		for (int i = 1; i <= aBoard.length; i++) { out.add(i); }
		
		//check line
		for (int i = 0; i < aBoard.length; i++) {
			Integer item = aBoard[x][i];
			if (item != null && out.contains(Integer.valueOf(item))) { out.remove(Integer.valueOf(item)); }
		}
		//check column
		for (int i = 0; i < aBoard.length; i++) {
			Integer item = aBoard[i][y];
			if (item != null && out.contains(Integer.valueOf(item))) { out.remove(Integer.valueOf(item)); }
		}
		//check box
		int xb = ((int) x / aN) * aN; //x beginning
		int yb = ((int) y / aN) * aN; //y beginning
		for (int i = 0; i < aN; i++) {
			for (int j = 0; j < aN; j++) {
				Integer item = aBoard[xb+i][yb+j];
				if (item != null && out.contains(Integer.valueOf(item))) { out.remove(Integer.valueOf(item)); }
			}
		}
		
		return out;
	}
	
	//tell whether placement is valid
	private static boolean isValidPlacement(Integer[][] aBoard, int x, int y, Integer aN) {
		Integer value = aBoard[y][x];
		if (value == null) { return false; }
		for (int i = 0; i < aBoard.length; i++) {
			if (aBoard[i][x] != null && i != y) { 
				if (value.equals(aBoard[i][x])) { return false; }
			}
			if (aBoard[y][i] != null && i != x) {
				if (value.equals(aBoard[y][i])) { return false; }
			}
		}
		int xb = ((int) x / aN) * aN; //column beginning
		int yb = ((int) y / aN) * aN; //row beginning 
		for (int i = 0; i < aN; i++) {
			for (int j = 0; j < aN; j++) {
				if ((j+yb == y && i+xb == x) || aBoard[j+yb][i+xb] == null) { continue; }
				if (value.equals(aBoard[j+yb][i+xb])) { return false; }
			}
		}
		return true;
	}
	
	//fill in sudoku using backtracking
	private static int fillIn(Integer[][] aBoard, int row, int col, Integer aN, long start, long limit) {
		if (System.currentTimeMillis() - start >= limit) { return -1; }
		if (row == aBoard.length-1 && col == aBoard.length) { return 1; }
		else if (col == aBoard.length) { return fillIn(aBoard, row+1, 0, aN, start, limit); }
		
		ArrayList<Integer> numbers = Board.makePossible(aBoard, row, col, aN);
		if (numbers.size() == 0) { return 0; }
		Collections.shuffle(numbers);
		
		for (int i = 0; i < numbers.size(); i++) {
			Integer temp = numbers.get(i);
			aBoard[row][col] = temp;
			if (fillIn(aBoard, row, col+1, aN, start, limit) == 1) { break; }
			else if (i == numbers.size()-1) { aBoard[row][col] = null; return 0; }
			else { aBoard[row][col] = null; }
		}
		
		if (System.currentTimeMillis() - start >= limit) { return -1; }
		return 1;
	}
	
	//return how many times a board can be solved (stops after 2)
	private static int solveCount(Integer[][] maskedBoard, int row, int col, int count, int n, boolean[][] mask, long limit, long start, Integer aN) {
		if (System.currentTimeMillis() - start >= limit) { return -1; }
		if (row == maskedBoard.length) { Board.maskBoardInPlace(maskedBoard, mask); return 1; }
		else if (col == maskedBoard.length) { count += Board.solveCount(maskedBoard, row+1, 0, count, n, mask, limit, start, aN); return count; }
		else if (maskedBoard[row][col] != null) { count += Board.solveCount(maskedBoard, row, col+1, count, n, mask, limit, start, aN); return count; }
		
		ArrayList<Integer> numbers = Board.makePossible(maskedBoard, row, col, aN);
		if (numbers.size() == 0) { return 0; }
		Collections.shuffle(numbers);
		
		for (int i = 0; i < numbers.size() && count < 2; i++) {
			Integer temp = numbers.get(i);
			maskedBoard[row][col] = temp;
			count += Board.solveCount(maskedBoard, row+1, col, count, n, mask, limit, start, aN);
		}
		
		if (System.currentTimeMillis() - start >= limit) { return -1; }
		return count;
	}
	
	public Board(Integer enteredN, GridPane pane) {
		n = enteredN;
		makeBoard();
		makeMask();
		
		int nSquared = n*n;
		boxes = new ChoiceBox[nSquared][nSquared];
		
		for (int i = 0; i < nSquared; i++) {
			for (int j = 0; j < nSquared; j++) {
				if (mask[i][j]) {
					ChoiceBox<Integer> box = newBox();
					boxes[i][j] = box;
					pane.add(box, i+1, j+1);
				}
				else { pane.add(new Label(Integer.toString(board[i][j])), i+1, j+1); }
			}
		}
	}
	
	public Board(Integer enteredN) {
		n = enteredN;
		int response = -1;
		while (response == -1) {
			makeBoard();
			response = makeMask();
		}
	}
	
	private ChoiceBox<Integer> newBox() {
		ChoiceBox<Integer> nBox = new ChoiceBox<Integer>();
		for (int i = 1; i <= board.length; i++) { nBox.getItems().add(i); }
		return nBox;
	}
	
	//make a board at random
	private void makeBoard() {
		long limit = (long) Board.getLimit(n);
		int response = -1;
		int count = 1;
		while (response == -1 || response == 0) {
			board = new Integer[n*n][n*n];
			for (int i = 0; i < board.length; i++) {
				for (int j = 0; j < board.length; j++) { board[i][j] = null; }
			}
			long time = System.currentTimeMillis();
			response = fillIn(board, 0, 0, n, time, limit);
			count++;
		}
		System.out.print("Count: ");
		System.out.print(count-1);
		System.out.print(". ");
	}
	
	//make a board mask at random
	private int makeMask() {
		long limit = (long) (5 * Board.getLimit(n));
		long time = System.currentTimeMillis();
		
		mask = new boolean[n*n][n*n];
		for (int i = 0; i < mask.length; i++) {
			for (int j = 0; j < mask.length; j++) { mask[i][j] = false; }
		}
		
		ArrayList<Integer> places = new ArrayList<Integer>();
		for (int i = 0; i < mask.length * mask.length; i++) { places.add(i); }
		Collections.shuffle(places);
		
		for (Integer place : places) {
			int x = place % (mask.length);
			int y = place / (mask.length);
			mask[x][y] = true;
			int count = Board.solveCount(getMaskedBoard(), 0, 0, 0, n, mask, limit, time, n);
			if (count == -1) { return -1; }
			if (count > 1) { mask[x][y] = false; }
		}
		
		return 1;
	}
	
	//check if a cell is correct
	public boolean checkBox(int row, int col) {
		if (boxes[row][col] != null && boxes[row][col].getValue() != null) { return (board[row][col].equals(boxes[row][col].getValue())); }
		else { return true; }
	}
	
	//check if a board is correct
	public boolean checkBoard() {
		for (int i = 0; i < boxes.length; i++) {
			for (int j = 0; j < boxes.length; j++) {
				if (!checkBox(i, j)) { return false; }
			}
		}
		return true;
	}
	
	public Integer getN() { return n; }
	public Integer[][] getBoard() { return board; }
	public boolean[][] getMask() { return mask; }
	public Integer[][] getMaskedBoard() { return Board.maskBoard(board, mask); }
}
