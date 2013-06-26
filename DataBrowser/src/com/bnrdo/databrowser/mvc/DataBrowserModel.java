package com.bnrdo.databrowser.mvc;

import java.beans.PropertyChangeListener;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.SwingWorker;
import javax.swing.event.SwingPropertyChangeSupport;
import javax.swing.table.DefaultTableModel;

import org.apache.commons.lang3.mutable.MutableInt;
import org.apache.log4j.Logger;
import org.jooq.SQLDialect;
import org.jooq.impl.Factory;

import com.bnrdo.databrowser.ColumnInfoMap;
import com.bnrdo.databrowser.Constants.ModelField;
import com.bnrdo.databrowser.Constants.SortOrder;
import com.bnrdo.databrowser.Constants;
import com.bnrdo.databrowser.DBroUtil;
import com.bnrdo.databrowser.DBDataSource;
import com.bnrdo.databrowser.Filter;
import com.bnrdo.databrowser.Query;
import com.bnrdo.databrowser.exception.ModelException;
import com.bnrdo.databrowser.format.ListSourceFormat;
import com.bnrdo.databrowser.mvc.services.ModelService;

public class DataBrowserModel<E> {
	
	private static org.apache.log4j.Logger log = Logger.getLogger(DataBrowserModel.class);

	private ListSourceFormat<E> tableDataSourceFormat;
	private DefaultTableModel pagedTableModel;

	private SwingPropertyChangeSupport propChangeFirer;
	private ModelService<E> service;
	private ColumnInfoMap colInfoMap;
	private DBDataSource dbDataSource;
	
	private SortOrder sortOrder;
	private SQLDialect dialect;
	
	private String sortCol;
	private String sortColAsInUI;
	private String tableName;
	private Filter filter;
	private List<E> listDataSource;
	
	/* used by the view to change its state, not the realtime basis of row count since
	 * query is done in background
	 */
	private int dataSourceRowCount;
	private int recordLimit;
	private int recordOffset;
	private int maxExposableCount;
	private int itemsPerPage;
	private int totalPageCount;
	
	//for List data source, this tracks the number of records currently inserted to the embedded DB
	private MutableInt INSERTED;
	
	private boolean isDataForTableLoading;
	private boolean isDataSourceLoading;
	private boolean isStatusShowable;
	private boolean isSortDetailsShowable;
	private boolean isFilterDetailsShowable;
	private boolean isRowCountDetailsShowable;

	private String QRY_TEMPLATE;
	private String QRY_RECORD_COUNT;

	public DataBrowserModel() {
		dataSourceRowCount = 0;
		recordLimit = 0;
		recordOffset = 0;
		isDataForTableLoading = false;
		isDataSourceLoading = false;
		propChangeFirer = new SwingPropertyChangeSupport(this);
		service = new ModelService<E>(this);
		
		tableName = "data_browser_persist";
	}
	
	/* setDataSource and its overloaded methods are like entry points.
	 * set the default values after configuring the data source
	 */
	public void setDataSource(DBDataSource dSource){
		StringBuilder colsBdr = new StringBuilder();
		
		for(String col : colInfoMap.getPropertyNames()){
			colsBdr.append(col).append(", ");
		}
		
		colsBdr.replace(colsBdr.length()-2, colsBdr.length(), "");
		Query qry = dSource.getSelectQuery();
		qry.addOrderBy("col_name", SortOrder.ASC);
		qry.addCondition(Factory.condition("upper(filter_col) LIKE upper('filter_key%')"));
		qry.setOffset(775487541);
		qry.setLimit(554754478);
		
		QRY_TEMPLATE = qry.buildSQL()
							.replace(" asc",  " SortOrder")
							.replace("775487541", "offset_count")
							.replace("554754478", "limit_count");
		
		StringBuilder countQry = new StringBuilder("SELECT COUNT(*) FROM ");
		Iterator<String> iter = qry.getFrom().iterator();
		
		while(iter.hasNext()){
			countQry.append(iter.next());
			
			if(iter.hasNext())
				countQry.append(", ");
		}
		
		countQry.append(" WHERE filter_col LIKE 'filter_key%'");
		
		QRY_RECORD_COUNT = countQry.toString();
		
	    //silent set the default value for filter (should not notify the listeners)
		filter = new Filter("", colInfoMap.getPropertyName(0), colInfoMap.getColumnName(0));
		
		dbDataSource = dSource;
		dialect = dSource.getSQLDialect();
		
		setDataSourceRowCount(queryRowCount());
		derivePagination(getDataSourceRowCount());
	}

