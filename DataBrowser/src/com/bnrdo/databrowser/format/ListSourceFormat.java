package com.bnrdo.databrowser.format;


public interface ListSourceFormat<E>{
	int getColumnCount();
	String getValueAt(int index, E p);
}
