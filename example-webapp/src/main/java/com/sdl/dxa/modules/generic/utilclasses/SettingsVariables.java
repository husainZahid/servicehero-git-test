package com.sdl.dxa.modules.generic.utilclasses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

@Scope("session")
public class SettingsVariables {
    private static Logger logger = LoggerFactory.getLogger(SettingsVariables.class);

    public TreeMap<String, String> settings = new TreeMap<String, String>();
	public static TreeMap<String, String> environmentVariables = new TreeMap<String, String>();

	public TreeMap<String, String> getSettings() {
		return settings;
	}

	public void setSettings(TreeMap<String, String> settings) {
		this.settings = settings;
	}

	public static TreeMap<String, String> getEnvironmentVariables() {
		return environmentVariables;
	}

	public static void setEnvironmentVariables(TreeMap<String, String> environmentVariables) {
		SettingsVariables.environmentVariables = environmentVariables;
	}
	
	public String getValue(String fieldName)
    {
        return settings.get(fieldName);
    }
	
	public String setValue(String fieldName, String fieldValue)
    {
        return settings.put(fieldName.toLowerCase(), fieldValue);
    }


	public SettingsVariables()
    {
		setDefaltValues();
    }
    
    public void setDefaltValues()
    {
        List<String> variableNames;
        variableNames = new ArrayList<String>();

        variableNames.add("isTomcatServer");
        variableNames.add("dbDriver");
        variableNames.add("dbConnectString");
        variableNames.add("dbUsername");
        variableNames.add("dbPassword");
        variableNames.add("tridionPublicationIds");
        variableNames.add("tridionSiteLabelId");
        variableNames.add("encryptionMasterKey");


        /*variableNames.add("persistentPropsValues");
        variableNames.add("dbHostname");
        variableNames.add("dbPort");
        variableNames.add("dbSID");
        variableNames.add("dbMinConn");
        variableNames.add("dbMaxConn");
        

        variableNames.add("webAppName");
        variableNames.add("webAppFolderName");
        variableNames.add("websiteAddress");
        variableNames.add("webAddress");
        variableNames.add("secureWebAddress");

        variableNames.add("brokerDBConnectString");
        variableNames.add("brokerDBUsername");
        variableNames.add("brokerDBPassword");
        variableNames.add("dbMailConnectString");
        variableNames.add("dbMailUsername");
        variableNames.add("dbMailPassword");*/
        try
        {
            Context env = (Context) new InitialContext().lookup("java:comp/env");
            for (String variableName : variableNames) {
                try {
                    if(env.lookup(variableName) != null)
                    	environmentVariables.put(variableName, env.lookup(variableName).toString());
                } catch(Exception e) {
                    logger.error("Cannot find value for environment variable " + variableName + " in web.xml", e);
                }
            }
            settings.clear();
            
            DatabaseConnection dbConnection = new DatabaseConnection();
            ResultSet objRs = dbConnection.executeSQLQuery("select * from settings order by upper(varName)", false);
            while (objRs.next())
            {
            	if(environmentVariables.get("isTomcatServer") != null && objRs.getString("varName").equals("webAppName")) {
                	if(environmentVariables.get("isTomcatServer").equals("Yes"))
                		settings.put(objRs.getString("varName"), objRs.getString(4));
                	else
                		settings.put(objRs.getString("varName").toLowerCase(), "");
                }
                else
                	settings.put(objRs.getString("varName"), objRs.getString(4));
            }
            logger.info("SettingsVariables initiated: settings.size(): " + settings.size());

            objRs.close();
            dbConnection.getConnection().close();
        }
        catch (Exception e)
        {
            logger.error(e.getMessage(),e);
        }
    }
}
