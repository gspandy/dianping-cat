package com.dianping.cat.report.page.browser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.ServletException;

import com.dianping.cat.Cat;
import com.dianping.cat.config.web.WebConfigManager;
import com.dianping.cat.config.web.WebSpeedConfigManager;
import com.dianping.cat.config.web.url.UrlPatternConfigManager;
import com.dianping.cat.configuration.web.speed.entity.Speed;
import com.dianping.cat.configuration.web.speed.entity.Step;
import com.dianping.cat.configuration.web.url.entity.PatternItem;
import com.dianping.cat.mvc.PayloadNormalizer;
import com.dianping.cat.report.ReportPage;
import com.dianping.cat.report.graph.LineChart;
import com.dianping.cat.report.graph.PieChart;
import com.dianping.cat.report.graph.PieChart.Item;
import com.dianping.cat.report.page.browser.display.AjaxDataDetail;
import com.dianping.cat.report.page.browser.display.AjaxDataDetailSorter;
import com.dianping.cat.report.page.browser.display.AjaxDataDisplayInfo;
import com.dianping.cat.report.page.browser.display.JsErrorMsg;
import com.dianping.cat.report.page.browser.display.JsErrorDisplayInfo;
import com.dianping.cat.report.page.browser.display.JsErrorDetailInfo;
import com.dianping.cat.report.page.browser.display.AjaxPieChartDetailInfos;
import com.dianping.cat.report.page.browser.display.AjaxPieChartDetailInfos.PieChartDetailInfo;
import com.dianping.cat.report.page.browser.display.WebSpeedDisplayInfo;
import com.dianping.cat.report.page.browser.service.AjaxDataField;
import com.dianping.cat.report.page.browser.service.AjaxDataQueryEntity;
import com.dianping.cat.report.page.browser.service.AjaxDataService;
import com.dianping.cat.report.page.browser.service.AjaxGraphCreator;
import com.dianping.cat.report.page.browser.service.JsErrorLogService;
import com.dianping.cat.report.page.browser.service.AjaxQueryType;
import com.dianping.cat.report.page.browser.service.SpeedQueryEntity;
import com.dianping.cat.report.page.browser.service.WebSpeedService;
import com.dianping.cat.web.JsErrorLog;

import org.unidal.dal.jdbc.DalException;
import org.unidal.lookup.annotation.Inject;
import org.unidal.tuple.Pair;
import org.unidal.web.mvc.PageHandler;
import org.unidal.web.mvc.annotation.InboundActionMeta;
import org.unidal.web.mvc.annotation.OutboundActionMeta;
import org.unidal.web.mvc.annotation.PayloadMeta;

import com.dianping.cat.config.web.js.Level;
import com.dianping.cat.helper.JsonBuilder;
import com.site.lookup.util.StringUtils;

public class Handler implements PageHandler<Context> {

	private final int LIMIT = 10000;

	@Inject
	private AjaxDataService m_ajaxDataService;

	@Inject
	private AjaxGraphCreator m_graphCreator;

	@Inject
	private JspViewer m_jspViewer;

	@Inject
	private ModuleManager m_moduleManager;

	@Inject
	private PayloadNormalizer m_normalizePayload;

	@Inject
	private UrlPatternConfigManager m_patternManager;

	@Inject
	private WebConfigManager m_webConfigManager;

	@Inject
	private WebSpeedConfigManager m_webSpeedConfigManager;

	@Inject
	private WebSpeedService m_webSpeedService;

	@Inject
	private JsErrorLogService m_jsErrorLogService;

	private JsonBuilder m_jsonBuilder = new JsonBuilder();

	public void addBrowserCount(String browser, Map<String, AtomicInteger> distributions) {
		AtomicInteger count = distributions.get(browser);

		if (count == null) {
			count = new AtomicInteger(1);
			distributions.put(browser, count);
		} else {
			count.incrementAndGet();
		}
	}

	protected Map<String, AjaxDataDetail> buildAjaxComparisonInfo(Payload payload) {
		AjaxDataQueryEntity currentEntity = payload.getQueryEntity1();
		AjaxDataQueryEntity comparisonEntity = payload.getQueryEntity2();
		Map<String, AjaxDataDetail> result = new HashMap<String, AjaxDataDetail>();

		if (currentEntity != null) {
			AjaxDataDetail detail = buildComparisonInfo(currentEntity);

			if (detail != null) {
				result.put("当前值", detail);
			}
		}

		if (comparisonEntity != null) {
			AjaxDataDetail detail = buildComparisonInfo(comparisonEntity);

			if (detail != null) {
				result.put("对比值", detail);
			}
		}

		return result;
	}

