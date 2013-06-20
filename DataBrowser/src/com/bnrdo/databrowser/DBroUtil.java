package com.bnrdo.databrowser;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.Formatter;
import java.util.List;

import org.apache.commons.lang3.mutable.MutableInt;

import com.bnrdo.databrowser.Constants.SORT_ORDER;
import com.bnrdo.databrowser.Constants.JAVA_TYPE;
import com.bnrdo.databrowser.comparator.DateComparator;
import com.bnrdo.databrowser.comparator.IntegerComparator;
import com.bnrdo.databrowser.comparator.StringComparator;
import com.bnrdo.databrowser.format.ListSourceFormat;

public class DBroUtil {
	
	public static Connection getConnection(){
		Connection con = null;
		try {
			Class.forName("org.hsqldb.jdbcDriver");
			con = DriverManager.getConnection("jdbc:hsqldb:mem:data-browser", "sa", "");
			//con = DriverManager.getConnection("jdbc:hsqldb:file:C:\\Users\\ut1p98\\Desktop\\db\\data-browser", "sa", "");
			//con = DriverManager.getConnection("jdbc:hsqldb:file:C:\\Users\\ut1p98\\Desktop\\db\\data-browser", "sa", "");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}  catch(SQLException e){
			e.printStackTrace();
		}
		return con;
	}
	public static <E> Object[] convertPojoToObjectArray(E domain, List<String> propNamesToPut){
		Object[] retVal = new Object[propNamesToPut.size()];
		System.out.println(propNamesToPut);
		return retVal;
	}
	
	public static int getIndexOfNumFromArray(int[] numArr, int toFind){
		int retVal = 0;
		
		for(int i : numArr){
			if(i == toFind){
				break;
			}else{
				retVal++;	
			}
		}
		
		return retVal;
	}
	
	public static Integer toInt(String s){
		try{
			return Integer.parseInt(s);
		}catch(Exception e){
			return new Integer(0);
		}
	}
	
	public static Double toDouble(String s){
		try{
			return Double.parseDouble(s);
		}catch(Exception e){
			return new Double(0);
		}
	}
	
	public static Float toFloat(String s){
		try{
			return Float.parseFloat(s);
		}catch(Exception e){
			return new Float(0);
		}
	}
	
	public static Long toLong(String s){
		try{
			return Long.parseLong(s);
		}catch(Exception e){
			return new Long(0);
		}
	}
	
	public static int getPageCount(int itemCount, int itemsPerpage){
		int retVal = 0;
		int srcSize = itemCount;
		int itemsPerPage = itemsPerpage;
		int pageCountForEvenSize = (srcSize / itemsPerPage);
		retVal = ((srcSize % itemsPerPage) == 0 ? pageCountForEvenSize : pageCountForEvenSize + 1);
		return retVal;
	}
	

	public static Comparator getComparator(Class forWhatClass){
		if(forWhatClass == Integer.class)
			return new IntegerComparator();
		else if(forWhatClass == Date.class){
			return new DateComparator();
		}else{
			return new StringComparator();
		}
	}
	
	public static String makeCSVFromArray(String[] arr){
		StringBuilder bdr = new StringBuilder();
		
		for(String s : arr){
			bdr.append(s).append(", ");
		}
		
		bdr.replace(bdr.length()-2, bdr.length(), "");
		
		return bdr.toString();
	}
	
	public static String moneyFormat(String money){
		return moneyFormat(Double.parseDouble(money));
	}
	
	public static String moneyFormat(double money){
		DecimalFormat formatter = new DecimalFormat("#,###");
		return formatter.format(money);
	}
	
    
}
