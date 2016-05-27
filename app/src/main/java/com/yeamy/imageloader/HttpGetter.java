package com.yeamy.imageloader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class HttpGetter {
	public static RESULT get(TaskBean bean) {
		try {
			return get(bean.uri, bean.getFile());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return RESULT.FAIL;
	}

	private static RESULT get(String url, File path) throws IOException {
		URL realUrl = new URL(url);
		URLConnection connection = realUrl.openConnection();
		connection.setConnectTimeout(3000);
		connection.connect();
		int code = ((HttpURLConnection) connection).getResponseCode();
		long lm = connection.getLastModified();
		switch (code) {
		case 200:
			if ((path.exists()) && (path.lastModified() == lm) && (path.length() == connection.getContentLength())) {
				return RESULT.NEW;
			}
			break;
		default:
			return RESULT.FAIL;
		}
		InputStream in = connection.getInputStream();
		FileOutputStream out = new FileOutputStream(path);
		byte[] buf = new byte[256];
		int l;
		while ((l = in.read(buf)) > -1) {
			out.write(buf, 0, l);
		}
		in.close();
		out.flush();
		out.close();
		path.setLastModified(lm);
		return RESULT.OK;
	}

	public enum RESULT {
		/** has file data */
		OK,
		/** newest, no update */
		NEW,
		/** otherwise */
		FAIL;
	}
}