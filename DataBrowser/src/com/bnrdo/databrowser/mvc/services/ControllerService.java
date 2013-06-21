package com.bnrdo.databrowser.mvc.services;

import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import com.bnrdo.databrowser.ColumnInfoMap;
import com.bnrdo.databrowser.Filter;
import com.bnrdo.databrowser.Pagination;
import com.bnrdo.databrowser.PushableTableHeaderRenderer;
import com.bnrdo.databrowser.exception.ModelException;
import com.bnrdo.databrowser.listener.PushableTableHeaderListener;
import com.bnrdo.databrowser.listener.TableSortListener;
import com.bnrdo.databrowser.mvc.DataBrowserModel;
import com.bnrdo.databrowser.mvc.DataBrowserView;
import com.bnrdo.databrowser.mvc.DataBrowserView.PageButton;

public class ControllerService<E> {
	
	private DataBrowserView view;
	private DataBrowserModel<E> model;
	
	public ControllerService(DataBrowserView v, DataBrowserModel<E> m){
		view = v;
		model = m;
	}
	
	@SuppressWarnings("serial")
	public void setupFilterInView() {
		final JComboBox cbo = view.getCboSearch();
		final JButton btn = view.getBtnSearch();
		final JTextField txt = view.getTxtSearch();
		final ColumnInfoMap colInfo = model.getColInfoMap();

		cbo.removeAllItems();

		for (Integer t : colInfo.getKeySet()) {
			cbo.addItem(colInfo.getColumnName(t));
		}

		btn.setAction(new AbstractAction("Search") {
			@Override public void actionPerformed(ActionEvent arg0) {
				int index = cbo.getSelectedIndex();
				filter(index,
						txt.getText(),
						colInfo.getPropertyName(index),
						colInfo.getColumnName(index));
			}
		});
		
		txt.addKeyListener(new KeyAdapter() {
			@Override public void keyPressed(KeyEvent evt){
				if(evt.getKeyCode() == KeyEvent.VK_ENTER){
					int index = cbo.getSelectedIndex();
					filter(index,
							txt.getText(),
							colInfo.getPropertyName(index),
							colInfo.getColumnName(index));
				}
			}
		});
	}
	
	public void setupDataTableInView() {
		JTable tbl = view.getDataTable();
		JTableHeader header = tbl.getTableHeader();
		TableColumnModel colModel = tbl.getColumnModel();

		PushableTableHeaderRenderer renderer = new PushableTableHeaderRenderer();
		PushableTableHeaderListener ptListen = new PushableTableHeaderListener(
				header, renderer);

		// renderer the column header with a button for push effect
		for (int i = 0; i < colModel.getColumnCount(); i++) {
			TableColumn tc = colModel.getColumn(i);
			tc.setHeaderRenderer(renderer);
		}

		// remove listener if already existing
		for (MouseListener ms : header.getMouseListeners()) {
			if (ms instanceof PushableTableHeaderListener
					|| ms instanceof TableSortListener) {
				header.removeMouseListener(ms);
			}
		}

		for (MouseMotionListener ms : header.getMouseMotionListeners()) {
			if (ms instanceof PushableTableHeaderListener) {
				header.removeMouseMotionListener(ms);
			}
		}

		header.addMouseListener(ptListen);
		header.addMouseMotionListener(ptListen);
		header.addMouseListener(new TableSortListener<E>(tbl, model));

		tbl.repaint();
	}

	@SuppressWarnings("serial")
	public void setupPageNumbersInView(int[] pages, int currentPageNum) {

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
		
		PageButton[] btns = view.getPageBtns();
		PageButton btnFirst = view.getBtnFirst();
		PageButton btnPrev = view.getBtnPrev();
		PageButton btnNext = view.getBtnNext();
		PageButton btnLast = view.getBtnLast();
		
		int lastPageNum = model.getPagination().getLastRawPageNum();
		int firstPageNum = model.getPagination().getFirstRawPageNum();
		
		boolean isFirstPageSelected = (currentPageNum == firstPageNum);
		boolean isLastPageSelected = (currentPageNum == lastPageNum);

		for (PageButton btn : btns) {
			btn.setSelected(Integer.parseInt(btn.getText()) == currentPageNum);
		}
		
		if(btnFirst != null) btnFirst.setVisible(!(isFirstPageSelected));		
		if(btnPrev != null) btnPrev.setVisible(!(isFirstPageSelected));
		if(btnNext != null) btnNext.setVisible(!(isLastPageSelected));
		if(btnLast != null) btnLast.setVisible(!(isLastPageSelected));
	}
	
	public void pageButtonIsClicked(Object where){
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
		
		/*System.out.println("items per page : " + itemsPerPage);
		System.out.println("item count : " + itemCount);
		System.out.println("last item : " + lastItem);
		System.out.println("from : " + from);
		System.out.println("to : " + to);*/
		
		
		
		model.populateViewTableWithScrolledSource(from, to);
	}

	private void filter(int colIndex, String key, String propName, String colNameAsInUI){
		Filter f = new Filter();
		f.setKey(key);
		f.setCol(propName);
		f.setColAsInUI(colNameAsInUI);
		model.setFilter(f);
	}

}
