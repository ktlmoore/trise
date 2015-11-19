package com.tlear.trise;

import java.util.LinkedHashMap;
import java.util.Map;

import com.tlear.trise.environment.Environment;

public class Simulation {

	private Environment env;
	private Map<Integer, Integer> timeMap;
	private int prevKeyframe, nextKeyframe;
	private int time;
	
	public Simulation() {
		env = new Environment();
		timeMap = new LinkedHashMap<>();
		timeMap.put(0,  0);
	}
	
	public void update() {
		env.update(timeMap, prevKeyframe, time, nextKeyframe);
		time++;
		if (timeMap.get(nextKeyframe) == time) {
			prevKeyframe = nextKeyframe;
			env.getNextKeyframe(timeMap);
			nextKeyframe++;
		}
	}
}
