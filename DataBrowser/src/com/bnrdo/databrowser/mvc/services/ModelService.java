package com.bnrdo.databrowser.mvc.services;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.apache.commons.lang3.mutable.MutableInt;

import com.bnrdo.databrowser.ColumnInfoMap;
import com.bnrdo.databrowser.TableDataSourceFormat;

public class ModelService<E> {
	public void populateTable(Statement stmt, List<E> source, TableDataSourceFormat<E> fmt, MutableInt ctr) throws SQLException{
		int countForLog = 0;
		int countForINSERTED = 0;
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
    		countForINSERTED++;
    		if(countForLog % 1000 == 0 || countForLog == source.size()){
    			bdr.replace(bdr.length()-2, bdr.length(), "");
    			stmt.executeUpdate(bdr.toString());
    			ctr.add(countForINSERTED);
    			
    			countForINSERTED = 0;
    			bdr = new StringBuilder();
    			bdr.append("INSERT INTO data_browser_persist VALUES ");
    		}
    		//source.remove(e);
    	}
		System.out.println(countForLog + " rows succesfully inserted!");
    }
	public String translateColInfoMapToCreateDbQuery(ColumnInfoMap colInfoMap){
    	StringBuilder retVal = new StringBuilder();
    	boolean primarySet = false;
    	
    	retVal.append("CREATE MEMORY TABLE data_browser_persist ( ");
    	
    	for(Integer i : colInfoMap.getKeySet()){
    		if(colInfoMap.hasIndex(i)){
    			retVal.append(colInfoMap.getPropertyName(i));
    			if(!primarySet){
    				retVal.append(" varchar(256) not null, ");
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
