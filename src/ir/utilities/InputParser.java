package ir.utilities;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

/**
 * This class parses various input files
 * @author dhaval
 *
 */
public class InputParser 
{
	// Data structure for each specific input file
	private HashMap<Integer, List<String>> unStemmedCorpus = new HashMap<>();
	private HashMap<Integer, List<String>> stemmedCorpus = new HashMap<>();
	private TreeMap<Integer, List<String>> unStemmedQueries = new TreeMap<>(Collections.reverseOrder());
	private TreeMap<Integer, List<String>> stemmedQueries = new TreeMap<>(Collections.reverseOrder());
	private HashMap<Integer, Set<Integer>> relJudgements = new HashMap<>();
	private Set<String> stopList = new HashSet<String>();
	
	public InputParser() throws Exception
	{
		parseUnStemmedCorpus();
		parseStemmedCorpus();
		parseUnStemmedQueries();
		parseStemmedQueries();
		parseRelJudgements();
		parseStopList();
	}

	/**
	 * Parse raw document corpus
	 * It converts the document Id to number
	 * @throws Exception
	 */
	private void parseUnStemmedCorpus() throws Exception
	{
		File folder = new File(FilePaths.corpusRawDocsPath);
		for (File file : folder.listFiles()) 
		{
			List<String> tokens = new ArrayList<>();
			String fileName = file.getName();
			
			BufferedReader br = new BufferedReader(new FileReader(file));
			String currentLine = null;
			while ((currentLine = br.readLine()) != null) 
			{
				if(!Utility.isStringContainsDigitsOnly(currentLine))
				{
					tokens.addAll(Utility.tokenize(currentLine));
				}
			}
			br.close();
			
			unStemmedCorpus.put(Integer.parseInt(fileName.substring(5, fileName.indexOf("."))), tokens);
	    }
	}
	
	/**
	 * Parse Stemmed Document Corpus
	 * It converts the document Id to number
	 * @throws Exception
	 */
	private void parseStemmedCorpus() throws Exception
	{
		String docId = "";
		boolean ignoreFlag = false;
		List<String> tokens = new ArrayList<>();
		BufferedReader br = new BufferedReader(new FileReader(FilePaths.corpusStemmedPath));
		String currentLine = null;
		while ((currentLine = br.readLine()) != null) 
		{
			if(currentLine.contains("#"))
			{
				if(!docId.equals(""))
				{
					stemmedCorpus.put(Integer.parseInt(docId), tokens);
				}
				
				docId = currentLine.split(" ")[1];
				tokens = new ArrayList<>();
				ignoreFlag = false;
			}
			else
			{
				for (String token : currentLine.split(" "))
				{
					token = token.trim();
					if(token.length() > 0)
					{
						if(!ignoreFlag)
						{
							tokens.add(token);	
						}
						if(token.equalsIgnoreCase("pm") || token.equalsIgnoreCase("am"))
						{
							ignoreFlag = true;
						}
					}
				}
			}
		}
		if(!docId.equals(""))
		{
			stemmedCorpus.put(Integer.parseInt(docId), tokens);
		}
		br.close();
	}
	
	/**
	 * Parse UnStemmed = Not Processed queries
	 * @throws Exception
	 */
	private void parseUnStemmedQueries() throws Exception
	{
		String queryId = "";
		List<String> tokens = new ArrayList<>();
		BufferedReader br = new BufferedReader(new FileReader(FilePaths.queriesNotProcessedPath));
		String currentLine = null;
		while ((currentLine = br.readLine()) != null) 
		{
			if(currentLine.contains("<DOC>"))
			{
				continue;
			}
			else if (currentLine.contains("<DOCNO>"))
			{
				queryId = currentLine.split(" ")[1];
				tokens = new ArrayList<>();
			}
			else if(currentLine.contains("</DOC>"))
			{
				unStemmedQueries.put(Integer.parseInt(queryId), tokens);
			}
			else
			{
				tokens.addAll(Utility.tokenize(currentLine));
			}
		}
		br.close();
	}
	
	/**
	 * Parse Stemmed Queries
	 * Query Ids are manually updated in the file
	 * @throws Exception
	 */
	private void parseStemmedQueries() throws Exception
	{
		BufferedReader br = new BufferedReader(new FileReader(FilePaths.queriesStemmedPath));
		String currentLine = null;
		while ((currentLine = br.readLine()) != null) 
		{
			String[] strArr = currentLine.split(":");
			Integer queryId = Integer.parseInt(strArr[0]);
			String queryContent = strArr[1];
			
			List<String> tokens = new ArrayList<>();
			for (String token : queryContent.split(" "))
			{
				token = token.trim();
				if(token.length() > 0)
				{
					tokens.add(token);
				}
			}
			stemmedQueries.put(queryId, tokens);
			queryId++;
		}
		br.close();
	}
	
	/**
	 * Parse the relevance judgments of all the queries
	 * @throws Exception
	 */
	private void parseRelJudgements() throws Exception
	{
		String queryId = "";
		Set<Integer> relDocList = new HashSet<>();
		BufferedReader br = new BufferedReader(new FileReader(FilePaths.relJudgementsPath));
		String currentLine = null;
		while ((currentLine = br.readLine()) != null) 
		{
			String[] strArr = currentLine.split(" ");
			
			if(queryId.equals(""))
			{
				queryId = strArr[0];
				relDocList.add(Integer.parseInt(strArr[2].substring(5)));
			}
			else 
			{
				if(strArr[0].equals(queryId))
				{
					relDocList.add(Integer.parseInt(strArr[2].substring(5)));
				}
				else
				{
					relJudgements.put(Integer.parseInt(queryId), relDocList);
					queryId = strArr[0];
					relDocList = new HashSet<>();
					relDocList.add(Integer.parseInt(strArr[2].substring(5)));
				}
			}
		}
		if(!queryId.equals(""))
		{
			relJudgements.put(Integer.parseInt(queryId), relDocList);
		}
		br.close();
	}
	
	/**
	 * Parse the stop words
	 * @throws Exception
	 */
	private void parseStopList() throws Exception
	{
		BufferedReader br = new BufferedReader(new FileReader(FilePaths.stopListPath));
		String currentLine = null;
		while ((currentLine = br.readLine()) != null) 
		{
			currentLine = currentLine.trim();
			if(currentLine.length() > 0)
			{
				stopList.add(currentLine);
			}
		}
		br.close();
	}
	
	
	public HashMap<Integer, List<String>> getUnStemmedCorpus() 
	{
		return unStemmedCorpus;
	}
	
	public HashMap<Integer, List<String>> getStemmedCorpus() 
	{
		return stemmedCorpus;
	}
	
	public TreeMap<Integer, List<String>> getUnStemmedQueries() 
	{
		return unStemmedQueries;
	}
	
	public TreeMap<Integer, List<String>> getStemmedQueries() 
	{
		return stemmedQueries;
	}
	
	public HashMap<Integer, Set<Integer>> getRelJudgements() 
	{
		return relJudgements;
	}
	
	public Set<String> getStopList() 
	{
		return stopList;
	}
}
