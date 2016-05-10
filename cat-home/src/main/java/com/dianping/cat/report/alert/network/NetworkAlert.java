package com.dianping.cat.report.alert.network;

import java.util.Map;

import org.unidal.lookup.annotation.Inject;
import org.unidal.lookup.annotation.Named;

import com.dianping.cat.consumer.company.model.entity.ProductLine;
import com.dianping.cat.report.alert.spi.BaseAlert;
import com.dianping.cat.report.alert.spi.config.BaseRuleConfigManager;
import com.dianping.cat.alarm.spi.AlertType;

@Named
public class NetworkAlert extends BaseAlert {

	@Inject
	protected NetworkRuleConfigManager m_ruleConfigManager;

	@Override
	public String getName() {
		return AlertType.Network.getName();
	}

	@Override
	protected Map<String, ProductLine> getProductlines() {
		return m_productLineConfigManager.queryNetworkProductLines();
	}

	@Override
	protected BaseRuleConfigManager getRuleConfigManager() {
		return m_ruleConfigManager;
	}

}