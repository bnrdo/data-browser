package com.bnrdo.databrowser.comparator;

import java.util.Comparator;
/**
 * @author UT1P98
 * 
 */
public class StringComparator implements Comparator<String>{
	
	private String sortOrder;
	
	public StringComparator(String order){
		sortOrder = order;
	}

	public int compare(String s1, String s2) {
		int r = 0;
		
		if(sortOrder.equals("asc"))
			r = s1.compareTo(s2);
		else
			r = s2.compareTo(s1);
		
        return r;
      }
}