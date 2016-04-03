<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="xpm" uri="http://www.sdl.com/tridion-xpm" %>
<jsp:useBean id="entity" type="com.sdl.webapp.common.api.model.entity.LinkList" scope="request"/>
<jsp:useBean id="markup" type="com.sdl.webapp.common.markup.Markup" scope="request"/>
<div class="container-fluid">
    <div class="row">
        <div class="col-md-12 footer-nav">
            <ul class="list-unstyled clearfix" ${markup.entity(entity)}>
                <c:forEach var="link" items="${entity.links}" varStatus="status">
                    <li class="${fn:toLowerCase(fn:replace(link.alternateText,' ', '-'))}">
                        <c:if test="${fn:toLowerCase(link.linkText)=='blog'}">
                        <span></span>
                        </c:if>
                        <a title="${link.alternateText}" href="${link.url}">${link.linkText}</a>
                        
                     </li>
                </c:forEach>
            </ul>
        </div>
    </div>
</div>