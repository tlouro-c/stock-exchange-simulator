package tc.tlouro_c.utils;

import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class InputReader {

	private static InputReader instance;
	private Scanner scanner;

	private InputReader() {
		scanner = new Scanner(System.in);
	}

	public static InputReader getInstance() {
		if (instance == null) {
			instance = new InputReader();
		}
		return instance;
	}

	public Integer optionsBasedInput(List<Integer> listOfOptions) {
		int input = 0;
		boolean firstAttempt = true;

		while (!listOfOptions.contains(input)) {
			if (!firstAttempt) {
				System.out.println("Invalid Option");
			}

			try {
				input = scanner.nextInt();
			} catch (InputMismatchException e) {
				scanner.nextLine();
				firstAttempt = false;
				continue;
			}

			firstAttempt = false;
		}
		return input;
	}

	public String optionsBasedInputString(List<String> listOfOptions) {

		String input = "";
		int i = 0;

		while (!listOfOptions.contains(input.toLowerCase())) {
			if (i != 0) {
				System.out.println("Invalid Option");
			}
			input = scanner.next();
			i++;
		}
		return input;
	}

	public int getIntBetween(int min, int max, String errorMessage) {

		Integer input = null;
		int i = 0;

		while (input == null || input < min || input > max) {
			if (i != 0) {
				System.out.println(errorMessage);
			}
			try {
				input = scanner.nextInt();
			} catch (InputMismatchException e) {
				System.out.println("Invalid number format");
				scanner.next();
				input = null;
			}
			i++;
		}
		return input;
	}

	public double getDoubleBetween(double min, double max, String errorMessage) {

		Double input = null;
		int i = 0;

		while (input == null || input < min || input > max) {
			if (i != 0) {
				System.out.println(errorMessage);
			}
			try {
				input = scanner.nextDouble();
			} catch (InputMismatchException e) {
				System.out.println("Invalid number format");
				scanner.next();
				input = null;
			}
			i++;
		}
		return input;
	}
	

	public String getString() {
		return scanner.next();
	}

	public void pressEnterToContinue() {
		scanner.nextLine();
	}
}
