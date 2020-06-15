package search;

import java.util.HashMap;

public class TrieNode {
	char key;
	HashMap<Integer, Integer> value;
	HashMap<Character, TrieNode> children;
	
	public TrieNode() {
		this.children = new HashMap<Character, TrieNode>();		
	}
	public TrieNode(char key) {
		this.key = key;
		this.children = new HashMap<Character, TrieNode>();
	}
}
