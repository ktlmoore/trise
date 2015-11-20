package com.tlear.trise.graph;

import com.tlear.trise.utils.Tuple;

public class Edge<T> extends Tuple<Node<T>, Node<T>> {

	public Edge(Node<T> first, Node<T> second) {
		super(first, second);
	}

	@Override
	public String toString() {
		return "Edge [fst=" + fst + ", snd=" + snd + "]";
	}

}
