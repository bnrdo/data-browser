package com.bnrdo.databrowser.mvc;

import java.beans.PropertyChangeListener;
import java.util.List;
import javax.swing.event.SwingPropertyChangeSupport;
import com.bnrdo.databrowser.Pagination;
import com.bnrdo.databrowser.TableDataSourceFormat;
import com.google.common.collect.Multimap;

@SuppressWarnings("unchecked")
public class DataBrowserModel<E> {

    public static final String FN_DATA_TABLE_SOURCE = "dataTableSource";
    public static final String FN_DATA_TABLE_SOURCE_FORMAT = "tableDataSourceFormat";
    public static final String FN_COL_INFO_MAP = "colInfoMap";
    public static final String FN_PAGINATION = "pagination";

    private Pagination pagination;
    private List<E> dataTableSource;
    private TableDataSourceFormat<E> tableDataSourceFormat;
    private Multimap<Integer, Object> colInfoMap;

    private SwingPropertyChangeSupport propChangeFirer;

    public DataBrowserModel() {
        propChangeFirer = new SwingPropertyChangeSupport(this);
        pagination = new Pagination();
    }

	public void setPagination(Pagination p) {
		Pagination oldVal = pagination;
		pagination = p;
    	propChangeFirer.firePropertyChange(FN_PAGINATION, oldVal, p);
	}
	
	public void setDataTableSource(List<E> list) {
		if(colInfoMap == null || tableDataSourceFormat == null){
			dataTableSource = list;
		}else{
			List<E> oldVal = dataTableSource;
			dataTableSource = list;
			propChangeFirer.firePropertyChange(FN_DATA_TABLE_SOURCE, oldVal, list);
		}
	}
	
	public void setColInfoMap(Multimap<Integer, Object> map){
		if(dataTableSource == null || tableDataSourceFormat == null){
			colInfoMap = map;
		}else{
			Multimap<Integer, Object> oldVal = colInfoMap;
			colInfoMap = map;
			propChangeFirer.firePropertyChange(FN_COL_INFO_MAP, oldVal, map);
		}
	}
	
	public void setTableDataSourceFormat(TableDataSourceFormat<E> fmt) {
		if(dataTableSource == null || colInfoMap == null){
			tableDataSourceFormat = fmt;
		}else{
			TableDataSourceFormat<E> oldVal = tableDataSourceFormat;
			tableDataSourceFormat = fmt;
			propChangeFirer.firePropertyChange(FN_DATA_TABLE_SOURCE_FORMAT, oldVal, fmt);
		}		
	}
	
	public Multimap<Integer, Object> getColInfoMap(){
		return colInfoMap;
	}
	
	public TableDataSourceFormat<E> getTableDataSourceFormat() {
		return tableDataSourceFormat;
	}
	
	public List<E> getDataTableSource(){
		return dataTableSource;
	}
	
	public Pagination getPagination(){
		return pagination;
	}
	
	public void addModelListener(PropertyChangeListener prop) {
        propChangeFirer.addPropertyChangeListener(prop);
    }
}
