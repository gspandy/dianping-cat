package com.dianping.cat.report.page.browser;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
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
import com.dianping.cat.report.graph.PieChartDetailInfo;
import com.dianping.cat.report.page.browser.display.AjaxDataDetail;
import com.dianping.cat.report.page.browser.display.ChartSorter;
import com.dianping.cat.report.page.browser.display.WebSpeedDisplayInfo;
import com.dianping.cat.report.page.browser.graph.WebGraphCreator;
import com.dianping.cat.report.page.browser.service.AjaxDataField;
import com.dianping.cat.report.page.browser.service.AjaxDataQueryEntity;
import com.dianping.cat.report.page.browser.service.AjaxDataService;
import com.dianping.cat.report.page.browser.service.SpeedQueryEntity;
import com.dianping.cat.report.page.browser.service.WebSpeedService;
import com.dianping.cat.web.JsErrorLog;
import com.dianping.cat.web.JsErrorLogContent;
import com.dianping.cat.web.JsErrorLogContentDao;
import com.dianping.cat.web.JsErrorLogContentEntity;
import com.dianping.cat.web.JsErrorLogDao;
import com.dianping.cat.web.JsErrorLogEntity;

import org.unidal.dal.jdbc.DalException;
import org.unidal.lookup.annotation.Inject;
import org.unidal.tuple.Pair;
import org.unidal.web.mvc.PageHandler;
import org.unidal.web.mvc.annotation.InboundActionMeta;
import org.unidal.web.mvc.annotation.OutboundActionMeta;
import org.unidal.web.mvc.annotation.PayloadMeta;

import com.dianping.cat.config.web.js.Level;
import com.dianping.cat.helper.TimeHelper;
import com.site.lookup.util.StringUtils;

public class Handler implements PageHandler<Context> {

	private final int LIMIT = 10000;

	@Inject
	private AjaxDataService m_ajaxDataService;

	@Inject
	private WebGraphCreator m_graphCreator;

	@Inject
	private JsErrorLogContentDao m_jsErrorLogContentlDao;

	@Inject
	private JsErrorLogDao m_jsErrorLogDao;

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

	public void addBrowserCount(String browser, Map<String, Integer> distributions) {
		Integer count = distributions.get(browser);

		if (count == null) {
			count = 1;
		} else {
			count++;
		}

		distributions.put(browser, count);
	}

