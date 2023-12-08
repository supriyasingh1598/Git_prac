// 
// Decompiled by Procyon v0.5.36
// 

package com.saviynt.SAPUser;

import java.io.IOException;
import org.apache.log4j.Appender;
import org.apache.log4j.Layout;
import org.apache.log4j.RollingFileAppender;
import org.apache.log4j.SimpleLayout;
import org.apache.log4j.Logger;
import org.apache.log4j.Level;

public class Logging
{
    public void initiateLoggerSetup(final String loggerLocation, final String loggerName, final String loggerMaxFileSize, final int loggerMaxBackupIndex, final Level loggerLevel, final Logger logger) {
        try {
            final SimpleLayout layout = new SimpleLayout();
            final RollingFileAppender appender = new RollingFileAppender((Layout)layout, String.valueOf(loggerLocation) + loggerName, true);
            appender.setMaxFileSize(loggerMaxFileSize);
            appender.setMaxBackupIndex(loggerMaxBackupIndex);
            logger.addAppender((Appender)appender);
            logger.setLevel(loggerLevel);
        }
        catch (IOException e) {
            e.printStackTrace();
            logger.debug((Object)"initiateLoggerSetup IOExceptionError", (Throwable)e);
        }
    }
}
