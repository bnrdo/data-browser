package com.bnrdo.databrowser;

import java.awt.BorderLayout;
import java.util.List;

import javax.swing.JPanel;

import org.apache.log4j.Logger;

import com.bnrdo.databrowser.Constants.SortOrder;
import com.bnrdo.databrowser.exception.ModelException;
import com.bnrdo.databrowser.format.ListSourceFormat;
import com.bnrdo.databrowser.mvc.DataBrowserController;
import com.bnrdo.databrowser.mvc.DataBrowserModel;
import com.bnrdo.databrowser.mvc.DataBrowserView;

@SuppressWarnings("serial")
public class DataBrowser<E> extends JPanel {
	
	private static org.apache.log4j.Logger log = Logger.getLogger(DataBrowser.class);

    private DataBrowserModel<E> model;
    private DataBrowserView view;
    private DataBrowserController<E> controller;
    
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
	
	private boolean isPaginationEnabled;
    
    public DataBrowser() {
        maxPageCountExposable = 7;
        rowCountPerPage = 10;
        
        isStatusShowable = true;
    	isSortDetailsShowable = true;
    	isFilterDetailsShowable = true;
    	isRowCountDetailsShowable = true;
    	isPaginationEnabled = true;
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
	
	public void setPaginationEnabled(boolean bool){
		isPaginationEnabled = bool;
	}

	public void create(){
    	validateInput();
    	
    	view = new DataBrowserView();
    	model = new DataBrowserModel<E>();
    	controller = new DataBrowserController<E>(view, model);
        
        setLayout(new BorderLayout());
        add(view.getUI(), BorderLayout.CENTER);
    	
    	log.debug("Initial setting - is status showable ? : " + isStatusShowable);
    	log.debug("Initial setting - is sort details showable ? : " + isSortDetailsShowable);
    	log.debug("Initial setting - is filter details showable ? : " + isFilterDetailsShowable);
    	log.debug("Initial setting - is row count details showable ? : " + isRowCountDetailsShowable);
    	log.debug("Initial setting - maximum page number count exposable : " + maxPageCountExposable);
    	log.debug("Initial setting - maximum row count per page : " + rowCountPerPage);
    	
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
    					SortOrder.ASC);
    	
    	controller.afterMVCInit();
    }
    
    private void validateInput(){
    	if(colInfoMap == null)
    		throw new ModelException("Column info map should not be null");
    	if(sourceFormat == null && source != null)
    		throw new ModelException("Format for the list datasource should not be null");
    	if(source == null && dbDataSource == null)
    		throw new ModelException("You should provide a valid datasource");
    	if(maxPageCountExposable <= 1 && isPaginationEnabled)
    		throw new ModelException("Max page numbers to be shown must not be lesser than or equal to 0");
    	if(rowCountPerPage <= 0 && isPaginationEnabled)
    		throw new ModelException("Row numbers per page must not be lesser than or equal to 0");
    	log.debug("All inputs are valid.");
    }
    
    public DataBrowserModel<E> getModel(){
    	return model;
    }
}
