// 
// Decompiled by Procyon v0.5.36
// 

package com.saviynt.SAPUser;

import java.sql.Statement;
import com.mysql.jdbc.exceptions.jdbc4.MySQLSyntaxErrorException;
import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.DriverManager;
import java.sql.Connection;

public class DataBaseConnection
{
    public Connection conn;
    
    DataBaseConnection(final String url, final String user, final String pass, final String jdbc_driver) {
        try {
            Class.forName(jdbc_driver);
            this.conn = DriverManager.getConnection(url, user, pass);
        }
        catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.exit(-1);
        }
        catch (SQLException e2) {
            e2.printStackTrace();
        }
    }
    
    public ResultSet execute_query(final String query) throws SQLException {
        final Statement stmt = this.conn.createStatement(1004, 1008);
        ResultSet rs = null;
        try {
            rs = stmt.executeQuery(query);
        }
        catch (MySQLIntegrityConstraintViolationException e) {
            e.printStackTrace();
        }
        catch (MySQLSyntaxErrorException e2) {
            e2.printStackTrace();
        }
        return rs;
    }
    
    public int execute_update(final String query) throws SQLException {
        final Statement stmt = this.conn.createStatement(1004, 1008);
        int rs = 0;
        try {
            rs = stmt.executeUpdate(query);
        }
        catch (MySQLIntegrityConstraintViolationException e) {
            e.printStackTrace();
        }
        catch (MySQLSyntaxErrorException e2) {
            e2.printStackTrace();
        }
        return rs;
    }
    
    void close() {
        try {
            this.conn.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        this.conn.close();
    }
}
