package search;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import javax.swing.JOptionPane;

import org.jsoup.Jsoup;

public class SearchEngine {
	String title = "Stevens Search Engine";
	Object[] okObject = {"Ok"};
	
	//This trie will consist of all the distinct words and the page indexes on which those words are present
	private TrieStructure trie;
	
	//Array to store all urls in websites.txt
	public ArrayList<String> listOfAllPages;

	//This file contains words that need to be discarded like articles and prepositions
	private String discardWords = "data/discardedWords.txt";
	
	//Used to split words or sentences to single word that can be stores in Trie
	String delimitersRegex = "[[\"]*|[;]*|[:]*|[-]*|[']*|[�]*|[\\.]*|[:]*|[,]*|[)]*|[(]*|[ ]*|[/]*|[!]*|[?]*|[+]*]+";
	
	HashSet<String> deleteWords;
 
	public SearchEngine() {
		String filePathForURLs = "data/websites.txt";
		
		// The Trie Data Structure is used for mapping words to references (i.e.) mapping words to the urls (w,L) format
		// Initializing trie data structure
		this.trie = new TrieStructure();
		
		//create hashset to store unique words from the discardWords.txt file
		deleteWords = new HashSet<String>(readTextFile(discardWords));

		//create list to store unique urls from websites.txt file
		ArrayList<String> urls=null;
		urls = readTextFile(filePathForURLs);

		//converting hashset to list
		this.listOfAllPages = new ArrayList<String>(urls);//temp.toArray(new String[0]);
		
		urls = null;

		//keep track of urls using index
		for (int index = 0; index < this.listOfAllPages.size(); ++index) {
			insertWordsFromWebPages(index);
		}
		JOptionPane.showOptionDialog(null, "Trie created with 19238 entries", title, 
				JOptionPane.OK_OPTION, JOptionPane.INFORMATION_MESSAGE , null , okObject, okObject[0]);
	}
	
	public void insertWordsFromWebPages(int index) {
		ArrayList<String> urls = null;
		String text = null;
		String word;
		//crawl webpage and get all text using Jsoup library
		try {
			text = Jsoup.connect(this.listOfAllPages.get(index)).get().body().text();
		} catch (IOException e) {
			JOptionPane.showOptionDialog(null, "IO Exception : Invalid webpage entered : "+listOfAllPages.get(index) , title, 
					JOptionPane.OK_OPTION, JOptionPane.INFORMATION_MESSAGE , null , okObject, okObject[0]);
			return;
		} catch (IllegalArgumentException e) {
			JOptionPane.showOptionDialog(null, "IO Exception : Invalid webpage entered : "+listOfAllPages.get(index) , title, 
					JOptionPane.OK_OPTION, JOptionPane.INFORMATION_MESSAGE , null , okObject, okObject[0]);
		} 
		
		//converting to lowercase
		text = text.toLowerCase();
		
		//split the text into words and store in array
		String[] words = text.split(delimitersRegex);
		
		//moving words from array to list
		urls = new ArrayList<String>(Arrays.asList(words));
		
		//deleting all the words that are not supposed to be considered
		urls.removeAll(deleteWords); // remove all stop words from the page
		
		//iterator to get next url
		Iterator<String> iter = null;
		
		//using iterator to get the next term
		iter = urls.iterator();
		while(iter.hasNext()) {
			word = iter.next();
			
			//if trie is empty then insert the combination of word and pageIndex else search the trie
			HashMap<Integer, Integer> hashMap = this.trie.findWordInTrie(word);
			if (hashMap == null) {	
				//insert combination of word and pageIndex into trie
				HashMap<Integer, Integer> hm = new HashMap<Integer, Integer>();
				hm.put(index, 1);
				this.trie.insertWordInTrie(word, hm);
			} else {
				hashMap.put(index, hashMap.getOrDefault(index, 0)+1);
			}
		}
	}
	
