package com.sdl.webapp.main.controller.core;

import com.google.common.base.Strings;
import com.sdl.webapp.common.api.MediaHelper;
import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.content.ContentProvider;
import com.sdl.webapp.common.api.content.ContentProviderException;
import com.sdl.webapp.common.api.content.ContentResolver;
import com.sdl.webapp.common.api.content.PageNotFoundException;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.model.MvcData;
import com.sdl.webapp.common.api.model.Page;
import com.sdl.webapp.common.controller.ViewResolver;
import com.sdl.webapp.common.util.StreamUtils;
import com.sdl.webapp.common.controller.exception.BadRequestException;
import com.sdl.webapp.common.controller.exception.InternalServerErrorException;
import com.sdl.webapp.common.controller.exception.NotFoundException;
import com.sdl.webapp.common.markup.Markup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UrlPathHelper;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static com.sdl.webapp.common.controller.RequestAttributeNames.*;
import static com.sdl.webapp.common.controller.ControllerUtils.*;

/**
 * Main controller. This handles requests that come from the client.
 */
@Controller
public class MainController {

    // TODO: Move this to common-impl or core-module

    private static final Logger LOG = LoggerFactory.getLogger(MainController.class);

    private final UrlPathHelper urlPathHelper = new UrlPathHelper();

    @Value("#{environment.getProperty('AllowJsonResponse', 'false')}")
    private boolean allowJsonResponse;

    private final ContentProvider contentProvider;

    private final ContentResolver contentResolver;

    private final MediaHelper mediaHelper;

    private final WebRequestContext webRequestContext;

    private final Markup markup;

    private final ViewResolver viewResolver;

    @Autowired
    public MainController(ContentProvider contentProvider, ContentResolver contentResolver, MediaHelper mediaHelper,
                          WebRequestContext webRequestContext, Markup markup, ViewResolver viewResolver) {
        this.contentProvider = contentProvider;
        this.contentResolver = contentResolver;
        this.mediaHelper = mediaHelper;
        this.webRequestContext = webRequestContext;
        this.markup = markup;
        this.viewResolver = viewResolver;
    }

    /**
     * Gets a page requested by a client. This is the main handler method which gets called when a client sends a
     * request for a page.
     *
     * @param request The request.
     * @return The view name of the page.
     */
    @RequestMapping(method = RequestMethod.GET, value = "/**", produces = {MediaType.TEXT_HTML_VALUE, MediaType.ALL_VALUE})
    public String handleGetPage(HttpServletRequest request) {
        final String requestPath = webRequestContext.getRequestPath();
        LOG.trace("handleGetPage: requestPath={}", requestPath);

        final Localization localization = webRequestContext.getLocalization();
        final Page page = getPageModel(requestPath, localization);
        LOG.trace("handleGetPage: page={}", page);

        if (!isIncludeRequest(request)) {
            request.setAttribute(PAGE_ID, page.getId());
        }

        request.setAttribute(PAGE_MODEL, page);
        request.setAttribute(LOCALIZATION, localization);
        request.setAttribute(MARKUP, markup);
        request.setAttribute(SCREEN_WIDTH, mediaHelper.getScreenWidth());

        final MvcData mvcData = page.getMvcData();
        LOG.trace("Page MvcData: {}", mvcData);

        return this.viewResolver.resolveView(mvcData, "Page", request);
        //return mvcData.getAreaName() + "/Page/" + mvcData.getViewName();
    }

    /**
     * Gets a page requested by a client in JSON format. For security reasons, this is only enabled if the system
     * property "AllowJsonResponse" is set to {@code true}; if not, a 406 Not Acceptable status code is returned.
     *
     * @param response The response.
     * @throws IOException If an I/O error occurs.
     */
    @RequestMapping(method = RequestMethod.GET, value = "/**", produces = "application/json")
    public void handleGetPageJSON(HttpServletResponse response) throws IOException {
        final ServletServerHttpResponse res = new ServletServerHttpResponse(response);

        // Only handle this if explicitly enabled (by an environment property)
        if (!allowJsonResponse) {
            res.setStatusCode(HttpStatus.NOT_ACCEPTABLE);
            res.close();
            return;
        }

        final String requestPath = webRequestContext.getRequestPath();
        LOG.trace("handleGetPageJSON: requestPath={}", requestPath);

        res.setStatusCode(HttpStatus.OK);
        res.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        final Localization localization = webRequestContext.getLocalization();
        try (final InputStream in = getPageContent(requestPath, localization); final OutputStream out = res.getBody()) {
            StreamUtils.copy(in, out);
        }

        res.close();
    }

