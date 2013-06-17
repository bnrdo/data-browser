package com.bnrdo.databrowser.mvc;

import java.beans.PropertyChangeListener;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingWorker;
import javax.swing.event.SwingPropertyChangeSupport;
import javax.swing.table.DefaultTableModel;

import org.apache.commons.lang3.mutable.MutableInt;

import com.bnrdo.databrowser.ColumnInfoMap;
import com.bnrdo.databrowser.Constants.SORT_ORDER;
import com.bnrdo.databrowser.Constants.SQL_TYPE;
import com.bnrdo.databrowser.AppStat;
import com.bnrdo.databrowser.Constants;
import com.bnrdo.databrowser.DBroUtil;
import com.bnrdo.databrowser.Filter;
import com.bnrdo.databrowser.Pagination;
import com.bnrdo.databrowser.TableDataSourceFormat;
import com.bnrdo.databrowser.exception.ModelException;
import com.bnrdo.databrowser.mvc.services.ModelService;

public class DataBrowserModel<E> {

	private Pagination pagination;
	private TableDataSourceFormat<E> tableDataSourceFormat;
	private ColumnInfoMap colInfoMap;
	private DefaultTableModel pagedTableModel;

	private SwingPropertyChangeSupport propChangeFirer;
	private ModelService<E> service;
	
	private SORT_ORDER sortOrder;
	private SQL_TYPE sortType;
	private String sortCol;
	private String sortColAsInUI;
	private Filter filter;
	private List<E> dataSource;
	
	/* used by the view to change its state, not the realtime basis of row count since
	 * query is done in background
	 */
	private int dataSourceRowCount;
	private int recordLimit;
	private int recordOffset;
	
	//for List data source, this tracks the number of records currently inserted to the embedded DB
	private MutableInt INSERTED;
	
	private boolean isDataForTableLoading;
	private boolean isDataSourceLoading;

	private String QRY_TEMPLATE;
	private String QRY_RECORD_COUNT;
	
	private Connection dsConn;

	public DataBrowserModel() {
		dataSourceRowCount = 0;
		recordLimit = 0;
		recordOffset = 0;
		isDataForTableLoading = false;
		isDataSourceLoading = false;
		propChangeFirer = new SwingPropertyChangeSupport(this);
		service = new ModelService<E>();
	}

	public void setPagination(Pagination p) {
		boolean isEqual = p.equals(pagination);
		Pagination oldVal = pagination;
		pagination = p;
		propChangeFirer.firePropertyChange(Constants.ModelFields.FN_PAGINATION, oldVal, p);
	}
	
	/* setDataTableSource and its overloaded methods are like entry points.
	 * set the default values after configuring the data source
	 */
	public void setDataTableSource(Connection conn, String tblName){
		StringBuilder colsBdr = new StringBuilder();
		
		for(String col : colInfoMap.getPropertyNames()){
			colsBdr.append(col).append(", ");
		}
		colsBdr.replace(colsBdr.length()-2, colsBdr.length(), "");
		
		//for oracle
		QRY_TEMPLATE = "SELECT " + colsBdr.toString() + 
				" FROM (SELECT " + colsBdr.toString() + 
				", row_number() over (ORDER BY col_name sort_order) rnk FROM " + tblName + 
				" WHERE upper(filter_col) LIKE upper('filter_key%')" + 
				") WHERE rnk BETWEEN offset_count AND (limit_count-1)";
		
		QRY_RECORD_COUNT = "SELECT COUNT(*) FROM " + tblName + " WHERE filter_col LIKE 'filter_key%'";
		
	    //silent set the default value for filter (should not notify the listeners)
		filter = new Filter("", colInfoMap.getPropertyName(0), colInfoMap.getColumnName(0));
		
		dsConn = conn;
		setDataSourceRowCount(queryRowCount());
		derivePagination(getDataSourceRowCount());
	}

