import java.lang.System;

public class Tester {
	public static void main(String[] args) {
		for (int n = 1; n <= 5; n++) {
			for (int i = 0; i < 10; i++) {
				long start = System.currentTimeMillis();
				Board board = new Board(n);
				long elapsed = (System.currentTimeMillis() - start);
				System.out.print("n: ");
				System.out.print(n);
				System.out.print(". i: ");
				System.out.print(i);
				System.out.print(". Elapsed: ");
				System.out.print(elapsed);
				System.out.println(".");
			}
		}
	}
}