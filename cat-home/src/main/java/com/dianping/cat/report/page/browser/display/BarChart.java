package com.dianping.cat.report.page.browser.display;

import java.util.List;

import com.dianping.cat.helper.JsonBuilder;

public class BarChart {

	private String m_title;

	private String m_serieName;

	private String m_yAxis;

	private List<String> m_xAxis;

	private List<Double> m_values;
	
	private List<WebSpeedDetail> m_details;

	private JsonBuilder m_jsonBuilder = new JsonBuilder();
	
	public String getTitle() {
		return m_title;
	}

	public BarChart setTitle(String title) {
		m_title = title;
		return this;
	}

	public List<WebSpeedDetail> getDetails() {
		return m_details;
	}

	public void setDetails(List<WebSpeedDetail> details) {
		m_details = details;
	}

	public void setxAxis(List<String> xAxis) {
		m_xAxis = xAxis;
	}

	public void setValues(List<Double> values) {
		m_values = values;
	}

	public String getSerieName() {
		return m_serieName;
	}

	public BarChart setSerieName(String serieName) {
		m_serieName = serieName;
		return this;
	}

	public String getyAxis() {
		return m_yAxis;
	}

	public BarChart setyAxis(String yAxis) {
		m_yAxis = yAxis;
		return this;
	}

	public List<String> getxAxis() {
		return m_xAxis;
	}
	
	public String getxAxisJson() {
		return m_jsonBuilder.toJson(m_xAxis);
	}
	
	public String getValuesJson() {
		return m_jsonBuilder.toJson(m_values);
	}

	public List<Double> getValues() {
		return m_values;
	}

}
