package com.tlear.trise.utils;

public class Triple<T, U, V> {
	public T fst;
	public U snd;
	public V thd;
	
	public Triple(T first, U second, V third) {
		fst = first;
		snd = second;
		thd = third;
	}

	@Override
	public String toString() {
		return "Triple [fst=" + fst + ", snd=" + snd + ", thd=" + thd + "]";
	}
	
}
