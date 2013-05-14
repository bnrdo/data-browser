package com.bnrdo.databrowser;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.apache.commons.lang3.ArrayUtils;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.gui.TableFormat;

import com.bnrdo.databrowser.DataBrowser;
import com.bnrdo.databrowser.Pagination;
import com.bnrdo.databrowser.listener.PaginationListener;
import com.bnrdo.databrowser.mvc.DataBrowserModel;

@SuppressWarnings("unchecked")
public class DataBrowserMain {
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@SuppressWarnings({ "unchecked", "serial" })
			public void run() {
				try {
					UIManager
							.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
				} catch (ClassNotFoundException e1) {
					e1.printStackTrace();
				} catch (InstantiationException e1) {
					e1.printStackTrace();
				} catch (IllegalAccessException e1) {
					e1.printStackTrace();
				} catch (UnsupportedLookAndFeelException e1) {
					e1.printStackTrace();
				}
				
				DataBrowserMain creator = new DataBrowserMain();

				JFrame frame = new JFrame("Demo");
				frame.getContentPane().setLayout(new BorderLayout());
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.getContentPane().add(creator.createDataBrowser(),
						BorderLayout.CENTER);
				frame.pack();
				frame.setVisible(true);
			}
		});
	}

	private DataBrowser createDataBrowser() {
		final DataBrowser dbrowse = new DataBrowser();
		final DataBrowserModel dmodel = dbrowse.getModel();
		final EventList tableDataSource =  dmodel.getDataTableSource();
		final List<String[]> source = populateSource();
		
		final int rowCount = source.size();
		final int maxExposable = 10;
		final int itemsPerPage = 10;
		
		dmodel.setDataTableFormat(new BasicTableFormat());
		
		Pagination p = new Pagination();
		p.setMaxExposableCount(maxExposable);
		p.setTotalPageCount(rowCount / itemsPerPage);
		p.addPaginationListener(new PaginationListener() {
			public void pageChanged(int pageNum) {
				List<String[]> sourceCopy = new ArrayList<String[]>(source);
				int from = (pageNum * itemsPerPage) - itemsPerPage;
				int to = (pageNum * itemsPerPage);
				List<String[]> pagedSrc = sourceCopy.subList(from, to);
				
				tableDataSource.clear();
				tableDataSource.addAll(pagedSrc);
			}
		});
		
		p.setCurrentPageNum(1);
		
		dbrowse.setPagination(p);
		
		return dbrowse;
	}
	
	private static List<String[]> populateSource(){
		List<String[]> retVal = new ArrayList<String[]>();
		for (int i = 0; i < 141200; i++) {
			String[] s = new String[] { "data" + (i + 1), "data" + (i + 1),
					"data" + (i + 1), "data" + (i + 1), "data" + (i + 1) };
			retVal.add(s);
		}
		return retVal;
	}

	private class BasicTableFormat implements TableFormat<String[]> {

		public int getColumnCount() {
			return 5;
		}

		public String getColumnName(int indx) {
			if (indx == 0)
				return "Zero";
			else if (indx == 1)
				return "One";
			else if (indx == 2)
				return "Two";
			else if (indx == 3)
				return "Three";
			else
				return "Four";
		}

		public Object getColumnValue(String[] src, int indx) {
			return src[indx];
		}
	}
}
