package com.tlear.trise.graph;

import java.util.List;

public interface Graph<T> {

	public List<Node<T>> getNodes();
	public List<Edge<T>> getEdges();
	public boolean addNode(T value);
	public boolean addEdge(Node<T> u, Node<T> v);
	public boolean deleteNode(T value);
	public boolean deleteEdge(Node<T> u, Node<T> v);
	public Node<T> findNode(T value);
	public Edge<T> findEdge(T uValue, T vValue);
	
}
 