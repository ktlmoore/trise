package com.tlear.trise.utils;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.tlear.trise.objects.StaticObstacle;

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

	public static BoundingBox getBoundingBoxForEnvObject(StaticObstacle o) {
		return new BoundingBox(new Vector3(o.pos, 0), new Vector3(o.pos.cpy()
				.add(o.width, o.height), 0));
	}
}
