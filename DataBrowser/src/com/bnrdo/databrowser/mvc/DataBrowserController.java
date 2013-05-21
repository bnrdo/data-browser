package com.bnrdo.databrowser.mvc;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

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

	private void setUpTable() {
		JTable tblData = view.getDataTable();
		Multimap<Integer, Object> map = model.getColInfoMap();

		Object[] colNames = DataBrowserUtil.extractColNamesFromMap(map);
		DefaultTableModel tableModel = new DefaultTableModel(null, colNames);
		TableDataSourceFormat<E> fmt = model.getTableDataSourceFormat();

		for (E domain : model.getDataTableSourceExposed()) {
			tableModel.addRow(DataBrowserUtil.extractRowFromFormat(fmt, domain));
		}

		tblData.setModel(tableModel);
	}

	private void setUpPaginationUI(int[] pages) {

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
		final Pagination p = model.getPagination();
		
		if(where instanceof Integer){
			p.setCurrentPageNum((Integer)where);
		}else if(where instanceof String){
			p.setCurrentPageNum((String)where);
		}else {
			throw new ModelException("Invalid page click destination.");
		}
		
		setUpPaginationUI(p.getPageNumsExposed());
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
	
	private void setUpPagination(){
		model.getPagination().addPaginationListener(new PaginationListener() {
			@Override public void pageChanged(int pageNum) {
				List<E> sourceCopy = new ArrayList<E>(model.getDataTableSource());
				
				int itemCount = sourceCopy.size();
				int itemsPerPage = model.getPagination().getItemsPerPage();
				int lastItem = pageNum * itemsPerPage;
				int from = lastItem - itemsPerPage;
				int to = (lastItem > (itemCount)) ? itemCount : lastItem;
				
				List<E> pagedSrc = sourceCopy.subList(from, to);
				
				model.setDataTableSourceExposed(pagedSrc);
				
				sourceCopy = null;
				pagedSrc = null;
			}
		});
	}

	// if data in model changes reflect it to the UI
	public void propertyChange(PropertyChangeEvent evt){
		String propName = evt.getPropertyName();
		Object newVal = evt.getNewValue();

		if (DataBrowserModel.FN_PAGINATION.equalsIgnoreCase(propName)) {
			System.out.println("pagination changed");
			setUpPaginationUI(model.getPagination().getPageNumsExposed());
			setUpPagination();
			view.getPageBtns()[0].doClick();
		} else if (DataBrowserModel.FN_DATA_TABLE_SOURCE_EXPOSED.equalsIgnoreCase(propName)
				|| DataBrowserModel.FN_DATA_TABLE_SOURCE_FORMAT.equalsIgnoreCase(propName)
				|| DataBrowserModel.FN_COL_INFO_MAP.equalsIgnoreCase(propName)) {
			// if one of the three fired a property change, re-setup the table
			System.out.println("data source changed");
			setUpTable();
		}
	}
}