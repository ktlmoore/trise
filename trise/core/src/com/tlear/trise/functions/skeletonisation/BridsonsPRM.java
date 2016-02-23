package com.tlear.trise.functions.skeletonisation;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.math.Vector2;
import com.tlear.trise.environment.Environment;
import com.tlear.trise.graph.Node;
import com.tlear.trise.graph.TrackedGraph;
import com.tlear.trise.graph.TrackedUndirectedGraph;
import com.tlear.trise.objects.StaticObstacle;

public class BridsonsPRM extends ProbabilisticRoadMap {

	public BridsonsPRM(int noPoints, int maxNeighbours) {
		super(noPoints, maxNeighbours);
	}

	@Override
	public TrackedGraph<Vector2> skeletonise(Environment t) {

		System.out.println("BRIDSON");

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

		Map<Integer, Vector2> points = new HashMap<>();
		List<Vector2> activeList = new LinkedList<>();

		// We use Bridson's algorithm to generate points
		// k is number of samples to try before rejection
		// r is the minimum distance between samples.
		// Step 0: initialise a 2-dimensional grid for storing samples, with
		// cell size r/sqrt(n)
		// Each cell will contain at most one sample.
		int k = 30; // Typical value
		int r = 10; // Spread out by 10 pixels
		int[][] samples = new int[env.maxX / r][env.maxY / r];
		int ptr = 0;

		// Step 1: Select a point x0 randomly, insert it into the samples and
		// initialise the active list with this index
		Vector2 x0 = new Vector2((float) (Math.random() * env.maxX), (float) (Math.random() * env.maxY));
		points.put(ptr, x0);
		activeList.add(x0);
		samples[(int) (x0.x / r)][(int) (x0.y / r)] = ptr;

		ptr++;

		// Step 2: While the active list is not empty
		// Choose a random index in it, xi
		// Up to k times, pick a point between r and 2r away from xi
		// Check if it is too close to any other samples using the background
		// grid. If it isn't we add it. If we fail after k, remove xi

		while (!activeList.isEmpty()) {

			Vector2 xi = activeList.get(0);

			for (int i = 0; i < k; i++) {

				System.out.println(i);
				float theta = (float) (Math.random() * 360);
				Vector2 newVector = new Vector2();
				newVector.setAngle(theta);
				newVector.setLength(r + (float) (Math.random() * r));

				if (samples[(int) (newVector.x / r)][(int) (newVector.y / r)] != 0) {
					points.put(ptr, xi);
					activeList.add(xi);
					samples[(int) (newVector.x / r)][(int) (newVector.y / r)] = ptr;
					ptr++;
				}
			}
			activeList.remove(0);
		}

		// Once we have all the points, we create a graph using those as the
		// nodes
		TrackedGraph<Vector2> roadmap = new TrackedUndirectedGraph<Vector2>(env.agents.getFirst().pos);
		env.goals.stream().forEach(g -> roadmap.addNode(g.pos));
		points.values().stream().forEach(p -> roadmap.addNode(p));

		/*
		 * We then go through the roadmap and connect each point to its closest
		 * legal neighbours. A legal neighbour is one we don't have to draw a
		 * line through a rectangle to get to.
		 */
		for (int n = 0; n < roadmap.getNodes().size(); n++) {
			LinkedList<Node<Vector2>> nearestNeighbours = new LinkedList<Node<Vector2>>();
			Node<Vector2> node = roadmap.getNodes().get(n);

			// First we generate all legal links
			for (Node<Vector2> m : roadmap.getNodes()) {
				if (!node.equals(m)) {

					boolean legalEdge = true;

					for (StaticObstacle o : env.obstacles) {
						if (o.intersectsLine(node.getValue(), m.getValue())) {
							legalEdge = false;
							break;
						}
					}

					if (legalEdge) {
						nearestNeighbours.add(m);
					}
				}
			}

			// Then we sort and only take the closest ones
			nearestNeighbours.sort((x, y) -> isCloser(x.getValue(), y.getValue(), node.getValue()));
			nearestNeighbours = new LinkedList<>(nearestNeighbours.subList(0, Math.min(maxNeighbours, nearestNeighbours.size())));

			for (Node<Vector2> nearestNeighbour : nearestNeighbours) {
				roadmap.addEdge(node, nearestNeighbour);
			}
		}
		return roadmap;
	}

	private int isCloser(Vector2 p, Vector2 q, Vector2 s) {
		// Returning sp - sq will give us which is further away
		return (int) ((p.cpy().sub(s).len() - q.cpy().sub(s).len()));
	}
	
	public String getName() {
		return "Bridson's Algorithm PRM";
	}
}
