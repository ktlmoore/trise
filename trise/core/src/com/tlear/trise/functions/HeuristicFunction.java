package com.tlear.trise.functions;

import java.util.function.Function;

import com.badlogic.gdx.math.Vector2;
import com.tlear.trise.graph.Node;

public interface HeuristicFunction extends Function<Node<Vector2>, Float> {
	
}
