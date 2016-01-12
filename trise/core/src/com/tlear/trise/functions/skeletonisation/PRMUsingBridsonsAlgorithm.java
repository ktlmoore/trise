package com.tlear.trise.functions.skeletonisation;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import com.badlogic.gdx.math.Vector2;
import com.tlear.trise.environment.Environment;
import com.tlear.trise.graph.Node;
import com.tlear.trise.graph.TrackedGraph;
import com.tlear.trise.graph.TrackedUndirectedGraph;
import com.tlear.trise.objects.StaticObstacle;

public class PRMUsingBridsonsAlgorithm implements S13n {

	/**
	 * The proportion of points to place on the map
	 */
	private int noPoints;
	private int maxNeighbours;
	
	
	public PRMUsingBridsonsAlgorithm(int noPoints, int maxNeighbours) {
		this.noPoints = noPoints;
		this.maxNeighbours = maxNeighbours;
	} 
	
	@Override
	public TrackedGraph<Vector2> skeletonise(Environment t) {
		
		Environment env = new Environment(t);
		LinkedList<StaticObstacle> newObs = new LinkedList<StaticObstacle>();
		
		/*
		 * Enlarge all the obstacles so that they take into account the width and height of the robot
		 */
		for (StaticObstacle o : env.obstacles) {
			StaticObstacle p = new StaticObstacle(o);
			p.pos.sub(env.agents.getFirst().width/2, env.agents.getFirst().height/2);
			p.width += env.agents.getFirst().width;
			p.height += env.agents.getFirst().height;
			newObs.add(p);
		}
		
		env.obstacles = new LinkedList<>(newObs);
		
		Set<Vector2> points = new HashSet<>();
		
		/*
		 * DO BRIDSON'S ALGORITHM HERE
		 */
		bridson(t, points);
		
		// Once we have all the points, we create a graph using those as the nodes
		TrackedGraph<Vector2> roadmap = new TrackedUndirectedGraph<Vector2>(env.agents.getFirst().pos);
		env.goals.stream().forEach(g -> roadmap.addNode(g.pos));
		points.stream().forEach(p -> roadmap.addNode(p));
		
		/*
		 * We then go through the roadmap and connect each point to its closest legal
		 * neighbours.  A legal neighbour is one we don't have to draw a line through a
		 * rectangle to get to.
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
	
	private void bridson(Environment env, Set<Vector2> points) {
		/*
		 * INPUT: maxX, maxY
		 * 		  r minimum distance between samples
		 * 		  k for number of samples
		 */
		
		// Let's calculate r as being a function of the size of the world and the number of points we want
		float r = (env.maxX * env.maxY) / noPoints;
		int k = 30;	// Typical value
		
		/*
		 * STEP 1: Initialise a 2D background grid with cell size r/sqrt(2)
		 * 			such that each cell has at most one sample.  This is a
		 * 			2D grid of ints: -1 for no sample, otherwise the index
		 * 			of the sample in the cell
		 */
		
		float cellSize = (float) (r / (Math.sqrt(2)));
		int noXCells = (int) (env.maxX / cellSize);
		int noYCells = (int) (env.maxY / cellSize);
		int grid[][] = new int[noXCells][noYCells];
		for (int i = 0; i < noXCells; i++) {
			for (int j = 0; j < noXCells; j++) {
				grid[i][j] = -1;
			}
		}
		
		/*
		 * STEP 2: Select a random point x0, insert it into the background grid
		 * 			and initialise the active list with it.
		 */
		Vector2 x0 = new Vector2((float) (Math.random() * env.maxX), (float) (Math.random() * env.maxY));
		LinkedList<Vector2> activePoints = new LinkedList<Vector2>();
		activePoints.add(x0);
	}

}
