package com.bnrdo.databrowser.mvc;

import java.beans.PropertyChangeEvent;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import com.bnrdo.databrowser.DataBrowserUtil;
import com.bnrdo.databrowser.listener.ModelListener;

@SuppressWarnings("unchecked")
public class DataBrowserController<E> implements ModelListener {
	private DataBrowserView view;
	private DataBrowserModel<E> model;

	public DataBrowserController(DataBrowserView v, DataBrowserModel<E> m) {
		view = v;
		model = m;
		
		model.addModelListener(this);
	}

	public void control(){
		setUpTable();
	}

	private void setUpTable(){
		JTable tblData = view.getDataTable();
		Object[] colNames = DataBrowserUtil.extractColNamesFromMap(model.getColInfoMap());
		DefaultTableModel tableModel = new DefaultTableModel(null, colNames);
		
		for(E li : model.getDataTableSource()){
			tableModel.addRow(DataBrowserUtil.convertPojoToObjectArray(li));
		}
		
		tblData.setModel(tableModel);
	}
	
	//if data in model changes reflect it to the UI
	public void propertyChange(PropertyChangeEvent evt) {
		String propName = evt.getPropertyName();
		Object newVal = evt.getNewValue();
		
		if (DataBrowserModel.FN_PAGINATION.equalsIgnoreCase(propName)) {
			
		}else if (DataBrowserModel.FN_DATA_TABLE_SOURCE.equalsIgnoreCase(propName)) {
			setUpTable();
		}
	}
}


