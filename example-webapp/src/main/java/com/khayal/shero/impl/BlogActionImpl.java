package com.khayal.shero.impl;

import com.khayal.shero.BlogRowMapper;
import com.khayal.shero.SHSurveyUser;
import com.khayal.shero.UserActivityLog;
import com.sdl.dxa.modules.generic.model.BlogComments;

import org.apache.commons.dbcp2.PoolableConnection;
import org.apache.commons.dbcp2.PoolingDataSource;
import org.apache.commons.dbcp2.Utils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.servlet.http.HttpServletRequest;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by DELL on 10/18/2015.
 */

public class BlogActionImpl extends BlogComments {
	private static final Logger LOG = LoggerFactory.getLogger(BlogActionImpl.class);

	public static String sayHello(String s){
		return "Hello "+s;
	}

	public static String getRating(HttpServletRequest request, String blogid){
		long avgRating = 0;
		int count = 0;
		LOG.error("blogid inside getRating is : " + blogid);
		/*select all the ratings for that blog and take the average of it */
		
		ApplicationContext context = RequestContextUtils.getWebApplicationContext(request);
		PoolingDataSource<PoolableConnection> dataSource = (PoolingDataSource<PoolableConnection>) context.getBean("dataSource");
		Connection connection = null;
		try {
			String sqlQuery = "SELECT avg(rating_value) avg,  count(*) cnt FROM BLOG_RATINGS WHERE BLOGID = "+blogid;
			connection = dataSource.getConnection();
			PreparedStatement ps = connection.prepareStatement(sqlQuery);
			ResultSet objRs = ps.executeQuery();
			if (objRs.next()){
				if (objRs.getLong("avg") != 0) {
					avgRating = objRs.getLong("avg");
					count = objRs.getInt("cnt");
				}
			}
			objRs.close();
			ps.close();

		}catch (Exception exception){
			//TODO: later handle exception handling
			LOG.error("Exception occured : ");
			LOG.error(exception.getMessage(),exception);
		} finally {
			Utils.closeQuietly(connection);
		}

		return avgRating+"~"+count;
	}
	/*
		comment
	 */
	public static void insertCommentData(HttpServletRequest request, BlogComments comments) throws Exception {
		int result = 0;
		long nextSeq =0;
		try {
			// insert
			String sql = "INSERT INTO BLOG_COMMENTS (COMMENTID, PARENTCOMMENTID, BLOGID, ORIG_COMMENT_CONTENT, COMMENTERID, COMMENTTIME, SERVERTIME, USERTIMEZONE, STATUS )"
				                    + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
			ApplicationContext context = RequestContextUtils.getWebApplicationContext(request);
			PoolingDataSource<PoolableConnection> dataSource = (PoolingDataSource<PoolableConnection>) context.getBean("dataSource");

			/*Object[] params = new Object[] { comments.getCommentid(), comments.getParentcommentid(), comments.getBlogid(), comments.getComment(), comments.getCreatorid(),
			                                comments.getCommentDate(), comments.getCommentDate(), comments.getCommentDate(),  "C" };

			// define SQL types of the arguments
			int[] types = new int[] { Types.BIGINT, Types.BIGINT, Types.BIGINT, Types.VARCHAR, Types.BIGINT, Types.TIMESTAMP, Types.TIMESTAMP, Types.TIMESTAMP,
			Types.VARCHAR};

			JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
			// execute insert query to insert the data
	        // return number of row / rows processed by the executed query
			int row = jdbcTemplate.update(sql, params, types);  */
			Connection connection = dataSource.getConnection();
			UserActivityLog userLog = new UserActivityLog();
			nextSeq = userLog.getNextSequence("BLOGCOMMENTS_SEQ", connection);
		    if(nextSeq == 0){
			    throw new Exception("Error getting sequence number ");
		    }

			PreparedStatement ps = connection.prepareStatement(sql);
			ps.setLong(1, nextSeq);
			ps.setLong(2, comments.getParentcommentid());
			ps.setLong(3, comments.getBlogid());
			ps.setString(4, comments.getComment());
			ps.setLong(5, comments.getCreatorid());
			Timestamp ts = new Timestamp(comments.getCommentDate().getMillis());
			ps.setTimestamp(6, ts);
			ps.setTimestamp(7, ts);
			ps.setTimestamp(8, ts);
			ps.setString(9, "C");
			result = ps.executeUpdate();
			ps.close();
			LOG.error(result + " row inserted. ");


		}catch (Exception exception){
			//TODO: later handle exception handling
			LOG.error("Exception occured : ");
			LOG.error(exception.getMessage(),exception);
			throw new Exception("Error Inserting comments "+exception);
		}
	}
	/*
		rating data
	 */
	public static void insertRatingData(HttpServletRequest request,String blogRatingScore, String blogid) throws Exception {
		int result = 0;
		long nextSeq =0;
		try {
			// insert

			String sql = "INSERT INTO BLOG_RATINGS (RATEID, BLOGID, RATING_VALUE, CREATORID, RATETIME, SERVERTIME, USERTIMEZONE)"
				                    + " VALUES (?, ?, ?, ?, ?, ?, ?)";
			ApplicationContext context = RequestContextUtils.getWebApplicationContext(request);
			PoolingDataSource<PoolableConnection> dataSource = (PoolingDataSource<PoolableConnection>) context.getBean("dataSource");

			Connection connection = dataSource.getConnection();
			UserActivityLog userLog = new UserActivityLog();
			nextSeq = userLog.getNextSequence("BLOG_RATINGS_SEQ", connection);
		    if(nextSeq == 0){
			    throw new Exception("Error getting sequence number ");
		    }

			PreparedStatement ps = connection.prepareStatement(sql);
			ps.setLong(1, nextSeq);
			ps.setLong(2, Long.parseLong(blogid));
			ps.setLong(3,  Long.parseLong(blogRatingScore));
			SHSurveyUser shSurveyUser = (SHSurveyUser) request.getSession().getAttribute("shSurveyUser");
			shSurveyUser.setHttpServletRequest(request);
			ps.setLong(4, shSurveyUser.getId());
			DateTime dt = new DateTime(DateTimeZone.forID("Asia/Riyadh"));
			Timestamp ts = new Timestamp(dt.getMillis());
			ps.setTimestamp(5, ts);
			ps.setTimestamp(6, ts);
			ps.setTimestamp(7, ts);
			result = ps.executeUpdate();
			ps.close();
			LOG.error(result + " row inserted. ");


		}catch (Exception exception){
			//TODO: later handle exception handling
			LOG.error("Exception occured : ");
			LOG.error(exception.getMessage(),exception);
			throw new Exception("Error Inserting rating for blogs "+exception);
		}
	}