	public void setDataSource(final List<E> list) {
		
		if(colInfoMap == null){
			throw new ModelException("Column info map should be supplied before setting the data source.");
		}
		
		if(tableDataSourceFormat == null){
			throw new ModelException("Table data source format should be supplied before setting the data source..");
		}
				
		dialect = SQLDialect.HSQLDB;
		listDataSource = list;
		INSERTED = new MutableInt(0);
		
		QRY_TEMPLATE = "SELECT * FROM " + tableName
						+ " WHERE filter_col like 'filter_key%'"
						+ " ORDER BY col_name SortOrder"
						+ " LIMIT limit_count " + "OFFSET offset_count";
		QRY_RECORD_COUNT = "SELECT COUNT(*) FROM " + tableName + " WHERE filter_col like 'filter_key%'";
		
		setDataSourceLoading(true);
		
		new SwingWorker<Void, E>() {
	        @Override
	        protected Void doInBackground() throws Exception {
	        	Connection conn = null;
	    		Statement stmt = null;
	    		
	        	try {
	    			conn = DBroUtil.getConnection();
	    			stmt = conn.createStatement();
	    			stmt.execute("DROP TABLE IF EXISTS " + tableName + ";");
	    			stmt.execute("SET IGNORECASE TRUE;");
	    			stmt.execute(service.translateColInfoMapToCreateTableSQL(tableName, colInfoMap));
	    			
	    			service.populateDBTable(tableName,stmt, list, ((ListSourceFormat<E>)tableDataSourceFormat), INSERTED);
	    			
	    		} catch (Exception e) {
	    			log.error("An error occured while persisting list data source", e);
	    		} finally {
	    			try {
	    				stmt.close();
	    				conn.close();
	    			} catch (Exception e) {
	    				log.error("An error occured while cleaning up connection resources", e);
	    			}
	    		}
				return null;
	        }
	        
	        @Override
	        protected void done() {
//	        	System.out.println("its done bitchachos");
//	        	System.out.println("Passed data source set to null, cannot use its cached size anymore. Will use select count for getting the dssize.");
	        	listDataSource = null;
	        	setDataSourceLoading(false);
	        }
	    }.execute();
	
	    //silent set the default value for filter (should not notify the listeners)
	    filter = new Filter("", colInfoMap.getPropertyName(0), colInfoMap.getColumnName(0));
		setDataSourceRowCount(queryRowCount());
		derivePagination(getDataSourceRowCount());
	}
	
	public int queryRowCount(){
		int retVal = 0;
		
		if(dialect.equals(SQLDialect.HSQLDB) && listDataSource != null){
			//System.out.println("Base datasource not fully processed. Using its cached size.");
			retVal = listDataSource.size();
		}else{
			String qryCount = QRY_RECORD_COUNT;
			
			qryCount = qryCount.replace("filter_col", filter.getCol())
								.replace("filter_key", filter.getKey());
			ResultSet rs = null;
			Statement stmt = null;
			Connection con = null;
	
			try {
				con = (dbDataSource == null) ? DBroUtil.getConnection() : dbDataSource.getConnection();
				stmt = con.createStatement(); 
				//System.out.println("Count query is : " + qryCount);
				rs = stmt.executeQuery(qryCount);
				
				while(rs.next()){
					retVal = (int) rs.getLong(1);
					//System.out.println("Data source count is : " + dataSourceRowCount);
				}
			} catch (Exception e) {
				log.error("An error occured while cleaning up the db connection", e);
			}
			
			//System.out.println("query count is : " + qryCount);
		}
		return retVal;
	}

