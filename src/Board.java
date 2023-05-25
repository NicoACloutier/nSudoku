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
	
	private static double getLimit(Integer aN) { return 1000 * aN; }
	
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
	
	//check if a row is valid
    private static boolean checkRow(Integer[] row, Integer aN) {
        Integer[] numbers = new Integer[row.length];
		for (int i = 0; i < row.length; i++) { numbers[i] = 0; }
		
        int number;
        for (int i = 0; i < row.length; i++) {
            if (row[i] != null) {
                number = row[i];
                if (numbers[number-1] == 0) { numbers[number-1]++; }
                else { return false; }
            }
        }
        return true;
    }

	//check if a column is valid
    private static boolean checkColumn(Integer[][] aBoard, int column, Integer aN) {
        Integer[] numbers = new Integer[aBoard.length];
		for (int i = 0; i < aBoard.length; i++) { numbers[i] = 0; }
		
        int number;
        for (int i = 0; i < aBoard.length; i++) {
            if (aBoard[i][column] != null) {
                number = aBoard[i][column];
                if (numbers[number-1] == 0) { numbers[number-1]++; }
                else { return false; }
            }
        }
        return true;
    }

	//check if an n*n box is valid
    private static boolean checkBox(Integer[][] aBoard, int x, int y, Integer aN) {
        Integer[] numbers = new Integer[aBoard.length];
		for (int i = 0; i < aBoard.length; i++) { numbers[i] = 0; }
		
        int number;
        for (int i = 0; i < aN; i++) {
            for (int j = 0; j < aN; j++) {
                if (aBoard[x+i][y+j] != null) {
                    number = aBoard[x+i][y+j];
                    if (numbers[number-1] == 0) { numbers[number-1]++; }
                    else { return false; }
                }
            }
        }
        return true;
    }

	//check is sudoku board is valid
	private static boolean isValidSudoku(Integer[][] aBoard, Integer aN) {
        for (int i = 0; i < aBoard.length; i++) {
            if (!Board.checkRow(aBoard[i], aN)) { return false; }
            if (!Board.checkColumn(aBoard, i, aN)) { return false; }
        }
        for (int i = 0; i < aBoard.length; i += aN) {
            for (int j = 0; j < aBoard.length; j += aN) {
                if (!Board.checkBox(aBoard, i, j, aN)) { return false; }
            }
        }
        return true;
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
		for (int i = 1; i <= aBoard.length; i++) { numbers.add(i); }
		Collections.shuffle(numbers);
		
		for (int i = 0; i < numbers.size(); i++) {
			Integer temp = numbers.get(i);
			aBoard[row][col] = temp;
			if (Board.isValidPlacement(aBoard, col, row, aN) && fillIn(aBoard, row, col+1, aN, start, limit) == 1) { break; }
			else if (i == numbers.size()-1) { aBoard[row][col] = null; return 0; }
			else { aBoard[row][col] = null; }
		}
		
		if (System.currentTimeMillis() - start >= limit) { return -1; }
		return 1;
	}

	//solve sudoku using backtracking
    private static boolean solve(Integer[][] aBoard, int row, int col, Integer aN) {
        if (row == aBoard.length) { return true; }
        if (col == aBoard[0].length) { return solve(aBoard, row + 1, 0, aN); }
        if (aBoard[row][col] != null) { return solve(aBoard, row, col + 1, aN); }
        
        for (int num = 1; num <= aBoard.length; num++) {
            if (isValidSudoku(aBoard, aN)) {
                aBoard[row][col] = num;
                if (solve(aBoard, row, col + 1, aN)) { return true; }
                aBoard[row][col] = null;
            }
        }
        return false;
    }
	
	//return how many times a board can be solved (stops after 2)
	private static int solveCount(Integer[][] maskedBoard, int row, int col, int count, int n, boolean[][] mask) {
		if (row == maskedBoard.length) { Board.maskBoardInPlace(maskedBoard, mask); return 1; }
		else if (col == maskedBoard.length) { count += Board.solveCount(maskedBoard, row+1, 0, count, n, mask); return count; }
		else if (maskedBoard[row][col] != null) { count += Board.solveCount(maskedBoard, row, col+1, count, n, mask); return count; }
		
		for (int i = 1; i <= maskedBoard.length && count < 2; i++) {
			maskedBoard[row][col] = i;
			if (Board.isValidPlacement(maskedBoard, col, row, n)) {
				count += Board.solveCount(maskedBoard, row+1, col, count, n, mask);
			}
			else { maskedBoard[row][col] = null; }
		}
		
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
		makeBoard();
		makeMask();
		
		/*
		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board[i].length; j++) {
				System.out.print(board[i][j]);
				System.out.print(" ");
			}
			System.out.println();
		}
		
		System.out.println();
		for (int i = 0; i < mask.length; i++) {
			for (int j = 0; j < mask[i].length; j++) {
				System.out.print(mask[i][j]);
				System.out.print(" ");
			}
			System.out.println();
		}
		
		System.out.println();
		for (int i = 0; i < getMaskedBoard().length; i++) {
			for (int j = 0; j < getMaskedBoard().length; j++) {
				System.out.print(getMaskedBoard()[i][j]);
				System.out.print(" ");
			}
			System.out.println();
		}
		*/
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
	private void makeMask() {
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
			int count = Board.solveCount(getMaskedBoard(), 0, 0, 0, n, mask);
			if (count > 1) { mask[x][y] = false; }
		}
	}
	
	public Integer getN() { return n; }
	public Integer[][] getBoard() { return board; }
	public boolean[][] getMask() { return mask; }
	public Integer[][] getMaskedBoard() { return Board.maskBoard(board, mask); }
}
