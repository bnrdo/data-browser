package com.bnrdo.databrowser;

import java.awt.BorderLayout;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;

import org.hsqldb.DatabaseManager;

import com.bnrdo.databrowser.Constants.SORT_ORDER;
import com.bnrdo.databrowser.exception.ModelException;
import com.bnrdo.databrowser.format.ListSourceFormat;
import com.bnrdo.databrowser.mvc.DataBrowserController;
import com.bnrdo.databrowser.mvc.DataBrowserModel;
import com.bnrdo.databrowser.mvc.DataBrowserView;

@SuppressWarnings("serial")
public class DataBrowser<E> extends JPanel {

    private DataBrowserController<E> controller;
    private DataBrowserModel<E> model;
    private DataBrowserView view;
    
    private List<E> source;
    private ListSourceFormat<E> sourceFormat;
    
    private ColumnInfoMap colInfoMap;
    private DBDataSource dbDataSource;
    
    private int maxPageCountExposable;
    private int rowCountPerPage;

	private boolean isStatusShowable;
	private boolean isSortDetailsShowable;
	private boolean isFilterDetailsShowable;
	private boolean isRowCountDetailsShowable;
    
    public DataBrowser() {
        view = new DataBrowserView();
        model = new DataBrowserModel<E>();

        controller = new DataBrowserController<E>(view, model);
        
        maxPageCountExposable = 7;
        rowCountPerPage = 10;
        
        isStatusShowable = true;
    	isSortDetailsShowable = true;
    	isFilterDetailsShowable = true;
    	isRowCountDetailsShowable = true;
        
        setLayout(new BorderLayout());
        add(view.getUI(), BorderLayout.CENTER);
    }

    public void setStatusVisible(boolean bool){
    	isStatusShowable = bool;
    }
    
    public void setSortDetailsVisible(boolean bool){
    	isSortDetailsShowable = bool;
    }
    
    public void setFilterDetailsVisible(boolean bool){
    	isFilterDetailsShowable = bool;
    }
    
    public void setRowCountDetailsVisible(boolean bool){
    	isRowCountDetailsShowable = bool;
    }
    
    public void setDataSource(List<E> src){
    	source = src;
    	dbDataSource = null;
    }
    
    public void setDataSource(DBDataSource ds){
    	dbDataSource = ds;
    	source = null;
    }
    
    public void setTableDataSourceFormat(ListSourceFormat<E> fmt){
    	sourceFormat = fmt;
    }
    
	public void setColInfoMap(ColumnInfoMap map){
    	colInfoMap = map;
    }
    
    public void setMaxPageCountExposable(int num) {
    	maxPageCountExposable = num;
	}

	public void setRowCountPerPage(int num) {
		rowCountPerPage = num;
	}

	public void create(){
    	validateInput();
    	
    	model.setStatusShowable(isStatusShowable);
    	model.setSortDetailsShowable(isSortDetailsShowable);
    	model.setFilterDetailsShowable(isFilterDetailsShowable);
    	model.setRowCountDetailsShowable(isRowCountDetailsShowable);
    	
    	model.setColInfoMap(colInfoMap);    		
    	model.setTableDataSourceFormat(sourceFormat);
    	model.setMaxExposableCount(maxPageCountExposable);
    	model.setItemsPerPage(rowCountPerPage);
    	
    	if(dbDataSource == null) 
    		model.setDataSource(source);
    	else if(source == null) 
    		model.setDataSource(dbDataSource);
    	
    	model.setSort(colInfoMap.getColumnName(0), 
    					colInfoMap.getPropertyName(0), 
    					SORT_ORDER.ASC);
    }
    
    private void validateInput(){
    	if(colInfoMap == null)
    		throw new ModelException("Column info map should not be null");
    	else if(sourceFormat == null && source != null)
    		throw new ModelException("Format for the list datasource should not be null");
    	else if(source == null && dbDataSource == null)
    		throw new ModelException("You should provide a valid datasource");
    }
    
    public DataBrowserModel<E> getModel(){
    	return model;
    }
}
