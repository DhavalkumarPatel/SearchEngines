package ir.utilities;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

/**
 * This class is used to sort the values with duplicate keys
 */
public class MyTreeMap 
{
	private TreeMap<Double, List<String>> map;
	private int size;
	
	public MyTreeMap() 
	{
		 map = new TreeMap<Double, List<String>>();
		 size = 0;
	}
	
	/**
	 * Add the str in list for given integer
	 * @param integer
	 * @param str
	 */
	public void add(Double a, String b)
	{
		List<String> bs = map.get(a);
		
		if(bs == null)
		{
			bs = new ArrayList<String>();
		}
		
		bs.add(b);
		map.put(a, bs);
		size++;
	}
	
	public TreeMap<Double, List<String>> getMap() 
	{
		return map;
	}
	
	public int size()
	{
		return size;
	}
}
