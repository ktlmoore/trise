package com.tlear.trise.utils;

public class Tuple<T, U> {
	public T fst;
	public U snd;
	
	/**
	 * Creates a Tuple of two different types
	 * @param first
	 * @param second
	 */
	public Tuple(T first, U second) {
		fst = first;
		snd = second;
	}

	@Override
	public String toString() {
		return "Tuple [" + fst + ", " + snd + "]";
	}
}
