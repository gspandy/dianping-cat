package org.unidal.cat.message.storage;

import java.io.IOException;
import java.util.Date;

public interface TokenMappingManager {
	public TokenMapping getTokenMapping(Date startTime, String ip) throws IOException;
}
