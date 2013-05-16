package com.bnrdo.databrowser.mvc;

import java.beans.PropertyChangeEvent;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import com.bnrdo.databrowser.DataBrowserUtil;
import com.bnrdo.databrowser.domain.Person;
import com.bnrdo.databrowser.listener.ModelListener;
import com.google.common.collect.Multimap;

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
		Multimap<Integer, Object> map = model.getColInfoMap();
		
		Object[] colNames = DataBrowserUtil.extractColNamesFromMap(map);
		DefaultTableModel tableModel = new DefaultTableModel(null, colNames);
		
		for(E domain : model.getDataTableSource()){
			TableSourceFormat fmt =new TableSourceFormat((Person) domain);
			tableModel.addRow(new Object[]{fmt.getValueAt(0),
											fmt.getValueAt(1),
											fmt.getValueAt(2),
											fmt.getValueAt(3),
											fmt.getValueAt(4)});
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

class TableSourceFormat{
	private Person p;
	public TableSourceFormat(Person p){
		this.p = p;
	}
	public String getValueAt(int index){
		
		if(index == 0){
			return p.getFirstName();
		}else if(index == 1){
			return p.getLastName();
		}else if(index == 2){
			return p.getBirthDay().toString();
		}else if(index == 3){
			return Integer.toString(p.getAge());
		}else if(index == 4){
			return p.getOccupation();
		}
		
		throw new IllegalStateException();
	}
}
