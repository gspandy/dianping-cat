package org.unidal.cat.message.storage;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.unidal.cat.message.storage.internals.DefaultBlock;
import org.unidal.helper.Files;
import org.unidal.lookup.ComponentTestCase;

import com.dianping.cat.message.internal.MessageId;
import com.dianping.cat.message.spi.MessageCodec;
import com.dianping.cat.message.spi.MessageTree;
import com.dianping.cat.message.spi.codec.PlainTextMessageCodec;

public class BucketTest extends ComponentTestCase {
	private MessageCodec m_codec;

	@Before
	public void before() {
		File baseDir = new File("target");

		Files.forDir().delete(new File(baseDir, "dump"), true);

		lookup(StorageConfiguration.class).setBaseDataDir(baseDir);
		m_codec = lookup(MessageCodec.class, PlainTextMessageCodec.ID);
	}

	@Test
	public void testWriteAndRead() throws Exception {
		String ip = "0a010203";
		String domain = "mock";
		int hour = 404857;
		BucketManager manager = lookup(BucketManager.class, "local");
		Bucket bucket = manager.getBucket(domain, ip, hour, true);

		for (int i = 0; i < 1000; i++) {
			Block block = new DefaultBlock(domain, hour);

			for (int index = 0; index < 10; index++) {
				MessageId id = new MessageId(domain, ip, hour, i * 10 + index);
				MessageTree tree = TreeHelper.tree(m_codec, id);

				block.pack(id, tree.getBuffer());
			}

			block.finish();
			bucket.puts(block.getData(), block.getMappings());

			for (MessageId id : block.getMappings().keySet()) {
				ByteBuf buf = bucket.get(id);
				MessageTree tree = m_codec.decode(buf);

				Assert.assertEquals(id.toString(), tree.getMessageId());
			}
		}
	}

	@Test
	public void testWritePerf() throws IOException {
		BucketManager manager = lookup(BucketManager.class, "local");

		for (int i = 0; i < 100000; i++) {
			String domain = "mock";
			Bucket bucket = manager.getBucket(domain, "0a010203", 404448, true);

			Block block = new MockBlock(domain, 404448, 10, i);

			try {
				bucket.puts(block.getData(), block.getMappings());
			} catch (Exception e) {
				System.out.println(i);
				e.printStackTrace();
				break;
			}
		}

		manager.closeBuckets();
	}

	private static class MockBlock implements Block {
		private String m_domain;

		private int m_hour;

		private int m_capacity = 1536;

		private Map<MessageId, Integer> m_mappings = new HashMap<MessageId, Integer>();

		public MockBlock(String domain, int hour, int count, int index) {
			m_domain = domain;
			m_hour = hour;

			String ip = "10.1.2.4";

			for (int i = 0; i < count; i++) {
				MessageId id = new MessageId(domain, ip, hour, count * index + i);

				m_mappings.put(id, i % m_capacity);
			}
		}

		@Override
		public void finish() {
		}

		@Override
		public ByteBuf getData() throws IOException {
			ByteBuf buf = Unpooled.buffer(m_capacity);

			buf.writeZero(m_capacity);
			return buf;
		}

		@Override
		public String getDomain() {
			return m_domain;
		}

		@Override
		public int getHour() {
			return m_hour;
		}

		@Override
		public Map<MessageId, Integer> getMappings() {
			return m_mappings;
		}

		@Override
		public boolean isFull() {
			return true;
		}

		@Override
		public void pack(MessageId id, ByteBuf buf) throws IOException {
		}

		@Override
		public ByteBuf unpack(MessageId id) throws IOException {
			throw new UnsupportedOperationException();
		}

		@Override
      public ByteBuf findTree(MessageId id) {
	      // TODO Auto-generated method stub
	      return null;
      }
	}
}
