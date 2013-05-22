package com.bnrdo.databrowser;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;

public class DataBrowserUtil {
	public static Object[] extractColNamesFromMap(Multimap<Integer, Object> map){
		Set<Integer> keys = map.keySet();
		Object[] retVal = new Object[keys.size()];
		int i = 0;
		
		for(Integer s : map.keySet()){
			retVal[i] = map.get(s).iterator().next();
			i++;
		}
		
		return retVal;
	}
	
	public static <E> Object[] convertPojoToObjectArray(E domain, List<String> propNamesToPut){
		Object[] retVal = new Object[propNamesToPut.size()];
		System.out.println(propNamesToPut);
		return retVal;
	}
	
	//colIndex 0 == key
	public static <E> List<String> getMultimapColumnAsList(int colIndex, Multimap<E, Object> map){
		List<String> retVal = new ArrayList<String>();
		
		if(colIndex == 0){
			Iterator r = map.keySet().iterator();
			
			while(r.hasNext()){
				retVal.add(r.next().toString());
			}
		}else{
			for(E key : map.keySet()){
				Iterator r = map.get(key).iterator();
				int i = 0;
				
				while(r.hasNext()){
					if(colIndex == i+1){
						retVal.add(r.next().toString());
					}
					i++;
				}
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
}
