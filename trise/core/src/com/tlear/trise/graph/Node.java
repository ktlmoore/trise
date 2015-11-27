package com.tlear.trise.graph;

import java.util.LinkedList;
import java.util.List;

public class Node<T> {

	@Override
	public String toString() {
		return "Node [value=" + value + ", numberOfNeighbours: " + neighbours.size() + "]";
	}

	private List<Node<T>> neighbours;
	private T value;
	
	public Node(T value) {
		this.value = value;
		neighbours = new LinkedList<Node<T>>();
	}
	
	@Override
	public boolean equals(Object that) {
		if (that == null) {
			return false;
		}
		if (!(that instanceof Node<?>)) {
			return false;
		}
		Node<?> thatNode = (Node<?>) that;
		if (!thatNode.value.equals(value)) {
			return false;
		}
		if (!thatNode.neighbours.equals(neighbours)) {
			return false;
		}
		
		return true;
	}
	
	public Node(Node<T> that) {
		this.value = that.value;
		this.neighbours = new LinkedList<Node<T>>(that.neighbours);
	}
	
	public int addNeighbour(Node<T> n) {
		neighbours.add(n);
		return neighbours.size();
	}
	
	public boolean deleteNeighbour(Node<T> n) {
		return neighbours.remove(n);
	}
	
	public List<Node<T>> getNeighbours() {
		return new LinkedList<>(neighbours);
	}
	
	public int numberOfNeighbours() {
		return neighbours.size();
	}
	
	public T getValue() {
		return value;
	}
	
	public void setValue(T val) {
		this.value = val;
	}
}
