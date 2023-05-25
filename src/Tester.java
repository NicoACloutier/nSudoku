import java.lang.System;

public class Tester {
	public static void main(String[] args) {
		for (int i = 0; i < 50; i++) {
			long start = System.currentTimeMillis();
			Board board = new Board(5);
			long elapsed = (System.currentTimeMillis() - start);
			System.out.print("i: ");
			System.out.print(i);
			System.out.print(". Elapsed: ");
			System.out.print(elapsed);
			System.out.println(".");
		}
	}
}