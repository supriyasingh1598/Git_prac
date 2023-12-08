package com.Integration.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseConnector {
	static final Logger logger = Logger.getLogger(SaviyntMain.class.getName());

	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";

	public static Connection getDatabaseConnection(String db_url, String user, String pass) {
		Connection conn = null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			logger.info("Connecting to database...");
			if (db_url != null && user != null && pass != null && db_url.trim().length() > 0 && user.trim().length() > 0
					&& pass.trim().length() > 0) {
				conn = DriverManager.getConnection(db_url, user, pass);
			} else {
				System.out.println("Database server path or username or password is null");
			}
			System.out.println("connection established");
			return conn;
		} catch (SQLException se) {
			se.printStackTrace();
			logger.log(Level.SEVERE, se.getMessage(), se);
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			logger.log(Level.SEVERE, e.getMessage(), e);
			return null;
		}
	}

	public Statement getDbCreateStatement(Connection conn) {
		Statement stmt = null;
		try {
			if (conn != null) {
				stmt = conn.createStatement();
				System.out.println(stmt);
			} else {
				System.out.println("Connection is null");
			}
			return stmt;
		} catch (SQLException se) {
			se.printStackTrace();
			logger.log(Level.SEVERE, se.getMessage(), se);
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			logger.log(Level.SEVERE, e.getMessage(), e);
			return null;
		}
	}

	public ResultSet getResultSet(Statement stmt, String query) {
		ResultSet rs = null;
		try {
			if (stmt != null && query != null) {
				rs = stmt.executeQuery(query);
				return rs;
			}
			System.out.println("Connection or query is null");
			return null;
		} catch (SQLException se) {
			se.printStackTrace();
			logger.log(Level.SEVERE, se.getMessage(), se);
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			logger.log(Level.SEVERE, e.getMessage(), e);
			return null;
		}
	}

	public void updateTable(Statement stmt, String query) {
		try {
			if (stmt != null && query != null) {
				stmt.executeUpdate(query);
			} else {
				System.out.println("Connection or query is null");
			}
		} catch (SQLException se) {
			se.printStackTrace();
			logger.log(Level.SEVERE, se.getMessage(), se);
		} catch (Exception e) {
			e.printStackTrace();
			logger.log(Level.SEVERE, e.getMessage(), e);
		}
	}
}
