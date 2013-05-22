package com.bnrdo.databrowser.mvc;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import com.bnrdo.databrowser.DataBrowserUtil;
import com.bnrdo.databrowser.Pagination;
import com.bnrdo.databrowser.TableDataSourceFormat;
import com.bnrdo.databrowser.exception.ModelException;
import com.bnrdo.databrowser.listener.ModelListener;
import com.bnrdo.databrowser.listener.PaginationListener;
import com.bnrdo.databrowser.mvc.DataBrowserView.PageButton;
import com.google.common.collect.Multimap;

public class DataBrowserController<E> implements ModelListener {
	
	private DataBrowserView view;
	private DataBrowserModel<E> model;

	public DataBrowserController(DataBrowserView v, DataBrowserModel<E> m) {
		view = v;
		model = m;
		
		model.addModelListener(this);
	}

	private void renderDataInTableInView(JTable tbl, Multimap<Integer, Object> colInfo, 
							TableDataSourceFormat<E> fmt, List<E> source) {
		
		Object[] colNames = DataBrowserUtil.extractColNamesFromMap(colInfo);
		DefaultTableModel tableModel = new DefaultTableModel(null, colNames);

		for (E domain : model.getDataTableSourceExposed()) {
			tableModel.addRow(DataBrowserUtil.extractRowFromFormat(fmt, domain));
		}

		tbl.setModel(tableModel);
		
		TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(tableModel);
	    tbl.setRowSorter(sorter);
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
	

	private List<E> getPaginatedSource(Pagination p, List<E> source){
		List<E> retVal = new ArrayList<E>();
		List<E> sourceCopy = new ArrayList<E>(source);
		
		int itemsPerPage = p.getItemsPerPage();
		
		int itemCount = sourceCopy.size();
		int lastItem = p.getCurrentPageNum() * itemsPerPage;
		int from = lastItem - itemsPerPage;
		int to = (lastItem > (itemCount)) ? itemCount : lastItem;
		
		retVal = sourceCopy.subList(from, to);
		
		return retVal;
	}
	
	//if data in model changes reflect it to the UI
	public void propertyChange(PropertyChangeEvent evt){
		String propName = evt.getPropertyName();
		//Object newVal = evt.getNewValue();

		if (DataBrowserModel.FN_DATA_TABLE_SOURCE_EXPOSED.equalsIgnoreCase(propName)) {
			renderDataInTableInView(view.getDataTable(), model.getColInfoMap(), 
							model.getTableDataSourceFormat(), model.getDataTableSource());
		} /*else if(DataBrowserModel.FN_DATA_TABLE_SOURCE.equalsIgnoreCase(propName)){
			
		}*/ else if(DataBrowserModel.FN_PAGINATION.equalsIgnoreCase(propName)){
			Pagination p = model.getPagination();
			p.removePaginationListener(Pagination.PAGINATE_CONTENT_ID);
			p.addPaginationListener(Pagination.PAGINATE_CONTENT_ID, new PaginationListener() {
				@Override public void pageChanged(int pageNum) {
					model.setDataTableSourceExposed(getPaginatedSource(model.getPagination(), model.getDataTableSource()));
				}
			});
			
			renderPageNumbersInView(p.getPageNumsExposed());
			
			view.getPageBtns()[0].doClick();
		}
		
		//the following fragments should also be put in place ok.
		//|| DataBrowserModel.FN_DATA_TABLE_SOURCE_FORMAT.equalsIgnoreCase(propName)
		//|| DataBrowserModel.FN_COL_INFO_MAP.equalsIgnoreCase(propName)
	}
}