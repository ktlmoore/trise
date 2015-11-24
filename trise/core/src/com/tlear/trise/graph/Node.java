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
