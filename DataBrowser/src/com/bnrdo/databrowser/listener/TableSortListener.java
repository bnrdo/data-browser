package com.bnrdo.databrowser.listener;

import java.awt.Cursor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import javax.swing.JTable;
import javax.swing.table.JTableHeader;

import com.bnrdo.databrowser.AppStat;
import com.bnrdo.databrowser.ColumnInfoMap;
import com.bnrdo.databrowser.Constants.SORT_ORDER;
import com.bnrdo.databrowser.Constants.SQL_TYPE;
import com.bnrdo.databrowser.mvc.DataBrowserModel;

public class TableSortListener<E> extends MouseAdapter{
	
	private JTable table;
	private DataBrowserModel<E> model;
	
	public TableSortListener(JTable tbl, DataBrowserModel<E> m){
		table = tbl;
		model = m;
	}
	
	@Override
	public void mouseReleased(MouseEvent e) {
		JTableHeader header = table.getTableHeader();
		Cursor cursor = header.getCursor();
		boolean isSortableAccdgToPushableListener = true;
		
		for(MouseMotionListener listen : header.getMouseMotionListeners()){
			if(listen instanceof PushableTableHeaderListener){
				isSortableAccdgToPushableListener = ((PushableTableHeaderListener)listen).isSortable();
				System.out.println("isSortableAccdgToPushableListener : " + isSortableAccdgToPushableListener );
				break;
			}
		}
		
		if(cursor.getType() == Cursor.E_RESIZE_CURSOR ||
				isSortableAccdgToPushableListener == false)
			return;
		
		int selIndex = table.convertColumnIndexToModel(table.columnAtPoint(e.getPoint()));
		
    	if (!(selIndex < 0)) {
    		ColumnInfoMap map = model.getColInfoMap();
    		String propNameToSort = map.getPropertyName(selIndex);
    		SQL_TYPE propType = map.getPropertyType(selIndex);
    		
    		if(model.getSortOrder().equals(SORT_ORDER.ASC)){
    			model.setSort(propNameToSort, SORT_ORDER.DESC, propType);
    		}else{
    			model.setSort(propNameToSort, SORT_ORDER.ASC, propType);
    		}
    	}
	}
}
