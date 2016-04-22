package org.unidal.cat.message.storage.hdfs;

import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.unidal.cat.message.storage.TokenMapping;
import org.unidal.cat.message.storage.TokenMappingManager;
import org.unidal.lookup.ContainerHolder;
import org.unidal.lookup.annotation.Named;
import org.unidal.tuple.Pair;

@Named(type = TokenMappingManager.class, value = "hdfs")
public class HdfsTokenMappingManager extends ContainerHolder implements TokenMappingManager {
	private Map<Pair<Integer, String>, TokenMapping> m_cache = new LinkedHashMap<Pair<Integer, String>, TokenMapping>() {

		private static final long serialVersionUID = 1L;

		@Override
		protected boolean removeEldestEntry(Entry<Pair<Integer, String>, TokenMapping> eldest) {
			return size() > 100;
		}

	};

	@Override
	public void close(int hour) {
		Set<Pair<Integer, String>> removes = new HashSet<Pair<Integer, String>>();

		for (Entry<Pair<Integer, String>, TokenMapping> entry : m_cache.entrySet()) {
			Pair<Integer, String> entryKey = entry.getKey();
			Integer key = entryKey.getKey();

			if (key <= hour) {
				removes.add(entryKey);
			}
		}

		for (Pair<Integer, String> pair : removes) {
			TokenMapping mapping = null;

			synchronized (this) {
				mapping = m_cache.remove(pair);
			}

			if (mapping != null) {
				mapping.close();
			}
			super.release(mapping);
		}
	}

	@Override
	public TokenMapping getTokenMapping(int hour, String ip) throws IOException {
		Pair<Integer, String> pair = new Pair<Integer, String>(hour, ip);
		TokenMapping mapping = m_cache.get(pair);

		if (mapping == null) {
			synchronized (this) {
				mapping = m_cache.get(pair);

				if (mapping == null) {
					mapping = lookup(TokenMapping.class, "hdfs");
					mapping.open(hour, ip);
					m_cache.put(pair, mapping);
					super.release(mapping);
				}
			}
		}

		return mapping;
	}

}
