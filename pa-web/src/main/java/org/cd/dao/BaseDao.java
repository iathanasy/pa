package org.cd.dao;

import java.sql.Connection;
import java.sql.SQLException;

public interface BaseDao<T> {

	Long insert(T t);
	Long insert(Connection conn, T t);
	
	boolean check(Object id);
	boolean check(Connection conn, Object id);

	boolean executeQuery(String sql) throws SQLException;
	boolean executeQuery(Connection conn, String sql) throws SQLException;
}
