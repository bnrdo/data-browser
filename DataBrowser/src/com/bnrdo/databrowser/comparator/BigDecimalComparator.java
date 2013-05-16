package com.bnrdo.databrowser.comparator;

import java.math.BigDecimal;
import java.util.Comparator;
/**
 * @author UT1P98
 * 
 */
public class BigDecimalComparator implements Comparator<BigDecimal>{
	
	private String sortOrder;
	
	public BigDecimalComparator(String order){
		sortOrder = order;
	}
	
	public int compare( BigDecimal bd1,  BigDecimal bd2) {  
		int r = 0;
		
        if(sortOrder.equals("asc")){
			r = bd1.compareTo(bd2);
		}else{
			r = bd2.compareTo(bd1);
		}
        return r;
     } 
}