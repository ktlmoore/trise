package com.tlear.trise;

import java.util.LinkedHashMap;
import java.util.Map;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.tlear.trise.environment.Environment;
import com.tlear.trise.objects.Agent;
import com.tlear.trise.utils.Tuple;

public class Simulation {

	private Environment env;
	private Map<Integer, Integer> timeMap;
	private int prevKeyframe, nextKeyframe;
	private int time;
	
	public Simulation() {
		createSim();
		
		prevKeyframe = 0;
		nextKeyframe = 0;
		
		time = 0;
		
		Tuple<Integer, Integer> mapEntry = env.getNextKeyframe(timeMap);
		timeMap.put(mapEntry.fst, mapEntry.snd);
		
		nextKeyframe++;
	}
	
	public void update() {
//		System.out.println("UPDATE SIMULATION");
//		System.out.println("PREV: " + prevKeyframe + ", NEXT: " + nextKeyframe);
//		System.out.println("TIME MAP: " + timeMap.toString());
		
//		System.out.println(nextKeyframe);
		
		if (timeMap.get(nextKeyframe) == time) {
			prevKeyframe = nextKeyframe;
			
			Tuple<Integer, Integer> mapEntry = env.getNextKeyframe(timeMap);
			timeMap.put(mapEntry.fst, mapEntry.snd);
			
			nextKeyframe++;
		}
		
		System.out.println(time);
		
		time++;
		env.update(timeMap, prevKeyframe, time, nextKeyframe);
	}
	
	public void draw(ShapeRenderer sr) {
		env.draw(sr);
	}
	
	private void createSim() {
		env = new Environment();
		Agent a = new Agent(10, 10, 25, 25);
		env.placeAgent(a);
		
//		System.out.println("Creating sim: " + env);
		timeMap = new LinkedHashMap<>();
		timeMap.put(0, 0);
	}
}
