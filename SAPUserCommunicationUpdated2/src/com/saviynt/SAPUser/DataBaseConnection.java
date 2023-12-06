package com.saviynt.SAPUser;

import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;
import com.mysql.jdbc.exceptions.jdbc4.MySQLSyntaxErrorException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DataBaseConnection
{
  public Connection conn;
  
  DataBaseConnection(String url, String user, String pass, String jdbc_driver)
  {
    try
    {
      Class.forName(jdbc_driver);
      this.conn = DriverManager.getConnection(url, user, pass);
    }
    catch (ClassNotFoundException e)
    {
      e.printStackTrace();
      System.exit(-1);
    }
    catch (SQLException e)
    {
      e.printStackTrace();
    }
  }
  
  public ResultSet execute_query(String query)
    throws SQLException
  {
    Statement stmt = this.conn.createStatement(1004, 1008);
    
    ResultSet rs = null;
    try
    {
      rs = stmt.executeQuery(query);
    }
    catch (MySQLIntegrityConstraintViolationException e)
    {
      e.printStackTrace();
    }
    catch (MySQLSyntaxErrorException e)
    {
      e.printStackTrace();
    }
    return rs;
  }
  
  public int execute_update(String query)
    throws SQLException
  {
    Statement stmt = this.conn.createStatement(1004, 1008);
    
    int rs = 0;
    try
    {
      rs = stmt.executeUpdate(query);
    }
    catch (MySQLIntegrityConstraintViolationException e)
    {
      e.printStackTrace();
    }
    catch (MySQLSyntaxErrorException e)
    {
      e.printStackTrace();
    }
    return rs;
  }
  
  void close()
  {
    try
    {
      this.conn.close();
    }
    catch (SQLException e)
    {
      e.printStackTrace();
    }
  }
  
  protected void finalize()
    throws Throwable
  {
    super.finalize();
    this.conn.close();
  }
}
