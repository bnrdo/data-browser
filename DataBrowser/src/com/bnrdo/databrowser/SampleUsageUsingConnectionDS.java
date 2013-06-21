package com.bnrdo.databrowser;

import java.awt.BorderLayout;
import java.sql.Connection;
import java.sql.DriverManager;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.apache.log4j.Logger;
import org.jooq.SQLDialect;

import com.bnrdo.databrowser.DataBrowser;
import com.bnrdo.databrowser.domain.Logs;

public class SampleUsageUsingConnectionDS {
	
	private static org.apache.log4j.Logger log = Logger.getLogger(SampleUsageUsingConnectionDS.class);
	
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable(){
			public void run() {
				try {
					//UIManager.setLookAndFeel("com.jgoodies.looks.plastic.PlasticXPLookAndFeel");
					UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
				} catch (Exception e){
					log.error("An error occured while setting the application's Look and Feel", e);
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
				} catch (Exception e) {
					log.error("An error occured while getting database connection", e);
				}
				
				return retVal;
			}
			@Override public SQLDialect getSQLDialect() {
				return SQLDialect.ORACLE;
			}
		});
		
		
		dbrowse.setColInfoMap(colInfoMap);
		dbrowse.setMaxPageCountExposable(2);
		dbrowse.setRowCountPerPage(10);
		dbrowse.katsu();
		
		return dbrowse;
	}
}


