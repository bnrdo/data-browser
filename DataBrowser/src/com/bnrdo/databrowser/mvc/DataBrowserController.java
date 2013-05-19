package com.bnrdo.databrowser.mvc;

import java.beans.PropertyChangeEvent;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import com.bnrdo.databrowser.DataBrowserUtil;
import com.bnrdo.databrowser.Pagination;
import com.bnrdo.databrowser.TableDataSourceFormat;
import com.bnrdo.databrowser.listener.ModelListener;
import com.google.common.collect.Multimap;

public class DataBrowserController<E> implements ModelListener {
	private DataBrowserView view;
	private DataBrowserModel<E> model;

	public DataBrowserController(DataBrowserView v, DataBrowserModel<E> m) {
		view = v;
		model = m;
		
		model.addModelListener(this);
	}

	private void setUpTable(){
		JTable tblData = view.getDataTable();
		Multimap<Integer, Object> map = model.getColInfoMap();
		
		Object[] colNames = DataBrowserUtil.extractColNamesFromMap(map);
		DefaultTableModel tableModel = new DefaultTableModel(null, colNames);
		TableDataSourceFormat<E> fmt = model.getTableDataSourceFormat();
		
		for(E domain : model.getDataTableSource()){
			tableModel.addRow(DataBrowserUtil.extractRowFromFormat(fmt, domain));
		}
		
		tblData.setModel(tableModel);
		setUpPagination();
	}
	
	private void setUpPagination(){
		Pagination p = new Pagination();
		p.setCurrentPageNum(Pagination.FIRST_PAGE);
		p.setMaxExposableCount(10);
		p.setTotalPageCount(model.getDataTableSource().size());
		model.setPagination(p);
	}
	
	//if data in model changes reflect it to the UI
	public void propertyChange(PropertyChangeEvent evt) {
		String propName = evt.getPropertyName();
		Object newVal = evt.getNewValue();
		
		if (DataBrowserModel.FN_PAGINATION.equalsIgnoreCase(propName)) {
			
		}else if (DataBrowserModel.FN_DATA_TABLE_SOURCE.equalsIgnoreCase(propName) ||
				DataBrowserModel.FN_DATA_TABLE_SOURCE_FORMAT.equalsIgnoreCase(propName) ||
				DataBrowserModel.FN_COL_INFO_MAP.equalsIgnoreCase(propName)) {
			//if one of the three fired a property change, re-setup the table
			
			setUpTable();
		}
	}
}