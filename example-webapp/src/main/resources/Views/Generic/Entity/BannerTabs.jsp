<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="xpm" uri="http://www.sdl.com/tridion-xpm" %>
<jsp:useBean id="entity" type="com.sdl.webapp.common.api.model.entity.LinkList" scope="request"/>
<jsp:useBean id="markup" type="com.sdl.webapp.common.markup.Markup" scope="request"/>
<ul class="nav nav-tabs row" id="tabsMain">
    <c:forEach var="link" items="${entity.links}" varStatus="status">
    <li class="col-xs-4 active">
        <c:set var="classname" value="${fn:toLowerCase(link.alternateText)" />
        <c:set var="idname" value="${fn:replace(link.alternateText, '-', '')" />
        <c:choose>
            <c:when test="${empty link.url}">
                 <a href="javascript:;"  class ="${classname}" id = "${idname}" >${link.linkText}</a>
            </c:when>
            <c:when test="${not empty link.url}">
                <a href="${link.url}"  class ="${classname}" id = "${idname}" >${link.linkText}</a>
            </c:when>
        </c:if>
    </li>
</ul>
