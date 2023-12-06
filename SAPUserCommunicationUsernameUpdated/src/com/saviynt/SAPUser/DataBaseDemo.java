package com.saviynt.SAPUser;

import java.io.InputStream;

public class DataBaseDemo
{
  InputStream inputStream;
  
  public DataBaseConnection getDatabaseConnection(String db_url, String user, String password, String jdbc_driver)
  {
    DataBaseConnection db = new DataBaseConnection(db_url, user, password, jdbc_driver);
    return db;
  }
}
