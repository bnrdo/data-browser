package com.bnrdo.databrowser;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
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
import com.bnrdo.databrowser.domain.Logs;
import com.bnrdo.databrowser.format.LogsFormat;

public class DataBrowserSampleUsage3 {
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					//UIManager.setLookAndFeel("com.jgoodies.looks.plastic.PlasticXPLookAndFeel");
					UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
				} catch (Exception e){
					e.printStackTrace();
				}
				
				DataBrowserSampleUsage3 creator = new DataBrowserSampleUsage3();

				JFrame frame = new JFrame("Demo");
				frame.getContentPane().setLayout(new BorderLayout());
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.getContentPane().add(creator.createDataBrowser(), BorderLayout.CENTER);
				frame.pack();
				frame.setVisible(true);
			}
		});
	}
	
	private DataBrowser<Logs> createDataBrowser() {
		final DataBrowser<Logs> dbrowse = new DataBrowser<Logs>();
		//final List<Person> source =  generateRandomSource();
		final ColumnInfoMap colInfoMap = new ColumnInfoMap();
		
		colInfoMap.putColumnName(0, "Event ID");
		colInfoMap.putColumnName(1, "Probe ID");
		colInfoMap.putColumnName(2, "Entity");
		colInfoMap.putColumnName(3, "Environment");
		colInfoMap.putColumnName(4, "User ID");
		colInfoMap.putColumnName(5, "Transaction ID");
		colInfoMap.putColumnName(6, "Message");
		colInfoMap.putColumnName(7, "Number Value");
		colInfoMap.putColumnName(8, "Date Value");
		colInfoMap.putColumnName(9, "Send Time");
		colInfoMap.putColumnName(10, "Absolute Time");
		colInfoMap.putColumnName(11, "Thread ID");
		colInfoMap.putColumnName(12, "IP Address");
		
		colInfoMap.putPropertyName(0, "EVENT_ID");
		colInfoMap.putPropertyName(1, "PROBE_ID");
		colInfoMap.putPropertyName(2, "ENTITY");
		colInfoMap.putPropertyName(3, "ENVIRONMENT");
		colInfoMap.putPropertyName(4, "USERID");
		colInfoMap.putPropertyName(5, "TRANSACTION_ID");
		colInfoMap.putPropertyName(6, "MESSAGE");
		colInfoMap.putPropertyName(7, "NUMBER_VALUE");
		colInfoMap.putPropertyName(8, "DATE_VALUE");
		colInfoMap.putPropertyName(9, "SEND_TIME");
		colInfoMap.putPropertyName(10, "ABSOLUTE_TIME");
		colInfoMap.putPropertyName(11, "THREAD_ID");
		colInfoMap.putPropertyName(12, "IP_ADDRESS");
		
		colInfoMap.putPropertyType(0, SQL_TYPE.NUMBER);
		colInfoMap.putPropertyType(1, SQL_TYPE.VARCHAR2);
		colInfoMap.putPropertyType(2, SQL_TYPE.VARCHAR2);
		colInfoMap.putPropertyType(3, SQL_TYPE.VARCHAR2);
		colInfoMap.putPropertyType(4, SQL_TYPE.VARCHAR2);
		colInfoMap.putPropertyType(5, SQL_TYPE.VARCHAR2);
		colInfoMap.putPropertyType(6, SQL_TYPE.VARCHAR2 );
		colInfoMap.putPropertyType(7, SQL_TYPE.FLOAT);
		colInfoMap.putPropertyType(8, SQL_TYPE.TIMESTAMP);
		colInfoMap.putPropertyType(9, SQL_TYPE.TIMESTAMP);
		colInfoMap.putPropertyType(10, SQL_TYPE.TIMESTAMP);
		colInfoMap.putPropertyType(11, SQL_TYPE.NUMBER);
		colInfoMap.putPropertyType(12, SQL_TYPE.VARCHAR2);
		
		
		Connection con = null;
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			con = DriverManager.getConnection("jdbc:oracle:thin:@LXISAP0230.ISAP.ASIA.CIB:1521:FCLDEV1", "UT05MR", "UT05MR");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}  catch(SQLException e){
			e.printStackTrace();
		}
		
		dbrowse.setColInfoMap(colInfoMap);
		dbrowse.setTableDataSourceFormat(new LogsFormat());
		dbrowse.setDataTableSource(con, "LOGS");
		dbrowse.create();
		
		return dbrowse;
	}
}


