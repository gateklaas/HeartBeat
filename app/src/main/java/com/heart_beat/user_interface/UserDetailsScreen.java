package com.heart_beat.user_interface;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.heart_beat.R;
import com.heart_beat.communication.FitbitCommunication;
import com.heart_beat.communication.OAuth2Helper;
import com.heart_beat.other.Constants;
import com.heart_beat.other.Details;
import com.heart_beat.other.HRMath;

public class UserDetailsScreen extends Activity
{
	private SharedPreferences prefs;
	private OAuth2Helper oAuth2Helper;
	private EditText etAge;
	private EditText etHRrest;
	private EditText etHRmax;
	private Button bNext;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_details);

		etAge = (EditText) findViewById(R.id.et_age);
		etHRrest = (EditText) findViewById(R.id.et_hrrest);
		etHRmax = (EditText) findViewById(R.id.et_hrmax);
		bNext = (Button) findViewById(R.id.b_next);

		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		oAuth2Helper = new OAuth2Helper(prefs);

		Details.load(prefs);
		showUserDetails();

		etAge.setOnFocusChangeListener(new View.OnFocusChangeListener()
		{
			@Override
			public void onFocusChange(View v, boolean hasFocus)
			{
				if (!hasFocus)
				{
					Details.age = Integer.parseInt(((EditText) v).getText().toString());
					Details.save(prefs);
				}
			}
		});

		etHRrest.setOnFocusChangeListener(new View.OnFocusChangeListener()
		{
			@Override
			public void onFocusChange(View v, boolean hasFocus)
			{
				if (!hasFocus)
				{
					Details.hrRest = Integer.parseInt(((EditText) v).getText().toString());
					Details.save(prefs);
				}
			}
		});

		etHRmax.setOnFocusChangeListener(new View.OnFocusChangeListener()
		{
			@Override
			public void onFocusChange(View v, boolean hasFocus)
			{
				if (!hasFocus)
				{
					Details.hrMax = Integer.parseInt(((EditText) v).getText().toString());
					Details.save(prefs);
				}
			}
		});

		bNext.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				etAge.clearFocus();
				etHRrest.clearFocus();
				etHRmax.clearFocus();
				startActivity(new Intent().setClass(getBaseContext(), ActivityDetailsScreen.class));
			}
		});

		new AsyncTask<Void, Void, Void>()
		{
			@Override
			protected Void doInBackground(Void... params)
			{
				try
				{
					Details.age = FitbitCommunication.retrieveAge(oAuth2Helper);
					Details.hrMax = (int) Math.round(HRMath.estimateHRmax(Details.age));
				} catch (Exception e) { e.printStackTrace(); }

				try
				{
					Details.hrRest = FitbitCommunication.retrieveHRrest(oAuth2Helper);
				} catch (Exception e) { e.printStackTrace(); }

				return null;
			}

			@Override
			protected void onPostExecute(Void result)
			{
				Details.save(prefs);
				showUserDetails();
			}
		}.execute();
	}

	public void showUserDetails()
	{
		if (Details.age > 0)
		{
			etAge.setText("" + (int) Details.age);
			etHRmax.setText("" + (int) Details.hrMax);
		}

		if (Details.hrRest > 0)
		{
			etHRrest.setText("" + (int) Details.hrRest);
		}
	}
}
