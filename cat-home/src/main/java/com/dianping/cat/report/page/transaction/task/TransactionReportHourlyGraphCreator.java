package com.dianping.cat.report.page.transaction.task;

import java.util.Calendar;

import org.codehaus.plexus.util.StringUtils;

import com.dianping.cat.consumer.GraphTrendUtil;
import com.dianping.cat.consumer.transaction.model.entity.GraphTrend;
import com.dianping.cat.consumer.transaction.model.entity.Machine;
import com.dianping.cat.consumer.transaction.model.entity.Range;
import com.dianping.cat.consumer.transaction.model.entity.TransactionName;
import com.dianping.cat.consumer.transaction.model.entity.TransactionReport;
import com.dianping.cat.consumer.transaction.model.entity.TransactionType;
import com.dianping.cat.consumer.transaction.model.transform.BaseVisitor;

public class TransactionReportHourlyGraphCreator {

	public static final int DEFAULT_DURATION = 10;

	private TransactionReport m_report;

	private int m_duration;

	public TransactionReportHourlyGraphCreator(TransactionReport transactionReport, int duration) {
		m_report = transactionReport;

		if (duration > 0 && (60 % duration == 0)) {
			m_duration = duration;
		} else {
			m_duration = DEFAULT_DURATION;
		}
	}

	public void createGraph(TransactionReport from) {
		new TransactionReportVisitor().visitTransactionReport(from);
	}

	class TransactionReportVisitor extends BaseVisitor {

		private Machine m_currentMachine;

		private TransactionType m_currentType;

		private TransactionName m_currentName;

		private Integer[] m_currentNameCount = new Integer[60];

		private Integer[] m_currentNameFails = new Integer[60];

		private Double[] m_currentNameSum = new Double[60];

		private Integer[] m_currentTypeCount = new Integer[60];

		private Integer[] m_currentTypeFails = new Integer[60];

		private Double[] m_currentTypeSum = new Double[60];

		private int m_currentHour;

		private int m_graphLength;

		private void buildGraphTrend(GraphTrend graph, boolean isType) {
			Long[] count = GraphTrendUtil.parseToLong(graph.getCount(), m_graphLength);
			Long[] fails = GraphTrendUtil.parseToLong(graph.getFails(), m_graphLength);
			Double[] sum = GraphTrendUtil.parseToDouble(graph.getSum(), m_graphLength);
			Double[] avg = GraphTrendUtil.parseToDouble(graph.getAvg(), m_graphLength);

			long countValue = 0;
			double sumValue = 0;
			long failsValue = 0;
			double avgValue = 0;

			for (int i = 0; i < 60; i++) {
				countValue += isType ? m_currentTypeCount[i] : m_currentNameCount[i];
				sumValue += isType ? m_currentTypeSum[i] : m_currentNameSum[i];
				failsValue += isType ? m_currentTypeFails[i] : m_currentNameFails[i];

				if ((i + 1) % m_duration == 0) {
					if (countValue > 0) {
						avgValue = sumValue / countValue;
					}

					int index = m_currentHour * (60 / m_duration) + (i + 1) / m_duration - 1;
					count[index] = (long) Math.ceil((countValue + 0.0) / m_duration);
					fails[index] = (long) Math.ceil((failsValue + 0.0) / m_duration);
					sum[index] = ((int) ((sumValue * 100) / m_duration)) / 100.0;
					avg[index] = ((int) (avgValue * 100)) / 100.0;

					countValue = 0;
					sumValue = 0;
					avgValue = 0;
					failsValue = 0;
				}
			}

			graph.setCount(StringUtils.join(count, GraphTrendUtil.GRAPH_SPLITTER));
			graph.setAvg(StringUtils.join(avg, GraphTrendUtil.GRAPH_SPLITTER));
			graph.setSum(StringUtils.join(sum, GraphTrendUtil.GRAPH_SPLITTER));
			graph.setFails(StringUtils.join(fails, GraphTrendUtil.GRAPH_SPLITTER));
		}

		private void initNameParams() {
			for (int i = 0; i < 60; i++) {
				m_currentNameCount[i] = 0;
				m_currentNameFails[i] = 0;
				m_currentNameSum[i] = 0.0;
			}
		}

		private void initTypeParams() {
			for (int i = 0; i < 60; i++) {
				m_currentTypeCount[i] = 0;
				m_currentTypeFails[i] = 0;
				m_currentTypeSum[i] = 0.0;
			}
		}

		@Override
		public void visitMachine(Machine machine) {
			String ip = machine.getIp();
			m_currentMachine = m_report.findOrCreateMachine(ip);
			super.visitMachine(machine);
		}

		@Override
		public void visitName(TransactionName name) {
			String nameId = name.getId();
			m_currentName = m_currentType.findOrCreateName(nameId);

			initNameParams();
			super.visitName(name);

			GraphTrend graphTrend = m_currentName.getGraphTrend();

			if (graphTrend == null) {
				graphTrend = new GraphTrend();
				graphTrend.setDuration(m_duration);
				m_currentName.setGraphTrend(graphTrend);
			}
			buildGraphTrend(graphTrend, false);
		}

		@Override
		public void visitRange(Range range) {
			int index = range.getValue();
			m_currentNameCount[index] = range.getCount();
			m_currentNameSum[index] = range.getSum();
			m_currentNameFails[index] = range.getFails();
			m_currentTypeCount[index] += range.getCount();
			m_currentTypeSum[index] += range.getSum();
			m_currentTypeFails[index] += range.getFails();
		}

		@Override
		public void visitTransactionReport(TransactionReport transactionReport) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(transactionReport.getStartTime());

			m_currentHour = cal.get(Calendar.HOUR_OF_DAY);
			m_graphLength = (60 / m_duration) * 24;
			super.visitTransactionReport(transactionReport);
		}

		@Override
		public void visitType(TransactionType type) {
			String typeId = type.getId();
			m_currentType = m_currentMachine.findOrCreateType(typeId);

			initTypeParams();
			super.visitType(type);

			GraphTrend graphTrend = m_currentType.getGraphTrend();

			if (graphTrend == null) {
				graphTrend = new GraphTrend();
				graphTrend.setDuration(m_duration);
				m_currentType.setGraphTrend(graphTrend);
			}
			buildGraphTrend(graphTrend, true);
		}
	}

}
