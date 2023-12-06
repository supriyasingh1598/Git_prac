package com.saviynt.SAPUser;

import java.io.IOException;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.RollingFileAppender;
import org.apache.log4j.SimpleLayout;

public class Logging
{
  public void initiateLoggerSetup(String loggerLocation, String loggerName, String loggerMaxFileSize, int loggerMaxBackupIndex, Level loggerLevel, Logger logger)
  {
    try
    {
      SimpleLayout layout = new SimpleLayout();
      RollingFileAppender appender = new RollingFileAppender(layout, loggerLocation + loggerName, true);
      appender.setMaxFileSize(loggerMaxFileSize);
      appender.setMaxBackupIndex(loggerMaxBackupIndex);
      logger.addAppender(appender);
      logger.setLevel(loggerLevel);
    }
    catch (IOException e)
    {
      e.printStackTrace();
      logger.debug("initiateLoggerSetup IOExceptionError", e);
    }
  }
}