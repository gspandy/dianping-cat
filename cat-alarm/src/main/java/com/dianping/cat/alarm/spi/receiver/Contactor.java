package com.dianping.cat.alarm.spi.receiver;

import java.util.List;

public interface Contactor {

	public String getId();

	public List<String> queryEmailContactors(String id);

	public List<String> queryWeiXinContactors(String id);

	public List<String> querySmsContactors(String id);
}
