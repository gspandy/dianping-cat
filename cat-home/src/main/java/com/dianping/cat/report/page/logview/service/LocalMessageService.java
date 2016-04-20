package com.dianping.cat.report.page.logview.service;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

import java.io.IOException;
import java.nio.charset.Charset;

import org.unidal.cat.message.storage.Bucket;
import org.unidal.cat.message.storage.BucketManager;
import org.unidal.cat.message.storage.Index;
import org.unidal.cat.message.storage.IndexManager;
import org.unidal.cat.message.storage.MessageFinderManager;
import org.unidal.lookup.annotation.Inject;
import org.unidal.lookup.annotation.Named;

import com.dianping.cat.Cat;
import com.dianping.cat.configuration.NetworkInterfaceManager;
import com.dianping.cat.consumer.dump.DumpAnalyzer;
import com.dianping.cat.message.Message;
import com.dianping.cat.message.Transaction;
import com.dianping.cat.message.codec.HtmlMessageCodec;
import com.dianping.cat.message.codec.WaterfallMessageCodec;
import com.dianping.cat.message.internal.MessageId;
import com.dianping.cat.message.spi.MessageCodec;
import com.dianping.cat.message.spi.MessageTree;
import com.dianping.cat.message.spi.codec.PlainTextMessageCodec;
import com.dianping.cat.mvc.ApiPayload;
import com.dianping.cat.report.service.LocalModelService;
import com.dianping.cat.report.service.ModelPeriod;
import com.dianping.cat.report.service.ModelRequest;
import com.dianping.cat.report.service.ModelResponse;
import com.dianping.cat.report.service.ModelService;

@Named(type = LocalModelService.class, value = "logview")
public class LocalMessageService extends LocalModelService<String> implements ModelService<String> {
	public static final String ID = DumpAnalyzer.ID;

	@Inject
	private MessageFinderManager m_finderManager;

	@Inject("local")
	private BucketManager m_bucketManager;

	@Inject("local")
	private IndexManager m_indexManager;

	@Inject(HtmlMessageCodec.ID)
	private MessageCodec m_html;

	@Inject(WaterfallMessageCodec.ID)
	private MessageCodec m_waterfall;

	@Inject(PlainTextMessageCodec.ID)
	private MessageCodec m_plainText;

	public LocalMessageService() {
		super("logview");
	}

	@Override
	public String buildReport(ModelRequest request, ModelPeriod period, String domain, ApiPayload payload)
	      throws Exception {
		String messageId = payload.getMessageId();
		boolean waterfull = payload.isWaterfall();
		MessageId id = MessageId.parse(messageId);

		if (payload.isMap()) {
			MessageId mapId = findIndex(id);

			if (mapId != null) {
				id = mapId;
			}
		}

		ByteBuf buf = m_finderManager.find(id);
		MessageTree tree = null;

		try {
			if (buf != null) {
				tree = m_plainText.decode(buf);
			}

			if (tree == null) {
				Bucket bucket = m_bucketManager.getBucket(id.getDomain(),
				      NetworkInterfaceManager.INSTANCE.getLocalHostAddress(), id.getHour(), false);

				if (bucket != null) {
					bucket.flush();

					ByteBuf data = bucket.get(id);

					if (data != null) {
						tree = m_plainText.decode(data);
					}
				}
			}
		} finally {
			m_plainText.reset();
		}

		if (tree != null) {
			ByteBuf content = ByteBufAllocator.DEFAULT.buffer(8192);

			if (tree.getMessage() instanceof Transaction && waterfull) {
				m_waterfall.encode(tree, content);
			} else {
				m_html.encode(tree, content);
			}

			try {
				content.readInt(); // get rid of length
				return content.toString(Charset.forName("utf-8"));
			} catch (Exception e) {
				// ignore it
			}
		}

		return null;
	}

	private MessageId findIndex(MessageId from) throws IOException {
		int hour = from.getHour();
		String domain = from.getDomain();
		String ip = NetworkInterfaceManager.INSTANCE.getLocalHostAddress();

		Index index = m_indexManager.getIndex(domain, ip, hour, false);

		return index.find(from);
	}

	@Override
	public ModelResponse<String> invoke(ModelRequest request) {
		ModelResponse<String> response = new ModelResponse<String>();
		Transaction t = Cat.newTransaction("ModelService", getClass().getSimpleName());

		try {
			ModelPeriod period = request.getPeriod();
			String domain = request.getDomain();
			ApiPayload payload = new ApiPayload();

			payload.setMessageId(request.getProperty("messageId"));
			payload.setWaterfall(Boolean.valueOf(request.getProperty("waterfall", "false")));

			String report = getReport(request, period, domain, payload);

			response.setModel(report);

			t.addData("period", period);
			t.addData("domain", domain);
			t.setStatus(Message.SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			Cat.logError(e);
			t.setStatus(e);
			response.setException(e);
		} finally {
			t.complete();
		}
		return response;
	}

	@Override
	public boolean isEligable(ModelRequest request) {
		if (m_manager.isHdfsOn()) {
			boolean eligibale = request.getPeriod().isCurrent();

			return eligibale;
		} else {
			return true;
		}
	}

}
