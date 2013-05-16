package com.bnrdo.databrowser;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.bnrdo.databrowser.DataBrowser;
import com.bnrdo.databrowser.domain.Person;

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
		final List<Person> source =  populateSource();
		final Map<Integer, String> colNameIndexMap = new HashMap<Integer, String>();
		
		colNameIndexMap.put(0, "First Name");
		colNameIndexMap.put(1, "Last Name");
		colNameIndexMap.put(2, "Birthday");
		colNameIndexMap.put(3, "Age");
		colNameIndexMap.put(4, "Occupation");
		
		dbrowse.setColNameIndexMap(colNameIndexMap);
		dbrowse.setDataTableSource(source);
		dbrowse.create();
		//nahinto ako sa multimap, colinfomap un index-colname-propnamefrompojo
		return dbrowse;
	}
	
	private List<Person> populateSource(){
		List<Person> retVal = new ArrayList<Person>();
		for (int i = 0; i < 33; i++) {
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
}