	public List<BlogComments> getAllCommentList(){
		return null;
	}

	public void updateData(BlogComments comment) {

	}

	public static List<BlogComments> getCommentListByBlogId(HttpServletRequest request, String blogid){
		List commentList = new ArrayList();
		try {
			LOG.error("blogid is : " + blogid);
			String sqlQuery = "SELECT C.COMMENTID, C.PARENTCOMMENTID, C.BLOGID, C.COMMENT_CONTENT, C.COMMENTTIME, C.SCORE, C.STATUS  FROM BLOG_COMMENTS C" +
						                                                   " WHERE C.BLOGID = "+blogid+" and C.STATUS = 'A'";
			ApplicationContext context = RequestContextUtils.getWebApplicationContext(request);
            PoolingDataSource<PoolableConnection> dataSource = (PoolingDataSource<PoolableConnection>) context.getBean("dataSource");
            JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
			/*Connection connection = dataSource.getConnection();
			PreparedStatement ps = connection.prepareStatement(sqlQuery);
			ResultSet objRs = ps.executeQuery();*/
			commentList = jdbcTemplate.query(sqlQuery, new BlogRowMapper("Asia/Riyadh"));
			/*while(objRs.next()){
				BlogComments com = new BlogComments();
				com.setCommentid(objRs.getLong("COMMENTID"));
				com.setParentcommentid(objRs.getLong("PARENTCOMMENTID"));
				com.setBlogid(objRs.getLong("BLOGID"));
				com.setComment(objRs.getString("COMMENT_CONTENT"));
				com.setCommentDate(objRs.getDate("COMMENT_DATE"));
				com.setModerator(objRs.getString("MODERATOR"));
				com.setModeratedDate(objRs.getDate("MODERATED_DATE"));
				com.setScore(objRs.getString("SCORE"));
				com.setStatus(objRs.getString("STATUS"));
				commentList.add(com);
			} */
			LOG.error("commentList has : " + commentList.size() + " rows");


		}catch (Exception exception){
			//TODO: later handle exception handling
			LOG.error("Exception occured : ");
			LOG.error(exception.getMessage(),exception);
		}
		return commentList;
	}




}
