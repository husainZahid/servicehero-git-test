package com.sdl.dxa.modules.generic.utilclasses;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import javax.servlet.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class HeaderFilter implements Filter{

    private static Logger logger= LoggerFactory.getLogger(HeaderFilter.class);

    private FilterConfig filterConfig;

    private Map<String, String> headersMap;

    public void init(FilterConfig filterConfig) throws ServletException {
        this.filterConfig = filterConfig;

        String headerParam = filterConfig.getInitParameter("header");
        if (headerParam == null) {
            logger.info("No headers were found in the web.xml (init-param) for the HeaderFilter !");
            return;
        }

        // Init the header list :
        headersMap = new LinkedHashMap<String, String>();

        if (headerParam.contains("|")) {
            String[] headers = headerParam.split("\\|");
            for (String header : headers) {
                parseHeader(header);
            }

        } else {
            parseHeader(headerParam);
        }

        logger.info("The following headers were registered in the HeaderFilter :");
        Set headers = headersMap.keySet();
		Iterator<String> iterator = headers.iterator();
	    String setElement;
        while(iterator.hasNext()) {
            setElement = iterator.next();
            logger.info(setElement + ':' + headersMap.get(setElement));
        }
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (headersMap != null) {
            // Add the header to the response
	        Set headers = headersMap.keySet();
			Iterator<String> iterator = headers.iterator();
		    String setElement;
	        while(iterator.hasNext()) {
	            setElement = iterator.next();
		        if(setElement.equalsIgnoreCase("expires")) {
			        try {
				        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss zzz");
//				        logger.info("Formatted Date : " + simpleDateFormat.format(new java.util.Date(System.currentTimeMillis() + (Integer.parseInt(headersMap.get(setElement)) * 24 * 60 * 60 * 1000))));
			            ((HttpServletResponse) response).setHeader(setElement, simpleDateFormat.format(new java.util.Date(System.currentTimeMillis() + (Integer.parseInt(headersMap.get(setElement)) * 24 * 60 * 60 * 1000))));
			        } catch(Exception e) {
	                    logger.error("Error adding header : " + setElement + " [" + headersMap.get(setElement) + "] (" + e.getMessage() + ")");
			        }
		        } else
		            ((HttpServletResponse) response).setHeader(setElement, headersMap.get(setElement));
//	            logger.info(setElement + ':' + headersMap.get(setElement));
	        }
        }

        final HttpSession session = ((HttpServletRequest)request).getSession(false);
        if (session != null) {
/*
            final Cookie sessionCookie = new Cookie("JSESSIONID", session.getId());
            sessionCookie.setMaxAge(-1);
            sessionCookie.setSecure(false);
            sessionCookie.setPath("/");
            ((HttpServletResponse) response).addCookie(sessionCookie);
*/
	        //((HttpServletResponse) response).addHeader("Set-Cookie", "CBKSESSIONID=" + session.getId() + "; host=" + adminV3.MyHttpServlet.getValue("webAddress") + "; max-age=-1; Path=/; HTTPOnly;");
        }
        // Continue
        chain.doFilter(request, response);
    }

    public void destroy() {
        this.filterConfig = null;
        this.headersMap = null;
    }

    private void parseHeader(String header) {
        String headerName = header.substring(0, header.indexOf(":"));
        if (!headersMap.containsKey(headerName)) {
		        headersMap.put(headerName, header.substring(header.indexOf(":") + 1).trim());
        }
    }
	

}
