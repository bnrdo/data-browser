package com.bnrdo.databrowser.mvc.services;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.apache.commons.lang3.mutable.MutableInt;

import com.bnrdo.databrowser.AppStat;
import com.bnrdo.databrowser.ColumnInfoMap;
import com.bnrdo.databrowser.Constants.JAVA_TYPE;
import com.bnrdo.databrowser.format.ListSourceFormat;

public class ModelService<E> {
	public void populateDBTable(String tblName, Statement stmt, List<E> source, ListSourceFormat<E> fmt, MutableInt ctr) throws SQLException{
		int countForLog = 0;
		int countForINSERTED = 0;
		StringBuilder bdr = new StringBuilder();
		bdr.append("INSERT INTO " + tblName + " VALUES ");
		
		for(E e : source){
			bdr.append("(");
    		for(int i = 0; i < fmt.getColumnCount(); i++){
    			bdr.append("'").append(fmt.getValueAt(i, e)).append("', ");
    		}
    		//System.out.println();
    		bdr.replace(bdr.length()-2, bdr.length(), "");
    		bdr.append("), ");
    		
    		countForLog++;
    		countForINSERTED++;
    		if(countForLog % 1000 == 0 || countForLog == source.size()){
    			bdr.replace(bdr.length()-2, bdr.length(), "");
    			stmt.executeUpdate(bdr.toString());
    			ctr.add(countForINSERTED);
    			
    			countForINSERTED = 0;
    			bdr = new StringBuilder();
    			bdr.append("INSERT INTO " + tblName + " VALUES ");
    		}
    		//source.remove(e);
    	}
		System.out.println(countForLog + " rows succesfully inserted!");
    }
	public String translateColInfoMapToCreateTableSQL(String tblName, ColumnInfoMap colInfoMap){
    	StringBuilder retVal = new StringBuilder();
    	
    	retVal.append("CREATE MEMORY TABLE " + tblName + " ( ");
    	
    	for(Integer i : colInfoMap.getKeySet()){
    		if(colInfoMap.hasIndex(i)){
    			retVal.append(colInfoMap.getPropertyName(i));
    			retVal.append(" " + getHSQLTypeFromSQLType(colInfoMap.getPropertyType(i)) + " not null, ");
    		}
    	}
    	retVal.replace(retVal.length()-2, retVal.length(), "");
    	retVal.append(" );");
    	
    	return retVal.toString();
    }
	private String getHSQLTypeFromSQLType(JAVA_TYPE type){
		String retVal = "VARCHAR(1000)";
		
		switch(type){
			case INTEGER:
				retVal = "INTEGER";
				break;
			case DATE:
				retVal = "DATE";
				break;
			case DATETIME:
				retVal = "TIMESTAMP";
				break;
			case BIGDECIMAL:
				retVal = "NUMERIC";
				break;
			case BOOLEAN:
				retVal = "BOOLEAN";
				break;
			case FLOAT:
				retVal = "FLOAT";
				break;
			case DOUBLE:
				retVal = "DOUBLE";
				break;
			case LONG:
				retVal = "BIGINT";
				break;
			case OBJECT:
				retVal = "OTHER";
				break;
			default:
				break;
		}
		
		return retVal;
	}
}
