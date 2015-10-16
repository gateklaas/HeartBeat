package com.heart_beat.music;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import java.util.LinkedList;
import java.util.List;

public class MusicHelper
{
	public static final List<Music> MUSIC_LIST = new LinkedList<Music>();

	public static void loadAll(Activity activity)
	{
		load(activity, MediaStore.Audio.Media.INTERNAL_CONTENT_URI, MUSIC_LIST);
		load(activity, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, MUSIC_LIST);
	}

	public static Music find(String path)
	{
		for (Music music : MUSIC_LIST)
		{
			if (music.PATH.equals(path))
			{
				return music;
			}
		}
		return null;
	}

	/**
	 * Load music from a search path into a music list
	 */
	private static void load(Activity activity, Uri searchPath, List<Music> musicList)
	{
		String[] projection = {MediaStore.Audio.Media.DATA};
		String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";
		Cursor cursor = activity.getContentResolver().query(searchPath, projection, selection, null, null);

		if (cursor != null)
		{
			while (cursor.moveToNext())
			{
				Music music = new Music(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA)));
				if (!musicList.contains(music))
				{
					musicList.add(music);
					music.loadDetails(activity);
				}
			}
			cursor.close();
		}
	}

	public static void setOnLoadedListener(final OnLoadedListener listener)
	{
		if (listener != null)
		{
			if (Music.loader.isShutdown())
			{listener.onLoaded(); }
			else
			{
				Music.loader.execute(new Runnable()
				{
					@Override
					public void run() { listener.onLoaded(); }
				});
			}
		}
	}


	public static interface OnLoadedListener
	{
		void onLoaded();
	}
}
