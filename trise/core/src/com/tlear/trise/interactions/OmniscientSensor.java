package com.tlear.trise.interactions;

import com.tlear.trise.environment.Environment;

public class OmniscientSensor implements Sensor {

	@Override
	public Environment apply(Environment t) {
		return t;
	}

}
