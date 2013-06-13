package com.bnrdo.databrowser;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JPanel;


public class AppStat {
	public enum DBVendor{
		ORACLE, MYSQL, HSQLDB;
	}
	public static DBVendor dbVendor = DBVendor.ORACLE; 
}
class Mani {
    /**
     * @param args the command line arguments
     */
	public void test(Object obj){
        System.out.println("Object called");
    }

    public void test(String obj){
        System.out.println("String called");
    }

    public void testtest(){
    	test(null);
        System.out.println("10%2==0 is "+(10%2==0));
        test((10%2==0)?null:new Object());
        test((10%2==0)?null:null);
    }
    
    public static void main(String[] args){
        
    }
}