	private List<AjaxDataDetail> buildAjaxDataDetails(Payload payload) {
		List<AjaxDataDetail> ajaxDetails = new ArrayList<AjaxDataDetail>();

		try {
			ajaxDetails = m_ajaxDataService.buildAjaxDataDetailInfos(payload.getQueryEntity1(), payload.getGroupByField());
			Collections.sort(ajaxDetails, new ChartSorter(payload.getSort()).buildLineChartInfoComparator());
		} catch (Exception e) {
			Cat.logError(e);
		}
		return ajaxDetails;
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

	protected Map<String, AjaxDataDetail> buildComparisonInfo(Payload payload) {
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

	public String buildDistributionChart(Map<String, Integer> distributions) {
		PieChart chart = new PieChart();
		List<Item> items = new ArrayList<Item>();

		for (Entry<String, Integer> entry : distributions.entrySet()) {
			Item item = new Item();

			item.setNumber(entry.getValue()).setTitle(entry.getKey());
			items.add(item);
		}
		chart.addItems(items);

		return chart.getJsonString();
	}

	private LineChart buildLineChart(Payload payload) {
		AjaxDataQueryEntity entity1 = payload.getQueryEntity1();
		AjaxDataQueryEntity entity2 = payload.getQueryEntity2();
		String type = payload.getType();
		LineChart lineChart = new LineChart();

		try {
			lineChart = m_graphCreator.buildLineChart(entity1, entity2, type);
		} catch (Exception e) {
			Cat.logError(e);
		}
		return lineChart;
	}

	private Pair<PieChart, List<PieChartDetailInfo>> buildPieChart(Payload payload) {
		try {
			Pair<PieChart, List<PieChartDetailInfo>> pair = m_graphCreator.buildPieChart(payload.getQueryEntity1(),
			      payload.getGroupByField());
			List<PieChartDetailInfo> infos = pair.getValue();
			Collections.sort(infos, new ChartSorter().buildPieChartInfoComparator());

			return pair;
		} catch (Exception e) {
			Cat.logError(e);
		}
		return null;
	}

	private void buildSpeedInfo(Payload payload, Model model) {
		try {
			Map<String, Speed> speeds = m_webSpeedConfigManager.getSpeeds();
			SpeedQueryEntity queryEntity1 = normalizeSpeedQueryEntity(payload, speeds);
			WebSpeedDisplayInfo info = m_webSpeedService.buildSpeedDisplayInfo(queryEntity1,
			      payload.getSpeedQueryEntity2());

			model.setSpeeds(m_webSpeedConfigManager.getSpeeds());
			model.setWebSpeedDisplayInfo(info);
		} catch (Exception e) {
			Cat.logError(e);
		}
	}

	private void buildBarCharts(Payload payload, Model model) {
		try {
			Map<String, Speed> speeds = m_webSpeedConfigManager.getSpeeds();
			SpeedQueryEntity queryEntity1 = normalizeSpeedQueryEntity(payload, speeds);
			WebSpeedDisplayInfo info = m_webSpeedService.buildBarCharts(queryEntity1);
			
			model.setSpeeds(m_webSpeedConfigManager.getSpeeds());
			model.setWebSpeedDisplayInfo(info);
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
		case VIEW:
			parallelBuildLineChart(model, payload);
			break;
		case PIECHART:
			Pair<PieChart, List<PieChartDetailInfo>> pieChartPair = buildPieChart(payload);

			if (pieChartPair != null) {
				model.setPieChart(pieChartPair.getKey());
				model.setPieChartDetailInfos(pieChartPair.getValue());
			}

			int commandId = payload.getQueryEntity1().getId();
			model.setCommandId(commandId);
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
		case SPEED_GRAPH:
			buildBarCharts(payload, model);
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
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					StringBuilder sb = new StringBuilder();

					sb.append(sdf.format(TimeHelper.getCurrentDay())).append(split).append(pageId).append(split)
					      .append(stepId).append(split).append(split).append(split).append(split).append(split);
					
					payload.setQuery1(sb.toString());
				}
			}
		}

		return query1;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void parallelBuildLineChart(Model model, final Payload payload) {
		ExecutorService executor = Executors.newFixedThreadPool(3);
		List<FutureTask> tasks = new LinkedList<FutureTask>();

		FutureTask lineChartTask = new FutureTask(new CallableTask<LineChart>() {
			@Override
			public LineChart call() throws Exception {
				return buildLineChart(payload);
			}
		});

		tasks.add(lineChartTask);
		executor.execute(lineChartTask);

		FutureTask ajaxDetailTask = new FutureTask(new CallableTask<List<AjaxDataDetail>>() {
			@Override
			public List<AjaxDataDetail> call() throws Exception {
				return buildAjaxDataDetails(payload);
			}

		});
		tasks.add(ajaxDetailTask);
		executor.execute(ajaxDetailTask);

		FutureTask comparisonTask = new FutureTask(new CallableTask<Map<String, AjaxDataDetail>>() {
			@Override
			public Map<String, AjaxDataDetail> call() throws Exception {
				return buildComparisonInfo(payload);
			}
		});
		tasks.add(comparisonTask);
		executor.execute(comparisonTask);

		LineChart lineChart = fetchTaskResult(tasks, 0);
		List<AjaxDataDetail> ajaxDataDetails = fetchTaskResult(tasks, 1);
		Map<String, AjaxDataDetail> comparisonDetails = fetchTaskResult(tasks, 2);

		executor.shutdown();
		model.setLineChart(lineChart);
		model.setAjaxDataDetailInfos(ajaxDataDetails);
		model.setComparisonAjaxDetails(comparisonDetails);
	}

	private void processLog(Map<String, ErrorMsg> errorMsgs, JsErrorLog log, Map<String, Integer> distributions) {
		String msg = log.getMsg();
		ErrorMsg errorMsg = errorMsgs.get(msg);

		if (errorMsg == null) {
			errorMsg = new ErrorMsg();
			errorMsg.setMsg(msg);
			errorMsgs.put(msg, errorMsg);
		}

		errorMsg.addCount();
		errorMsg.addId(log.getId());

		addBrowserCount(log.getBrowser(), distributions);
	}

	private List<ErrorMsg> sort(Map<String, ErrorMsg> errorMsgs) {
		List<ErrorMsg> errorMsgList = new ArrayList<ErrorMsg>();
		Iterator<Entry<String, ErrorMsg>> iter = errorMsgs.entrySet().iterator();

		while (iter.hasNext()) {
			errorMsgList.add(iter.next().getValue());
		}

		Collections.sort(errorMsgList);
		return errorMsgList;
	}

	private void viewJsError(Payload payload, Model model) {
		try {
			Date startTime = payload.buildStartTime();
			Date endTime = payload.buildEndTime();
			int levelCode = payload.buildLevel();
			String module = payload.getModule();
			String dpid = payload.getDpid();
			Map<String, ErrorMsg> errorMsgs = new HashMap<String, ErrorMsg>();
			int offset = 0;
			int totalCount = 0;
			Map<String, Integer> distributions = new HashMap<String, Integer>();

			while (true) {
				List<JsErrorLog> result = m_jsErrorLogDao.findDataByTimeModuleLevelBrowser(startTime, endTime, module,
				      levelCode, null, dpid, offset, LIMIT, JsErrorLogEntity.READSET_FULL);

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

			List<ErrorMsg> errorMsgList = sort(errorMsgs);

			model.setErrors(errorMsgList);
			model.setTotalCount(totalCount);
			model.setLevels(Level.getLevels());
			model.setModules(m_moduleManager.getModules());
			model.setDistributionChart(buildDistributionChart(distributions));
		} catch (DalException e) {
			Cat.logError(e);
		}
	}

	private void viewJsErrorDetail(Payload payload, Model model) {
		try {
			int id = payload.getId();

			JsErrorLogContent detail = m_jsErrorLogContentlDao.findByPK(id, JsErrorLogContentEntity.READSET_FULL);
			JsErrorLog jsErrorLog = m_jsErrorLogDao.findByPK(id, JsErrorLogEntity.READSET_FULL);

			model.setErrorTime(jsErrorLog.getErrorTime());
			model.setLevel(Level.getNameByCode(jsErrorLog.getLevel()));
			model.setModule(jsErrorLog.getModule());
			model.setDetail(new String(detail.getContent(), "UTF-8"));
			model.setAgent(jsErrorLog.getBrowser());
			model.setDpid(jsErrorLog.getDpid());
		} catch (Exception e) {
			Cat.logError(e);
		}
	}

	public class CallableTask<T> implements Callable<T> {

		@Override
		public T call() throws Exception {
			return null;
		}

	}

}
