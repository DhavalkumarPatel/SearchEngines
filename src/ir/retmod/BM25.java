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
 * BM25 Ranking Algorithm
 * @author dhaval
 *
 */
public class BM25 
{
	// Constants to calculate the document score
	private static final double k1=1.2;
	private static final double b=0.75;
	private static final double k2=100.0;
	
	/**
	 * Search queries and write the output in output folder using
	 * given inverted index and BM25 ranking algorithm
	 * @param corpus
	 * @param queries
	 * @param n
	 * @param stopList
	 * @param relJudgements
	 * @param queryExpansion
	 * @throws Exception
	 */
	public static void searchQueriesInCorpus(HashMap<Integer, List<String>> corpus, TreeMap<Integer, List<String>> queries, int n, Set<String> stopList, HashMap<Integer, Set<Integer>> relJudgements, boolean queryExpansion) throws Exception
	{
		InvertedIndex invIndex = new InvertedIndex(corpus, n, stopList);
		
		// Query search results writer
		PrintWriter queryResultWriter = new PrintWriter(new FileOutputStream(FilePaths.bM25SearchReultsPath), true);
		
		for (Integer queryId : queries.descendingKeySet()) 
		{
			HashMap<String, Integer> queryTerms = Utility.getQueryTerms(Utility.getContentFromList(queries.get(queryId), stopList), n);
		    
			// Get ordered documents with score calculated using BM25 scoring algorithm
			MyTreeMap docWithScore = rankDocumentsUsingBM25(queryTerms, invIndex, relJudgements.get(queryId));
			
			// expand the query if queryExpansion flag is true
			if(queryExpansion)
			{
				System.out.println("Query Terms (" + queryId +") :: " + queryTerms.keySet().toString());
				queryTerms = Utility.getExpandedQueryTerms(queryTerms, docWithScore, invIndex.getDocTermFreqMap());
				System.out.println("Expanded Query Terms (" + queryId +") :: " + queryTerms.keySet().toString());
				
				docWithScore = rankDocumentsUsingBM25(queryTerms, invIndex, relJudgements.get(queryId));
				
			}
			
			// Write search results
			int rank = 1;
			loop:
			for (Double score : docWithScore.getMap().descendingKeySet()) 
			{
				for(String docId : docWithScore.getMap().get(score))
				{
					queryResultWriter.println(queryId + " Q0 " + docId + " " + rank + " " + score + " BM25_DOD");
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
	 * BM25 score calculating algorithm
	 * @param query
	 * @param ii
	 * @return
	 * @throws Exception
	 */
	private static MyTreeMap rankDocumentsUsingBM25(HashMap<String, Integer> queryTerms, InvertedIndex invIndex, Set<Integer> relDocs) throws Exception
	{
		// document score accumulator
		HashMap<Integer, Double> docScoreMap = new HashMap<Integer, Double>();
		int N = invIndex.getNoOfDocs();
		int R = 0;
		
		if(null != relDocs)
		{
			R = relDocs.size();
		}
		
		for (Map.Entry<String,Integer> query : queryTerms.entrySet()) 
		{
			String queryTerm = query.getKey();
			int qfi = query.getValue();
			
			TreeMap<Integer, Integer> docTFMap = invIndex.getIndexMap().get(queryTerm);
			
			if(null != docTFMap)
			{
				Iterator<Map.Entry<Integer,Integer>> docTFIterator = docTFMap.entrySet().iterator();
				Integer ni = docTFMap.size();
				Integer ri = getRI(docTFMap, relDocs);
				
				// Iterate each document of the inverted list which contains the query term
		        while(docTFIterator.hasNext())
		        {
		        	Map.Entry<Integer, Integer> docTF = docTFIterator.next();
		        	Integer docId = docTF.getKey();
		        	Integer fi = docTF.getValue();
		        	
		        	double dl = invIndex.getDocLength(docId);
		        	double avdl = invIndex.getAverageDocLength();
		        	
		        	double K = k1 * ((1 - b) + (b * dl/avdl));
		        	
		        	// score calculator, we can take normal log as well
		        	double score = Math.log10((((ri + 0.5)/(R - ri + 0.5)) / ((ni - ri + 0.5)/(N - ni - R + ri + 0.5))) * (((k1 + 1) * fi) / (K + fi)) * (((k2 + 1) * qfi) / (k2 + qfi)));
		        	
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
	
	/**
	 * This method finds the Ri value 
	 * @param docList
	 * @param relDocs
	 * @return
	 */
	private static Integer getRI(TreeMap<Integer, Integer> docList, Set<Integer> relDocs)
	{
		Integer ri = 0;
		
		if(null != relDocs)
		{
			for(Integer docId : relDocs)
			{
				if(docList.containsKey(docId))
				{
					ri++;
				}
			}
		}
		
		return ri;
	}
}