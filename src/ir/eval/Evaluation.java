package ir.eval;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

import ir.utilities.FilePaths;

/**
 * This class calculates Evaluation Measures like Precision, Recall, Reciprocal Rank, MAP and MRR
 * for a specific search engine query results and relevance judgments
 * @author dhaval
 *
 */
public class Evaluation 
{
	/*
	 * This method calculates Evaluation Measures like Precision, Recall, Reciprocal Rank, MAP and MRR
	 * for a specific search engine query results and relevance judgments and write them to files
	 */
	public static void evaluateSearchResults(String searchResulltFilePath, HashMap<Integer, Set<Integer>> relJudgements) throws Exception
	{
		// File writer to write precision and recall values for each rank of a query
		PrintWriter precRecTableWriter = new PrintWriter(new FileOutputStream(FilePaths.precRecTableFilePath), true);
		precRecTableWriter.println("QueryId Rank Precision Recall");
		
		// File writer to write P@K, average precision and reciprocal rank of each query
		PrintWriter evalStatsPerQueryWriter = new PrintWriter(new FileOutputStream(FilePaths.evalStatsPerQueryFilePath), true);
		evalStatsPerQueryWriter.println("QueryId PrecisionAt5 PrecisionAt20 AveragePrecision ReciprocalRank");
		
		// File writer to write MAP and MRR of this run
		PrintWriter evalStatsPerRunWriter = new PrintWriter(new FileOutputStream(FilePaths.evalStatsPerRunFilePath), true);
		
		// Parse query result by queryId
		TreeMap<Integer, List<String>> queryResults = new TreeMap<>(Collections.reverseOrder());
		BufferedReader br = new BufferedReader(new FileReader(searchResulltFilePath));
		String currentLine = null;
		while ((currentLine = br.readLine()) != null) 
		{
			if(!currentLine.equals(""))
			{
				Integer queryId = Integer.parseInt(currentLine.split(" ")[0]);
			
				List<String> results = queryResults.get(queryId);
				
				if(results == null)
				{
					results = new ArrayList<>();
				}
				
				results.add(currentLine);
				
				queryResults.put(queryId, results);
			}
		}
		br.close();
		
		// calculate evaluation measures
		Double avgPrecisionSum = 0.0;
		Integer avgPrecisionCount = 0;
		
		Double recRankSum = 0.0;
		Integer recRankCount = 0;
		
		for (Integer queryId : queryResults.descendingKeySet()) 
		{
			// ignore queries who does not have relevance information
			if(relJudgements.containsKey(queryId))
			{
				Set<Integer> relDocs = relJudgements.get(queryId);
				Integer noOfRelRet = 0;
				
				// For Precision at K per query
				Double pAt5 = 0.0;
				Double pAt20 = 0.0;
				
				// For Average Precision per query
				Double precisionSum = 0.0;
				Integer precisionCount = 0;
				
				// For Reciprocal Rank per query
				Double recRank = 0.0;
				
				for(String result : queryResults.get(queryId))
				{
					String strArr[] = result.split(" ");
					Integer docId = Integer.parseInt(strArr[2]);
					Integer rank = Integer.parseInt(strArr[3]);
					
					if(relDocs.contains(docId))
					{
						noOfRelRet++;
					}
					
					double precision = (double) noOfRelRet / rank;
					double recall = (double) noOfRelRet / relDocs.size();
					precRecTableWriter.println(queryId + " " + rank + " " + precision + " " + recall);
					
					if(relDocs.contains(docId))
					{
						// This is a relevant document, update precision for average
						precisionSum += precision;
						precisionCount++;
						
						if(recRank == 0.0)
						{
							recRank = (double) 1.0 / rank;
						}
					}
					
					if(rank == 5)
					{
						pAt5 = precision;
					}
					else if(rank == 20)
					{
						pAt20 = precision;
					}
				}
				
				// calculates the average precision and reciprocal rank of a query
				Double avgPrecision = 0.0;
				if(precisionCount > 0)
				{
					avgPrecision = (double) precisionSum / precisionCount;
				}
				
				evalStatsPerQueryWriter.println(queryId + " " + pAt5 + " " + pAt20 + " " + avgPrecision + " " + recRank);
				
				avgPrecisionSum += avgPrecision;
				avgPrecisionCount++;
				
				recRankSum += recRank;
				recRankCount++;
			}
		}
		
		// calculate and write MAP & MRR
		evalStatsPerRunWriter.println("MAP = " + (double) avgPrecisionSum / avgPrecisionCount);
		evalStatsPerRunWriter.println("MRR = " + (double) recRankSum / recRankCount);
		
		precRecTableWriter.close();
		evalStatsPerQueryWriter.close();
		evalStatsPerRunWriter.close();
	}
}
