package com.bnrdo.databrowser;

import java.sql.Connection;
import java.sql.SQLException;

import org.jooq.SQLDialect;


public interface DBDataSource {
	Connection getConnection() throws SQLException;
	Query getSelectQuery();
	SQLDialect getSQLDialect();
}
