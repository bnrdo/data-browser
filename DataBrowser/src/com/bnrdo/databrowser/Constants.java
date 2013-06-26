package com.bnrdo.databrowser;

public class Constants{
	public enum JavaType{
		STRING, INTEGER, DATE, DATETIME, BIGDECIMAL, BOOLEAN, FLOAT, DOUBLE, LONG, OBJECT;
	}
	public enum SortOrder{
		ASC, DESC;
	}
	public enum ModelField{
		 pagedTableModel, 
		 dataTableSource, 
		 dataSourceRowCount, 
		 tableDataSourceFormat, 
		 colInfoMap, 
		 pagination, 
		 sortOrder, 
		 dsType, 
		 filterKey, 
		 isDataForTableLoading, 
		 isDataSourceLoading, 
		 isStatusShowable, 
		 isSortDetailsShowable, 
		 isFilterDetailsShowable, 
		 isRowCountDetailsShowable, 
		 maxExposableCount, 
		 totalPageCount;
	}
}