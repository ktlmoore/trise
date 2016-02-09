package com.tlear.trise.functions.skeletonisation;

import java.util.LinkedList;

import com.badlogic.gdx.math.Vector2;
import com.tlear.trise.environment.Environment;
import com.tlear.trise.graph.TrackedGraph;
import com.tlear.trise.graph.TrackedUndirectedGraph;
import com.tlear.trise.objects.StaticGoal;
import com.tlear.trise.objects.StaticObstacle;

public class GridMap implements S13n {

	private int squareSize;

	public GridMap(int squareSize) {
		this.squareSize = squareSize;
	}

	@Override
	public TrackedGraph<Vector2> skeletonise(Environment t) {

		Environment env = new Environment(t);
		LinkedList<StaticObstacle> newObs = new LinkedList<StaticObstacle>();

		for (StaticObstacle o : env.obstacles) {
			StaticObstacle p = new StaticObstacle(o);
			p.pos.sub(env.agents.getFirst().width / 2, env.agents.getFirst().height / 2);
			p.width += env.agents.getFirst().width;
			p.height += env.agents.getFirst().height;
			newObs.add(p);
		}

		env.obstacles = new LinkedList<>(newObs);

		Vector2[][] points = new Vector2[(env.maxX / squareSize) + 2][(env.maxY / squareSize) + 2];

		for (int j = 0; j < env.maxY / squareSize; j++) {
			points[0][j] = null;
			points[(env.maxX / squareSize) + 1][j] = null;
		}
		for (int i = 0; i < env.maxX / squareSize; i++) {
			points[i][0] = null;
			points[i][(env.maxY / squareSize) + 1] = null;
		}

		for (int i = squareSize; i < env.maxX; i += squareSize) {
			for (int j = squareSize; j < env.maxY; j += squareSize) {
				Vector2 newPoint = new Vector2(i, j);
				boolean valid = true;
				for (StaticObstacle o : env.obstacles) {
					if (o.containsPoint(newPoint)) {
						valid = false;
					}
				}
				if (valid) {
					points[i / squareSize][j / squareSize] = newPoint;
				} else {
					points[i / squareSize][j / squareSize] = null;
				}
			}
		}

		TrackedGraph<Vector2> grid = new TrackedUndirectedGraph<Vector2>(points[1][1]);

		for (int i = 1; i < env.maxX / squareSize; i++) {
			for (int j = 1; j < env.maxY / squareSize; j++) {
				if (points[i][j] != null) {

					grid.addNode(points[i][j]);
				}
			}
		}

		grid.addNode(env.agents.getFirst().pos);
		env.goals.forEach(x -> grid.addNode(x.pos));

		for (int i = 1; i < env.maxX / squareSize; i++) {
			for (int j = 1; j < env.maxY / squareSize; j++) {
				if (points[i][j] != null) {
					// N
					if (points[i][j + 1] != null) {
						grid.addEdge(grid.findNode(points[i][j]), grid.findNode(points[i][j + 1]));
					}
					// E
					if (points[i + 1][j] != null) {
						grid.addEdge(grid.findNode(points[i][j]), grid.findNode(points[i + 1][j]));
					}
					// S
					if (points[i][j - 1] != null) {
						grid.addEdge(grid.findNode(points[i][j]), grid.findNode(points[i][j - 1]));
					}
					// W
					if (points[i - 1][j] != null) {
						grid.addEdge(grid.findNode(points[i][j]), grid.findNode(points[i - 1][j]));
					}
				}
			}
		}

		Vector2 startPoint = env.agents.getFirst().pos.cpy();
		Vector2 nearestPointToStart = points[(int) (startPoint.x / squareSize)][(int) (startPoint.y / squareSize)];
		grid.addEdge(grid.findNode(startPoint), grid.findNode(nearestPointToStart));

		for (StaticGoal g : env.goals) {
			Vector2 goalPoint = g.pos.cpy();
			Vector2 nearestPointToGoal = points[(int) (goalPoint.x / squareSize)][(int) (goalPoint.y / squareSize)];
			grid.addEdge(grid.findNode(goalPoint), grid.findNode(nearestPointToGoal));
		}

		return grid;
	}
}
