package com.bnrdo.databrowser.mvc;

import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.Random;

import javax.swing.event.SwingPropertyChangeSupport;

import com.bnrdo.databrowser.ColumnInfoMap;
import com.bnrdo.databrowser.DataBrowserUtil;
import com.bnrdo.databrowser.Pagination;
import com.bnrdo.databrowser.TableDataSourceFormat;
import com.bnrdo.databrowser.exception.ModelException;

@SuppressWarnings("unchecked")
public class DataBrowserModel<E> {

    public static final String FN_DATA_TABLE_SOURCE_EXPOSED = "dataTableSourceExposed";
    public static final String FN_DATA_TABLE_SOURCE = "dataTableSource";
    public static final String FN_DATA_TABLE_SOURCE_FORMAT = "tableDataSourceFormat";
    public static final String FN_COL_INFO_MAP = "colInfoMap";
    public static final String FN_PAGINATION = "pagination";
    
    public static final int SORT_ASC = 0;
    public static final int SORT_DESC = 1;

    private Pagination pagination;
    private List<E> dataTableSource;
    private List<E> dataTableSourceExposed;
    private TableDataSourceFormat<E> tableDataSourceFormat;
    private ColumnInfoMap colInfoMap;

    private SwingPropertyChangeSupport propChangeFirer;
    
    private int sortOrder;

    public DataBrowserModel() {
        propChangeFirer = new SwingPropertyChangeSupport(this);
        sortOrder = SORT_ASC;
    }

	public void setPagination(Pagination p) {
		/*if(dataTableSource == null || tableDataSourceFormat == null || colInfoMap == null){
			throw new ModelException("You should completely setup the table's data source before setting it's pagination.");
		}
		
		int srcSize = dataTableSource.size();
		int itemsPerPage = p.getItemsPerPage();
		int pageCountForEvenSize = (srcSize / itemsPerPage);
		
		p.setTotalPageCount((srcSize % itemsPerPage) == 0 ? pageCountForEvenSize : pageCountForEvenSize + 1);
		*/
		
		/*Pagination oldVal = pagination;
		pagination = p;
		pagination.setTotalPageCount(DataBrowserUtil.getPageCount(dataTableSource.size(), p.getItemsPerPage()));
    	propChangeFirer.firePropertyChange(FN_PAGINATION, oldVal, pagination);
    	*/
		pagination = p;
	}
	
	public void setDataTableSource(List<E> list){
		/*if(colInfoMap == null || tableDataSourceFormat == null || pagination == null){
			dataTableSource = list;
		}else{
			List<E> oldVal = dataTableSource;
			dataTableSource = list;
			propChangeFirer.firePropertyChange(FN_DATA_TABLE_SOURCE, oldVal, list);
		}*/
		if(colInfoMap == null || tableDataSourceFormat == null || pagination == null){
			dataTableSource = list;
		}else{
			List<E> oldVal = dataTableSource;
			dataTableSource = list;
			propChangeFirer.firePropertyChange(FN_DATA_TABLE_SOURCE, oldVal, list);
		}
	}
	
	public void setDataTableSourceExposed(List<E> list) {
		List<E> oldVal = dataTableSourceExposed;
		dataTableSourceExposed = list;
		propChangeFirer.firePropertyChange(FN_DATA_TABLE_SOURCE_EXPOSED, oldVal, list);
	}
	
	public List<E> getDataTableSourceExposed(){
		return dataTableSourceExposed;
	}
	
	public void setColInfoMap(ColumnInfoMap map){
		if(dataTableSource == null || tableDataSourceFormat == null){
			colInfoMap = map;
		}else{
			ColumnInfoMap oldVal = colInfoMap;
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
	
	public ColumnInfoMap getColInfoMap(){
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
	
	public int getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(int sortOrder) {
		this.sortOrder = sortOrder;
	}

	public void addModelListener(PropertyChangeListener prop) {
        propChangeFirer.addPropertyChangeListener(prop);
    }
}
