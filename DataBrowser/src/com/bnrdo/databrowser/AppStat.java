package com.bnrdo.databrowser;

public class AppStat {
	public enum DBVendor{
		ORACLE, MYSQL, HSQLDB;
	}
	public static DBVendor dbVendor = DBVendor.ORACLE; 
}