package com.tlear.trise;

import java.util.LinkedHashMap;
import java.util.Map;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.tlear.trise.environment.Environment;
import com.tlear.trise.graph.Edge;
import com.tlear.trise.graph.Graph;
import com.tlear.trise.graph.Node;
import com.tlear.trise.graph.TrackedGraph;
import com.tlear.trise.objects.Agent;
import com.tlear.trise.objects.StaticObstacle;
import com.tlear.trise.utils.Triple;
import com.tlear.trise.utils.Tuple;

public class Simulation {

	private Environment env;
	private Map<Integer, Integer> timeMap;
	private int prevKeyframe, nextKeyframe;
	private int time;
	private TrackedGraph<Vector2> g;
	
	public Simulation() {
		createSim();
		
		prevKeyframe = 0;
		nextKeyframe = 0;
		
		time = 0;
		
		Triple<Integer, Integer, TrackedGraph<Vector2>> next = env.getNextKeyframe(timeMap);
		timeMap.put(next.fst, next.snd);
		g = next.thd;
		
		nextKeyframe++;
	}
	
	public void update() {
//		System.out.println("UPDATE SIMULATION");
//		System.out.println("PREV: " + prevKeyframe + ", NEXT: " + nextKeyframe);
//		System.out.println("TIME MAP: " + timeMap.toString());
		
//		System.out.println(nextKeyframe);
		
		if (timeMap.get(nextKeyframe) == time) {
			prevKeyframe = nextKeyframe;
			
			Triple<Integer, Integer, TrackedGraph<Vector2>> next = env.getNextKeyframe(timeMap);
			timeMap.put(next.fst, next.snd);
			g = next.thd;
			
			nextKeyframe++;
		}
		
//		System.out.println(time);
		
		time++;
		env.update(timeMap, prevKeyframe, time, nextKeyframe);
	}
	
	public void draw(ShapeRenderer sr, SpriteBatch batch) {
		
		BitmapFont font = new BitmapFont();
        font.setColor(Color.WHITE);
		
		
		sr.begin(ShapeType.Line);
		sr.setColor(0, 0, 0, 1);
		for (Edge<Vector2> e : g.getEdges()) {
			sr.line(e.fst.getValue(), e.snd.getValue());
		}
		sr.end();
		
		sr.begin(ShapeType.Filled);
		sr.setColor(1, 1, 0, 1);
		for (Node<Vector2> n : g.getNodes()) {
			if (g.visited(n)) {
				sr.setColor(0, 0, 1, 1);
			} else {
				sr.setColor(1, 1, 0, 1);
			}
			Vector2 v = n.getValue();
			sr.circle(v.x, v.y, 5);
		}
		sr.end();

		env.draw(sr, batch);
		
		System.out.println(g.getExploredNodes().size() + " / " + g.getNodes().size());
		
		batch.begin();
		font.draw(batch, (float) (100 * g.getExploredNodes().size() / g.getNodes().size()) + "%", 600, 50);
		batch.end();
	}
	
	private void createSim() {
		env = new Environment();
		Agent a = new Agent(10, 10, 25, 25);
		env.placeAgent(a);
		
		StaticObstacle o = new StaticObstacle(100, 100, 100, 100);
		env.placeObstacle(o);
		o = new StaticObstacle(300, 300, 100, 100);
		env.placeObstacle(o);
		o = new StaticObstacle(100, 300, 100, 100);
		env.placeObstacle(o);
		o = new StaticObstacle(300, 100, 100, 100);
		env.placeObstacle(o);
		
//		System.out.println("Creating sim: " + env);
		timeMap = new LinkedHashMap<>();
		timeMap.put(0, 0);
	}
}
