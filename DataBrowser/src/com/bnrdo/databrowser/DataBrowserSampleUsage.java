package com.bnrdo.databrowser;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.bnrdo.databrowser.Constants.SQL_TYPE;
import com.bnrdo.databrowser.DataBrowser;
import com.bnrdo.databrowser.domain.Person;
import com.bnrdo.databrowser.format.PersonFormat;

public class DataBrowserSampleUsage {
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					//UIManager.setLookAndFeel("com.jgoodies.looks.plastic.PlasticXPLookAndFeel");
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
		final List<Person> source =  generateRandomSource();
		final ColumnInfoMap colInfoMap = new ColumnInfoMap();
		
		colInfoMap.putColumnName(0, "First Name");
		colInfoMap.putColumnName(1, "Last Name");
		colInfoMap.putColumnName(2, "Birthday");
		colInfoMap.putColumnName(3, "Age");
		colInfoMap.putColumnName(4, "Occupation");
		
		colInfoMap.putPropertyName(0, "firstName");
		colInfoMap.putPropertyName(1, "lastName");
		colInfoMap.putPropertyName(2, "birthDay");
		colInfoMap.putPropertyName(3, "age");
		colInfoMap.putPropertyName(4, "occupation");
		
		colInfoMap.putPropertyType(0, SQL_TYPE.STRING);
		colInfoMap.putPropertyType(1, SQL_TYPE.STRING);
		colInfoMap.putPropertyType(2, SQL_TYPE.TIMESTAMP);
		colInfoMap.putPropertyType(3, SQL_TYPE.INTEGER);
		colInfoMap.putPropertyType(4, SQL_TYPE.STRING);
		
		dbrowse.setColInfoMap(colInfoMap);
		dbrowse.setTableDataSourceFormat(new PersonFormat());
		dbrowse.setDataTableSource(source);
		dbrowse.create();
		
		/*dbrowse.getTestButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dbrowse.setDataTableSource(generateRandomSource());
				dbrowse.create();
			}
		});*/
		
		return dbrowse;
	}
	public List<Person> generateRandomSource(){
		Random r = new Random();
		int num = 10000;//r.nextInt(1000);
		System.out.println("Generating " + num + " rows...");
		List<Person> retVal = new ArrayList<Person>();
		for (int i = 1; i <= num; i++) {
			Person p = new Person();
			p.setFirstName("First Name" + i);
			p.setLastName("Last Name" + i);
			
			//date should be sumthin like this
			String target = "2013-6-05 10:59:11.00129";
			java.text.DateFormat df = new java.text.SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSSSSSSSS", java.util.Locale.ENGLISH);
			Date result = null;
		    try {
				result =  df.parse(target);
			} catch (ParseException e) {
				e.printStackTrace();
			}  
			p.setBirthDay(new Date());
			
			p.setOccupation("Occupation" + i);
			p.setAge(i);
			retVal.add(p);
		}
		return retVal;
	}
}


