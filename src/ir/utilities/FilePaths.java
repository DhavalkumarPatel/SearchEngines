package ir.utilities;

/**
 * This class contains all the input and output file paths.
 * Please verify this paths before running the program.
 * @author dhaval
 *
 */
public class FilePaths 
{
	//START - input paths
	public static final String corpusRawDocsPath = "data/input/CorpusRawDocs";
	public static final String corpusStemmedPath = "data/input/CorpusStemmed/cacm_stem.txt";
	public static final String queriesNotProcessedPath = "data/input/QueriesNotProcessed/cacm.query";
	public static final String queriesStemmedPath = "data/input/QueriesStemmed/cacm_stem.query";
	public static final String relJudgementsPath = "data/input/RelevanceJudgments/cacm.rel";
	public static final String stopListPath = "data/input/Stoplist/common_words";
	//END - input paths
	
	//START - output paths
	public static final String luceneIndexPath = "data/output/LuceneIndexes";
	public static final String bM25SearchReultsPath = "data/output/BM25_SearchResults.txt";
	public static final String tfIdfSearchReultsPath = "data/output/TfIdf_SearchResults.txt";
	public static final String luceneSearchReultsPath = "data/output/Lucene_SearchResults.txt";
	public static final String precRecTableFilePath = "data/output/PrecisionRecallTable.txt";
	public static final String evalStatsPerQueryFilePath = "data/output/EvaluationStatsPerQuery.txt";
	public static final String evalStatsPerRunFilePath = "data/output/EvaluationStatsPerRun.txt";
	//END - output paths
}
