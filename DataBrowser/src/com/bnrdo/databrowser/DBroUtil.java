package com.bnrdo.databrowser;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.bnrdo.databrowser.Constants.SORT_ORDER;
import com.bnrdo.databrowser.Constants.SQL_TYPE;
import com.bnrdo.databrowser.comparator.DateComparator;
import com.bnrdo.databrowser.comparator.IntegerComparator;
import com.bnrdo.databrowser.comparator.StringComparator;

public class DBroUtil {
	
	public static Connection getConnection(){
		Connection cons = null;
		try {
			Class.forName("org.hsqldb.jdbcDriver");
			cons = DriverManager.getConnection("jdbc:hsqldb:mem:data-browser", "sa", "");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}  catch(SQLException e){
			e.printStackTrace();
		}
		return cons;
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
	
	public static <E> void populateTable(Statement stmt, List<E> source, TableDataSourceFormat<E> fmt) throws SQLException{
		int countForLog = 0;
		for(E e : source){
    		StringBuilder bdr = new StringBuilder();
    		bdr.append("INSERT INTO data_browser_persist VALUES ('");
    		for(int i = 0; i < fmt.getColumnCount(); i++){
    			bdr.append(fmt.getValueAt(i, e)).append("', '");
    			//System.out.print(fmt.getValueAt(i, e) + " ");
    		}
    		bdr.replace(bdr.length()-3, bdr.length(), "");
    		bdr.append(")");
    		
    		stmt.executeUpdate(bdr.toString());
    		countForLog++;
    	}
		System.out.println(countForLog + " rows succesfully inserted!");
    }
	
	public static String getSortQryChunk(String sortCol, SORT_ORDER sortOrder, SQL_TYPE sortType){
		StringBuilder retVal = new StringBuilder();
		retVal.append("ORDER BY "); 
		
		if(sortType.equals(SQL_TYPE.INTEGER)){
			retVal.append("CAST(").append(sortCol)
					.append(" AS ").append(sortType)
					.append(") ").append(sortOrder);;
		}else if(sortType.equals(SQL_TYPE.BIGDECIMAL)){
			retVal.append("CAST(").append(sortCol)
					.append(" AS ").append("DOUBLE")
					.append(") ").append(sortOrder);;
		}else if(sortType.equals(SQL_TYPE.BOOLEAN)){
			retVal.append("CAST(").append(sortCol)
					.append(" AS ").append(sortType)
					.append(") ").append(sortOrder);;
		}else if(sortType.equals(SQL_TYPE.TIMESTAMP)){
			retVal.append("CAST(").append(sortCol)
					.append(" AS ").append(SQL_TYPE.TIMESTAMP)
					.append(") ").append(sortOrder);;
		}else {
			retVal.append(sortCol).append(" ").append(sortOrder);
		}
		
		return retVal.toString();
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