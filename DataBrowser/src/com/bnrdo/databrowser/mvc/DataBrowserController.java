package com.bnrdo.databrowser.mvc;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.collections.comparators.ComparableComparator;
import org.apache.commons.collections.comparators.ReverseComparator;

import com.bnrdo.databrowser.ColumnInfoMap;
import com.bnrdo.databrowser.DataBrowserUtil;
import com.bnrdo.databrowser.Pagination;
import com.bnrdo.databrowser.TableDataSourceFormat;
import com.bnrdo.databrowser.exception.ModelException;
import com.bnrdo.databrowser.listener.ModelListener;
import com.bnrdo.databrowser.listener.PaginationListener;
import com.bnrdo.databrowser.mvc.DataBrowserView.PageButton;

public class DataBrowserController<E> implements ModelListener {
	
	private DataBrowserView view;
	private DataBrowserModel<E> model;

	public DataBrowserController(DataBrowserView v, DataBrowserModel<E> m) {
		view = v;
		model = m;
		
		model.addModelListener(this);
	}
	
	
	private void renderDataInTableInView(final JTable tbl, 
										final ColumnInfoMap colInfo, 
										final TableDataSourceFormat<E> fmt, List<E> source) {
		
		Object[] colNames = colInfo.getColumnNames();
		DefaultTableModel tableModel = new DefaultTableModel(null, colNames);

		for (E domain : model.getDataTableSourceExposed()) {
			tableModel.addRow(DataBrowserUtil.extractRowFromFormat(fmt, domain));
		}

		tbl.setModel(tableModel);
		
		JTableHeader header = tbl.getTableHeader();
		header.setReorderingAllowed(false);
		
		if(header.getMouseListeners().length <= 2){
			header.addMouseListener(new MouseAdapter() {
				@SuppressWarnings({"unchecked", "rawtypes"})
				@Override public void mouseReleased(MouseEvent evt){
					int selIndex = tbl.convertColumnIndexToModel(tbl.columnAtPoint(evt.getPoint()));
				
		        	if (!(selIndex < 0)) {
		        		List<E> copy = new ArrayList<E>(model.getDataTableSource());
		        		ColumnInfoMap map = model.getColInfoMap();
		        		String propNameToSort = map.getPropertyName(selIndex);
		        		Comparator comparatorToUse = DataBrowserUtil.getComparator(map.getPropertyClass(selIndex));
		        		BeanComparator beanComp = new BeanComparator(propNameToSort, comparatorToUse);
		        		
		        		if(model.getSortOrder() == DataBrowserModel.SORT_ASC){
		        			Collections.sort(copy, new ReverseComparator(beanComp));
		        			model.setSortOrder(DataBrowserModel.SORT_DESC);
		        		}else{
			        		Collections.sort(copy, beanComp);
		        			model.setSortOrder(DataBrowserModel.SORT_ASC);
		        		}
		        	    
		        	    model.setDataTableSource(copy);
		        	}
				}
			});
		}
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
		} else if(DataBrowserModel.FN_DATA_TABLE_SOURCE.equalsIgnoreCase(propName)){
			Pagination p = model.getPagination();
			int srcSize = model.getDataTableSource().size();
			int itemsPerPage = p.getItemsPerPage();
			int pageCountForEvenSize = (srcSize / itemsPerPage);
			p.setTotalPageCount((srcSize % itemsPerPage) == 0 ? pageCountForEvenSize : pageCountForEvenSize + 1);
			p.removePaginationListener(Pagination.PAGINATE_CONTENT_ID);
			p.addPaginationListener(Pagination.PAGINATE_CONTENT_ID, new PaginationListener() {
				@Override public void pageChanged(int pageNum) {
					model.setDataTableSourceExposed(getPaginatedSource(model.getPagination(), model.getDataTableSource()));
				}
			});
			
			renderPageNumbersInView(p.getPageNumsExposed());
			///nageeror kapag pinage last then sort plangak investigate pls
			view.getPageBtns()[DataBrowserUtil.getIndexOfNumFromArray(p.getPageNumsExposed(), p.getCurrentPageNum())].doClick();
		}
//		} else if(DataBrowserModel.FN_PAGINATION.equalsIgnoreCase(propName)){
//			Pagination p = model.getPagination();
//			p.removePaginationListener(Pagination.PAGINATE_CONTENT_ID);
//			p.addPaginationListener(Pagination.PAGINATE_CONTENT_ID, new PaginationListener() {
//				@Override public void pageChanged(int pageNum) {
//					model.setDataTableSourceExposed(getPaginatedSource(model.getPagination(), model.getDataTableSource()));
//				}
//			});
//			
//			renderPageNumbersInView(p.getPageNumsExposed());
//			
//			view.getPageBtns()[0].doClick();
//		}
		
		//the following fragments should also be put in place ok.
		//|| DataBrowserModel.FN_DATA_TABLE_SOURCE_FORMAT.equalsIgnoreCase(propName)
		//|| DataBrowserModel.FN_COL_INFO_MAP.equalsIgnoreCase(propName)
	}
}