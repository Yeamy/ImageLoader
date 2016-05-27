package com.yeamy.imageloader;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.yeamy.imageloader.ExecuteTask.FROM;
import com.yeamy.utils.MD5;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

class ImageLoaderImpl {
	final static float NO_SCALE = 1F;
	private MemoryCache memoryCache;
	private File diskPath;
	private final ArrayList<TaskBean> taskList = new ArrayList<>();

	ImageLoaderImpl(int memorySize, File diskPath) {
		this.memoryCache = new MemoryCache(memorySize);
		this.diskPath = diskPath;
	}

	void setDiskPath(File diskPath) {
		this.diskPath = diskPath;
	}

	File getDiskPath() {
		return this.diskPath;
	}

	File getImagePath(String key) {
		return new File(getDiskPath(), key + ".img");
	}

	void get(String uri, ImageView iv, float scaleXY, Drawable loading) {
		String key = MD5.get32(uri);
		if (key == null){
			return;
		}
		Bitmap bitmap = this.memoryCache.get(key);
		if (bitmap != null) { // no need to do task
			iv.setImageBitmap(bitmap);
		} else {//begin async task
			memoryCache.remove(key); // maybe memory-cache keeping a null-reference, clear it
			iv.setImageDrawable(loading);
			TaskBean bean = new TaskBean(uri, iv, getImagePath(key));
			bean.setKey(key);
			bean.setScaleXY(scaleXY);
			ThreadExecutor.executePool(new ExecuteTask(this, bean));
		}
	}

	void remove(ImageView iv) {
		if (iv == null) {
			return;
		}
		synchronized (this.taskList) {
			Iterator<TaskBean> iterator = this.taskList.iterator();
			while (iterator.hasNext()) {
				TaskBean bean = iterator.next();
				if (iv.equals(bean.getImageView())) {
					iterator.remove();
					bean.disableCallback();
				}
			}
		}
	}

	void addCache(String key, Bitmap bitmap) {
		if (bitmap != null)
			this.memoryCache.put(key, bitmap);
	}

	void clearTask(TaskBean bean) {
		synchronized (taskList) {
			final List<TaskBean> list = this.taskList;
			ImageView beanIv = bean.getImageView();
			for (TaskBean tmp : list) {
				ImageView tmpIv = tmp.getImageView();
				if (tmpIv == null) {
					if (tmp.key.equals(bean.key)) {
						list.remove(tmp);
						break;
					}
				} else if (tmpIv.equals(beanIv)) {
					if (tmp.key.equals(bean.key)) {
						list.remove(tmp);
						break;
					}
					tmp.disableCallback();

					break;
				}
			}
			list.add(bean);
		}
	}

	void getFromDisk(ExecuteTask task) {
		TaskBean bean = task.getBean();
		Bitmap bm = DiskCache.get(bean);
		if (bm != null) {
			bm = createScaledBitmap(bm, bean.getScaleXY());
//			bean.setCompleted();
		}
		notifyComplete(bean, bm);
		// check for update
		task.setFrom(FROM.HTTP);
		ThreadExecutor.executeSingle(task);
	}

	void getFromHttp(ExecuteTask task) {
		Bitmap bm = null;
		TaskBean bean = task.getBean();
		switch (HttpGetter.get(bean)) {
		case OK:
			bm = DiskCache.get(bean);
			if (bm != null) {
				bm = createScaledBitmap(bm, bean.getScaleXY());
			}
			break;
		case NEW:
		case FAIL:
		default:
		}
		notifyComplete(bean, bm);
	}

	private Bitmap createScaledBitmap(Bitmap src, float scaleXY) {
		if (scaleXY >= NO_SCALE) {
			return src;
		}
		int dstWidth = (int) (src.getWidth() * scaleXY);
		int dstHeight = (int) (src.getHeight() * scaleXY);
		return Bitmap.createScaledBitmap(src, dstWidth, dstHeight, false);
	}

	void notifyComplete(TaskBean bean, Bitmap bm) {
		this.taskList.remove(bean);
		if (bm != null) {
			addCache(bean.key, bm);
			bean.postCallback(bm);
		}
	}

	long getDiskCacheSize() {
		return DiskCache.sizeOf(this.diskPath);
	}

	void clearDiskCache(ClearDiskCacheListener l) {
		ThreadExecutor.executePool(new ClearDiskCacheTask(this.diskPath, l));
	}
}