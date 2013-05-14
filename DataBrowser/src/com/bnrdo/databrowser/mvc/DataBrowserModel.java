package com.bnrdo.databrowser.mvc;

import java.beans.PropertyChangeListener;
import java.util.Arrays;
import java.util.List;

import javax.swing.event.SwingPropertyChangeSupport;

import com.bnrdo.databrowser.Pagination;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.gui.TableFormat;
import ca.odell.glazedlists.swing.AdvancedTableModel;
import ca.odell.glazedlists.swing.GlazedListsSwing;

@SuppressWarnings("unchecked")
public class DataBrowserModel {

    public static final String FN_SEARCH_FILTER_LIST = "searchFilter";
    public static final String FN_PAGINATION = "pagination";

    private List<String> searchFilter;

    private Pagination pagination;
    private EventList dataTableSource;
    private TableFormat dataTableFormat;
    private AdvancedTableModel dataTableModel;

    private SwingPropertyChangeSupport propChangeFirer;

    public DataBrowserModel() {
        propChangeFirer = new SwingPropertyChangeSupport(this);

        pagination = new Pagination();

        dataTableSource = new BasicEventList();
        dataTableFormat = new BasicTableFormat();

        searchFilter = Arrays.asList("Filter 1", "Filter 2", "Filter 3",
                "Filter 4", "Filter 5");

        assembleTableModel(dataTableSource, dataTableFormat);
    }

    private void assembleTableModel(EventList source, TableFormat format) {
        dataTableModel = GlazedListsSwing.eventTableModelWithThreadProxyList(
                source, format);
    }

    public AdvancedTableModel getDataTableModel() {
        return dataTableModel;
    }

    public EventList getDataTableSource() {
        return dataTableSource;
    }

    public TableFormat getDataTableFormat() {
        return dataTableFormat;
    }

    public List<String> getSearchFilters() {
        return searchFilter;
    }

    public void setSearchFilter(List<String> filter) {
        List<String> oldVal = searchFilter;
        searchFilter = filter;
        propChangeFirer.firePropertyChange(FN_SEARCH_FILTER_LIST, oldVal,
                filter);
    }

    public void setPagination(Pagination p) {
        /*
         * pagination.setCurrentPageNum(p.getCurrentPageNum()); //
         * pagination.setMaxExposableNums(p.getMaxExposableNums());
         * pagination.setPageNumsExposed(p.getPageNumsExposed());
         */
        Pagination oldVal = p;
        pagination = p;
        propChangeFirer.firePropertyChange(FN_PAGINATION, oldVal, p);
    }

    public Pagination getPagination() {
        return pagination;
    }

    public void addModelListener(PropertyChangeListener prop) {
        propChangeFirer.addPropertyChangeListener(prop);
    }

    private class BasicTableFormat implements TableFormat {
        public int getColumnCount() {
            return 5;
        }

        public String getColumnName(int arg0) {
            return "sample column";
        }

        public Object getColumnValue(Object arg0, int arg1) {
            return "table data missing - table format not specified";
        }
    }

}
