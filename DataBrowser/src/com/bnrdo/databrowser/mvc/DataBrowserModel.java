package com.bnrdo.databrowser.mvc;

import java.beans.PropertyChangeListener;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.swing.event.SwingPropertyChangeSupport;

import com.bnrdo.databrowser.ColumnInfoMap;
import com.bnrdo.databrowser.Constants.SORT_ORDER;
import com.bnrdo.databrowser.Constants.SQL_TYPE;
import com.bnrdo.databrowser.DBroUtil;
import com.bnrdo.databrowser.Pagination;
import com.bnrdo.databrowser.TableDataSourceFormat;

public class DataBrowserModel<E> {
	
	public static final String FN_DATA_TABLE_SOURCE_EXPOSED = "dataTableSourceExposed";
	public static final String FN_DATA_TABLE_SOURCE = "dataTableSource";
	public static final String FN_DATA_TABLE_SOURCE_FORMAT = "tableDataSourceFormat";
	public static final String FN_COL_INFO_MAP = "colInfoMap";
	public static final String FN_PAGINATION = "pagination";
	public static final String FN_SORT_ORDER = "sortOrder";

	private Pagination pagination;
	private List<E> dataTableSourceExposed;
	private TableDataSourceFormat<E> tableDataSourceFormat;
	private ColumnInfoMap colInfoMap;

	private SwingPropertyChangeSupport propChangeFirer;

	private SORT_ORDER sortOrder;
	private SQL_TYPE sortType;
	private String sortCol;	
	private String filterCol;
	private String filterKey;
	private int dataSourceRowCount;

	private String QRY_TEMPLATE = "SELECT * FROM data_browser_persist " +
	 		"WHERE filter_col like '%filter_key'" + 
	 		"ORDER BY CAST(col_name AS sort_type) sort_order " +
	 		"LIMIT limit_count " +
	 		"OFFSET offset_count";
	
	public DataBrowserModel() {
		propChangeFirer = new SwingPropertyChangeSupport(this);
		dataSourceRowCount = 0;
	}

	public void setPagination(Pagination p) {
		Pagination oldVal = pagination;
		pagination = p;
		propChangeFirer.firePropertyChange(FN_PAGINATION, oldVal, pagination);
	}

