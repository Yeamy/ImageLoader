package com.yeamy.imageloader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;

public class DiskCache {

	public static Bitmap get(TaskBean bean) {
		File file = bean.getFile();
		if (file.exists()) {
			return BitmapFactory.decodeFile(file.toString());
		}
		return null;
	}

	public static File getDefaultPath(Context context) {
		String name = DiskCache.class.getPackage().getName();
		File path = context.getDir(name, Context.MODE_PRIVATE);
		if (!path.exists()) {
			path.mkdirs();
		}
		return path;
	}

	public static long sizeOf(File path) {
		File[] list = path.listFiles();
		long size = 0L;
		for (File file : list) {
			size += file.length();
		}
		return size;
	}

	public static void clear(File path) {
		File[] list = path.listFiles();
		for (File file : list)
			file.delete();
	}
}