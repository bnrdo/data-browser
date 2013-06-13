package com.bnrdo.databrowser;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import com.bnrdo.databrowser.Constants.SORT_ORDER;
import com.bnrdo.databrowser.Constants.SQL_TYPE;
import com.bnrdo.databrowser.comparator.DateComparator;
import com.bnrdo.databrowser.comparator.IntegerComparator;
import com.bnrdo.databrowser.comparator.StringComparator;

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
	
	/*
	public static <E> void populateTable(Connection con, List<E> source, TableDataSourceFormat<E> fmt) throws SQLException{
		/*int countForLog = 0;
		
		StringBuilder bdr = new StringBuilder();
		bdr.append("INSERT INTO data_browser_persist VALUES (");
		for(int i = 0; i < fmt.getColumnCount(); i++){
			bdr.append("?, ");
		}
		bdr.replace(bdr.length()-2, bdr.length(), "");
		bdr.append(")");
		
		PreparedStatement pStmt = con.prepareStatement(bdr.toString());;
		
		for(E e : source){
			for(int i = 0; i < fmt.getColumnCount(); i++){
				pStmt.setString(i+1, fmt.getValueAt(i, e));
			}
			pStmt.addBatch();
			if(countForLog % 1000 == 2 || countForLog == source.size()){
				pStmt.executeBatch();
			}
    		countForLog++;
    	}
		con.commit();
		System.out.println(countForLog + " rows succesfully inserted!");
    }
	*/
	public static String makeCSVFromArray(String[] arr){
		StringBuilder bdr = new StringBuilder();
		
		for(String s : arr){
			bdr.append(s).append(", ");
		}
		
		bdr.replace(bdr.length()-2, bdr.length(), "");
		
		return bdr.toString();
	}
	public static <E> void populateTable(Statement stmt, List<E> source, TableDataSourceFormat<E> fmt) throws SQLException{
		int countForLog = 0;
		StringBuilder bdr = new StringBuilder();
		bdr.append("INSERT INTO data_browser_persist VALUES ");
		
		for(E e : source){
			bdr.append("(");
    		for(int i = 0; i < fmt.getColumnCount(); i++){
    			bdr.append("'").append(fmt.getValueAt(i, e)).append("', ");
    			//System.out.print(fmt.getValueAt(i, e) + " ");
    		}
    		//System.out.println();
    		bdr.replace(bdr.length()-2, bdr.length(), "");
    		bdr.append("), ");
    		
    		countForLog++;
    		
    		if(countForLog % 1000 == 0 || countForLog == source.size()){
    			bdr.replace(bdr.length()-2, bdr.length(), "");
    			stmt.executeUpdate(bdr.toString());
    			bdr = new StringBuilder();
    			bdr.append("INSERT INTO data_browser_persist VALUES ");
    		}
    		source.remove(e);
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
    	
    	retVal.append("CREATE CACHED TABLE data_browser_persist ( ");
    	
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
