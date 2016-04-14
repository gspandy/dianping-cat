package com.dianping.cat.build;

import java.util.ArrayList;
import java.util.List;

import org.unidal.dal.jdbc.datasource.JdbcDataSourceDescriptorManager;
import org.unidal.initialization.Module;
import org.unidal.lookup.configuration.AbstractResourceConfigurator;
import org.unidal.lookup.configuration.Component;

import com.dianping.cat.CatCoreModule;
import com.dianping.cat.analysis.DefaultMessageAnalyzerManager;
import com.dianping.cat.analysis.DefaultMessageHandler;
import com.dianping.cat.analysis.MessageAnalyzerManager;
import com.dianping.cat.analysis.MessageConsumer;
import com.dianping.cat.analysis.MessageHandler;
import com.dianping.cat.analysis.RealtimeConsumer;
import com.dianping.cat.analysis.TcpSocketReceiver;
import com.dianping.cat.config.app.AppCommandConfigManager;
import com.dianping.cat.config.app.AppCommandGroupConfigManager;
import com.dianping.cat.config.app.AppSpeedConfigManager;
import com.dianping.cat.config.app.CrashLogConfigManager;
import com.dianping.cat.config.app.MobileConfigManager;
import com.dianping.cat.config.app.command.CommandFormatConfigManager;
import com.dianping.cat.config.app.command.CommandFormatHandler;
import com.dianping.cat.config.app.command.DefaultCommandFormatlHandler;
import com.dianping.cat.config.business.BusinessConfigManager;
import com.dianping.cat.config.content.ContentFetcher;
import com.dianping.cat.config.content.LocalResourceContentFetcher;
import com.dianping.cat.config.sample.SampleConfigManager;
import com.dianping.cat.config.server.ServerConfigManager;
import com.dianping.cat.config.server.ServerFilterConfigManager;
import com.dianping.cat.config.web.WebConfigManager;
import com.dianping.cat.config.web.WebSpeedConfigManager;
import com.dianping.cat.config.web.js.AggregationHandler;
import com.dianping.cat.config.web.js.DefaultAggregationHandler;
import com.dianping.cat.config.web.url.DefaultUrlPatternHandler;
import com.dianping.cat.config.web.url.UrlPatternConfigManager;
import com.dianping.cat.config.web.url.UrlPatternHandler;
import com.dianping.cat.core.config.BusinessConfigDao;
import com.dianping.cat.core.config.ConfigDao;
import com.dianping.cat.core.dal.HostinfoDao;
import com.dianping.cat.core.dal.TaskDao;
import com.dianping.cat.message.DefaultPathBuilder;
import com.dianping.cat.message.PathBuilder;
import com.dianping.cat.message.spi.MessageCodec;
import com.dianping.cat.message.spi.codec.PlainTextMessageCodec;
import com.dianping.cat.report.DomainValidator;
import com.dianping.cat.report.HourlyReportTableProvider;
import com.dianping.cat.service.HostinfoService;
import com.dianping.cat.service.IpService;
import com.dianping.cat.statistic.ServerStatisticManager;
import com.dianping.cat.task.TaskManager;

public class ComponentsConfigurator extends AbstractResourceConfigurator {
	public static void main(String[] args) {
		generatePlexusComponentsXmlFile(new ComponentsConfigurator());
	}

	@Override
	public List<Component> defineComponents() {
		List<Component> all = new ArrayList<Component>();

		all.add(C(MessageConsumer.class, RealtimeConsumer.class) //
		      .req(MessageAnalyzerManager.class, ServerStatisticManager.class));

		all.add(C(ServerConfigManager.class));
		all.add(C(HostinfoService.class).req(HostinfoDao.class, ServerConfigManager.class));
		all.add(C(IpService.class));
		all.add(C(TaskManager.class).req(TaskDao.class));
		all.add(C(ServerStatisticManager.class));
		all.add(C(DomainValidator.class));
		all.add(C(ContentFetcher.class, LocalResourceContentFetcher.class));
		all.add(C(ServerFilterConfigManager.class).req(ConfigDao.class, ContentFetcher.class));

		all.add(C(PathBuilder.class, DefaultPathBuilder.class));

		all.add(C(MessageAnalyzerManager.class, DefaultMessageAnalyzerManager.class));

		all.add(C(TcpSocketReceiver.class).req(ServerConfigManager.class).req(ServerStatisticManager.class)
		      .req(MessageCodec.class, PlainTextMessageCodec.ID).req(MessageHandler.class));

		all.add(C(MessageHandler.class, DefaultMessageHandler.class));

		all.add(C(AggregationHandler.class, DefaultAggregationHandler.class));

		all.add(C(CommandFormatHandler.class, DefaultCommandFormatlHandler.class));

		all.add(C(CommandFormatConfigManager.class)
		      .req(ConfigDao.class, ContentFetcher.class, CommandFormatHandler.class));

		all.add(C(SampleConfigManager.class).req(ConfigDao.class, ContentFetcher.class));

		all.add(C(AppCommandConfigManager.class).req(ConfigDao.class, ContentFetcher.class));

		all.add(C(CrashLogConfigManager.class).req(ConfigDao.class, ContentFetcher.class));

		all.add(C(AppCommandGroupConfigManager.class).req(CommandFormatHandler.class, ConfigDao.class,
		      ContentFetcher.class, AppCommandConfigManager.class));

		all.add(C(WebConfigManager.class).req(ConfigDao.class, ContentFetcher.class));

		all.add(C(WebSpeedConfigManager.class).req(ConfigDao.class, ContentFetcher.class));

		all.add(C(AppSpeedConfigManager.class).req(ConfigDao.class, ContentFetcher.class));

		all.add(C(BusinessConfigManager.class).req(BusinessConfigDao.class));

		all.add(C(UrlPatternHandler.class, DefaultUrlPatternHandler.class));

		all.add(C(UrlPatternConfigManager.class).req(ConfigDao.class, UrlPatternHandler.class, ContentFetcher.class));

		all.add(A(MobileConfigManager.class));

		all.add(C(Module.class, CatCoreModule.ID, CatCoreModule.class));
		all.add(A(HourlyReportTableProvider.class));

		// database
		all.add(C(JdbcDataSourceDescriptorManager.class) //
		      .config(E("datasourceFile").value("/data/appdatas/cat/datasources.xml")));

		all.addAll(new CatCoreDatabaseConfigurator().defineComponents());
		all.addAll(new AppDatabaseConfigurator().defineComponents());
		all.addAll(new WebDatabaseConfigurator().defineComponents());

		all.addAll(new CodecComponentConfigurator().defineComponents());
		all.addAll(new StorageComponentConfigurator().defineComponents());

		return all;
	}
}
