package com.sdl.webapp.addon.controller;


import com.khayal.shero.*;
import com.khayal.shero.impl.BlogActionImpl;
import com.khayal.shero.services.SHSearchService;
import com.sdl.dxa.modules.generic.model.BlogComments;
import com.sdl.dxa.modules.generic.model.KeyValuePair;
import com.sdl.dxa.modules.generic.utilclasses.PasswordEncryption;
import com.sdl.dxa.modules.generic.utilclasses.TaxonomyComparator;
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
import com.sdl.webapp.common.controller.exception.InternalServerErrorException;
import com.sdl.webapp.common.controller.exception.NotFoundException;
import com.sdl.webapp.common.markup.Markup;
import com.tridion.marketingsolution.profile.Contact;

import com.tridion.taxonomies.Keyword;
import com.tridion.taxonomies.TaxonomyFactory;
import org.apache.commons.dbcp2.PoolableConnection;
import org.apache.commons.dbcp2.PoolingDataSource;
import org.apache.commons.dbcp2.Utils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.springframework.web.util.UrlPathHelper;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.sql.*;
import java.util.*;

import static com.sdl.webapp.common.controller.RequestAttributeNames.*;

/*import com.khayal.shero.impl.BlogActionImpl;
import com.sdl.dxa.modules.generic.model.BlogComments;*/

@Controller
public class GenericMainController {

	// TODO: Move this to common-impl or core-module

