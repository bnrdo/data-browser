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
import org.apache.log4j.Logger;

import com.bnrdo.databrowser.Constants.ModelField;
import com.bnrdo.databrowser.Constants.SortOrder;
import com.bnrdo.databrowser.Constants.JavaType;
import com.bnrdo.databrowser.comparator.DateComparator;
import com.bnrdo.databrowser.comparator.IntegerComparator;
import com.bnrdo.databrowser.comparator.StringComparator;
import com.bnrdo.databrowser.format.ListSourceFormat;
import com.bnrdo.databrowser.mvc.DataBrowserModel;

public class DBroUtil {
	
	private static org.apache.log4j.Logger log = Logger.getLogger(DBroUtil.class);
	
	public static Connection getConnection(){
		Connection con = null;
		try {
			Class.forName("org.hsqldb.jdbcDriver");
			con = DriverManager.getConnection("jdbc:hsqldb:mem:data-browser", "sa", "");
			//con = DriverManager.getConnection("jdbc:hsqldb:file:C:\\Users\\ut1p98\\Desktop\\db\\data-browser", "sa", "");
			//con = DriverManager.getConnection("jdbc:hsqldb:file:C:\\Users\\ut1p98\\Desktop\\db\\data-browser", "sa", "");
		} catch (Exception e) {
			log.error("An error occured while getting embedded database connection", e);
		}
		
		return con;
	}
	public static <E> Object[] convertPojoToObjectArray(E domain, List<String> propNamesToPut){
		Object[] retVal = new Object[propNamesToPut.size()];
		//System.out.println(propNamesToPut);
		return retVal;
	}
	/*public static String convertArrayToString(String[] array){
		for(String s : array){
			
		}
	}*/
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
	
	public static String moneyFormat(int money){
		return moneyFormat(Integer.toString(money));
	}
	
	public static String moneyFormat(double money){
		DecimalFormat formatter = new DecimalFormat("#,###");
		return formatter.format(money);
	}
	
	public static void logPropertyChange(Logger log, Object oldVal, Object newVal, ModelField propertyName){
		if(oldVal == null && newVal != null){
			log.debug("New " + propertyName + " value set [old = null, new = " + newVal.toString() + "]. Notifying the model listeners...");
			return;
		}else if(oldVal != null && newVal == null){
			log.debug("New " + propertyName + " value set [old = " + oldVal.toString() + ", new = null]. Notifying the model listeners...");
			return;
		}else if(oldVal == null && newVal == null){
			log.debug("Same value of old " + propertyName + " set [old = null, new = null]. Model listeners will not be notified");
			return;
		}else if(oldVal != null && newVal != null){
			if(newVal.equals(oldVal)){
				log.debug("Same value of old " + propertyName + " set [old = " + oldVal.toString() + ", new = " + newVal.toString() + "]. Model listeners will not be notified");
			}else{
				log.debug("New " + propertyName + " value set [old = " + oldVal.toString() + ", new = " + newVal.toString() + "]. Notifying the model listeners...");
			}
		}
	}
	public static Object[] injectElementInArrayAt(Object[] array, Object toInsert, int at){
		Object[] retVal = new Object[array.length + 1];
		retVal[at] = toInsert;
		int i = 0;
		for(Object obj : array){
			if(i != at)
				retVal[i] = obj;
			
			i++;
		}
		
		return retVal;
	}
	
	public static void logPropertyChange(Logger log, int oldVal, int newVal, ModelField propertyName){
		logPropertyChange(log, Integer.toString(oldVal), Integer.toString(newVal), propertyName);
	}
	
	public static void logPropertyChange(Logger log, boolean oldVal, boolean newVal, ModelField propertyName){
		logPropertyChange(log, Boolean.toString(oldVal), Boolean.toString(newVal), propertyName);
	}
}
