package com.yeamy.imageloader;

import android.graphics.Bitmap;
import android.widget.ImageView;

import java.io.File;
import java.lang.ref.WeakReference;

class TaskBean {
	public String key;
	public String uri;
	private ViewKeeper ref;
	private File file;
//	boolean completed = false;
	private float scaleXY = 1.0F;

	public TaskBean(String uri, ImageView iv, File file) {
		this.ref = new ViewKeeper(iv);
		this.uri = uri;
		this.file = file;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public ImageView getImageView() {
		return ref.get();
	}

	public void disableCallback() {
		this.ref.clear();
	}
	
	public void setFile(File file) {
		this.file = file;
	}

	public File getFile() {
		return this.file;
	}

//	public boolean isCompleted() {
//		return this.completed;
//	}
//
//	public void setCompleted() {
//		this.completed = true;
//	}

	void postCallback(Bitmap bm) {
		ref.postCallback(bm);
	}

	public float getScaleXY() {
		return scaleXY;
	}

	public void setScaleXY(float scaleXY) {
		this.scaleXY = scaleXY;
	}

	private class ViewKeeper extends WeakReference<ImageView> implements Runnable {
		private Bitmap bm;

		public ViewKeeper(ImageView r) {
			super(r);
		}

		private void postCallback(Bitmap bm) {
			this.bm = bm;
			ImageView iv = get();
			if (iv != null)
				iv.post(this);
		}

		@Override
		public void run() {
			ImageView iv = get();
			if (iv != null)
				iv.setImageBitmap(bm);
//			clear();
			bm = null;
		}

	}
}