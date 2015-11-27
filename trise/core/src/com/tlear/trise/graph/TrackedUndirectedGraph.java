package com.tlear.trise.graph;

import java.util.HashSet;
import java.util.Set;

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
