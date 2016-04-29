package com.tlear.trise.functions.skeletonisation;

import com.badlogic.gdx.math.Vector2;
import com.tlear.trise.environment.Environment;
import com.tlear.trise.graph.TrackedGraph;

public interface S13n {

	/*
	 * Takes an environment and constructs a skeletonisation based on it
	 */
	public TrackedGraph<Vector2> skeletonise(Environment env);

	public String getName();

}
