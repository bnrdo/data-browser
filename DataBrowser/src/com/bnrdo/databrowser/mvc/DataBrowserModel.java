package com.bnrdo.databrowser.mvc;

import java.beans.PropertyChangeListener;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingWorker;
import javax.swing.event.SwingPropertyChangeSupport;

import com.bnrdo.databrowser.ColumnInfoMap;
import com.bnrdo.databrowser.Constants.SORT_ORDER;
import com.bnrdo.databrowser.Constants.SQL_TYPE;
import com.bnrdo.databrowser.AppStat;
import com.bnrdo.databrowser.Constants;
import com.bnrdo.databrowser.DBroUtil;
import com.bnrdo.databrowser.Pagination;
import com.bnrdo.databrowser.TableDataSourceFormat;
import com.bnrdo.databrowser.exception.ModelException;

public class DataBrowserModel<E> {

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
	private int recordLimit;
	private int recordOffset;
	
	private boolean isDataForTableLoading;

	private String QRY_TEMPLATE;
	private String QRY_RECORD_COUNT;
	
	private Connection dsConn;

	public DataBrowserModel() {
		propChangeFirer = new SwingPropertyChangeSupport(this);
		dataSourceRowCount = 0;
		recordLimit = 0;
		recordOffset = 0;
		isDataForTableLoading = false;
	}

	public void setPagination(Pagination p) {
		Pagination oldVal = pagination;
		pagination = p;
		propChangeFirer.firePropertyChange(Constants.ModelFields.FN_PAGINATION, oldVal, pagination);
	}
	
	public void setDataTableSource(Connection conn, String tblName){
		StringBuilder colsBdr = new StringBuilder();
		
		for(String col : colInfoMap.getPropertyNames()){
			colsBdr.append(col).append(", ");
		}
		colsBdr.replace(colsBdr.length()-2, colsBdr.length(), "");
		
		//for oracle
		QRY_TEMPLATE = "SELECT " + colsBdr.toString() + " FROM (SELECT " + colsBdr.toString() + ", row_number() over (ORDER BY col_name sort_order) rnk FROM " + tblName + ") WHERE rnk BETWEEN offset_count AND limit_count";
		/*QRY_TEMPLATE = "SELECT " + bdr.toString() + " FROM " + tblName + " "
				+ "WHERE filter_col like 'filter_key%' AND ROWNUM >= offset_count AND ROWNUM <= (to_number(limit_count) + to_number(offset_count)) "
				+ "ORDER BY col_name sort_order ";
				*/
		QRY_RECORD_COUNT = "SELECT COUNT(*) FROM " + tblName + " WHERE filter_col like 'filter_key%'";
		
		sortCol = colInfoMap.getPropertyName(3);
		sortOrder = SORT_ORDER.ASC;
		sortType = colInfoMap.getPropertyType(3);
	
		filterCol = colInfoMap.getPropertyName(0);
		filterKey = "";
		
		dsConn = conn;
		
		derivePagination(getDataSourceRowCount());
	}

	public void setDataTableSource(final List<E> list) {
		
		if(colInfoMap == null){
			throw new ModelException("Column info map should be supplied before setting the data source.");
		}
		
		if(tableDataSourceFormat == null){
			throw new ModelException("Table data source format should be supplied before setting the data source..");
		}
		
		AppStat.dbVendor = AppStat.DBVendor.HSQLDB;
		
		QRY_TEMPLATE = "SELECT * FROM data_browser_persist "
						+ "WHERE filter_col like 'filter_key%' "
						+ "ORDER BY CAST(col_name AS sort_type) sort_order "
						+ "LIMIT limit_count " + "OFFSET offset_count";
		QRY_RECORD_COUNT = "SELECT COUNT(*) FROM data_browser_persist WHERE filter_col like 'filter_key%'";
		
		new SwingWorker<Void, E>() {
	        @Override
	        protected Void doInBackground() throws Exception {
	        	Connection conn = null;
	    		Statement stmt = null;
	    		
	        	try {
	    			conn = DBroUtil.getConnection();
	    			stmt = conn.createStatement();
	    			// table name = data_browser_persist
	    			stmt.execute("DROP TABLE IF EXISTS DATA_BROWSER_PERSIST;");
	    			stmt.execute("SET IGNORECASE TRUE;");
	    			stmt.execute(DBroUtil.translateColInfoMapToCreateDbQuery(colInfoMap));
	    			
	    			DBroUtil.populateTable(stmt, list, tableDataSourceFormat);
	    			
	    		} catch (Exception e) {
	    			e.printStackTrace();
	    		} finally {
	    			try {
	    				stmt.close();
	    				//conn.close();
	    			} catch (Exception e) {
	    				e.printStackTrace();
	    			}
	    		}
				return null;
	        }
	        
	        @Override
	        protected void done() {
	        	System.out.println("its done bitchachos");
	        }
	    }.execute();
		/* default values for sorting and filter
		 */
		sortCol = colInfoMap.getPropertyName(3);
		sortOrder = SORT_ORDER.ASC;
		sortType = colInfoMap.getPropertyType(3);
	
		filterCol = colInfoMap.getPropertyName(0);
		filterKey = "";
		
		derivePagination(list.size());
	}
	
