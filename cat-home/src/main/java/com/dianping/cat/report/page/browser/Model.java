package com.dianping.cat.report.page.browser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.unidal.web.mvc.view.annotation.EntityMeta;

import com.dianping.cat.configuration.web.entity.Item;
import com.dianping.cat.configuration.web.speed.entity.Speed;
import com.dianping.cat.configuration.web.url.entity.Code;
import com.dianping.cat.configuration.web.url.entity.PatternItem;
import com.dianping.cat.helper.JsonBuilder;
import com.dianping.cat.mvc.AbstractReportModel;
import com.dianping.cat.report.ReportPage;
import com.dianping.cat.report.graph.LineChart;
import com.dianping.cat.report.graph.PieChart;
import com.dianping.cat.report.graph.PieChartDetailInfo;
import com.dianping.cat.report.page.browser.display.AjaxDataDetail;
import com.dianping.cat.report.page.browser.display.WebSpeedDetail;
import com.dianping.cat.report.page.browser.display.WebSpeedDisplayInfo;
import com.dianping.cat.report.page.problem.transform.ProblemStatistics;

public class Model extends AbstractReportModel<Action, ReportPage, Context> {

	@EntityMeta
	private ProblemStatistics m_allStatistics;

	private Map<String, PatternItem> m_pattermItems;

	@EntityMeta
	private LineChart m_lineChart;

	@EntityMeta
	private PieChart m_pieChart;
	
	private List<PieChartDetailInfo> m_pieChartDetailInfos;

	private Date m_start;

	private Date m_end;

	private Date m_compareStart;

	private Date m_compareEnd;

	private String m_json;

	private Speed m_speed;

	private Map<Integer, Item> m_cities;

	private Map<Integer, Item> m_platforms;

	private Map<Integer, Item> m_operators;

	private Map<Integer, Code> m_codes;

	private Map<Integer, Item> m_networks;

	private Map<Integer, Item> m_sources;

	private Map<String, Speed> m_speeds;

	private String m_defaultApi;

	private List<String> m_levels;

	private List<String> m_modules;

	private int m_totalCount;

	private List<ErrorMsg> m_errors;

	private String m_detail;

	private Date m_errorTime;

	private String m_level;

	private String m_module;

	private String m_distributionChart;

	private String m_agent;

	private String m_dpid;

	private WebSpeedDisplayInfo m_webSpeedDisplayInfo;

	private Map<String, AjaxDataDetail> m_comparisonAjaxDetails;

	private List<AjaxDataDetail> m_ajaxDataDetailInfos;

	private int m_commandId;
	
	private String m_fetchData;

	public Model(Context ctx) {
		super(ctx);
	}

	public String getAgent() {
		return m_agent;
	}

	public List<AjaxDataDetail> getAjaxDataDetailInfos() {
		return m_ajaxDataDetailInfos;
	}

	public ProblemStatistics getAllStatistics() {
		return m_allStatistics;
	}

	public Map<Integer, Item> getCities() {
		return m_cities;
	}

	public Map<Integer, Code> getCodes() {
		return m_codes;
	}

	public int getCommandId() {
		return m_commandId;
	}

	public Date getCompareEnd() {
		return m_compareEnd;
	}

	public Date getCompareStart() {
		return m_compareStart;
	}

	public Map<String, AjaxDataDetail> getComparisonAjaxDetails() {
		return m_comparisonAjaxDetails;
	}

	@Override
	public Action getDefaultAction() {
		return Action.VIEW;
	}

	public String getDefaultApi() {
		return m_defaultApi;
	}

	public String getDetail() {
		return m_detail;
	}

	public String getDistributionChart() {
		return m_distributionChart;
	}

	@Override
	public String getDomain() {
		return getDisplayDomain();
	}

	@Override
	public Collection<String> getDomains() {
		return new ArrayList<String>();
	}

	public String getDpid() {
		return m_dpid;
	}

	public Date getEnd() {
		return m_end;
	}

	public List<ErrorMsg> getErrors() {
		return m_errors;
	}

	public Date getErrorTime() {
		return m_errorTime;
	}

	public String getFetchData() {
		return m_fetchData;
	}

	public String getJson() {
		return m_json;
	}

	public String getLevel() {
		return m_level;
	}

	public List<String> getLevels() {
		return m_levels;
	}

	public LineChart getLineChart() {
		return m_lineChart;
	}

	public String getModule() {
		return m_module;
	}

	public List<String> getModules() {
		return m_modules;
	}

	public Map<Integer, Item> getNetworks() {
		return m_networks;
	}

	public Map<Integer, Item> getOperators() {
		return m_operators;
	}

	public String getPage2StepsJson() {
		return new JsonBuilder().toJson(m_speeds);
	}

	public Map<String, PatternItem> getPattermItems() {
		return m_pattermItems;
	}

	public String getPattern2Items() {
		return new JsonBuilder().toJson(m_pattermItems);
	}

	public PieChart getPieChart() {
		return m_pieChart;
	}

	public List<PieChartDetailInfo> getPieChartDetailInfos() {
		return m_pieChartDetailInfos;
	}

	public Map<Integer, Item> getPlatforms() {
		return m_platforms;
	}

	public Map<Integer, Item> getSources() {
		return m_sources;
	}

	public Speed getSpeed() {
		return m_speed;
	}

