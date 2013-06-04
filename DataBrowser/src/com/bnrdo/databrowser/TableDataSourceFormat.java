package com.bnrdo.databrowser;

import java.sql.ResultSet;
import java.util.List;

import com.bnrdo.databrowser.domain.Person;

public interface TableDataSourceFormat<E>{
	String getValueAt(int index, E p);
	E extractEntityFromList(List<String> list);
	int getColumnCount();
}
