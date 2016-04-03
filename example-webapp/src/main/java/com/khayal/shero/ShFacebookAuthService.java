package com.khayal.shero;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.misc.BASE64Decoder;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URL;
import java.util.StringTokenizer;
import java.util.TreeMap;

/**
 * Created by DELL on 12/24/2015.
 */
public class ShFacebookAuthService {

	private static Logger logger = LoggerFactory.getLogger(ShFacebookAuthService.class);

	/*private static final String apiKey = MyHttpServlet.getValue("shSurvey2011FbApiKey"); // YOUR API KEY
	private static final String appSecret = MyHttpServlet.getValue("shSurvey2011FbAppSecret"); // YOUR SECRET KEY
	private static final String appId = MyHttpServlet.getValue("shSurvey2011FbAppId"); // YOUR APP ID

	private static final String redirect_uri = MyHttpServlet.getValue("secureWebAddress") + "/servlet/adminV3.facebook.SHSurvey.ShFbLoginServlet?test=1";
	*/    //Changed the below code by Sree for the facebook platform upgrade
	    //private static final String[] perms = new String[]{"publish_stream", "email"};
	    private static final String[] perms = new String[]{"email"};

	/*    public static String getAPIKey() {
	        return apiKey;
	    }

	    public static String getSecret() {
	        return appSecret;
	    }


	public static String getAuthURL() {
	        return "https://www.facebook.com/dialog/oauth?client_id="  ;
	               + appId + "&redirect_uri=" + redirect_uri + "&scope=" + StringUtils.join(perms);
	    }
	   */

	public static JsonNode getJSONNode(String signedRequest) throws IOException {
		if (signedRequest == null) {
			// below changes done for using Logback by Jayanthi
			logger.error("ERROR: Unable to retrieve signed_request parameter");
			//  System.out.println("ERROR: Unable to retrieve signed_request parameter");


		}
		int count;
		String payload;
		JsonNode payloadObject;
		StringTokenizer st;
		BASE64Decoder decoder;

		count = 0;
		payload = null;
		payloadObject = null;
		st = new StringTokenizer(signedRequest, ".");
		decoder = new BASE64Decoder();

		//Retrieve payload (Note: encoded signature is used for internal verification and it is optional)
		while (st.hasMoreTokens()) {
			if (count == 1) {
				payload = st.nextToken();
				break;
			} else
				st.nextToken();

			count++;
		}

		//Replace special character in payload as indicated by FB
		payload = payload.replace("-", "+").replace("_", "/").trim();

		//Decode payload
		try {
			byte[] decodedPayload = decoder.decodeBuffer(payload);
			payload = new String(decodedPayload, "UTF8");
			//System.out.println("payload: " + payload);
		}
		catch (IOException e) {
			// below changes done for using Logback by Jayanthi
			logger.error("ERROR: Unable to perform Base64 Decode");

			//  System.out.println("ERROR: Unable to perform Base64 Decode");

		}

		//JSON Decode - payload
		try {

			ObjectMapper mapper = new ObjectMapper();
			payloadObject = mapper.readTree(payload);

		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return payloadObject;
	}

	public static String[] getUserDetails(String accessToken) {
        try {
            //Changed the below code by Sree for the facebook platform upgrade
            //JSONObject resp = new JSONObject(IOUtil.urlToString(new URL("https://graph.facebook.com/me?access_token=" + accessToken)));
	        ObjectMapper mapper = new ObjectMapper();
	        JsonNode resp = mapper.readTree(new URL("https://graph.facebook.com/v2.0/me?access_token=" + accessToken));
            String id, name, email, gender;

            try {

	            id = resp.path("id").asText();
            } catch (Exception e) {
                id = "0";
            }
            try {

	            name =  resp.path("first_name").asText() + " " + resp.path("last_name").asText();
            } catch (Exception e) {
                name = "";
            }
            try {

	            email = resp.path("email").asText();
            } catch (Exception e) {
                email = "";
            }
            try {

	            gender = resp.path("gender").asText();
            } catch (Exception e) {
                gender = "";
            }

            return new String[]{id, name, email, gender};
        } catch (Throwable ex) {
            throw new RuntimeException("failed login", ex);
        }
    }

	public static String[] getUserFriends(String accessToken, HttpSession session) {
        try {
            //Changed the below code by Sree for the facebook platform upgrade
            //JSONObject resp = new JSONObject(IOUtil.urlToString(new URL("https://graph.facebook.com/me/friends?access_token=" + accessToken)));
	        ObjectMapper mapper = new ObjectMapper();
	        JsonNode rootNode = mapper.readTree(new URL("https://graph.facebook.com/v2.0/me/friends?access_token=" + accessToken));

	        // Get data
	        try {
		        JsonNode dataNode = rootNode.path("data");
		        TreeMap<String, String> fbFriends = new TreeMap<String, String>();
	            if (dataNode.isArray()) {
	                // If this node an Arrray?
	            }

		        for (JsonNode node : dataNode) {
	                String id = node.path("id").asText();
	                String name = node.path("name").asText();
			        fbFriends.put(id, name);
	            }
		        session.setAttribute("fbFriends", fbFriends);
	        } catch (Exception e) {
             //   System.out.println(GetErrorInfo.getErrorInfo(e, "ShFacebookAuthService"));
             //   e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                // below changes done for using Logback by Jayanthi
               // logger.error(GetErrorInfo.getErrorInfo(e, "ShFacebookAuthService"));
                logger.error(e.getMessage(),e);

            }


            return new String[]{""};
        } catch (JsonGenerationException e) {
            e.printStackTrace();
	        throw new RuntimeException("failed login", e);
        } catch (JsonMappingException e) {
            e.printStackTrace();
	        throw new RuntimeException("failed login", e);
        } catch (IOException e) {
            e.printStackTrace();
	        throw new RuntimeException("failed login", e);
        } catch (Throwable ex) {
            throw new RuntimeException("failed login", ex);
        }
    }




}
