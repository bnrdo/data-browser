package com.bnrdo.databrowser.mvc;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import javax.swing.JComboBox;


import com.bnrdo.databrowser.Pagination;
import com.bnrdo.databrowser.exception.ViewException;
import com.bnrdo.databrowser.mvc.DataBrowserView.PageButton;

public class DataBrowserController implements PropertyChangeListener {
    private DataBrowserView view;
    private DataBrowserModel model;

    public DataBrowserController(DataBrowserView v, DataBrowserModel m) {
        view = v;
        model = m;
        model.addModelListener(this);
    }

    public void control() {
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

    public void propertyChange(PropertyChangeEvent evt) {
        String propName = evt.getPropertyName();
        Object newVal = evt.getNewValue();

        if (Pagination.FN_CURRENT_PAGE_NUMBER.equalsIgnoreCase(propName)
                && model.getPagination().getPageNumsExposed().length > 1) {
            PageButton [] btns = view.getPageBtns();
            int selectedPageNum = (Integer) newVal;
            int lastPageNum = model.getPagination().getLastRawPageNum();
            int firstPageNum = model.getPagination().getFirstRawPageNum();

            // synch button selection based on currentpagenum from the model and
            // its text

            // bug, kasi kapag last ung pinindot
            for (PageButton btn : btns) {
                btn
                        .setSelected(Integer.parseInt(btn.getText()) == selectedPageNum);
            }

            view.getBtnFirst().setVisible(!(selectedPageNum == firstPageNum));
            view.getBtnPrev().setVisible(!(selectedPageNum == firstPageNum));
            view.getBtnNext().setVisible(!(selectedPageNum == lastPageNum));
            view.getBtnLast().setVisible(!(selectedPageNum == lastPageNum));

        } else if (Pagination.FN_PAGE_NUMS_EXPOSED.equalsIgnoreCase(propName)) {
            int [] newNumsExposed = (int []) newVal;

            view.refreshPageNumbers(newNumsExposed);
            assemblePageEvents();
        } else if (DataBrowserModel.FN_SEARCH_FILTER_LIST
                .equalsIgnoreCase(propName)) {
            List<String> newFilters = (List<String>) newVal;
            JComboBox cbo = view.getCboSearch();
            cbo.removeAllItems();
            for (String items : newFilters) {
                cbo.addItem(items);
            }
        }
    }
}
