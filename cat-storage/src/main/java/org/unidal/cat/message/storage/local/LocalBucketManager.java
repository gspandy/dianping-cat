package org.unidal.cat.message.storage.local;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import org.codehaus.plexus.logging.LogEnabled;
import org.codehaus.plexus.logging.Logger;
import org.unidal.cat.message.storage.Bucket;
import org.unidal.cat.message.storage.BucketManager;
import org.unidal.cat.message.storage.FileBuilder;
import org.unidal.cat.message.storage.FileBuilder.FileType;
import org.unidal.lookup.ContainerHolder;
import org.unidal.lookup.annotation.Inject;
import org.unidal.lookup.annotation.Named;

import com.dianping.cat.helper.TimeHelper;

@Named(type = BucketManager.class, value = "local")
public class LocalBucketManager extends ContainerHolder implements BucketManager, LogEnabled {

	@Inject("local")
	private FileBuilder m_bulider;

	private Map<Integer, Map<String, Bucket>> m_buckets = new LinkedHashMap<Integer, Map<String, Bucket>>();

	private Logger m_logger;

	@Override
	public void closeBuckets(long timestamp) {
		int hour = (int) (timestamp / TimeHelper.ONE_HOUR);
		Map<String, Bucket> map = m_buckets.get(hour);

		if (map != null) {
			for (Bucket bucket : map.values()) {
				bucket.close();
				m_logger.info("close bucket " + bucket.toString());
				super.release(bucket);
			}
		}
		m_buckets.remove(hour);
	}

	@Override
	public void enableLogging(Logger logger) {
		m_logger = logger;
	}

	private boolean exsitBucket(String domain, String ip, int hour) {
		long timestamp = hour * 3600 * 1000L;
		Date startTime = new Date(timestamp);
		File dataPath = m_bulider.getFile(domain, startTime, ip, FileType.DATA);
		File indexPath = m_bulider.getFile(domain, startTime, ip, FileType.INDEX);

		return dataPath.exists() && indexPath.exists();
	}

	private Map<String, Bucket> findOrCreateMap(Map<Integer, Map<String, Bucket>> map, int hour,
	      boolean createIfNotExists) {
		Map<String, Bucket> m = map.get(hour);

		if (m == null && createIfNotExists) {
			synchronized (map) {
				m = map.get(hour);

				if (m == null) {
					m = new LinkedHashMap<String, Bucket>();
					map.put(hour, m);
				}
			}
		}

		return m;
	}

	@Override
	public Bucket getBucket(String domain, String ip, int hour, boolean createIfNotExists) throws IOException {
		Map<String, Bucket> map = findOrCreateMap(m_buckets, hour, createIfNotExists);
		Bucket bucket = map == null ? null : map.get(domain);

		if (bucket == null && createIfNotExists) {
			synchronized (map) {
				bucket = map.get(domain);

				if (bucket == null) {
					bucket = lookup(Bucket.class, "local");
					bucket.initialize(domain, ip, hour);
					map.put(domain, bucket);
				}
			}
		} else if (createIfNotExists == false) {
			if (exsitBucket(domain, ip, hour)) {
				bucket = lookup(Bucket.class, "local");
				bucket.initialize(domain, ip, hour);
			}
		}

		return bucket;
	}
}
