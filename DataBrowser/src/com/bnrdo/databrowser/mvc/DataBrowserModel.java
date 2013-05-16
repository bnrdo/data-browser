package com.bnrdo.databrowser.mvc;

import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.Map;

import javax.swing.event.SwingPropertyChangeSupport;
import javax.swing.table.TableModel;

import com.bnrdo.databrowser.Pagination;
import com.google.common.collect.Multimap;

@SuppressWarnings("unchecked")
public class DataBrowserModel<E> {

    public static final String FN_DATA_TABLE_SOURCE = "dataTableSource";
    public static final String FN_PAGINATION = "pagination";

    private Pagination pagination;
    private List<E> dataTableSource;
    
    private Multimap<Integer, Object> colInfoMap;

    private SwingPropertyChangeSupport propChangeFirer;

    public DataBrowserModel() {
        propChangeFirer = new SwingPropertyChangeSupport(this);
        pagination = new Pagination();
    }

	public void setPagination(Pagination p) {
		Pagination oldVal = pagination;
		pagination = p;
    	propChangeFirer.firePropertyChange(FN_PAGINATION, oldVal, p);
	}

	public void setDataTableSource(List<E> list) {
		List<E> oldVal = dataTableSource;
    	dataTableSource = list;
    	propChangeFirer.firePropertyChange(FN_DATA_TABLE_SOURCE, oldVal, list);
	}
	
	public List<E> getDataTableSource(){
		return dataTableSource;
	}
	
	public void setColInfoMap(Multimap<Integer, Object> map){
		colInfoMap = map;
	}
	
	public Multimap<Integer, Object> getColInfoMap(){
		return colInfoMap;
	}

	public void addModelListener(PropertyChangeListener prop) {
        propChangeFirer.addPropertyChangeListener(prop);
    }
}
