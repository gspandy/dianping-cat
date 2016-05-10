package com.dianping.cat.config.app;

import java.util.Map;

import org.unidal.dal.jdbc.QueryEngine;
import org.unidal.dal.jdbc.mapping.TableProvider;
import org.unidal.lookup.annotation.Named;

import com.dianping.cat.app.AppCommandDataDaily;

@Named(type = TableProvider.class, value = AppCmdDailyTableProvider.LOGIC_TABLE_NAME)
public class AppCmdDailyTableProvider implements TableProvider {

	public final static String LOGIC_TABLE_NAME = "app-command-data-daily";

	private String m_physicalTableName = "app_command_data_daily";

	private String m_dataSourceName = "app";

	@Override
	public String getDataSourceName(Map<String, Object> hints, String logicalTableName) {
		return m_dataSourceName;
	}

	@Override
	public String getPhysicalTableName(Map<String, Object> hints, String logicalTableName) {
		AppCommandDataDaily command = (AppCommandDataDaily) hints.get(QueryEngine.HINT_DATA_OBJECT);

		return m_physicalTableName + "_" + command.getCommandId();
	}

}