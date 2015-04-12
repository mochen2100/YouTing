package com.example.cacheplayer;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import android.os.Environment;
import android.util.Log;

/**
 * Tools
 */
public class Utils
{
	private static final String TAG = "Utils";

	static protected String getSubString(String source, String startStr,
			String endStr)
	{
		int startIndex = source.indexOf(startStr) + startStr.length();
		int endIndex = source.indexOf(endStr, startIndex);
		return source.substring(startIndex, endIndex);
	}

	/**
	 * Get a file folder, sorted by date, from the old to the new
	 * 
	 * @param dirPath
	 * @return
	 */
	static private List<File> getFilesSortByDate(String dirPath)
	{
		List<File> result = new ArrayList<File>();
		File dir = new File(dirPath);
		File[] files = dir.listFiles();
		if (files == null || files.length == 0) return result;
		Arrays.sort(files, new Comparator<File>()
		{
			public int compare(File f1, File f2)
			{
				return Long.valueOf(f1.lastModified()).compareTo(
						f2.lastModified());
			}
		});
		for (int i = 0; i < files.length; i++)
		{
			result.add(files[i]);
		}
		return result;
	}

	/**
	 * Remove files
	 * 
	 * @param dirPath cache file folder path
	 * @param maxnum maximum number of cached files
	 * @param maxSize
	 * @param pref
	 * @param deltemp
	 * @param string
	 */
	static protected void asynRemoveBufferFile(final String dirPath,
			final int maxnum, final long maxSize, final boolean deltemp,
			final String pref, final String current)
	{
		new Thread()
		{
			public void run()
			{
				long freespace = Environment.getExternalStorageDirectory()
						.getFreeSpace() - 200000000; // pessimist
				long maxSize1 = freespace > maxSize ? maxSize : freespace;
				List<File> lstBufferFile = Utils.getFilesSortByDate(dirPath);
				Log.v(TAG,"freespace:"+freespace/1024/1024);
				Log.v(TAG,"lstBufferFile:"+lstBufferFile.size());
				if (deltemp == true)
				{
					for (int i = 0; i < lstBufferFile.size(); i++)
					{
						if ((lstBufferFile.get(i).getName().endsWith(pref))
								&& (!lstBufferFile.get(i).getPath().equals(current)))
						{
							lstBufferFile.get(i).delete();
							lstBufferFile.remove(i);
						}
					}
				}
				while (lstBufferFile.size() > maxnum)
				{
					lstBufferFile.get(0).delete();
					lstBufferFile.remove(0);
				}
				long size = maxSize1 + 1;
				while (size > maxSize1)
				{
					size = 0;
					for (int i = 0; i < lstBufferFile.size(); i++)
					{
						size = size + lstBufferFile.get(i).length();
					}
					if ((size > maxSize1) && (lstBufferFile.size() > 1))
					{
						lstBufferFile.get(0).delete();
						lstBufferFile.remove(0);
					}
				}
			}
		}.start();
	}

	public static String getExceptionMessage(Exception ex)
	{
		String result = "";
		StackTraceElement[] stes = ex.getStackTrace();
		for (int i = 0; i < stes.length; i++)
		{
			result = result + stes[i].getClassName() + "."
					+ stes[i].getMethodName() + "  " + stes[i].getLineNumber()
					+ "line" + "\r\n";
		}
		return result;
	}
}
