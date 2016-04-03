package com.khayal.shero.webservices;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;


import javax.servlet.http.HttpServletRequest;



import org.apache.commons.dbcp2.PoolableConnection;
import org.apache.commons.dbcp2.PoolingDataSource;
import org.jose4j.jwe.ContentEncryptionAlgorithmIdentifiers;
import org.jose4j.jwe.JsonWebEncryption;
import org.jose4j.jwe.KeyManagementAlgorithmIdentifiers;
import org.jose4j.jwk.JsonWebKey;
import org.jose4j.keys.PbkdfKey;
import org.jose4j.jwk.PublicJsonWebKey;
import org.apache.commons.dbcp2.Utils;


import org.jose4j.jwk.RsaJsonWebKey;
import org.jose4j.jwk.RsaJwkGenerator;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;

import org.jose4j.lang.JoseException;
import org.springframework.context.ApplicationContext;
import org.springframework.web.servlet.support.RequestContextUtils;

import com.khayal.shero.*;

public class JWT {
	//private static RsaJsonWebKey rsaJsonWebKey = null;
	static String jweEncryptedJwk = "";
	static PublicJsonWebKey publicJsonWebKey = null;
	
	public static void getJweEncryptedJwk(HttpServletRequest request)
	{		
		Connection objCon = null;
		try{
			String jwkjson = "";			
		 	ApplicationContext context = RequestContextUtils.getWebApplicationContext(request);
	    	PoolingDataSource<PoolableConnection> dataSource = (PoolingDataSource<PoolableConnection>) context.getBean("dataSource");
	    	objCon = dataSource.getConnection();				
			PreparedStatement ps = objCon.prepareStatement("Select VALUE from SETTINGS_SHWS where lower(NAME)=?");
            ps.setString(1, "jweEncryptedJwk".toLowerCase());
            ResultSet objRs = ps.executeQuery();
            if (objRs.next())
            {
            	jweEncryptedJwk = objRs.getString("VALUE");
            }
            objRs.close();
            ps.close();
            
            ps = objCon.prepareStatement("Select VALUE from SETTINGS_SHWS where lower(NAME)=?");
            ps.setString(1, "jwkjson".toLowerCase());
            objRs = ps.executeQuery();
            if (objRs.next())
            {
            	jwkjson = objRs.getString("VALUE");
            }
            objRs.close();
            ps.close();
            
			JsonWebEncryption encryptingJwe = new JsonWebEncryption();
			encryptingJwe.setAlgorithmHeaderValue(KeyManagementAlgorithmIdentifiers.PBES2_HS256_A128KW);
			encryptingJwe.setEncryptionMethodHeaderParameter(ContentEncryptionAlgorithmIdentifiers.AES_128_CBC_HMAC_SHA_256);
			encryptingJwe.setKey(new PbkdfKey("s3rv1c3h3r0"));			
			encryptingJwe.setPayload(jwkjson);			
			JsonWebEncryption decryptingJwe = new JsonWebEncryption();			
	    	decryptingJwe.setCompactSerialization(jweEncryptedJwk);	 
	    	String payload = encryptingJwe.getPayload();	    	
	    	publicJsonWebKey = PublicJsonWebKey.Factory.newPublicJwk(payload);
	    	// share the public part with whomever/whatever needs to verify the signatures 
	    	//System.out.println(publicJsonWebKey.toJson(JsonWebKey.OutputControlLevel.PUBLIC_ONLY));
	    	//System.out.println("publicJsonWebKey.getKey(): "+publicJsonWebKey.getKey());
	    	//System.out.println("publicJsonWebKey.getPrivateKey(): "+publicJsonWebKey.getPrivateKey());
		}catch (JoseException e) {
			// TODO Auto-generated catch block
			e.getCause().printStackTrace();			
			e.printStackTrace();
			System.out.println(e.getMessage());	
		}catch (Exception e)
        {
			e.getCause().printStackTrace();			
        	e.printStackTrace();
        	System.out.println(e.getMessage());	           
        }finally
        {
        	Utils.closeQuietly(objCon);
        }
	}
	public static String generateJWT(HttpServletRequest request, long lSHUserId) {
		
		String jwt = "";
		try {
			System.out.println("inside generateJWT ");
			
	    	/*if(rsaJsonWebKey == null){
	    		//Generate an RSA key pair, which will be used for signing and verification of the JWT, wrapped in a JWK
	    		rsaJsonWebKey = RsaJwkGenerator.generateJwk(2048);
	    	}*/
	    				
			 //The below is the method to generate jweEncryptedJwk &  jwkjson and it is saved in SETTINGS_SHWS table 
			 //* name "jweEncryptedJwk" & "jwkjson" so that the issue with JWS validation due to different Keys will not be there even if we restart the server.	
	    	 /*String jwkjson = rsaJsonWebKey.toJson(JsonWebKey.OutputControlLevel.INCLUDE_PRIVATE);
	    	 System.out.println("jwkjson: "+jwkjson);
	    	    JsonWebEncryption encryptingJwe = new JsonWebEncryption();
	    	    encryptingJwe.setAlgorithmHeaderValue(KeyManagementAlgorithmIdentifiers.PBES2_HS256_A128KW);
	    	    encryptingJwe.setEncryptionMethodHeaderParameter(ContentEncryptionAlgorithmIdentifiers.AES_128_CBC_HMAC_SHA_256);
	    	    encryptingJwe.setKey(new PbkdfKey("s3rv1c3h3r0"));
	    	    encryptingJwe.setPayload(jwkjson);
	    	    String jweEncryptedJwk = encryptingJwe.getCompactSerialization();
	    	    System.out.println("jweEncryptedJwk: "+jweEncryptedJwk);

	    	    // save  jweEncryptedJwk somewhere  and load it on application start 

	    	    JsonWebEncryption decryptingJwe = new JsonWebEncryption();
	    	    decryptingJwe.setCompactSerialization(jweEncryptedJwk);
	    	    encryptingJwe.setKey(new PbkdfKey("s3rv1c3h3r0"));
	    	    String payload = encryptingJwe.getPayload();
	    	    PublicJsonWebKey publicJsonWebKey = PublicJsonWebKey.Factory.newPublicJwk(payload);
	    	    // share the public part with whomever/whatever needs to verify the signatures 
	    	    //System.out.println(publicJsonWebKey.toJson(JsonWebKey.OutputControlLevel.PUBLIC_ONLY));
	    	System.out.println("publicJsonWebKey.getKey(): "+publicJsonWebKey.getKey());
	    	System.out.println("publicJsonWebKey.getPrivateKey(): "+publicJsonWebKey.getPrivateKey());*/
	    	    
			if(jweEncryptedJwk==null || jweEncryptedJwk.equals("") || jweEncryptedJwk.length()==0){
				System.out.println("before getJweEncryptedJwk ");
				JWT.getJweEncryptedJwk(request);
			}
		    // Create the Claims, which will be the content of the JWT
		    JwtClaims claims = new JwtClaims();
		    claims.setIssuer("SHero");  // who creates the token and signs it
		    claims.setAudience("Audience"); // to whom the token is intended to be sent
		    claims.setExpirationTimeMinutesInTheFuture(60); // time when the token will expire (60 minutes from now)
		    claims.setGeneratedJwtId(); // a unique identifier for the token
		    claims.setIssuedAtToNow();  // when the token was issued/created (now)
		    claims.setNotBeforeMinutesInThePast(2); // time before which the token is not yet valid (2 minutes ago)
		    claims.setSubject("subject"); // the subject/principal is whom the token is about
		    claims.setClaim("userid",lSHUserId); // additional claims/attributes about the subject can be added		    
		    
		    System.out.println("after claims ");
		    
		    JsonWebSignature jws = new JsonWebSignature();		   
		    jws.setPayload(claims.toJson());		    
		    jws.setHeader("typ", "JWT");		    
		    jws.setKey(publicJsonWebKey.getPrivateKey());		    
		    jws.setAlgorithmHeaderValue(AlgorithmIdentifiers.RSA_USING_SHA256);		
		    System.out.println("after jws ");
		    jwt = jws.getCompactSerialization();  
		    System.out.println("after jwt: "+jwt);
				    
		} catch (JoseException e) {
			// TODO Auto-generated catch block
			e.getCause().printStackTrace();			
			e.printStackTrace();
			System.out.println(e.getMessage());	
		}catch (Exception e)
        {
			e.getCause().printStackTrace();			
        	e.printStackTrace();
        	System.out.println(e.getMessage());	           
        }
		return jwt;
	}
	
