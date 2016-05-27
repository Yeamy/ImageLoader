package com.yeamy.imageloader;

import android.graphics.Bitmap;
import android.support.v4.graphics.BitmapCompat;
import android.support.v4.util.LruCache;

public class MemoryCache extends LruCache<String, Bitmap> {
	public MemoryCache(int maxSize) {
		super(maxSize);
	}

	protected int sizeOf(String key, Bitmap value) {
		return BitmapCompat.getAllocationByteCount(value);
	}
}