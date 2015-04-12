package com.example.util;

import java.util.Locale;

public class TimeHelper {
	// private static final String TAG = TimeHelper.class.getSimpleName();

	/**
	 * �������ĺ�����ת����00:00:00��ʽ���ַ���
	 * 
	 * @param milliseconds
	 *            ��ת���ĺ�����
	 * */
	public static String milliSecondsToFormatTimeString(long milliseconds) {
		String finalTimerString = "";
		int hours, minutes, seconds;

		hours = (int) (milliseconds / (1000 * 60 * 60));
		minutes = (int) (milliseconds % (1000 * 60 * 60)) / (1000 * 60);
		seconds = (int) ((milliseconds % (1000 * 60 * 60)) % (1000 * 60) / 1000);

		if (hours > 0) {
			finalTimerString = String.format(Locale.getDefault(),
					"%02d%02d:%02d", hours, minutes, seconds);
		} else {
			finalTimerString = String.format(Locale.getDefault(), "%02d:%02d",
					minutes, seconds);
		}
		// Log.d(TAG, "milliseconds=" + milliseconds + "\t finalTimerString=" +
		// finalTimerString);
		return finalTimerString;
	}
}
