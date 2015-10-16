package com.heart_beat.other;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;
public class Utils
{
	public static boolean isWifiConnected(Context context)
	{
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
		{
			Network[] networks = cm.getAllNetworks();
			for (Network network : networks)
			{
				NetworkInfo info = cm.getNetworkInfo(network);
				if (info.getType() == ConnectivityManager.TYPE_WIFI && info.isConnected())
				{
					return true;
				}
			}
			return false;
		}
		else
		{
			NetworkInfo info = cm.getActiveNetworkInfo();
			return info.getType() == ConnectivityManager.TYPE_WIFI && info.isConnected();
		}
	}

	public static int[] toInt(String[] array)
	{
		int[] newArray = new int[array.length];
		for (int i = 0; i < array.length; i++) { newArray[i] = Integer.parseInt(array[i]); }
		return newArray;
	}

	public static int[] toInt(long[] array)
	{
		int[] newArray = new int[array.length];
		for (int i = 0; i < array.length; i++) { newArray[i] = (int) array[i]; }
		return newArray;
	}

	public static int[] toInt(double[] array)
	{
		int[] newArray = new int[array.length];
		for (int i = 0; i < array.length; i++) { newArray[i] = (int) array[i]; }
		return newArray;
	}

	public static double[] toDouble(String[] array)
	{
		double[] newArray = new double[array.length];
		for (int i = 0; i < array.length; i++) { newArray[i] = Double.parseDouble(array[i]); }
		return newArray;
	}

	public static double[] toDouble(long... array)
	{
		double[] newArray = new double[array.length];
		for (int i = 0; i < array.length; i++) { newArray[i] = (double) array[i]; }
		return newArray;
	}

	public static long[] toLong(double... array)
	{
		long[] newArray = new long[array.length];
		for (int i = 0; i < array.length; i++) { newArray[i] = (long) array[i]; }
		return newArray;
	}

	public static long[] toLong(String[] array)
	{
		long[] newArray = new long[array.length];
		for (int i = 0; i < array.length; i++) { newArray[i] = Long.parseLong(array[i]); }
		return newArray;
	}

	public static double[][] toDouble(String[][] array)
	{
		double[][] newArray = new double[array.length][array[0].length];
		for (int i = 0; i < array.length; i++)
		{
			for (int j = 0; j < array[0].length; j++)
			{ newArray[i][j] = Double.parseDouble(array[i][j]); }
		}
		return newArray;
	}

	public static long[][] toLong(double[][] array)
	{
		long[][] newArray = new long[array.length][array[0].length];
		for (int i = 0; i < array.length; i++)
		{
			for (int j = 0; j < array[0].length; j++)
			{ newArray[i][j] = (long) array[i][j]; }
		}
		return newArray;
	}

	public static int[][] toInt(double[][] array)
	{
		int[][] newArray = new int[array.length][array[0].length];
		for (int i = 0; i < array.length; i++)
		{
			for (int j = 0; j < array[0].length; j++)
			{ newArray[i][j] = (int) array[i][j]; }
		}
		return newArray;
	}

	public static double[][] toDouble(long[][] array)
	{
		double[][] newArray = new double[array.length][array[0].length];
		for (int i = 0; i < array.length; i++)
		{
			for (int j = 0; j < array[0].length; j++)
			{ newArray[i][j] = (double) array[i][j]; }
		}
		return newArray;
	}

	public static double[][] flip(double[][] array)
	{
		double[][] newArray = new double[array[0].length][array.length];
		for (int i = 0; i < array.length; i++)
		{ for (int j = 0; j < array[0].length; j++) { newArray[j][i] = array[i][j]; } }
		return newArray;
	}

	public static String toString(double[][] array)
	{
		String string = "{";
		for (int i = 0; i < array.length; i++)
		{
			string += "{";
			for (int j = 0; j < array[i].length; j++)
			{ string += (int) array[i][j] + ","; }
			string += "}\n";
		}
		return string + "}";
	}
}
