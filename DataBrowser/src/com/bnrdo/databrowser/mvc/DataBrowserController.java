package com.bnrdo.databrowser.mvc;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import com.bnrdo.databrowser.AbstractDocumentListener;
import com.bnrdo.databrowser.ColumnInfoMap;
import com.bnrdo.databrowser.DBroUtil;
import com.bnrdo.databrowser.Constants.SORT_ORDER;
import com.bnrdo.databrowser.Constants.SQL_TYPE;
import com.bnrdo.databrowser.PushableTableHeaderRenderer;
import com.bnrdo.databrowser.Pagination;
import com.bnrdo.databrowser.TableDataSourceFormat;
import com.bnrdo.databrowser.exception.ModelException;
import com.bnrdo.databrowser.listener.ModelListener;
import com.bnrdo.databrowser.listener.PaginationListener;
import com.bnrdo.databrowser.listener.PushableTableHeaderListener;
import com.bnrdo.databrowser.listener.TableSortListener;
import com.bnrdo.databrowser.mvc.DataBrowserView.PageButton;

public class DataBrowserController<E> implements ModelListener {
	
	private DataBrowserView view;
	private DataBrowserModel<E> model;

	public DataBrowserController(DataBrowserView v, DataBrowserModel<E> m) {
		view = v;
		model = m;
		
		model.addModelListener(this);
	}
	
	private void setUpFiltering(){
		// the search category combobox
		ColumnInfoMap colInfo = model.getColInfoMap();
		view.getCboSearch().removeAllItems();
		for(Integer t : colInfo.getKeySet()){
			view.getCboSearch().addItem(colInfo.getColumnName(t));
		}
		
		// the textfield
		final JTextField txtSearch = view.getTxtSearch();
		txtSearch.getDocument().addDocumentListener(new AbstractDocumentListener(){
			@Override
			public void documentChanged(DocumentEvent evt){
				
			}
		});
	}
	
	private void renderDataInTableInView(final JTable tbl, 
											final ColumnInfoMap colInfo, 
											final TableDataSourceFormat<E> fmt) {
		
		Object[] colNames = colInfo.getColumnNames();

		/*
		 * table's visibility is nothing but an indicator whether the table
		 * model is already set, if already set then just reuse the existing
		 * one, else set it. then set visible to true to prevent resetting.
		 * Table model resue is necessary to prevent the restoration of column
		 * adjustment, column arrangement etc. done before calling this method
		 * (e.g. sorting)
		 */
		if(!tbl.isVisible()){
			tbl.setModel(new DefaultTableModel(null, colNames));
			tbl.setVisible(true);
		}
		
		JTableHeader header = tbl.getTableHeader();
		DefaultTableModel tableModel = (DefaultTableModel) tbl.getModel();
		TableColumnModel colModel = tbl.getColumnModel();
		
		PushableTableHeaderRenderer renderer = new PushableTableHeaderRenderer();
		PushableTableHeaderListener ptListen = new PushableTableHeaderListener(header, renderer);
		
		//remove the rows to expose, yes this line clears the jtable
		tableModel.setRowCount(0);
		
		//add the rows to expose
		for (E domain : model.getDataTableSourceExposed()){
			tableModel.addRow(DBroUtil.extractRowFromFormat(fmt, domain));
		}
		
		//renderer the column header with a button for push effect
		for(int i = 0 ; i < colNames.length; i++){
			TableColumn tc = colModel.getColumn(i);
			tc.setHeaderRenderer(renderer);
		}
		
		//remove listener if already existing
		for(MouseListener ms : header.getMouseListeners()){
			if(ms instanceof PushableTableHeaderListener ||
					ms instanceof TableSortListener){
				header.removeMouseListener(ms);
			}
		}
		
		for(MouseMotionListener ms : header.getMouseMotionListeners()){
			if(ms instanceof PushableTableHeaderListener){
				header.removeMouseMotionListener(ms);
			}
		}
		
		header.addMouseListener(ptListen);
		header.addMouseMotionListener(new PushableTableHeaderListener(header, renderer));
		header.addMouseListener(new TableSortListener<E>(tbl, model));
	}

