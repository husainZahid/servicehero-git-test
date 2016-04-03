package com.khayal.shero;

import com.sdl.dxa.modules.generic.model.BlogComments;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

/**
 * Created by DELL on 10/19/2015.
 */
public class BlogRowMapper implements RowMapper<BlogComments>{
	private static final Logger LOG = LoggerFactory.getLogger(BlogRowMapper.class);

	private final String timeZone;

	public BlogRowMapper(String timezone){
		this.timeZone = timezone;
	}

	@Override
	public BlogComments mapRow(ResultSet resultSet, int rowNumber) throws SQLException {
		BlogComments com = new BlogComments();
		com.setCommentid(resultSet.getLong("COMMENTID"));
		com.setParentcommentid(resultSet.getLong("PARENTCOMMENTID"));
		com.setBlogid(resultSet.getLong("BLOGID"));
		com.setComment(resultSet.getString("COMMENT_CONTENT"));
		/* read http://stackoverflow.com/questions/15994450/joda-datetime-to-timestamp-conversion*/
		// Get the "original" value from database.
		Timestamp momentFromDB = resultSet.getTimestamp("COMMENTTIME");
		//LOG.error("  momentFromDB " + momentFromDB);
		// Turn it into a Joda DateTime with time zone.
		DateTime dt = new DateTime(momentFromDB, DateTimeZone.forID(this.timeZone));
		//LOG.error("  dt "+dt);
		// And then turn it back into a timestamp but "with time zone".
		//Timestamp ts = new Timestamp(dt.getMillis());
		//LOG.error("  ts "+ts);
		com.setCommentDate(dt);
		//LOG.error("  rs  " + resultSet.getTimestamp("COMMENTTIME").getTime());
		com.setScore(resultSet.getString("SCORE"));
		com.setStatus(resultSet.getString("STATUS"));

		return com;
	}

}
