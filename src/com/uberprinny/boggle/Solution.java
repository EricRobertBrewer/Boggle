package com.uberprinny.boggle;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Solution {

	private static final int SIZE = 5;
	private static final int FACES = 6;
	
	private static final int MAX_WORD_LENGTH = 9; // optimize to throw out checking for strings past this length
	
	private static final int NEW_DICE = 0;
	private static final int OLD_DICE = 1;
	private static final int DICE_VERSION = NEW_DICE; // change to old if you want
	private static final String[][][] DICE = {
		{ // New edition of Boggle
			{ "A", "A", "E", "E", "G", "N" },
			{ "E", "L", "R", "T", "T", "Y" },
			{ "A", "O", "O", "T", "T", "W" },
			{ "A", "B", "B", "J", "O", "O" },
			{ "E", "H", "R", "T", "V", "W" },
			{ "C", "I", "M", "O", "T", "U" },
			{ "D", "I", "S", "T", "T", "Y" },
			{ "E", "I", "O", "S", "S", "T" },
			{ "D", "E", "L", "R", "V", "Y" },
			{ "A", "C", "H", "O", "P", "S" },
			{ "H", "I", "M", "N", "Qu", "U" },
			{ "E", "E", "I", "N", "S", "U" },
			{ "E", "E", "G", "H", "N", "W" },
			{ "A", "F", "F", "K", "P", "S" },
			{ "H", "L", "N", "N", "R", "Z" },
			{ "D", "E", "I", "L", "R", "X" }
		},
		{ // Old edition of Boggle
			{ "A", "A", "C", "I", "O", "T" },
			{ "A", "H", "M", "O", "R", "S" },
			{ "E", "G", "K", "L", "U", "Y" },
			{ "A", "B", "I", "L", "T", "Y" },
			{ "A", "C", "D", "E", "M", "P" },
			{ "E", "G", "I", "N", "T", "V" },
			{ "G", "I", "L", "R", "U", "W" },
			{ "E", "L", "P", "S", "T", "U" },
			{ "D", "E", "N", "O", "S", "W" },
			{ "A", "C", "E", "L", "R", "S" },
			{ "A", "B", "J", "M", "O", "Qu" },
			{ "E", "E", "F", "H", "I", "Y" },
			{ "E", "H", "I", "N", "P", "S" },
			{ "D", "K", "N", "O", "T", "U" },
			{ "A", "D", "E", "N", "V", "Z" },
			{ "B", "I", "F", "O", "R", "X" }
		}
	};
	
	public static void main(String[] args) throws FileNotFoundException {
		Scanner fin = new Scanner(new FileInputStream(new File("web2"))); // file is ordered
		// use different data structures here for better performance!
		//Tree<String> dictionary = new BinaryTree<>();
		List<String> dictionary = new ArrayList<String>();
		int wordCount = 0;
		int dictionaryCompleteness = 1; // change this to change size of dictionary
		// dictionary size will be [approximately] ACTUAL_SIZE_OF_DICTIONARY * 1.0 / completeness 
		boolean useCompletenessRandomly = true; // 
		Random random = new Random();
		while (fin.hasNext()) {
			String word = fin.nextLine();
			if ((useCompletenessRandomly && random.nextInt(dictionaryCompleteness) == 0) ||
					(!useCompletenessRandomly && wordCount % dictionaryCompleteness == 0)) {
//				dictionary.insert(word);
				dictionary.add(word);
			}
			wordCount++;
		}
		fin.close();
		System.out.println("Dictionary completeness: " + dictionaryCompleteness + " Size: " + dictionary.size());
		
		// create dice and shuffle
		int[] dice = new int[SIZE * SIZE];
		for (int i = 0; i < SIZE * SIZE; i++) {
			dice[i] = i;
		}
		for (int i = 0; i < 1000; i++) {
			int a = random.nextInt(SIZE * SIZE);
			int b = random.nextInt(SIZE * SIZE);
			int temp = dice[a];
			dice[a] = dice[b];
			dice[b] = temp;
		}
		
		// create and fill board with shuffled dice and random face
		String[][] board = new String[SIZE][];
		for (int i = 0; i < board.length; i++) {
			board[i] = new String[SIZE];
			for (int j = 0; j < board[i].length; j++) {
				board[i][j] = DICE[DICE_VERSION][dice[4 * i + j] % DICE[DICE_VERSION].length][random.nextInt(FACES)];
			}
		}
		System.out.println();
		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board[i].length; j++) {
				System.out.print(board[i][j] + (j == board[i].length-1 ? "" : " "));
			}
			System.out.println();
		}
		
		// find words!
		long startTime = System.currentTimeMillis();
		System.out.println();
		System.out.print("Finding words");
		List<String> allWords = getAllWords(board, dictionary);
		System.out.println(" Done!");
		System.out.println("Time to find all words up to " + MAX_WORD_LENGTH + " letters long: " + (System.currentTimeMillis() - startTime) + "ms");
		System.out.println("Total unique words in board: " + allWords.size());
		for (String word : allWords) {
			System.out.println(word);
		}
	}
	
	private static List<String> getAllWords(String[][] board, List<String> dictionary) {
		List<String> words = new ArrayList<>();
		boolean[][] path = new boolean[board.length][];
		for (int i = 0; i < board.length; i++) {
			path[i] = new boolean[board[i].length];
			for (int j = 0; j < path[i].length; j++) {
				path[i][j] = false;
			}
		}
		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board[i].length; j++) {
				System.out.print(".");
				String word = "";
				findWords(board, i, j, word, words, path, dictionary);
			}
		}
		return words;
	}
	
	private static void findWords(String[][] board, final int i, final int j,
			String word, List<String> words, boolean[][] path, List<String> dictionary) {
		if (word.length() >= MAX_WORD_LENGTH) {
			return;
		}
		if (i < 0 || i >= board.length ||
				j < 0 || j >= board[i].length ||
				path[i][j]) {
			return;
		}
		word += board[i][j].toLowerCase();
		path[i][j] = true;
		if (word.length() > 2 && exists(word, dictionary)) {
			boolean isAdded = false;
			for (int w = 0; w < words.size() && !isAdded; w++) {
				if (words.get(w).compareTo(word) == 0) {
					isAdded = true;
				}
			}
			if (!isAdded) {
				words.add(word);
			}
		}
		for (int ii = i-1; ii <= i+1; ii++) {
			for (int jj = j-1; jj <= j+1; jj++) {
				findWords(board, ii, jj, word, words, path, dictionary);
			}
		}
		path[i][j] = false;
	}
	
	private static boolean exists(String word, List<String> dictionary) {
		return exists(word, dictionary, 0, dictionary.size()-1);
	}
	
	private static boolean exists(String word, List<String> dictionary, int front, int back) {
		if (front > back) {
			return false;
		}
		if (back - front < 5) {
			for (int i = front; i <= back; i++) {
				if (dictionary.get(i).compareTo(word) == 0) {
					return true;
				}
			}
		}
		int middle = (front + back) / 2;
		String middleValue = dictionary.get(middle);
		if (word.compareTo(middleValue) < 0) {
			return exists(word, dictionary, front, middle-1);
		}
		if (word.compareTo(middleValue) > 0) {
			return exists(word, dictionary, middle+1, back);
		}
		return true;
	}
}
