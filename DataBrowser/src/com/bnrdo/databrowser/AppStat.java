package com.bnrdo.databrowser;

public class AppStat {
	public enum DBVendor{
		ORACLE, MYSQL, HSQLDB_EMBEDDED;
	}
	public static DBVendor dbVendor = DBVendor.ORACLE; 
	public static boolean isUsingListDS(){
		return dbVendor.equals(DBVendor.HSQLDB_EMBEDDED);
	}
}