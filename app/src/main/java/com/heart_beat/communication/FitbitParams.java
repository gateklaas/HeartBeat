package com.heart_beat.communication;

import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.http.BasicAuthentication;
import com.google.api.client.http.HttpExecuteInterceptor;

public class FitbitParams
{
	public static final String CLIENT_KEY = ; // TODO: fill in
	public static final String CLIENT_ID = ; // TODO: fill in
	public static final String CLIENT_SECRET = ; // TODO: fill in
	public static final String AUTHORIZATION_SERVER_ENCODED_URL = "https://www.fitbit.com/oauth2/authorize";
	public static final String TOKEN_SERVER_URL = "https://api.fitbit.com/oauth2/token";
	public static final Credential.AccessMethod ACCESS_METHOD = BearerToken.authorizationHeaderAccessMethod();
	public static final HttpExecuteInterceptor AUTHENTICATION_METHOD = new BasicAuthentication(CLIENT_ID, CLIENT_SECRET);
	public static final String SCOPE = "profile,heartrate";
	public static final String REDIRECT_URI = "http://localhost";
	public static final String USER_ID = "fitbit";
	public static final String API_URL = "https://api.fitbit.com/1/user/-";
}
