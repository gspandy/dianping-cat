package com.dianping.cat.hadoop.hdfs.bucket;

import java.io.IOException;
import java.util.Date;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.unidal.lookup.annotation.Named;

import com.dianping.cat.hadoop.hdfs.HdfsMessageBucketManager;
import com.dianping.cat.hadoop.hdfs.MessageBlockReader;
import com.dianping.cat.message.storage.MessageBucket;

@Named(type = MessageBucket.class, value = HdfsMessageBucket.ID, instantiationStrategy = Named.PER_LOOKUP)
public class HdfsMessageBucket extends AbstractHdfsMessageBucket {

	public static final String ID = HdfsMessageBucketManager.HDFS_BUCKET;

	@Override
	public void initialize(String dataFile) throws IOException {
		StringBuilder sb = new StringBuilder();
		FileSystem fs = m_manager.getFileSystem(m_id, sb);
		Path basePath = new Path(sb.toString());
		m_reader = new MessageBlockReader(fs, basePath, dataFile);
	}

	@Override
	public void initialize(String dataFile, Date date) throws IOException {
		initialize(dataFile);
	}
}