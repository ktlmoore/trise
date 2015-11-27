package com.tlear.trise.graph;

import java.util.Set;

public interface TrackedGraph<T> extends Graph<T> {
	
	public Set<Node<T>> getExploredNodes();
	public void visit(Node<T> n);
	public boolean visited(Node<T> n);
}
 