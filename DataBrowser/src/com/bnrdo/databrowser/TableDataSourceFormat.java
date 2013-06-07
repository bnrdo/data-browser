package com.bnrdo.databrowser;

import java.util.List;

public interface TableDataSourceFormat<E>{
	String getValueAt(int index, E p);
	E extractEntityFromList(List<String> list);
	int getColumnCount();
}
