package com.dianping.cat.report.alert.storage;

import org.unidal.lookup.annotation.Inject;

import com.dianping.cat.report.alert.spi.AlertType;

public class StorageRPCAlert extends AbstractStorageAlert {

	@Inject
	private StorageRPCRuleConfigManager m_configManager;

	public static final String ID = AlertType.STORAGE_RPC.getName();

	@Override
	public String getName() {
		return ID;
	}

	@Override
	protected StorageRuleConfigManager getRuleConfigManager() {
		return m_configManager;
	}
}
