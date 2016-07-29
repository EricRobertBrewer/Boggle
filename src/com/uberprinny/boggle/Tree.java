package com.uberprinny.boggle;

public interface Tree<T extends Comparable<T>> {
	int size();
	int depth();
	T getFirst();
	T getLast();
	boolean find(T value);
	boolean insert(T value);
	boolean delete(T value);
}
