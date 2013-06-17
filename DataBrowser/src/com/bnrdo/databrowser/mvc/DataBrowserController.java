package com.bnrdo.databrowser.mvc;

import java.awt.event.ActionEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
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
	private void setupFilterCategoryInView(final ColumnInfoMap colInfo, 
											final JTextField txt, 
											final JComboBox cbo, 
											final JButton btn){
		
		cbo.removeAllItems();
		
		for(Integer t : colInfo.getKeySet()){
			cbo.addItem(colInfo.getColumnName(t));
		}
		
		btn.setAction(new AbstractAction("Search") {
			@Override
			public void actionPerformed(ActionEvent arg0){
				System.out.println("pindolya");
				int index = cbo.getSelectedIndex();
				Filter f= new Filter();
				f.setKey(txt.getText());
				f.setCol(colInfo.getPropertyName(index));
				f.setColAsInUI(colInfo.getColumnName(index));
				model.setFilter(f);
			}
		});
	}
	
	private void setupDataTableInView(final JTable tbl, 
											final ColumnInfoMap colInfo, 
											final TableDataSourceFormat<E> fmt) {
		
	
		JTableHeader header = tbl.getTableHeader();
		TableColumnModel colModel = tbl.getColumnModel();
		
		PushableTableHeaderRenderer renderer = new PushableTableHeaderRenderer();
		PushableTableHeaderListener ptListen = new PushableTableHeaderListener(header, renderer);
			
		//renderer the column header with a button for push effect
		for(int i = 0 ; i < colModel.getColumnCount(); i++){
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
		header.addMouseMotionListener(ptListen);
		header.addMouseListener(new TableSortListener<E>(tbl, model));
		
		tbl.repaint();
	}

	@SuppressWarnings("serial")
	private void setupPageNumbersInView(int[] pages, int currentPageNum) {

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
		changeCurrentPageNum(currentPageNum);
	}

	private void pageButtonIsClicked(Object where){
		final Pagination p = new Pagination(model.getPagination());
		
		if(where instanceof Integer){
			p.setCurrentPageNum((Integer)where);
		}else if(where instanceof String){
			p.setCurrentPageNum((String)where);
		}else {
			throw new ModelException("Invalid page click destination.");
		}
		
		model.setPagination(p);
 
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
	 */
	public void propertyChange(PropertyChangeEvent evt){
		String propName = evt.getPropertyName();
		
		if(Constants.ModelFields.FN_PAGINATION.equalsIgnoreCase(propName)){
			Pagination newVal = (Pagination) evt.getNewValue();
			setupPageNumbersInView(newVal.getPageNumsExposed(), newVal.getCurrentPageNum());
		}else if(Constants.ModelFields.FN_SORT_ORDER.equalsIgnoreCase(propName)){
			
			if(view.getPageBtns().length > 0){
				Pagination p = model.getPagination();
				pageButtonIsClicked(p.getCurrentPageNum());
				
				view.showStatus("Sorting by " + model.getSortColAsInUI() + " " + model.getSortOrder().toString());
				
				JLabel lblSort = view.getLblSort();
				lblSort.setText(model.getSortColAsInUI());
				if(model.getSortOrder().equals(Constants.SORT_ORDER.ASC)){
					lblSort.setIcon(new ImageIcon(getClass().getResource("/sort_asc_16x16.png")));
					lblSort.setToolTipText("Column Sorted : " + lblSort.getText() + " | Sort Order : Ascending");
				}else{
					lblSort.setIcon(new ImageIcon(getClass().getResource("/sort_desc_16x16.png")));
					lblSort.setToolTipText("Column Sorted : " + lblSort.getText() + " | Sort Order : Descending");
				}
				//view.getLblSortDir().setText(model.getSortOrder().toString());
			} 
		}else if(Constants.ModelFields.FN_FILTER.equalsIgnoreCase(propName)){
			view.showStatus("Filtering by " + model.getSortColAsInUI() + " Keyword : " + model.getSortOrder().toString());
			pageButtonIsClicked(1);
			
			Filter f = model.getFilter();
			JLabel lblFilter = view.getLblFilter();
			
			if(f.getKey().equals("")){
				lblFilter.setVisible(false);
			}else{
				lblFilter.setText(f.getColAsInUI() + " : '" + f.getKey() + "'");
				lblFilter.setIcon(new ImageIcon(getClass().getResource("/filter_16x16.gif")));
				lblFilter.setToolTipText("Column Filtered : " + lblFilter.getText().split(":")[0] 
												+ " | Keyword : " + lblFilter.getText().split(":")[1]);
				lblFilter.setVisible(true);
			}
		}else if(Constants.ModelFields.FN_COL_INFO_MAP.equalsIgnoreCase(propName)){
			ColumnInfoMap newVal = (ColumnInfoMap) evt.getNewValue();
			model.setPagedTableModel(new DefaultTableModel(null, newVal.getColumnNames()));
		}else if(Constants.ModelFields.FN_IS_TABLE_LOADING.equalsIgnoreCase(propName)){
			boolean newVal = (Boolean) evt.getNewValue();
			
			if(newVal == true){
				view.showTableLoader();
			}else{
				view.hideTableLoader();
				view.hideStatus();
			}
		}else if(Constants.ModelFields.FN_DATA_TABLE_SOURCE_ROW_COUNT.equalsIgnoreCase(propName)){
			JLabel lblRowCount = view.getLblRowCount();
			lblRowCount.setIcon(new ImageIcon(getClass().getResource("/row_count2_16x16.png")));
			lblRowCount.setText(Integer.toString((Integer)evt.getNewValue()));
			lblRowCount.setToolTipText("Total Record Count Returned : " + lblRowCount.getText());
		}else if(Constants.ModelFields.FN_IS_DS_LOADING.equalsIgnoreCase(propName)){
			boolean newVal = (Boolean) evt.getNewValue();
			
			if(newVal == true){
				view.disableSearch();
				view.showStatus("Loading datasource");
			}else{
				view.enableSearch();
				view.hideStatus();
			}
		}else if(Constants.ModelFields.FN_PAGED_TABLE_MODEL.equalsIgnoreCase(propName)){
			DefaultTableModel newVal = (DefaultTableModel) evt.getNewValue();
			ColumnInfoMap colInfo = model.getColInfoMap();
			JTable tbl = view.getDataTable();
			
			tbl.setModel(newVal);
			setupDataTableInView(tbl, colInfo, model.getTableDataSourceFormat());
			setupFilterCategoryInView(colInfo, view.getTxtSearch(), view.getCboSearch(), view.getBtnSearch());
		}
		
		//the following fragments should also be put in place ok.
		//|| DataBrowserModel.FN_DATA_TABLE_SOURCE_FORMAT.equalsIgnoreCase(propName)
		//|| DataBrowserModel.FN_COL_INFO_MAP.equalsIgnoreCase(propName)[ok]
	}
}