	public Map<String, Speed> getSpeeds() {
		return m_speeds;
	}

	public Date getStart() {
		return m_start;
	}
	
	public int getTotalCount() {
		return m_totalCount;
	}

	public Map<String, Map<Integer, WebSpeedDetail>> getWebSpeedDetails() {
		Map<String, Map<Integer, WebSpeedDetail>> map = new LinkedHashMap<String, Map<Integer, WebSpeedDetail>>();
		Map<String, List<WebSpeedDetail>> details = m_webSpeedDisplayInfo.getWebSpeedDetails();

		if (details != null && !details.isEmpty()) {
			for (Entry<String, List<WebSpeedDetail>> entry : details.entrySet()) {
				Map<Integer, WebSpeedDetail> m = new LinkedHashMap<Integer, WebSpeedDetail>();

				for (WebSpeedDetail detail : entry.getValue()) {
					m.put(detail.getMinuteOrder(), detail);
				}
				map.put(entry.getKey(), m);
			}
		}
		return map;
	}

	public WebSpeedDisplayInfo getWebSpeedDisplayInfo() {
		return m_webSpeedDisplayInfo;
	}

	public Map<String, Map<Integer, WebSpeedDetail>> getWebSpeedSummarys() {
		Map<String, Map<Integer, WebSpeedDetail>> map = new LinkedHashMap<String, Map<Integer, WebSpeedDetail>>();
		Map<String, WebSpeedDetail> details = m_webSpeedDisplayInfo.getWebSpeedSummarys();

		if (details != null && !details.isEmpty()) {
			for (Entry<String, WebSpeedDetail> entry : details.entrySet()) {
				Map<Integer, WebSpeedDetail> m = new LinkedHashMap<Integer, WebSpeedDetail>();
				WebSpeedDetail d = entry.getValue();

				m.put(d.getMinuteOrder(), d);
				map.put(entry.getKey(), m);
			}
		}
		return map;
	}

	public void setAgent(String agent) {
		m_agent = agent;
	}

	public void setAjaxDataDetailInfos(List<AjaxDataDetail> ajaxDataDetailInfos) {
		m_ajaxDataDetailInfos = ajaxDataDetailInfos;
	}

	public void setAllStatistics(ProblemStatistics allStatistics) {
		m_allStatistics = allStatistics;
	}

	public void setCities(Map<Integer, Item> cities) {
		m_cities = cities;
	}

	public void setCodes(Map<Integer, Code> codes) {
		m_codes = codes;
	}

	public void setCommandId(int commandId) {
		m_commandId = commandId;
	}

	public void setCompareEnd(Date compareEnd) {
		m_compareEnd = compareEnd;
	}

	public void setCompareStart(Date compareStart) {
		m_compareStart = compareStart;
	}

	public void setComparisonAjaxDetails(Map<String, AjaxDataDetail> comparisonAjaxDetail) {
		m_comparisonAjaxDetails = comparisonAjaxDetail;
	}

	public void setDefaultApi(String defaultApi) {
		m_defaultApi = defaultApi;
	}

	public void setDetail(String detail) {
		m_detail = detail;
	}

	public void setDistributionChart(String distributionChart) {
		m_distributionChart = distributionChart;
	}

	public void setDpid(String dpid) {
		m_dpid = dpid;
	}

	public void setEnd(Date end) {
		m_end = end;
	}

	public void setErrors(List<ErrorMsg> errors) {
		m_errors = errors;
	}

	public void setErrorTime(Date errorTime) {
		m_errorTime = errorTime;
	}

	public void setFetchData(String fetchData) {
		m_fetchData = fetchData;
	}

	public void setJson(String json) {
		m_json = json;
	}

	public void setLevel(String level) {
		m_level = level;
	}

	public void setLevels(List<String> levels) {
		m_levels = levels;
	}

	public void setLineChart(LineChart lineChart) {
		m_lineChart = lineChart;
	}

	public void setModule(String module) {
		m_module = module;
	}

	public void setModules(List<String> modules) {
		m_modules = modules;
	}

	public void setNetworks(Map<Integer, Item> networks) {
		m_networks = networks;
	}

	public void setOperators(Map<Integer, Item> operators) {
		m_operators = operators;
	}

	public void setPattermItems(Map<String, PatternItem> pattermItems) {
		m_pattermItems = pattermItems;
	}

	public void setPieChart(PieChart pieChart) {
		m_pieChart = pieChart;
	}

	public void setPieChartDetailInfos(List<PieChartDetailInfo> pieChartDetailInfos) {
		m_pieChartDetailInfos = pieChartDetailInfos;
	}

	public void setPlatforms(Map<Integer, Item> platforms) {
		m_platforms = platforms;
	}

	public void setSources(Map<Integer, Item> sources) {
		m_sources = sources;
	}

	public void setSpeed(Speed speed) {
		m_speed = speed;
	}

	public void setSpeeds(Map<String, Speed> speeds) {
		m_speeds = speeds;
	}

	public void setStart(Date start) {
		m_start = start;
	}

	public void setTotalCount(int totalCount) {
		m_totalCount = totalCount;
	}

	public void setWebSpeedDisplayInfo(WebSpeedDisplayInfo webSpeedDisplayInfo) {
		m_webSpeedDisplayInfo = webSpeedDisplayInfo;
	}

}
