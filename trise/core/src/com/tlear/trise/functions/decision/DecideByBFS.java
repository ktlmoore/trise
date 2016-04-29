package com.tlear.trise.functions.decision;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.math.Vector2;
import com.tlear.trise.environment.Environment;
import com.tlear.trise.functions.GoalFunction;
import com.tlear.trise.functions.skeletonisation.ProbabilisticRoadMap;
import com.tlear.trise.graph.Node;
import com.tlear.trise.graph.TrackedGraph;
import com.tlear.trise.graph.TrackedUndirectedGraph;
import com.tlear.trise.interactions.Action;
import com.tlear.trise.interactions.MoveToAction;
import com.tlear.trise.interactions.NoAction;
import com.tlear.trise.metrics.MutableMetrics;
import com.tlear.trise.utils.Tuple;

public class DecideByBFS implements DecisionFunction {

	private ProbabilisticRoadMap probabilisticRoadMap;

	private LinkedList<Node<Vector2>> frontier;
	private HashSet<Node<Vector2>> explored;

	private TrackedGraph<Vector2> prm;
	private GoalFunction goal;

	private Map<Node<Vector2>, Node<Vector2>> pathBack; // Path back stores the
														// links back from the
														// goal
	private List<Node<Vector2>> path = new LinkedList<>();

	private MutableMetrics metrics;

	private boolean atGoal;

	private boolean initialised;

	@Override
	public void reset() {
		initialised = false;
	}

	public DecideByBFS(GoalFunction goal) {
		probabilisticRoadMap = new ProbabilisticRoadMap(1000, 10);
		frontier = new LinkedList<>();
		prm = new TrackedUndirectedGraph<>();
		initialised = false;
		this.goal = goal;
		pathBack = new HashMap<Node<Vector2>, Node<Vector2>>();
		explored = new HashSet<Node<Vector2>>();
		atGoal = false;
		metrics = new MutableMetrics();
	}

	@Override
	public Tuple<MutableMetrics, Tuple<Action, TrackedGraph<Vector2>>> apply(
			Environment t) {

		if (!atGoal) {
			System.out.println("APPLYING");
		}
		if (!initialised) {
			metrics.reset();
			pathBack = new HashMap<Node<Vector2>, Node<Vector2>>();
			System.out.println("INITIALISING");

			long startTime = System.currentTimeMillis();
			prm = probabilisticRoadMap.skeletonise(t);
			long endTime = System.currentTimeMillis();

			metrics.setTimeToSkeletonise(endTime - startTime);
			atGoal = false;
			initialised = true;
		}

		if (path.size() <= 0 && !atGoal) {
			frontier = new LinkedList<>();
			explored = new HashSet<Node<Vector2>>();
			pathBack = new HashMap<Node<Vector2>, Node<Vector2>>();
			Node<Vector2> startNode = prm.findNode(t.agents.getFirst().pos);
			frontier.add(startNode);

			if (goal.apply(t, startNode)) {
				atGoal = true;
			}

			Node<Vector2> goalNode = null;
			System.out.println("SEARCHING");

			pathBack.put(startNode, startNode);

			int nodesExplored = 0;
			int nodesInFrontier = 1;

			long startTime = System.currentTimeMillis();
			while (!frontier.isEmpty() && goalNode == null) {
				// System.out.println("Frontier has " + frontier.size() +
				// " nodes");
				Node<Vector2> node = frontier.pop();
				explored.add(node);
				nodesExplored++;
				// System.out.println("Frontier has " + frontier.size() +
				// " nodes after popping");
				// System.out.println(node);
				if (goal.apply(t, node)) {
					goalNode = node;
				} else {
					int oldFrontier = frontier.size();
					node.getNeighbours().forEach(n -> {
						if (!explored.contains(n)) {

							frontier.add(n);
							pathBack.put(n, node);
						}
					});
					nodesInFrontier += frontier.size() - oldFrontier;
					// System.out.println("Frontier has " + frontier.size() +
					// " nodes after exploring");
				}
			}
			long endTime = System.currentTimeMillis();

			metrics.setTimeToSearch(endTime - startTime);
			metrics.setNodesExplored(nodesExplored);
			metrics.setNodesInFrontier(nodesInFrontier);

			System.out.println("ROUTE FOUND");
			// Once we have found the goal, we then follow the path back to the
			// start

			path.add(goalNode);
			System.out.println("GOAL: " + goalNode);
			Node<Vector2> node = pathBack.get(goalNode);
			do {
				path.add(node);
				node = pathBack.get(node);
				System.out.println("NODE: " + node);
				System.out.println("START: " + startNode);
			} while (!node.equals(startNode));

			Collections.reverse(path);
		}

		if (!atGoal) {
			Node<Vector2> next = path.remove(0);
			if (next == null) {
				next = new Node<Vector2>(t.agents.getFirst().pos.cpy());
			}
			Action a = new MoveToAction(t.agents.getFirst().pos.cpy(), next
					.getValue().cpy());
			prm.visit(next);

			return new Tuple<>(metrics, new Tuple<>(a, prm));
		} else {
			return new Tuple<>(metrics, new Tuple<>(new NoAction(), prm));
		}
	}

	@Override
	public String getName() {
		return "Breadth First Search";
	}

	@Override
	public String getS13nName() {
		return "PRM";
	}

}
