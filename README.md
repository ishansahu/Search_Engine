
Initial Setup
1. Add all the stop words in data/discardedWords.txt
2. Add all the URLs on which web scarpping will be done in data/Websites.txt

Tries Generation
1. After inital setup execute the file Runner.java.
2. This will generate the tries will data present in all the websites mentioned in Websites.txt.
3. User can also add any additional url.
4. Content of these additional URL will be added to the exisiting trie.
5. User can search a word or set of words on this trie.
6. Webpages containing these word will be dispalyed based on ranking.

Ranking Logic
1. website which has the maximum number of occurence of input word will be ranked highest will rank 1.
2. Similarly, subsequent ranking will be done.
3. This can be achieved by counting the occurence of words in each web page and sort them based on the number of occurence

Output
1. A output file will be generated for each search operation performed at output/ folder.
2. Output file tells the ranking and occurence of website on which search keys are found.


Technology Used
1. Java 
2. Jsoup for scraping web pages
3. Java Swing

Algorithm suffixTrieMatch(T , P ):
Input: Compact suffix trie T for a text X and pattern P
Output: Starting index of a substring of X matching P or an indication that P is not a substring of X
	p ← P.length() // length of suffix of the pattern to be matched j ← 0 // start of suffix of the pattern to be matched
	v ← T.root()
	repeat
		f ← true // flag indicating that no child was successfully processed 
		for each child w of v do
			i ← start(w)
			if P[j] = T[i] then
				// process child w
				x ← end(w) − i + 1 
				ifp≤x then
				// suffix is shorter than or of the same length of the node label
					if P[j..j+p−1]=X[i..i+p−1] then 
						return i − j // match
					else
						return “P is not a substring of X”
				else
					if P[j..j + x − 1] = X[i..i + x − 1] then 
						p←p−x// updatesuffixlength
						j ← j + x // update suffix start index 
						v←w
						f ← false
						break out of the for loop 
		until f or T.isExternal(v)
		return “P is not a substring of X”
