package com.dianping.cat.task;

import java.util.ArrayList;
import java.util.List;

import org.unidal.helper.Threads.Task;

import com.dianping.cat.Cat;
import com.dianping.cat.helper.TimeHelper;
import com.dianping.cat.message.Transaction;

public class ConfigSyncTask implements Task {

	private static final long DURATION = TimeHelper.ONE_MINUTE;

	private List<SyncHandler> m_handlers = new ArrayList<SyncHandler>();

	private static ConfigSyncTask m_instance = new ConfigSyncTask();

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

			for (SyncHandler handler : m_handlers) {
				Transaction t = Cat.newTransaction("ConfigSynchronize", handler.getName());

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
