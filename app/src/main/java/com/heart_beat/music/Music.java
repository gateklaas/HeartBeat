package com.heart_beat.music;

import android.app.Activity;
import android.media.MediaPlayer;
import android.util.Log;

import com.heart_beat.other.Constants;
import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Music
{
	public static ExecutorService loader = Executors.newFixedThreadPool(8); // background loader
	private static MediaPlayer mp;

	public final String PATH;
	public String title;
	public String artist;
	public String genre;
	public int tempo;

	private OnLoadedListener onLoadedListener;
	private Object lock = new Object();

	public Music(String path)
	{
		PATH = path;
	}

	public boolean play(MediaPlayer.OnCompletionListener listener)
	{
		if (mp != null)
		{
			if (mp.isPlaying())
			{
				mp.pause();
				return false;
			}
			else
			{
				mp.start();
				return true;
			}
		}

		try
		{
			mp = new MediaPlayer();
			mp.setDataSource(PATH);
			mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener()
			{
				@Override
				public void onPrepared(MediaPlayer mp)
				{
					mp.start();
				}
			});
			mp.prepareAsync();
			mp.setOnCompletionListener(listener);
			return true;
		} catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}
	}

	public static boolean isPlaying()
	{
		return mp != null && mp.isPlaying();
	}

	public static void stop()
	{
		if (mp != null)
		{
			mp.stop();
			mp.release();
			mp = null;
		}
	}

	/**
	 * Load music details, such as title, artist, genre and tempo, in the background.
	 */
	public Music loadDetails(final Activity activity)
	{
		loader.execute(new Runnable()
		{
			@Override
			public void run()
			{
				synchronized (lock)
				{
					try
					{
						Mp3File mp3file = new Mp3File(PATH);

						if (!mp3file.hasId3v2Tag())
						{
							throw new UnsupportedTagException("No Id3v2Tag found");
						}

						ID3v2 id3Tag = mp3file.getId3v2Tag();
						title = id3Tag.getTitle();
						artist = id3Tag.getArtist();
						genre = id3Tag.getGenreDescription();
						tempo = id3Tag.getBPM();

						if (title == null || artist == null || genre == null || tempo == -1)
						{
							throw new UnsupportedTagException("Missing vital tag");
						}

						title = title.trim();
						artist = artist.trim();
						genre = genre.trim();

						if (onLoadedListener != null)
						{
							onLoadedListener.onLoaded(Music.this);
						}

						Log.d(Constants.TAG, "Loaded: " + title + ", " + artist + ", " + genre + ", " + tempo);
					} catch (Exception e)
					{
						Log.e(Constants.TAG, "Error while loading: (" + artist + ", " + title + ", " + genre + ", " + tempo + "): " + e.getMessage());
						activity.runOnUiThread(new Runnable()
						{
							@Override
							public void run()
							{
								MusicHelper.MUSIC_LIST.remove(Music.this);
							}
						});
					}
				}
			}
		});

		return this;
	}

	public void setOnLoadedListener(OnLoadedListener listener)
	{
		synchronized (lock)
		{
			onLoadedListener = listener;

			if (title != null)
			{ listener.onLoaded(this);}
		}
	}

	public static interface OnLoadedListener
	{
		void onLoaded(Music music);
	}
}
