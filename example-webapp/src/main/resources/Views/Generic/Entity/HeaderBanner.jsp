<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="xpm" uri="http://www.sdl.com/tridion-xpm" %>
<jsp:useBean id="entity" type="com.sdl.webapp.common.api.model.entity.Image" scope="request"/>
<jsp:useBean id="markup" type="com.sdl.webapp.common.markup.Markup" scope="request"/>
<!--
1. need to choose an image out of a list everytime this load in random
2. Hello Guest should change to Welcome Back, <Name of the consumer>
-->
<header class="header">
     <div class="container-fluid" ${markup.entity(entity)}>
        <div class="row">
        <c:set var="rand"><%= java.lang.Math.round(java.lang.Math.random() * 5) %></c:set>
        <c:set var="imageurl" value="${fn:substringBefore(text, ' ')}" />
        <c:set var="imageurl">${entity.url}</c:set>

            <div class="hero-unit" style="background-image:url('${entity.url}')">
                 <p class="hero-welcome">Hello guest,</p>
                 <div class="hero-content">
                     ${entity.url.alternateText}
                 </div>
            </div>
        </div>
     </div>
</header>