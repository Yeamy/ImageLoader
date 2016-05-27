package com.yeamy.imageloader;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v4.os.AsyncTaskCompat;
import android.widget.ImageView;

import com.yeamy.utils.MD5;

import java.io.File;

/**
 * Using getInstance() to instance the Loader,<br>
 * then invoke init(context) or init(memorySize, diskPath) to initialize it;<br>
 * using get(...) to load image as remove(imageView) to cancel.<br>
 *
 */
public class ImageLoader {
	private static ImageLoader instance;
	private ImageLoaderImpl impl;
	private float scaleXY = ImageLoaderImpl.NO_SCALE;

	public static ImageLoader getInstance() {
		if (instance == null) {
			instance = new ImageLoader();
		}
		return instance;
	}

	/**
	 * default memory 4MB, default path under app's dir
	 * @param context the loader will not keep it
     */
	public ImageLoader init(Context context) {
		if (impl == null) {
			impl = new ImageLoaderImpl(4 * 1024 * 1024, defaultPath(context));
		}
		return this;
	}

	/**
	 *
	 * @param memorySize memory cache size (unit:byte)
	 * @param diskPath the location where image to cache in disk
     */
	public ImageLoader init(int memorySize, File diskPath) {
		if (impl == null) {
			impl = new ImageLoaderImpl(memorySize, diskPath);
		}
		return this;
	}

	/**
	 * scale the out bitmap
	 * @param scaleXY (0~1F]
     */
	public void setScaleXY(float scaleXY) {
		this.scaleXY = scaleXY;
	}

	public float getScaleXY() {
		return this.scaleXY;
	}

	public boolean isInit() {
		return impl != null;
	}

	public static File defaultPath(Context context) {
		return DiskCache.getDefaultPath(context);
	}

	public File getDiskPath() {
		return (impl == null) ? null : impl.getDiskPath();
	}

	public void get(String uri, ImageView iv) {
		get(uri, iv, this.scaleXY, null);
	}

	public void get(String uri, ImageView iv, Drawable loading) {
		if (impl != null) impl.get(uri, iv, this.scaleXY, loading);
	}

	public void get(String uri, ImageView iv, float scaleXY, Drawable loading) {
		if (impl != null) impl.get(uri, iv, scaleXY, loading);
	}

	/**
	 * @param uri the full http-uri of the image
	 * @return the path of image in disk-cache
     */
	public File getImageFile(String uri) {
		return impl.getImagePath(MD5.get32(uri));
	}

	/**
	 * stop loading image of the imageView
     */
	public void remove(ImageView iv) {
		if (impl != null) impl.remove(iv);
	}

	/**
	 * check the disk cache size now
     */
	public long getDiskCacheSize() {
		return impl == null ? 0 : impl.getDiskCacheSize();
	}

	/**
	 * remove all image in disk cache path
     */
	public void clearDiskCache(ClearDiskCacheListener l) {
		if (impl != null) impl.clearDiskCache(l);
	}

	/** 清除旧版本缓存 */
	@Deprecated
	public void _clearDiskCache(final Context context) {
		AsyncTaskCompat.executeParallel(new AsyncTask<Object, Object, Object>() {
			@Override
			protected Object doInBackground(Object... params) {
				// 旧Loader缓存
				File path = new File(context.getFilesDir(), "com.yeamy.imageloader");// 旧版本loader
				File[] list = path.listFiles();
				for (File file : list)
					file.delete();
				return null;
			}
		});
	}
}