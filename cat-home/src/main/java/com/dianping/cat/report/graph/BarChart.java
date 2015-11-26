package com.dianping.cat.report.graph;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.dianping.cat.helper.JsonBuilder;

public class BarChart {

	private String m_title;

	private String m_serieName;

	private String m_yAxis;

	private List<String> m_xAxis;

	private List<Double> m_values;

	private JsonBuilder m_jsonBuilder = new JsonBuilder();
	
	public void addValues(Map<String, Double> data) {
		List<String> xAxis = new ArrayList<String>();
		List<Double> values = new ArrayList<Double>();

		for (Entry<String, Double> value : data.entrySet()) {
			xAxis.add(value.getKey());
			values.add(value.getValue());
		}

		m_xAxis = xAxis;
		m_values = values;
	}

	public String getTitle() {
		return m_title;
	}

	public BarChart setTitle(String title) {
		m_title = title;
		return this;
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
