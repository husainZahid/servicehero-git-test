<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="tri" uri="http://www.sdl.com/tridion-reference-impl" %>
<header class="header">
     <div class="container-fluid">
        <div class="row">
            <c:set var="imageurl" >${entity.url}</c:set>
            <div class="hero-unit" style="background-image:url('${entity.url}')">
                 <p class="hero-welcome">Hello guest,</p>
                 <div class="hero-content">
                     ${entity.url.alternateText}
                 </div>
            </div>
        </div>
     </div>
</header>
<section class="tabs-main">
    <div class="container-fluid">
        <div class="row">
            <div class="col-sm-12">
            <tri:region name="tabs"/>
                <div class="row">
                    <div class="col-sm-12 rate-pane">
                        <tri:region name="search"/>
                        <div class="overlay rate-overlay" id="rateOverlay">
                            <div id="rateLoader" class="loader-wrapper">
                                <div class="loader-content">
                                    <span class="icon-spin5 animate-spin"></span>
                                    Loading...
                                </div>
                            </div>
                            <div class="overlay-content" id="rateContent"></div>
                            <div class="overlay-controls">
                                <ul class="list-unstyled">
                                    <li><a href="javascript:;" class="btn-close" id="closeRate"><span class="icon-close"></span></a></li>
                                </ul>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</section>