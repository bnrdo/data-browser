package com.bnrdo.databrowser;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.bnrdo.databrowser.Constants.JAVA_TYPE;
import com.bnrdo.databrowser.DataBrowser;
import com.bnrdo.databrowser.domain.Person;
import com.bnrdo.databrowser.format.ListSourceFormat;

public class SampleUsageUsingListDS {
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					//UIManager.setLookAndFeel("com.jgoodies.looks.plastic.PlasticXPLookAndFeel");
					UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
				} catch (Exception e){
					e.printStackTrace();
				}
				
				SampleUsageUsingListDS creator = new SampleUsageUsingListDS();

				JFrame frame = new JFrame("Demo");
				frame.getContentPane().setLayout(new BorderLayout());
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.getContentPane().add(creator.createDataBrowser(), BorderLayout.CENTER);
				frame.pack();
				frame.setVisible(true);
			}
		});
	}
	
	private DataBrowser<Person> createDataBrowser(){
		final DataBrowser<Person> dbrowse = new DataBrowser<Person>();
		final List<Person> source =  generateRandomSource();
		final ColumnInfoMap colInfoMap = new ColumnInfoMap();
		
		colInfoMap.putInfo(0, "First Name", "firstName", 	JAVA_TYPE.STRING);
		colInfoMap.putInfo(1, "Last Name", 	"lastName", 	JAVA_TYPE.STRING);
		colInfoMap.putInfo(2, "Birthday", 	"birthDay", 	JAVA_TYPE.DATETIME);
		colInfoMap.putInfo(3, "Age", 		"age", 			JAVA_TYPE.INTEGER);
		colInfoMap.putInfo(4, "Occupation", "occupation", 	JAVA_TYPE.STRING);
		
		ListSourceFormat<Person> format = new ListSourceFormat<Person>() {
			@Override public int getColumnCount() {
				return 5;
			}
			@Override public String getValueAt(int index, Person p) {
				if(index == 0){
					return p.getFirstName();
				}else if(index == 1){
					return p.getLastName();
				}else if(index == 2){
					SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.ENGLISH);
					return formatter.format(p.getBirthDay());
				}else if(index == 3){
					return Integer.toString(p.getAge());
				}else if(index == 4){
					return p.getOccupation();
				}
				
				throw new IllegalStateException();
			}
		};
		
		dbrowse.setColInfoMap(colInfoMap);
		dbrowse.setTableDataSourceFormat(format);
		dbrowse.setDataSource(source);
		dbrowse.create();
		
		/*dbrowse.getTestButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dbrowse.setDataSource(generateRandomSource());
				dbrowse.create();
			}
		});*/
		
		return dbrowse;
	}
	public List<Person> generateRandomSource(){
		DummyMgr dbsu = new DummyMgr();
		
		Random r = new Random();
		int num = 121;//r.nextInt(1000);
		System.out.println("Generating " + num + " rows...");
		List<Person> retVal = new ArrayList<Person>();
		for (int i = 1; i <= num; i++) {
			Person p = new Person();
			p.setFirstName(dbsu.getRandomFirstName());
			p.setLastName(dbsu.getRandomLastName());
			
			//date should be sumthin like this
			String target = "2013-6-05 10:59:11";
			java.text.DateFormat df = new java.text.SimpleDateFormat("yyyy-MM-dd hh:mm:ss", java.util.Locale.ENGLISH);
			Date result = null;
		    try {
				result =  df.parse(target);
			} catch (ParseException e) {
				e.printStackTrace();
			}  
			p.setBirthDay(result);
			
			p.setOccupation(dbsu.getRandomOccupation());
			p.setAge(dbsu.getRandomAge());
			retVal.add(p);
		}
		return retVal;
	}
}


