package com.dianping.cat.report;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;
import org.unidal.dal.jdbc.QueryEngine;
import org.unidal.dal.jdbc.mapping.TableProvider;
import org.unidal.lookup.annotation.Named;

import com.dianping.cat.Cat;
import com.dianping.cat.core.dal.HourlyReport;

@Named(type = TableProvider.class, value = HourlyReportTableProvider.LOGIC_TABLE_NAME)
public class HourlyReportTableProvider implements TableProvider, Initializable {

	public final static String LOGIC_TABLE_NAME = "report";

	private String m_logicalTableName = LOGIC_TABLE_NAME;

	private String m_physicalTableName = LOGIC_TABLE_NAME;

	private String m_dataSourceName = "cat";

	private Date m_historyDate;

	@Override
	public String getDataSourceName(Map<String, Object> hints) {
		return m_dataSourceName;
	}

	@Override
	public String getLogicalTableName() {
		return m_logicalTableName;
	}

	@Override
	public String getPhysicalTableName(Map<String, Object> hints) {
		HourlyReport command = (HourlyReport) hints.get(QueryEngine.HINT_DATA_OBJECT);
		System.err.println(command.getPeriod());

		try {
			if (command.getPeriod().before(m_historyDate)) {
				System.err.println("read table: " + m_physicalTableName);
				return m_physicalTableName;
			} else {
				System.err.println("read table: hourlyreport");

				return "hourlyreport";
			}
		} catch (Exception e) {
			e.printStackTrace();
			return m_physicalTableName;
		}
	}

	@Override
	public void initialize() throws InitializationException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

		try {
			m_historyDate = sdf.parse("2016-04-16 00:00");
			System.err.println(this.getClass().getSimpleName() + ": " + m_historyDate);
		} catch (ParseException e) {
			Cat.logError(e);
		}
	}

	public void setLogicalTableName(String logicalTableName) {
		m_logicalTableName = logicalTableName;
	}

}