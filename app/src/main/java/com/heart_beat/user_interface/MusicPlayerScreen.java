package com.heart_beat.user_interface;

import android.app.Activity;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.heart_beat.R;
import com.heart_beat.communication.OAuth2Helper;
import com.heart_beat.machine_learner.MlHelper;
import com.heart_beat.music.Music;
import com.heart_beat.other.Details;
import com.heart_beat.other.HRMath;
import com.heart_beat.other.Utils;

import java.util.Calendar;

public class MusicPlayerScreen extends Activity implements MediaPlayer.OnCompletionListener
{
	private SharedPreferences prefs;
	private OAuth2Helper oAuth2Helper;
	MlHelper machineLearnerHelper;

	private TextView tvDetails;
	private Button bPlay;
	private Button bthumbsUp;
	private Button bthumbsDown;

	private Music music;
	private long startTime;
	private float hrTarget;
	private int thumbs = 0;
	private int musicCounter = 1;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_music_player);

		tvDetails = (TextView) findViewById(R.id.tv_details);
		bPlay = (Button) findViewById(R.id.b_play);
		bthumbsUp = (Button) findViewById(R.id.b_thumbs_up);
		bthumbsDown = (Button) findViewById(R.id.b_thumbs_down);

		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		Details.load(prefs);
		hrTarget = HRMath.estimateHRtarget(Details.hrMax, Details.hrRest, Details.intensity);

		oAuth2Helper = new OAuth2Helper(prefs);
		machineLearnerHelper = new MlHelper(getBaseContext(), Details.hrRest);

		bPlay.setEnabled(false);
		bPlay.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				findAndPlaySong();
			}
		});
		bthumbsUp.setEnabled(false);
		bthumbsUp.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				thumbs = 1;
				bthumbsUp.setEnabled(false);
				bthumbsDown.setEnabled(false);
			}
		});
		bthumbsDown.setEnabled(false);
		bthumbsDown.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				thumbs = -1;
				bthumbsUp.setEnabled(false);
				bthumbsDown.setEnabled(false);

				onCompletion(null);
			}
		});

		findAndPlaySong();
	}

	public void findAndPlaySong()
	{
		if (music == null)
		{
			bPlay.setEnabled(false);
			bPlay.setText("Play");

			new AsyncTask<Void, Void, Void>()
			{
				@Override
				protected Void doInBackground(Void... params)
				{
					findSong();
					return null;
				}

				@Override
				protected void onPostExecute(Void aVoid) { playSong(); }
			}.execute();
		}
		else
		{
			if (Music.isPlaying())
			{ bPlay.setText("Play"); }
			else
			{ bPlay.setText("Pause"); }
			music.play(this);
		}
	}

	@Override
	public void onCompletion(MediaPlayer mp) // on end of song
	{
		new AsyncTask<Void, Void, Void>()
		{
			@Override
			protected void onPreExecute()
			{
				Music.stop();
			}

			@Override
			protected Void doInBackground(Void... params)
			{
				long endTime = Calendar.getInstance().getTimeInMillis();
				machineLearnerHelper.learnLater(startTime, endTime, hrTarget, thumbs, music);
				if (Utils.isWifiConnected(getBaseContext()))
				{ machineLearnerHelper.learnNow(oAuth2Helper); }
				findSong();
				return null;
			}

			@Override
			protected void onPostExecute(Void aVoid) { playSong(); }
		}.execute();
	}

	public void findSong()
	{
		double hrStart = hrTarget - (hrTarget - Details.hrRest) / musicCounter; // TODO: change
		music = machineLearnerHelper.predict(hrStart, hrTarget);
		startTime = Calendar.getInstance().getTimeInMillis();
	}

	public void playSong()
	{
		thumbs = 0;
		bthumbsUp.setEnabled(true);
		bthumbsDown.setEnabled(true);
		bPlay.setEnabled(true);
		bPlay.setText("Pause");
		tvDetails.setText("Title: " + music.title + "\n" +
				"Artist: " + music.artist + "\n" +
				"Genre: " + music.genre + "\n" +
				"Tempo: " + music.tempo + " BPM\n");
		musicCounter++;
		music.play(this);
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		Music.stop();
	}
}