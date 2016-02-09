package com.tlear.trise.functions.decision;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;

import com.badlogic.gdx.math.Vector2;
import com.tlear.trise.environment.Environment;
import com.tlear.trise.functions.GoalFunction;
import com.tlear.trise.functions.HeuristicFunction;
import com.tlear.trise.functions.skeletonisation.GridMap;
import com.tlear.trise.functions.skeletonisation.S13n;
import com.tlear.trise.graph.Node;
import com.tlear.trise.graph.TrackedGraph;
import com.tlear.trise.graph.TrackedUndirectedGraph;
import com.tlear.trise.interactions.Action;
import com.tlear.trise.interactions.MoveToAction;
import com.tlear.trise.utils.Tuple;

public class DecideByAStarSearch implements DecisionFunction {

	private HeuristicFunction heuristicFunction;

	private Queue<Node<Vector2>> frontier;
	private HashSet<Node<Vector2>> explored;

	private TrackedGraph<Vector2> prm;
	private GoalFunction goal;

	private Map<Node<Vector2>, Node<Vector2>> pathBack; // Path back stores the
														// links back from the
														// goal
	private List<Node<Vector2>> path = new LinkedList<>();

	private boolean initialised;

	private S13n map;

	public DecideByAStarSearch(GoalFunction goal, HeuristicFunction heuristicFunction) {
		// probabilisticRoadMap = new ProbabilisticRoadMap(1000, 10);
		map = new GridMap(10);
		frontier = new PriorityQueue<>();
		prm = new TrackedUndirectedGraph<>();
		initialised = false;
		this.goal = goal;
		pathBack = new HashMap<Node<Vector2>, Node<Vector2>>();
		explored = new HashSet<Node<Vector2>>();
		this.heuristicFunction = heuristicFunction;
	}

	@Override
	public Tuple<Action, TrackedGraph<Vector2>> apply(Environment t) {

		System.out.println("APPLYING");
		if (!initialised) {
			pathBack = new HashMap<Node<Vector2>, Node<Vector2>>();
			System.out.println("INITIALISING");
			prm = map.skeletonise(t);
			initialised = true;
			t.clean();
		}

		if (path.size() <= 0) {

			frontier = new PriorityQueue<>((x, y) -> (int) (heuristicFunction.apply(x) - heuristicFunction.apply(y)));
			explored = new HashSet<Node<Vector2>>();
			pathBack = new HashMap<Node<Vector2>, Node<Vector2>>();
			Node<Vector2> startNode = prm.findNode(t.agents.getFirst().pos);
			frontier.add(startNode);

			Node<Vector2> goalNode = null;
			System.out.println("SEARCHING");

			pathBack.put(startNode, startNode);
			while (!frontier.isEmpty() && goalNode == null) {
				Node<Vector2> node = frontier.poll();
				explored.add(node);
				if (goal.apply(t, node)) {
					goalNode = node;
				} else {
					node.getNeighbours().forEach(n -> {
						if (!explored.contains(n)) {

							frontier.add(n);
							pathBack.put(n, node);
						}
					});
				}
			}
			System.out.println("ROUTE FOUND");

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

		Node<Vector2> next = path.remove(0);
		if (next == null) {
			next = new Node<Vector2>(t.agents.getFirst().pos.cpy());
		}

		Action a = new MoveToAction(t.agents.getFirst().pos.cpy(), next.getValue().cpy());
		prm.visit(next);
		return new Tuple<Action, TrackedGraph<Vector2>>(a, prm);
	}

}
