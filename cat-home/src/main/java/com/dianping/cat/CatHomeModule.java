package com.dianping.cat;

import org.unidal.helper.Threads;
import org.unidal.initialization.AbstractModule;
import org.unidal.initialization.Module;
import org.unidal.initialization.ModuleContext;

import com.dianping.cat.analysis.MessageConsumer;
import com.dianping.cat.analysis.TcpSocketReceiver;
import com.dianping.cat.config.server.ServerConfigManager;
import com.dianping.cat.consumer.CatConsumerModule;
import com.dianping.cat.report.alert.AlarmManager;
import com.dianping.cat.report.task.DefaultTaskConsumer;
import com.dianping.cat.report.task.RemoteServersUpdater;

public class CatHomeModule extends AbstractModule {
	public static final String ID = "cat-home";

	@Override
	protected void execute(ModuleContext ctx) throws Exception {
		ServerConfigManager serverConfigManager = ctx.lookup(ServerConfigManager.class);

		ctx.lookup(MessageConsumer.class);

		if (serverConfigManager.isJobMachine()) {
			DefaultTaskConsumer taskConsumer = ctx.lookup(DefaultTaskConsumer.class);
			RemoteServersUpdater remoteServersupdater = ctx.lookup(RemoteServersUpdater.class);

			Threads.forGroup("cat").start(taskConsumer);
			Threads.forGroup("cat").start(remoteServersupdater);
		}

		AlarmManager alarmManager = ctx.lookup(AlarmManager.class);

		if (serverConfigManager.isAlertMachine()) {
			alarmManager.startAlarm();
		}

		final MessageConsumer consumer = ctx.lookup(MessageConsumer.class);
		Runtime.getRuntime().addShutdownHook(new Thread() {

			@Override
			public void run() {
				consumer.doCheckpoint();
			}
		});
	}

	@Override
	public Module[] getDependencies(ModuleContext ctx) {
		return ctx.getModules(CatConsumerModule.ID);
	}

	@Override
	protected void setup(ModuleContext ctx) throws Exception {
		final TcpSocketReceiver messageReceiver = ctx.lookup(TcpSocketReceiver.class);

		messageReceiver.init();

		Runtime.getRuntime().addShutdownHook(new Thread() {

			@Override
			public void run() {
				messageReceiver.destory();
			}
		});
	}

}
