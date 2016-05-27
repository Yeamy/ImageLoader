package com.yeamy.imageloader;

/**
 * Loading image here
 */
class ExecuteTask implements Runnable {

	public enum FROM {
		DISK, HTTP
	}

	private ImageLoaderImpl loader;
	private TaskBean bean;
	private FROM from;

	public ExecuteTask(ImageLoaderImpl loader, TaskBean bean) {
		this.loader = loader;
		this.bean = bean;
		this.from = FROM.DISK;
	}

	public TaskBean getBean() {
		return bean;
	}

	/**
	 * where to load the image DISK or HTTP
     */
	public void setFrom(FROM from) {
		this.from = from;
	}

	public void run() {
		switch (from) {
		case DISK:
			loader.clearTask(bean);
			loader.getFromDisk(this);
			break;
		case HTTP:
			loader.getFromHttp(this);
			break;
		}
	}

}