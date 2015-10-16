package com.heart_beat.communication;

import android.util.Log;

import com.heart_beat.other.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class FitbitCommunication
{
	public static int retrieveAge(OAuth2Helper helper) throws Exception
	{
		String url = FitbitParams.API_URL + "/profile.json";
		String response = helper.getAPIresponse(url);
		return new JSONObject(response).getJSONObject("user").getInt("age");
	}

	public static int retrieveHRrest(OAuth2Helper helper) throws Exception
	{
		int restingHeartRate = 0;
		String url = FitbitParams.API_URL + "/activities/heart/date/today/1m.json";
		String response = helper.getAPIresponse(url);

		JSONObject jObject = new JSONObject(response);
		JSONArray jArray = jObject.getJSONArray("activities-heart");

		for (int i = 0; i < jArray.length(); i++)
		{
			JSONObject values = jArray.getJSONObject(i).getJSONObject("value");

			try
			{
				restingHeartRate = values.getInt("restingHeartRate");
			} catch (JSONException e) {/* ignore */}
		}

		if (restingHeartRate == 0)
		{ throw new Exception("Resting heart-rate not found"); }
		else
		{ return restingHeartRate; }
	}

	public static int retrieveHR(OAuth2Helper helper, long getTime) throws Exception
	{
		SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
		String startTime = timeFormat.format(new Date(getTime - 30000));
		String endTime = timeFormat.format(new Date(getTime - 5000));

		Log.i(Constants.TAG, "startTime: " + startTime + " endTime: " + endTime);

		String url = FitbitParams.API_URL + "/activities/heart/date/today/1d/1sec/time/" + startTime + "/" + endTime + ".json";
		String response = helper.getAPIresponse(url);

		JSONArray jArray = new JSONObject(response).getJSONObject("activities-heart-intraday").getJSONArray("dataset");

		long minimum = 100000;
		int heartRate = 0;
		for (int i = 0; i < jArray.length(); i++)
		{
			JSONObject jObject = jArray.getJSONObject(i);
			long diff = Math.abs(timeFormat.parse(jObject.getString("time")).getTime() - getTime);
			if (minimum > diff)
			{
				minimum = diff;
				heartRate = jObject.getInt("value");
			}
		}

		return heartRate;
	}
}
