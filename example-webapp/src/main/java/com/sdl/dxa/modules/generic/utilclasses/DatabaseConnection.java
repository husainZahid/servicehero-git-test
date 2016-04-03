package com.sdl.dxa.modules.generic.utilclasses;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Vector;

public class DatabaseConnection {
    private static Logger logger = LoggerFactory.getLogger(SettingsVariables.class);

    private Connection connection = null;
    private PreparedStatement objPs = null;
	private int iMaxAttempts = 5;

	public Connection getConnection() {
		return connection;
	}
	public void setConnection(Connection connection) {
		this.connection = connection;
	}

	public int getiMaxAttempts() {
		return iMaxAttempts;
	}
	public void setiMaxAttempts(int iMaxAttempts) {
		this.iMaxAttempts = iMaxAttempts;
	}
	
	public DatabaseConnection()
    {
        /* commented by Sudha because we will set this from MyHttpServlet
		/*this((String)SettingsVariables.environmentVariables.get("dbUsername"),
        		(String)SettingsVariables.environmentVariables.get("dbPassword"), 
        		(String)SettingsVariables.environmentVariables.get("dbConnectString"));
        		*/
    }
	
	public DatabaseConnection(String strUName, String strPass, String dbConnectString)
    {
        try
        {
	        boolean isNotConnected = true;
            int iAttempts = 0;
            do
            {
                try
                {
                    iAttempts++;
                    Class.forName((String)SettingsVariables.environmentVariables.get("dbDriver"));
                    connection = DriverManager.getConnection(dbConnectString, strUName, strPass);
                    connection.setAutoCommit(false);
					isNotConnected = false;
                }
                catch (Exception e)
                {
                	logger.error(e.getMessage(),e);
                    isNotConnected = true;
                }
            }
            while (isNotConnected && iAttempts < iMaxAttempts);
        }
        catch (Exception e)
        {
            logger.error(e.getMessage(),e);
        }
    }
	
    /**
     * The method executes the select sql query and returns the object of ResultSet   
     */
    public ResultSet executeSQLQuery(String sqlQuery, boolean isScrollInsensitive)
    {
    	try
        {
        	if(isScrollInsensitive)
        		objPs = connection.prepareStatement(sqlQuery, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            else 
            	objPs = connection.prepareStatement(sqlQuery);
        	return objPs.executeQuery();
        }
        catch (Exception e)
        {
            logger.error(e.getMessage(),e);
            return null;
        }
    }

    public static ResultSet executeSQLQuery(Connection connection, PreparedStatement objPs, String sqlQuery, Vector queryParams, boolean isScrollInsensitive)
    {
        try
        {
            if(isScrollInsensitive)
        		objPs = connection.prepareStatement(sqlQuery, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            else 
            	objPs = connection.prepareStatement(sqlQuery);
            if(queryParams != null) {
	            for(int i = 1; i<queryParams.size(); i++) {
	            	objPs.setString(i, (String)queryParams.get(i-1));
	            }
            }            
        	return objPs.executeQuery();
        }
        catch (Exception e)
        {
            logger.error(e.getMessage(),e);
            return null;
        } finally {
        	
        }
    }

    /**
     * The method executes the update sql query and returns number of updated rows  
     */
    public int updateSQLQuery(String sqlQuery)
    {
        try
        {
          	objPs = connection.prepareStatement(sqlQuery);
        	int iCount = objPs.executeUpdate();
        	return iCount;
        }
        catch (Exception e)
        {
            logger.error(e.getMessage(),e);
        	return -1;
        }
    }

    public static int updateSQLQuery(Connection connection, String sqlQuery)
    {
        try
        {
            PreparedStatement objPs;
          	objPs = connection.prepareStatement(sqlQuery);
        	int iCount = objPs.executeUpdate();
        	objPs.close();
        	return iCount;
        }
        catch (Exception e)
        {
            logger.error(e.getMessage(),e);
        	return -1;
        }
    }
    
    
    /**
     * The method uncommit the updated data due to runtime error
     */
	public void rollback()
    {
        try
        {
            connection.rollback();
        }
        catch (Exception e)
        {
            logger.error(e.getMessage(),e);
        }
    }

    /**
     * The method sets AutoCommit property of connection to specified parameter
     *
     * @param bValue true/false
     */
    public void setAutoCommit(boolean bValue)
    {
        try
        {
            connection.setAutoCommit(bValue);
        }
        catch (Exception e)
        {
            logger.error(e.getMessage(),e);
        }
    }

    /**
     * The method return true is connection active
     */
    public boolean isClosed()
    {
        try
        {
            return connection.isClosed();
        }
        catch (Exception e)
        {
            return true;
        }
    }

    /**
     * The method closes the respective opened Connection
     */
    public void close()
    {
        try
        {
            if(objPs != null)
                objPs.close();
        	connection.close();
        }
        catch (Exception e)
        {
            logger.error(e.getMessage(),e);
        }
    }


}
