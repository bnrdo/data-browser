package com.bnrdo.databrowser.mvc.services;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import com.bnrdo.databrowser.ColumnInfoMap;
import com.bnrdo.databrowser.Constants;
import com.bnrdo.databrowser.Filter;
import com.bnrdo.databrowser.PushableTableHeaderRenderer;
import com.bnrdo.databrowser.exception.ModelException;
import com.bnrdo.databrowser.listener.PushableTableHeaderListener;
import com.bnrdo.databrowser.listener.TableSortListener;
import com.bnrdo.databrowser.mvc.DataBrowserModel;
import com.bnrdo.databrowser.mvc.DataBrowserView;
import com.bnrdo.databrowser.mvc.DataBrowserView.PageButton;
import com.bnrdo.pagination.listener.PaginationListener;

public class ControllerService<E> {
	
	private DataBrowserView view;
	private DataBrowserModel<E> model;
	
	public ControllerService(DataBrowserView v, DataBrowserModel<E> m){
		view = v;
		model = m;
	}
	
	@SuppressWarnings("serial")
	public void setupSettingsInView(){
		view.getBtnSettings().setAction(new AbstractAction("S") {
			@Override public void actionPerformed(ActionEvent arg0) {
				JPanel pnlSettings = view.getPnlSettings();
				
				if(pnlSettings.isVisible()){
					pnlSettings.setVisible(false);
				}else{
					pnlSettings.setVisible(true);
					pnlSettings.repaint();
				}
			}
		});
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
				
				txt.requestFocusInWindow();
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
					
					txt.requestFocusInWindow();
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
	
	public void setupPaginationInView(){
		view.getPagination().addPaginationListener("", new PaginationListener(){
			@Override public void pageChanged(int num) { }
			@Override public void pageSelected(int num) {
				pageButtonIsClicked(num);
			}
		});
		
		view.getPagination().setCurrentPageNum(1);
	}
	
	public void setupFilterStatus(){
		Filter f = model.getFilter();
		JLabel lblFilter = view.getLblFilter();

		if(f.getKey().equals("")){
			lblFilter.setVisible(false);
		}else{
			lblFilter.setText(f.getColAsInUI() + " : '" + f.getKey() + "'");
			lblFilter.setIcon(new ImageIcon(getClass().getResource("/filter_16x16.gif")));
			lblFilter.setToolTipText("Column Filtered : " + lblFilter.getText().split(":")[0] 
											+ " | Keyword : " + lblFilter.getText().split(":")[1]);
			
			lblFilter.setVisible(model.isFilterDetailsShowable());
		}
	}
	
	public void setupSortStatus(){
		JLabel lblSort = view.getLblSort();
		lblSort.setText(model.getSortColAsInUI());
		if(model.getSortOrder().equals(Constants.SortOrder.ASC)){
			lblSort.setIcon(new ImageIcon(getClass().getResource("/sort_asc_16x16.png")));
			lblSort.setToolTipText("Column Sorted : " + lblSort.getText() + " | Sort Order : Ascending");
		}else{
			lblSort.setIcon(new ImageIcon(getClass().getResource("/sort_desc_16x16.png")));
			lblSort.setToolTipText("Column Sorted : " + lblSort.getText() + " | Sort Order : Descending");
		}
		
		lblSort.setVisible(model.isSortDetailsShowable());
	}

	public void pageButtonIsClicked(int pageNum){
		//computation for offset and limit of sql query
		int itemsPerPage = model.getItemsPerPage();
		int itemCount = model.getDataSourceRowCount();
		int lastItem = pageNum * itemsPerPage;
		
		final int from = lastItem - itemsPerPage;
		final int to = (lastItem > (itemCount)) ? itemCount : lastItem;
		
		System.out.println("items per page : " + itemsPerPage);
		System.out.println("item count : " + itemCount);
		System.out.println("last item : " + lastItem);
		System.out.println("from : " + from);
		System.out.println("to : " + to);
		
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