    @RequestMapping(method = RequestMethod.GET, value = "/resolve/{itemId}")
    public String handleResolve(@PathVariable String itemId, @RequestParam String localizationId,
                                @RequestParam(required = false) String defaultPath,
                                @RequestParam(required = false) String defaultItem) {
        String url = contentResolver.resolveLink(itemId, localizationId);
        if (Strings.isNullOrEmpty(url)) {
            url = contentResolver.resolveLink(defaultItem, localizationId);
        }
        if (Strings.isNullOrEmpty(url)) {
            url = Strings.isNullOrEmpty(defaultPath) ? "/" : defaultPath;
        }
        return "redirect:" + url;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{locPath}/resolve/{itemId}")
    public String handleResolveLoc(@PathVariable String locPath, @PathVariable String itemId,
                                   @RequestParam String localizationId, @RequestParam String defaultPath,
                                   @RequestParam(required = false) String defaultItem) {
        return handleResolve(itemId, localizationId, defaultPath, defaultItem);
    }

    // Blank page for XPM
    @RequestMapping(method = RequestMethod.GET, value = "/se_blank.html", produces = "text/html")
    @ResponseBody
    public String blankPage() {
        return "";
    }

    /**
     * Throws a {@code BadRequestException} when a request is made to an URL under /system/mvc which is not handled
     * by another controller.
     *
     * @param request The request.
     */
    @RequestMapping(method = RequestMethod.GET, value = INCLUDE_PATH_PREFIX + "**")
    public void handleGetUnknownAction(HttpServletRequest request) {
        throw new BadRequestException("Request to unknown action: " + urlPathHelper.getRequestUri(request));
    }

    /**
     * Handles a {@code NotFoundException}.
     *
     * @param request The request.
     * @return The name of the view that renders the "not found" page.
     */
    @ExceptionHandler(NotFoundException.class)
    public String handleNotFoundException(HttpServletRequest request) {
        request.setAttribute(MARKUP, markup);
        return isIncludeRequest(request) ? SECTION_ERROR_VIEW : NOT_FOUND_ERROR_VIEW;
    }

    /**
     * Handles non-specific exceptions.
     *
     * @param request The request.
     * @param exception The exception.
     * @return The name of the view that renders the "internal server error" page.
     */
    @ExceptionHandler(Exception.class)
    public String handleException(HttpServletRequest request, Exception exception) {
        LOG.error("Exception while processing request for: {}", urlPathHelper.getRequestUri(request), exception);
        request.setAttribute(MARKUP, markup);
        return isIncludeRequest(request) ? SECTION_ERROR_VIEW : SERVER_ERROR_VIEW;
    }

    private Page getPageModel(String path, Localization localization) {
        try {
        	return contentProvider.getPageModel(path, localization);
        } catch (Exception e) {
        	if(path.indexOf("/?") > -1) {
				path = path.replace("/?", "/");
			} else if(path.indexOf("?") > -1) {
				path = path.replace("?", "/?");
			} else {
				if(path.endsWith("/")) {
					path = path.substring(0, path.length() - 1);
				} else {
					path = path + "/";
				}					
			}
	        try {
	        	return contentProvider.getPageModel(path, localization);
	        } catch (PageNotFoundException ex) {
	            LOG.error("Page not found: {}", path, ex);
	            throw new NotFoundException("Page not found: " + path, ex);
	        } catch (ContentProviderException ex) {
	            LOG.error("An unexpected error occurred", ex);
	            throw new InternalServerErrorException("An unexpected error occurred", ex);
	        }
		}
    }
    
   /* private Page getPageModel(String path, Localization localization) {
        try {
			return contentProvider.getPageModel(path, localization);
        } catch (PageNotFoundException e) {
            LOG.error("Page not found: {}", path, e);
            throw new NotFoundException("Page not found: " + path, e);
        } catch (ContentProviderException e) {
            LOG.error("An unexpected error occurred", e);
            throw new InternalServerErrorException("An unexpected error occurred", e);
        }
    }*/

    private InputStream getPageContent(String path, Localization localization) {
        try {
            return contentProvider.getPageContent(path, localization);
        } catch (PageNotFoundException e) {
            LOG.error("Page not found: {}", path, e);
            throw new NotFoundException("Page not found: " + path, e);
        } catch (ContentProviderException e) {
            LOG.error("An unexpected error occurred", e);
            throw new InternalServerErrorException("An unexpected error occurred", e);
        }
    }

    private boolean isIncludeRequest(HttpServletRequest request) {
        return request.getAttribute(WebUtils.INCLUDE_REQUEST_URI_ATTRIBUTE) != null;
    }
}
