package ir.index;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import ir.utilities.Utility;

/**
 * This class stores the entire inverted index table and frequency of each term.
 * @author dhaval
 *
 */
public class InvertedIndex implements Serializable
{
	private static final long serialVersionUID = 7916105588379557273L;
	
	// To index the terms as :: <term1, [<docId1, freqIn1>, <docId2, freqIn2>,...]
	private TreeMap<String, TreeMap<Integer, Integer>> indexMap;
	
	// To store the total frequency of a term as :: <term1, tf1>
	private Map<String, Integer> termFreqMap;
	
	// To store docId with its length
	private Map<Integer, Integer> docLengthMap;
	
	// To get all terms with frequency for a document <docId, <term, frequency>>
	private Map<Integer, Map<String, Integer>> docTermFreqMap;
	
	// Total corpus length
	private Integer totalCorpusLength;
	
	/**
	 * This function generates the inverted index for given corpus and for given n
	 * @param inputFolder
	 * @param n
	 * @param outputFile
	 * @throws Exception
	 */
	public InvertedIndex(HashMap<Integer, List<String>> corpus, int n, Set<String> stopList) throws Exception
	{
		indexMap = new TreeMap<String, TreeMap<Integer, Integer>>();
		termFreqMap = new HashMap<String, Integer>();
		docLengthMap = new HashMap<Integer, Integer>();
		docTermFreqMap = new HashMap<>();
		totalCorpusLength = 0;
		
		for (Map.Entry<Integer,List<String>> docInCorpus : corpus.entrySet()) 
		{
			Integer docId = docInCorpus.getKey();
		    List<String> docContent = Utility.getContentFromList(docInCorpus.getValue(), stopList);
		    
			int noOfTokens = 0;
			
			// to store the local term and frequency of a document
			HashMap<String, Integer> wordFreqMap = new HashMap<String, Integer>();
			
			for(int i=0; (i+n) <= docContent.size(); i++)
			{
				// generate a n-gram
				String word = docContent.get(i);
					
				for(int j=1; j<n; j++)
				{
					word += " " + docContent.get(i+j);
				}
				
				if(wordFreqMap.containsKey(word))
				{
					Integer freq = wordFreqMap.get(word);
					freq++;
					wordFreqMap.put(word, freq);
				}
				else
				{
					wordFreqMap.put(word, 1);
				}
				noOfTokens++;
			}
			
			docTermFreqMap.put(docId, wordFreqMap);
			
			// update main index table with terms found in this document with its frequency
			addDocumentTerm(wordFreqMap, docId);
			
			docLengthMap.put(docId, noOfTokens);
			totalCorpusLength += noOfTokens;
	    }
	}

	/**
	 * This function merges the term and frequency of one document with main index table
	 * @param wordFreqMap
	 * @param docID
	 */
	public void addDocumentTerm(HashMap<String, Integer> wordFreqMap, Integer docID)
	{
		// for each unique term of a document
		for(Map.Entry<String, Integer> wordFreq : wordFreqMap.entrySet())
		{
			if(indexMap.containsKey(wordFreq.getKey()))
			{
				// update the index table
				TreeMap<Integer, Integer> indexEntryMap = indexMap.get(wordFreq.getKey());
				indexEntryMap.put(docID, wordFreq.getValue());
				indexMap.put(wordFreq.getKey(), indexEntryMap);
				
				// update the frequency table
				Integer freq = termFreqMap.get(wordFreq.getKey());
				freq += wordFreq.getValue();
				termFreqMap.put(wordFreq.getKey(), freq);
			}
			else
			{
				// update the index table
				TreeMap<Integer, Integer> indexEntryMap = new TreeMap<Integer, Integer>();
				indexEntryMap.put(docID, wordFreq.getValue());
				indexMap.put(wordFreq.getKey(), indexEntryMap);
				
				// update the frequency table
				termFreqMap.put(wordFreq.getKey(), wordFreq.getValue());
			}
		}
	}
	
	public TreeMap<String, TreeMap<Integer, Integer>> getIndexMap() 
	{
		return indexMap;
	}
	
	public Map<String, Integer> getTermFreqMap() 
	{
		return termFreqMap;
	}
	
	public Integer getDocLength(Integer docId)
	{
		return docLengthMap.get(docId);
	}
	
	public Integer getNoOfDocs()
	{
		return docLengthMap.size();
	}
	
	public double getAverageDocLength()
	{
		return (double) totalCorpusLength / docLengthMap.size();
	}
	
	public Map<Integer, Map<String, Integer>> getDocTermFreqMap() 
	{
		return docTermFreqMap;
	}
}
