package com.bnrdo.databrowser.mvc;

import java.beans.PropertyChangeListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.swing.event.SwingPropertyChangeSupport;

import com.bnrdo.databrowser.ColumnInfoMap;
import com.bnrdo.databrowser.DBroUtil;
import com.bnrdo.databrowser.DataType;
import com.bnrdo.databrowser.Pagination;
import com.bnrdo.databrowser.TableDataSourceFormat;
import com.bnrdo.databrowser.exception.ModelException;

public class DataBrowserModel<E> {
	
	public static final String FN_DATA_TABLE_SOURCE_EXPOSED = "dataTableSourceExposed";
	public static final String FN_DATA_TABLE_SOURCE = "dataTableSource";
	public static final String FN_DATA_TABLE_SOURCE_FORMAT = "tableDataSourceFormat";
	public static final String FN_COL_INFO_MAP = "colInfoMap";
	public static final String FN_PAGINATION = "pagination";
	public static final String FN_SORT_ORDER = "sortOrder";

	public static final String SORT_ASC = "ASC";
	public static final String SORT_DESC = "DESC";

	private Pagination pagination;
	private List<E> dataTableSourceExposed;
	private TableDataSourceFormat<E> tableDataSourceFormat;
	private ColumnInfoMap colInfoMap;

	private SwingPropertyChangeSupport propChangeFirer;

	private String sortOrder;
	private String sortCol;
	private DataType sortType;
	private int dataSourceRowCount;

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
		
		Connection connection = null;
		Statement statement = null;

		try {
			Class.forName("org.hsqldb.jdbcDriver");

			connection = DriverManager.getConnection(
					"jdbc:hsqldb:mem:data-browser", "sa", "");
			statement = connection.createStatement();

			// table name = data_browser_persist
			String createTableQry = DBroUtil
					.translateColInfoMapToCreateDbQuery(colInfoMap);
			statement.execute(createTableQry);

			dataSourceRowCount = DBroUtil.populateTable(statement, list, tableDataSourceFormat);
	    	System.out.println(dataSourceRowCount + " rows successfully inserted.");

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				statement.close();
				connection.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

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

	public List<E> getScrolledSource(int from, int to){
		 List<E> retVal = new ArrayList<E>();
		 
		 Connection connection = null;  
		 Statement statement = null;  
		 ResultSet rs = null;
		 
		 try {  
			 Class.forName("org.hsqldb.jdbcDriver");  

			 connection = DriverManager.getConnection("jdbc:hsqldb:mem:data-browser", "sa", "");
			 statement = connection.createStatement(); 
			 
			 if(sortCol == null || sortOrder == null || sortType == null){
				 sortCol = colInfoMap.getPropertyName(0);
				 sortOrder = SORT_ASC;
				 sortType = colInfoMap.getPropertyType(0);
			 }
			 
			 rs = statement.executeQuery("SELECT * FROM data_browser_persist " + DBroUtil.getSortQryChunk(sortCol, sortOrder, sortType) + " LIMIT " + (to - from) + " OFFSET " + from + "");
			 
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
				statement.close();   
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

	public String getSortOrder() {
		return sortOrder;
	}

	public void setSort(String colToSort, String order, DataType type) {
		String oldVal = sortOrder;
		sortOrder = order;
		sortCol = colToSort;
		sortType = type;
		propChangeFirer.firePropertyChange(FN_SORT_ORDER, oldVal, order);
	}

	public void addModelListener(PropertyChangeListener prop) {
		propChangeFirer.addPropertyChangeListener(prop);
	}
}
