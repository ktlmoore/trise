package com.tlear.trise.functions;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.badlogic.gdx.math.Vector2;
import com.tlear.trise.environment.Environment;
import com.tlear.trise.graph.Node;
import com.tlear.trise.objects.StaticGoal;

public class GoalFunctionTest {
	
	private Environment env;
	private Node<Vector2> node;
	private StaticGoal goal;
	private GoalFunction toTest;

	@Before
	public void setUp() throws Exception {
		env = new Environment();
		node = new Node<>(new Vector2(10, 10));
		goal = new StaticGoal(10, 10, 0, 0);
		env.placeGoal(goal);
		
		toTest = new GoalFunction();
	}

	@Test
	public void test() {
		assertTrue(toTest.apply(env, node));
	}

}
