<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.sdl.webapp.common.api.ScreenWidth" %>
<%@ page import="com.sdl.webapp.common.api.model.entity.SitemapItem" %>
<%@ page import="java.util.Iterator" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tri" uri="http://www.sdl.com/tridion-reference-impl" %>
<%@ taglib prefix="xpm" uri="http://www.sdl.com/tridion-xpm" %>
<jsp:useBean id="entity" type="com.sdl.webapp.common.api.model.entity.SitemapItem" scope="request"/>
<jsp:useBean id="markup" type="com.sdl.webapp.common.markup.Markup" scope="request"/>
<jsp:useBean id="screenWidth" type="com.sdl.webapp.common.api.ScreenWidth" scope="request"/>
<div>
<%
    final int cols = screenWidth == ScreenWidth.SMALL ? 2 : 3;
    final int rows = (int) Math.ceil(entity.getItems().size() / (double) cols);
    final Iterator<SitemapItem> iterator = entity.getItems().iterator();

    for (int row = 0; row < rows; row++) {
        for (int col = 0; col < cols && iterator.hasNext(); col++) {
            final SitemapItem item = iterator.next();
            %>
            <li>
                <a href="javascript:;"><%= item.getTitle() %></a>
                <ul class="list-unstyled clearfix"><%
                   for (SitemapItem link : item.getItems()) {
                       %><%= markup.siteMapList(link) %><%
                   }
                %></ul>
            </li>
                <%
        }
    }

%>