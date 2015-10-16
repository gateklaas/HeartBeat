package com.heart_beat.user_interface;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.heart_beat.R;
import com.heart_beat.communication.FitbitParams;
import com.heart_beat.communication.OAuth2Helper;

import java.io.IOException;
import java.net.URLDecoder;

@SuppressLint("SetJavaScriptEnabled")
public class OAuth2Screen extends Activity
{
	private static final int HANDLING_NEEDED = 0;
	private static final int HANDLING = 1;
	private static final int HANDLED_SUCCESFULLY = 2;
	private static final int HANDLING_ERROR = 3;

	private int handlingState = HANDLING_NEEDED;
	private SharedPreferences prefs;
	private OAuth2Helper oAuth2Helper;
	private WebView webview;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_auth2);
		webview = (WebView) findViewById(R.id.wv_authentication);
		webview.getSettings().setJavaScriptEnabled(true);

		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		oAuth2Helper = new OAuth2Helper(prefs);
		String authorizationUrl = oAuth2Helper.getAuthorizationUrl();

		webview.setWebViewClient(new WebViewClient()
		{
			@Override
			public void onPageFinished(final WebView view, final String url)
			{
				if (url.startsWith(FitbitParams.REDIRECT_URI))
				{
					webview.setVisibility(View.INVISIBLE);
					new ProcessToken().execute(url);
				}
				else
				{
					webview.setVisibility(View.VISIBLE);
				}
			}
		});

		webview.loadUrl(authorizationUrl);
	}

	private class ProcessToken extends AsyncTask<String, Void, String>
	{
		@Override
		protected void onPreExecute()
		{
			if (handlingState == HANDLING_NEEDED)
			{ handlingState = HANDLING; }
			else
			{ cancel(true); }
		}

		@Override
		protected String doInBackground(String... urls)
		{
			String url = urls[0];
			String error = null;

			try
			{
				if (url.contains("code="))
				{
					oAuth2Helper.retrieveAndStoreAccessToken(extractFromUrl(url, "code="));
					handlingState = HANDLED_SUCCESFULLY;
				}
				else if (url.contains("error="))
				{
					throw new Exception(extractFromUrl(url, "error="));
				}
			} catch (Exception e)
			{
				e.printStackTrace();
				error = "Failed to log in.\n" + e.getMessage();
				handlingState = HANDLING_ERROR;
			}

			return error;
		}

		private String extractFromUrl(String url, String tag) throws IOException
		{
			String encodedCode = url.substring(url.indexOf(tag) + tag.length());
			return URLDecoder.decode(encodedCode, "UTF-8");
		}

		@Override
		protected void onPostExecute(String error)
		{
			switch (handlingState)
			{
				case HANDLED_SUCCESFULLY:
					startActivity(new Intent(OAuth2Screen.this, UserDetailsScreen.class));
					finish();
					break;

				case HANDLING_ERROR:
					startActivity(new Intent(OAuth2Screen.this, ErrorScreen.class).putExtra("error", error));
					finish();
					break;

				default:
					break;
			}
		}
	}
}
