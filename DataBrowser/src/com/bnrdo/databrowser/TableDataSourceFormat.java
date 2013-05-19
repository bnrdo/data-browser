package com.bnrdo.databrowser;

import com.bnrdo.databrowser.domain.Person;

public interface TableDataSourceFormat<E>{
	String getValueAt(int index, E p);
	int getColumnCount();
}
