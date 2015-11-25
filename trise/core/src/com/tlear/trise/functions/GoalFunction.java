package com.tlear.trise.functions;

import java.util.function.BiFunction;
import java.util.function.Function;

import com.badlogic.gdx.math.Vector2;
import com.tlear.trise.environment.Environment;
import com.tlear.trise.graph.Node;
import com.tlear.trise.objects.StaticGoal;

public interface GoalFunction extends BiFunction<Environment, Node<Vector2>, Boolean> {

	

}
