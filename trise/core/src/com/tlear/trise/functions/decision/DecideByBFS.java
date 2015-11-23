package com.tlear.trise.functions.decision;

import java.util.LinkedList;

import com.badlogic.gdx.math.Vector2;
import com.tlear.trise.environment.Environment;
import com.tlear.trise.functions.GoalFunction;
import com.tlear.trise.functions.skeletonisation.ProbabilisticRoadMap;
import com.tlear.trise.graph.Graph;
import com.tlear.trise.graph.Node;
import com.tlear.trise.graph.TrackedGraph;
import com.tlear.trise.graph.UndirectedGraph;
import com.tlear.trise.interactions.Action;
import com.tlear.trise.utils.Tuple;

public class DecideByBFS implements DecisionFunction {

	private ProbabilisticRoadMap probabilisticRoadMap;
	
	private LinkedList<Node<Vector2>> frontier;
	private Graph<Vector2> prm;
	private GoalFunction goal;
	
	private boolean initialised;
	
	public DecideByBFS(GoalFunction goal) {
		probabilisticRoadMap = new ProbabilisticRoadMap(500, 10);
		frontier = new LinkedList<>();
		prm = new UndirectedGraph<>();
		initialised = false;
		this.goal = goal;
	}
	
	@Override
	public Tuple<Action, TrackedGraph<Vector2>> apply(Environment t) {
		
		if (!initialised) {
			prm = probabilisticRoadMap.skeletonise(t);
			
			frontier.add(prm.findNode(t.agents.getFirst().pos));
			boolean foundGoal = false;
			
			while (!frontier.isEmpty() && !foundGoal) {
				Node<Vector2> node = frontier.pop();
				if (goal.apply(t, node)) {
					foundGoal = true;
				} else {
					node.getNeighbours().forEach(n -> frontier.add(n));					
				}				
			}

			initialised = true;
		}
		
		
		
		return null;
	}

}
