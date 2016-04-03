<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="org.springframework.web.util.UrlPathHelper" %>
<%@ page import="com.sdl.webapp.common.api.model.entity.SitemapItem" %>
<%@ page import="java.util.Iterator" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="xpm" uri="http://www.sdl.com/tridion-xpm" %>
<jsp:useBean id="entity" type="com.sdl.webapp.common.api.model.entity.SitemapItem" scope="request"/>
<jsp:useBean id="markup" type="com.sdl.webapp.common.markup.Markup" scope="request"/>

<% request.setAttribute("requestPath", new UrlPathHelper().getOriginatingRequestUri(request));
   final Iterator<SitemapItem> iterator = entity.getItems().iterator();
   final int rows = (int)entity.getItems().size();
   
   %><ul class="nav navbar-nav nav-main" ${markup.entity(entity)}><%
   for (int row = 0; row < rows; row++) {
   final SitemapItem item = iterator.next();
   String cssClass = request.getAttribute("requestPath").toString().startsWith(item.getUrl()) ? "active" : "";
   %><li class="<%=cssClass%> dropdown">
        <a href="#" title="<%= item.getTitle()%>" class="dropdown-toggle" data-hover="dropdown"
           data-toggle="dropdown" data-delay="300" data-close-others="true"><%=item.getTitle()%>
            <span class="fa fa-angle-down dropdown-arrow"></span>
        </a>
        <ul class="dropdown-menu"><%
        for (SitemapItem link : item.getItems()) {
            %><%= markup.siteMapList(link) %><%
        }
        %>
        <li class="nav-img"><img src="assets/images/img-the-index.jpg" width="260" height="168"></li>
        </ul>
      </li>
   <%}%>
   </ul>

