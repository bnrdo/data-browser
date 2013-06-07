package com.bnrdo.databrowser.mvc;

import java.awt.event.ActionEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import com.bnrdo.databrowser.ColumnInfoMap;
import com.bnrdo.databrowser.Constants;
import com.bnrdo.databrowser.DBroUtil;
import com.bnrdo.databrowser.PushableTableHeaderRenderer;
import com.bnrdo.databrowser.Pagination;
import com.bnrdo.databrowser.TableDataSourceFormat;
import com.bnrdo.databrowser.exception.ModelException;
import com.bnrdo.databrowser.listener.ModelListener;
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
				String key = txtSearch.getText();
				String col = colInfo.getPropertyName(cboSearch.getSelectedIndex());
				model.setFilter(key, col);
			}
		});
	}
	
	private void setupDataTableInView(final JTable tbl, 
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
		Pagination p = model.getPagination();
		
		if(where instanceof Integer){
			p.setCurrentPageNum((Integer)where);
		}else if(where instanceof String){
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
		
		new SwingWorker<Void, String>() {
			List<E> scrollSrc = null;
			boolean loaded;
			
	        @Override
	        protected Void doInBackground() throws Exception {
	        	loaded = false;
	        	
	        	while(!loaded){
	        		
		        	try{
						scrollSrc = model.getScrolledSource(from, to);
						loaded = true;
						break;
					}catch(ModelException e){
						System.out.println("message : ");
						e.printStackTrace();
					}
					try{
						Thread.sleep(1000);
					}catch(InterruptedException e){
						//e.printStackTrace();
					}
	        	}
				return null;
	        }
	        
	        @Override
	        protected void done() {
	        	model.setDataTableSourceExposed(scrollSrc);
	        }
	    }.execute();
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
		
		
		if (Constants.ModelFields.FN_DATA_TABLE_SOURCE_EXPOSED.equalsIgnoreCase(propName)) {
			setupDataTableInView(view.getDataTable(), 
								model.getColInfoMap(), 
								model.getTableDataSourceFormat());
		}else if(Constants.ModelFields.FN_PAGINATION.equalsIgnoreCase(propName)){
			Pagination p = model.getPagination();
			setupPageNumbersInView(p.getPageNumsExposed());
			
			// pagebtns array will be populated after executing setupPageNumbersInView
			PageButton[] btns = view.getPageBtns();
			if(btns.length > 0){
				int index = DBroUtil.getIndexOfNumFromArray(p.getPageNumsExposed(), p.getCurrentPageNum()); 
				btns[index].doClick();
			}
		}else if(Constants.ModelFields.FN_SORT_ORDER.equalsIgnoreCase(propName)){
			Pagination p = model.getPagination();
			PageButton[] btns = view.getPageBtns();
			
			if(btns.length > 0){
				int index = DBroUtil.getIndexOfNumFromArray(p.getPageNumsExposed(), p.getCurrentPageNum()); 
				btns[index].doClick();
			}
		}else if(Constants.ModelFields.FN_FILTER_KEY.equalsIgnoreCase(propName)){
			PageButton[] btns = view.getPageBtns();
			
			if(btns.length == 0){
				model.setDataTableSourceExposed(new ArrayList<E>());
			}else{
				int index = 0; 
				btns[index].doClick();
			}
		}else if(Constants.ModelFields.FN_COL_INFO_MAP.equalsIgnoreCase(propName)){
			setupFilterCategoryInView();
		}
		
		//the following fragments should also be put in place ok.
		//|| DataBrowserModel.FN_DATA_TABLE_SOURCE_FORMAT.equalsIgnoreCase(propName)
		//|| DataBrowserModel.FN_COL_INFO_MAP.equalsIgnoreCase(propName)[ok]
	}
}