	public static String[] validateJWT(HttpServletRequest httpServletRequest, String userLanguage, String strJWT){		
		String isValid = "false", strRenewedJWT=strJWT, isRenewed="false";
		String[] returnArray = new String[5];
		Connection connection =null;
		try{			
			if(jweEncryptedJwk==null || jweEncryptedJwk.equals("") || jweEncryptedJwk.length()==0){
				JWT.getJweEncryptedJwk(httpServletRequest);
			}
			
			JwtConsumer jwtConsumer = new JwtConsumerBuilder()
		    .setRequireExpirationTime() // the JWT must have an expiration time
		    .setAllowedClockSkewInSeconds(30) // allow some leeway in validating time based claims to account for clock skew
		    .setRequireSubject() // the JWT must have a subject claim
		    .setExpectedIssuer("SHero") // whom the JWT needs to have been issued by
		    .setExpectedAudience("Audience") // to whom the JWT is intended for
		    //.setVerificationKey(rsaJsonWebKey.getKey()) // verify the signature with the public key
		    .setVerificationKey(publicJsonWebKey.getKey()) 
		    .build(); // create the JwtConsumer instance
			
			JwtClaims jwtClaims = null;
			try
			{
				jwtClaims = jwtConsumer.processToClaims(strJWT);
				long lUserId = 0;				
				try{
					//System.out.println("jwtClaims.getClaimValue(userid): "+""+jwtClaims.getClaimValue("userid"));
					lUserId = Long.parseLong(""+jwtClaims.getClaimValue("userid"));					
				}catch(Exception e1){
					lUserId = 0;
					e1.printStackTrace();
				}
				System.out.println("lUserId: "+lUserId);
				if(lUserId != 0){
					try{
						ApplicationContext context = RequestContextUtils.getWebApplicationContext(httpServletRequest);
				    	PoolingDataSource<PoolableConnection> dataSource = (PoolingDataSource<PoolableConnection>) context.getBean("dataSource");
				    	connection = dataSource.getConnection();
						SHSurveyUser shSurveyUser = new SHSurveyUser(httpServletRequest, userLanguage, lUserId); 
						httpServletRequest.getSession().setAttribute("shSurveyUser", shSurveyUser);
						UserActivityLog userLog = new UserActivityLog();						
						userLog.insertRecord(httpServletRequest, connection,  UserActivityLog.ACTIVITY_SURVEY_TABLE ,UserActivityLog.ACTIVITY_WEB_LOGIN, lUserId, 0, "");

						
					}catch(Exception e1) {
						e1.printStackTrace();
			        	System.out.println(e1.getMessage());	
					}finally {
						Utils.closeQuietly(connection);
					}
				}
				System.out.println("JWT validation succeeded! " + jwtClaims);
				isValid = "true";
				isRenewed="false";
				strRenewedJWT = strJWT;
			}
			catch (InvalidJwtException e)
			{
				// InvalidJwtException will be thrown, if the JWT failed processing or validation in anyway.
				// Hopefully with meaningful explanations(s) about what went wrong.
				System.out.println("Invalid JWT! " + e);
				isValid = "false";
				if(e.getMessage().indexOf("on or after the Expiration Time")>0){
					//System.out.println("JWT Invalidated because of expiry");
					if(e.getMessage().indexOf("userid")>0){
						String strUserId = e.getMessage().substring(e.getMessage().indexOf("userid")+8);						
						int iend = strUserId.indexOf("}");
						if (iend != -1) 
							strUserId= strUserId.substring(0 , iend); 						
						//System.out.println("strUserId: "+strUserId);
						long lUserId = 0;
						try{
							lUserId = Long.parseLong(strUserId);
						}catch(Exception e1){
							lUserId = 0;
						}
						if(lUserId != 0){
							try{								
								ApplicationContext context = RequestContextUtils.getWebApplicationContext(httpServletRequest);
						    	PoolingDataSource<PoolableConnection> dataSource = (PoolingDataSource<PoolableConnection>) context.getBean("dataSource");
						    	connection = dataSource.getConnection();
								SHSurveyUser shSurveyUser = new SHSurveyUser(httpServletRequest, userLanguage, lUserId); 
								httpServletRequest.getSession().setAttribute("shSurveyUser", shSurveyUser);
								UserActivityLog userLog = new UserActivityLog();
								//userLog.insertRecord(httpServletRequest, connection, "login", lUserId, 0, "");
								userLog.insertRecord(httpServletRequest, connection,  UserActivityLog.ACTIVITY_SURVEY_TABLE ,UserActivityLog.ACTIVITY_WEB_LOGIN, lUserId, 0, "");

								try{
									strRenewedJWT = JWT.generateJWT(httpServletRequest, shSurveyUser.getId());
									isValid = "true";
									isRenewed = "true";
								}catch (Exception ex) {			 	
									System.err.println("An InvocationTargetException was caught!");			 	
									Throwable cause = ex.getCause();			 	       
									System.out.format("Invocation of %s failed because of: %s%n", "methodName", cause.getMessage());			 	
								}
								connection.commit();
								connection.close();
							} catch(Exception e1) {
								e1.printStackTrace();
					        	System.out.println(e1.getMessage());	
							}finally {
								Utils.closeQuietly(connection);
							}
						}
						
					}								        
				}
				e.printStackTrace();
				
			}
		}catch (Exception e)
        {
			e.getCause().printStackTrace();			
        	e.printStackTrace();
        	System.out.println(e.getMessage());	           
        }
		returnArray[0] = isValid;
		returnArray[1] = isRenewed;
		returnArray[2] = strRenewedJWT;
		//return isValid + strRenewedJWT;
		return returnArray;
	}	
	
}


