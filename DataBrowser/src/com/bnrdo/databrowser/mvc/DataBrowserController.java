package com.bnrdo.databrowser.mvc;

import java.awt.event.ActionEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import com.bnrdo.databrowser.ColumnInfoMap;
import com.bnrdo.databrowser.Constants;
import com.bnrdo.databrowser.DBroUtil;
import com.bnrdo.databrowser.Filter;
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
	
	@SuppressWarnings("serial")
	private void setupFilterCategoryInView(){
		// the search category combobox
		final ColumnInfoMap colInfo = model.getColInfoMap();
		final JTextField txtSearch = view.getTxtSearch();
		final JComboBox cboSearch = view.getCboSearch();
		final JButton btnSearch = view.getBtnSearch();
		
		cboSearch.removeAllItems();
		
		for(Integer t : colInfo.getKeySet()){
			cboSearch.addItem(colInfo.getColumnName(t));
		}
		
		btnSearch.setAction(new AbstractAction("Search") {
			@Override
			public void actionPerformed(ActionEvent arg0){
				int index = cboSearch.getSelectedIndex();
				Filter f= new Filter();
				f.setKey(txtSearch.getText());
				f.setCol(colInfo.getPropertyName(index));
				f.setColAsInUI(colInfo.getColumnName(index));
				model.setFilter(f);
			}
		});
	}
	
