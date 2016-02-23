package com.tlear.trise.functions.decision;

import com.badlogic.gdx.math.Vector2;
import com.tlear.trise.environment.Environment;
import com.tlear.trise.functions.skeletonisation.ProbabilisticRoadMap;
import com.tlear.trise.graph.Node;
import com.tlear.trise.graph.TrackedGraph;
import com.tlear.trise.graph.TrackedUndirectedGraph;
import com.tlear.trise.interactions.Action;
import com.tlear.trise.interactions.MoveToAction;
import com.tlear.trise.metrics.MutableMetrics;
import com.tlear.trise.utils.Tuple;

public class DecideByRandomPRM implements DecisionFunction {

	private ProbabilisticRoadMap probabilisticRoadMap;
	private TrackedGraph<Vector2> prm;
	private boolean initialised;
	
	private MutableMetrics metrics;
	
	public DecideByRandomPRM() {
		probabilisticRoadMap = new ProbabilisticRoadMap(500, 10);
		prm = new TrackedUndirectedGraph<Vector2>();
		initialised = false;
		
		metrics = new MutableMetrics();
	}
	 
	@Override 
	public Tuple<MutableMetrics, Tuple<Action, TrackedGraph<Vector2>>> apply(Environment t){
		
		if (!initialised) {
			metrics.reset();
			long startTime = System.currentTimeMillis();
			prm = probabilisticRoadMap.skeletonise(t);
			long endTime = System.currentTimeMillis();
			
			metrics.setTimeToSkeletonise(endTime - startTime);
			
			initialised = true;
		}
		
		Vector2 p = t.agents.getFirst().pos.cpy();
		
		long startTime = System.currentTimeMillis();
		Node<Vector2> node = prm.findNode(t.agents.getFirst().pos).getNeighbours().get((int) (Math.random() * prm.findNode(t.agents.getFirst().pos).getNeighbours().size()));
		long endTime = System.currentTimeMillis();
		metrics.setTimeToSearch(endTime - startTime);
		metrics.setNodesExplored(metrics.getNodesExplored() + 1);
		
		Vector2 q = new Vector2(node.getValue().cpy());
		prm.visit(node);
		
		return new Tuple<>(metrics, new Tuple<>(new MoveToAction(p, q), prm));
	}
	
	public String getName() {
		return "Random PRM";
	}
	
	public String getS13nName() {
		return "PRM";
	}
	
}