	public void setDataTableSource(final List<E> list) {
		
		if(colInfoMap == null){
			throw new ModelException("Column info map should be supplied before setting the data source.");
		}
		
		if(tableDataSourceFormat == null){
			throw new ModelException("Table data source format should be supplied before setting the data source..");
		}
				
		AppStat.dbVendor = AppStat.DBVendor.HSQLDB_EMBEDDED;
		dataSource = list;
		INSERTED = new MutableInt(0);
		
		QRY_TEMPLATE = "SELECT * FROM data_browser_persist "
						+ "WHERE filter_col like 'filter_key%' "
						+ "ORDER BY col_name sort_order "
						+ "LIMIT limit_count " + "OFFSET offset_count";
		QRY_RECORD_COUNT = "SELECT COUNT(*) FROM data_browser_persist WHERE filter_col like 'filter_key%'";
		
		setDataSourceLoading(true);
		
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
	    			stmt.execute(service.translateColInfoMapToCreateDbQuery(colInfoMap));
	    			
	    			service.populateTable(stmt, list, tableDataSourceFormat, INSERTED);
	    			
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
	        	System.out.println("Passed data source set to null, cannot use its cached size anymore. Will use select count for getting the dssize.");
	        	dataSource = null;
	        	setDataSourceLoading(false);
	        }
	    }.execute();
	
	    //silent set the default value for filter (should not notify the listeners)
	    filter = new Filter("", colInfoMap.getPropertyName(0), colInfoMap.getColumnName(0));
	    
		setDataSourceRowCount(queryRowCount());
		derivePagination(getDataSourceRowCount());
	}
	
	public int queryRowCount() throws ModelException{
		int retVal = 0;
		
		if(AppStat.isUsingListDS() && dataSource != null){
			System.out.println("Base datasource not fully processed. Using its cached size.");
			retVal = dataSource.size();
		}else{
			String qryCount = QRY_RECORD_COUNT;
			
			qryCount = qryCount.replace("filter_col", filter.getCol())
								.replace("filter_key", filter.getKey());
			ResultSet rs = null;
			Statement stmt = null;
			Connection con = null;
	
			try {
				con = (dsConn == null) ? DBroUtil.getConnection() : dsConn;
				stmt = con.createStatement(); 
				System.out.println("Count query is : " + qryCount);
				rs = stmt.executeQuery(qryCount);
				
				while(rs.next()){
					retVal = (int) rs.getLong(1);
					System.out.println("Data source count is : " + dataSourceRowCount);
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw new ModelException(e.toString());
			}
			
			System.out.println("query count is : " + qryCount);
		}
		return retVal;
	}

	public void setFilter(Filter filtr){
		Filter oldVal = filter;
		filter = filtr;
		
		setDataSourceRowCount(queryRowCount());
		derivePagination(getDataSourceRowCount());
		
		propChangeFirer.firePropertyChange(Constants.ModelFields.FN_FILTER, oldVal, filtr);
	}
	/* Returns list of cropped data. Data is cropped by calculating the right
	 * sql LIMIT and OFFSET to be applied in the query. For ORACLE, LIMIT and OFFSET are done
	 * using some other codes
	 * Filter and sort are also applied in the query.
	 */
	public void populateTableWithScrolledSource(final int from, final int to){
		System.out.println("inside populateTableWithScrolledSource");
		
		new SwingWorker<Void, List<String>>() {
			
			private Connection conn = null;
			private Statement statement = null;
			private ResultSet rs = null;
			private ResultSetMetaData rsmd = null;
			
			boolean tblShouldClear = true ;
			
	        @Override
	        protected Void doInBackground() throws Exception {
	        	if(AppStat.dbVendor.equals(AppStat.DBVendor.ORACLE))
	        		recordLimit = to;
	        	else if(AppStat.dbVendor.equals(AppStat.DBVendor.MYSQL) ||
	        			AppStat.isUsingListDS())
	        		recordLimit = to - from;

	        	recordOffset = from;
	        	
	        	try {
	        		conn = (dsConn == null) ? DBroUtil.getConnection() : dsConn;
	        		statement = conn.createStatement();
	        		
	        		String qry = QRY_TEMPLATE;
	        		
	        		/* start fetching the scrolled list */
        			qry = qry.replace("col_name", sortCol);
        			qry = qry.replace("sort_type", sortType.toString());
	        		
	        		qry = qry.replace("sort_order", sortOrder.toString())
	        				.replace("limit_count", Integer.toString((recordLimit)))
	        				.replace("offset_count", Integer.toString(recordOffset))
	        				.replace("filter_col", filter.getCol())
	        				.replace("filter_key", filter.getKey());
	        		
	        		System.out.println("query is : " + qry);
	        		
	        		setDataForTableLoading(true);
	        		
	        		if(AppStat.isUsingListDS()){
		        		if(INSERTED.intValue() < to){
		        			System.out.println("INSERTED record size is not enough for the requested ResultSet [from : " + from + " | to : " + to +"]");
		        			
		        			while(INSERTED.intValue() < to){
		        				System.out.println("Waiting for the model to insert the requested records.");
		        				Thread.currentThread().sleep(500);
		        			}
		        		}
	        		}
	        		
	        		rs = statement.executeQuery(qry);
	        		rsmd = rs.getMetaData();
	        		
	        		while (rs.next()) {
	        			List<String> cont = new ArrayList<String>();
	        			int colCount = rsmd.getColumnCount();
	        			
	        			for(int i = 1; i <= colCount; i++){
	        				cont.add(rs.getString(i));
	        			}
	        			
	        			publish(cont);
	        		}
	        	} catch (Exception e) {
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
				return null;
	        }
	        @Override
	        protected void process(List<List<String>> chunks){
	        	if(tblShouldClear == true){
	        		/* clearing of the table should happen in the EDT but not before executing
	        		 * swing worker since executing it before swingworker will show a cleared table effect
	        		 * while data is being loaded, which is not desired. the desired behavior is 
	        		 * view#showTableLoader
	        		 */
	        		pagedTableModel.setRowCount(0);
	        		tblShouldClear = false;
	        	}
	        	for(List<String> list : chunks){
	        		System.out.println("published " + list + " to the table");
	        		pagedTableModel.addRow(list.toArray());
	        	}
	        }
	        @Override
	        protected void done() {
	        	if(tblShouldClear == true){
	        		/* in case there is no published data, the old table data wont
	        		 * be cleared because the clearing of the table data happens on
	        		 * the first publish. To avoid that, do this check again in the done()
	        		 */
	        		pagedTableModel.setRowCount(0);
	        	}
	        	
	        	setDataForTableLoading(false);
	        }
	    }.execute();
	    
	}

	
	
	public void setDataForTableLoading(boolean bool) {
		boolean oldVal = isDataForTableLoading;
		isDataForTableLoading = bool;
		propChangeFirer.firePropertyChange(Constants.ModelFields.FN_IS_TABLE_LOADING, oldVal, bool);
	}
	
	public void setDataSourceLoading(boolean bool) {
		boolean oldVal = isDataSourceLoading;
		isDataSourceLoading = bool;
		propChangeFirer.firePropertyChange(Constants.ModelFields.FN_IS_DS_LOADING, oldVal, bool);
	}

	public void setColInfoMap(ColumnInfoMap map) {
		ColumnInfoMap oldVal = colInfoMap;
		colInfoMap = map;
		propChangeFirer.firePropertyChange(Constants.ModelFields.FN_COL_INFO_MAP, oldVal, map);
	}
	
	public void setDataSourceRowCount(int count){
		int oldVal = dataSourceRowCount;
		dataSourceRowCount = count;
		propChangeFirer.firePropertyChange(Constants.ModelFields.FN_DATA_TABLE_SOURCE_ROW_COUNT, oldVal, count);
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
	
	public DefaultTableModel getPagedTableModel(){
		return pagedTableModel;
	}
	
	public void setPagedTableModel(DefaultTableModel model){
		DefaultTableModel oldVal = pagedTableModel;
		pagedTableModel = model;
		propChangeFirer.firePropertyChange(Constants.ModelFields.FN_PAGED_TABLE_MODEL, oldVal, model);
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
	
	public Filter getFilter(){
		return filter;
	}
	
	public String getSortColAsInUI(){
		return sortColAsInUI;
	}
	
	public int getDataSourceRowCount(){
		return dataSourceRowCount;
	}

	public void setSort(String colAsInTable, String colToSort, SORT_ORDER order, SQL_TYPE type) {
		SORT_ORDER oldVal = sortOrder;
		sortOrder = order;
		sortCol = colToSort;
		sortType = type;
		sortColAsInUI = colAsInTable;
		
		//firing just the sortorder is already enough because it always changes whenever
		//setsort is called
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
