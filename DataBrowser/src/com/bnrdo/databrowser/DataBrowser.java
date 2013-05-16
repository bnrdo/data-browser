package com.bnrdo.databrowser;

import java.awt.BorderLayout;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;

import com.bnrdo.databrowser.exception.ModelException;
import com.bnrdo.databrowser.mvc.DataBrowserController;
import com.bnrdo.databrowser.mvc.DataBrowserModel;
import com.bnrdo.databrowser.mvc.DataBrowserView;

@SuppressWarnings("serial")
public class DataBrowser<E> extends JPanel {

    private DataBrowserController<E> controller;
    private DataBrowserModel<E> model;
    private DataBrowserView view;

    public DataBrowser() {
        view = new DataBrowserView();
        model = new DataBrowserModel<E>();

        controller = new DataBrowserController<E>(view, model);
        setLayout(new BorderLayout());
        add(view.getUI(), BorderLayout.CENTER);
    }

    public void setDataTableSource(List<E> source){
    	model.setDataTableSource(source);
    }
    
    public void setColNameIndexMap(Map<Integer, String> map){
    	//model.setColNameIndexMap(map);
    }
    
    public void create(){
    	controller.control();
    }
    
    public DataBrowserModel<E> getModel(){
    	return model;
    }
}
