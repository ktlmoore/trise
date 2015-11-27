package com.tlear.trise.functions.decision;

import com.badlogic.gdx.math.Vector2;
import com.tlear.trise.environment.Environment;
import com.tlear.trise.functions.skeletonisation.ProbabilisticRoadMap;
import com.tlear.trise.graph.Node;
import com.tlear.trise.graph.TrackedGraph;
import com.tlear.trise.graph.TrackedUndirectedGraph;
import com.tlear.trise.interactions.Action;
import com.tlear.trise.interactions.MoveToAction;
import com.tlear.trise.utils.Tuple;

public class DecideByRandomPRM implements DecisionFunction {

	private ProbabilisticRoadMap probabilisticRoadMap;
	private TrackedGraph<Vector2> prm;
	private boolean initialised;
	
	public DecideByRandomPRM() {
		probabilisticRoadMap = new ProbabilisticRoadMap(500, 10);
		prm = new TrackedUndirectedGraph<Vector2>();
		initialised = false;
	}
	 
	@Override 
	public Tuple<Action, TrackedGraph<Vector2>> apply(Environment t){
		
		if (!initialised) {
			
			
			prm = probabilisticRoadMap.skeletonise(t);
			initialised = true;
		}
		
		Vector2 p = t.agents.getFirst().pos.cpy();
	
		Node<Vector2> node = prm.findNode(t.agents.getFirst().pos).getNeighbours().get((int) (Math.random() * prm.findNode(t.agents.getFirst().pos).getNeighbours().size()));
		Vector2 q = new Vector2(node.getValue().cpy());
		prm.visit(node);
		
		return new Tuple<>(new MoveToAction(p, q), prm);
	}
	
}
