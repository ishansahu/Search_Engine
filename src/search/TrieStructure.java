package search;

import java.util.HashMap;
public class TrieStructure {
	
	private TrieNode root;
	public int size;
	
	public TrieStructure() {
		this.root = new TrieNode();
		this.size = 0;
	}
	
	//the 2 parameters are the word and the page index containing the word
	//check if char is present in trie, if not then insert else check next char
	//the leaf char will contain the page index or URL
	public void insertWordInTrie(String word, HashMap<Integer, Integer> pageIndex) {
		HashMap<Character, TrieNode> children = this.root.children;
		TrieNode trieNode = null;
		int i=-1;
		while(++i<word.length()) {
			char character = word.charAt(i);
			
			if (children.containsKey(character)) trieNode = children.get(character);
			else {
				trieNode = new TrieNode(character);
				children.put(character,trieNode);
			}
			
			//END OF WORD
			if (i==word.length()-1) trieNode.value = pageIndex;
			children = trieNode.children;
		}
		this.size += 1;
	}
	
	//if word is present in trie then return the url which contains it else return null
	public HashMap<Integer, Integer> findWordInTrie(String word) {
		HashMap<Character, TrieNode> children = this.root.children;
		TrieNode trieNode = null;
		HashMap<Integer, Integer> result = null;
		
		int i=-1;
		while(++i<word.length()) {
			char character = word.charAt(i);
			
			if (children.containsKey(character)) trieNode = children.get(character);
			else return null;
			
			//LAST LETTER OF WORD
			if (i == word.length()-1) result = trieNode.value;
			children = trieNode.children;
		}
		return result; //return url else null
	}
}