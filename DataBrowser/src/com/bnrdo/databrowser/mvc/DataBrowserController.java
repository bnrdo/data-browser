package com.bnrdo.databrowser.mvc;

import java.beans.PropertyChangeEvent;
import java.util.Arrays;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import org.apache.log4j.Logger;

import com.bnrdo.databrowser.ColumnInfoMap;
import com.bnrdo.databrowser.Constants.ModelField;
import com.bnrdo.databrowser.DBroUtil;
import com.bnrdo.databrowser.listener.ModelListener;
import com.bnrdo.databrowser.mvc.services.ControllerService;
import com.bnrdo.pagination.Pagination;

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
	
	public void afterMVCInit(){
		service.setupDataTableInView();
		service.setupFilterInView();
		service.setupPaginationInView();
		service.setupSettingsInView();
	}
	
	/* if data in model changes reflect it to the UI
	 */
	@SuppressWarnings("serial")
	public void propertyChange(PropertyChangeEvent evt){
		String propName = evt.getPropertyName();
		
		log.debug("Notification received from " + propName);
		
		switch(ModelField.valueOf(propName)){
			case totalPageCount : 
				view.getPagination().setCount((Integer) evt.getNewValue());
				break;
				
			case maxExposableCount :
				view.getPagination().setExposableCount((Integer) evt.getNewValue());
				break;
				
			case sortOrder : {
				Pagination p = view.getPagination();
				
				if(p.getCount() > 0){
					//reset page num to populate the table with re-queried data.
					p.setCurrentPageNum(p.getCurrentPageNum());
					
					/* status and stuff */
					if(model.isStatusShowable()){
						view.showStatus("Sorting by " + model.getSortColAsInUI() + " " + model.getSortOrder().toString());
					}
					service.setupSortStatus();
				}
				
				break;
			}
			case filterKey : {
				Pagination p = view.getPagination(); //0 page count means no record to display
				
				if(p.getCount() > 0){
					/* reset current page num to populate the table with re-queried data */
					p.setCurrentPageNum(p.getCurrentPageNum());
					
					/* status and stuff */
					if(model.isStatusShowable()){
						view.showStatus("Filtering " + model.getSortColAsInUI() + "  '" + view.getTxtSearch().getText() + "'");
					}
					service.setupFilterStatus();
				}else{
					model.getPagedTableModel().setRowCount(0);
				}
				break;
			}	
			case colInfoMap : {
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
				
				break;
			}
			case isDataForTableLoading : {
				boolean newVal = (Boolean) evt.getNewValue();
				
				if(newVal == true){
					view.showTableLoader();
				}else{
					view.hideTableLoader();
					view.hideStatus();
				}
				
				break;
			}
			case dataSourceRowCount : {
				int newVal = ((Integer)evt.getNewValue()).intValue();
				
				JLabel lblRowCount = view.getLblRowCount();
				lblRowCount.setIcon(new ImageIcon(getClass().getResource("/row_count2_16x16.png")));
				lblRowCount.setText(DBroUtil.moneyFormat(newVal) + " row" + (newVal == 1 ? "" : "s") + " returned");
				lblRowCount.setToolTipText("Total Record Count Returned : " + lblRowCount.getText());
				lblRowCount.setVisible(model.isRowCountDetailsShowable());
				
				break;
			}
			case isDataSourceLoading : {
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
				
				break;
			}
			case pagedTableModel : {
				view.getDataTable().setModel((DefaultTableModel) evt.getNewValue());
				break;
			}
				
			default:
				break;
		}
		//the following fragments should also be put in place ok.
		//|| DataBrowserModel.FN_DATA_TABLE_SOURCE_FORMAT.equalsIgnoreCase(propName)
		//|| DataBrowserModel.FN_COL_INFO_MAP.equalsIgnoreCase(propName)[ok]
	}
}