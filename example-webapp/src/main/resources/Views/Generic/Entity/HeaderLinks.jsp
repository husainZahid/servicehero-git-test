<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tri" uri="http://www.sdl.com/tridion-reference-impl" %>
<%@ taglib prefix="xpm" uri="http://www.sdl.com/tridion-xpm" %>
<jsp:useBean id="entity" type="com.sdl.webapp.common.api.model.entity.ItemList" scope="request"/>
<jsp:useBean id="markup" type="com.sdl.webapp.common.markup.Markup" scope="request"/>
<c:if test="${not empty entity.itemListElements}" >
    <ul class="nav navbar-nav navbar-right nav-global" ${markup.entity(entity)}>
        <c:forEach var="element" items="${entity.itemListElements}" varStatus="status">
        ${markup.property(entity, "itemListElements", status.index)}
            
               <c:if test="${not empty element.link.url}">
			   <li ${markup.property(element, "headline")} class="${element.headline}" ${markup.property(element.link, "linkText")}>
			   <tri:link link="${element.link}"  />
			   <span ${markup.property(element, "text")} class="${element.text}"></span>
			   </li>
			   </c:if>
			   <c:if test="${empty element.link.url}">
			   <li ${markup.property(element, "headline")} class="${element.headline}" ${markup.property(element.link, "linkText")}>
			   
				<a id="${element.link.linkText}" href="javascript:;"><span class="${element.text}"></span></a>
				</li>
			   </c:if>
               
               
            
        </c:forEach>
    </ul>
</c:if>
