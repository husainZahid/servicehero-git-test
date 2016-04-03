package com.sdl.webapp.addon.controller;

/**
 * Created by DELL on 11/4/2015.
 */

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.content.ContentProvider;
import com.sdl.webapp.common.api.content.NavigationProvider;
import com.sdl.webapp.common.api.content.NavigationProviderException;
import com.sdl.webapp.common.controller.AbstractController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Navigation controller for the Generic area.
 *
 * This handles include requests to /system/mvc/Generic/Navigation/Navigation/{regionName}/{entityId}
 *
 */
/*@Controller*/
/*@RequestMapping(INCLUDE_PATH_PREFIX +"Generic/GenericNavigation")*/
public class GenericNavigationController  extends AbstractController {

	private static final Logger LOG = LoggerFactory.getLogger(GenericNavigationController.class);

	private static final String NAV_TYPE_TOP = "Top";
	    private static final String NAV_TYPE_LEFT = "Left";
	    private static final String NAV_TYPE_BREADCRUMB = "Breadcrumb";

	    private final WebRequestContext webRequestContext;

	    private final NavigationProvider navigationProvider;
		private final ObjectMapper objectMapper;
		private final ContentProvider contentProvider;

	/*@Autowired*/
	public GenericNavigationController(WebRequestContext webRequestContext, NavigationProvider navigationProvider, ObjectMapper objectMapper, ContentProvider contentProvider) {
	    this.webRequestContext = webRequestContext;
	    this.navigationProvider = navigationProvider;
		this.objectMapper = objectMapper; this.contentProvider = contentProvider;
	}


	/**
     * Handles a request for navigation data, for example for the top navigation menu, left-side navigation or
     * breadcrumb bar.
     *
     * @param request The request.
     * @param regionName The name of the region.
     * @param entityId The name of the entity.
     * @param navType Navigation type.
     * @return The name of the entity view that should be rendered for this request.
     * @throws NavigationProviderException If an error occurs so that the navigation data cannot be retrieved.
     */
   /* @RequestMapping(method = RequestMethod.GET, value = NAVIGATION_ACTION_NAME + "/{regionName}/{entityId}")
    public String handleGetNavigation(HttpServletRequest request, @PathVariable String regionName,
                                      @PathVariable String entityId, @RequestParam String navType)
            throws NavigationProviderException {
        LOG.error("handleGetNavigation: regionName={}, entityId={}", regionName, entityId);

        final Entity entity = getEntityFromRequest(request, regionName, entityId);
        request.setAttribute(ENTITY_MODEL, entity);

        final String requestPath = webRequestContext.getRequestPath();
        final Localization localization = webRequestContext.getLocalization();
	    LOG.error("handleGetNavigation: requestPath= "+ requestPath);
        final NavigationLinks navigationLinks;
        switch (navType) {
            case NAV_TYPE_TOP:
                navigationLinks = navigationProvider.getTopNavigationLinks(requestPath, localization);
                break;

            case NAV_TYPE_LEFT:
                navigationLinks = navigationProvider.getContextNavigationLinks(requestPath, localization);
                break;

            case NAV_TYPE_BREADCRUMB:
	            LOG.error("handleGetNavigation: this is breadcrumb= ");
	            /*if (requestPath.contains("blog")) {
		            /*Maipulate the breadcrumb here for pages that are not included in navigation like blog*/
		           /* navigationLinks = null;
		        } else {   */
		      //      navigationLinks = navigationProvider.getBreadcrumbNavigationLinks(requestPath, localization);
	            /*}*/
               // break;

            /*default:
                LOG.warn("Unsupported navigation type: {}", navType);
                navigationLinks = null;
                break;
        }

        if (navigationLinks != null) {
            navigationLinks.setEntityData(entity.getEntityData());
            navigationLinks.setPropertyData(entity.getPropertyData());
            request.setAttribute(ENTITY_MODEL, navigationLinks);
        }

        final MvcData mvcData = entity.getMvcData();
        LOG.trace("Entity MvcData: {}", mvcData);
        return resolveView(mvcData, "Entity", request);
        //return mvcData.getAreaName() + "/Entity/" + mvcData.getViewName();
    }

	private Link createLinkForItem(String url, String title) {
        final Link link = new Link();
        link.setUrl(url);
        link.setLinkText(title);
        return link;
    }

       */


}
