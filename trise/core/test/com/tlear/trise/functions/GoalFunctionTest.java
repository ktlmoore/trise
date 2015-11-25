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
	private GoalFunction booleanTest, multipleTest;

	@Before
	public void setUp() throws Exception {
		env = new Environment();
		node = new Node<>(new Vector2(10, 10));
		goal = new StaticGoal(10, 10, 0, 0);
		env.placeGoal(goal);
		
		booleanTest = new BooleanIsGoalFunction();
		multipleTest = new MultipleGoalsGoalFunction();
	}

	@Test
	public void testBoolean() {
		assertTrue(booleanTest.apply(env, node));
		assertTrue(env.goals.contains(goal));
	}
	
	@Test
	public void testMultiple() {
		assertTrue(multipleTest.apply(env, node));
		assertFalse(env.goals.contains(goal));
	}

}
