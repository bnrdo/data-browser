package com.bnrdo.databrowser.comparator;

import java.util.Comparator;
/**
 * @author UT1P98
 * 
 */
public class IntegerComparator implements Comparator<Integer>{
	
	private String sortOrder;
	
	public IntegerComparator(String order){
		sortOrder = order;
	}
	
	public int compare(Integer i1, Integer i2) { 
         int int1 = i1.intValue();
         int int2 = i2.intValue();
         int r = 0;
         
         if(sortOrder.equals("asc")){
	        r = (int2 > int1 ? -1 : (int1 == int2 ? 0 : 1));
         }else{
        	r = (int1 > int2 ? -1 : (int1 == int2 ? 0 : 1));
         }
         return r;
     } 
	
}