package ir.utilities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This class contains all the utility methods used by other classes.
 * @author dhaval
 *
 */
public class Utility 
{
	/**
	 * This method tokenizes the input line and parse it to valid tokens.
	 * @param line
	 * @return
	 */
	public static List<String> tokenize(String line)
	{
		List<String> tokens = new ArrayList<>();
		
		line = line.toLowerCase().replaceAll("[\t\\[\\]\\;?(){}\"-]", " ");
		line = depunctuateString(line.toCharArray());
		
		for (String token : line.split(" "))
		{
			token = token.trim();
			if(!token.equals("") && !token.equals("<html>") && !token.equals("<pre>") && !token.equals("</pre>") && !token.equals("</html>"))
			{
				if(token.length() == 1 && !(Character.isAlphabetic(token.charAt(0)) || Character.isDigit(token.charAt(0))))
				{
					continue;
				}
				
				token = deApostropheString(token);
				
				if(token.length() > 0)
				{
					tokens.add(token);
				}
			}
		}
		
		return tokens;
	}
	
	/**
	 * This function removes . , and : from the token but retains if it is between digits :: a,b,c.1.2 = abc1.2
	 * @param token
	 * @return
	 */
	private static String depunctuateString(char[] token)
	{
		StringBuffer tokenBuffer = new StringBuffer();
		
		for(int i=0; i<token.length; i++)
		{
			if(token[i] == ',' || token[i] == '.' ||  token[i] == ':')
			{
				if(i>0 && i<token.length-1 && token[i-1] >= '0' && token[i-1] <= '9' && token[i+1] >= '0' && token[i+1] <= '9')
				{
					tokenBuffer.append(token[i]);
				}
				else
				{
					tokenBuffer.append(" ");
				}
			}
			else
			{
				tokenBuffer.append(token[i]);
			}
		}
		
		return tokenBuffer.toString();
	}
	
	/**
	 * This method removes the ' character from token with below cases
	 * 1. abc's = abc
	 * 2. o'cornel = ocornel
	 * @param token
	 * @return
	 */
	private static String deApostropheString(String token)
	{
		if(token.contains("'"))
		{
			if(token.indexOf("'") == 0)
			{
				token = token.substring(1);
			}
			else if(token.lastIndexOf("'") >= token.length()-2)
			{
				token = token.substring(0, token.lastIndexOf("'")-1);
			}
			else
			{
				token = token.replace("'", "");
			}
		}
		
		return token;
	}
	
	/**
	 * This method filters stop words from document text
	 * @param strList
	 * @param stopList
	 * @return
	 */
	public static List<String> getContentFromList(List<String> strList, Set<String> stopList)
    {
		List<String> opList = new ArrayList<>();
    	for(String str : strList)
    	{
    		if(!stopList.contains(str))
    		{
    			opList.add(str); 
    		}
    	}
    	return opList;
    }
	
	/**
	 * This method parses the query content into tokens
	 * @param queryContent
	 * @param n
	 * @return
	 */
	public static HashMap<String, Integer> getQueryTerms(List<String> queryContent, int n)
    {
		HashMap<String, Integer> queryTermFreqMap = new HashMap<String, Integer>();
		for(int i=0; (i+n) <= queryContent.size(); i++)
		{
			// generate a n-gram
			String word = queryContent.get(i);
				
			for(int j=1; j<n; j++)
			{
				word += " " + queryContent.get(i+j);
			}
			
			if(queryTermFreqMap.containsKey(word))
			{
				Integer freq = queryTermFreqMap.get(word);
				freq++;
				queryTermFreqMap.put(word, freq);
			}
			else
			{
				queryTermFreqMap.put(word, 1);
			}
		}
    	return queryTermFreqMap;
    }
	
	/**
	 * This method checks that the string has only digits or not
	 * @param str
	 * @return
	 */
	public static boolean isStringContainsDigitsOnly(String str)
	{
		for(Character c : str.toCharArray())
		{
			if(c != ' ' && c != '\t')
			{
				if(!Character.isDigit(c))
				{
					return false;
				}
			}
		}
		
		return true;
	}
	
	/**
	 * This method finds the most frequent words from the top retrieved documents
	 * and add them to query terms as a query expansion
	 * @param queryTerms
	 * @param docWithScore
	 * @param docTermFreqMap
	 * @return
	 */
	public static HashMap<String, Integer> getExpandedQueryTerms(HashMap<String, Integer> queryTerms, MyTreeMap docWithScore, Map<Integer, Map<String, Integer>> docTermFreqMap)
	{
		int docLimit = 10;
		int termLimit = 10;
		
		// find the most frequent words from top retrieved documents
		Map<String, Integer> globalTermFreqMap = new HashMap<>();
		int docRank = 1;
		docLoop:
		for (Double score : docWithScore.getMap().descendingKeySet()) 
		{
			for(String docIdStr : docWithScore.getMap().get(score))
			{
				Integer docId = Integer.parseInt(docIdStr);
				for(Map.Entry<String, Integer> termFreq :  docTermFreqMap.get(docId).entrySet())
				{
					String term = termFreq.getKey();
					Integer freq = termFreq.getValue();
					
					if(globalTermFreqMap.containsKey(term))
					{
						freq += globalTermFreqMap.get(term);
					}
					
					globalTermFreqMap.put(term, freq);
				}
				
				docRank++;
				if(docRank > docLimit)
				{
					break docLoop;
				}
			}
		}
		
		// Order terms by frequency
		MyTreeMap orderedTermsByFreq = new MyTreeMap();
		for(Map.Entry<String, Integer> termFreq : globalTermFreqMap.entrySet())
		{
			orderedTermsByFreq.add(termFreq.getValue().doubleValue(), termFreq.getKey());
	    }
		
		// Add most frequent words to query terms
		int termRank = 1;
		termLoop:
		for (Double freq : orderedTermsByFreq.getMap().descendingKeySet()) 
		{
			for(String term : orderedTermsByFreq.getMap().get(freq))
			{
				if(!queryTerms.containsKey(term))
				{
					queryTerms.put(term, 1);
					termRank++;
				}
				
				if(termRank > termLimit)
				{
					break termLoop;
				}
			}
		}
		
		return queryTerms;
	}
}
