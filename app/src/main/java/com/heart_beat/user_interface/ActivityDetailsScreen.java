package com.heart_beat.user_interface;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.heart_beat.R;
import com.heart_beat.communication.OAuth2Helper;
import com.heart_beat.other.Details;
import com.heart_beat.other.HRMath;

public class ActivityDetailsScreen extends Activity
{
	private SharedPreferences prefs;
	private OAuth2Helper oAuth2Helper;
	private TextView tvIntensity;
	private SeekBar sbIntensity;
	private TextView tvDetails;
	private Button bNext;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_activity_details);

		tvIntensity = (TextView) findViewById(R.id.tv_intensity);
		sbIntensity = (SeekBar) findViewById(R.id.sb_intensity);
		tvDetails = (TextView) findViewById(R.id.tv_details);
		bNext = (Button) findViewById(R.id.b_next);

		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		oAuth2Helper = new OAuth2Helper(prefs);

		Details.load(prefs);

		sbIntensity.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
		{
			boolean first = true;

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
			{
				if (first || fromUser)
				{
					first = false;
					seekBar.setProgress(progress);
					float intensity = (float) progress / seekBar.getMax();
					tvIntensity.setText("Intensity level: " + ((int) (intensity * 100)) + "%");
					Details.intensity = intensity;
					Details.save(prefs);

					int hrTarget = (int) HRMath.estimateHRtarget(Details.hrMax, Details.hrRest, Details.intensity);
					tvDetails.setText("Target heart-rate: " + hrTarget);
				}
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) { }

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) { }
		});

		bNext.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				startActivity(new Intent().setClass(getBaseContext(), MusicPlayerScreen.class));
			}
		});

		sbIntensity.setProgress((int) (Details.intensity * sbIntensity.getMax()));
	}
}
