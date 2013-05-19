package com.bnrdo.databrowser;

import java.awt.BorderLayout;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;

import com.bnrdo.databrowser.exception.ModelException;
import com.bnrdo.databrowser.mvc.DataBrowserController;
import com.bnrdo.databrowser.mvc.DataBrowserModel;
import com.bnrdo.databrowser.mvc.DataBrowserView;
import com.google.common.collect.Multimap;

@SuppressWarnings("serial")
public class DataBrowser<E> extends JPanel {

    private DataBrowserController<E> controller;
    private DataBrowserModel<E> model;
    private DataBrowserView view;
    
    private List<E> source;
    private TableDataSourceFormat sourceFormat;
    private Multimap<Integer, Object> colInfoMap;

    public DataBrowser() {
        view = new DataBrowserView();
        model = new DataBrowserModel<E>();

        controller = new DataBrowserController<E>(view, model);
        setLayout(new BorderLayout());
        add(view.getUI(), BorderLayout.CENTER);
    }

    public void setDataTableSource(List<E> src){
    	model.setDataTableSource(src);
    }
    
    public void setTableDataSourceFormat(TableDataSourceFormat fmt){
    	model.setTableDataSourceFormat(fmt);
    }
    
    public void setColInfoMap(Multimap<Integer, Object> map){
    	model.setColInfoMap(map);
    }
    
    private void validateInput(){
    	if(colInfoMap == null)
    		throw new ModelException("Column info map should not be null");
    	else if(sourceFormat == null)
    		throw new ModelException("Format for the data source should not be null");
    	else if(source == null)
    		throw new ModelException("Data source should not be null");
    	
    }
    public javax.swing.JButton getTestButton(){
    	return view.getTestButton();
    }
    public DataBrowserModel<E> getModel(){
    	return model;
    }
}
