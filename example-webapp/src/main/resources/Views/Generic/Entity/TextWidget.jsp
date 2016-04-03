<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="xpm" uri="http://www.sdl.com/tridion-xpm" %>
<jsp:useBean id="entity" type="com.sdl.webapp.common.api.model.entity.LinkList" scope="request"/>
<jsp:useBean id="markup" type="com.sdl.webapp.common.markup.Markup" scope="request"/>
<div class="text-widget clearfix">
    <h2>${entity.headline}</h2>
    <c:forEach var="link" items="${entity.links}" varStatus="status">
    <c:set var="className" value="${fn:replace(fn:toLowerCase(link.linkText), ' ', '-')}" />
    <a href="${link.url}" class="${className}"><span class="${link.alternateText}"></span>${link.linkText}</a>
	</c:forEach>
    <a class="btn-red-lg" href="#">Vote Now!</a>
</div>