	private List<AjaxDataDetail> buildAjaxDataDetails(Payload payload) {
		List<AjaxDataDetail> ajaxDetails = new ArrayList<AjaxDataDetail>();

		try {
			ajaxDetails = m_ajaxDataService.buildAjaxDataDetailInfos(payload.getQueryEntity1(), payload.getGroupByField());
			AjaxQueryType type = AjaxQueryType.findByType(payload.getSort());
			Collections.sort(ajaxDetails, new AjaxDataDetailSorter(type));
		} catch (Exception e) {
			Cat.logError(e);
		}
		return ajaxDetails;
	}

	private LineChart buildAjaxLineChart(Payload payload) {
		AjaxDataQueryEntity entity1 = payload.getQueryEntity1();
		AjaxDataQueryEntity entity2 = payload.getQueryEntity2();
		AjaxQueryType type = AjaxQueryType.findByType(payload.getType());
		LineChart lineChart = new LineChart();

		try {
			lineChart = m_graphCreator.buildLineChart(entity1, entity2, type);
		} catch (Exception e) {
			Cat.logError(e);
		}
		return lineChart;
	}

	private Pair<PieChart, AjaxPieChartDetailInfos> buildAjaxPieChart(Payload payload) {
		try {
			Pair<PieChart, AjaxPieChartDetailInfos> pair = m_graphCreator.buildPieChart(payload.getQueryEntity1(),
			      payload.getGroupByField());
			AjaxPieChartDetailInfos infos = pair.getValue();

			Collections.sort(infos.getDetails(), new Comparator<PieChartDetailInfo>() {
				@Override
				public int compare(PieChartDetailInfo o1, PieChartDetailInfo o2) {
					return (int) (o2.getRequestSum() - o1.getRequestSum());
				}
			});

			return pair;
		} catch (Exception e) {
			Cat.logError(e);
		}
		return null;
	}

	private AjaxDataDetail buildComparisonInfo(AjaxDataQueryEntity entity) {
		AjaxDataDetail appDetail = null;

		try {
			List<AjaxDataDetail> appDetails = m_ajaxDataService.buildAjaxDataDetailInfos(entity, AjaxDataField.CODE);

			if (appDetails.size() >= 1) {
				appDetail = appDetails.iterator().next();
			}
		} catch (Exception e) {
			Cat.logError(e);
		}
		return appDetail;
	}

	public String buildDistributionChart(Map<String, AtomicInteger> distributions) {
		PieChart chart = new PieChart();
		List<Item> items = new ArrayList<Item>();

		for (Entry<String, AtomicInteger> entry : distributions.entrySet()) {
			Item item = new Item();

			item.setNumber(entry.getValue().get()).setTitle(entry.getKey());
			items.add(item);
		}
		chart.addItems(items);

		return chart.getJsonString();
	}

	private void buildSpeedBarCharts(Payload payload, Model model) {
		try {
			Map<String, Speed> speeds = m_webSpeedConfigManager.getSpeeds();
			SpeedQueryEntity queryEntity1 = normalizeSpeedQueryEntity(payload, speeds);
			WebSpeedDisplayInfo info = m_webSpeedService.buildBarCharts(queryEntity1);

			model.setSpeeds(speeds);
			model.setWebSpeedDisplayInfo(info);
		} catch (Exception e) {
			Cat.logError(e);
		}
	}

	private void buildSpeedInfo(Payload payload, Model model) {
		try {
			Map<String, Speed> speeds = m_webSpeedConfigManager.getSpeeds();
			SpeedQueryEntity queryEntity1 = normalizeSpeedQueryEntity(payload, speeds);
			WebSpeedDisplayInfo info = m_webSpeedService.buildSpeedDisplayInfo(queryEntity1,
			      payload.getSpeedQueryEntity2());

			model.setSpeeds(speeds);
			model.setWebSpeedDisplayInfo(info);
		} catch (Exception e) {
			Cat.logError(e);
		}
	}

