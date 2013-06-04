package com.bnrdo.databrowser;

import java.util.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ColumnInfoMap {
	
	private Map<Integer,List<String>> map;
	
	public ColumnInfoMap(){
		map = new HashMap<Integer, List<String>>();
	}
	public void putColumnName(int index, String colName){
		List<String> list = map.get(index);
		
		if(list == null){
			List<String> newList =new ArrayList<String>();
			newList.add(colName);
			newList.add("");
			newList.add("");
			map.put(index, newList);
		}else{
			list.set(0, colName);
		}
		
	}
	public void putPropertyName(int index, String propName){
		List<String> list = map.get(index);
		
		if(list == null){
			List<String> newList =new ArrayList<String>();
			newList.add("");
			newList.add(propName);
			newList.add("");
			map.put(index, newList);
		}else{
			list.set(1, propName);
		}
	}
	public void putPropertyClass(int index, Class clazz){
		List<String> list = map.get(index);
		String classStr = clazz.toString();
		
		if(list == null){
			List<String> newList =new ArrayList<String>();
			newList.add("");
			newList.add("");
			newList.add(classStr);
			map.put(index, newList);
		}else{
			list.set(2, classStr);
		}
	}
	public String getColumnName(Integer index){
		return map.get(index).get(0);
	}
	public String getPropertyName(Integer index){
		return map.get(index).get(1);
	}
	public Class getPropertyClass(int index){
		Class retVal = Object.class;
		String propSet = map.get(index).get(2); 
		if(propSet.contains("String")){
			retVal = String.class;
		}else if(propSet.contains("Integer")){
			retVal = Integer.class;
		}else if(propSet.contains("Double")){
			retVal = Double.class;
		}else if(propSet.contains("Date")){
			retVal = Date.class;
		}
		return retVal;
	}
	public String[] getColumnNames(){
		String[] retVal = new String[map.size()];
		int ctr = 0;
		
		for(Integer i : map.keySet()){
			List<String> ele = map.get(i);
			retVal[ctr++] = ele.get(0);
		}
		
		return retVal;
	}
	public String[] getPropertyNames(){
		String[] retVal = new String[map.size()];
		int ctr = 0;
		
		for(Integer i : map.keySet()){
			List<String> ele = map.get(i);
			retVal[ctr++] = ele.get(1);
		}
		
		return retVal;
	}
	public Set<Integer> getKeySet(){
		return map.keySet();
	}
	
	public boolean hasIndex(Integer i){
		if(map.containsKey(i))
			return true;
		
		return false;
	}
}
