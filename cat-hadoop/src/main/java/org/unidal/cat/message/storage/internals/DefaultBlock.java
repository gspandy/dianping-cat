package org.unidal.cat.message.storage.internals;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.zip.Deflater;
import java.util.zip.DeflaterInputStream;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.unidal.cat.message.storage.Block;
import org.xerial.snappy.SnappyInputStream;
import org.xerial.snappy.SnappyOutputStream;

import com.dianping.cat.Cat;
import com.dianping.cat.message.internal.MessageId;

public class DefaultBlock implements Block {
	private static final int MAX_SIZE = 128 * 1024;

	private static final int BUFFER_SIZE = 1024;

	private String m_domain;

	private int m_hour;

	private ByteBuf m_data;

	private int m_offset;

	private Map<MessageId, Integer> m_offsets = new LinkedHashMap<MessageId, Integer>();

	private Map<MessageId, MessageId> m_mappingIds = new LinkedHashMap<MessageId, MessageId>();

	private OutputStream m_out;

	private boolean m_isFulsh;

	private CompressTye m_type = CompressTye.SNAPPY;

	public DefaultBlock(MessageId id, int offset, byte[] data) {
		m_offsets.put(id, offset);
		m_data = data == null ? null : Unpooled.wrappedBuffer(data);
	}

	public DefaultBlock(String domain, int hour) {
		this(domain, hour, CompressTye.SNAPPY);
	}

	public DefaultBlock(String domain, int hour, CompressTye type) {
		m_domain = domain;
		m_hour = hour;
		m_data = Unpooled.buffer(8 * 1024);

		m_out = createOutputSteam(m_data, type);
	}

	@Override
	public void clear() {
		m_data = null;
		m_offsets.clear();
	}

	private InputStream createInputSteam(ByteBuf buf, CompressTye type) {
		ByteBufInputStream os = new ByteBufInputStream(buf);
		InputStream out = null;

		if (type == CompressTye.SNAPPY) {
			try {
				out = new SnappyInputStream(os);
			} catch (IOException e) {
				Cat.logError(e);
			}
		} else if (type == CompressTye.GZIP) {
			try {
				out = new GZIPInputStream(os, BUFFER_SIZE);
			} catch (IOException e) {
				Cat.logError(e);
			}
		} else if (type == CompressTye.DEFLATE) {
			out = new DeflaterInputStream(os, new Deflater(2, true), BUFFER_SIZE);
		}
		return out;
	}

	private OutputStream createOutputSteam(ByteBuf buf, CompressTye type) {
		m_type = type;
		ByteBufOutputStream os = new ByteBufOutputStream(buf);
		OutputStream out = null;

		if (type == CompressTye.SNAPPY) {
			out = new SnappyOutputStream(os);
		} else if (type == CompressTye.GZIP) {
			try {
				out = new GZIPOutputStream(os, BUFFER_SIZE);
			} catch (IOException e) {
				Cat.logError(e);
			}
		} else if (type == CompressTye.DEFLATE) {
			out = new DeflaterOutputStream(os, new Deflater(2, true), BUFFER_SIZE);
		}
		return out;
	}

	@Override
	public ByteBuf find(MessageId id) {
		Integer offset = m_offsets.get(id);

		if (offset != null) {
			finish();

			m_isFulsh = true;

			try {
				ByteBuf copyData = Unpooled.copiedBuffer(m_data);
				@SuppressWarnings("resource")
				DataInputStream in = new DataInputStream(new GZIPInputStream(new ByteBufInputStream(copyData)));

				in.skip(offset);
				int length = in.readInt();
				byte[] result = new byte[length];

				in.readFully(result);

				return Unpooled.wrappedBuffer(result);
			} catch (IOException e) {
				Cat.logError(e);
			}
		}

		return null;
	}

	@Override
	public synchronized void finish() {
		if (m_out != null) {
			try {
				m_out.flush();
				m_out.close();
			} catch (IOException e) {
				// ignore it
			}

			m_out = null;
		}
	}

	@Override
	public ByteBuf getData() throws IOException {
		return m_data;
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
	public Map<MessageId, MessageId> getMappIds() {
		return m_mappingIds;
	}

	@Override
	public Map<MessageId, Integer> getOffsets() {
		return m_offsets;
	}

	@Override
	public boolean isFull() {
		return m_offset >= MAX_SIZE || m_isFulsh;
	}

	@Override
	public void map(MessageId from, MessageId to) {
		m_mappingIds.put(from, to);
	}

	@Override
	public void pack(MessageId id, ByteBuf buf) throws IOException {
		int len = buf.readableBytes();

		buf.readBytes(m_out, len);
		m_offsets.put(id, m_offset);
		m_offset += len;

	}

	@Override
	public ByteBuf unpack(MessageId id) throws IOException {
		if (m_data == null) {
			return null;
		}

		DataInputStream in = new DataInputStream(createInputSteam(m_data, m_type));
		int offset = m_offsets.get(id);

		in.skip(offset);

		int len = in.readInt();
		byte[] data = new byte[len];

		in.readFully(data);
		in.close();

		ByteBuf buf = Unpooled.wrappedBuffer(data);

		return buf;
	}
}
