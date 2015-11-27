package com.tlear.trise.functions.skeletonisation;

import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

import com.badlogic.gdx.math.Vector2;
import com.tlear.trise.environment.Environment;
import com.tlear.trise.graph.Graph;
import com.tlear.trise.objects.Agent;

public class ProbabilisticRoadMapTest {
	
	private ProbabilisticRoadMap toTest;
	private Environment env;

	@Before
	public void setUp() throws Exception {
		env = new Environment();
		Agent a = new Agent(10, 10, 10, 10, env);
		env.placeAgent(a);
	}

	@Test
	public void testFindsMap() {
		toTest = new ProbabilisticRoadMap(10, 3);
		Graph<Vector2> skeletonise = toTest.skeletonise(env);
		assertNotNull(skeletonise);
	}

}
