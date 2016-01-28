package com.dianping.cat.alarm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dianping.cat.alarm.entity.Condition;
import com.dianping.cat.alarm.entity.Rule;
import com.dianping.cat.core.alarm.ServerAlarmRule;
import com.dianping.cat.metric.QueryParameter;

public class AlarmTask {

	private ServerAlarmRule m_rule;

	private List<Rule> m_ruleConfigs = new ArrayList<Rule>();

	private Map<String, AlarmParameter> m_paramters = new HashMap<String, AlarmParameter>();

	private long m_startTime;

	private String m_category;

	private boolean m_completed = false;

	public AlarmTask(ServerAlarmRule rule, String categroy) {
		m_rule = rule;
		m_category = categroy;
	}

	public void addParameter(Condition condition, QueryParameter parameter) {
		StringBuilder sb = new StringBuilder();

		sb.append(condition.getInterval());
		sb.append(condition.getDuration());
		sb.append(condition.getAlertType());

		String key = sb.toString();
		AlarmParameter param = m_paramters.get(key);

		if (param == null) {
			param = new AlarmParameter(condition);

			m_paramters.put(key, param);
		}
		param.addParameter(parameter);
	}

	public void addRules(List<Rule> rules) {
		m_ruleConfigs.addAll(rules);
	}

	public List<Rule> getRuleConfigs() {
		return m_ruleConfigs;
	}

	public String getCategory() {
		return m_category;
	}

	public List<Rule> getConfig() {
		return m_ruleConfigs;
	}

	public Map<String, AlarmParameter> getParamters() {
		return m_paramters;
	}

	public ServerAlarmRule getRule() {
		return m_rule;
	}

	public long getStartTime() {
		return m_startTime;
	}

	public boolean isCompleted() {
		return m_completed;
	}

	public void setCategory(String category) {
		m_category = category;
	}

	public void setCompleted(boolean completed) {
		m_completed = completed;
	}

	public void setStartTime(long startTime) {
		m_startTime = startTime;
	}

	public class AlarmParameter {

		private List<QueryParameter> m_queries = new ArrayList<QueryParameter>();

		private Condition m_condition;

		public AlarmParameter(Condition condition) {
			m_condition = condition;
		}

		public Condition getCondition() {
			return m_condition;
		}

		public List<QueryParameter> getQueries() {
			return m_queries;
		}

		public void addParameter(QueryParameter query) {
			m_queries.add(query);
		}

	}

}
