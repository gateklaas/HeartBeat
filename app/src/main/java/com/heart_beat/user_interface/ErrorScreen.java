package com.heart_beat.user_interface;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.heart_beat.R;

public class ErrorScreen extends Activity
{
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_error);

		String error = getIntent().getExtras().getString("error");
		if (error != null)
		{
			TextView txtError = (TextView) findViewById(R.id.tv_error);
			txtError.setText("An error occurred:\n" + error);
		}
	}
}