package tc.tlouro_c.utils;

public class Logger {

	public static void INFO(String message) {
		var blue = "\u001B[34m";
		var reset = "\u001B[0m";
		var timestamp = java.time.LocalTime.now();
		System.out.println(blue + timestamp + ": " + message + reset);
	}

	public static void ERROR(String message) {
		var red = "\u001B[31m";
		var reset = "\u001B[0m";
		var timestamp = java.time.LocalTime.now();
		System.out.println(red + timestamp + ": " + message + reset);
	}

	public static void SUCCESS(String message) {
		var green = "\u001B[32m";
		var reset = "\u001B[0m";
		var timestamp = java.time.LocalTime.now();
		System.out.println(green + timestamp + ": " + message + reset);
	}

	public static void WARNING(String message) {
		var yellow = "\u001B[33m";
		var reset = "\u001B[0m";
		var timestamp = java.time.LocalTime.now();
		System.out.println(yellow + timestamp + ": " + message + reset);
	}
	
}
