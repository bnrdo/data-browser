package com.bnrdo.databrowser;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.gui.TableFormat;

import com.bnrdo.databrowser.DataBrowser;
import com.bnrdo.databrowser.Pagination;
import com.bnrdo.databrowser.domain.Person;
import com.bnrdo.databrowser.listener.PaginationListener;
import com.bnrdo.databrowser.mvc.DataBrowserModel;

@SuppressWarnings("unchecked")
public class DataBrowserSampleUsage {
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
				} catch (Exception e){
					e.printStackTrace();
				}
				
				DataBrowserSampleUsage creator = new DataBrowserSampleUsage();

				JFrame frame = new JFrame("Demo");
				frame.getContentPane().setLayout(new BorderLayout());
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.getContentPane().add(creator.createDataBrowser(), BorderLayout.CENTER);
				frame.pack();
				frame.setVisible(true);
			}
		});
	}
	
	private DataBrowser<Person> createDataBrowser() {
		final DataBrowser<Person> dbrowse = new DataBrowser<Person>();
		final DataBrowserModel<Person> dmodel = dbrowse.getModel();
		final EventList<Person> tableDataSource =  dmodel.getDataTableSource();
		final List<Person> source = populateSource();
		
		final int rowCount = source.size();
		final int maxExposable = 10;
		final int itemsPerPage = 10;
		
		Pagination p = new Pagination();
		p.setMaxExposableCount(maxExposable);
		p.setTotalPageCount(rowCount / itemsPerPage);
		p.addPaginationListener(new PaginationListener() {
			public void pageChanged(int pageNum) {
				List<Person> sourceCopy = new ArrayList<Person>(source);
				int from = (pageNum * itemsPerPage) - itemsPerPage;
				int to = (pageNum * itemsPerPage);
				List<Person> pagedSrc = sourceCopy.subList(from, to);
				
				tableDataSource.clear();
				tableDataSource.addAll(pagedSrc);
			}
		});
		
		p.setCurrentPageNum(1);
		dmodel.setDataTableFormat(new PersonTableFormat());
		dbrowse.setPagination(p);
		
		return dbrowse;
	}
	
	private List<Person> populateSource(){
		List<Person> retVal = new ArrayList<Person>();
		for (int i = 0; i < 141200; i++) {
			Person p = new Person();
			p.setFirstName("First Name" + i);
			p.setLastName("Last Name" + i);
			p.setBirthDay(new Date());
			p.setOccupation("Occupation" + i);
			p.setAge(i);
			retVal.add(p);
		}
		return retVal;
	}

	class PersonTableFormat implements TableFormat<Person>{

		public int getColumnCount() {
			return 5;
		}

		public String getColumnName(int index) {
			String[] colNames = new String[]{"First Name", "Last Name", "Birth Day", "Age", "Occupation"};
			return colNames[index];
		}

		public Object getColumnValue(Person p, int index) {
			if(index == 0)
				return p.getFirstName();
			else if(index == 1)
				return p.getLastName();
			else if(index == 2)
				return p.getBirthDay().toString();
			else if(index == 3)
				return Integer.toString(p.getAge());
			else if(index == 4)
				return p.getOccupation();
			else
				return "This should not happen";
		}
		
	}
}