	public int getDataSourceRowCount() throws ModelException{
		
		String qryCount = QRY_RECORD_COUNT;
		
		qryCount = qryCount.replace("filter_col", filterCol)
							.replace("filter_key", filterKey);
		ResultSet rs = null;
		Statement stmt = null;
		Connection con = null;

		try {
			con = (dsConn == null) ? DBroUtil.getConnection() : dsConn;
			stmt = con.createStatement(); 
			System.out.println("Count query is : " + qryCount);
			rs = stmt.executeQuery(qryCount);
			
			while(rs.next()){
				dataSourceRowCount = (int) rs.getLong(1);
				System.out.println("Data source count is : " + dataSourceRowCount);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new ModelException(e.toString());
		}
		
		System.out.println("query count is : " + qryCount);
		
		return dataSourceRowCount;
	}

	public void setFilter(String key, String colFilter) {
		String oldVal = filterKey;
		filterKey = key;
		filterCol = colFilter;
		
		/*just set the filter key and filter col to a new value, then
		 * calling derivePagination will automatically compute for the number
		 * of page numbers the filtered data needs
		 */
		derivePagination(getDataSourceRowCount());
		
		propChangeFirer.firePropertyChange(Constants.ModelFields.FN_FILTER_KEY, oldVal, key);
	}
	/* Returns list of cropped data. Data is cropped by calculating the right
	 * sql LIMIT and OFFSET to be applied in the query. For ORACLE, LIMIT and OFFSET are done
	 * using some other codes
	 * Filter and sort are also applied in the query.
	 */
	public List<E> getScrolledSource(int from, int to) throws ModelException, SQLException{
		List<E> retVal = new ArrayList<E>();

		Connection conn = null;
		Statement statement = null;
		ResultSet rs = null;

		if(AppStat.dbVendor.equals(AppStat.DBVendor.ORACLE))
			recordLimit = to;
		else if(AppStat.dbVendor.equals(AppStat.DBVendor.MYSQL) ||
				AppStat.dbVendor.equals(AppStat.DBVendor.HSQLDB))
			recordLimit = to - from;
		
		recordOffset = from;
		
		try {
			conn = (dsConn == null) ? DBroUtil.getConnection() : dsConn;
			statement = conn.createStatement();
			
			String qry = QRY_TEMPLATE;
			
			/* start fetching the scrolled list */
			if (sortType == SQL_TYPE.STRING) {
				qry = qry.replace("CAST(col_name AS sort_type)", sortCol);
			} else {
				qry = qry.replace("col_name", sortCol);
				qry = qry.replace("sort_type", sortType.toString());
			}
			
			qry = qry.replace("sort_order", sortOrder.toString())
					.replace("limit_count", Integer.toString((recordLimit)))
					.replace("offset_count", Integer.toString(recordOffset))
					.replace("filter_col", filterCol)
					.replace("filter_key", filterKey);

			System.out.println("query is : " + qry);
			
			rs = statement.executeQuery(qry);
			
			while (rs.next()) {
				List<String> cont = new ArrayList<String>();
				cont.add(rs.getString(1));
				cont.add(rs.getString(2));
				cont.add(rs.getString(3));
				cont.add(rs.getString(4));
				cont.add(rs.getString(5));
				cont.add(rs.getString(6));
				cont.add(rs.getString(7));
				cont.add(rs.getString(8));
				cont.add(rs.getString(9));
				cont.add(rs.getString(10));
				cont.add(rs.getString(11));
				cont.add(rs.getString(12));
				cont.add(rs.getString(13));
				retVal.add(tableDataSourceFormat.extractEntityFromList(cont));
			}
			
		} catch (Exception e) {
			System.out.println(e.toString());
			e.printStackTrace();
			throw new ModelException(e.toString());
		} finally {
			try {
				rs.close();
				statement.close();
				//conn.close();
			} catch (Exception e) {
				//throw new ModelException(e.toString());
			}
		}

		return retVal;
	}

	public void setDataTableSourceExposed(List<E> list) {
		List<E> oldVal = dataTableSourceExposed;
		dataTableSourceExposed = list;
		propChangeFirer.firePropertyChange(Constants.ModelFields.FN_DATA_TABLE_SOURCE_EXPOSED, oldVal, list);
	}
	
	public void setDataForTableLoading(boolean bool) {
		boolean oldVal = isDataForTableLoading;
		isDataForTableLoading = bool;
		propChangeFirer.firePropertyChange(Constants.ModelFields.FN_IS_TABLE_LOADING, oldVal, bool);
	}

	public List<E> getDataTableSourceExposed() {
		return dataTableSourceExposed;
	}

	public void setColInfoMap(ColumnInfoMap map) {
		ColumnInfoMap oldVal = colInfoMap;
		colInfoMap = map;
		propChangeFirer.firePropertyChange(Constants.ModelFields.FN_COL_INFO_MAP, oldVal, map);
	}

	public void setTableDataSourceFormat(TableDataSourceFormat<E> fmt) {
		if (colInfoMap == null) {
			tableDataSourceFormat = fmt;
		} else {
			TableDataSourceFormat<E> oldVal = tableDataSourceFormat;
			tableDataSourceFormat = fmt;
			propChangeFirer.firePropertyChange(Constants.ModelFields.FN_DATA_TABLE_SOURCE_FORMAT,
					oldVal, fmt);
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
		propChangeFirer.firePropertyChange(Constants.ModelFields.FN_SORT_ORDER, oldVal, order);
	}

	public void addModelListener(PropertyChangeListener prop) {
		propChangeFirer.addPropertyChangeListener(prop);
	}
	
	/* This method is for adjusting the pagination when the query changes.
	 */
	private void derivePagination(int srcSize){
		Pagination p = new Pagination();
		p.setCurrentPageNum(Pagination.FIRST_PAGE);
		p.setMaxExposableCount(7);
		p.setItemsPerPage(10);

		int itemsPerPage = p.getItemsPerPage();
		int pageCountForEvenSize = (srcSize / itemsPerPage);
		int totalPageCount = (srcSize % itemsPerPage) == 0 ? pageCountForEvenSize : pageCountForEvenSize + 1; 
		p.setTotalPageCount(totalPageCount);

		setPagination(p);
	}
}
