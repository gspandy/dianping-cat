package org.unidal.cat.message.storage;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.unidal.helper.Files;
import org.unidal.lookup.ComponentTestCase;

public class TokenMappingTest extends ComponentTestCase {

	@Before
	public void setUp() {
		StorageConfiguration config = lookup(StorageConfiguration.class);
		File target = new File("target");

		config.setBaseDataDir(target);

		Files.forDir().delete(target, true);
	}

	@Test
	public void test() throws IOException {
		TokenMapping mapping = lookup(TokenMapping.class, "local");
		Date timestamp = new Date(1454901510210L); // Mon Feb 08 11:18:30 CST 2016

		for (int times = 0; times < 3; times++) {
			mapping.open(timestamp, "127.0.0.1");

			for (int i = 0; i < 64 * 1024; i++) {
				String expected = "token-mapping-" + i;
				int index = mapping.map(expected);
				String actual = mapping.lookup(index);

				Assert.assertEquals(i + 1, index);
				Assert.assertEquals(expected, actual);
			}

			mapping.close();
		}
	}

	@Test
	public void testMany() throws IOException {
		StorageConfiguration config = lookup(StorageConfiguration.class);

		config.setBaseDataDir(new File("target"));

		TokenMapping mapping = lookup(TokenMapping.class, "local");
		Date timestamp = new Date(1454901510210L); // Mon Feb 08 11:18:30 CST 2016

		for (int times = 0; times < 3; times++) {
			mapping.open(timestamp, "127.0.0.1");

			for (int i = 0; i < 64 * 1024 * 10; i++) {
				String expected = "token-mapping-" + i;
				int index = mapping.map(expected);
				String actual = mapping.lookup(index);

				Assert.assertEquals(i + 1, index);
				Assert.assertEquals(expected, actual);
			}

			mapping.close();
		}
	}

}
