<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tri" uri="http://www.sdl.com/tridion-reference-impl" %>
<%@ taglib prefix="xpm" uri="http://www.sdl.com/tridion-xpm" %>
<jsp:useBean id="entity" type="com.sdl.dxa.modules.generic.model.CustomContentList" scope="request"/>
<jsp:useBean id="markup" type="com.sdl.webapp.common.markup.Markup" scope="request"/>
<div class="post-list"  ${markup.entity(entity)}>
 <c:forEach var="item" items="${entity.itemListElements}">
    <h2 class="post-title">

        <a href="${item.link.url}" class="post-title" style="TEXT-DECORATION: NONE" title="${item.text}">
         ${item.text}</a>
    </h2>
    <div class="post-meta">By ${item.text} on ${item.date}</div>
           <div class="post-img"><img src="${item.svgImage}" width="132" height="74" alt="${item.introText}"></div>
           <div class="post-entry">${item.introText} </div>

    <a href="${item.link.url}" class="post-more" style="TEXT-DECORATION: NONE" title="${item.text}"> More</a>

 </c:forEach>

</div>
