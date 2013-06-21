package com.bnrdo.databrowser.mvc;

import java.beans.PropertyChangeEvent;
import java.util.Arrays;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import org.apache.log4j.Logger;

import com.bnrdo.databrowser.ColumnInfoMap;
import com.bnrdo.databrowser.Constants;
import com.bnrdo.databrowser.DBroUtil;
import com.bnrdo.databrowser.Filter;
import com.bnrdo.databrowser.Pagination;
import com.bnrdo.databrowser.listener.ModelListener;
import com.bnrdo.databrowser.mvc.services.ControllerService;

public class DataBrowserController<E> implements ModelListener {
	
	private static org.apache.log4j.Logger log = Logger.getLogger(DataBrowserController.class);
	
	private DataBrowserView view;
	private DataBrowserModel<E> model;
	private ControllerService<E> service;

	public DataBrowserController(DataBrowserView v, DataBrowserModel<E> m) {
		view = v;
		model = m;
		service = new ControllerService<E>(view, model);
		model.addModelListener(this);
	}
	
	/* if data in model changes reflect it to the UI
	 */
	@SuppressWarnings("serial")
	public void propertyChange(PropertyChangeEvent evt){
		String propName = evt.getPropertyName();
		
		if(Constants.ModelFields.FN_PAGINATION.equalsIgnoreCase(propName)){
			log.debug("Notification from model property " + Constants.ModelFields.FN_PAGINATION + " received.");
			Pagination newVal = (Pagination) evt.getNewValue();
			service.setupPageNumbersInView(newVal.getPageNumsExposed(), newVal.getCurrentPageNum());
			
			JLabel lblRowCount = view.getLblRowCount();
			String currText = lblRowCount.getText();
			String info = " | " + DBroUtil.moneyFormat(newVal.getItemsPerPage()) + "/page | " + DBroUtil.moneyFormat(newVal.getTotalPagecount()) + " page" + (newVal.getTotalPagecount() <= 1 ? "" : "s");
			
			if(currText.contains("|")) 
				lblRowCount.setText(currText.substring(0, currText.indexOf("|")-1) + info);
			else 
				lblRowCount.setText(currText + info);
			
			lblRowCount.setVisible(model.isRowCountDetailsShowable());
			
		}else if(Constants.ModelFields.FN_SORT_ORDER.equalsIgnoreCase(propName)){
			log.debug("Notification from model property " + Constants.ModelFields.FN_SORT_ORDER + " received.");
			
			if(view.getPageBtns().length > 0){
				Pagination p = model.getPagination();
				service.pageButtonIsClicked(p.getCurrentPageNum());
				
				if(model.isStatusShowable())
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
				
				lblSort.setVisible(model.isSortDetailsShowable());
			} 
		}else if(Constants.ModelFields.FN_FILTER.equalsIgnoreCase(propName)){
			log.debug("Notification from model property " + Constants.ModelFields.FN_FILTER + " received.");
			
			view.showStatus("Filtering " + model.getSortColAsInUI() + "  '" + view.getTxtSearch().getText() + "'");
			service.pageButtonIsClicked(1);
			
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
		}else if(Constants.ModelFields.FN_COL_INFO_MAP.equalsIgnoreCase(propName)){
			log.debug("Notification from model property " + Constants.ModelFields.FN_COL_INFO_MAP + " received.");
			
			ColumnInfoMap newVal = (ColumnInfoMap) evt.getNewValue();
			model.setPagedTableModel(new DefaultTableModel(null, newVal.getColumnNames()){
				@Override public String toString(){
					StringBuilder bdr = new StringBuilder("Table Model Information -> [");
					bdr.append("Row Count : ").append(getRowCount()).append(", ")
						.append("Column Count : ").append(getColumnCount()).append(", ")
						.append("Column Names : ").append("(");
					
					for(int i = 0; i < getColumnCount(); i++){
						bdr.append(getColumnName(i));
						bdr.append((i+1 < getColumnCount()) ? ", " : "");
					}
					bdr.append("), for the content of the table, please inspect it your self.]");
					return bdr.toString();
				}
			});
		}else if(Constants.ModelFields.FN_IS_DATA_FOR_TABLE_LOADING.equalsIgnoreCase(propName)){
			log.debug("Notification from model property " + Constants.ModelFields.FN_IS_DATA_FOR_TABLE_LOADING + " received.");
			
			boolean newVal = (Boolean) evt.getNewValue();
			
			if(newVal == true){
				view.showTableLoader();
			}else{
				view.hideTableLoader();
				view.hideStatus();
			}
		}else if(Constants.ModelFields.FN_DATA_TABLE_SOURCE_ROW_COUNT.equalsIgnoreCase(propName)){
			log.debug("Notification from model property " + Constants.ModelFields.FN_DATA_TABLE_SOURCE_ROW_COUNT + " received.");
			
			JLabel lblRowCount = view.getLblRowCount();
			int newVal = ((Integer)evt.getNewValue()).intValue();
			
			lblRowCount.setIcon(new ImageIcon(getClass().getResource("/row_count2_16x16.png")));
			lblRowCount.setText(DBroUtil.moneyFormat(Integer.toString(newVal))
						+ " row" + (newVal == 1 ? "" : "s") + " returned");
			lblRowCount.setToolTipText("Total Record Count Returned : " + lblRowCount.getText());
			
			lblRowCount.setVisible(model.isRowCountDetailsShowable());
			
		}else if(Constants.ModelFields.FN_IS_DS_LOADING.equalsIgnoreCase(propName)){
			log.debug("Notification from model property " + Constants.ModelFields.FN_IS_DS_LOADING + " received.");
			
			boolean newVal = (Boolean) evt.getNewValue();
			
			if(newVal == true){
				view.disableSearch();
				if(model.isStatusShowable())
					view.showStatus("Loading datasource");
			}else{
				view.enableSearch();
				if(model.isStatusShowable())
					view.hideStatus();
			}
		}else if(Constants.ModelFields.FN_PAGED_TABLE_MODEL.equalsIgnoreCase(propName)){
			log.debug("Notification from model property " + Constants.ModelFields.FN_PAGED_TABLE_MODEL + " received.");
			
			DefaultTableModel newVal = (DefaultTableModel) evt.getNewValue();
			JTable tbl = view.getDataTable();
			
			tbl.setModel(newVal);
			service.setupDataTableInView();
			service.setupFilterInView();
		}
		//the following fragments should also be put in place ok.
		//|| DataBrowserModel.FN_DATA_TABLE_SOURCE_FORMAT.equalsIgnoreCase(propName)
		//|| DataBrowserModel.FN_COL_INFO_MAP.equalsIgnoreCase(propName)[ok]
	}
}