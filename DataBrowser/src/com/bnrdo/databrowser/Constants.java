package com.bnrdo.databrowser;

public class Constants{
	public enum SQL_TYPE {
		STRING, INTEGER, BIGDECIMAL, TIMESTAMP, BOOLEAN, NUMBER, VARCHAR2, FLOAT;
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
		public static final String FN_FILTER = "filterKey";
		public static final String FN_IS_TABLE_LOADING = "isTableLoading";
		public static final String FN_IS_DS_LOADING = "isDataSourceLoading";
	}
}