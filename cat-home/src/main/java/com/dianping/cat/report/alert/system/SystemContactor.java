package com.dianping.cat.report.alert.system;

import java.util.List;

import com.dianping.cat.alarm.spi.AlertType;
import com.dianping.cat.alarm.spi.receiver.ProjectContactor;

public class SystemContactor extends ProjectContactor {

	private static final String prefix = "system-";

	public static final String ID = AlertType.System.getName();

	@Override
	public String getId() {
		return ID;
	}

	@Override
	public List<String> queryEmailContactors(String domain) {
		if (domain.startsWith(prefix)) {
			domain = domain.substring(7);
		}
		return super.queryEmailContactors(domain);
	}

	@Override
	public List<String> querySmsContactors(String domain) {
		if (domain.startsWith(prefix)) {
			domain = domain.substring(7);
		}
		return super.querySmsContactors(domain);
	}

	@Override
	public List<String> queryWeiXinContactors(String domain) {
		if (domain.startsWith(prefix)) {
			domain = domain.substring(7);
		}
		return super.queryWeiXinContactors(domain);
	}

	@Override
	public List<String> queryDXContactors(String domain) {
		if (domain.startsWith(prefix)) {
			domain = domain.substring(7);
		}
		return super.queryDXContactors(domain);
	}

}
