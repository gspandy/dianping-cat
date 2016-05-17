package com.dianping.cat.report.page.network.service;

import java.util.Date;
import java.util.List;

import org.unidal.dal.jdbc.DalException;
import org.unidal.lookup.annotation.Named;

import com.dianping.cat.Cat;
import com.dianping.cat.Constants;
import com.dianping.cat.core.dal.HourlyReport;
import com.dianping.cat.core.dal.HourlyReportContent;
import com.dianping.cat.core.dal.HourlyReportContentEntity;
import com.dianping.cat.core.dal.HourlyReportEntity;
import com.dianping.cat.home.network.entity.NetGraphSet;
import com.dianping.cat.home.network.transform.DefaultNativeParser;
import com.dianping.cat.report.service.AbstractReportService;

@Named
public class NetTopologyReportService extends AbstractReportService<NetGraphSet> {

	@Override
	public NetGraphSet makeReport(String domain, Date start, Date end) {
		NetGraphSet report = new NetGraphSet();

		return report;
	}

	@Override
	public NetGraphSet queryDailyReport(String domain, Date start, Date end) {
		throw new RuntimeException("net topology report don't support daily report");
	}

	private NetGraphSet queryFromHourlyBinary(int id, Date period) throws DalException {
		HourlyReportContent content = m_hourlyReportContentDao.findByPK(id, period,
		      HourlyReportContentEntity.READSET_CONTENT);

		if (content != null) {
			return DefaultNativeParser.parse(content.getContent());
		} else {
			return null;
		}
	}

	@Override
	public NetGraphSet queryHourlyReport(String domain, Date start, Date end) {
		long startTime = start.getTime();
		String name = Constants.REPORT_NET_TOPOLOGY;
		NetGraphSet netGraphs = null;
		List<HourlyReport> reports = null;

		try {
			reports = m_hourlyReportDao.findAllByDomainNamePeriod(new Date(startTime), domain, name,
			      HourlyReportEntity.READSET_FULL);
		} catch (DalException e) {
			Cat.logError(e);
		}

		if (reports != null && reports.size() > 0) {
			try {
				HourlyReport report = reports.get(0);
				netGraphs = queryFromHourlyBinary(report.getId(), report.getPeriod());
			} catch (DalException e) {
				Cat.logError(e);
			}
		}

		return netGraphs;
	}

	@Override
	public NetGraphSet queryMonthlyReport(String domain, Date start) {
		throw new RuntimeException("net topology report don't support monthly report");
	}

	@Override
	public NetGraphSet queryWeeklyReport(String domain, Date start) {
		throw new RuntimeException("net topology report don't support weekly report");
	}

}
