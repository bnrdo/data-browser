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
	private List<E> dataTableSourceExposed;
	private TableDataSourceFormat<E> tableDataSourceFormat;
	private ColumnInfoMap colInfoMap;

	private SwingPropertyChangeSupport propChangeFirer;

	private int sortOrder;
	private int dataSourceRowCount;

	public DataBrowserModel() {
		propChangeFirer = new SwingPropertyChangeSupport(this);
		sortOrder = SORT_ASC;
		dataSourceRowCount = 0;
	}

	public void setPagination(Pagination p) {
		/*
		 * if(dataTableSource == null || tableDataSourceFormat == null ||
		 * colInfoMap == null){ throw new ModelException(
		 * "You should completely setup the table's data source before setting it's pagination."
		 * ); }
		 * 
		 * int srcSize = dataTableSource.size(); int itemsPerPage =
		 * p.getItemsPerPage(); int pageCountForEvenSize = (srcSize /
		 * itemsPerPage);
		 * 
		 * p.setTotalPageCount((srcSize % itemsPerPage) == 0 ?
		 * pageCountForEvenSize : pageCountForEvenSize + 1);
		 */

		Pagination oldVal = pagination;
		pagination = p;
		propChangeFirer.firePropertyChange(FN_PAGINATION, oldVal, pagination);

		// pagination = p;
	}

	public void setDataTableSource(List<E> list) {
		/*
		 * if(colInfoMap == null || tableDataSourceFormat == null || pagination
		 * == null){ dataTableSource = list; }else{ List<E> oldVal =
		 * dataTableSource; dataTableSource = list;
		 * propChangeFirer.firePropertyChange(FN_DATA_TABLE_SOURCE, oldVal,
		 * list); }
		 */
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
		// convention over configuration, if pagination is null provide default
		// vals
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
		/*
		 * if(colInfoMap == null || tableDataSourceFormat == null || pagination
		 * == null){ dataTableSource = list; }else{ List<E> oldVal =
		 * dataTableSource; dataTableSource = list;
		 * propChangeFirer.firePropertyChange(FN_DATA_TABLE_SOURCE, oldVal,
		 * list); }
		 */
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

			 rs = statement.executeQuery("SELECT * FROM data_browser_persist ORDER BY CAST(AGE AS INTEGER) LIMIT " + (to - from) + " OFFSET " + from + "");
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
			propChangeFirer.firePropertyChange(FN_DATA_TABLE_SOURCE_FORMAT,
					oldVal, fmt);
		}
	}

	public ColumnInfoMap getColInfoMap() {
		return colInfoMap;
	}

	public TableDataSourceFormat<E> getTableDataSourceFormat() {
		return tableDataSourceFormat;
	}

	/*
	 * public List<E> getDataTableSource(){ return dataTableSource; }
	 */
	public Pagination getPagination() {
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
