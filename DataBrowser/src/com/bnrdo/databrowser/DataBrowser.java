package com.bnrdo.databrowser;

import java.awt.BorderLayout;

import javax.swing.JPanel;

import com.bnrdo.databrowser.exception.ModelException;
import com.bnrdo.databrowser.mvc.DataBrowserController;
import com.bnrdo.databrowser.mvc.DataBrowserModel;
import com.bnrdo.databrowser.mvc.DataBrowserView;

@SuppressWarnings("serial")
public class DataBrowser extends JPanel {

    private DataBrowserController controller;
    private DataBrowserModel model;
    private DataBrowserView view;

    public DataBrowser() {
        view = new DataBrowserView();
        model = new DataBrowserModel();

        controller = new DataBrowserController(view, model);
        controller.control();
        setLayout(new BorderLayout());
        add(view.getUI(), BorderLayout.CENTER);
    }

    public void setPagination(Pagination p) {    	
        model.setPagination(p);
    }
    
    public DataBrowserModel getModel(){
    	return model;
    }
}
