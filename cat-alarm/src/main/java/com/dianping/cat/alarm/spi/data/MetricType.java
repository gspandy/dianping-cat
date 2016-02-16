package com.dianping.cat.alarm.spi.data;

public enum MetricType {
	COUNT("COUNT"),

	AVG("AVG"),

	SUM("SUM");

	private String m_name;

	MetricType(String name) {
		m_name = name;
	}

	public String getName() {
		return m_name;
	}
};
