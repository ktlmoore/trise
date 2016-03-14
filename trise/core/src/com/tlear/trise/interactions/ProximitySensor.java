package com.tlear.trise.interactions;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.function.Function;

import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.tlear.trise.environment.Environment;
import com.tlear.trise.objects.EnvObject;
import com.tlear.trise.objects.StaticObstacle;
import com.tlear.trise.utils.Utils;

public class ProximitySensor extends EnvObject implements Sensor {

	/**
	 * HUGE ASSUMPTION: That the proximity sensors will be used orthogonally
	 */

	private float rotation;

	/**
	 * Constructs a proximity sensor at this relative position on the agent
	 * 
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 */
	public ProximitySensor(float x, float y, float width, float height) {
		super(x, y, 0, 0);
		rotation = 0;
	}

	/**
	 * Updates the position of the sensor to the new relative location
	 * 
	 * @param x
	 * @param y
	 */
	public void updatePosition(Vector2 newPos) {
		this.pos.x = newPos.x;
		this.pos.y = newPos.y;
	}

	/**
	 * Adds a vector to the current relative position of the sensor
	 * 
	 * @param addPos
	 */
	public void addPosition(Vector2 addPos) {
		this.pos.add(addPos);
	}

	/**
	 * Updates the rotation of the sensor
	 * 
	 * @param r
	 */
	public void updateRotation(float r) {
		rotation = r;
	}

	/**
	 * Determines how the environment is perceived by this sensor and returns a
	 * plane at the obstacle it perceives
	 */
	@Override
	public Environment apply(Environment t) {
		LinkedList<StaticObstacle> sortedObstacles = new LinkedList<>(
				t.obstacles);
		Vector2 sensorPos = t.agents.getFirst().pos.cpy().add(pos);

		Vector3 intersectionPoint = new Vector3(0, 0, 0);

		Comparator<? super StaticObstacle> sortByX = (StaticObstacle a,
				StaticObstacle b) -> (a.pos.x - b.pos.x) > 0 ? 1 : -1;
		Comparator<? super StaticObstacle> sortByY = (StaticObstacle a,
				StaticObstacle b) -> (a.pos.y - b.pos.y) > 0 ? 1 : -1;

		sortedObstacles.sort(sortXFirst() ? sortByX : sortByY);

		if (sortXFirst()) {
			// Find where the sensor is in the list
			int i = 0;
			StaticObstacle o = sortedObstacles.getFirst();
			while (o.pos.x + o.width < sensorPos.x) {
				i++;
				o = sortedObstacles.get(i);
			}

			// To avoid massive code duplication, define the common variables
			// that will be changed
			// depending on which way the sensor is facing.
			Vector3 direction = new Vector3(0, 0, 0);
			Function<Integer, Boolean> loopCondition = (x) -> false;
			Function<Integer, Integer> changeIndex = (x) -> x;
			Vector3 defaultPoint = new Vector3(0, 0, 0);
			if (rotation > 315 && rotation <= 45) {
				direction = new Vector3(0, 1, 0);
				loopCondition = (x) -> x < sortedObstacles.size() && x > 0;
				changeIndex = (x) -> x + 1;
				defaultPoint.set(sensorPos.x, t.maxY, 0);
			} else if (rotation > 45 && rotation <= 135) {
				direction = new Vector3(1, 0, 0);
				loopCondition = (x) -> x < sortedObstacles.size() && x > 0;
				changeIndex = (x) -> x + 1;
				defaultPoint.set(t.maxX, sensorPos.y, 0);
			} else if (rotation > 135 && rotation <= 225) {
				direction = new Vector3(0, -1, 0);
				loopCondition = (x) -> x > 0;
				changeIndex = (x) -> x - 1;
				defaultPoint.set(sensorPos.x, 0, 0);
			} else {
				direction = new Vector3(-1, 0, 0);
				loopCondition = (x) -> x > 0;
				changeIndex = (x) -> x - 1;
				defaultPoint.set(0, sensorPos.y, 0);
			}

			// Then fire off the sensor and see what it hits
			Ray sensorRay = new Ray(new Vector3(sensorPos, 0), direction);
			// Go through from left to right trying to find the intersection
			// point for the sensor.
			while (loopCondition.apply(i)) {
				o = sortedObstacles.get(i);
				if (Intersector.intersectRayBounds(sensorRay,
						Utils.getBoundingBoxForEnvObject(o), intersectionPoint)) {
					i = -1;
				}
				changeIndex.apply(i);
			}
			// If the intersection point is not set, we set it to the
			// boundary in the direction
			// the sensor is currently pointing in.
			if (intersectionPoint.equals(new Vector3(0, 0, 0))) {
				intersectionPoint.set(defaultPoint);
			}
		}

		// Now we have an intersection point, build an obstacle in at this point
		StaticObstacle o = new StaticObstacle(intersectionPoint.x,
				intersectionPoint.y, 1, 1);
		if (rotation > 315 && rotation <= 45) {
			o.width = t.maxX;
			o.pos.x = 0;
		} else if (rotation > 45 && rotation <= 135) {
			o.height = t.maxY;
			o.pos.y = 0;
		} else if (rotation > 135 && rotation <= 225) {
			o.width = t.maxY;
			o.pos.x = 0;
		} else {
			o.height = t.maxY;
			o.pos.y = 0;
		}

		Environment newEnv = new Environment();
		newEnv.placeObstacle(o);

		return newEnv;
	}

	private boolean sortXFirst() {
		if (rotation > 315 && rotation <= 45 || rotation > 135
				&& rotation <= 225) {
			return false;
		} else if (rotation > 45 && rotation <= 135 || rotation > 225
				&& rotation <= 315) {
			return true;
		} else {
			throw new RuntimeException("Rotation is out of bounds! " + rotation);
		}
	}

}
