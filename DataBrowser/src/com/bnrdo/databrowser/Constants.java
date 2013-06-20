package com.bnrdo.databrowser;

public class Constants{
	public enum JAVA_TYPE{
		STRING, INTEGER, DATE, DATETIME, BIGDECIMAL, BOOLEAN, FLOAT, DOUBLE, LONG, OBJECT;
	}
	public enum SORT_ORDER{
		ASC, DESC;
	}
	public static class ModelFields{
		public static final String FN_PAGED_TABLE_MODEL = "pagedTableModel";
		public static final String FN_DATA_TABLE_SOURCE = "dataTableSource";
		public static final String FN_DATA_TABLE_SOURCE_ROW_COUNT = "dataSourceRowCount";
		public static final String FN_DATA_TABLE_SOURCE_FORMAT = "tableDataSourceFormat";
		public static final String FN_COL_INFO_MAP = "colInfoMap";
		public static final String FN_PAGINATION = "pagination";
		public static final String FN_SORT_ORDER = "sortOrder";
		public static final String FN_DS_TYPE = "dsType";
		public static final String FN_FILTER = "filterKey";
		public static final String FN_IS_TABLE_LOADING = "isTableLoading";
		public static final String FN_IS_DS_LOADING = "isDataSourceLoading";
		public static final String FN_IS_STATUS_SHOWABLE = "isStatusShowable";
		public static final String FN_IS_SORT_DETAILS_SHOWABLE = "isSortDetailsShowable";
		public static final String FN_FILTER_DETAILS_SHOWABLE = "isFilterDetailsShowable";
		public static final String FN_IS_ROWCOUNT_SHOWABLE = "isRowCountDetailsShowable";
	}
}