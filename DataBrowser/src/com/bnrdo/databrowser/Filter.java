package com.bnrdo.databrowser;

public class Filter {
	
	private String key;
	private String col;
	private String colAsInUI;
	
	public Filter(String keyArg, String colArg, String colAsInUIArg){
		key = keyArg;
		col = colArg;
		colAsInUI = colAsInUIArg;
	}
	
	public Filter(){}
	
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getCol() {
		return col;
	}
	public void setCol(String col) {
		this.col = col;
	}
	public String getColAsInUI() {
		return colAsInUI;
	}
	public void setColAsInUI(String colAsInUI) {
		this.colAsInUI = colAsInUI;
	}
}
