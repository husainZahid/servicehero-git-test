<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="xpm" uri="http://www.sdl.com/tridion-xpm" %>
<jsp:useBean id="entity" type="com.sdl.dxa.modules.generic.model.HTMLForm" scope="request"/>
<jsp:useBean id="markup" type="com.sdl.webapp.common.markup.Markup" scope="request"/>

<c:choose>
	<c:when test="${not empty entity.submitUrl}">
		<form id="${entity.name}" name="${entity.name}" method="${entity.method}" action="${entity.submitUrl}">
	</c:when>
	<c:when test="${empty entity.submitUrl}">
		<form id="${entity.name}" name="${entity.name}">
	</c:when>
</c:choose>
    <c:if test="${not empty entity.formElement}">
        <c:forEach var="element" items="${entity.formElement}" varStatus="status">
            <div class="${element.label}">
                <div class="circle-squiggle" id="squiggle"><img src="assets/images/circle-squiggle.png" width="274" height="99"> </div>
                <span class="${element.value}" id="searchLabel"><i class="icon-${fn:toLowerCase(element.type)"></i> ${element.name}</span>
                <%
                    String[] parts = element.getName().split(" ");
                    String camelCaseString = "";
                    int iCounter = 0;
                    for (String part : parts){
                        if(iCounter == 0)
                        {
                            camelCaseString = camelCaseString + part.toLowerCase();
                        } else {
                            camelCaseString = camelCaseString + (part.substring(0, 1).toUpperCase() +
                                                                                   part.substring(1).toLowerCase());
                        }
                    }
                %>
                <input type="${element.type)" class="${element.validationClass)" id="<%=camelCaseString%>">
            </div>
        </c:forEach>
    </c:if>
