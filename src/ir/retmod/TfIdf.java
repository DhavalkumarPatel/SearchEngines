package ir.retmod;

import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import ir.index.InvertedIndex;
import ir.utilities.FilePaths;
import ir.utilities.MyTreeMap;
import ir.utilities.Utility;

/**
 * TfIdf Ranking Algorithm
 * @author dhaval
 *
 */
public class TfIdf 
{
	/**
	 * Search queries and write the output in output folder using
	 * given inverted index and TfIdf ranking algorithm
	 * @param corpus
	 * @param queries
	 * @param n
	 * @param stopList
	 * @param queryExpansion
	 * @throws Exception
	 */
	public static void searchQueriesInCorpus(HashMap<Integer, List<String>> corpus, TreeMap<Integer, List<String>> queries, int n, Set<String> stopList, boolean queryExpansion) throws Exception
	{

		InvertedIndex invIndex = new InvertedIndex(corpus, n, stopList);
		
		// Query search results writer
		PrintWriter queryResultWriter = new PrintWriter(new FileOutputStream(FilePaths.tfIdfSearchReultsPath), true);
					
		for (Integer queryId : queries.descendingKeySet()) 
		{
			HashMap<String, Integer> queryTerms = Utility.getQueryTerms(Utility.getContentFromList(queries.get(queryId), stopList), n);
					
			// Get ordered documents with score calculated using TFIDF scoring algorithm
			MyTreeMap docWithScore = rankDocumentsUsingTfIdf(queryTerms, invIndex);
			
			if(queryExpansion)
			{
				System.out.println("Query Terms (" + queryId +") :: " + queryTerms.keySet().toString());
				queryTerms = Utility.getExpandedQueryTerms(queryTerms, docWithScore, invIndex.getDocTermFreqMap());
				System.out.println("Expanded Query Terms (" + queryId +") :: " + queryTerms.keySet().toString());
				
				docWithScore = rankDocumentsUsingTfIdf(queryTerms, invIndex);
			}
			
			// Write search results
			int rank = 1;
			loop:
			for (Double score : docWithScore.getMap().descendingKeySet()) 
			{
				for(String docId : docWithScore.getMap().get(score))
				{
					queryResultWriter.println(queryId + " Q0 " + docId + " " + rank + " " + score + " TfIdf_DOD");
					rank++;
					
					if(rank > 100)
					{
						break loop;
					}
				}
			}
			
			queryResultWriter.println();
		}
		
		queryResultWriter.close();
	}
	
	/**
	 * TFIDF score calculating algorithm 
	 * @param queryTerms
	 * @param invIndex
	 * @return
	 * @throws Exception
	 */
	private static MyTreeMap rankDocumentsUsingTfIdf(HashMap<String, Integer> queryTerms, InvertedIndex invIndex) throws Exception
	{
		// document score accumulator
		HashMap<Integer, Double> docScoreMap = new HashMap<Integer, Double>();
		int N = invIndex.getNoOfDocs();
		
		for (Map.Entry<String,Integer> query : queryTerms.entrySet()) 
		{
			String queryTerm = query.getKey();
			int qfi = query.getValue();
			
			TreeMap<Integer, Integer> docTFMap = invIndex.getIndexMap().get(queryTerm);
			
			if(null != docTFMap)
			{
				Iterator<Map.Entry<Integer,Integer>> docTFIterator = docTFMap.entrySet().iterator();
				Integer ni = docTFMap.size();
				
				// Iterate each document of the inverted list which contains the query term
		        while(docTFIterator.hasNext())
		        {
		        	Map.Entry<Integer, Integer> docTF = docTFIterator.next();
		        	Integer docId = docTF.getKey();
		        	Integer fi = docTF.getValue();
		        	
		        	Integer dl = invIndex.getDocLength(docId);
		        	
		        	double tf = (double) fi / dl;
		        	double idf = Math.log10((double) N/ni);
		        	double score = tf * idf * qfi;
		        	
		        	// add document score in accumulator
		        	if(docScoreMap.containsKey(docId))
        			{
		        		score += docScoreMap.get(docId);
        			}
		        	docScoreMap.put(docId, score);
		        }
			}
		}
		
		// sort document by document score
		MyTreeMap orderedDocs = new MyTreeMap();
		for(Map.Entry<Integer, Double> docScore : docScoreMap.entrySet())
		{
			orderedDocs.add(docScore.getValue(), docScore.getKey().toString());
	    }
		
		return orderedDocs;
	}
}
