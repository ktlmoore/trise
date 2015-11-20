package com.tlear.trise.graph;

import java.util.LinkedList;
import java.util.List;

public class UndirectedGraph<T> implements Graph<T> {

	protected List<Node<T>> nodes;
	protected List<Edge<T>> edges;
	
	public UndirectedGraph(T source) {
		nodes = new LinkedList<Node<T>>();
		edges = new LinkedList<Edge<T>>();
		
		nodes.add(new Node<T>(source));
	}

	@Override
	public List<Node<T>> getNodes() {
		return new LinkedList<>(nodes);
	}

	@Override
	public List<Edge<T>> getEdges() {
		return new LinkedList<>(edges);
	}

	@Override
	public boolean addNode(T value) {
		if (findNode(value) == null) {
			nodes.add(new Node<T>(value));
			return true;
		}
		return false;
	}

	@Override
	public boolean addEdge(Node<T> u, Node<T> v) {
		if (findEdge(u.getValue(), v.getValue()) == null) {
			edges.add(new Edge<>(u, v));
			edges.add(new Edge<>(v, u));
			
			u.addNeighbour(v);
			v.addNeighbour(u);
			return true;
		}
		return false;
	}

	@Override
	public boolean deleteNode(T value) {
		Node<T> toDelete = findNode(value);
		if (toDelete != null) {
			for (Node<T> n : toDelete.getNeighbours()) {
				deleteEdge(n, toDelete);
				deleteEdge(toDelete, n);
			}
			return nodes.remove(toDelete);
		}
		return false;
	}

	@Override
	public boolean deleteEdge(Node<T> u, Node<T> v) {
		Edge<T> toDelete = findEdge(u.getValue(), v.getValue());
		Edge<T> toDeleteBack = findEdge(v.getValue(), u.getValue());
		
		if (toDelete != null && toDeleteBack != null) {
			
			v.deleteNeighbour(u);
			u.deleteNeighbour(v);
			
			return edges.remove(toDelete);
			
		}
		if (toDelete != null || toDeleteBack != null) {
			throw new RuntimeException("Undirected Graph does not have both a forward and back edge between two linked nodes: " + u + " and " + v);
		}
		return false;
	}

	@Override
	public Node<T> findNode(T value) {
		Node<T> result = null;
		for (Node<T> n : nodes) {
			if (n.getValue() == value) {
				result = n;
				break;
			}
		}
		return result;
	}

	@Override
	public Edge<T> findEdge(T uValue, T vValue) {
		Edge<T> result = null;
		for (Edge<T> e : edges) {
			if (e.fst.getValue() == uValue && e.snd.getValue() == vValue) {
				result = e;
				break;
			}
			if (e.snd.getValue() == uValue && e.fst.getValue() == vValue) {
				result = e;
				break;
			}
		}
		return result;
	}

}
