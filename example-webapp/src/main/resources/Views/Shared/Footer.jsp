<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="tri" uri="http://www.sdl.com/tridion-reference-impl" %>
<footer class="footer">
    <tri:region name="Left"/>
    <div class="footer-content">
        <div class="row">
            <div class="col-sm-6 sponsors">
                <ul class="list-unstyled clearfix">
                    <tri:region name="Tools"/>
                </ul>
            </div>
            <div class="col-sm-6 social">
                <tri:region name="Social"/>
            </div>
        </div>
        <div class="row">
            <div class="col-md-12">
            <ul class="list-unstyled clearfix site-map">
                <tri:region name="SiteMap"/>
            </ul>
            <ul class="list-unstyled copyrights">
                <tri:region name="Links"/>
            </ul>
        </div>
    </div>
</footer>
