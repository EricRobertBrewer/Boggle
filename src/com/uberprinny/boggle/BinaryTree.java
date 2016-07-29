package com.uberprinny.boggle;

public class BinaryTree<T extends Comparable<T>> implements Tree<T> {

	private class Node {
		Node(T value) {
			this.value = value;
		}
		T value;
		Node left;
		Node right;
	}
	
	private Node mRoot;
	
	public BinaryTree() {
	}
	
	@Override
	public int size() {
		return size(mRoot);
	}
	
	private int size(Node node) {
		if (node == null) {
			return 0;
		}
		return size(node.left) + 1 + size(node.right);
	}

	@Override
	public int depth() {
		return depth(mRoot);
	}
	
	private int depth(Node node) {
		if (node == null) {
			return 0;
		}
		return Math.max(depth(node.left), depth(node.right)) + 1;
	}

	@Override
	public T getFirst() {
		if (mRoot == null) {
			return null;
		}
		return getFirstNode(mRoot).value;
	}
	
	private Node getFirstNode(Node node) {
		if (node.left == null) {
			return node;
		}
		return getFirstNode(node.left);
	}

	@Override
	public T getLast() {
		if (mRoot == null) {
			return null;
		}
		return getLastNode(mRoot).value;
	}
	
	private Node getLastNode(Node node) {
		if (node.right == null) {
			return node;
		}
		return getLastNode(node.right);
	}

	@Override
	public boolean find(T value) {
		return find(value, mRoot);
	}
	
	private boolean find(T value, Node node) {
		if (node == null) {
			return false;
		}
		if (node.value.compareTo(value) < 0) {
			return find(value, node.left);
		}
		if (node.value.compareTo(value) > 0) {
			return find(value, node.right);
		}
		return true;
	}

	@Override
	public boolean insert(T value) {
		if (mRoot == null) {
			mRoot = new Node(value);
			return true;
		}
		return insert(value, mRoot);
	}
	
	private boolean insert(T value, Node node) {
		if (node.value.compareTo(value) < 0) {
			if (node.left == null) {
				node.left = new Node(value);
				return true;
			} else {
				return insert(value, node.left);
			}
		}
		if (node.value.compareTo(value) > 0) {
			if (node.right == null) {
				node.right = new Node(value);
				return true;
			} else {
				return insert(value, node.right);
			}
		}
		return false;
	}

	@Override
	public boolean delete(T value) {
		if (mRoot == null) {
			return false;
		}
		if (mRoot.value.compareTo(value) == 0) { // root to be deleted
			if (depth(mRoot.left) < depth(mRoot.right)) { // right side is legitimately deeper than left
				getFirstNode(mRoot.right).left = mRoot.left;
				mRoot = mRoot.right;
			} else if (mRoot.left != null) { // left side is legitimately deeper than right
				getLastNode(mRoot.left).right = mRoot.right;
				mRoot = mRoot.left;
			} else { // both are depth 0, ie. null; no work to do
				mRoot = null;
			}
			return true;
		}
		return delete(value, mRoot);
	}
	
	private boolean delete(T value, Node node) {
		if (node.value.compareTo(value) < 0) {
			if (node.left == null) {
				return false;
			}
			if (node.left.value.compareTo(value) == 0) {
				if (depth(node.left.left) < depth(node.left.right)) { // left.right is ACTUALLY deeper
					getFirstNode(node.left.right).left = node.left.left;
					node.left = node.left.right;
				} else if (node.left.left != null) { // left.left is ACTUALLY deeper
					getLastNode(node.left.left).right = node.left.right;
					node.left = node.left.left;
				} else { // they're both depth 0 (null)
					node.left = null;
				}
				return true;
			}
			return delete(value, node.left);
		}
		if (node.value.compareTo(value) > 0) {
			if (node.right == null) {
				return false;
			}
			if (node.right.value.compareTo(value) == 0) {
				if (depth(node.right.left) < depth(node.right.right)) { // right.right is ACTUALLY deeper, ie. not null
					getFirstNode(node.right.right).left = node.right.left;
					node.right = node.right.right;
				} else if (node.right.left != null) { // right.left is ACTUALLY deeper
					getLastNode(node.right.left).right = node.right.right;
					node.right = node.right.left;
				} else { // they're both depth 0 (null)
					node.right = null;
				}
				return true;
			}
			return delete(value, node.right);
		}
		return false; // should never reach this
	}
}
