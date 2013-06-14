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

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableModel;

import com.bnrdo.databrowser.Constants.SQL_TYPE;
import com.bnrdo.databrowser.DataBrowser;
import com.bnrdo.databrowser.domain.Person;
import com.bnrdo.databrowser.domain.Logs;
import com.bnrdo.databrowser.format.LogsFormat;

public class DataBrowserSampleUsage4 {
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable(){
			public void run() {
				try {
					//UIManager.setLookAndFeel("com.jgoodies.looks.plastic.PlasticXPLookAndFeel");
					UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
				} catch (Exception e){
					e.printStackTrace();
				}
				
				DataBrowserSampleUsage4 creator = new DataBrowserSampleUsage4();

				JFrame frame = new JFrame("Demo");
				frame.getContentPane().setLayout(new BorderLayout());
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.getContentPane().add(creator.createDataBrowser(), BorderLayout.CENTER);
				
				/*JTable tbl = new JTable();
				tbl.setVisible(false);
				DefaultTableModel mod = new DefaultTableModel(null, new Object[]{"2","2","2","2","2","2","2","2","2","2","2","2","2"});
				mod.addRow(new Object[]{"2","2","2","2","2","2","2","2","2","2","2","2","2"});
				mod.addRow(new Object[]{"2","2","2","2","2","2","2","2","2","2","2","2","2"});
				tbl.setModel(mod);
				tbl.setVisible(true);
				frame.getContentPane().add(tbl, BorderLayout.CENTER);*/
				
				frame.pack();
				frame.setVisible(true);
			}
		});
	}
	
	private DataBrowser<Logs> createDataBrowser() {
		final DataBrowser<Logs> dbrowse = new DataBrowser<Logs>();
		//final List<Person> source =  generateRandomSource();
		final ColumnInfoMap colInfoMap = new ColumnInfoMap();
		
		colInfoMap.putColumnName(0, "ID");
		colInfoMap.putColumnName(1, "Date");
		colInfoMap.putColumnName(2, "Entity");
		colInfoMap.putColumnName(3, "Environment");
		colInfoMap.putColumnName(4, "Message");
		colInfoMap.putColumnName(5, "Number");
		colInfoMap.putColumnName(6, "Probe ID");
		colInfoMap.putColumnName(7, "Process ID");
		colInfoMap.putColumnName(8, "Server IP");
		colInfoMap.putColumnName(9, "Timestamp");
		colInfoMap.putColumnName(10, "Transaction ID");
		colInfoMap.putColumnName(11, "Unknown");
		colInfoMap.putColumnName(12, "User ID");
		
		colInfoMap.putPropertyName(0, "ID");
		colInfoMap.putPropertyName(1, "DATE_VAL");
		colInfoMap.putPropertyName(2, "ENTITY");
		colInfoMap.putPropertyName(3, "ENVIRONMENT");
		colInfoMap.putPropertyName(4, "MESSAGE_VAL");
		colInfoMap.putPropertyName(5, "NUMBER_VAL");
		colInfoMap.putPropertyName(6, "PROBE_ID");
		colInfoMap.putPropertyName(7, "PROCESS_ID");
		colInfoMap.putPropertyName(8, "SERVER_IP");
		colInfoMap.putPropertyName(9, "TIMESTAMP");
		colInfoMap.putPropertyName(10, "TRANSACTION_ID");
		colInfoMap.putPropertyName(11, "UNKNOWN");
		colInfoMap.putPropertyName(12, "USER_ID");
		
		colInfoMap.putPropertyType(0, SQL_TYPE.NUMBER);
		colInfoMap.putPropertyType(1, SQL_TYPE.TIMESTAMP);
		colInfoMap.putPropertyType(2, SQL_TYPE.VARCHAR2);
		colInfoMap.putPropertyType(3, SQL_TYPE.VARCHAR2);
		colInfoMap.putPropertyType(4, SQL_TYPE.VARCHAR2);
		colInfoMap.putPropertyType(5, SQL_TYPE.NUMBER);
		colInfoMap.putPropertyType(6, SQL_TYPE.VARCHAR2 );
		colInfoMap.putPropertyType(7, SQL_TYPE.NUMBER);
		colInfoMap.putPropertyType(8, SQL_TYPE.VARCHAR2);
		colInfoMap.putPropertyType(9, SQL_TYPE.NUMBER);
		colInfoMap.putPropertyType(10, SQL_TYPE.VARCHAR2);
		colInfoMap.putPropertyType(11, SQL_TYPE.VARCHAR2);
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
		dbrowse.setDataTableSource(con, "THOT_LOGS");
		dbrowse.create();
		
		return dbrowse;
	}
}