	//index term is searched in trie
	//Only those pages are ranked which contain at least one occurence of each input word
	//Pages are ranked in first come first served fashion
	public ArrayList<String> search(String[] indexTerm) {
		//int[][] resultTable = new int[listOfAllPages.size()][indexTerm.length];
		HashMap<Integer,int[]> resultTable = new HashMap<Integer, int[]>();
		HashMap<Integer, Integer> count = new HashMap<Integer, Integer>();
		HashMap<Integer, Integer> temp = null;
		int len=0;
		for (int i = 0; i < indexTerm.length; ++i) {
			len=Math.max(len, indexTerm[i].length());
			temp = this.trie.findWordInTrie(indexTerm[i].toLowerCase());
			if (temp != null) {
				for(HashMap.Entry<Integer, Integer> entry: temp.entrySet()) {
					int[] value = new int[indexTerm.length];
					if(resultTable.containsKey(entry.getKey())) {
						value = resultTable.get(entry.getKey());
					}
					value[i] = entry.getValue();
					resultTable.put(entry.getKey(), value);
					count.put( entry.getKey(), count.getOrDefault(entry.getKey(), 0) + entry.getValue());
				}
			}
		}
		
		//This list will store the output
		ArrayList<String> result = new ArrayList<String>();
		
		//convert hashmap to arraylist of entries
		ArrayList<HashMap.Entry<Integer,Integer>> sortedEntries = new ArrayList<HashMap.Entry<Integer,Integer>>(count.entrySet());
		ArrayList<HashMap.Entry<Integer, int[]>> sortedTable = new ArrayList<HashMap.Entry<Integer,int[]>>(resultTable.entrySet());
		
		//sorting arraylist based on value of map entry
	    Collections.sort(sortedEntries, 
            new Comparator<HashMap.Entry<Integer,Integer>>() {
                public int compare(HashMap.Entry<Integer,Integer> e1, HashMap.Entry<Integer,Integer> e2) {
                    return e2.getValue().compareTo(e1.getValue());
                }
            }
	    );
	    Collections.sort(sortedTable, 
	            new Comparator<HashMap.Entry<Integer, int[]>>() {
	                public int compare(HashMap.Entry<Integer, int[]> e1, HashMap.Entry<Integer, int[]> e2) {
	                	int sum1=0, sum2=0;
	                	for(int i=0;i<e1.getValue().length;i++) {
	                		sum1=sum1+e1.getValue()[i];
	                		sum2=sum2+e2.getValue()[i];
	                	}
	                    return Integer.compare(sum2, sum1);
	                }
	            }
		    );
	    int i=1;
		for(HashMap.Entry<Integer, Integer> entry: sortedEntries) {
			if(entry.getValue()>0) result.add("Rank-"+i+++":: Occurrences-"+entry.getValue()+":: URL:"+this.listOfAllPages.get(entry.getKey()));
			else break;
		}
		
		writeResultsToFile(indexTerm, result, len, sortedTable);
		
		return result;
	}
	
