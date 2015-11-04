package com.dianping.cat.report.page.browser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.unidal.web.mvc.view.annotation.EntityMeta;

import com.dianping.cat.configuration.web.entity.Item;
import com.dianping.cat.configuration.web.url.entity.Code;
import com.dianping.cat.configuration.web.url.entity.PatternItem;
import com.dianping.cat.helper.JsonBuilder;
import com.dianping.cat.mvc.AbstractReportModel;
import com.dianping.cat.report.ReportPage;
import com.dianping.cat.report.graph.LineChart;
import com.dianping.cat.report.graph.PieChart;
import com.dianping.cat.report.page.app.display.PieChartDetailInfo;
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

	private Map<Integer, Item> m_cities;

	private Map<Integer, Item> m_operators;

	private Map<Integer, Code> m_codes;

	private Map<Integer, Item> m_networks;

	private String m_defaultApi;

	private List<String> m_levels;

	private List<String> m_modules;

	private int m_totalCount;

	private List<ErrorMsg> m_errors;

	private String m_detail;

	private Date m_errorTime;

	private String m_level;

	private String m_module;

	public Model(Context ctx) {
		super(ctx);
	}

	public ProblemStatistics getAllStatistics() {
		return m_allStatistics;
	}

	public Date getErrorTime() {
		return m_errorTime;
	}

	public String getLevel() {
		return m_level;
	}

	public String getModule() {
		return m_module;
	}

	public Map<Integer, Item> getNetworks() {
		return m_networks;
	}

	public Map<Integer, Item> getCities() {
		return m_cities;
	}

	public Map<Integer, Code> getCodes() {
		return m_codes;
	}

	public Date getCompareEnd() {
		return m_compareEnd;
	}

	public Date getCompareStart() {
		return m_compareStart;
	}

	@Override
	public Action getDefaultAction() {
		return Action.VIEW;
	}

	public String getDefaultApi() {
		return m_defaultApi;
	}

	@Override
	public String getDomain() {
		return getDisplayDomain();
	}

	@Override
	public Collection<String> getDomains() {
		return new ArrayList<String>();
	}

	public Date getEnd() {
		return m_end;
	}

	public String getJson() {
		return m_json;
	}

	public LineChart getLineChart() {
		return m_lineChart;
	}

	public Map<Integer, Item> getOperators() {
		return m_operators;
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

	public Date getStart() {
		return m_start;
	}

	public void setLevel(String level) {
		m_level = level;
	}

	public void setModule(String module) {
		m_module = module;
	}

	public void setErrorTime(Date errorTime) {
		m_errorTime = errorTime;
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

	public void setNetworks(Map<Integer, Item> networks) {
		m_networks = networks;
	}

	public void setCompareEnd(Date compareEnd) {
		m_compareEnd = compareEnd;
	}

	public void setCompareStart(Date compareStart) {
		m_compareStart = compareStart;
	}

	public void setDefaultApi(String defaultApi) {
		m_defaultApi = defaultApi;
	}

	public void setEnd(Date end) {
		m_end = end;
	}

	public void setJson(String json) {
		m_json = json;
	}

	public void setLineChart(LineChart lineChart) {
		m_lineChart = lineChart;
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

	public void setStart(Date start) {
		m_start = start;
	}

	public String getDetail() {
		return m_detail;
	}

	public void setDetail(String detail) {
		m_detail = detail;
	}

	public List<ErrorMsg> getErrors() {
		return m_errors;
	}

	public void setErrors(List<ErrorMsg> errors) {
		m_errors = errors;
	}

	public int getTotalCount() {
		return m_totalCount;
	}

	public void setTotalCount(int totalCount) {
		m_totalCount = totalCount;
	}

	public List<String> getModules() {
		return m_modules;
	}

	public void setModules(List<String> modules) {
		m_modules = modules;
	}

	public List<String> getLevels() {
		return m_levels;
	}

	public void setLevels(List<String> levels) {
		m_levels = levels;
	}

}