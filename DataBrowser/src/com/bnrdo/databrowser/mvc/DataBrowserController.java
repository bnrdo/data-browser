package com.bnrdo.databrowser.mvc;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import com.bnrdo.databrowser.DataBrowserUtil;
import com.bnrdo.databrowser.Pagination;
import com.bnrdo.databrowser.TableDataSourceFormat;
import com.bnrdo.databrowser.exception.ViewException;
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

	private void setUpTableFromModelChange() {
		JTable tblData = view.getDataTable();
		Multimap<Integer, Object> map = model.getColInfoMap();

		Object[] colNames = DataBrowserUtil.extractColNamesFromMap(map);
		DefaultTableModel tableModel = new DefaultTableModel(null, colNames);
		TableDataSourceFormat<E> fmt = model.getTableDataSourceFormat();

		for (E domain : model.getDataTableSource()) {
			tableModel.addRow(DataBrowserUtil.extractRowFromFormat(fmt, domain));
		}

		tblData.setModel(tableModel);
		
		//apply the pagination set by client
		Pagination p = new Pagination();
		p.setCurrentPageNum(Pagination.FIRST_PAGE);
		p.setMaxExposableCount(9);
		p.setTotalPageCount(model.getDataTableSource().size());
		model.setPagination(p);
	}

	private void setUpPaginationFromModelChange(int[] pages) {

		view.createPageButtons(pages);

		final PageButton[] pageBtns = view.getPageBtns();
		final Pagination p = model.getPagination();

		if (p.getPageNumsExposed().length > 1) {

			view.getBtnFirst().addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					p.setCurrentPageNum(Pagination.FIRST_PAGE);
					changePageNumsExposed(p.getPageNumsExposed());
					changeCurrentPageNum(p.getCurrentPageNum());
				}
			});
			view.getBtnPrev().addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					p.setCurrentPageNum(Pagination.PREV_PAGE);
					changePageNumsExposed(p.getPageNumsExposed());
					changeCurrentPageNum(p.getCurrentPageNum());
				}
			});
			view.getBtnNext().addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					p.setCurrentPageNum(Pagination.NEXT_PAGE);
					changePageNumsExposed(p.getPageNumsExposed());
					changeCurrentPageNum(p.getCurrentPageNum());
				}
			});
			view.getBtnLast().addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					p.setCurrentPageNum(Pagination.LAST_PAGE);
					changePageNumsExposed(p.getPageNumsExposed());
					changeCurrentPageNum(p.getCurrentPageNum());
				}
			});
		}

		for (final PageButton btn : pageBtns) {
			btn.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					p.setCurrentPageNum(Integer.parseInt(btn.getText()));
					changePageNumsExposed(p.getPageNumsExposed());
					changeCurrentPageNum(p.getCurrentPageNum());
				}
			});
		}
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

	private void changePageNumsExposed(int[] newVal) {
		setUpPaginationFromModelChange(newVal);
	}

	// if data in model changes reflect it to the UI
	public void propertyChange(PropertyChangeEvent evt) {
		String propName = evt.getPropertyName();
		Object newVal = evt.getNewValue();

		if (DataBrowserModel.FN_PAGINATION.equalsIgnoreCase(propName)) {
			setUpPaginationFromModelChange(model.getPagination().getPageNumsExposed());
			view.getPageBtns()[0].doClick();
		} else if (DataBrowserModel.FN_DATA_TABLE_SOURCE
				.equalsIgnoreCase(propName)
				|| DataBrowserModel.FN_DATA_TABLE_SOURCE_FORMAT
						.equalsIgnoreCase(propName)
				|| DataBrowserModel.FN_COL_INFO_MAP.equalsIgnoreCase(propName)) {
			// if one of the three fired a property change, re-setup the table
			setUpTableFromModelChange();
		}
	}
}