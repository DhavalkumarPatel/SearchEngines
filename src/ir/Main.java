package ir;

import java.util.HashSet;

import ir.eval.Evaluation;
import ir.retmod.BM25;
import ir.retmod.Lucene;
import ir.retmod.TfIdf;
import ir.utilities.FilePaths;
import ir.utilities.InputParser;

/**
 * This class is used to run all the Tasks
 * All the tasks are already configured in main method. 
 * Just remove the comments to run any task.
 * @author dhaval
 *
 */
public class Main 
{
	public static void main(String[] args) throws Exception
	{
		// Parse the different input files
		InputParser input = new InputParser();
		
		// Task-1 BM25 Baseline Run
		//BM25.searchQueriesInCorpus(input.getUnStemmedCorpus(), input.getUnStemmedQueries(), 1, new HashSet<>(), input.getRelJudgements(), false);
		
		// Task-1 TfIdf Baseline Run
		//TfIdf.searchQueriesInCorpus(input.getUnStemmedCorpus(), input.getUnStemmedQueries(), 1, new HashSet<>(), false);
		
		// Task-1 Lucene Baseline Run
		//Lucene.searchQueriesInCorpus(input.getUnStemmedCorpus(), input.getUnStemmedQueries(), new HashSet<>());
		
		// Task-2 BM25 with Query Expansion
		//BM25.searchQueriesInCorpus(input.getUnStemmedCorpus(), input.getUnStemmedQueries(), 1, input.getStopList(), input.getRelJudgements(), true);
		
		// Task-2 TfIdf with Query Expansion
		//TfIdf.searchQueriesInCorpus(input.getUnStemmedCorpus(), input.getUnStemmedQueries(), 1, input.getStopList(), true);
		
		// Task-3-A BM25 with Stopping
		//BM25.searchQueriesInCorpus(input.getUnStemmedCorpus(), input.getUnStemmedQueries(), 1, input.getStopList(), input.getRelJudgements(), false);
		
		// Task-3-A TfIdf with Stopping
		//TfIdf.searchQueriesInCorpus(input.getUnStemmedCorpus(), input.getUnStemmedQueries(), 1, input.getStopList(), false);
		
		// Task-3-B BM25 with Stemming
		//BM25.searchQueriesInCorpus(input.getStemmedCorpus(), input.getStemmedQueries(), 1, new HashSet<>(), input.getRelJudgements(), false);
		
		// Task-3-B TfIdf with Stemming
		//TfIdf.searchQueriesInCorpus(input.getStemmedCorpus(), input.getStemmedQueries(), 1, new HashSet<>(), false);
		
		// Evaluate any search result of specific run, just modify the path of the file in input 
		//Evaluation.evaluateSearchResults(FilePaths.bM25SearchReultsPath, input.getRelJudgements());
		
		System.out.println("All steps executed successfully.");
	}
}
