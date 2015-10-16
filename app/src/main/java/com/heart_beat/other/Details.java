package com.heart_beat.other;

import android.content.SharedPreferences;
public class Details
{
	public static float age = 0;
	public static float hrRest = 0;
	public static float hrMax = 0;
	public static float intensity = 0;

	public static void save(SharedPreferences prefs)
	{
		SharedPreferences.Editor editor = prefs.edit();
		editor.putFloat("age", age);
		editor.putFloat("HRrest", hrRest);
		editor.putFloat("HRmax", hrMax);
		editor.putFloat("intensity", intensity);
		editor.commit();
	}

	public static void load(SharedPreferences prefs)
	{
		age = Math.max(age, prefs.getFloat("age", 0));
		hrRest = Math.max(hrRest, prefs.getFloat("HRrest", 0));
		hrMax = Math.max(hrMax, prefs.getFloat("HRmax", 0));
		intensity = Math.max(intensity, prefs.getFloat("intensity", 0));
	}
}