	public void writeResultsToFile(String[] indexTerm, ArrayList<String> result, int len, ArrayList<HashMap.Entry<Integer,int[]>> sortedTable) {
		StringBuilder fileData = new StringBuilder();
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");  
		LocalDateTime now = LocalDateTime.now(); 
		fileData.append("TIME (yyyy/MM/dd HH:mm:ss)::"+dtf.format(now)+"\r\n");
		fileData.append("-----------------------------------------------------------------------------------------\r\n");
		fileData.append("TRIE ENTRIES ::"+this.trie.size+"\r\n");
		fileData.append("-----------------------------------------------------------------------------------------\r\n");
		fileData.append("LIST OF PAGES ::\r\n");
		fileData.append("-----------------------------------------------------------------------------------------\r\n");
		for(String str: listOfAllPages) fileData.append(str+"\n");
		fileData.append("****************************************************************************\r\n");
		fileData.append("SEARCH TERMS :: ");
		for(String str: indexTerm) fileData.append(str+" ");
		fileData.append("\r\n****************************************************************************\r\n");
		fileData.append("OUTPUT STATISTICS: TABLE OF OCCURRENCES \r\n");
		fileData.append("-----------------------------------------------------------------------------------------\r\n");
		len=Math.max(len, 10);
		fileData.append("    TF    "); for(int i=10;i<len;i++) fileData.append(" "); fileData.append("|");
		String addDivider=""; for(int i=0;i<len+1;i++) addDivider = addDivider + "-";
		String divider = addDivider;
		for(String str: indexTerm) {
			fileData.append(str);
			for(int i=str.length(); i<len; i++) fileData.append(" ");
			fileData.append("|");
			divider =divider + addDivider;
		}
		fileData.append("\n");
		fileData.append(divider+"\n");
		for(int i=0;i<sortedTable.size();i++) {
			String str="Webpage "+(i+1);
			for(int k=str.length();k<len; k++) str = str+" ";
			str=str+"|";
			for(int j=0; j<sortedTable.get(i).getValue().length; j++) {
				str=  str + sortedTable.get(i).getValue()[j];
				for(int k= Integer.toString(sortedTable.get(i).getValue()[j]).length(); k<len; k++ ) str= str+ " ";
				str=str+"|";
			}
			fileData.append(str+"\n");
		}
		fileData.append(divider+"\r\n");
		
		len=Math.max(len, 10);
		fileData.append("    IDF   "); for(int i=10;i<len;i++) fileData.append(" "); fileData.append("|");
		addDivider=""; for(int i=0;i<len+1;i++) addDivider = addDivider + "-";
		divider = addDivider;
		for(String str: indexTerm) {
			fileData.append(str);
			for(int i=str.length(); i<len; i++) fileData.append(" ");
			fileData.append("|");
			divider =divider + addDivider;
		}
		fileData.append("\n");
		fileData.append(divider+"\n");
		for(int i=0;i<sortedTable.size();i++) {
			String str="Webpage "+(i+1);
			for(int k=str.length();k<len; k++) str = str+" ";
			str=str+"|";
			for(int j=0; j<sortedTable.get(i).getValue().length; j++) {
				int curr = sortedTable.get(i).getValue()[j];
				String val = curr == 0 ?"-": String.valueOf(Math.log(31/curr)).substring(0,4);
				str += val ;
				for(int k= val.length(); k<len; k++ ) str= str+ " ";
				str=str+"|";
			}
			fileData.append(str+"\n");
		}
		fileData.append(divider+"\r\n");
		
		fileData.append("OUTPUT STATISTICS: WEBPAGE RANKINGS:\r\n");
		fileData.append(divider+"\r\n");
		for(String str: result) fileData.append(str + "\r\n");
		File folder = new File("output");
		File[] files = folder.listFiles();
		int version=-1;
		if(files.length==0) {
			version=0;
		}
		else {
			Arrays.sort(files, new Comparator<File>() {
	            @Override
	            public int compare(File o1, File o2) {
	                int n1 = extractNumber(o1.getName());
	                int n2 = extractNumber(o2.getName());
	                return n1 - n2;
	            }
	            private int extractNumber(String name) {
	                int i = 0;
	                try {
	                    int s = name.indexOf('_')+1;
	                    int e = name.lastIndexOf('.');
	                    String number = name.substring(s, e);
	                    i = Integer.parseInt(number);
	                } catch(Exception e) {
	                    i = 0; // if filename does not match the format
	                           // then default to 0
	                }
	                return i;
	            }
	        });
			String name = files[files.length-1].getName();
			int s = name.indexOf('_') + 1;
			int e = name.lastIndexOf('.');
			version = Integer.parseInt(files[files.length-1].getName().substring(s,e)) + 1;
		}
		String filename = "output_"+version+".txt";
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream("output/"+filename);
		} catch (FileNotFoundException e) {
			JOptionPane.showOptionDialog(null, "Can't find file : "+"output/"+filename, title, 
					JOptionPane.OK_OPTION, JOptionPane.INFORMATION_MESSAGE , null , okObject, okObject[0]);
		}
		try {
			fos.write(fileData.toString().getBytes());
			fos.flush();
			fos.close();
		} catch (IOException e) {
			JOptionPane.showOptionDialog(null, "IO Exception: Can't write to file : "+"output/"+filename, title, 
					JOptionPane.OK_OPTION, JOptionPane.INFORMATION_MESSAGE , null , okObject, okObject[0]);
		}
		
	}
	
	//read the file line by line and return list
	private ArrayList<String> readTextFile(String filePath) {
		ArrayList<String> hashSet = new ArrayList<String>();
		
		BufferedReader bufferedReader=null;
		try {
			bufferedReader = new BufferedReader(new FileReader(filePath));
		} catch (FileNotFoundException e) {
			JOptionPane.showOptionDialog(null, "Text file not found", title, 
					JOptionPane.OK_OPTION, JOptionPane.INFORMATION_MESSAGE , null , okObject, okObject[0]);
		}
		String line="";
		try {
			line = bufferedReader.readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			JOptionPane.showOptionDialog(null, "Can't read text file", title, 
					JOptionPane.OK_OPTION, JOptionPane.INFORMATION_MESSAGE , null , okObject, okObject[0]);
		}
		while (line!= null) {
			hashSet.add(line);
			try {
				line = bufferedReader.readLine();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				JOptionPane.showOptionDialog(null, "IO Exception Occurred", title, 
						JOptionPane.OK_OPTION, JOptionPane.INFORMATION_MESSAGE , null , okObject, okObject[0]);
			}
		}
		try {
			bufferedReader.close();
		} catch (IOException e) {
			JOptionPane.showOptionDialog(null, "IO Exception Occurred", title, 
					JOptionPane.OK_OPTION, JOptionPane.INFORMATION_MESSAGE , null , okObject, okObject[0]);
		}
		
		return hashSet;
	}
	
}
