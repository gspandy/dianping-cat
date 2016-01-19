package com.dianping.cat.report.page.app.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.codehaus.plexus.util.StringUtils;

import com.dianping.cat.helper.TimeHelper;

public class CrashLogQueryEntity {

	private String m_day;

	private String m_startTime;

	private String m_endTime;

	private String m_appName = "Android(主APP)";

	private String m_module;

	private int m_platform = -1;

	private String m_dpid = null;

	private String m_msg = null;

	private SimpleDateFormat m_format = new SimpleDateFormat("yyyy-MM-dd HH:mm");

	private String m_query;

	public Date buildEndTime() {
		if (StringUtils.isNotBlank(m_day) && StringUtils.isNotBlank(m_endTime)) {
			try {
				Date date = m_format.parse(m_day + " " + m_endTime);
				return date;
			} catch (ParseException e) {
			}
		}
		return TimeHelper.getCurrentDay(1);
	}

	public Date buildStartTime() {
		if (StringUtils.isNotBlank(m_day) && StringUtils.isNotBlank(m_startTime)) {
			try {
				Date date = m_format.parse(m_day + " " + m_startTime);
				return date;
			} catch (ParseException e) {
			}
		}
		return TimeHelper.getCurrentHour();
	}

	public String getQuery() {
		return m_query;
	}

	public void setQuery(String query) {
		m_query = query;
	}

	public String getDay() {
		return m_day;
	}

	public void setDay(String day) {
		m_day = day;
	}

	public String getDpid() {
		if (StringUtils.isEmpty(m_dpid)) {
			return null;
		}

		return m_dpid;
	}

	public void setDpid(String dpid) {
		m_dpid = dpid;
	}

	public String getAppName() {
		return m_appName;
	}

	public void setAppName(String appName) {
		m_appName = appName;
	}

	public String getStartTime() {
		return m_startTime;
	}

	public void setStartTime(String startTime) {
		m_startTime = startTime;
	}

	public String getEndTime() {
		return m_endTime;
	}

	public void setEndTime(String endTime) {
		m_endTime = endTime;
	}

	public String getModule() {
		return m_module;
	}

	public void setModule(String module) {
		m_module = module;
	}

	public int getPlatform() {
		return m_platform;
	}

	public void setPlatform(int platform) {
		m_platform = platform;
	}

	public String getMsg() {
		return m_msg;
	}

	public void setMsg(String msg) {
		m_msg = msg;
	}

}
