<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="xpm" uri="http://www.sdl.com/tridion-xpm" %>
<jsp:useBean id="entity" type="com.sdl.webapp.common.api.model.entity.LinkList" scope="request"/>
<jsp:useBean id="markup" type="com.sdl.webapp.common.markup.Markup" scope="request"/>
<div class="col-sm-6 social">
    <ul class="list-unstyled clearfix" ${markup.entity(entity)}>
        <c:forEach var="link" items="${entity.links}" varStatus="status">
            <li class="${fn:toLowerCase(fn:replace(link.linkText,' ', '-'))}">
            <a title="${link.alternateText}" target="_blank" href="${link.url}">
               <span class="icon-${fn:toLowerCase(link.linkText)}"></span>
            </a>
            </li>
        </c:forEach>
    </ul>
</div>
