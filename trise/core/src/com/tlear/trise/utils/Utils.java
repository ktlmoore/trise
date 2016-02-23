package com.tlear.trise.utils;

public class Utils {
	/**
	 * Determines whether or not input is numeric.
	 * 
	 * @param input
	 * @return
	 */
	public static boolean isNumeric(String input) {
		try {
			Integer.parseInt(input);
			return true;
		} catch (NumberFormatException e) {
			// input is not Integer
			try {
				Double.parseDouble(input);
				return true;
			} catch (NumberFormatException e2) {
				return false;
			}
		}
	}
}
