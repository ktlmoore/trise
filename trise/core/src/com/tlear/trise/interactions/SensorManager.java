package com.tlear.trise.interactions;

import java.util.Collection;
import java.util.PriorityQueue;
import java.util.function.Function;

import com.tlear.trise.environment.Environment;

public class SensorManager implements Function<Environment, Environment> {
	/*
	 * We maintain a priority queue of sensors that will be prioritising
	 * OmniscientSensors.
	 */
	private PriorityQueue<Sensor> sensors;

	/**
	 * Constructs a sensor manager managing no sensors
	 */
	public SensorManager() {
		sensors = new PriorityQueue<Sensor>(
				(x, y) -> priorityToOmniscientSensors(x, y));
	}

	/**
	 * Constructs a sensors manager managing all the given sensors
	 * 
	 * @param sensors
	 */
	public SensorManager(Collection<Sensor> sensors) {
		this();
		this.sensors.addAll(sensors);
	}

	/**
	 * Constructs a deep copy of a given sensor manager.
	 * 
	 * @param that
	 */
	public SensorManager(SensorManager that) {
		this.sensors = new PriorityQueue<Sensor>(that.sensors);
	}

	/**
	 * Adds a sensor to the sensors being managed
	 * 
	 * @param sensor
	 */
	public void addSensor(Sensor sensor) {
		sensors.add(sensor);
	}

	/**
	 * Deletes a given sensor from the sensor array and returns true if it was
	 * possible.
	 * 
	 * @param sensor
	 * @return
	 */
	public boolean deleteSensor(Sensor sensor) {
		return sensors.remove(sensor);
	}

	/**
	 * The sensor manager determines what all of the sensors are able to
	 * confirm.
	 */
	@Override
	public Environment apply(Environment t) {

		/*
		 * If we have any omniscient sensors, they will be the first sensor on
		 * the list.
		 */
		Sensor firstSensor = sensors.peek();
		if (firstSensor instanceof OmniscientSensor) {
			return firstSensor.apply(t);
		}

		/*
		 * Otherwise, we'll have to do some complex intersections of the things
		 * sensors tell us. But we can't do that right now, so just throw a
		 * hissy fit.
		 */
		throw new RuntimeException("Sensor was not omniscient!");
	}

	/**
	 * If a xor b is an OmniscientSensor it will be prioritised
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	private int priorityToOmniscientSensors(Sensor a, Sensor b) {
		if (a instanceof OmniscientSensor) {
			if (b instanceof OmniscientSensor) {
				return 0;
			} else {
				return -1;
			}
		} else if (b instanceof OmniscientSensor) {
			return 1;
		} else {
			return 0;
		}
	}

}
