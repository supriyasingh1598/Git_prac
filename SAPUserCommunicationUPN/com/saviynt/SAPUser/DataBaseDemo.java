// 
// Decompiled by Procyon v0.5.36
// 

package com.saviynt.SAPUser;

import java.io.InputStream;

public class DataBaseDemo
{
    InputStream inputStream;
    
    public DataBaseConnection getDatabaseConnection(final String db_url, final String user, final String password, final String jdbc_driver) {
        final DataBaseConnection db = new DataBaseConnection(db_url, user, password, jdbc_driver);
        return db;
    }
}
