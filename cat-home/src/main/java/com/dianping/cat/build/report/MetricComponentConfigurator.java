package com.dianping.cat.build.report;

import java.util.ArrayList;
import java.util.List;

import org.unidal.lookup.configuration.AbstractResourceConfigurator;
import org.unidal.lookup.configuration.Component;

import com.dianping.cat.alarm.spi.config.AlertConfigManager;
import com.dianping.cat.alarm.spi.decorator.Decorator;
import com.dianping.cat.alarm.spi.receiver.Contactor;
import com.dianping.cat.config.server.ServerConfigManager;
import com.dianping.cat.consumer.business.BusinessAnalyzer;
import com.dianping.cat.consumer.config.ProductLineConfigManager;
import com.dianping.cat.consumer.metric.MetricAnalyzer;
import com.dianping.cat.report.alert.business.BusinessAlert;
import com.dianping.cat.report.alert.business.BusinessContactor;
import com.dianping.cat.report.alert.business.BusinessDecorator;
import com.dianping.cat.report.alert.business.BusinessRuleConfigManager;
import com.dianping.cat.report.alert.business2.BusinessAlert2;
import com.dianping.cat.report.alert.business2.BusinessContactor2;
import com.dianping.cat.report.alert.business2.BusinessDecorator2;
import com.dianping.cat.report.alert.business2.BusinessReportGroupService;
import com.dianping.cat.report.alert.business2.BusinessRuleConfigManager2;
import com.dianping.cat.report.alert.spi.data.MetricReportGroupService;
import com.dianping.cat.report.alert.summary.AlertSummaryExecutor;
import com.dianping.cat.report.graph.metric.impl.CachedMetricReportServiceImpl;
import com.dianping.cat.report.graph.metric.impl.DataExtractorImpl;
import com.dianping.cat.report.graph.metric.impl.MetricDataFetcherImpl;
import com.dianping.cat.report.page.business.service.BusinessReportService;
import com.dianping.cat.report.page.business.service.CompositeBusinessService;
import com.dianping.cat.report.page.business.service.HistoricalBusinessService;
import com.dianping.cat.report.page.business.service.LocalBusinessService;
import com.dianping.cat.report.page.business.task.BusinessBaselineReportBuilder;
import com.dianping.cat.report.page.business.task.BusinessKeyHelper;
import com.dianping.cat.report.page.business.task.BusinessPointParser;
import com.dianping.cat.report.page.metric.service.CompositeMetricService;
import com.dianping.cat.report.page.metric.service.DefaultBaselineService;
import com.dianping.cat.report.page.metric.service.HistoricalMetricService;
import com.dianping.cat.report.page.metric.service.LocalMetricService;
import com.dianping.cat.report.page.metric.service.MetricReportService;
import com.dianping.cat.report.page.metric.task.BaselineConfigManager;
import com.dianping.cat.report.page.metric.task.DefaultBaselineCreator;
import com.dianping.cat.report.page.metric.task.MetricBaselineReportBuilder;
import com.dianping.cat.report.page.metric.task.MetricPointParser;
import com.dianping.cat.report.page.server.display.LineChartBuilder;
import com.dianping.cat.report.page.server.display.MetricScreenTransformer;
import com.dianping.cat.report.page.server.service.MetricGraphService;
import com.dianping.cat.report.page.server.service.MetricScreenService;
import com.dianping.cat.report.server.RemoteServersManager;
import com.dianping.cat.report.service.ModelService;
import com.dianping.cat.service.ProjectService;

public class MetricComponentConfigurator extends AbstractResourceConfigurator {
	@Override
	public List<Component> defineComponents() {
		List<Component> all = new ArrayList<Component>();

		all.add(A(BusinessRuleConfigManager.class));
		all.add(A(BusinessRuleConfigManager2.class));
		all.add(A(CachedMetricReportServiceImpl.class));

		all.add(A(MetricReportService.class));

		all.add(A(BusinessReportService.class));

		all.add(A(DataExtractorImpl.class));
		all.add(A(MetricDataFetcherImpl.class));

		all.add(A(MetricScreenTransformer.class));
		all.add(A(MetricScreenService.class));
		all.add(A(MetricGraphService.class));

		all.add(A(LineChartBuilder.class));

		all.add(A(MetricReportGroupService.class));
		all.add(A(BusinessReportGroupService.class));

		all.add(A(LocalMetricService.class));
		all.add(C(ModelService.class, "metric-historical", HistoricalMetricService.class) //
		      .req(MetricReportService.class, ServerConfigManager.class));
		all.add(C(ModelService.class, MetricAnalyzer.ID, CompositeMetricService.class) //
		      .req(ServerConfigManager.class, RemoteServersManager.class) //
		      .req(ModelService.class, new String[] { "metric-historical" }, "m_services"));

		all.add(A(LocalBusinessService.class));
		all.add(C(ModelService.class, "business-historical", HistoricalBusinessService.class) //
		      .req(BusinessReportService.class, ServerConfigManager.class));
		all.add(C(ModelService.class, BusinessAnalyzer.ID, CompositeBusinessService.class) //
		      .req(ServerConfigManager.class, RemoteServersManager.class) //
		      .req(ModelService.class, new String[] { "business-historical" }, "m_services"));

		all.add(A(MetricPointParser.class));
		all.add(A(BaselineConfigManager.class));
		all.add(A(BusinessPointParser.class));
		all.add(A(BusinessKeyHelper.class));
		all.add(A(DefaultBaselineCreator.class));
		all.add(A(DefaultBaselineService.class));
		all.add(A(MetricBaselineReportBuilder.class));
		all.add(A(BusinessBaselineReportBuilder.class));

		all.add(C(Contactor.class, BusinessContactor.ID, BusinessContactor.class).req(ProjectService.class,
		      AlertConfigManager.class));
		all.add(C(Contactor.class, BusinessContactor2.ID, BusinessContactor2.class).req(ProjectService.class,
				AlertConfigManager.class));

		all.add(C(Decorator.class, BusinessDecorator.ID, BusinessDecorator.class).req(ProductLineConfigManager.class,
		      AlertSummaryExecutor.class, ProjectService.class));
		all.add(C(Decorator.class, BusinessDecorator2.ID, BusinessDecorator2.class).req(ProjectService.class,
		      AlertSummaryExecutor.class));
		all.add(A(BusinessAlert.class));

		all.add(A(BusinessAlert2.class));

		return all;
	}
}
