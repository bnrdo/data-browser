package com.bnrdo.databrowser;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.bnrdo.databrowser.comparator.DateComparator;
import com.bnrdo.databrowser.comparator.IntegerComparator;
import com.bnrdo.databrowser.comparator.StringComparator;

public class DBroUtil {
	
	
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
	
	public static <E> Object[] extractRowFromFormat(TableDataSourceFormat<E> fmt, E e){
		int colCount= fmt.getColumnCount();
		Object[] retVal= new Object[colCount];
		
		for(int i = 0; i < colCount; i++){
			retVal[i] = fmt.getValueAt(i, e);
		}
		
		return retVal;
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
	
	public static <E> int populateTable(Statement stmt, List<E> source, TableDataSourceFormat<E> fmt) throws SQLException{
		int rowsInserted = 0;
		
    	for(E e : source){
    		StringBuilder bdr = new StringBuilder();
    		bdr.append("INSERT INTO data_browser_persist VALUES ('");
    		for(int i = 0; i < fmt.getColumnCount(); i++){
    			bdr.append(fmt.getValueAt(i, e)).append("', '");
    		}
    		bdr.replace(bdr.length()-3, bdr.length(), "");
    		bdr.append(")");
    		
    		stmt.executeUpdate(bdr.toString());
    		rowsInserted++;
    	}
    	
    	return rowsInserted;
    }
    public static String translateColInfoMapToCreateDbQuery(ColumnInfoMap colInfoMap){
    	StringBuilder retVal = new StringBuilder();
    	boolean primarySet = false;
    	
    	retVal.append("CREATE MEMORY TABLE data_browser_persist ( ");
    	
    	for(Integer i : colInfoMap.getKeySet()){
    		if(colInfoMap.hasIndex(i)){
    			retVal.append(colInfoMap.getPropertyName(i));
    			if(!primarySet){
    				retVal.append(" varchar(256) not null primary key, ");
    				primarySet = true;
    			}else{
    				retVal.append(" varchar(256) not null, ");
    			}
    		}
    	}
    	retVal.replace(retVal.length()-2, retVal.length(), "");
    	retVal.append(" );");
    	
    	return retVal.toString();
    }
}
