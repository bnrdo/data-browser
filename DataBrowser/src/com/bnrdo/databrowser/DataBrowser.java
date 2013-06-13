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

import com.bnrdo.databrowser.exception.ModelException;
import com.bnrdo.databrowser.mvc.DataBrowserController;
import com.bnrdo.databrowser.mvc.DataBrowserModel;
import com.bnrdo.databrowser.mvc.DataBrowserView;

@SuppressWarnings("serial")
public class DataBrowser<E> extends JPanel {

    private DataBrowserController<E> controller;
    private DataBrowserModel<E> model;
    private DataBrowserView view;
    
    private List<E> source;
    private TableDataSourceFormat<E> sourceFormat;
    private ColumnInfoMap colInfoMap;

    private Connection dsConn;
    private String tableName;
    
    public DataBrowser() {
        view = new DataBrowserView();
        model = new DataBrowserModel<E>();

        controller = new DataBrowserController<E>(view, model);
        
        setLayout(new BorderLayout());
        add(view.getUI(), BorderLayout.CENTER);
    }

    public void setDataTableSource(List<E> src){
    	source = src;
    	dsConn = null;
    }
    
    public void setDataTableSource(Connection conn, String tblName){
    	dsConn = conn;
    	tableName = tblName;
    	source = null;
    }
    
    public void setTableDataSourceFormat(TableDataSourceFormat<E> fmt){
    	sourceFormat = fmt;
    }
    
	public void setColInfoMap(ColumnInfoMap map){
    	colInfoMap = map;
    }
    
    public void create(){
    	/*
    	validateInput();
    	model.setColInfoMap(colInfoMap);
    	model.setTableDataSourceFormat(sourceFormat);
    	model.setDataTableSource(source);
    	
    	Pagination p = new Pagination();
    	p.setCurrentPageNum(Pagination.FIRST_PAGE);
    	p.setMaxExposableCount(numOfPagesExposed);
    	p.setItemsPerPage(itemsPerPage);
    	
    	model.setPagination(p);*/
    	validateInput();
    	
    	/*Pagination p = new Pagination();
    	p.setCurrentPageNum(Pagination.FIRST_PAGE);
    	p.setMaxExposableCount(numOfPagesExposed);
    	p.setItemsPerPage(itemsPerPage);
    	model.setPagination(p);*/
    	
    	model.setColInfoMap(colInfoMap);
    	model.setTableDataSourceFormat(sourceFormat);
    	
    	if(dsConn == null) model.setDataTableSource(source);
    	else if(source == null) model.setDataTableSource(dsConn, tableName);
    }
    
    private void validateInput(){
    	if(colInfoMap == null)
    		throw new ModelException("Column info map should not be null");
    	else if(sourceFormat == null)
    		throw new ModelException("Format for the data source should not be null");
    	else if(source == null && dsConn == null)
    		throw new ModelException("You should provide a valid datasource");
    }
    
    public javax.swing.JButton getTestButton(){
    	return view.getTestButton();
    }
    
    public DataBrowserModel<E> getModel(){
    	return model;
    }
}
