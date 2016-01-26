package com.tlear.trise;

import java.util.LinkedHashMap;
import java.util.Map;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.tlear.trise.environment.Environment;
import com.tlear.trise.graph.Edge;
import com.tlear.trise.graph.Node;
import com.tlear.trise.graph.TrackedGraph;
import com.tlear.trise.objects.Agent;
import com.tlear.trise.objects.EnvObject;
import com.tlear.trise.objects.StaticGoal;
import com.tlear.trise.objects.StaticObstacle;
import com.tlear.trise.utils.Triple;

public class Simulation {

	private Environment env;
	private Map<Integer, Integer> timeMap;
	private int prevKeyframe, nextKeyframe;
	private int time;
	private TrackedGraph<Vector2> g;

	private TRISE parent;

	public EnvObject selectedObject;

	public Simulation(TRISE parent) {
		this.parent = parent;

		createSim();

		prevKeyframe = 0;
		nextKeyframe = 0;

		time = 0;

		Triple<Integer, Integer, TrackedGraph<Vector2>> next = env.getNextKeyframe(timeMap);
		timeMap.put(next.fst, next.snd);
		g = next.thd;

		nextKeyframe++;

		selectedObject = null;
	}

	public void update() {
		// System.out.println("UPDATE SIMULATION");
		// System.out.println("PREV: " + prevKeyframe + ", NEXT: " +
		// nextKeyframe);
		// System.out.println("TIME MAP: " + timeMap.toString());

		// System.out.println(nextKeyframe);

		if (timeMap.get(nextKeyframe) == time) {
			prevKeyframe = nextKeyframe;

			Triple<Integer, Integer, TrackedGraph<Vector2>> next = env.getNextKeyframe(timeMap);
			timeMap.put(next.fst, next.snd);
			g = next.thd;

			nextKeyframe++;
		}

		// System.out.println(time);

		time++;
		env.update(timeMap, prevKeyframe, time, nextKeyframe);
	}

	public void draw(ShapeRenderer sr, SpriteBatch batch) {

		BitmapFont font = new BitmapFont();
		font.setColor(Color.WHITE);

		// Draw the graph
		if (parent.showGraph) {
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
				sr.circle(v.x, v.y, 2);
			}
			sr.end();
		}

		env.draw(sr, batch);

		// System.out.println(g.getExploredNodes().size() + " / " +
		// g.getNodes().size());

		batch.begin();
		font.draw(batch, (float) (100 * g.getExploredNodes().size() / g.getNodes().size()) + "%", 600, 50);
		font.draw(batch, parent.modeEdit ? "EDIT" : "", 600, 100);
		font.draw(batch, parent.modeSim ? "SIMULATING" : "", 600, 150);
		batch.end();
	}

	public EnvObject getObjectContainingPoint(Vector2 pos) {
		// Returns the environment object that this position contains
		// for (Agent a : env.agents) {
		// if (a.containsPoint(pos)) {
		// return a;
		// }
		// }
		for (StaticObstacle o : env.obstacles) {
			if (o.containsPoint(pos)) {
				return o;
			}
		}
		for (StaticGoal g : env.goals) {
			if (g.containsPoint(pos)) {
				return g;
			}
		}
		return null;
	}

	public void selectEnvObject(EnvObject obj) {
		// Pre: obj != null
		if (obj == null) {
			throw new NullPointerException("Object to be selected must not be null!!!");
		}
		if (!obj.selected) {
			obj.select();
			if (selectedObject != null) {
				selectedObject.deselect();
			}
			selectedObject = obj;
		} else {
			obj.deselect();
			selectedObject = null;
		}
	}

	public void dirtyEnvironment() {
		env.dirty();
	}

	public void cleanEnvironment() {
		env.clean();
	}

	private void createSim() {
		env = new Environment();

		StaticObstacle o = new StaticObstacle(100, 100, 100, 100);
		env.placeObstacle(o);
		o = new StaticObstacle(300, 300, 100, 100);
		env.placeObstacle(o);
		o = new StaticObstacle(100, 300, 100, 100);
		env.placeObstacle(o);
		o = new StaticObstacle(300, 100, 100, 100);
		env.placeObstacle(o);

		StaticGoal g = new StaticGoal(450, 450, 20, 20);
		env.placeGoal(g);

		Agent a = new Agent(10, 10, 25, 25, env);
		env.placeAgent(a);

		// g = new StaticGoal(250, 250, 20, 20);
		// env.placeGoal(g);
		// g = new StaticGoal(450, 250, 20, 20);
		// env.placeGoal(g);
		// g = new StaticGoal(250, 450, 20, 20);
		// env.placeGoal(g);
		// g = new StaticGoal(250, 50, 20, 20);
		// env.placeGoal(g);
		// g = new StaticGoal(50, 250, 20, 20);
		// env.placeGoal(g);

		// System.out.println("Creating sim: " + env);
		timeMap = new LinkedHashMap<>();
		timeMap.put(0, 0);
	}

	public void deleteObject(EnvObject obj) {
		if (obj instanceof Agent) {
			env.agents.remove(obj);
		} else if (obj instanceof StaticObstacle) {
			env.obstacles.remove(obj);
		} else if (obj instanceof StaticGoal) {
			env.goals.remove(obj);
		} else {
			throw new RuntimeException("Could not delete object from simulation");
		}

	}
}
