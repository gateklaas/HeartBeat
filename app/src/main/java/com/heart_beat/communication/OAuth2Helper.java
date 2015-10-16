package com.heart_beat.communication;

import android.content.SharedPreferences;
import android.util.Log;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.AuthorizationCodeRequestUrl;
import com.google.api.client.auth.oauth2.AuthorizationCodeTokenRequest;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.CredentialStore;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.heart_beat.other.Constants;
import com.heart_beat.other.SharedPreferencesCredentialStore;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * Help for OAuth protocol
 */
public class OAuth2Helper
{
	private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
	private static final JsonFactory JSON_FACTORY = new JacksonFactory();

	private final CredentialStore credentialStore;

	private AuthorizationCodeFlow flow;

	public OAuth2Helper(SharedPreferences sharedPreferences)
	{
		this.credentialStore = new SharedPreferencesCredentialStore(sharedPreferences);

		AuthorizationCodeFlow.Builder builder = new AuthorizationCodeFlow.Builder(FitbitParams.ACCESS_METHOD, HTTP_TRANSPORT, JSON_FACTORY, new GenericUrl(FitbitParams.TOKEN_SERVER_URL), FitbitParams.AUTHENTICATION_METHOD, FitbitParams.CLIENT_ID, FitbitParams.AUTHORIZATION_SERVER_ENCODED_URL);
		builder.setCredentialStore(this.credentialStore);
		this.flow = builder.build();
	}

	public String getAuthorizationUrl()
	{
		AuthorizationCodeRequestUrl request = flow.newAuthorizationUrl();
		request.setRedirectUri(FitbitParams.REDIRECT_URI);
		request.setScopes(convertScopesToString(FitbitParams.SCOPE));
		return request.build();
	}

	public void retrieveAndStoreAccessToken(String authorizationCode) throws IOException
	{
		AuthorizationCodeTokenRequest request = flow.newTokenRequest(authorizationCode);
		request.setRedirectUri(FitbitParams.REDIRECT_URI);
		request.setScopes(convertScopesToString(FitbitParams.SCOPE));
		TokenResponse response = request.execute();
		flow.createAndStoreCredential(response, FitbitParams.USER_ID);
	}

	public String getAPIresponse(String url) throws IOException
	{
		HttpRequestFactory factory = HTTP_TRANSPORT.createRequestFactory(loadCredential());
		HttpRequest request = factory.buildGetRequest(new GenericUrl(url));
		return request.execute().parseAsString();
	}

	public Credential loadCredential() throws IOException
	{
		return flow.loadCredential(FitbitParams.USER_ID);
	}

	public void clearCredentials() throws IOException
	{
		flow.getCredentialStore().delete(FitbitParams.USER_ID, null);
	}

	private Collection<String> convertScopesToString(String scopesConcat)
	{
		String[] scopes = scopesConcat.split(",");
		Collection<String> collection = new ArrayList<String>();
		Collections.addAll(collection, scopes);
		return collection;
	}
}