	public void setDataTableSource(List<E> list) {
		
		Connection conn = null;
		Statement statement = null;

		try {
			conn = DBroUtil.getConnection();
			statement = conn.createStatement();

			// table name = data_browser_persist
			statement.execute("DROP TABLE IF EXISTS DATA_BROWSER_PERSIST;");
			statement.execute(DBroUtil.translateColInfoMapToCreateDbQuery(colInfoMap));

			dataSourceRowCount = DBroUtil.populateTable(statement, list, tableDataSourceFormat);
	    	System.out.println(dataSourceRowCount + " rows successfully inserted.");

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				statement.close();
				conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		//set the value for filter col and filter key. at the first datasource set,
		//filter should be in the first column and should return all the data ('%')
		filterCol = colInfoMap.getPropertyName(0);
		filterKey = "";

		// realize the pagination info after setting the base datasource..
		// convention over configuration, if pagination is null provide default vals
		Pagination p = new Pagination();
		p.setCurrentPageNum(Pagination.FIRST_PAGE);
		p.setMaxExposableCount(10);
		p.setItemsPerPage(10);

		int srcSize = list.size();
		int itemsPerPage = p.getItemsPerPage();
		int pageCountForEvenSize = (srcSize / itemsPerPage);

		p.setTotalPageCount((srcSize % itemsPerPage) == 0 ? pageCountForEvenSize
				: pageCountForEvenSize + 1);

		setPagination(p);
	}
	public int getDataSourceRowCount(){
		return dataSourceRowCount;
	}
	
	public List<E> getFilteredSource(String key, String colFilter){
		List<E> retVal = new ArrayList<E>();
		
		return retVal;
	}

	public List<E> getScrolledSource(int from, int to){
		 List<E> retVal = new ArrayList<E>();
		
		 Connection conn = null;  
		 Statement statement = null;  
		 ResultSet rs = null;
		 
		 try {  
			 conn = DBroUtil.getConnection();
			 statement = conn.createStatement(); 
			 
			 if(sortCol == null || sortOrder == null || sortType == null){
				 sortCol = colInfoMap.getPropertyName(0);
				 sortOrder = SORT_ORDER.ASC;
				 sortType = colInfoMap.getPropertyType(0);
			 }
			 
			 String qry = QRY_TEMPLATE;
			 
			 if(sortType == SQL_TYPE.STRING){
				 qry = qry.replace("CAST(col_name AS sort_type)", sortCol);	 
			 }else{
				 qry = qry.replace("col_name", sortCol);			 
				 qry = qry.replace("sort_type", sortType.toString());
			 }
			 qry = qry.replace("sort_order", sortOrder.toString());
			 qry = qry.replace("limit_count", Integer.toString((to - from)));
			 qry = qry.replace("offset_count", Integer.toString(from));
			 qry = qry.replace("filter_col", filterCol);
			 qry = qry.replace("filter_key", filterKey);
			 
			 System.out.println("query is : " + qry);
			 rs = statement.executeQuery(qry);
			 
			 while(rs.next()){
	         	List<String> cont = new ArrayList<String>();
	         	cont.add(rs.getString(1));
	         	cont.add(rs.getString(2));
	         	cont.add(rs.getString(3));
	         	cont.add(rs.getString(4));
	         	cont.add(rs.getString(5));
	         	retVal.add(tableDataSourceFormat.extractEntityFromList(cont));
	         }
		 } catch (Exception e) {  
			 e.printStackTrace();  
		 } finally {  
			 try {
				 rs.close();
				 statement.close();
				 conn.close();
			 } catch (Exception e) {
				 e.printStackTrace();
			 }
		 } 
		 
         return retVal;
	}

	public void setDataTableSourceExposed(List<E> list) {
		List<E> oldVal = dataTableSourceExposed;
		dataTableSourceExposed = list;
		propChangeFirer.firePropertyChange(FN_DATA_TABLE_SOURCE_EXPOSED,
				oldVal, list);
	}

	public List<E> getDataTableSourceExposed() {
		return dataTableSourceExposed;
	}

	public void setColInfoMap(ColumnInfoMap map) {
		if (tableDataSourceFormat == null) {
			colInfoMap = map;
		} else {
			ColumnInfoMap oldVal = colInfoMap;
			colInfoMap = map;
			propChangeFirer.firePropertyChange(FN_COL_INFO_MAP, oldVal, map);
		}
	}

	public void setTableDataSourceFormat(TableDataSourceFormat<E> fmt) {
		if (colInfoMap == null) {
			tableDataSourceFormat = fmt;
		} else {
			TableDataSourceFormat<E> oldVal = tableDataSourceFormat;
			tableDataSourceFormat = fmt;
			propChangeFirer.firePropertyChange(FN_DATA_TABLE_SOURCE_FORMAT, oldVal, fmt);
		}
	}

	public ColumnInfoMap getColInfoMap() {
		return colInfoMap;
	}

	public TableDataSourceFormat<E> getTableDataSourceFormat() {
		return tableDataSourceFormat;
	}
	
	public Pagination getPagination() {
		return pagination;
	}

	public SORT_ORDER getSortOrder() {
		return sortOrder;
	}

	public void setSort(String colToSort, SORT_ORDER order, SQL_TYPE type) {
		SORT_ORDER oldVal = sortOrder;
		sortOrder = order;
		sortCol = colToSort;
		sortType = type;
		propChangeFirer.firePropertyChange(FN_SORT_ORDER, oldVal, order);
	}

	public void setFilterCol(String col) {
		filterCol = col;
	}

	public void setFilterKey(String key) {
		filterKey = key;
	}

	public void addModelListener(PropertyChangeListener prop) {
		propChangeFirer.addPropertyChangeListener(prop);
	}
}