	private void buildSpeedInfoJson(Payload payload, Model model) {
		try {
			Map<String, Speed> speeds = m_webSpeedConfigManager.getSpeeds();
			SpeedQueryEntity queryEntity1 = normalizeSpeedQueryEntity(payload, speeds);
			WebSpeedDisplayInfo info = m_webSpeedService.buildSpeedDisplayInfo(queryEntity1,
			      payload.getSpeedQueryEntity2());
			Map<String, Object> jsonObjs = new HashMap<String, Object>();

			jsonObjs.put("webSpeedDetails", info.getWebSpeedDetails());
			jsonObjs.put("webSpeedSummarys", info.getWebSpeedSummarys());

			model.setFetchData(m_jsonBuilder.toJson(jsonObjs));
		} catch (Exception e) {
			Cat.logError(e);
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private <T> T fetchTaskResult(List<FutureTask> tasks, int i) {
		T data = null;
		FutureTask task = tasks.get(i);

		try {
			data = (T) task.get(10L, TimeUnit.SECONDS);
		} catch (Exception e) {
			task.cancel(true);
			Cat.logError(e);
		}
		return data;
	}

	@Override
	@PayloadMeta(Payload.class)
	@InboundActionMeta(name = "browser")
	public void handleInbound(Context ctx) throws ServletException, IOException {
		// display only, no action here
	}

	@Override
	@OutboundActionMeta(name = "browser")
	public void handleOutbound(Context ctx) throws ServletException, IOException {
		Model model = new Model(ctx);
		Payload payload = ctx.getPayload();
		Action action = payload.getAction();

		normalize(model, payload);

		switch (action) {
		case AJAX_LINECHART:
			parallelBuildAjaxLineChart(model, payload);
			break;
		case AJAX_PIECHART:
			Pair<PieChart, AjaxPieChartDetailInfos> pieChartPair = buildAjaxPieChart(payload);
			AjaxDataDisplayInfo info = new AjaxDataDisplayInfo();

			if (pieChartPair != null) {
				info.setPieChart(pieChartPair.getKey());
				info.setPieChartDetailInfos(pieChartPair.getValue());
			}

			model.setAjaxDataDisplayInfo(info);
			break;
		case JS_ERROR:
			viewJsError(payload, model);
			break;
		case JS_ERROR_DETAIL:
			viewJsErrorDetail(payload, model);
			break;
		case SPEED:
			buildSpeedInfo(payload, model);
			break;
		case SPEED_JSON:
			buildSpeedInfoJson(payload, model);
			break;
		case SPEED_GRAPH:
			buildSpeedBarCharts(payload, model);
			break;
		case SPEED_CONFIG_FETCH:
			String type = payload.getType();
			try {
				if ("xml".equalsIgnoreCase(type)) {
					model.setFetchData(m_webSpeedConfigManager.getConfig().toString());
				} else if (StringUtils.isEmpty(type) || "json".equalsIgnoreCase(type)) {
					model.setFetchData(m_jsonBuilder.toJson(m_webSpeedConfigManager.getConfig()));
				}
			} catch (Exception e) {
				Cat.logError(e);
			}
			break;
		}

		if (!ctx.isProcessStopped()) {
			m_jspViewer.view(ctx, model);
		}
	}

	private void normalize(Model model, Payload payload) {
		model.setAction(payload.getAction());
		model.setPage(ReportPage.BROWSER);
		model.setCities(m_webConfigManager.queryConfigItem(WebConfigManager.CITY));
		model.setOperators(m_webConfigManager.queryConfigItem(WebConfigManager.OPERATOR));
		model.setNetworks(m_webConfigManager.queryConfigItem(WebConfigManager.NETWORK));
		model.setPlatforms(m_webConfigManager.queryConfigItem(WebConfigManager.PLATFORM));
		model.setSources(m_webConfigManager.queryConfigItem(WebConfigManager.SOURCE));
		model.setCodes(m_patternManager.queryCodes());

		PatternItem first = m_patternManager.queryUrlPatternRules().iterator().next();

		model.setDefaultApi(first.getName() + "|" + first.getPattern());
		model.setPattermItems(m_patternManager.queryUrlPatterns());
		m_normalizePayload.normalize(model, payload);
	}

	private SpeedQueryEntity normalizeSpeedQueryEntity(Payload payload, Map<String, Speed> speeds) {
		SpeedQueryEntity query1 = payload.getSpeedQueryEntity1();

		if (StringUtils.isEmpty(payload.getQuery1())) {
			if (!speeds.isEmpty()) {
				Speed first = speeds.get(speeds.keySet().toArray()[0]);
				Map<Integer, Step> steps = first.getSteps();

				if (first != null && !steps.isEmpty()) {
					String pageId = first.getPage();
					int stepId = steps.get(steps.keySet().toArray()[0]).getId();

					query1.setPageId(pageId);
					query1.setStepId(stepId);

					String split = ";";
					StringBuilder sb = new StringBuilder();

					sb.append(split).append(first.getId()).append("|").append(pageId).append(split).append(stepId)
					      .append(split).append(split).append(split).append(split).append(split);

					payload.setQuery1(sb.toString());
				}
			}
		}

		return query1;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void parallelBuildAjaxLineChart(Model model, final Payload payload) {
		ExecutorService executor = Executors.newFixedThreadPool(3);
		List<FutureTask> tasks = new LinkedList<FutureTask>();

		FutureTask lineChartTask = new FutureTask(new Callable<LineChart>() {
			@Override
			public LineChart call() throws Exception {
				return buildAjaxLineChart(payload);
			}
		});

		tasks.add(lineChartTask);
		executor.execute(lineChartTask);

		FutureTask ajaxDetailTask = new FutureTask(new Callable<List<AjaxDataDetail>>() {
			@Override
			public List<AjaxDataDetail> call() throws Exception {
				return buildAjaxDataDetails(payload);
			}

		});
		tasks.add(ajaxDetailTask);
		executor.execute(ajaxDetailTask);

		FutureTask comparisonTask = new FutureTask(new Callable<Map<String, AjaxDataDetail>>() {
			@Override
			public Map<String, AjaxDataDetail> call() throws Exception {
				return buildAjaxComparisonInfo(payload);
			}
		});
		tasks.add(comparisonTask);
		executor.execute(comparisonTask);

		LineChart lineChart = fetchTaskResult(tasks, 0);
		List<AjaxDataDetail> ajaxDataDetails = fetchTaskResult(tasks, 1);
		Map<String, AjaxDataDetail> comparisonDetails = fetchTaskResult(tasks, 2);

		executor.shutdown();

		AjaxDataDisplayInfo info = new AjaxDataDisplayInfo();

		info.setLineChart(lineChart);
		info.setAjaxDataDetailInfos(ajaxDataDetails);
		info.setComparisonAjaxDetails(comparisonDetails);
		model.setAjaxDataDisplayInfo(info);
	}

	private void processLog(Map<String, JsErrorMsg> errorMsgs, JsErrorLog log, Map<String, AtomicInteger> distributions) {
		String msg = log.getMsg();
		JsErrorMsg errorMsg = errorMsgs.get(msg);

		if (errorMsg == null) {
			errorMsg = new JsErrorMsg();
			errorMsg.setMsg(msg);
			errorMsgs.put(msg, errorMsg);
		}

		errorMsg.addCount();
		errorMsg.addId(log.getId());

		addBrowserCount(log.getBrowser(), distributions);
	}

	private List<JsErrorMsg> sort(Map<String, JsErrorMsg> errorMsgs) {
		List<JsErrorMsg> errorMsgList = new ArrayList<JsErrorMsg>();
		Iterator<Entry<String, JsErrorMsg>> iter = errorMsgs.entrySet().iterator();

		while (iter.hasNext()) {
			errorMsgList.add(iter.next().getValue());
		}

		Collections.sort(errorMsgList);
		return errorMsgList;
	}

	private void viewJsError(Payload payload, Model model) {
		try {
			Map<String, JsErrorMsg> errorMsgs = new HashMap<String, JsErrorMsg>();
			int offset = 0;
			int totalCount = 0;
			Map<String, AtomicInteger> distributions = new HashMap<String, AtomicInteger>();

			while (true) {
				List<JsErrorLog> result = m_jsErrorLogService.queryJsErrorInfo(payload.getJsErrorQuery(), offset, LIMIT);

				for (JsErrorLog log : result) {
					processLog(errorMsgs, log, distributions);
				}

				int count = result.size();
				totalCount += count;
				offset += count;

				if (count < LIMIT) {
					break;
				}
			}

			List<JsErrorMsg> errorMsgList = sort(errorMsgs);
			JsErrorDisplayInfo info = new JsErrorDisplayInfo();

			info.setErrors(errorMsgList);
			info.setTotalCount(totalCount);
			info.setLevels(Level.getLevels());
			info.setModules(m_moduleManager.getModules());
			info.setDistributionChart(buildDistributionChart(distributions));

			model.setJsErrorDisplayInfo(info);
		} catch (DalException e) {
			Cat.logError(e);
		}
	}

	private void viewJsErrorDetail(Payload payload, Model model) {
		JsErrorDetailInfo info = m_jsErrorLogService.queryJsErrorInfo(payload.getId());
		model.setJsErrorDetailInfo(info);
	}

}
