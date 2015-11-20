package com.tlear.trise.graph;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class TrackedUndirectedGraph<T> extends UndirectedGraph<T> implements
		TrackedGraph<T>, Graph<T> {

	private HashSet<Node<T>> exploredNodes;
	
	public TrackedUndirectedGraph(T source) {
		super(source);
		exploredNodes = new HashSet<>();
		visit(findNode(source));
	}
	
	public TrackedUndirectedGraph() {
		super();
	}
	
	@Override
	public Set<Node<T>> getExploredNodes() {
		return new HashSet<>(exploredNodes);
	}

	@Override
	public void visit(Node<T> n) {
		if (!exploredNodes.contains(n)) {
			exploredNodes.add(n);
			
		}
	}

	@Override
	public boolean visited(Node<T> n) {
		return exploredNodes.contains(n);
	}
	
	

}
