package com.bnrdo.databrowser.comparator;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
/**
 * @author UT1P98
 * 
 */
public class DateComparator implements Comparator<String>{
	
	private String sortOrder;
	private SimpleDateFormat dateFormat;
	
	public DateComparator(String format, String order){
		sortOrder = order;
		try{
			dateFormat =  new SimpleDateFormat(format);
		}catch(IllegalArgumentException ia){
			ia.printStackTrace();
		}
	}
	
	public int compare(String str1, String str2) {  
        Date date1 = null;
        Date date2 = null;
        
		try {
			date1 = dateFormat.parse(str1);
			date2 = dateFormat.parse(str2);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		 
        int r = 0;
         
        if(sortOrder.equals("asc")){
			r = date1.compareTo(date2);
		}else{
			r =date2.compareTo(date1);
		}
        return r;
     } 
}