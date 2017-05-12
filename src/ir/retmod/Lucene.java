package ir.retmod;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import ir.utilities.FilePaths;

/**
 * To create Apache Lucene index in a folder and add files into this index based
 * on the input of the user.
 */
public class Lucene 
{
    private static Analyzer sAnalyzer = new SimpleAnalyzer(Version.LUCENE_47);
    private IndexWriter writer;

    public static void searchQueriesInCorpus(HashMap<Integer, List<String>> corpus, TreeMap<Integer, List<String>> queries, Set<String> stopList) throws IOException 
    {
    	String indexLocation = FilePaths.luceneIndexPath;
		Lucene indexer = null;
		try 
		{
		    indexer = new Lucene(indexLocation);
		    indexer.indexCorpus(corpus, stopList);
		    indexer.closeIndex();
		}
		catch (Exception ex) 
		{
		    System.out.println("Cannot create index..." + ex.getMessage());
		    ex.printStackTrace();
		    System.exit(-1);
		}

		IndexReader reader = DirectoryReader.open(FSDirectory.open(new File(indexLocation)));
		IndexSearcher searcher = new IndexSearcher(reader);
		PrintWriter writer = new PrintWriter(new FileOutputStream(FilePaths.luceneSearchReultsPath), true);
		
		for (Integer queryId : queries.descendingKeySet()) 
		{
		    String queryContent = getContentFromList(queries.get(queryId), stopList);
		    queryContent = QueryParser.escape(queryContent);
		    
		    try 
		    {
		    	TopScoreDocCollector collector = TopScoreDocCollector.create(100, true);
				Query q = new QueryParser(Version.LUCENE_47, "docContent", sAnalyzer).parse(queryContent);
				searcher.search(q, collector);
				ScoreDoc[] hits = collector.topDocs().scoreDocs;
		
				for (int i = 0; i < hits.length; ++i)
				{
				    Document doc = searcher.doc(hits[i].doc);
				    String docId = doc.get("docId");
				    writer.println(queryId + " Q0 " + docId + " " + (i+1) + " " + hits[i].score + " Lucene_DOD");
				}
		    } 
		    catch (Exception e) 
		    {
				System.out.println("Error searching " + queryId + " : " + e.getMessage());
				e.printStackTrace();
			}
		    
		    writer.println();
		}
		
		writer.close();
    }

    /**
     * Constructor
     * @param indexLocation the name of the folder in which the index should be created
     * @throws java.io.IOException when exception creating index.
     */
    Lucene(String indexLocation) throws IOException
    {
    	FSDirectory dir = FSDirectory.open(new File(indexLocation));
    	IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_47, sAnalyzer);
    	writer = new IndexWriter(dir, config);
    }

    /**
     * Indexes a corpus
     * @param fileName the name of a text file or a folder we wish to add to the index
     * @throws java.io.IOException when exception
     */
    private void indexCorpus(HashMap<Integer, List<String>> corpus, Set<String> stopList) throws IOException 
    {
		//int originalNumDocs = writer.numDocs();
		
		for (Map.Entry<Integer,List<String>> docInCorpus : corpus.entrySet()) 
		{
			Integer docId = docInCorpus.getKey();
		    String docContent = getContentFromList(docInCorpus.getValue(), stopList);
		    
			try 
		    {
			    Document doc = new Document();
				doc.add(new TextField("docContent", docContent, Field.Store.YES));
				doc.add(new StringField("docId", docId.toString(), Field.Store.YES));
		
				writer.addDocument(doc);
		    }
		    catch (Exception e) 
		    {
				System.out.println("Could not add: " + docId);
				e.printStackTrace();
		    }
		}

		//int newNumDocs = writer.numDocs();
		//System.out.println((newNumDocs - originalNumDocs) + " documents added.");
    }
	
    /**
     * Close the index.
     * @throws java.io.IOException when exception closing
     */
    private void closeIndex() throws IOException 
    {
    	writer.close();
    }
    
    private static String getContentFromList(List<String> strList, Set<String> stopList)
    {
    	StringBuffer buf = new StringBuffer();
    	for(String str : strList)
    	{
    		if(!stopList.contains(str))
    		{
    			buf.append(str + " "); 
    		}
    	}
    	return buf.toString();
    }
}