package com.bnrdo.databrowser;

import java.util.Map;
import java.util.Set;

import com.google.common.collect.Multimap;

public class DataBrowserUtil {
	public static Object[] extractColNamesFromMap(Multimap<Integer, Object> map){
		Set<Integer> keys = map.keySet();
		Object[] retVal = new Object[keys.size()];
		int i = 0;
		
		for(Integer s : map.keySet()){
			//retVal[i] = map.get(s)[0];
		}
		
		return retVal;
	}
	
	public static <E> Object[] convertPojoToObjectArray(E domain){
		Object[] retVal = new Object[]{"haha", "hehe", "hihi", "hoho", "huhu"};
		return retVal;
	}
}
