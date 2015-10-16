package com.heart_beat.user_interface;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.heart_beat.R;
import com.heart_beat.music.MusicHelper;

public class SplashScreen extends Activity
{
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		MusicHelper.loadAll(this);

		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				try {Thread.sleep(1000);} catch (InterruptedException e) {}

				finish();
				startActivity(new Intent().setClass(getBaseContext(), OAuth2Screen.class));
			}
		}).start();
	}
}