	public void setFilter(Filter filtr){
		Filter oldVal = filter;
		filter = filtr;
		
		setDataSourceRowCount(queryRowCount());
		derivePagination(getDataSourceRowCount());
		
		DBroUtil.logPropertyChange(log, oldVal, filtr, ModelField.filterKey);
		
		propChangeFirer.firePropertyChange(ModelField.filterKey.name(), oldVal, filtr);
	}
	/* Returns list of cropped data. Data is cropped by calculating the right
	 * sql LIMIT and OFFSET to be applied in the query. For ORACLE, LIMIT and OFFSET are done
	 * using some other codes
	 * Filter and sort are also applied in the query.
	 */
	public void populateViewTableWithScrolledSource(final int from, final int to){
		
		new SwingWorker<Void, List<String>>() {
			
			private Connection conn = null;
			private Statement statement = null;
			private ResultSet rs = null;
			private ResultSetMetaData rsmd = null;
			
			private boolean tblShouldClear = true ;
			
	        @Override
	        protected Void doInBackground() throws Exception {
	        	recordLimit = to - from;
	        	recordOffset = from;
	        	
	        	try {
	        		conn = (dbDataSource == null) ? DBroUtil.getConnection() : dbDataSource.getConnection();
	        		statement = conn.createStatement();
	        		
	        		String qry = QRY_TEMPLATE;
	        		
	        		//System.out.println("query before setting params  : " + qry);
	        		
	        		/* start fetching the scrolled list */
        			qry = qry.replace("col_name", sortCol);
	        		
	        		qry = qry.replace("SortOrder", sortOrder.toString())
	        				.replace("limit_count", Integer.toString((recordLimit)))
	        				.replace("offset_count", Integer.toString(recordOffset))
	        				.replace("filter_col", filter.getCol())
	        				.replace("filter_key", filter.getKey());

	        		log.info("Query for Paged Table  : " + qry);
	        		
	        		setDataForTableLoading(true);
	        		
	        		if(dialect.equals(SQLDialect.HSQLDB)){
		        		if(INSERTED.intValue() < to){
		        			//System.out.println("INSERTED record size is not enough for the requested ResultSet [from : " + from + " | to : " + to +"]");
		        			
		        			while(INSERTED.intValue() < to){
		        				//System.out.println("Waiting for the model to insert the requested records.");
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
	        		log.error("An error occured while the data browser is populated", e);
	        	} finally {
	        		try {
	        			rs.close();
	        			statement.close();
	        			conn.close();
	        		} catch (Exception e) {
	        			log.error("An error occured while cleaning up connection resources", e);
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
	        		//System.out.println("published " + list + " to the table");
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
		
		DBroUtil.logPropertyChange(log, oldVal, bool, ModelField.isDataForTableLoading);
		
		propChangeFirer.firePropertyChange(ModelField.isDataForTableLoading.name(), oldVal, bool);
	}
	
	public void setDataSourceLoading(boolean bool) {
		boolean oldVal = isDataSourceLoading;
		isDataSourceLoading = bool;
		
		DBroUtil.logPropertyChange(log, oldVal, bool, ModelField.isDataSourceLoading);
		
		propChangeFirer.firePropertyChange(ModelField.isDataSourceLoading.name(), oldVal, bool);
	}

	public void setColInfoMap(ColumnInfoMap map) {
		ColumnInfoMap oldVal = colInfoMap;
		colInfoMap = map;
		
		DBroUtil.logPropertyChange(log, oldVal, map, ModelField.colInfoMap);
		
		propChangeFirer.firePropertyChange(ModelField.colInfoMap.name(), oldVal, map);
	}
	
	public void setDataSourceRowCount(int count){
		int oldVal = dataSourceRowCount;
		dataSourceRowCount = count;
		
		DBroUtil.logPropertyChange(log, oldVal, count, ModelField.dataSourceRowCount);
		
		propChangeFirer.firePropertyChange(ModelField.dataSourceRowCount.name(), oldVal, count);
	}

	public void setTableDataSourceFormat(ListSourceFormat<E> fmt) {
		if (colInfoMap == null) {
			tableDataSourceFormat = fmt;
		} else {
			ListSourceFormat<E> oldVal = tableDataSourceFormat;
			tableDataSourceFormat = fmt;
			
			DBroUtil.logPropertyChange(log, oldVal, fmt, ModelField.tableDataSourceFormat);
			
			propChangeFirer.firePropertyChange(ModelField.tableDataSourceFormat.name(), oldVal, fmt);
		}
	}
	
	public DefaultTableModel getPagedTableModel(){
		return pagedTableModel;
	}
	
	public void setPagedTableModel(DefaultTableModel mod){
		DefaultTableModel oldVal = pagedTableModel;
		pagedTableModel = mod;
		
		DBroUtil.logPropertyChange(log, oldVal, mod, ModelField.pagedTableModel);
		
		propChangeFirer.firePropertyChange(ModelField.pagedTableModel.name(), oldVal, mod);
	}

	public void setMaxExposableCount(int num) {
		Integer oldVal = maxExposableCount;
		maxExposableCount = num;
		
		DBroUtil.logPropertyChange(log, oldVal.intValue(), num, ModelField.maxExposableCount);
		
		propChangeFirer.firePropertyChange(ModelField.maxExposableCount.name(), oldVal, Integer.valueOf(num));
	}
	
	public void setTotalPageCount(int num) {
		Integer oldVal = totalPageCount;
		totalPageCount = num;
		
		DBroUtil.logPropertyChange(log, oldVal.intValue(), num, ModelField.totalPageCount);
		
		propChangeFirer.firePropertyChange(ModelField.totalPageCount.name(), oldVal, Integer.valueOf(num));
	}

	public void setItemsPerPage(int num) {
		itemsPerPage = num;
	}

	public SQLDialect getDialect() {
		return dialect;
	}

	public void setDialect(SQLDialect dialect) {
		this.dialect = dialect;
	}

	public ColumnInfoMap getColInfoMap() {
		return colInfoMap;
	}

	public ListSourceFormat<E> getTableDataSourceFormat() {
		return tableDataSourceFormat;
	}

	public SortOrder getSortOrder() {
		return sortOrder;
	}
	
	public Filter getFilter(){
		return filter;
	}
	
	public int getItemsPerPage(){
		return itemsPerPage;
	}
	
	public String getSortColAsInUI(){
		return sortColAsInUI;
	}
	
	public int getDataSourceRowCount(){
		return dataSourceRowCount;
	}

	public boolean isStatusShowable() {
		return isStatusShowable;
	}

	public void setStatusShowable(boolean isShowable) {
		isStatusShowable = isShowable;
	}

	public boolean isSortDetailsShowable() {
		return isSortDetailsShowable;
	}

	public boolean isFilterDetailsShowable() {
		return isFilterDetailsShowable;
	}

	public void setFilterDetailsShowable(boolean isShowable) {
		isFilterDetailsShowable = isShowable;
	}

	public void setSortDetailsShowable(boolean isShowable) {
		isSortDetailsShowable = isShowable;
	}

	public boolean isRowCountDetailsShowable() {
		return isRowCountDetailsShowable;
	}

	public void setRowCountDetailsShowable(boolean isShowable) {
		isRowCountDetailsShowable = isShowable;
	}

	public void setSort(String colAsInTable, String colToSort, SortOrder order) {
		log.info("A call to setSort. Order : " + order);
		
		SortOrder oldVal = sortOrder;
		sortOrder = order;
		sortCol = colToSort;
		sortColAsInUI = colAsInTable;
		
		DBroUtil.logPropertyChange(log, oldVal, order, ModelField.sortOrder);
		
		//firing just the sortorder is already enough because it always changes whenever
		//setsort is called
		propChangeFirer.firePropertyChange(ModelField.sortOrder.name(), oldVal, order);
	}

	public void addModelListener(PropertyChangeListener prop) {
		propChangeFirer.addPropertyChangeListener(prop);
	}
	
	/* This method is for adjusting the pagination when the query changes.
	 */
	private void derivePagination(int srcSize){
		int pageCountForEvenSize = (srcSize / itemsPerPage);
		int totalPageCount = (srcSize % itemsPerPage) == 0 ? pageCountForEvenSize : pageCountForEvenSize + 1; 
		setTotalPageCount(totalPageCount);
		
	}
}
