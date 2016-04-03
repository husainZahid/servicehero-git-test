package com.khayal.shero.services;

import com.sdl.dxa.modules.generic.model.BlogComments;

import java.util.List;

/**
 * Created by DELL on 10/19/2015.
 */
public interface BlogCommentService {

	public String sayHello(String s);
	public void insertData(BlogComments comments);
    public List<BlogComments> getAllCommentList();
	public void updateData(BlogComments comment);
	public List<BlogComments> getCommentListByBlogId(long blogid);
}
