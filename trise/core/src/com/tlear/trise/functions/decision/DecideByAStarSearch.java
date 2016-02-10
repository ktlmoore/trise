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
import com.tlear.trise.interactions.NoAction;
import com.tlear.trise.metrics.MutableMetrics;
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
	
	private MutableMetrics metrics;
	
	private boolean atGoal;

	private S13n map;

	/**
	 * Constructs an A* Decision Function using a given goal test and h(x).
	 * @param goal
	 * @param heuristicFunction
	 */
	public DecideByAStarSearch(GoalFunction goal, HeuristicFunction heuristicFunction) {
		// probabilisticRoadMap = new ProbabilisticRoadMap(1000, 10);
		map = new GridMap(50);
		frontier = new PriorityQueue<>();
		prm = new TrackedUndirectedGraph<>();
		initialised = false;
		this.goal = goal;
		pathBack = new HashMap<Node<Vector2>, Node<Vector2>>();
		explored = new HashSet<Node<Vector2>>();
		this.heuristicFunction = heuristicFunction;
		atGoal = false;
		
		metrics = new MutableMetrics();
	}

	/**
	 * Applies the decision function on the environment to get the decision graph and the action the agent should take
	 * @param env
	 */
	@Override
	public Tuple<MutableMetrics, Tuple<Action, TrackedGraph<Vector2>>> apply(Environment t) {

		if (!atGoal) {
			System.out.println("======================================");
			System.out.println("Applying A* Decision Function...");
			System.out.println("======================================");
		}
		
		// First we check to see if we've even got a decision graph.  If not, we make sure we do.
		if (!initialised) {
			
			System.out.println("Initialising decision graph...");
			
			// Metrics back to init
			metrics.reset();
			// The environment is not longer dirty
			t.clean();
			// Clean up path back
			pathBack = new HashMap<Node<Vector2>, Node<Vector2>>();
			// Assume we are not at the goal
			atGoal = false;
			
			System.out.println("Skeletonising environment...");
			
			// Skeletonise (and timestamp the ends)
			long startTime = System.currentTimeMillis();
			prm = map.skeletonise(t);
			long endTime = System.currentTimeMillis();
			
			metrics.setTimeToSkeletonise(endTime - startTime);
			
			System.out.println(String.format("Skeletonised environment in %dms", metrics.getTimeToSkeletonise()));
			
			initialised = true;
		}

		// If the path we're following has no more steps, we look for a new path to follow
		if (path.size() <= 0 && !atGoal) {

			// Reset search tools
			frontier = new PriorityQueue<>((x, y) -> (int) (heuristicFunction.apply(x) - heuristicFunction.apply(y)));
			explored = new HashSet<Node<Vector2>>();
			pathBack = new HashMap<Node<Vector2>, Node<Vector2>>();
			
			// Finds the node closest to the agent's current position
			Node<Vector2> startNode = prm.findNode(t.agents.getFirst().pos);
			Node<Vector2> goalNode = null;
			
			// We check whether this node is a goal node.
			if (goal.apply(t, startNode)) {
				// If we are at a goal, we stop.
				atGoal = true;
				System.out.println("At goal: no action.");
			} else {
				// If we're not at a goal, we continue and put the start node in the frontier.
				// It is also part of the path back to the start, for obvious reasons.
				frontier.add(startNode);
				pathBack.put(startNode, startNode);
	
				System.out.println("Searching for a goal node...");
				
				// Metrics information
				int nodesExplored = 0;
				int nodesInFrontier = 1;
				
				// Timestamp for search
				long startTime = System.currentTimeMillis();
				
				// We now keep looking through the frontier until it is empty or we have found a goal.
				// TODO: Fix to work with multiple goals!
				while (!frontier.isEmpty() && goalNode == null) {
					// Get the next node
					Node<Vector2> node = frontier.poll();
					
					// We explore the node now, in case it has a self-pointer.
					explored.add(node);
					nodesExplored++;
					
					// Check for whether we have a goal
					if (goal.apply(t, node)) {
						// If so, we save it and won't loop back round..
						goalNode = node;
					} else {
						// If not, we keep searching.
						
						// Metrics
						int oldSize = frontier.size();
						
						// Update frontier with new nodes
						node.getNeighbours().forEach(n -> {
							if (!explored.contains(n)) {
								frontier.add(n);
								pathBack.put(n, node);
							}
						});
						
						// Update metrics
						nodesInFrontier += frontier.size() - oldSize;
					}
				}
				
				// Timestamp for search
				long endTime = System.currentTimeMillis();
				
				System.out.println("Route found to a goal node!");
				System.out.println(String.format("Explored %d nodes", nodesExplored));
				System.out.println(String.format("Put %d nodes in the frontier", nodesInFrontier));
				
				// Update metrics
				metrics.setNodesExplored(nodesExplored);
				metrics.setNodesInFrontier(nodesInFrontier);
				metrics.setTimeToSearch(endTime - startTime);
	
				// Add the goal node to the end of the path, as this will be our final node.
				path.add(goalNode);
				Node<Vector2> node = pathBack.get(goalNode);
				
				System.out.println("Goal is: " + goalNode);
				
				// Now we start calculating our path back from the goal to the start
				// We will then reverse this to get the path we wish to follow.
				do {
					path.add(node);
					node = pathBack.get(node);
					System.out.println("NODE: " + node);
					System.out.println("START: " + startNode);
				} while (!node.equals(startNode));
	
				Collections.reverse(path);
			}
		}

		if (!atGoal) {
			// We have a path, so the next node is the next one in the list.
			Node<Vector2> next = path.remove(0);
			if (next == null) {
				next = new Node<Vector2>(t.agents.getFirst().pos.cpy());
			}

			// And then we move to that location, mark it as visited and return the data.  Simple.
			Action a = new MoveToAction(t.agents.getFirst().pos.cpy(), next.getValue().cpy());
			prm.visit(next);
			return new Tuple<>(metrics, new Tuple<>(a, prm));
		} else {
			return new Tuple<>(metrics, new Tuple<>(new NoAction(), prm));
		}
		
	}
	
	public String getName() {
		return "A* Search";
	}

}