//	private void setupDataTableInView(final JTable tbl, 
//											final ColumnInfoMap colInfo, 
//											final TableDataSourceFormat<E> fmt) {
//		
//		Object[] colNames = colInfo.getColumnNames();
//
//		/*
//		 * table's visibility is nothing but an indicator whether the table
//		 * model is already set, if already set then just reuse the existing
//		 * one, else set it. then set visible to true to prevent resetting.
//		 * Table model resue is necessary to prevent the restoration of column
//		 * adjustment, column arrangement etc. done before calling this method
//		 * (e.g. sorting)
//		 */
//		if(!tbl.isVisible()){
//			tbl.setModel(new DefaultTableModel(null, colNames));
//			tbl.setVisible(true);
//		}
//		
//		JTableHeader header = tbl.getTableHeader();
//		DefaultTableModel tableModel = (DefaultTableModel) tbl.getModel();
//		TableColumnModel colModel = tbl.getColumnModel();
//		
//		PushableTableHeaderRenderer renderer = new PushableTableHeaderRenderer();
//		PushableTableHeaderListener ptListen = new PushableTableHeaderListener(header, renderer);
//		
//		//remove the rows to expose, yes this line clears the jtable
//		tableModel.setRowCount(0);
//		
//		//add the rows to expose
//		for (E domain : model.getDataTableSourceExposed()){
//			tableModel.addRow(DBroUtil.extractRowFromFormat(fmt, domain));
//		}
//		
//		//renderer the column header with a button for push effect
//		for(int i = 0 ; i < colNames.length; i++){
//			TableColumn tc = colModel.getColumn(i);
//			tc.setHeaderRenderer(renderer);
//		}
//		
//		//remove listener if already existing
//		for(MouseListener ms : header.getMouseListeners()){
//			if(ms instanceof PushableTableHeaderListener ||
//					ms instanceof TableSortListener){
//				header.removeMouseListener(ms);
//			}
//		}
//		
//		for(MouseMotionListener ms : header.getMouseMotionListeners()){
//			if(ms instanceof PushableTableHeaderListener){
//				header.removeMouseMotionListener(ms);
//			}
//		}
//		
//		header.addMouseListener(ptListen);
//		header.addMouseMotionListener(ptListen);
//		header.addMouseListener(new TableSortListener<E>(tbl, model));
//		
//		tbl.repaint();
//	}

	@SuppressWarnings("serial")
	private void setupPageNumbersInView(int[] pages) {

		view.createPageButtons(pages);
		
		if(model.getPagination().getPageNumsExposed().length == 1){
			view.getPageBtns()[0].setVisible(false);
		}else if (model.getPagination().getPageNumsExposed().length > 1) {

			view.getBtnFirst().setAction(new AbstractAction("First") {
				public void actionPerformed(ActionEvent e) {
					pageButtonIsClicked(Pagination.FIRST_PAGE);
				}
			});
			view.getBtnPrev().setAction(new AbstractAction("Prev") {
				public void actionPerformed(ActionEvent e) {
					pageButtonIsClicked(Pagination.PREV_PAGE);
				}
			});
			view.getBtnNext().setAction(new AbstractAction("Next") {
				public void actionPerformed(ActionEvent e) {
					pageButtonIsClicked(Pagination.NEXT_PAGE);
					
				}
			});
			view.getBtnLast().setAction(new AbstractAction("Last") {
				public void actionPerformed(ActionEvent e) {
					pageButtonIsClicked(Pagination.LAST_PAGE);
				}
			});
		}
		
		for (final PageButton btn : view.getPageBtns()) {
			btn.setAction(new AbstractAction(btn.getText()) {
				@Override
				public void actionPerformed(ActionEvent e) {
					int pageNum = Integer.parseInt(btn.getText());
					pageButtonIsClicked(pageNum);
				}
			});
		}
		
	}

	private void pageButtonIsClicked(Object where){
		final Pagination p = model.getPagination();
		
		if(where instanceof Integer){
			//if(p.getCurrentPageNum() == ((Integer)where))
			//	return;
			
			p.setCurrentPageNum((Integer)where);
		}else if(where instanceof String){
			/*if(p.getCurrentPageNum() == p.getFirstRawPageNum() && where.toString().equals("FIRST") ||
					p.getCurrentPageNum() == p.getLastRawPageNum() && where.toString().equals("LAST"))
				return;*/
			
			p.setCurrentPageNum((String)where);
		}else {
			throw new ModelException("Invalid page click destination.");
		}
		
		//setup the new page set in view 
		setupPageNumbersInView(p.getPageNumsExposed());
		changeCurrentPageNum(p.getCurrentPageNum());
		
		//computation for offset and limit of sql query
		int itemsPerPage = p.getItemsPerPage();
		int itemCount = model.getDataSourceRowCount();
		int lastItem = p.getCurrentPageNum() * itemsPerPage;
		
		final int from = lastItem - itemsPerPage;
		final int to = (lastItem > (itemCount)) ? itemCount : lastItem;
		
		System.out.println("items per page : " + itemsPerPage);
		System.out.println("item count : " + itemCount);
		System.out.println("last item : " + lastItem);
		System.out.println("from : " + from);
		System.out.println("to : " + to);
		
		
		
		/* getscrolledsource might return a modelexception if the datasource type
		 * is list. the list will be persisted to this app's DB using batch inserts.
		 * if the batch insert is not yet done and the user requested for the last page,
		 * the model will be thrown since the records for the last page is not yet inserted
		 */
		
		/*model.setDataForTableLoading(true);
		model.setDataTableSourceExposed(model.getScrolledSource(from, to));
    	model.setDataForTableLoading(false);*/
		model.populateTableWithScrolledSource(from, to);
	}
	
	
	private void changeCurrentPageNum(int newVal) {
		PageButton[] btns = view.getPageBtns();
		PageButton btnFirst = view.getBtnFirst();
		PageButton btnPrev = view.getBtnPrev();
		PageButton btnNext = view.getBtnNext();
		PageButton btnLast = view.getBtnLast();
		
		int lastPageNum = model.getPagination().getLastRawPageNum();
		int firstPageNum = model.getPagination().getFirstRawPageNum();
		
		boolean isFirstPageSelected = (newVal == firstPageNum);
		boolean isLastPageSelected = (newVal == lastPageNum);

		for (PageButton btn : btns) {
			btn.setSelected(Integer.parseInt(btn.getText()) == newVal);
		}
		
		if(btnFirst != null) btnFirst.setVisible(!(isFirstPageSelected));		
		if(btnPrev != null) btnPrev.setVisible(!(isFirstPageSelected));
		if(btnNext != null) btnNext.setVisible(!(isLastPageSelected));
		if(btnLast != null) btnLast.setVisible(!(isLastPageSelected));
	}
	
	/* if data in model changes reflect it to the UI
	 * This part can be refactored, but I think its more readable this way
	 */
	public void propertyChange(PropertyChangeEvent evt){
		String propName = evt.getPropertyName();
		
		if(Constants.ModelFields.FN_PAGINATION.equalsIgnoreCase(propName)){
			Pagination p = model.getPagination();
			
			setupPageNumbersInView(p.getPageNumsExposed());
		}else if(Constants.ModelFields.FN_SORT_ORDER.equalsIgnoreCase(propName)){
			Pagination p = model.getPagination();
			PageButton[] btns = view.getPageBtns();
			
			if(btns.length > 0){
				pageButtonIsClicked(p.getCurrentPageNum());
			}
			
			view.getLblSortCol().setText(model.getSortColAsInUI());
			view.getLblSortDir().setText(model.getSortOrder().toString());
		}else if(Constants.ModelFields.FN_FILTER.equalsIgnoreCase(propName)){
			PageButton[] btns = view.getPageBtns();
			
			if(btns.length == 0){
				model.getPagedTableModel().setRowCount(0);
			}else{
				pageButtonIsClicked(1);
			}
			
			Filter f = model.getFilter();
			
			view.getLblFilterCol().setText(f.getColAsInUI());
			view.getLblFilterKey().setText(f.getKey());
			
		}else if(Constants.ModelFields.FN_COL_INFO_MAP.equalsIgnoreCase(propName)){
			setupFilterCategoryInView();
			DefaultTableModel mod = new DefaultTableModel(model.getColInfoMap().getColumnNames(), 0);
			
			model.setPagedTableModel(mod);
			view.getDataTable().setModel(model.getPagedTableModel());
		}else if(Constants.ModelFields.FN_IS_TABLE_LOADING.equalsIgnoreCase(propName)){
			boolean newVal = (Boolean) evt.getNewValue();
			
			if(newVal == true) view.showTableLoader();
			else view.hideTableLoader();
		}else if(Constants.ModelFields.FN_DATA_TABLE_SOURCE_ROW_COUNT.equalsIgnoreCase(propName)){
			view.getLblRowCount().setText(Integer.toString((Integer)evt.getNewValue()));
		}else if(Constants.ModelFields.FN_IS_DS_LOADING.equalsIgnoreCase(propName)){
			boolean newVal = (Boolean) evt.getNewValue();
			
			if(newVal == true) view.showLoadingDSNoti();
			else view.hideLoadingDSNoti();
		}
		
		//the following fragments should also be put in place ok.
		//|| DataBrowserModel.FN_DATA_TABLE_SOURCE_FORMAT.equalsIgnoreCase(propName)
		//|| DataBrowserModel.FN_COL_INFO_MAP.equalsIgnoreCase(propName)[ok]
	}
}