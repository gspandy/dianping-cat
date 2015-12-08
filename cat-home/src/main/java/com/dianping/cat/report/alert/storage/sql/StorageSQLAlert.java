package com.dianping.cat.report.alert.storage.sql;

import org.unidal.lookup.annotation.Inject;

import com.dianping.cat.report.alert.spi.AlertType;
import com.dianping.cat.report.alert.storage.AbstractStorageAlert;
import com.dianping.cat.report.alert.storage.StorageRuleConfigManager;

public class StorageSQLAlert extends AbstractStorageAlert {

	@Inject
	private StorageSQLRuleConfigManager m_configManager;

	public static final String ID = AlertType.STORAGE_SQL.getName();

	@Override
	public String getName() {
		return ID;
	}

	@Override
	protected StorageRuleConfigManager getRuleConfigManager() {
		return m_configManager;
	}
}
