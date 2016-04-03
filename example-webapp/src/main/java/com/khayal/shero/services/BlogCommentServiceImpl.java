package com.khayal.shero.services;

import com.khayal.shero.dao.BlogActionDAO;
import com.sdl.dxa.modules.generic.model.BlogComments;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Created by DELL on 10/19/2015.
 */
public class BlogCommentServiceImpl implements  BlogCommentService {

	@Autowired
	BlogActionDAO blogCommentDAO;

	@Override
	public String sayHello(String s){
		return blogCommentDAO.sayHello(s);
	}

	@Override
	public void insertData(BlogComments comments){
		blogCommentDAO.insertData(comments);
	}

	@Override
	public List<BlogComments> getAllCommentList(){
		return blogCommentDAO.getAllCommentList();
	}
	@Override
	public void updateData(BlogComments comment){
		blogCommentDAO.updateData(comment);
	}

	@Override
	public List<BlogComments> getCommentListByBlogId(long blogid){
		return blogCommentDAO.getCommentListByBlogId(blogid);
	}


}
