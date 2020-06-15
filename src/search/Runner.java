package search;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.plaf.ColorUIResource;

public class Runner extends JFrame {
	private static final long serialVersionUID = 1L;
	static JButton startButton = new JButton("Start");
	public static void main(String[] args)throws IOException, InterruptedException, ExecutionException { 
		Runner runner = new Runner();
		runner.execute();
	}
	
	public void execute() throws InterruptedException, ExecutionException {
		String title = "Stevens Search Engine";
		Object[] okObject = {"Ok"};
		Object[] alrightObject = {"Alright!"};
		Component frame = null;
		UIManager UI=new UIManager();
		UI.put("OptionPane.background",new ColorUIResource(173,216,230));
		UI.put("Panel.background",new ColorUIResource(173,216,230));
		
		//Search Engine for the list of URL on websites.txt
		//Used Tries to create inverted index
		//I have excluded words contained in the discardedWords.txt
		//If you want to add more webpages then simply add its url to the websites.txt file
		Object[] options = {"Yes","No"};
		int n = JOptionPane.showOptionDialog(frame, "Create Trie Structure of all the words in all the webpages?", title,
				JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
		if(n==1) {
			return;
		}
		JOptionPane.showOptionDialog(frame, "Press OK & wait, while we prepare Trie structure.", title, JOptionPane.NO_OPTION,
				JOptionPane.INFORMATION_MESSAGE, null, okObject, okObject[0]);
		
		String IMAGE_URL = "preloader.gif";
        
        JPanel contentPane;
        JLabel imageLabel = new JLabel();
        JLabel headerLabel = new JLabel();
        
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        contentPane = (JPanel) getContentPane();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(headerLabel, java.awt.BorderLayout.CENTER);
        setSize(new Dimension(300, 300));
        setTitle(title);
        headerLabel.setFont(new java.awt.Font("TimesRoman", Font.BOLD, 16));
        headerLabel.setText("Creating Trie of words found on URLs");
        headerLabel.setVerticalAlignment(JLabel.CENTER);
        headerLabel.setVerticalAlignment(JLabel.CENTER);
        contentPane.setBackground(new ColorUIResource(173,216,230));
        contentPane.add(headerLabel, java.awt.BorderLayout.NORTH);
        
        
        ImageIcon imageIcon = new ImageIcon(this.getClass().getResource( IMAGE_URL));
        imageLabel.setIcon(imageIcon);
        imageLabel.setHorizontalAlignment(JLabel.CENTER);
        imageLabel.setVerticalAlignment(JLabel.CENTER);
        contentPane.add(imageLabel, java.awt.BorderLayout.CENTER);

        this.setLocationRelativeTo(null);
        this.setVisible(true);
        
        SwingWorker<SearchEngine , SearchEngine> worker1 = new SwingWorker<SearchEngine, SearchEngine>() {
	    	SearchEngine engine;
			@Override
	        protected SearchEngine doInBackground() throws Exception {
	            engine = new SearchEngine();
	            setVisible(false);
	            return engine;
	        }
	        protected void done() {
	        	setVisible(false);
	        };
        };
        worker1.execute();
		SearchEngine engine = worker1.get();
		
		//Accept the word to be searched by user
		try{
			String inputString;
			String flag;
			do{
				flag=JOptionPane.showInputDialog(frame, "Do you wish to enter more pages to the search engine?: (Y/N)",title, JOptionPane.INFORMATION_MESSAGE);
				if(flag.equalsIgnoreCase("y")) {
					String newPageURL = JOptionPane.showInputDialog(frame, "Enter the URL",title, JOptionPane.INFORMATION_MESSAGE);
					int size=engine.listOfAllPages.size();
					engine.listOfAllPages.add(newPageURL);
					engine.insertWordsFromWebPages(size);
					inputString = "_garbage_";
					continue;
				}
				
				inputString = JOptionPane.showInputDialog(frame, "Enter the words to be searched separated by comma:",title, JOptionPane.INFORMATION_MESSAGE);
				String[] indexTermArray = inputString.split("[[,]*|[ ]*]+");
	            
	            headerLabel.setText("Searching the trie ");

	            this.setLocationRelativeTo(null);
	            this.setVisible(true);
	            SwingWorker<ArrayList<String> , ArrayList<String>> worker = new SwingWorker<ArrayList<String> , ArrayList<String>>() {
			    	ArrayList<String>  webPages;
					@Override
			        protected ArrayList<String> doInBackground() throws Exception {
						webPages = engine.search(indexTermArray);
			            return webPages;
			        }
			        protected void done() {
			        	setVisible(false);
			        };
	            };
	            worker.execute();
	            ArrayList<String> webPages = null;
	            
				webPages = worker.get();
				
				try{
					if(webPages.size()==0 || webPages==null) {
						JOptionPane.showOptionDialog(frame, "No page contains any of the words searched", title, 
								JOptionPane.OK_OPTION, JOptionPane.INFORMATION_MESSAGE, null , okObject, okObject[0]);
					}
					else{
						String temp = "";
						for(String str: webPages) temp+="\n"+str;
						JOptionPane.showOptionDialog(frame, temp, title, 
								JOptionPane.OK_OPTION, JOptionPane.INFORMATION_MESSAGE, null , okObject, okObject[0]);
					}
				}
				catch(NullPointerException e){
					JOptionPane.showOptionDialog(frame, "No page contains any of the words searched", title, 
							JOptionPane.OK_OPTION, JOptionPane.INFORMATION_MESSAGE, null , okObject, okObject[0]);
				}
				Object[] done= {"No", "Yes"};
				int temp=JOptionPane.showOptionDialog(frame, "Do you want to terminate the program?", title, 
						JOptionPane.OK_OPTION, JOptionPane.INFORMATION_MESSAGE, null , done, done[0]);
				if(temp==1) break;
			}while(true);
			JOptionPane.showOptionDialog(frame, "Thankyou for using my search engine.", title,
					JOptionPane.OK_OPTION, JOptionPane.INFORMATION_MESSAGE, null, alrightObject, alrightObject[0]);
			System.exit(0);
		}
		catch(NullPointerException e){
			JOptionPane.showOptionDialog(frame, "Thankyou for using my search engine.", title,
					JOptionPane.OK_OPTION, JOptionPane.INFORMATION_MESSAGE, null, alrightObject, alrightObject[0]);
			System.exit(0);
		}
		System.exit(0);
	}
}