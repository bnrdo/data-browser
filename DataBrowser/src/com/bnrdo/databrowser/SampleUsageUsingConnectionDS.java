package com.bnrdo.databrowser;

import java.awt.BorderLayout;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.jooq.Field;
import org.jooq.SQLDialect;
import org.jooq.Select;
import org.jooq.SelectJoinStep;
import org.jooq.impl.Factory;

import com.bnrdo.databrowser.DataBrowser;
import com.bnrdo.databrowser.domain.Logs;

public class SampleUsageUsingConnectionDS {
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable(){
			public void run() {
				try {
					//UIManager.setLookAndFeel("com.jgoodies.looks.plastic.PlasticXPLookAndFeel");
					UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
				} catch (Exception e){
					e.printStackTrace();
				}
				
				SampleUsageUsingConnectionDS creator = new SampleUsageUsingConnectionDS();

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
		final ColumnInfoMap colInfoMap = new ColumnInfoMap();
		
		colInfoMap.putInfo(0, "ID", "ID");
		colInfoMap.putInfo(1, "Date", "DATE_VAL");
		colInfoMap.putInfo(2, "Entity", "ENTITY");
		colInfoMap.putInfo(3, "Environment", "ENVIRONMENT");
		colInfoMap.putInfo(4, "Message", "MESSAGE_VAL");
		colInfoMap.putInfo(5, "Number", "NUMBER_VAL");
		colInfoMap.putInfo(6, "Probe ID", "PROBE_ID");
		colInfoMap.putInfo(7, "Process ID", "PROCESS_ID");
		colInfoMap.putInfo(8, "Server IP", "SERVER_IP");
		colInfoMap.putInfo(9, "Timestamp", "TIMESTAMP");
		colInfoMap.putInfo(10, "Transaction ID", "TRANSACTION_ID");
		colInfoMap.putInfo(11, "Unknown", "UNKNOWN");
		colInfoMap.putInfo(12, "User ID", "USER_ID");
		
		dbrowse.setDataSource(new DBDataSource() {
			@Override public Query getSelectQuery() {
				Query qry = new Query(getConnection(), SQLDialect.ORACLE);
				
				qry.addSelect("ID");
				qry.addSelect("DATE_VAL");
				qry.addSelect("ENTITY");
				qry.addSelect("ENVIRONMENT");
				qry.addSelect("MESSAGE_VAL");
				qry.addSelect("NUMBER_VAL");
				qry.addSelect("PROBE_ID");
				qry.addSelect("PROCESS_ID");
				qry.addSelect("SERVER_IP");
				qry.addSelect("TIMESTAMP");
				qry.addSelect("TRANSACTION_ID");
				qry.addSelect("UNKNOWN");
				qry.addSelect("USER_ID");
				qry.addFrom("THOT_LOGS");
				
				return qry;
			}
			@Override public Connection getConnection() {
				Connection retVal = null;
				try {
					Class.forName("oracle.jdbc.driver.OracleDriver");
					retVal = DriverManager.getConnection("jdbc:oracle:thin:@LXISAP0230.ISAP.ASIA.CIB:1521:FCLDEV1", "UT05MR", "UT05MR");
				} catch (SQLException e) {
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
				
				return retVal;
			}
			@Override public SQLDialect getSQLDialect() {
				return SQLDialect.ORACLE;
			}
			private Field<Object> fn(String tblName, String colName){
				return Factory.fieldByName(tblName, colName);
			}
			
		});
		
		
		dbrowse.setColInfoMap(colInfoMap);
		dbrowse.setMaxPageCountExposable(7);
		dbrowse.setRowCountPerPage(23);
		dbrowse.create();
		
		return dbrowse;
	}
}


