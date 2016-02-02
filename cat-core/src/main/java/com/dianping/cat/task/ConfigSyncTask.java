package com.dianping.cat.task;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

import org.unidal.helper.Threads;
import org.unidal.helper.Threads.Task;

import com.dianping.cat.Cat;
import com.dianping.cat.helper.TimeHelper;
import com.dianping.cat.message.Transaction;

public class ConfigSyncTask implements Task {

	private List<SyncHandler> m_handlers = new ArrayList<SyncHandler>();

	private static ConfigSyncTask m_instance = new ConfigSyncTask();

	private static ExecutorService s_threadPool = Threads.forPool().getFixedThreadPool("Cat-ConfigSyncTask", 5);

	private static final long DURATION = TimeHelper.ONE_MINUTE;

	public static ConfigSyncTask getInstance() {
		return m_instance;
	}

	@Override
	public String getName() {
		return "config-sync-task";
	}

	public void register(SyncHandler handler) {
		synchronized (this) {
			m_handlers.add(handler);
		}
	}

	@Override
	public void run() {
		boolean active = TimeHelper.sleepToNextMinute();

		while (active) {
			long current = System.currentTimeMillis();
			
			for (final SyncHandler handler : m_handlers) {
				s_threadPool.submit(new Runnable() {

					@Override
					public void run() {
						final Transaction t = Cat.newTransaction("SyncConfig", handler.getName());

						try {
							handler.handle();
							t.setStatus(Transaction.SUCCESS);
						} catch (Exception e) {
							t.setStatus(e);
							Cat.logError(e);
						} finally {
							t.complete();
						}
					}
				});
			}

			long duration = System.currentTimeMillis() - current;

			try {
				if (duration < DURATION) {
					Thread.sleep(DURATION - duration);
				}
			} catch (InterruptedException e) {
				active = false;
			}
		}
	}

	@Override
	public void shutdown() {
	}

	public static interface SyncHandler {

		public String getName();

		public void handle() throws Exception;

	}

}
