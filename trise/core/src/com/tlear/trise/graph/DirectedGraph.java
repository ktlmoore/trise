package com.tlear.trise.graph;

public class DirectedGraph<T> extends UndirectedGraph<T> {

	public DirectedGraph(T source) {
		super(source);
	}
	
	@Override
	public boolean addEdge(Node<T> u, Node<T> v) {
		if (findEdge(u.getValue(), v.getValue()) == null) {
			u.addNeighbour(v);	
			
			edges.add(new Edge<>(u, v));
			
			
			return true;
		}
		return false;
	}
	
	@Override
	public boolean deleteEdge(Node<T> u, Node<T> v) {
		Edge<T> toDelete = findEdge(u.getValue(), v.getValue());
		
		if (toDelete != null) {
			
			u.deleteNeighbour(v);
			
			return edges.remove(toDelete);
			
		}
		return false;
	}
	
	@Override
	public Edge<T> findEdge(T uValue, T vValue) {
		Edge<T> result = null;
		for (Edge<T> e : edges) {
			if (e.fst.getValue() == uValue && e.snd.getValue() == vValue) {
				result = e;
				break;
			}
		}
		return result;
	}

	
}