	private void renderPageNumbersInView(int[] pages) {

		view.createPageButtons(pages);

		if (model.getPagination().getPageNumsExposed().length > 1) {

			view.getBtnFirst().addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					pageButtonIsClicked(Pagination.FIRST_PAGE);
				}
			});
			view.getBtnPrev().addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					pageButtonIsClicked(Pagination.PREV_PAGE);
				}
			});
			view.getBtnNext().addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					pageButtonIsClicked(Pagination.NEXT_PAGE);
					
				}
			});
			view.getBtnLast().addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					pageButtonIsClicked(Pagination.LAST_PAGE);
				}
			});
		}

		for (final PageButton btn : view.getPageBtns()) {
			btn.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					int pageNum = Integer.parseInt(btn.getText());
					pageButtonIsClicked(pageNum);
				}
			});
		}
	}

	private void pageButtonIsClicked(Object where){
		Pagination p = model.getPagination();
		
		if(where instanceof Integer){
			p.setCurrentPageNum((Integer)where);
		}else if(where instanceof String){
			p.setCurrentPageNum((String)where);
		}else {
			throw new ModelException("Invalid page click destination.");
		}
		
		//render the new page set in view
		renderPageNumbersInView(p.getPageNumsExposed());
		changeCurrentPageNum(p.getCurrentPageNum());
	}
	
	private void changeCurrentPageNum(int newVal) {
		PageButton[] btns = view.getPageBtns();
		int lastPageNum = model.getPagination().getLastRawPageNum();
		int firstPageNum = model.getPagination().getFirstRawPageNum();

		for (PageButton btn : btns) {
			btn.setSelected(Integer.parseInt(btn.getText()) == newVal);
		}

		view.getBtnFirst().setVisible(!(newVal == firstPageNum));
		view.getBtnPrev().setVisible(!(newVal == firstPageNum));
		view.getBtnNext().setVisible(!(newVal == lastPageNum));
		view.getBtnLast().setVisible(!(newVal == lastPageNum));
	}
	
	//if data in model changes reflect it to the UI
	public void propertyChange(PropertyChangeEvent evt){
		String propName = evt.getPropertyName();

		if (DataBrowserModel.FN_DATA_TABLE_SOURCE_EXPOSED.equalsIgnoreCase(propName)) {
			renderDataInTableInView(view.getDataTable(), model.getColInfoMap(), 
							model.getTableDataSourceFormat());
		}else if(DataBrowserModel.FN_PAGINATION.equalsIgnoreCase(propName)){
			final Pagination p = model.getPagination();
			p.removePaginationListener(Pagination.PAGINATE_CONTENT_ID);
			p.addPaginationListener(Pagination.PAGINATE_CONTENT_ID, new PaginationListener() {
				@Override public void pageChanged(int pageNum) {
					//computation for offset and limit of sql query
					int itemsPerPage = p.getItemsPerPage();
					int itemCount = model.getDataSourceRowCount();
					int lastItem = p.getCurrentPageNum() * itemsPerPage;
					int from = lastItem - itemsPerPage;
					int to = (lastItem > (itemCount)) ? itemCount : lastItem;
					
					model.setDataTableSourceExposed(model.getScrolledSource(from, to));
				}
			});
			
			renderPageNumbersInView(p.getPageNumsExposed());
			setUpFiltering();
			
			PageButton[] btns = view.getPageBtns(); 
			if(btns.length > 0){
				int index = DBroUtil.getIndexOfNumFromArray(p.getPageNumsExposed(), p.getCurrentPageNum()); 
				btns[index].doClick();
			}
		}else if(DataBrowserModel.FN_SORT_ORDER.equalsIgnoreCase(propName)){
			Pagination p = model.getPagination();
			PageButton[] btns = view.getPageBtns(); 
			if(btns.length > 0){
				int index = DBroUtil.getIndexOfNumFromArray(p.getPageNumsExposed(), p.getCurrentPageNum()); 
				btns[index].doClick();
			}
		}
		
		//the following fragments should also be put in place ok.
		//|| DataBrowserModel.FN_DATA_TABLE_SOURCE_FORMAT.equalsIgnoreCase(propName)
		//|| DataBrowserModel.FN_COL_INFO_MAP.equalsIgnoreCase(propName)
	}
}