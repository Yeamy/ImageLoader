package com.yeamy.imageloader;

import android.support.annotation.NonNull;

import java.util.ArrayDeque;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * like a copy of AsyncTask, so it will not block by AsyncTask queue
 */
public class ThreadExecutor {
	private static final Executor THREAD_POOL_EXECUTOR = new PoolExecutor();
	private static final Executor SERIAL_EXECUTOR = new SerialExecutor();

	public static void executePool(Runnable command) {
		THREAD_POOL_EXECUTOR.execute(command);
	}

	public static void executeSingle(Runnable command) {
		SERIAL_EXECUTOR.execute(command);
	}

	private static class PoolExecutor extends ThreadPoolExecutor {
		private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
		private static final int CORE_POOL_SIZE = 1;
		private static final int MAXIMUM_POOL_SIZE = CPU_COUNT + 1;
		private static final int KEEP_ALIVE = 1;
		private static final LinkedBlockingQueue<Runnable> sPoolWorkQueue = new LinkedBlockingQueue<Runnable>(128);

		private static final ThreadFactory sThreadFactory = new ThreadFactory() {
			private final AtomicInteger mCount = new AtomicInteger(1);

			public Thread newThread(@NonNull Runnable r) {
				return new Thread(r, "ImageLoader #" + this.mCount.getAndIncrement());
			}
		};

		public PoolExecutor() {
			super(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE, TimeUnit.SECONDS, sPoolWorkQueue, sThreadFactory);
		}
	}

	private static class SerialExecutor implements Executor {
		final ArrayDeque<Runnable> mTasks = new ArrayDeque<>();
		Runnable mActive;

		public synchronized void execute(@NonNull final Runnable r) {
			this.mTasks.offer(new Runnable() {
				public void run() {
					try {
						r.run();
					} finally {
						SerialExecutor.this.scheduleNext();
					}
				}
			});
			if (this.mActive == null)
				scheduleNext();
		}

		protected synchronized void scheduleNext() {
			if ((this.mActive = this.mTasks.poll()) != null)
				ThreadExecutor.THREAD_POOL_EXECUTOR.execute(this.mActive);
		}
	}
}