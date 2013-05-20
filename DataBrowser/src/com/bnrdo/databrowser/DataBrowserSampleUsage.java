package com.bnrdo.databrowser;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.bnrdo.databrowser.DataBrowser;
import com.bnrdo.databrowser.domain.Person;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

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
		final Multimap<Integer, Object> colNameIndexMap = ArrayListMultimap.create();
		
		colNameIndexMap.put(0, "First Name");
		colNameIndexMap.put(0, "firstName");
		colNameIndexMap.put(1, "Last Name");
		colNameIndexMap.put(1, "lastName");
		colNameIndexMap.put(2, "Birthday");
		colNameIndexMap.put(2, "birthDay");
		colNameIndexMap.put(3, "Age");
		colNameIndexMap.put(3, "age");
		colNameIndexMap.put(4, "Occupation");
		colNameIndexMap.put(4, "occupation");
		
		dbrowse.setColInfoMap(colNameIndexMap);
		dbrowse.setTableDataSourceFormat(new PersonFormat());
		dbrowse.setDataTableSource(source);
		dbrowse.create();
		
		dbrowse.getTestButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Random r = new Random();
				int num = r.nextInt(100);
				dbrowse.setDataTableSource(populateSourceFromRandomNum(num+10), null);
			}
		});
		
		return dbrowse;
	}
	public List<Person> populateSourceFromRandomNum(int num){
		List<Person> retVal = new ArrayList<Person>();
		for (int i = 0; i < num; i++) {
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
	public List<Person> populateSource(){
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