	private static final Logger LOG = LoggerFactory.getLogger(GenericMainController.class);

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
	public GenericMainController(ContentProvider contentProvider, ContentResolver contentResolver, MediaHelper mediaHelper,
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
	//@RequestMapping(method = RequestMethod.POST, value = "/{languageName}/{pageName}")
	//@RequestMapping(method = RequestMethod.POST, value = "/{languageName}/{pageName}", produces = "text/html")
	//@RequestMapping(method = RequestMethod.GET, value = "/{languageName}/{pageName}")
	@RequestMapping(method = RequestMethod.GET, value = {"/en/{pageName}", "/ar/{pageName}", "/en/dashboard/{pageName}"}, produces = {MediaType.TEXT_HTML_VALUE, MediaType.ALL_VALUE})
	public String handleGetPage(@PathVariable Map<String, String> pathVariables, HttpServletRequest request, HttpServletResponse response) {
		//String languageName = pathVariables.get("languageName");
		HttpSession session = request.getSession();
		final String requestPath = webRequestContext.getRequestPath();
		String pageName = pathVariables.get("pageName");

		String languageName = "", countryName = "";

		languageName = (String) request.getSession().getAttribute("userLanguage");
		if ((webRequestContext.getFullUrl() + "/").indexOf("/ar/") > -1) {
			languageName = "ar";
		} else {
			languageName = "en";
		}
		if (languageName == null || languageName.length() == 0)
			languageName = "en";
		if(request.getSession().getAttribute("shortcutPage") == null)
			request.getSession().setAttribute("userLanguage", languageName);

		countryName = (String) request.getSession().getAttribute("userCountry");
		if (webRequestContext.getFullUrl().indexOf("/kw/") > -1)
			countryName = "kw";
		else if (webRequestContext.getFullUrl().indexOf("/ae/") > -1)
			countryName = "ae";
		if (countryName == null || countryName.length() == 0)
			countryName = "kw";
		if(request.getSession().getAttribute("shortcutPage") == null)
			request.getSession().setAttribute("userCountry", countryName);

		LOG.debug("handleGetPage: pageName={}", pageName);
		final Localization localization = webRequestContext.getLocalization();
		Page page = null;
		try {
			page = getPageModel(requestPath, localization);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
		if (!isIncludeRequest(request)) {
			request.setAttribute(PAGE_ID, page.getId());
		}

		request.setAttribute(PAGE_MODEL, page);
		request.setAttribute(LOCALIZATION, localization);
		request.setAttribute(MARKUP, markup);
		request.setAttribute(SCREEN_WIDTH, mediaHelper.getScreenWidth());
		session.setAttribute("pageName", pageName);
		String shStatus = "", strPageURL = "";
		int result = 0;
		String ajaxPageName = request.getParameter("ajaxPageName");
		if (ajaxPageName == null || ajaxPageName.length() == 0)
			ajaxPageName = "None";
		LOG.debug("GenericMainController ");
		LOG.debug("ajaxPageName: " + ajaxPageName);
		//else this is from dashboard
		strPageURL = webRequestContext.getFullUrl();
		if (!pageName.contains("brand-list"))
			session.setAttribute("socialshareUrl", strPageURL);

		if (!languageName.equals((String) request.getSession().getAttribute("userLanguage"))) {
			session.setAttribute("userLanguage", languageName);
		}

		String redirectItemType = "", redirectItemCode = "", redirectDealerCode = "", redirectSectorCode= "";
		if (request.getParameter("redirectItemType") != null) {
			redirectItemType = request.getParameter("redirectItemType");
			if (redirectItemType == null || redirectItemType.length() == 0)
				redirectItemType = "N/A";
			redirectItemCode = request.getParameter("redirectItemCode");
			if (redirectItemCode == null || redirectItemCode.length() == 0)
				redirectItemCode = "N/A";
			redirectDealerCode  =  request.getParameter("dealerCode");
			if (redirectDealerCode == null || redirectDealerCode.length() == 0)
				redirectDealerCode = "N/A";

			if (!redirectItemCode.equals("N/A")) {
				if (redirectItemType.equals("brandCode")) {
					String strTempValue = "";
					HashMap<String, HashMap> objHM = new HashMap<String, HashMap>();
					HashMap<String, String> objHMSort = new HashMap<String, String>();
					HashMap<String, String> objHMIdSectorId = new HashMap<String, String>();
					objHM = SHSearchService.searchBrands(request, BrandLuceneIndexer.INDEXER_SERACH_FIELD_BRAND_CODE, redirectItemCode);
					objHMSort = (HashMap) objHM.get("sorter");
					objHMIdSectorId = (HashMap) objHM.get("sectorId");
					List<String> titles = new ArrayList<String>(objHMSort.keySet());
					for (String title : titles) {
						strTempValue = objHMSort.get(title);
						redirectSectorCode = String.valueOf(Long.parseLong(objHMIdSectorId.get(strTempValue)));
					}
					if (ajaxPageName.equals("check-user-login") || ajaxPageName.equals("set-parameter")) {
						request.getSession().setAttribute("surveySector", redirectSectorCode);
						request.getSession().setAttribute("surveyBrand", redirectItemCode);
						request.getSession().setAttribute("surveyDealer", redirectDealerCode);
					}else if(ajaxPageName.equals("dashboard-check-user-login")  ){
						/*request.getSession().setAttribute("dashboardSector", redirectSectorCode);
						request.getSession().setAttribute("dashboardBrand", redirectItemCode);
						request.getSession().setAttribute("dashboardDealer", redirectDealerCode);     */
					}

				} else if (redirectItemType.equals("sectorCode")) {
					try {
						Long.parseLong(redirectItemCode);
						request.getSession().setAttribute("surveySector", redirectItemCode);
					} catch (Exception ex) {
						request.getSession().setAttribute("surveySearch", redirectItemCode);
					}
				}
			}
		}


		if (ajaxPageName.equals("check-user-login") || ajaxPageName.equals("dashboard-check-user-login")) {
			javax.servlet.http.Cookie[] userCookies = request.getCookies();
			long shUserId = 0, shBrandUserId = 0;
			String shUserCountry = "", shUserLanguage = "";
			if (userCookies != null) {
				for (int i = 0; i < userCookies.length; i++) {
					if (userCookies[i].getName().equals("sheroUserId")) {
						try {
							shUserId = Long.parseLong(userCookies[i].getValue());
						} catch (Exception e) {
							shUserId = 0;
						}
					} else if (userCookies[i].getName().equals("sheroUserLnguage")) {
						shUserLanguage = userCookies[i].getValue();
					} else if (userCookies[i].getName().equals("sheroUserCountry")) {
						shUserCountry = userCookies[i].getValue();
						if (session.getAttribute("userCountry") == null)
							session.setAttribute("userCountry", shUserCountry);
					} else if (userCookies[i].getName().equals("sheroBrandUserId")) {
						try {
							shBrandUserId = Long.parseLong(userCookies[i].getValue());
						} catch (Exception e) {
							shBrandUserId = 0;
						}

					}
				}
			}

			if (session.getAttribute("socialshareUrl1") != null) {
				String url = "";
				url = (String) session.getAttribute("socialshareUrl1") + "/";
				if (url.indexOf("/ar/") > -1) {
					shUserLanguage = "ar";
				} else {
					shUserLanguage = "en";
				}
				session.setAttribute("userLanguage", shUserLanguage);
			}
		   	if(ajaxPageName.equals("check-user-login")) {
			    SHSurveyUser shSurveyUser = null;
			    if (session.getAttribute("shSurveyUser") != null) {
				    shSurveyUser = (SHSurveyUser) session.getAttribute("shSurveyUser");
				    shSurveyUser.setHttpServletRequest(request);
				    shSurveyUser.fetchVotedBrands((String) session.getAttribute("userCountry"));
			    } else if (shUserId > 10000) {
				    shSurveyUser = new SHSurveyUser(request, shUserLanguage, shUserId);
				    session.setAttribute("shSurveyUser", shSurveyUser);
			    }

			    if (request.getSession().getAttribute("shSurveyUser") == null)
				    shStatus = ((redirectItemType.equals("")) ? "user" : "survey") + "-logged-out";
			    else {
				    if (redirectItemType.equals("")) {
					    session.removeAttribute("newNotificationCount");
					    try {
						    Contact contact;
						    contact = new Contact(new String[]{shSurveyUser.getEmailAddress(), "servicehero"});
						    contact.setExtendedDetail("extra_field_10", "0");
						    contact.save();
					    } catch (Exception px) {
					    }
				    }
				    shStatus = ((redirectItemType.equals("")) ? "user" : "survey") + "-logged-in";
			    }
		    }   else if ( ajaxPageName.equals("dashboard-check-user-login")){
			    SHBrandUser shBrandUser = null;
                if (session.getAttribute("shBrandUser") != null) {
	                shBrandUser = (SHBrandUser) session.getAttribute("shBrandUser");
	                shBrandUser.setHttpServletRequest(request);

                } else if (shUserId > 10000) {
	                shBrandUser = new SHBrandUser(request, shUserLanguage, shBrandUserId);
                    session.setAttribute("shBrandUser", shBrandUser);
                }

                if (request.getSession().getAttribute("shBrandUser") == null)
                    shStatus = "user-logged-out";
                else {

                    shStatus = "user-logged-in||branddtl="+redirectSectorCode+"~"+redirectItemCode+"~"+redirectDealerCode;
                }
		    }
		} else if(ajaxPageName.equals("dashboard-user-check-login")) {
			if (request.getSession().getAttribute("shBrandUser") == null) {
				//shStatus = ((redirectItemType.equals("")) ? "user" : "dashboard") + "-logged-out";
				session.removeAttribute("shBrandUser");
				session.removeAttribute("dashboardSector");
				session.removeAttribute("dashboardBrand");
				session.removeAttribute("dashboardDealer");
				session.removeAttribute("brandShortTitle");
				session.removeAttribute("platform");
				session.removeAttribute("userAgent");
				session.removeAttribute("ipAddress");
				session.removeAttribute("ipAddressCountry");
				session.removeAttribute("campaignId");
				session.removeAttribute("userValue");
				session.removeAttribute("socialMedia");
				session.removeAttribute("socialMediaPicture");

				session.removeAttribute("activeFilters");
				session.removeAttribute("lastRefreshTime");
				Cookie objCookie = new Cookie("sheroBrandUserId", "0");
				objCookie.setDomain("www.servicehero.com");
				objCookie.setPath("/");
				objCookie.setComment("");
				objCookie.setMaxAge(0);
				response.addCookie(objCookie);
				session.setAttribute("pageName", pageName);
				session.invalidate();
				shStatus = "user-logout";
			}  else{
				shStatus = "user-logged-in";
			}

		}  else if(ajaxPageName.equals("dashboard-check-valid-brand")){
			int valid = 1;
			if(redirectItemType.equals("dashboard") && !redirectItemCode.equals("N/A")) {
				// itemcode is countrycode~sectorcode~brnadcode
				SHBrandUser shBrandUser = (SHBrandUser) request.getSession().getAttribute("shBrandUser");
				shBrandUser.setHttpServletRequest(request);
				if (request.getSession().getAttribute("shBrandUser") == null) {
				//shStatus = ((redirectItemType.equals("")) ? "user" : "dashboard") + "-logged-out";
					session.removeAttribute("shBrandUser");
					session.removeAttribute("dashboardSector");
					session.removeAttribute("dashboardBrand");
					session.removeAttribute("dashboardDealer");
					session.removeAttribute("brandShortTitle");
					session.removeAttribute("platform");
					session.removeAttribute("userAgent");
					session.removeAttribute("ipAddress");
					session.removeAttribute("ipAddressCountry");
					session.removeAttribute("campaignId");
					session.removeAttribute("userValue");
					session.removeAttribute("socialMedia");
					session.removeAttribute("socialMediaPicture");
					session.removeAttribute("activeFilters");
					session.removeAttribute("lastRefreshTime");
					Cookie objCookie = new Cookie("sheroBrandUserId", "0");
					objCookie.setDomain("www.servicehero.com");
					objCookie.setPath("/");
					objCookie.setComment("");
					objCookie.setMaxAge(0);
					response.addCookie(objCookie);
					session.setAttribute("pageName", pageName);
					session.invalidate();
					shStatus = "user-logout";
				}  else{
					valid = shBrandUser.checkUserHasBrandAccess(redirectItemCode);
				}

			}
			if(valid == 0){
				shStatus= "user-brand-valid";
			}else{
				if(shStatus.equals(""))
					shStatus= "user-brand-invalid";
			}

		} else if (ajaxPageName.equals("dashboard-user-validation")) {
			shStatus = brandUserValidation(request, response, languageName);
		} else if (ajaxPageName.equals("dashboard-user-logout")) {

			session.removeAttribute("shBrandUser");
			session.removeAttribute("dashboardSector");
			session.removeAttribute("dashboardBrand");
			session.removeAttribute("dashboardDealer");
			session.removeAttribute("brandShortTitle");
			session.removeAttribute("platform");
			session.removeAttribute("userAgent");
			session.removeAttribute("ipAddress");
			session.removeAttribute("ipAddressCountry");
			session.removeAttribute("campaignId");
			session.removeAttribute("userValue");
			session.removeAttribute("socialMedia");
			session.removeAttribute("socialMediaPicture");
			session.removeAttribute("activeFilters");
			session.removeAttribute("lastRefreshTime");
			Cookie objCookie = new Cookie("sheroBrandUserId", "0");
			objCookie.setDomain("www.servicehero.com");
			objCookie.setPath("/");
			objCookie.setComment("");
			objCookie.setMaxAge(0);
			response.addCookie(objCookie);
			session.setAttribute("pageName", pageName);
			session.invalidate();
			shStatus = "user-logout";
		} else if (ajaxPageName.equals("survey-user-validation")) {
			shStatus = surveyUserValidation(request, response, languageName);
		} else if (ajaxPageName.equals("user-logout")) {
			session.removeAttribute("shSurveyUser");
			session.removeAttribute("surveySector");
			session.removeAttribute("surveySearch");
			session.removeAttribute("surveyBrand");
			session.removeAttribute("mediaChannel");
			session.removeAttribute("surveyStartTime");
			session.removeAttribute("surveyBrandName");
			session.removeAttribute("brandShortTitle");
			session.removeAttribute("platform");
			session.removeAttribute("userAgent");
			session.removeAttribute("ipAddress");
			session.removeAttribute("ipAddressCountry");
			session.removeAttribute("campaignId");
			session.removeAttribute("userValue");
			session.removeAttribute("socialMedia");
			session.removeAttribute("socialMediaPicture");

			Cookie objCookie = new Cookie("sheroUserId", "0");
			objCookie.setDomain("www.servicehero.com");
			objCookie.setPath("/");
			objCookie.setComment("");
			objCookie.setMaxAge(0);
			response.addCookie(objCookie);
			session.setAttribute("pageName", pageName);
			session.invalidate();
			shStatus = "user-logout";
		} else if (ajaxPageName.equals("user-cookie-update")) {
			shStatus = userCookieUpdation(request);
		} else if (ajaxPageName.equals("user-mailing-subscribe")) {
			SHSurveyUser shSurveyUser = (SHSurveyUser) request.getSession().getAttribute("shSurveyUser");
			shSurveyUser.setHttpServletRequest(request);
			shStatus = SHSurveyUser.updateTridionMailingPreferences(request, shSurveyUser.getEmailAddress(), SHSurveyUser.TRIDION_SUBSCRIBE_MAILING_PAGE);
		} else if (ajaxPageName.equals("user-mailing-unsubscribe")) {
			SHSurveyUser shSurveyUser = (SHSurveyUser) request.getSession().getAttribute("shSurveyUser");
			shSurveyUser.setHttpServletRequest(request);
			shStatus = shSurveyUser.updateTridionMailingPreferences(request, shSurveyUser.getEmailAddress(), SHSurveyUser.TRIDION_UNSUBSCRIBE_MAILING_PAGE);
		} else if (ajaxPageName.equals("user-forgot-password")) {
			String emailAddress = (String) request.getSession().getAttribute("forgotPasswordEMail");
			if (emailAddress == null || emailAddress.length() == 0)
				emailAddress = "N/A";
			if (!emailAddress.equals("N/A"))
				shStatus = SHSurveyUser.updateTridionMailingPreferences(request, emailAddress, SHSurveyUser.TRIDION_RESET_PASSWORD_PAGE);
			else
				shStatus = "user-email-error";
			//request.getSession().removeAttribute("forgotPasswordEMail");
		} else if (ajaxPageName.equals("dashboard-user-forgot-password")) {
			String emailAddress = (String) request.getSession().getAttribute("resetBrandPasswordEMail");
			if (emailAddress == null || emailAddress.length() == 0)
				emailAddress = "N/A";
			if (!emailAddress.equals("N/A"))
				shStatus = SHBrandUser.updateTridionMailingPreferences(request, emailAddress, SHBrandUser.TRIDION_RESET_PASSWORD_PAGE);
			else
				shStatus = "user-email-error";
			//request.getSession().removeAttribute("forgotPasswordEMail");
		}else if (ajaxPageName.equals("user-password-update")) {
			shStatus = userPasswordUpdate(request, response);
		} else if (ajaxPageName.equals("dashboard-password-update")) {
			shStatus = dashboardPasswordUpdate(request, response);
		} else if (ajaxPageName.equals("user-profile-activate")) {
			shStatus = userActivatedUpdate(request, response);
		} else if (ajaxPageName.equals("user-profile-update") || ajaxPageName.equals("user-profile-complete") ) {
			shStatus = userProfileUpdate(request);
		} else if (ajaxPageName.equals("survey-submission")) {
			SurveySubmission surveySubmission = new SurveySubmission(request, languageName);
			shStatus = surveySubmission.submitSurvey();
		} else if (ajaxPageName.equals("survey-comments-count")) {
			shStatus = commentCountUpdate(request, languageName);
		} else if (ajaxPageName.equals("survey-comment-response")) {
			shStatus = commentResponseUpdate(request, languageName);
		} else if (ajaxPageName.equals("add-blog-comment")) {
			shStatus = addBlogComment(request);
		} else if (ajaxPageName.equals("add-blog-rating")) {
			shStatus = addBlogRating(request);
		} else if (ajaxPageName.equals("contact-us-submission")) {
			ContactUsSubmission contactUsSubmission = new ContactUsSubmission(request, languageName);
			shStatus = contactUsSubmission.submitContactUsQuery();
		} else if (ajaxPageName.equals("survey-comment-count")) {
			String commentReadsIdsInserted = (String) request.getSession().getAttribute("commentReadsIdsInserted");
			if(commentReadsIdsInserted == null || commentReadsIdsInserted.length() == 0)
				commentReadsIdsInserted = "";
			String commentIds = request.getParameter("commentId");
			if(commentIds == null || commentIds.length() == 0)
				commentIds = "";
			if(!commentIds.equals("") && commentReadsIdsInserted.indexOf(commentIds + ",") == -1) {
				SurveySubmission.setUserCommentCount(request, commentIds, SurveySubmission.COMMENT_COUNT_READ);
				commentReadsIdsInserted += commentIds + ",";
				request.getSession().setAttribute("commentReadsIdsInserted", commentReadsIdsInserted);
			}
		} else if (ajaxPageName.equals("fbWallPost")) {
			shStatus = updateSHFBWallPost(request);
		}
		/* addded for dashboard */
		else if (ajaxPageName.equals("dashboard-action-lock")) {
			shStatus = dashboardLockSurveyComment(request, response, pageName);
		} else if (ajaxPageName.equals("dashboard-comment-action")) {
			shStatus = dashboardSurveyCommentAction(request, response, pageName);
		} else if (ajaxPageName.equals("dashboard-refresh-action")) {
			shStatus = dashboardFetchLatestCommentList(request);
		}  else if(ajaxPageName.equals("select-governorate")) {
			shStatus = getProfileGovernorate(request) ;
		}
		if(ajaxPageName.startsWith("dashboard-") && !shStatus.equals("")){
			request.getSession().setAttribute("ajaxDashboardSubmission", shStatus);
		}  else if (!shStatus.equals("")){
			request.getSession().setAttribute("ajaxSubmission", shStatus);
		}


		final MvcData mvcData = page.getMvcData();
		LOG.trace("Page MvcData: {}", mvcData);

		return this.viewResolver.resolveView(mvcData, "Page", request);
		//return mvcData.getAreaName() + "/Page/" + mvcData.getViewName();
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

	/*private Page getPageModel(String path, Localization localization) {
		try {
			return contentProvider.getPageModel(path, localization);
		} catch (PageNotFoundException e) {
			LOG.error("Page not found: {}", path, e);
			throw new NotFoundException("Page not found: " + path, e);
		} catch (ContentProviderException e) {
			LOG.error("An unexpected error occurred", e);
			throw new InternalServerErrorException("An unexpected error occurred", e);
		} catch (Exception e) {
			//LOG.error(e.getMessage(), e);
			throw new InternalServerErrorException("Exception here ", e);
		}
	}*/

	private boolean isIncludeRequest(HttpServletRequest request) {
		return request.getAttribute(WebUtils.INCLUDE_REQUEST_URI_ATTRIBUTE) != null;
	}

	private String surveyUserValidation(HttpServletRequest request, HttpServletResponse response, String languageName) {
		String strTemp = "", shStatus = "", formType = "", emailAddress = "", password = "", retypePassword = "", redirectItemType = "";
		long redirectItemCode = 0;

		redirectItemType = request.getParameter("redirectItemType");
		if (redirectItemType == null || redirectItemType.length() == 0)
			redirectItemType = "N/A";
		strTemp = request.getParameter("redirectItemCode");
		if (strTemp == null || strTemp.length() == 0)
			redirectItemCode = 0;
		else {
			try {
				redirectItemCode = Long.parseLong(strTemp);
			} catch (Exception ex) {
				redirectItemCode = 0;
			}
		}
		if (redirectItemCode > 0) {
			if (redirectItemType.equals("brandCode")) {
				request.getSession().setAttribute("surveyBrand", String.valueOf(redirectItemCode));
				String sectorCode, strTempValue = "", strSrchText;
				HashMap<String, HashMap> objHM = new HashMap<String, HashMap>();
				HashMap<String, String> objHMSort = new HashMap<String, String>();
				HashMap<String, String> objHMIdTitle = new HashMap<String, String>();
				HashMap<String, String> objHMIdSector = new HashMap<String, String>();

				objHM = SHSearchService.searchBrands(request, BrandLuceneIndexer.INDEXER_SERACH_FIELD_BRAND_CODE, String.valueOf(redirectItemCode));
				objHMSort = (HashMap) objHM.get("sorter");
				objHMIdTitle = (HashMap) objHM.get("title");
				objHMIdSector = (HashMap) objHM.get("sectorId");

				List<String> titles = new ArrayList<String>(objHMSort.keySet());
				for (String title : titles) {
					strTempValue = objHMSort.get(title);
					request.getSession().setAttribute("surveySector", (String) objHMIdSector.get(strTempValue));
				}
			} else if (redirectItemType.equals("sectorCode")) {
				request.getSession().setAttribute("surveySector", String.valueOf(redirectItemCode));
			}
		}

		formType = request.getParameter("forgotPasswordURL");
		if (formType == null || formType.length() == 0)
			formType = "register";
		else
			formType = "login";

		emailAddress = request.getParameter(formType + "Email");
		if (emailAddress == null || emailAddress.length() == 0)
			emailAddress = "None";
		password = request.getParameter(formType + "Password");
		if (password == null || password.length() == 0)
			password = "None";
		//LOG.error("emailAddress: *" + emailAddress + "*  password: *" + password + "*");
		if (!emailAddress.equals("None") && !password.equals("None")) {
			shStatus = SHSurveyUser.validateUserForSurvey(request, response, languageName, emailAddress, password, formType);
		}
		return shStatus;
	}


	private String brandUserValidation(HttpServletRequest request, HttpServletResponse response, String languageName) {
		String strTemp = "", shStatus = "", formType = "", emailAddress = "", password = "", retypePassword = "", redirectItemType = "";

		/*long redirectItemCode = 0;
		redirectItemType = request.getParameter("redirectItemType");
		if (redirectItemType == null || redirectItemType.length() == 0)
			redirectItemType = "N/A";
		strTemp = request.getParameter("redirectItemCode");
		if (strTemp == null || strTemp.length() == 0)
			redirectItemCode = 0;
		else {
			try {
				redirectItemCode = Long.parseLong(strTemp);
			} catch (Exception ex) {
				redirectItemCode = 0;
			}
		}
		if (redirectItemCode > 0) {
			if (redirectItemType.equals("brandCode")) {
				request.getSession().setAttribute("surveyBrand", String.valueOf(redirectItemCode));
				String sectorCode, strTempValue = "", strSrchText;
				HashMap<String, HashMap> objHM = new HashMap<String, HashMap>();
				HashMap<String, String> objHMSort = new HashMap<String, String>();
				HashMap<String, String> objHMIdTitle = new HashMap<String, String>();
				HashMap<String, String> objHMIdSector = new HashMap<String, String>();

				objHM = SHSearchService.searchBrands(request, BrandLuceneIndexer.INDEXER_SERACH_FIELD_BRAND_CODE, String.valueOf(redirectItemCode));
				objHMSort = (HashMap) objHM.get("sorter");
				objHMIdTitle = (HashMap) objHM.get("title");
				objHMIdSector = (HashMap) objHM.get("sectorId");

				List<String> titles = new ArrayList<String>(objHMSort.keySet());
				for (String title : titles) {
					strTempValue = objHMSort.get(title);
					request.getSession().setAttribute("surveySector", (String) objHMIdSector.get(strTempValue));
				}
			} else if (redirectItemType.equals("sectorCode")) {
				request.getSession().setAttribute("surveySector", String.valueOf(redirectItemCode));
			}
		}*/

		formType = request.getParameter("forgotPasswordURL");
		if (formType == null || formType.length() == 0)
			formType = "register";
		else
			formType = "login";

		emailAddress = request.getParameter(formType + "Email");
		if (emailAddress == null || emailAddress.length() == 0)
			emailAddress = "None";
		password = request.getParameter(formType + "Password");
		if (password == null || password.length() == 0)
			password = "None";
		//LOG.error("emailAddress: *" + emailAddress + "*  password: *" + password + "*");
		if (!emailAddress.equals("None") && !password.equals("None")) {
			shStatus = SHBrandUser.validateUserForDashboard(request, response, languageName, emailAddress, password, formType);
		}
		return shStatus;
	}


	private String userCookieUpdation(HttpServletRequest request) {
		Cookie objCookie[] = request.getCookies();
		Cookie cookie;
		int result = 0;
		String shStatus = "", newLanguage = "", newCountry = "";
		for (int i = 0; i < objCookie.length; i++) {
			cookie = objCookie[i];
			if (cookie.getName().equals("sheroUserLanguage"))
				newLanguage = cookie.getValue();
			if (newLanguage == null || newLanguage.length() == 0)
				newLanguage = "en";
			if (cookie.getName().equals("sheroUserCountry"))
				newCountry = cookie.getValue();
			if (newCountry == null || newCountry.length() == 0)
				newCountry = "kw";
		}
		//LOG.error("userCookieUpdation: " + newLanguage + "  " + newCountry);
		if (!newLanguage.equals("") && !newCountry.equals("")) {
			request.getSession().setAttribute("userLanguage", newLanguage);
			request.getSession().setAttribute("userCountry", newCountry);

			SHSurveyUser shSurveyUser = (SHSurveyUser) request.getSession().getAttribute("shSurveyUser");
			if (shSurveyUser != null) {
				request.getSession().setAttribute("preferedLanguage", newLanguage);
				request.getSession().setAttribute("countryCode", newCountry);
				shSurveyUser.setHttpServletRequest(request);
				result = shSurveyUser.updateSHUserValues();
				shSurveyUser.setPreferedLanguage(newLanguage);
				shSurveyUser.setCountryCode(newCountry);
				request.getSession().setAttribute("shSurveyUser", shSurveyUser);
			} else
				result = 1;

			if (result > 0)
				shStatus = "location-set";
			else
				shStatus = "location-error";
		}
		return shStatus;
	}

	private String userPasswordUpdate(HttpServletRequest request, HttpServletResponse response) {
		String shStatus = "";
		int result = 0;
		String forgotEmail, newPassword, retypePassword, contactId;
		String languageName = (String) request.getSession().getAttribute("userLanguage");
		forgotEmail = request.getParameter("forgotEmail");
		if (forgotEmail == null || forgotEmail.length() == 0)
			forgotEmail = "None";
		newPassword = request.getParameter("newPassword");
		if (newPassword == null || newPassword.length() == 0)
			newPassword = "None";
		retypePassword = request.getParameter("retypePassword");
		if (retypePassword == null || retypePassword.length() == 0)
			retypePassword = "None";

		if (newPassword.equals(retypePassword) && forgotEmail.equals(request.getSession().getAttribute("forgotPasswordEMail"))) {
			request.getSession().removeAttribute("forgotPasswordEMail");
			shStatus = SHSurveyUser.validateUserForSurvey(request, response, languageName, forgotEmail, newPassword, "resetpass");
			if (shStatus.equals("user-valid-resetpass-match")) {
				String strSalt = PasswordEncryption.gensalt();
				request.getSession().setAttribute("encryptionSalt", strSalt);
				String strPass = PasswordEncryption.hashpw(newPassword, strSalt);
				request.getSession().setAttribute("password", strPass);
				SHSurveyUser shSurveyUser = (SHSurveyUser) request.getSession().getAttribute("shSurveyUser");
				shSurveyUser.setHttpServletRequest(request);
				result = shSurveyUser.updateSHUserValues();
				if (result > 0)
					shStatus = "password-set";
				else
					shStatus = "password-error";
			} else {
				shStatus = "password-error";
			}
		} else
			shStatus = "password-error";
		request.getSession().removeAttribute("shSurveyUser");
		return shStatus;
	}


	private String dashboardPasswordUpdate(HttpServletRequest request, HttpServletResponse response) {

		String shStatus = "";
		int result = 0;
		String forgotEmail, newPassword, retypePassword, contactId;
		String languageName = (String) request.getSession().getAttribute("userLanguage");
		forgotEmail = request.getParameter("forgotEmail");
		if (forgotEmail == null || forgotEmail.length() == 0)
			forgotEmail = "None";
		newPassword = request.getParameter("newPassword");
		if (newPassword == null || newPassword.length() == 0)
			newPassword = "None";
		retypePassword = request.getParameter("retypePassword");
		if (retypePassword == null || retypePassword.length() == 0)
			retypePassword = "None";

		if (newPassword.equals(retypePassword) && forgotEmail.equals(request.getSession().getAttribute("resetBrandPasswordEMail"))) {
			LOG.debug(" dashboardPasswordUpdate entered  and all equal ");
			request.getSession().removeAttribute("resetBrandPasswordEMail");
			shStatus = SHBrandUser.validateUserForDashboard(request, response, languageName, forgotEmail, newPassword, "resetpass");
			if (shStatus.equals("user-valid-resetpass-match")) {
				LOG.debug(" dashboardPasswordUpdate entered and user-valid-resetpass-match ");
				String strSalt = PasswordEncryption.gensalt();
				request.getSession().setAttribute("dashboardencryptionSalt", strSalt);
				String strPass = PasswordEncryption.hashpw(newPassword, strSalt);
				request.getSession().setAttribute("password", strPass);
				LOG.debug(" dashboardPasswordUpdate entered and dashboardencryptionSalt " + strSalt + " and password " + strPass);
				SHBrandUser shBrandUser = (SHBrandUser) request.getSession().getAttribute("shBrandUser");
				shBrandUser.setHttpServletRequest(request);
				result = shBrandUser.updateBrandUserValues();
				LOG.debug(" dashboardPasswordUpdate entered and result " + result);
				if (result > 0)
					shStatus = "password-set";
				else
					shStatus = "password-error";
			} else {
				shStatus = "password-error";
			}
		} else
			shStatus = "password-error";
		//request.getSession().removeAttribute("shBrandUser");
		return shStatus;
	}


	private String userActivatedUpdate(HttpServletRequest request, HttpServletResponse response) {
		String shStatus = "";
		int result = 0;
		String activateEmail, contactId;
		String languageName = (String) request.getSession().getAttribute("userLanguage");
		activateEmail = request.getParameter("activateEmail");
		LOG.debug("activateEmail " + activateEmail);
		if (activateEmail == null || activateEmail.length() == 0) {
			com.tridion.marketingsolution.profile.Contact contact;
			try {
				contact = new com.tridion.marketingsolution.profile.Contact(request.getParameter("p"));
				activateEmail = contact.getExtendedDetail("mail");
			} catch (Exception ex) {
				activateEmail = "None";
			}
		}

		if (!activateEmail.equals("None")) {

			request.getSession().removeAttribute("activateEMail");
			shStatus = SHSurveyUser.validateUserForSurvey(request, response, languageName, activateEmail, "", "useractivate");
			LOG.debug("activateEmail " + shStatus);
			if (shStatus.equals("user-valid-useractivate-match")) {
				SHSurveyUser shSurveyUser = (SHSurveyUser) request.getSession().getAttribute("shSurveyUser");
				shSurveyUser.setHttpServletRequest(request);
				result = shSurveyUser.activateUser(request, shSurveyUser.getId());
				if (result > 0)
					shStatus = "activate-set";
				else
					shStatus = "activate-error";
			} else {
				shStatus = "activate-error";
			}
		} else
			shStatus = "activate-error";
		LOG.debug("activateEmail " + shStatus);
		request.getSession().removeAttribute("shSurveyUser");
		return shStatus;
	}


	private String userProfileUpdate(HttpServletRequest request) {
		String shStatus = "", fieldNane = "", fieldValue = "";
		int result = 0;
		fieldNane = request.getParameter("fieldName");
		if (fieldNane == null || fieldNane.length() == 0)
			fieldNane = "None";
		fieldValue = request.getParameter("fieldValue");
		if (fieldValue == null || fieldValue.length() == 0)
			fieldValue = "None";
		if (!fieldNane.equals("None") && !fieldValue.equals("None")) {
			if (fieldNane.indexOf("mobileNumber") > -1) {
				request.getSession().setAttribute("mobileNumber", fieldValue);
			} else {
				request.getSession().setAttribute(fieldNane, fieldValue);
			}
			SHSurveyUser shSurveyUser = (SHSurveyUser) request.getSession().getAttribute("shSurveyUser");
			shSurveyUser.setHttpServletRequest(request);
			LOG.debug("Entered userProfileUpdate with fieldNane " + fieldNane);
			if (fieldNane.equals("personName") || fieldNane.indexOf("mobileNumber") > -1 || fieldNane.equals("preferedLanguage") || fieldNane.equals("civilId") || fieldNane.equals("gender") || fieldNane.equals("ageGroup") ||
			    fieldNane.equals("dateOfBirth") || fieldNane.equals("nationality") || fieldNane.equals("residence") || fieldNane.equals("governorate") || fieldNane.equals("education")) {
				result = shSurveyUser.updateSHUserValues();
				LOG.debug("after updateSHUserValues result is  " + result);
				if (fieldNane.equals("personName") && result != -3)
					shSurveyUser.setPersonName(fieldValue);
				if (fieldNane.equals("preferedLanguage") && result != -1)
					shSurveyUser.setPreferedLanguage(fieldValue);
				if (fieldNane.indexOf("mobileNumber") > -1 && result != -4)
					shSurveyUser.setMobileNumber(fieldValue);
				if (fieldNane.equals("civilId"))
					shSurveyUser.setCivilId(Long.parseLong(fieldValue));
				if (fieldNane.equals("gender") && result != -5)
					shSurveyUser.setGender(fieldValue);
				if (fieldNane.equals("ageGroup") && result != -6)
					shSurveyUser.setAgeGroup(fieldValue);
				if (fieldNane.equals("dateOfBirth") && !(result == -7 || result == -8)) {
					String strTemp = fieldValue.substring(6) + "" + fieldValue.substring(3, 5) + "" + fieldValue.substring(0, 2);
					shSurveyUser.setDateOfBirth(Long.parseLong(strTemp));
				}
				if (fieldNane.equals("nationality") && result != -9)
					shSurveyUser.setNationality(fieldValue);
				if (fieldNane.equals("residence") && result != -10)
					shSurveyUser.setResidence(fieldValue);
				if (fieldNane.equals("governorate") && result != -11)
					shSurveyUser.setGovernorate(fieldValue);
				if (fieldNane.equals("education") && result != -12)
					shSurveyUser.setEducation(fieldValue);
			}

			if (fieldNane.equals("numberOfVotes") || fieldNane.equals("votingAverage") || fieldNane.equals("contactMe") || fieldNane.equals("offersMe") || fieldNane.equals("socialMediaWallPost")
			    || fieldNane.equals("interestedStaff") || fieldNane.equals("interestedLocation") || fieldNane.equals("interestedValue")
			    || fieldNane.equals("interestedQuality") || fieldNane.equals("interestedSpeed") || fieldNane.equals("interestedReliability") || fieldNane.equals("interestedCallCenter")
			    || fieldNane.equals("interestedWebsite") || fieldNane.equals("interestedOverallSatisfaction") || fieldNane.equals("interestedRecommendation") || fieldNane.equals("interestedIdealoffering")) {
				result = shSurveyUser.updateSHUserExtraValues();
				if (fieldNane.equals("numberOfVotes"))
					shSurveyUser.setNumberOfVotes(Integer.parseInt(fieldValue));
				if (fieldNane.equals("votingAverage"))
					shSurveyUser.setVotingAverage(Float.parseFloat(fieldValue));
				if (fieldNane.equals("contactMe"))
					shSurveyUser.setContactMe(fieldValue);
				if (fieldNane.equals("offersMe"))
					shSurveyUser.setOffersMe(fieldValue);
				if (fieldNane.equals("socialMediaWallPost"))
					shSurveyUser.setSocialMediaWallPost(fieldValue);
				if (fieldNane.equals("interestedStaff"))
					shSurveyUser.setInterestedStaff(fieldValue);
				if (fieldNane.equals("interestedLocation"))
					shSurveyUser.setInterestedLocation(fieldValue);
				if (fieldNane.equals("interestedValue"))
					shSurveyUser.setInterestedValue(fieldValue);
				if (fieldNane.equals("interestedQuality"))
					shSurveyUser.setInterestedQuality(fieldValue);
				if (fieldNane.equals("interestedSpeed"))
					shSurveyUser.setInterestedSpeed(fieldValue);
				if (fieldNane.equals("interestedReliability"))
					shSurveyUser.setInterestedReliability(fieldValue);
				if (fieldNane.equals("interestedCallCenter"))
					shSurveyUser.setInterestedCallCenter(fieldValue);
				if (fieldNane.equals("interestedWebsite"))
					shSurveyUser.setInterestedWebsite(fieldValue);
				if (fieldNane.equals("interestedOverAllSatisfaction"))
					shSurveyUser.setInterestedOverallSatisfaction(fieldValue);
				if (fieldNane.equals("interestedIdealoffering"))
					shSurveyUser.setInterestedIdealoffering(fieldValue);
			}
			request.getSession().setAttribute("shSurveyUser", shSurveyUser);
			if (result > 0) {
				LOG.debug("fieldNane: " + fieldNane + " fieldValue: " + fieldValue);
				if (fieldNane.equals("residence")  ) {
					if (fieldValue.equals("35367")   || fieldValue.equals("35368") )  {
						LOG.debug("calling getProfileGovernorate ");
						shStatus = getProfileGovernorate(request);
						LOG.debug(" shStatus " + shStatus);
					}else {
						shStatus = "governorate-set~" ;
					}

				}  else {
					shStatus = "profile-set";
				}

			}else if (result == -1)
				shStatus = "profile-preferedLanguage-error";
			else if (result == -2)
				shStatus = "profile-countryCode-error";
			else if (result == -3)
				shStatus = "profile-personName-error";
			else if (result == -4)
				shStatus = "profile-mobileNumber-error";
			else if (result == -5)
				shStatus = "profile-gender-error";
			else if (result == -6)
				shStatus = "profile-ageGroup-error";
			else if (result == -7)
				shStatus = "profile-dateOfBirth-range-error";
			else if (result == -8)
				shStatus = "profile-dateOfBirth-error";
			else if (result == -9)
				shStatus = "profile-nationality-error";
			else if (result == -10)
				shStatus = "profile-residence-error";
			else if (result == -11)
				shStatus = "profile-governorate-error";
			else if (result == -12)
				shStatus = "profile-education-error";
			else
				shStatus = "profile-error";
		}
		return shStatus;
	}


	private String commentCountUpdate(HttpServletRequest request, String languageName) {
		String shStatus = "", fieldNane = "", fieldValue = "";
		int result = 0;
		fieldNane = request.getParameter("fieldName");
		if (fieldNane == null || fieldNane.length() == 0)
			fieldNane = "None";
		fieldValue = request.getParameter("fieldValue");
		if (fieldValue == null || fieldValue.length() == 0)
			fieldValue = "None";
		if (!fieldNane.equals("None") && !fieldValue.equals("None")) {
			SurveySubmission surveySubmission = new SurveySubmission(request, languageName);
			request.getSession().setAttribute(fieldNane, fieldValue);
			result = surveySubmission.updateSurveyCommentsValues();
		}
		if (result > 0)
			shStatus = "comment-count-set";
		else
			shStatus = "comment-count-error";
		return shStatus;

	}
	private String commentResponseUpdate (HttpServletRequest request, String languageName) {
		String shStatus = "", fieldNane = "", fieldValue = "", parentCommentid = "", helpfulStatus = "",helpfulStatusDesc = "", isResponsePrivate = "",
			   brandMap, sectorcode = "0", brandCode = "0", countrycode = "0", dealercode = "0", useractivity = "", updateColumn = "", originalId = "";
		long commentId=0;
		int result = 0;
		StringBuffer objResponseDiv = new StringBuffer();
		String strLineBreak = "\n";
		String surveyQuery = "";
		Connection connection = null;
		try{
			SHSurveyUser shSurveyUser = (SHSurveyUser) request.getSession().getAttribute("shSurveyUser");
			shSurveyUser.setHttpServletRequest(request);
			parentCommentid = request.getParameter("extraId");
			originalId = request.getParameter("originalId");
			brandMap = request.getParameter("brandMap");
			fieldNane = request.getParameter("fieldName");
			if (fieldNane == null || fieldNane.length() == 0)
				fieldNane = "None";
			fieldValue = request.getParameter("fieldValue");
			if (fieldValue == null || fieldValue.length() == 0)
				fieldValue = "None";
			if (!fieldNane.equals("None") && !fieldValue.equals("None")) {
				PreparedStatement ps;
				ApplicationContext context = RequestContextUtils.getWebApplicationContext(request);
				PoolingDataSource<PoolableConnection> dataSource = (PoolingDataSource<PoolableConnection>) context.getBean("dataSource");
				connection = dataSource.getConnection();
				// thisis response by consumer
				isResponsePrivate = request.getParameter("fChkBox");
				LOG.debug("commentResponseUpdate debug points ");
				LOG.debug("fieldNane " + fieldNane);
				LOG.debug("parentCommentid " + parentCommentid);
				LOG.debug("originalId" + originalId);
				LOG.debug("shuserid" + shSurveyUser.getId());
				LOG.debug("isResponsePrivate" + isResponsePrivate);
				LOG.debug("brandMap" + brandMap);
				if (fieldNane.indexOf("textareareply") > -1) {
					/*get the countrycode, sectorcode, brandcode, dealercode from session */
					StringTokenizer objSt1;
					if(brandMap != null && brandMap.indexOf("~") > -1) {
						objSt1 = new java.util.StringTokenizer(brandMap, "~");
						countrycode =   objSt1.nextToken();
						sectorcode =   objSt1.nextToken();
						brandCode =   objSt1.nextToken();
						dealercode =   objSt1.nextToken();
					}  else{
						shStatus = "comment-response-error";
						throw new Exception("Brand Map is empty in this request for user " +shSurveyUser.getId());
					}

					if (!countrycode.equals("0") && !sectorcode.equals("0") && !brandCode.equals("0")) {
						isResponsePrivate = request.getParameter("fChkBox");
						if (isResponsePrivate == null || isResponsePrivate.length() == 0)
							isResponsePrivate = "";
						ps = connection.prepareStatement("Select surveyComments_Seq.nextVal from dual");
						ResultSet objRs = ps.executeQuery();
						if (objRs.next())
							commentId = objRs.getLong(1);
						objRs.close();
						ps.close();
						if (commentId < 10001)
							commentId = 10001;
						ps = connection.prepareStatement("insert into surveyComments (ID, PARENTCOMMENTID,  SHUSERID, LANGUAGECODE, COUNTRYCODE, SECTORCODE, BRANDCODE, DEALERCODE, ORIGINALCOMMENT, COMMENTSTATUS, COMMENTAPPROVED, ROOTPARENTID) values(?,?,?,?, ?, ?, ?,?,?, ?, ?,?)");
						ps.setLong(1, commentId);
						ps.setLong(2, Long.parseLong(parentCommentid));
						ps.setLong(3, shSurveyUser.getId());
						ps.setString(4, shSurveyUser.getPreferedLanguage());
						ps.setString(5, countrycode);
						ps.setString(6, sectorcode);
						ps.setString(7, brandCode);
						ps.setString(8, dealercode);
						ps.setString(9, fieldValue);
						if (isResponsePrivate.toString().equals("true")) {
							ps.setString(10, "29661");
						} else {
							ps.setString(10, "");
						}
						ps.setString(11, "X");
						ps.setLong(12, Long.parseLong(originalId));
						LOG.debug(" SURVEYCOMMENTS response ");
						ps.executeUpdate();
						/* moved updating SURVEYCOMMENTS_SUMMARY to admin page */
						ps.close();


					}
					UserActivityLog userLog = new UserActivityLog();
					userLog.insertRecord(request, connection, UserActivityLog.ACTIVITY_SURVEY_TABLE, UserActivityLog.ACTIVITY_COMMENT, shSurveyUser.getId(), commentId, "");
					DateTime dt = new DateTime(System.currentTimeMillis());
					DateTimeFormatter fmt = DateTimeFormat.forPattern("dd.MM.yyyy");

					objResponseDiv.append("comment-response-set").append("~").append("<div class=\"brand-response\"><h4 >").append(strLineBreak);
					if (isResponsePrivate.toString().equals("true")) {
						objResponseDiv.append(" <span class=\"private\" > PRIVATE </span > ").append(strLineBreak);
					}
					objResponseDiv.append( fieldValue).append("</h4>").append(strLineBreak);
					objResponseDiv.append("<p class=\"clearfix\"> <span class=\"likes\"><tri:resource key=\"core.UserProfileUnderReview\" /></span> <span class=\"date\">").append(fmt.print(dt) + "</span> </p>").append(strLineBreak);
					objResponseDiv.append("</div>").append(strLineBreak) ;

					result = 1;
					shStatus =   objResponseDiv.toString();
				} else if (fieldNane.indexOf("helpfulComment") > -1) {
					// COMMENT_BRAND_STATUS_HELPFULL   - 26517
					// COMMENT_BRAND_STATUS_NOTHELPFULL - 26518
					if (fieldValue.equals("true")) {
						helpfulStatus = "26517";
						helpfulStatusDesc = "Helpful response";
						useractivity =    UserActivityLog.ACTIVITY_COMMENT_HELPFUL;
						updateColumn =  "UPDATE SURVEYCOMMENTS_SUMMARY  set HELPFUL = 'Y' where id = ?"  ;


					} else {
						helpfulStatus = "26518";
						helpfulStatusDesc = "Not helpful response";
						useractivity =    UserActivityLog.ACTIVITY_COMMENT_NOT_HELPFUL;
						updateColumn =  "UPDATE SURVEYCOMMENTS_SUMMARY  set NOTHELPFUL = 'Y' where id = ?"  ;
					}

					ps = connection.prepareStatement("UPDATE SURVEYCOMMENTS  set BRANDRESPONSESTATUS = '" + helpfulStatus + "' where id = ?");
					ps.setLong(1, Long.parseLong(parentCommentid));       // in this case this is the brandresponse id
					result = ps.executeUpdate();

					ps = connection.prepareStatement(updateColumn);
					ps.setLong(1, Long.parseLong(originalId));       // in this case this is the root parent id
					result = ps.executeUpdate();
					ps.close();
					UserActivityLog userLog = new UserActivityLog();
					userLog.insertRecord(request, connection, UserActivityLog.ACTIVITY_SURVEY_TABLE, useractivity, shSurveyUser.getId(), Long.parseLong(parentCommentid), "");


					objResponseDiv.append("comment-response-helpful-set").append("~").append("<span class=\"likes\">"+helpfulStatusDesc+"</span>");
					result = 1;
					shStatus =   objResponseDiv.toString();
				}



			}
			if (result > 0)
				shStatus = shStatus;
			else
				shStatus = "comment-response-error";

		} catch(SQLException ex) {
			LOG.error(ex.getMessage(),ex);
		} catch(Exception e) {
			LOG.error(e.getMessage(),e);
		}finally {
			Utils.closeQuietly(connection);
		}
		return shStatus;

	}

	private String addBlogComment(HttpServletRequest request) {
		String shStatus = "";
		String comment = "", parentBlogid = "";
		comment = request.getParameter("comment");
		if (comment == null || comment.length() == 0)
			comment = "None";
		parentBlogid = request.getParameter("parentblogid");
		if (parentBlogid == null || parentBlogid.length() == 0)
			parentBlogid = "None";
		if (!comment.equals("None") && !parentBlogid.equals("None")) {
			BlogComments comments = new BlogComments();
			comments.setBlogid(Long.parseLong(request.getSession().getAttribute("blogEntityId").toString()));
			DateTime dt = new DateTime(DateTimeZone.forID("Asia/Riyadh"));
			comments.setCommentDate(dt);
			comments.setCommentid(3);
			comments.setComment(comment);
			comments.setParentcommentid(Integer.parseInt(parentBlogid));
			SHSurveyUser shSurveyUser = (SHSurveyUser) request.getSession().getAttribute("shSurveyUser");
			shSurveyUser.setHttpServletRequest(request);
			comments.setCreatorid(shSurveyUser.getId());
			try {
				BlogActionImpl.insertCommentData(request, comments);
			} catch (Exception exception) {
				shStatus = "comment-error";
			}
			shStatus = "comment-inserted";
		}
		return shStatus;
	}

	private String addBlogRating(HttpServletRequest request) {

		String shStatus = "";
		String ratingScore = "";
		ratingScore = request.getParameter("score");
		LOG.debug("ratingScore " + ratingScore);
		String blogid = request.getSession().getAttribute("blogEntityId").toString();
		if (ratingScore == null || ratingScore.length() == 0)
			ratingScore = "None";
		if (!ratingScore.equals("None")) {
			try {
				BlogActionImpl.insertRatingData(request, ratingScore, blogid);
			} catch (Exception exception) {
				shStatus = "rating-error";
			}
			shStatus = "rating-inserted";
		}
		return shStatus;
	}

	private String updateSHFBWallPost(HttpServletRequest request) {
		String shStatus = "", fieldNane = "", fieldValue = "";
		int result = 0;
		/* read this  ajaxPageName: 'fbWallPost', shuserid: '${shSurveyUser.id}', postId: response.post_id, posted: 'Y', uniqueId:uniqueid, mode:mode*/
		{
			String shuserid = "";
			shuserid = request.getParameter("shuserid");
			LOG.debug("shuserid from fbwallpost" + shuserid);
			String postId = request.getParameter("postId");
			String isPosted = request.getParameter("posted");
			String uniqueId = request.getParameter("uniqueId");
			String mode = request.getParameter("mode");
			if (uniqueId != null) {
				long iSHUserId = 0;
				SHSurveyUser shSurveyUser = null;

				if (request.getSession().getAttribute("shSurveyUser") != null) {
					shSurveyUser = (SHSurveyUser) request.getSession().getAttribute("shSurveyUser");
					iSHUserId = shSurveyUser.getId();
				}
				PreparedStatement ps;
				ApplicationContext context = RequestContextUtils.getWebApplicationContext(request);
				PoolingDataSource<PoolableConnection> dataSource = (PoolingDataSource<PoolableConnection>) context.getBean("dataSource");
				Connection connection = null;
				try {
					if (iSHUserId > 10000) {
						connection = dataSource.getConnection();

						ps = connection.prepareStatement("INSERT INTO SHFBWALLPOST ( SHEROID, UNIQUEID, POSTID, ISPOSTED, POSTMODE, POSTTIME, POSTDATE) values(?,?,?,?,?,?,?)");
						ps.setLong(1, iSHUserId);
						ps.setString(2, uniqueId);
						ps.setString(3, postId);
						ps.setString(4, isPosted);
						ps.setString(5, mode);
						ps.setLong(6, System.currentTimeMillis());
						java.util.Date today = new java.util.Date();
						ps.setLong(7, today.getTime());
						ps.executeUpdate();
						ps.close();
						connection.commit();
						shStatus = "shfbwallpost-set";
						UserActivityLog userLog = new UserActivityLog();
						userLog.insertRecord(request, connection, UserActivityLog.ACTIVITY_SURVEY_TABLE, UserActivityLog.ACTIVITY_FBWALLPOST, shSurveyUser.getId(), Long.getLong(postId), "");


					}
				} catch (Exception e) {
					shStatus = "shfbwallpost-error";

					LOG.error(e.getMessage(), e);
				} finally {
					Utils.closeQuietly(connection);

				}
			}

		}
		return shStatus;
	}

	public String getProfileGovernorate(HttpServletRequest request)      {
		String govcountryCode = ":";
		int pubId = 8;
        String userLanguage = request.getParameter("userLanguage");
		if(((String)userLanguage + "/").indexOf("ar/") > -1)
            pubId = 10;
        else
            pubId = 9;
		String countryOfResidence  = request.getParameter("fieldValue");
		String strLineBreak = "\n";
		StringBuffer objSb = new StringBuffer();
		TaxonomyComparator objComp = new TaxonomyComparator();
		String key;
		Keyword objKey;
		KeyValuePair keyValuePair;
		List<KeyValuePair> KeyValuePairList;
		TaxonomyFactory objTaxonomy = new TaxonomyFactory();
		List<Keyword> objKeyList = objTaxonomy.getTaxonomyKeywords("tcm:" + pubId + "-160-512").getKeywordChildren();
        Collections.sort(objKeyList, objComp);
        Iterator iterator = objKeyList.listIterator();
		String strReturn = "governorate-set";
		try{
	        while(iterator.hasNext()) {
		        objKey = (Keyword) iterator.next();
		        if (countryOfResidence.equals("35367")) {    //35144
			        govcountryCode = "kw";

		        } else if (countryOfResidence.equals("35368")) {
			        govcountryCode = "ae";

		        }
		        if (objKey.getKeywordName().indexOf(govcountryCode) > -1) {
			        objSb.append("<select id=\"governorate\" name=\"governorate\" class=\"edit-picker bs-select-hidden\" data-style=\"btn-profile\">").append(strLineBreak);

			        keyValuePair = new KeyValuePair();
			        key = objKey.getKeywordKey();
			        List<Keyword> objChildKeyList = objKey.getKeywordChildren();
			        KeyValuePairList = new ArrayList<KeyValuePair>();
			        Collections.sort(objChildKeyList, objComp);
			        Iterator childIterator = objChildKeyList.listIterator();
			        while (childIterator.hasNext()) {
				        objKey = (Keyword) childIterator.next();
				        if(objKey.getKeywordName().indexOf("00 - Not Specified") > -1){
					        objSb.append("<option value=\"0\" selected>").append(objKey.getKeywordDescription()).append("</option>").append(strLineBreak);
				        }
				        if (objKey.getKeywordName().indexOf("00 - Not Specified") == -1) {
					        keyValuePair = new KeyValuePair();
					        key = objKey.getKeywordURI();
					        objSb.append("<option value=\"").append(key.substring(key.indexOf("-") + 1, key.lastIndexOf("-"))).append("\">").append(objKey.getKeywordDescription()).append("</option>").append(strLineBreak);
				        }
			        }
			        objSb.append("</Select>").append(strLineBreak);

		        }

	        }
			strReturn = strReturn+"~"+objSb.toString();
        }catch (Exception e)   {
            System.out.println("Exception @get  governorate "+e.getMessage());
            e.printStackTrace();
			strReturn =  "governorate-error"   ;
        }

        return strReturn;


	}

	/**
	 * Gets a page requested by a client. This is the main handler method which gets called when a client sends a
	 * request for a page.
	 *
	 * @param request The request.
	 * @return The view name of the page.
	 */
     /*@RequestMapping(method = RequestMethod.GET, value = {"/f/{shortcutName}",
    													"/g/{shortcutName}",
    													"/l/{shortcutName}",
    													"/t/{shortcutName}",
    													"/i/{shortcutName}",
    													"/s/{shortcutName}",
    													"/q/{shortcutName}",
    													"/e/{shortcutName}"}, produces = {MediaType.TEXT_HTML_VALUE, MediaType.ALL_VALUE})*/
	@RequestMapping(method = RequestMethod.GET, value = {"/{shortcutName}",
	                                                     "/f/{shortcutName}",
	                                                     "/g/{shortcutName}",
	                                                     "/l/{shortcutName}",
	                                                     "/t/{shortcutName}",
	                                                     "/i/{shortcutName}",
	                                                     "/s/{shortcutName}",
	                                                     "/q/{shortcutName}",
	                                                     "/e/{shortcutName}"}, produces = {MediaType.TEXT_HTML_VALUE, MediaType.ALL_VALUE})
	public String handleBrandShortcut(@PathVariable Map<String, String> pathVariables, HttpServletRequest request, HttpServletResponse response) {
		String shortcutName = pathVariables.get("shortcutName");
		HttpSession session = request.getSession();
		final String requestPath = webRequestContext.getRequestPath();
		LOG.debug("handleBrandShortcut: pageName={}" + "  " + requestPath, shortcutName);
		final Localization localization = webRequestContext.getLocalization();
		Page page = null;
		try {
			page = getPageModel(requestPath, localization);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
		if (!isIncludeRequest(request)) {
			request.setAttribute(PAGE_ID, page.getId());
		}
		request.setAttribute(PAGE_MODEL, page);
		request.setAttribute(LOCALIZATION, localization);
		request.setAttribute(MARKUP, markup);
		request.setAttribute(SCREEN_WIDTH, mediaHelper.getScreenWidth());


		//LOG.error("1: shortcutName: " + shortcutName);
		if (!shortcutName.equals("en") && !shortcutName.equals("ar")) {
			//LOG.error("2: shortcutName: " + shortcutName);
			/*String channel = "", strTempValue = "";
			if (requestPath.indexOf("/" + shortcutName) > -1)
				channel = "direct-link";
			else if (requestPath.indexOf("/f/") > -1)
				channel = "facebook";
			else if (requestPath.indexOf("/g/") > -1)
				channel = "google-plus";
			else if (requestPath.indexOf("/l/") > -1)
				channel = "linked-in";
			else if (requestPath.indexOf("/t/") > -1)
				channel = "twitter";
			else if (requestPath.indexOf("/i/") > -1)
				channel = "instagram";
			else if (requestPath.indexOf("/s/") > -1)
				channel = "sms";
			else if (requestPath.indexOf("/e/") > -1)
				channel = "email";
			else if (requestPath.indexOf("/q/") > -1)
				channel = "qr-code";*/

			UserActivityLog.getIPLocation(request);

			request.getSession().setAttribute("shortcutPage", "yes");

			/*if (((String) request.getSession().getAttribute("ipAddressCountry")).toLowerCase().equals("kuwait") )
				request.getSession().setAttribute("userCountry", "kw");

			if (request.getSession().getAttribute("userLanguage") == null)
				request.getSession().setAttribute("userLanguage", "en");*/

		    /*HashMap<String,HashMap> objHM = new HashMap<String,HashMap>();
			HashMap<String,String> objHMSort = new HashMap<String,String>();
			HashMap<String,String> objHMIdTitle = new HashMap<String,String>();
			HashMap<String,String> objHMIdSector = new HashMap<String,String>();
	        HashMap<String,String> objHMIdDealers = new HashMap<String,String>();
	        HashMap<String,String> objHMIdShortcutName = new HashMap<String,String>();

	        objHM = SHSearchService.searchBrands(request, BrandLuceneIndexer.INDEXER_SERACH_FIELD_SHORTCUT_NAME, shortcutName, "brandid");
	        objHMSort = (HashMap) objHM.get("sorter");
	        objHMIdTitle = (HashMap) objHM.get("title");
	        objHMIdSector = (HashMap) objHM.get("sectorId");
	        objHMIdDealers = (HashMap) objHM.get("dealers");
	        objHMIdShortcutName = (HashMap) objHM.get("shortcutName");
	        if(objHMSort.size() == 1) {
	        	List<String> sorterList = new ArrayList<String>(objHMSort.keySet());
	            for (String sortValue : sorterList) {
	                strTempValue = objHMSort.get(sortValue);
	                request.getSession().setAttribute("surveySector", objHMIdSector.get(strTempValue));
	            	request.getSession().setAttribute("surveyBrand", strTempValue);
	            }
	        } else {
	        	objHM = SHSearchService.searchBrands(request, BrandLuceneIndexer.INDEXER_SERACH_FIELD_TITLE, shortcutName, "brandid");
	            objHMSort = (HashMap) objHM.get("sorter");
	            objHMIdTitle = (HashMap) objHM.get("title");
	            objHMIdSector = (HashMap) objHM.get("sectorId");
	            objHMIdDealers = (HashMap) objHM.get("dealers");
	            objHMIdShortcutName = (HashMap) objHM.get("shortcutName");
	            if(objHMSort.size() > 1) {
	            	request.getSession().setAttribute("surveySearch", shortcutName);
	            } else if(objHMSort.size() == 1) {
	            	List<String> sorterList = new ArrayList<String>(objHMSort.keySet());
		            for (String sortValue : sorterList) {
		                strTempValue = objHMSort.get(sortValue);
		                request.getSession().setAttribute("surveySector", objHMIdSector.get(strTempValue));
		            	request.getSession().setAttribute("surveyBrand", strTempValue);
		            }
	            } else {

	            }
	        }*/

			//request.getSession().setAttribute("campaignId", channel + "-" + shortcutName);
		}

		final MvcData mvcData = page.getMvcData();
		LOG.trace("Page MvcData: {}", mvcData);

		return this.viewResolver.resolveView(mvcData, "Page", request);
		//return mvcData.getAreaName() + "/Page/" + mvcData.getViewName();
	}

	/* brand user has started using this so update commentid with lock status */
	public String dashboardLockSurveyComment(HttpServletRequest request, HttpServletResponse response, String pageName) {
		String commentid = "", lockstatus = "", commentLockStatus = "", activityLockstatus = "", brandMap = "", countrycode = "", sectorcode= "", brandCode= "", dealercode = "";
		long lbrandDashoardUser = 10000;
		int result = 0, valid = 1;
		commentid = request.getParameter("actioncommentid");
		lockstatus = request.getParameter("lockstatus");
		PreparedStatement ps;
		ApplicationContext context = RequestContextUtils.getWebApplicationContext(request);
		PoolingDataSource<PoolableConnection> dataSource = (PoolingDataSource<PoolableConnection>) context.getBean("dataSource");
		Connection connection = null;
		try {
			/*check if user has access */
			brandMap = request.getParameter("brandMap") ;
			SHBrandUser shBrandUser = (SHBrandUser)request.getSession().getAttribute("shBrandUser");
			if (request.getSession().getAttribute("shBrandUser") == null) {
				commentLockStatus = "comment-activity-user-logout";
				dashboardInvalidateSession(request,  response, pageName) ;
				throw new Exception("No Brand User in session while performing lockstatus " +lockstatus);
			}
			lbrandDashoardUser =  shBrandUser.getShUserId();
			if (lbrandDashoardUser < 10001) {
				commentLockStatus = "comment-activity-user-logout";
				dashboardInvalidateSession(request,  response, pageName) ;
				throw new Exception("Invalid Brand User id "+lbrandDashoardUser +" in session while performing lockstatus " +lockstatus);
			}
			LOG.debug("dashboardLockSurveyComment debug points ");
			LOG.debug("commentid " + commentid);
			LOG.debug("lockstatus " + lockstatus);
			LOG.debug("lbrandDashoardUser" + lbrandDashoardUser);
			LOG.debug("brandMap" + brandMap);

			/* check if brand user has access to brand */
			StringTokenizer objSt1;
			/* get the countrycode, */
			if(brandMap.indexOf("~") > -1) {
				objSt1 = new java.util.StringTokenizer(brandMap, "~");
				countrycode =   objSt1.nextToken();
				sectorcode =   objSt1.nextToken();
				brandCode =   objSt1.nextToken();
				dealercode =   objSt1.nextToken();
			}

			if (brandCode == null  || brandCode.length()== 0 || sectorcode  == null  || sectorcode.length()== 0 ) {
				commentLockStatus = "comment-activity-error";
				dashboardInvalidateSession(request,  response, pageName) ;
				throw new Exception("Brand Map is empty in this request for user " +lbrandDashoardUser);
			}
			valid = shBrandUser.checkUserHasBrandAccess(brandMap);
			if(valid != 0){
				commentLockStatus = "comment-activity-user-brand-invalid";
				dashboardInvalidateSession(request,  response, pageName) ;
				throw new Exception("Brand User "+ lbrandDashoardUser + "does not have access to this brand " +brandMap);
			}

			if (lockstatus != null && lockstatus.equals("true")) {
				lockstatus = "Y";
				commentLockStatus = "comment-lock-success";
				activityLockstatus = UserActivityLog.ACTIVITY_DASHBOARD_COMMENT_LOCK ;
			} else if (lockstatus != null && lockstatus.equals("false")) {
				lockstatus = "N";
				activityLockstatus = UserActivityLog.ACTIVITY_DASHBOARD_COMMENT_UNLOCK ;
				commentLockStatus = "comment-unlock-success";
			}
			if (lbrandDashoardUser > 10000) {
				request.getSession().setAttribute("userCountry", countrycode);
				connection = dataSource.getConnection();
				ps = connection.prepareStatement("UPDATE SURVEYCOMMENTS  set LOCKSTATUS = '" + lockstatus + "' where id = ?");
				ps.setString(1, commentid);
				result = ps.executeUpdate();
				ps.close();
				connection.commit();
				UserActivityLog userLog = new UserActivityLog();
				userLog.insertRecord(request, connection, UserActivityLog.ACTIVITY_DASHBOARD_TABLE, activityLockstatus, lbrandDashoardUser, Long.parseLong(commentid), "surveycomments");


			}
		} catch (Exception e) {
			if(commentLockStatus.length() < 1) {
				commentLockStatus = "comment-lock-error";
				LOG.error(" Brand User Lock / Unlock activity error: lockstatus=" + lockstatus + ", commentid=" + commentid + ", brand user=" + lbrandDashoardUser);
			}
			LOG.error(e.getMessage(), e);
		} finally {
			Utils.closeQuietly(connection);
		}
		return commentLockStatus;
	}

	public String dashboardSurveyCommentAction(HttpServletRequest request, HttpServletResponse response,  String pageName) {
		String commentActionStatus = "";
		String parentCommentid = "", actionType = "",  statusId = "", responseClass="",
				strBrandComment = "", brandMap,sectorcode="0", brandCode = "0", countrycode = "0", dealercode= "0", shUserid = "0"
				,sReturnStatus = "", originalCommentid = "";
		String updateQuery = "" ;
		long lbrandDashoardUser = 0,commentId = 0;
		int result = 0, valid = 1;
		StringBuffer objResponseDiv = new StringBuffer();
		StringBuffer objCountResponseDiv = new StringBuffer();
		String strLineBreak = "\n";
		PreparedStatement ps;
		ApplicationContext context = RequestContextUtils.getWebApplicationContext(request);
		PoolingDataSource<PoolableConnection> dataSource = (PoolingDataSource<PoolableConnection>) context.getBean("dataSource");
		Connection connection = null;
		try {
			parentCommentid = request.getParameter("extraId");
			originalCommentid = request.getParameter("originalId");
			strBrandComment = request.getParameter("fieldValue");
			actionType = request.getParameter("fieldName");
			brandMap = request.getParameter("brandMap") ;
			shUserid =  request.getParameter("shuserid") ;
			SHBrandUser shBrandUser = (SHBrandUser)request.getSession().getAttribute("shBrandUser");
			if (request.getSession().getAttribute("shBrandUser") == null) {
				commentActionStatus = "comment-activity-user-logout";
				dashboardInvalidateSession(request,  response, pageName) ;
				throw new Exception("No Brand User in session while performing action " +actionType);
			}
			lbrandDashoardUser =  shBrandUser.getShUserId();
			if (lbrandDashoardUser < 10001) {
				commentActionStatus = "comment-activity-user-logout";
				dashboardInvalidateSession(request,  response, pageName) ;
				throw new Exception("Invalid Brand User id "+lbrandDashoardUser +" in session while performing action " +actionType);
			}
			LOG.debug("dashboardSurveyCommentAction debug points ");
			LOG.debug("lbrandDashoardUser" + lbrandDashoardUser);
			LOG.debug("parentCommentid" + parentCommentid);
			LOG.debug("actionType" + actionType);
			LOG.debug("brandMap" + brandMap);
			LOG.debug("shUserid" + shUserid);
			LOG.debug("strBrandComment" + strBrandComment);

			/* check if brand user has access to brand */
			StringTokenizer objSt1;
			/* get the countrycode, */
			if(brandMap != null && brandMap.indexOf("~") > -1) {
				objSt1 = new java.util.StringTokenizer(brandMap, "~");
				countrycode =   objSt1.nextToken();
				sectorcode =   objSt1.nextToken();
				brandCode =   objSt1.nextToken();
				dealercode =   objSt1.nextToken();
			}  else{
				commentActionStatus = "comment-activity-error";
				dashboardInvalidateSession(request,  response, pageName) ;
				throw new Exception("Brand Map is empty in this request for user " +lbrandDashoardUser);
			}
			if (brandCode == null  || brandCode.length()== 0 || sectorcode  == null  || sectorcode.length()== 0 ) {
				commentActionStatus = "comment-activity-error";
				dashboardInvalidateSession(request,  response, pageName) ;
				throw new Exception("Brand Map is empty in this request for user " +lbrandDashoardUser);
			}
			valid = shBrandUser.checkUserHasBrandAccess(brandMap);
			if(valid != 0){
				commentActionStatus = "comment-activity-user-brand-invalid";
				dashboardInvalidateSession(request,  response, pageName) ;
				throw new Exception("Brand User "+ lbrandDashoardUser + "does not have access to this brand " +brandMap);
			}
			if (actionType != null && actionType.indexOf("okay") == -1 && strBrandComment.length() < 2){
				commentActionStatus = "comment-activity-user-comment-invalid";
				throw new Exception("Brand User "+ lbrandDashoardUser + "has entered invalid comment ~" +strBrandComment +"~ ");
			}

			/* status-new"   - 26512
             // status-flag - 26513
             // status-replied - 26514
             // status-okay  - 26515
            */
			if (actionType != null && actionType.indexOf("flag") > -1) {
				actionType = UserActivityLog.ACTIVITY_DASHBOARD_COMMENT_FLAGGED;
				statusId = "26513";
				responseClass = "flag";
				updateQuery = "UPDATE SURVEYCOMMENTS_SUMMARY SET FLAGGED = 'Y', NEW_IND = 'N' where id = ?";
			} else if (actionType != null && actionType.indexOf("reply") > -1) {
				actionType = UserActivityLog.ACTIVITY_DASHBOARD_COMMENT_REPLY;
				statusId = "26514";
				updateQuery = "UPDATE SURVEYCOMMENTS_SUMMARY SET REPLIED = 'Y', NEW_IND = 'N' where id = ?";
				//set cookie here
				responseClass = "response";
			} else if (actionType != null && actionType.indexOf("okay") > -1) {
				actionType = UserActivityLog.ACTIVITY_DASHBOARD_COMMENT_OKAY;
				updateQuery = "UPDATE SURVEYCOMMENTS_SUMMARY SET OKAY = 'Y', NEW_IND = 'N' where id = ?";
				statusId = "26515";
			}
			request.getSession().setAttribute("userCountry", countrycode);

			connection = dataSource.getConnection();
			//flag or reply enter one more row.
			if (actionType.equals(UserActivityLog.ACTIVITY_DASHBOARD_COMMENT_FLAGGED) || actionType.equals(UserActivityLog.ACTIVITY_DASHBOARD_COMMENT_REPLY)
			    || actionType.equals(UserActivityLog.ACTIVITY_DASHBOARD_COMMENT_OKAY)) {
				//  insert a new row
				ps = connection.prepareStatement("Select surveyComments_Seq.nextVal from dual");
				ResultSet objRs = ps.executeQuery();
				if (objRs.next())
					commentId = objRs.getLong(1);
				objRs.close();
				ps.close();
				if (commentId < 10001)
					commentId = 10001;
				if (actionType.equals(UserActivityLog.ACTIVITY_DASHBOARD_COMMENT_OKAY)) {
					ps = connection.prepareStatement("insert into surveyComments (ID, BRANDUSERID, PARENTCOMMENTID, LANGUAGECODE, COUNTRYCODE, SECTORCODE, BRANDCODE, DEALERCODE, COMMENTSTATUS, ROOTPARENTID) values(?,?, ?,?, ?, ?,?,?,?, ?)");
				} else {
					ps = connection.prepareStatement("insert into surveyComments (ID, BRANDUSERID, PARENTCOMMENTID, LANGUAGECODE, COUNTRYCODE, SECTORCODE, BRANDCODE, DEALERCODE,COMMENTSTATUS, ROOTPARENTID, APPROVECOMMENT, APPROVEDLANGUAGECODE) values(?,?, ?, ?,?, ?,?,?, ?, ?, ?, ?)");
				}
				ps.setLong(1, commentId);
				ps.setLong(2, lbrandDashoardUser);
				ps.setLong(3, Long.parseLong(parentCommentid));
				ps.setString(4, "en");
				ps.setString(5, countrycode);
				ps.setString(6, sectorcode);
				ps.setString(7, brandCode);
				ps.setString(8, dealercode);
				ps.setString(9, statusId);
				ps.setLong(10, Long.parseLong(originalCommentid));
				if (!actionType.equals(UserActivityLog.ACTIVITY_DASHBOARD_COMMENT_OKAY)) {
					ps.setString(11, strBrandComment);
					ps.setString(12, "en");
				}
				ps.executeUpdate();
				ps.close();
			}
			// update original commentid
			ps = connection.prepareStatement("UPDATE SURVEYCOMMENTS  set LOCKSTATUS = 'N' where id = ?");
			ps.setLong(1, Long.parseLong(parentCommentid));
			result = ps.executeUpdate();
			LOG.debug(" SURVEYCOMMENTS is unlocked   " + result);

			//insert into brandactivitylog

			UserActivityLog userLog = new UserActivityLog();
			userLog = new UserActivityLog();
			userLog.insertRecord(request, connection, UserActivityLog.ACTIVITY_DASHBOARD_TABLE, UserActivityLog.ACTIVITY_DASHBOARD_COMMENT_UNLOCK, lbrandDashoardUser, Long.parseLong(parentCommentid), "surveycomments");

			ps = connection.prepareStatement(updateQuery);
			ps.setLong(1, Long.parseLong(originalCommentid));
			result = ps.executeUpdate();

			ps.close();
			//insert into brandactivitylog

			userLog = new UserActivityLog();
			userLog.insertRecord(request, connection, UserActivityLog.ACTIVITY_DASHBOARD_TABLE, actionType, lbrandDashoardUser, commentId, "surveycomments");

			connection.commit();
			commentActionStatus = "comment-activity-success";
			/*****  AJAX  REFRESH FUNCTIONS  *****/
			//call procedure to get latest counts
			LOG.debug("Calling sh_brand_dashboard countrycode =" + countrycode + ", categoryCode=" + sectorcode + ", brandCode=" + brandCode + ", dealerCode=" + dealercode);
			LOG.debug(" Brand User is  " + lbrandDashoardUser);
			CallableStatement callableStatement = null;
			String calcBrandDashboardCounts = "{call sh_brand_dashboard(?,?,?,?,?,?)}";
			try {
				callableStatement = connection.prepareCall(calcBrandDashboardCounts);
				callableStatement.setLong(1, lbrandDashoardUser);
				callableStatement.setString(2, countrycode);
				callableStatement.setLong(3, Long.parseLong(sectorcode));
				callableStatement.setLong(4, Long.parseLong(brandCode));
				callableStatement.setLong(5, Long.parseLong(dealercode));
				callableStatement.registerOutParameter(6, java.sql.Types.VARCHAR);
				// execute getDBUSERByUserId store procedure
				callableStatement.executeUpdate();
				sReturnStatus = callableStatement.getString(6);
				ps = null;
				if(sReturnStatus.length() >0 && sReturnStatus.startsWith("ERROR") ) {
					LOG.debug("Calling sh_brand_dashboard procedure gave error: countrycode =" + countrycode + ", categoryCode=" + sectorcode + ", brandCode=" + brandCode + ", dealerCode=" + dealercode);
					LOG.debug(sReturnStatus);
				} else if(sReturnStatus.length() >0 && sReturnStatus.startsWith("SUCCESS") ) {
					// then fetch the values from brandaveragescores
					String strQuery = "select newcomments, flagcomments, thisyearcomments, twittershares, fbshares, comments from brandaveragescores_" + countrycode +" where brandCode =? and  dealercode = ?";
					ps = connection.prepareStatement(strQuery, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
					ps.setString(1, brandCode);
					ps.setString(2, dealercode);
					ResultSet objRs = ps.executeQuery();
					try {
						while(objRs.next()) {
							objCountResponseDiv = new StringBuffer();
							objCountResponseDiv.append("|||commentCountStatus:" + " ~newcomment="+objRs.getString("newcomments"));
							objCountResponseDiv.append(",flagcomments=" + objRs.getString("flagcomments")) ;
							objCountResponseDiv.append(",thisyearcomments=" + objRs.getString("thisyearcomments")) ;
							objCountResponseDiv.append(",fbshares=" + objRs.getString("fbshares")) ;
							objCountResponseDiv.append(",twittershares=" + objRs.getString("twittershares")) ;
							objCountResponseDiv.append(",comments=" + objRs.getString("comments")) ;
                    	}
                    }
					catch(Exception ex) {
						LOG.error("ERROR fetching comments counts "+ex.getMessage());
					}
					objRs.close();
					ps.close();
				}
			} catch (SQLException e) {
				LOG.error("ERROR fetching comments counts "+e.getMessage());
			}
			// return back the added comment
			if (actionType.equals(UserActivityLog.ACTIVITY_DASHBOARD_COMMENT_FLAGGED) || actionType.equals(UserActivityLog.ACTIVITY_DASHBOARD_COMMENT_REPLY)) {
				DateTime dt = new DateTime(System.currentTimeMillis());
				DateTimeFormatter fmt = DateTimeFormat.forPattern("dd.MM.yyyy HH:mm");
                objResponseDiv.append(commentActionStatus + "|||");
				objResponseDiv.append("<div class=\"brand-response\">").append(strLineBreak);
				objResponseDiv.append("<h4><span class=\"" + responseClass + "\">" + responseClass.toUpperCase() + "</span>" + strBrandComment + "</h4>").append(strLineBreak);
				objResponseDiv.append("<div class=\"comment-meta\">").append(strLineBreak);
				objResponseDiv.append("<span class=\"date\">");
				objResponseDiv.append("By ").append(shBrandUser.getShUserName()).append(" on ") ;
                objResponseDiv.append(fmt.print(dt) + "</span>").append(strLineBreak);
				objResponseDiv.append("</div>").append(strLineBreak).append("</div>");
				if(objCountResponseDiv.toString().length()>1)
					objResponseDiv.append(objCountResponseDiv.toString());
				commentActionStatus = objResponseDiv.toString();
				request.getSession().setAttribute("lastRefreshTime", System.currentTimeMillis())    ;
			}
			//update notification counter for consumer
			try {
				if (Long.parseLong(shUserid) > 10000) {
					ps = connection.prepareStatement("SELECT * FROM SERVICEHEROUSERS WHERE ID = ?");
					ps.setLong(1, Long.parseLong(shUserid));
					ResultSet objRs = ps.executeQuery();
					String countExtraField;
					while (objRs.next()) {
						LOG.debug("update contact's notification for userid : " + shUserid + "  ");
						Contact contact = new Contact(new String[]{objRs.getString("emailAddress"), "servicehero"});
						countExtraField = contact.getExtendedDetail("extra_field_10");
						if (countExtraField != null && !countExtraField.equals("")) {
							contact.setExtendedDetail("extra_field_10", Integer.toString(Integer.parseInt(countExtraField) + 1));
						} else {
							contact.setExtendedDetail("extra_field_10", "1");
						}
						//contact.save("tcm:" + plId + "-" + TRIDION_RESET_PASSWORD_PAGE + "-64");
					}
					objRs.close();
					ps.close();

				}
			} catch (Exception e) {
				LOG.error("EXCEPTION updating shuser's "+shUserid+ " contact notification ");
				LOG.error(e.getMessage(), e);
			}
		} catch(SQLException se) {
			if(commentActionStatus.length() < 1) {
				commentActionStatus = "comment-activity-error";
				LOG.error(" Brand User Activity error: actiontype=" + actionType + ", commentid=" + parentCommentid + ", brand user=" + lbrandDashoardUser);
			}
			LOG.error(se.getMessage(),se);

		} catch(Exception ex) {
			if(commentActionStatus.length() < 1) {
				commentActionStatus = "comment-activity-error";
				LOG.error(" Brand User Activity error: actiontype=" + actionType + ", commentid=" + parentCommentid + ", brand user=" + lbrandDashoardUser);
			}
			LOG.error(ex.getMessage(), ex);
		}finally
		{
			Utils.closeQuietly(connection);
		}
		return commentActionStatus;
	}

	/* this function is called to dynammically refresh updated sections of a page for a user
	   parameters:
	   commentidMap
	   session-last updated time
	   country~sector~brand~dealer
	   branduser
	   lockedcommentid
       *get the brand and
       * 1. iterate the parent comment id and search the activity v_comment_tree for any reply or flag within that time
       and if shuserid is not null then this is consumer repsonse
       and if branduserid is not null then this is brnad response
       * 2.search activitylog for all locked, replied, flagged, new comments, counts and populate the area
	*/
	public String dashboardFetchLatestCommentList(HttpServletRequest request) {

		LOG.debug("Entered dashboardFetchLatestCommentList ");
		//String commentActionStatus = "";
		String brandMap, sectorcode = "0", brandCode = "0", countrycode = "0", dealercode = "0", strQuery = "", strQuery2 = "", commentIdMap = "", parentCommentId = "", sReturnStatus = "";
		long lastRefreshTime = 0;
		String commentAjaxStatus = "comment-ajax-success";
		//String parentCommentid = "", actionType = "",  statusId = "", responseClass="", strBrandComment = "", brandMap, sectorcode="0", brandCode = "0", countrycode = "0", dealercode= "0";
		//long lbrandDashoardUser,commentId = 0;
		//int result = 0;
		StringBuffer objCommentResponseDiv = new StringBuffer();
		StringBuffer objCountResponseDiv = new StringBuffer();
		StringBuffer objResponseDiv = new StringBuffer();
		String strLineBreak = "\n";
		PreparedStatement ps;
		ApplicationContext context = RequestContextUtils.getWebApplicationContext(request);
		PoolingDataSource<PoolableConnection> dataSource = (PoolingDataSource<PoolableConnection>) context.getBean("dataSource");
		Connection connection = null;
		long lBrandUserId = 10000;

		// diff action - reply, flag, okay
		// insert into surveycomments, insert branduseractivitylogs, unlock the comment
				/*fieldName: textareaField,
				                fieldValue:fValue,
								extraId: extraId,  */
			/*if (request.getSession().getAttribute("brandDashoardUser") != null) {
				lbrandDashoardUser = Long.parseLong((String) request.getSession().getAttribute("brandDashoardUser"));
			}*/
		LOG.debug("commentAjaxStatus ") ;
		try {
			connection = dataSource.getConnection();
			LOG.debug("SHBrandUser " + request.getSession().getAttribute("shBrandUser")) ;
			SHBrandUser shBrandUser = (SHBrandUser)request.getSession().getAttribute("shBrandUser");

			if(request.getSession().getAttribute("shBrandUser") == null)  {
				LOG.debug("shBrandUser is null ") ;
				commentAjaxStatus =  "user-logout";

			}   else if ( shBrandUser != null) {
				lBrandUserId = shBrandUser.getShUserId();

				brandMap = request.getParameter("brandMap");
				commentIdMap = request.getParameter("commentidMap");
				if (request.getSession().getAttribute("lastRefreshTime") != null) {
					lastRefreshTime = Long.parseLong(request.getSession().getAttribute("lastRefreshTime").toString());        //this is time in millisec

				}
				LOG.debug("lastRefreshTime " + lastRefreshTime + " and " + lBrandUserId);
				if (lastRefreshTime > 0) {
					LOG.debug("dashboardFetchLatestCommentList :: brandMap" + brandMap);
					StringTokenizer objSt1, objSt2;
	            /* get the countrycode, */
					if (brandMap.indexOf("~") > -1) {
						objSt1 = new java.util.StringTokenizer(brandMap, "~");
						countrycode = objSt1.nextToken();
						sectorcode = objSt1.nextToken();
						brandCode = objSt1.nextToken();
						dealercode = objSt1.nextToken();
					}

					// now create string for the counts
					// call procedure to get latest counts
					LOG.debug("fETCHaJAX :: Calling sh_brand_dashboard countrycode =" + countrycode + ", categoryCode=" + sectorcode + ", brandCode=" + brandCode + ", dealerCode=" + dealercode);
					LOG.debug(" Brand User is  " + lBrandUserId);
					CallableStatement callableStatement = null;
					String calcBrandDashboardCounts = "{call sh_brand_dashboard(?,?,?,?,?,?)}";
					try {


						callableStatement = connection.prepareCall(calcBrandDashboardCounts);
						callableStatement.setLong(1, lBrandUserId);
						callableStatement.setString(2, countrycode);
						callableStatement.setLong(3, Long.parseLong(sectorcode));
						callableStatement.setLong(4, Long.parseLong(brandCode));
						callableStatement.setLong(5, Long.parseLong(dealercode));
						callableStatement.registerOutParameter(6, java.sql.Types.VARCHAR);

						// execute getDBUSERByUserId store procedure
						callableStatement.executeUpdate();

						sReturnStatus = callableStatement.getString(6);
						ps = null;
						if (sReturnStatus.length() > 0 && sReturnStatus.startsWith("ERROR")) {
							LOG.debug("Error : Calling sh_brand_dashboard procedure gave error: countrycode =" + countrycode + ", categoryCode=" + sectorcode + ", brandCode=" + brandCode + ", dealerCode=" + dealercode);
							LOG.debug(sReturnStatus);
						} else if (sReturnStatus.length() > 0 && sReturnStatus.startsWith("SUCCESS")) {
							// then fetch the values from brandaveragescores

							String strQuery1 = "select newcomments, flagcomments, thisyearcomments, twittershares, fbshares, comments from brandaveragescores_" + countrycode + " where brandCode =? and  dealercode = ?";
							ps = connection.prepareStatement(strQuery1, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
							ps.setString(1, brandCode);
							ps.setString(2, dealercode);
							ResultSet objRs = ps.executeQuery();
							try {
								while (objRs.next()) {
									objCountResponseDiv = new StringBuffer();
									objCountResponseDiv.append("||commentCountStatus:" + " newcomment=" + objRs.getString("newcomments"));
									objCountResponseDiv.append(",flagcomments=" + objRs.getString("flagcomments"));
									objCountResponseDiv.append(",thisyearcomments=" + objRs.getString("thisyearcomments"));
									objCountResponseDiv.append(",fbshares=" + objRs.getString("fbshares"));
									objCountResponseDiv.append(",twittershares=" + objRs.getString("twittershares"));
									objCountResponseDiv.append(",comments=" + objRs.getString("comments"));

								}
							} catch (Exception ex) {
								LOG.error("error fetching comments counts " + ex.getMessage());
							}
							objRs.close();
							ps.close();
						}

					} catch (SQLException e) {

						LOG.error("error fetching comments counts " + e.getMessage());


					}


					LOG.debug("dashboardFetchLatestCommentList lBrandUserId" + lBrandUserId);
					if (lBrandUserId > 10000) {
						if (commentIdMap.indexOf(",") > -1) {
							//objSt2 = new java.util.StringTokenizer(commentIdMap, "~");

							String actionType = "", responseClass = "", actionDesc = "", isActionType = "false", additionalClass = "", brandresonseStatus = "", brandresonseStatusHtml = "";
							//while (objSt2.hasMoreTokens()) {
								//parentCommentId = objSt2.nextToken();

								strQuery = "select id, approvecomment,lockstatus,  approvedlanguagecode, branduserid, shuserid, commentapproved,\n" +
								           "personname, actiontime, commentstatus, brandresponsestatus, parentcommentid  from V_ALL_COMMENTS where parentCommentid in ("+commentIdMap+") and actionTime > = ? and nvl(commentapproved, 'Y')  not in ('X', 'N') order by id, actionTime";
								try {
									//ps = connection.prepareStatement(strQuery, ResultSet.TYPE_FORWARD_ONLY);
									ps = connection.prepareStatement(strQuery, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
									//ps.setString(1, Long.parseLong(commentIdMap));
									ps.setTimestamp(1, new Timestamp(lastRefreshTime));
									LOG.debug("dashboardFetchLatestCommentList time last refreshed" + new Timestamp(lastRefreshTime));
									ResultSet objRs = ps.executeQuery();
									int count = 0;
									while (objRs.next()) {

										/* if commentstatus = commentlock and this is not same user and has been less than given time then show locked status
										    if commentstatus is null and comment approved is Y and parent comment id is 0 then this is new comment - don't show
                                            if commentstatus is 26513  this is flagged comment
                                            if commentstatus is 26514 this is reply comment

										 */
										DateTime dt = new DateTime(objRs.getTimestamp("actiontime"));
										DateTimeFormatter fmt = DateTimeFormat.forPattern("dd.MM.yyyy HH:mm");

										if (count == 0) {
											objCommentResponseDiv = new StringBuffer();
											objCommentResponseDiv.append(":$:parentCommentid=ajaxRefresh").append(parentCommentId);
											objCommentResponseDiv.append("responseDiv=");

										}
										actionType = objRs.getString("commentstatus");
										if (actionType != null && actionType.indexOf("commentunlock") > -1) {
											actionType = UserActivityLog.ACTIVITY_DASHBOARD_COMMENT_UNLOCK;
											isActionType = "remove";

										} else if (actionType != null && actionType.indexOf("commentlock") > -1) {
											actionType = UserActivityLog.ACTIVITY_DASHBOARD_COMMENT_LOCK;
											responseClass = "icon-lock";
											isActionType = "add";
											additionalClass = "locked";
										} else if (actionType != null && actionType.indexOf("26513") > -1) {
											actionType = UserActivityLog.ACTIVITY_DASHBOARD_COMMENT_FLAGGED;
											responseClass = "flag";
											actionDesc = "FLAG";
											isActionType = "true";
											additionalClass = "brand-response";
										} else if (actionType != null && actionType.indexOf("26514") > -1) {
											actionType = UserActivityLog.ACTIVITY_DASHBOARD_COMMENT_REPLY;
											responseClass = "response";
											actionDesc = "RESPONSE";
											isActionType = "true";
											additionalClass = "brand-response";
										} else if (actionType != null && actionType.indexOf("29661") > -1) {
											actionType = UserActivityLog.ACTIVITY_DASHBOARD_COMMENT_PRIVATE;
											responseClass = "private";
											actionDesc = "PRIVATE";
											isActionType = "true";
											additionalClass = "brand-response";
											if (objRs.getString("approvedlanguagecode") != null && objRs.getString("approvedlanguagecode").toLowerCase().equals("ar"))
												additionalClass = additionalClass + " comment-arabic";
											else if (objRs.getString("approvedlanguagecode") != null && objRs.getString("approvedlanguagecode").toLowerCase().equals("en"))
												additionalClass = additionalClass + " comment-english";
										}
										brandresonseStatus = objRs.getString("brandresponsestatus");
											/*COMMENT_BRAND_STATUS_HELPFULL   - 26517
											  COMMENT_BRAND_STATUS_NOTHELPFULL - 26518       */
										if (brandresonseStatus != null && brandresonseStatus.indexOf("26518") > -1) {
											brandresonseStatusHtml = "<ul class=\"list-unstyled\"><li><span>Not helpful response</span></li></ul>";
										} else if (brandresonseStatus != null && brandresonseStatus.indexOf("26517") > -1) {
											brandresonseStatusHtml = "<ul class=\"list-unstyled\"><li><span>Helpful response</span></li></ul>";
										}
										//place the updates in the respective place holders
										/* status-new"   - 26512
										                    // status-flag - 26513
										                    // status-replied - 26514
										                    // status-okay  - 26515
										                    */
										if (isActionType.equals("true")) {
											//if (count > 0)
												//objCommentResponseDiv.append(",");
											objCommentResponseDiv.append(":,:commentid" + objRs.getLong("parentcommentid"));
											objCommentResponseDiv.append(":~:<div class=\"" + additionalClass + "\">").append(strLineBreak);
											objCommentResponseDiv.append("<h4><span class=\"" + responseClass + "\">" + actionDesc + "</span>" + objRs.getString("approvecomment") + "</h4>").append(strLineBreak);
											objCommentResponseDiv.append("<div class=\"comment-meta\">").append(strLineBreak);
											if (brandresonseStatusHtml.length() > 0)
												objCommentResponseDiv.append("brandresonseStatusHtml").append(strLineBreak);
											objCommentResponseDiv.append("<span class=\"date\">");
											if (objRs.getString("branduserid") != null && objRs.getString("branduserid").length() > 0) {
												objCommentResponseDiv.append("By ").append(objRs.getString("personname"))
														.append(" on ");
											}
											objCommentResponseDiv.append(fmt.print(dt) + "</span>").append(strLineBreak);
											objCommentResponseDiv.append("</div>").append(strLineBreak).append("</div>").append(strLineBreak);
										}else if (isActionType.equals("add")) {
											//if (count > 0)
												//objCommentResponseDiv.append(",");
											objCommentResponseDiv.append(":,:commentid" + objRs.getLong("id"));
											objCommentResponseDiv.append(":~:add-lock<div class=\"" + additionalClass + "\">").append(strLineBreak);
											objCommentResponseDiv.append("<span class=\"" + responseClass + "\"></span>").append(strLineBreak);

											objCommentResponseDiv.append("Locked by  ").append(objRs.getString("personname")).append(" on ");
											objCommentResponseDiv.append(fmt.print(dt) + "</div>").append(strLineBreak);
										}else if (isActionType.equals("remove")) {
												//objCommentResponseDiv.append(",");
											objCommentResponseDiv.append(":,:commentid" + objRs.getLong("id"));
											objCommentResponseDiv.append(":~:remove-lock");
										}
										count++;
									}
									objRs.close();
									ps.close();
								} catch (Exception e) {
									LOG.error(e.getMessage(), e);
								}

							//}

						}   //end of commentid
						if (objCountResponseDiv.indexOf("newcomment") > -1 || objResponseDiv.indexOf("parentCommentid") > -1) {
							objResponseDiv.append("commentAjaxStatus=" + commentAjaxStatus);

							if (objCountResponseDiv.toString().length() > 1) {
								objResponseDiv.append(objCountResponseDiv.toString());
							}
							if (objCommentResponseDiv.indexOf("commentid") > -1) {
								objResponseDiv.append("||commentidStatus" + objCommentResponseDiv.toString());
							}
						}


					}
					commentAjaxStatus = objResponseDiv.toString();
					request.getSession().setAttribute("lastRefreshTime", System.currentTimeMillis());
				}
			}
		} catch (SQLException se) {
			commentAjaxStatus = "comment-activity-error";

			LOG.error(" Brand User Ajax refresh: ");
			LOG.error(se.getMessage(), se);

		} catch (Exception ex) {
			commentAjaxStatus = "comment-activity-error";

			LOG.error(" Brand User Ajax refresh:");
			LOG.error(ex.getMessage(), ex);
		} finally

		{
			Utils.closeQuietly(connection);
		}


		return commentAjaxStatus;
	}
	public void dashboardInvalidateSession(HttpServletRequest request,  HttpServletResponse response, String pageName) {
		HttpSession session = request.getSession();
		session.removeAttribute("shBrandUser");
		session.removeAttribute("dashboardSector");
		session.removeAttribute("dashboardBrand");
		session.removeAttribute("dashboardDealer");
		session.removeAttribute("brandShortTitle");
		session.removeAttribute("platform");
		session.removeAttribute("userAgent");
		session.removeAttribute("ipAddress");
		session.removeAttribute("ipAddressCountry");
		session.removeAttribute("campaignId");
		session.removeAttribute("userValue");
		session.removeAttribute("socialMedia");
		session.removeAttribute("socialMediaPicture");
		session.removeAttribute("activeFilters");
		session.removeAttribute("lastRefreshTime");
		Cookie objCookie = new Cookie("sheroBrandUserId", "0");
		objCookie.setDomain("www.servicehero.com");
		objCookie.setPath("/");
		objCookie.setComment("");
		objCookie.setMaxAge(0);
		response.addCookie(objCookie);
		session.setAttribute("pageName", pageName);
		session.invalidate();
	}

}
