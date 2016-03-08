package org.unidal.cat.message.storage;

import java.io.File;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.unidal.lookup.ComponentTestCase;

import com.dianping.cat.message.internal.MessageId;

public class IndexTest extends ComponentTestCase {
	@Before
	public void before() throws Exception {
		StorageConfiguration config = lookup(StorageConfiguration.class);

		config.setBaseDataDir(new File("target"));
	}

	@Test
	public void testMapAndLookup() throws Exception {
		MessageId from = MessageId.parse("from-0a260014-403899-76543");
		MessageId expected = MessageId.parse("to-0a260015-403899-12345");
		IndexManager manager = lookup(IndexManager.class, "local");
		Index index = manager.getIndex(from, true);

		index.map(from, expected);

		MessageId actual = index.lookup(from);

		try {
			Assert.assertEquals(expected, actual);
		} finally {
			index.close();
		}
	}
}
