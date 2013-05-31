package com.bnrdo.databrowser.comparator;

import java.util.Comparator;

public class StringComparator implements Comparator<String> {

	public int compare(String str1, String str2) {
		return str1.compareTo(str2);
	}

}