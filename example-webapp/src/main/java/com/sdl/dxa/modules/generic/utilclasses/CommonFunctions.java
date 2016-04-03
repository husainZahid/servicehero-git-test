package com.sdl.dxa.modules.generic.utilclasses;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

/**
 * Created by DELL on 10/25/2015.
 */
public  class CommonFunctions {
	private static final Logger LOG = LoggerFactory.getLogger(CommonFunctions.class);

	/*
	Function to return the sequence number
	*/

	public static long getNextSequence(String sequenceName, Connection connection) throws Exception {
		long id = 0;
		try {
			String sequenceSqlStatement = "Select "+sequenceName+".nextVal from dual";
			PreparedStatement ps = connection.prepareStatement(sequenceSqlStatement);
			ResultSet objRs = ps.executeQuery();
			if (objRs.next())
				id = objRs.getLong(1);
			objRs.close();
			ps.close();
		} catch (SQLException exception){
			//TODO: later handle exception handling
			LOG.error("Exception occured : ");
			LOG.error(exception.getMessage(),exception);
			throw exception;
		}
		return id;
	}
}
