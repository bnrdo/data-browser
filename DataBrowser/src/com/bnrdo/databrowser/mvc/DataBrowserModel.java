package com.bnrdo.databrowser.mvc;

import java.beans.PropertyChangeListener;
import java.util.Arrays;
import java.util.List;

import javax.swing.JTextField;
import javax.swing.event.SwingPropertyChangeSupport;

import org.apache.commons.beanutils.BeanComparator;

import com.bnrdo.databrowser.Pagination;
import com.bnrdo.databrowser.comparator.SmartComparator;
import com.bnrdo.databrowser.exception.ModelException;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.FilterList;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.gui.TableFormat;
import ca.odell.glazedlists.migrationkit.swing.TextFilterList;
import ca.odell.glazedlists.swing.AdvancedTableModel;
import ca.odell.glazedlists.swing.GlazedListsSwing;

@SuppressWarnings("unchecked")
public class DataBrowserModel<E> {

	public static final String FN_DATA_TABLE_MODEL = "dataTableModel";
    public static final String FN_SEARCH_FILTER_LIST = "searchFilter";
    public static final String FN_PAGINATION = "pagination";

    private List<String> searchFilter;
    private Pagination pagination;
    private EventList<E> dataTableSource;
	private TableFormat<E> dataTableFormat;
    private AdvancedTableModel<E> dataTableModel;

    private SwingPropertyChangeSupport propChangeFirer;

    public DataBrowserModel() {
        propChangeFirer = new SwingPropertyChangeSupport(this);

        pagination = new Pagination();

        dataTableSource = new BasicEventList<E>();
        dataTableFormat = new BasicTableFormat();

        searchFilter = Arrays.asList("Filter 1", "Filter 2", "Filter 3",
                "Filter 4", "Filter 5");

        assembleTableModel(dataTableSource, dataTableFormat);
    }

    private void assembleTableModel(EventList<E> source, TableFormat<E> format) {
    	SortedList<E> sortedSource = new SortedList<E>(source, new SmartComparator<E>());
    	dataTableSource = sortedSource;
        dataTableModel = GlazedListsSwing.eventTableModelWithThreadProxyList(dataTableSource, format);
    }
    
    public AdvancedTableModel<E> getDataTableModel() {
        return dataTableModel;
    }
    
    public EventList<E> getDataTableSource() {
        return dataTableSource;
    }

    public TableFormat<E> getDataTableFormat() {
        return dataTableFormat;
    }
    
    public void setDataTableFormat(TableFormat<E> fmt){
    	TableFormat<E> oldVal = dataTableFormat;
    	dataTableFormat = fmt;
    	assembleTableModel(dataTableSource, fmt);
    	propChangeFirer.firePropertyChange(FN_DATA_TABLE_MODEL, oldVal, fmt);
    }
    
    public void setDataTableSource(EventList<E> list){
    	EventList<E> oldVal = dataTableSource;
    	dataTableSource = list;
    	assembleTableModel(dataTableSource, dataTableFormat);
    	propChangeFirer.firePropertyChange(FN_DATA_TABLE_MODEL, oldVal, list);
    }
    //overloaded version, for setting the datasource as filterable, no need to fire anything
    public void setDataTableSource(FilterList<E> list){
    	dataTableSource = list;
    	assembleTableModel(dataTableSource, dataTableFormat);
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
    	if(p.getTotalPagecount() < 1)
    		throw new ModelException("Pagination total page count should not be lesser than 1.");
    	
    	if(p.getMaxExposableCount() < 1)
    		throw new ModelException("Pagination max exposable count should not be lesser than 1.");
    	
    	if(p.getCurrentPageNum() < 1)
    		throw new ModelException("Pagination current page number should not be lesser than 1");
    
    	if(p.getMaxExposableCount() > p.getTotalPagecount())
    		throw new ModelException("Pagination max exposable count should be greater than or equal the total page count defined.");
    	
    	if(p.getCurrentPageNum() > p.getTotalPagecount())
    		throw new ModelException("Pagination current page number should not be greater than the total page count.");
    	
        Pagination oldVal = pagination;
        pagination = p;
        propChangeFirer.firePropertyChange(FN_PAGINATION, oldVal, p);
    }

    public Pagination getPagination() {
        return pagination;
    }

    public void addModelListener(PropertyChangeListener prop) {
        propChangeFirer.addPropertyChangeListener(prop);
    }

    private class BasicTableFormat implements TableFormat<E> {
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
