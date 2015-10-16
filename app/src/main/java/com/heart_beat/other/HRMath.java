package com.heart_beat.other;

/**
 * Mathematical formula's regarding the heart-rate
 */
public class HRMath
{
	/**
	 * Uses the Tanaka formula to estimate the maximum heart rate
	 */
	public static float estimateHRmax(float age)
	{
		return 208f - (0.7f * age);
	}

	/**
	 * Uses the Karvonen formula to estimate the target heart rate
	 */
	public static float estimateHRtarget(float HRmax, float HRrest, float intensity)
	{
		return ((HRmax - HRrest) * intensity) + HRrest;
	}
}
