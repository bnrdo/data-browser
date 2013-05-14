package com.bnrdo.databrowser.mvc;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import javax.swing.JComboBox;


import com.bnrdo.databrowser.Pagination;
import com.bnrdo.databrowser.listener.ModelListener;
import com.bnrdo.databrowser.listener.PaginationListener;
import com.bnrdo.databrowser.exception.ViewException;
import com.bnrdo.databrowser.mvc.DataBrowserView.PageButton;

public class DataBrowserController implements ModelListener {
    private DataBrowserView view;
    private DataBrowserModel model;

    public DataBrowserController(DataBrowserView v, DataBrowserModel m) {
        view = v;
        model = m;
        model.addModelListener(this);
    }

    public void control() {
    	assembleDataTable();
    }
    
    private void assembleDataTable(){
    	view.getDataTable().setModel(model.getDataTableModel());
    }

    private void assemblePageEvents() {
        if (view.getBtnFirst() == null
                && model.getPagination().getPageNumsExposed().length > 1) {
            throw new ViewException(
                    "The view for page numbers must be completely setup first before assembling its events");
        }

        final PageButton [] pageBtns = view.getPageBtns();

        if (model.getPagination().getPageNumsExposed().length > 1) {

            view.getBtnFirst().addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    model.getPagination().setCurrentPageNum(
                            Pagination.FIRST_PAGE);
                }
            });
            view.getBtnPrev().addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    model.getPagination().setCurrentPageNum(
                            Pagination.PREV_PAGE);
                }
            });
            view.getBtnNext().addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    model.getPagination().setCurrentPageNum(
                            Pagination.NEXT_PAGE);
                }
            });
            view.getBtnLast().addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    model.getPagination().setCurrentPageNum(
                            Pagination.LAST_PAGE);
                }
            });
        }

        for (final PageButton btn : pageBtns) {
            btn.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    int i = Integer.parseInt(btn.getText());
                    model.getPagination().setCurrentPageNum(i);
                }
            });
        }

        // model.getPagination().setCurrentPageNum(
        // Integer.parseInt(view.getPageBtns()[0].getText()));
    }

    /* Code when the model has changed some of its property */

    
    private void pageNumsExposedChanged(int[] newVal){
        view.refreshPageNumbers(newVal);
        assemblePageEvents();
    }
    private void currentPageNumChanged(int newVal){
    	PageButton [] btns = view.getPageBtns();
        int lastPageNum = model.getPagination().getLastRawPageNum();
        int firstPageNum = model.getPagination().getFirstRawPageNum();

        // synch button selection based on currentpagenum from the model and
        // its text
        
        for (PageButton btn : btns) {
            btn.setSelected(Integer.parseInt(btn.getText()) == newVal);
        }

        view.getBtnFirst().setVisible(!(newVal == firstPageNum));
        view.getBtnPrev().setVisible(!(newVal == firstPageNum));
        view.getBtnNext().setVisible(!(newVal == lastPageNum));
        view.getBtnLast().setVisible(!(newVal == lastPageNum));
    }
    
    public void propertyChange(PropertyChangeEvent evt) {
        String propName = evt.getPropertyName();
        Object newVal = evt.getNewValue();

        if(DataBrowserModel.FN_PAGINATION.equalsIgnoreCase(propName)){
        	
        	final Pagination newPagination = (Pagination) newVal;
        	pageNumsExposedChanged(newPagination.getPageNumsExposed());
            currentPageNumChanged(newPagination.getCurrentPageNum());
            
            newPagination.addPaginationListener(new PaginationListener(){
				public void pageChanged(int pageNum) {
					pageNumsExposedChanged(newPagination.getPageNumsExposed());
		            currentPageNumChanged(newPagination.getCurrentPageNum());
				}
            });
        }
        else if (DataBrowserModel.FN_SEARCH_FILTER_LIST
                .equalsIgnoreCase(propName)) {
            List<String> newFilters = (List<String>) newVal;
            JComboBox cbo = view.getCboSearch();
            cbo.removeAllItems();
            for (String items : newFilters) {
                cbo.addItem(items);
            }
        }
        else if(DataBrowserModel.FN_DATA_TABLE_MODEL
                .equalsIgnoreCase(propName)){
        	assembleDataTable();
        	
        }
    }
}
