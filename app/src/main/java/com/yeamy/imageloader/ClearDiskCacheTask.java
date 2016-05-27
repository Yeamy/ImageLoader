package com.yeamy.imageloader;

import java.io.File;

class ClearDiskCacheTask implements Runnable {

	private File path;
	private ClearDiskCacheListener l;

	public ClearDiskCacheTask(File path, ClearDiskCacheListener l) {
		this.path = path;
		this.l = l;
	}

	public void run() {
		DiskCache.clear(this.path);
		if (this.l != null)
			this.l.onDiskCacheCleared();
	}
}