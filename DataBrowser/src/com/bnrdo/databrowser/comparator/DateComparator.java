package com.bnrdo.databrowser.comparator;

import java.util.Comparator;
import java.util.Date;
/**
 * @author UT1P98
 * 
 */
public class DateComparator implements Comparator<Date>{
	public int compare(Date date1, Date date2) {  
        return date1.compareTo(date2);
     } 
}