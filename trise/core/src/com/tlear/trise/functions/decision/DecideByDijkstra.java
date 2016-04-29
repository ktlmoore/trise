package com.tlear.trise.functions.decision;

import com.badlogic.gdx.math.Vector2;
import com.tlear.trise.environment.Environment;
import com.tlear.trise.functions.skeletonisation.ProbabilisticRoadMap;
import com.tlear.trise.functions.skeletonisation.S13n;
import com.tlear.trise.graph.TrackedGraph;
import com.tlear.trise.graph.TrackedUndirectedGraph;
import com.tlear.trise.interactions.Action;
import com.tlear.trise.metrics.MutableMetrics;
import com.tlear.trise.utils.Tuple;

public class DecideByDijkstra implements DecisionFunction {

	/**
	 * THIS IS JUST A GENERALISATION OF UNIFORM COST SEARCH FOR OUR PURPOSES
	 */

	private S13n skeletonisation;
	private TrackedGraph<Vector2> prm;

	private MutableMetrics metrics;

	private boolean initialised;

	@Override
	public void reset() {
		initialised = false;
	}

	public DecideByDijkstra() {
		skeletonisation = new ProbabilisticRoadMap(200, 5);
		prm = null;
		initialised = false;

		metrics = new MutableMetrics();
	}

	@Override
	public Tuple<MutableMetrics, Tuple<Action, TrackedGraph<Vector2>>> apply(
			Environment t) {
		if (!initialised) {
			prm = new TrackedUndirectedGraph<>();
			long startTime = System.currentTimeMillis();
			prm = skeletonisation.skeletonise(t);
			long endTime = System.currentTimeMillis();

			metrics.setTimeToSkeletonise(endTime - startTime);

			initialised = true;
		}

		return null;
	}

	@Override
	public String getName() {
		return "Dijkstra";
	}

	@Override
	public String getS13nName() {
		// TODO Auto-generated method stub
		return null;
	}

}
