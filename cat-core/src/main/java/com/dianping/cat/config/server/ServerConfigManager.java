package com.dianping.cat.config.server;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.regex.Pattern;

import org.codehaus.plexus.logging.LogEnabled;
import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;
import org.unidal.dal.jdbc.DalNotFoundException;
import org.unidal.helper.Files;
import org.unidal.helper.Splitters;
import org.unidal.helper.Threads;
import org.unidal.lookup.annotation.Inject;
import org.unidal.tuple.Pair;
import org.xml.sax.SAXException;

import com.dianping.cat.Cat;
import com.dianping.cat.Constants;
import com.dianping.cat.config.content.ContentFetcher;
import com.dianping.cat.configuration.NetworkInterfaceManager;
import com.dianping.cat.configuration.server.entity.Domain;
import com.dianping.cat.configuration.server.entity.HarfsConfig;
import com.dianping.cat.configuration.server.entity.HdfsConfig;
import com.dianping.cat.configuration.server.entity.LongConfig;
import com.dianping.cat.configuration.server.entity.Property;
import com.dianping.cat.configuration.server.entity.Server;
import com.dianping.cat.configuration.server.entity.ServerConfig;
import com.dianping.cat.configuration.server.entity.StorageConfig;
import com.dianping.cat.configuration.server.transform.DefaultSaxParser;
import com.dianping.cat.core.config.Config;
import com.dianping.cat.core.config.ConfigDao;
import com.dianping.cat.core.config.ConfigEntity;
import com.dianping.cat.task.ConfigSyncTask;
import com.dianping.cat.task.ConfigSyncTask.SyncHandler;

public class ServerConfigManager implements LogEnabled, Initializable {

	@Inject
	protected ConfigDao m_configDao;

	@Inject
	protected ContentFetcher m_fetcher;

	private int m_configId;

	private long m_modifyTime;

	private static final String CONFIG_NAME = "server-config";

	private volatile ServerConfig m_config;

	private volatile Server m_server;

	private Logger m_logger;

	public ExecutorService m_threadPool;

	private static final long DEFAULT_HDFS_FILE_MAX_SIZE = 128 * 1024 * 1024L; // 128M

	public static final String DUMP_DIR = "dump";

	public static final String DEFAULT = "default";

	@Override
	public void enableLogging(Logger logger) {
		m_logger = logger;
	}

	public ServerConfig getConfig() {
		return m_config;
	}

	public String getConsoleDefaultDomain() {
		return Constants.CAT;
	}

	public List<Pair<String, Integer>> getConsoleEndpoints() {
		String remoteServers = getProperty(ServerConstants.REMOTE_SERVERS, "");
		List<String> endpoints = Splitters.by(',').noEmptyItem().trim().split(remoteServers);
		List<Pair<String, Integer>> pairs = new ArrayList<Pair<String, Integer>>(endpoints.size());

		for (String endpoint : endpoints) {
			int pos = endpoint.indexOf(':');
			String host = (pos > 0 ? endpoint.substring(0, pos) : endpoint);
			int port = (pos > 0 ? Integer.parseInt(endpoint.substring(pos + 1)) : 2281);

			pairs.add(new Pair<String, Integer>(host, port));
		}

		return pairs;
	}

	public String getConsoleRemoteServers() {
		String remoteServers = getProperty(ServerConstants.REMOTE_SERVERS, "");

		if (remoteServers != null && remoteServers.length() > 0) {
			return remoteServers;
		} else {
			return "";
		}
	}

	public String getHarfsBaseDir(String id) {
		if (m_server != null) {
			HarfsConfig harfsConfig = m_server.getStorage().findHarfs(id);

			if (harfsConfig != null) {
				String baseDir = harfsConfig.getBaseDir();

				if (baseDir != null && baseDir.trim().length() > 0) {
					return baseDir;
				}
			}
		}
		return null;
	}

	public long getHarfsFileMaxSize(String id) {
		if (m_server != null) {
			HarfsConfig hdfsConfig = m_server.getStorage().findHarfs(id);

			return toLong(hdfsConfig == null ? null : hdfsConfig.getMaxSize(), DEFAULT_HDFS_FILE_MAX_SIZE);
		} else {
			return DEFAULT_HDFS_FILE_MAX_SIZE;
		}
	}

	public String getHarfsServerUri(String id) {
		if (m_server != null) {
			HarfsConfig hdfsConfig = m_server.getStorage().findHarfs(id);

			if (hdfsConfig != null) {
				String serverUri = hdfsConfig.getServerUri();

				if (serverUri != null && serverUri.trim().length() > 0) {
					return serverUri;
				}
			}
		}

		return null;
	}

	public String getHdfsBaseDir(String id) {
		if (m_server != null) {
			HdfsConfig hdfsConfig = m_server.getStorage().findHdfs(id);

			if (hdfsConfig != null) {
				String baseDir = hdfsConfig.getBaseDir();

				if (baseDir != null && baseDir.trim().length() > 0) {
					return baseDir;
				}
			}
		}
		return null;
	}

	public long getHdfsFileMaxSize(String id) {
		if (m_server != null) {
			HdfsConfig hdfsConfig = m_server.getStorage().findHdfs(id);

			return toLong(hdfsConfig == null ? null : hdfsConfig.getMaxSize(), DEFAULT_HDFS_FILE_MAX_SIZE);
		} else {
			return DEFAULT_HDFS_FILE_MAX_SIZE;
		}
	}

	public String getHdfsLocalBaseDir(String id) {
		if (m_server != null) {
			StorageConfig storage = m_server.getStorage();

			return new File(storage.getLocalBaseDir(), id).getPath();
		} else if (id == null) {
			return "target/bucket";
		} else {
			return "target/bucket/" + id;
		}
	}

	public int getHdfsMaxStorageTime() {
		if (m_server != null) {
			StorageConfig storage = m_server.getStorage();

			return storage.getMaxHdfsStorageTime();
		} else {
			return 15;
		}
	}

	public Map<String, String> getHdfsProperties() {
		if (m_server != null) {
			Map<String, String> properties = new HashMap<String, String>();

			for (Property p : m_server.getStorage().getProperties().values()) {
				properties.put(p.getName(), p.getValue());
			}

			return properties;
		} else {
			return Collections.emptyMap();
		}
	}

	public String getHdfsServerUri(String id) {
		if (m_server != null) {
			HdfsConfig hdfsConfig = m_server.getStorage().findHdfs(id);

			if (hdfsConfig != null) {
				String serverUri = hdfsConfig.getServerUri();

				if (serverUri != null && serverUri.trim().length() > 0) {
					return serverUri;
				}
			}
		}

		return null;
	}

	public int getHdfsUploadThreadCount() {
		if (m_server != null) {
			StorageConfig storage = m_server.getStorage();

			return storage.getUploadThread();
		} else {
			return 5;
		}
	}

	public int getLocalReportStroageTime() {
		if (m_server != null) {
			StorageConfig storage = m_server.getStorage();

			return storage.getLocalReportStorageTime();
		} else {
			return 7;
		}
	}

	public int getLogViewStroageTime() {
		if (m_server != null) {
			StorageConfig storage = m_server.getStorage();

			return storage.getLocalLogivewStorageTime();
		} else {
			return 30;
		}
	}

	public Map<String, Domain> getLongConfigDomains() {
		if (m_server != null) {
			LongConfig longConfig = m_server.getConsumer().getLongConfig();

			if (longConfig != null) {
				return longConfig.getDomains();
			}
		}
		return Collections.emptyMap();
	}

	public int getLongUrlDefaultThreshold() {
		if (m_server != null) {
			LongConfig longConfig = m_server.getConsumer().getLongConfig();

			if (longConfig != null) {
				return longConfig.getDefaultSqlThreshold();
			}
		}
		return 1000; // 1 second
	}

	public ExecutorService getModelServiceExecutorService() {
		return m_threadPool;
	}

	public String getProperty(String name, String defaultValue) {
		if (m_server != null) {
			Property property = m_server.findProperty(name);

			if (property != null) {
				return property.getValue();
			}
		}
		return defaultValue;
	}

	public ServerConfig getServerConfig() {
		return m_config;
	}

	@Override
	public void initialize() throws InitializationException {
		try {
			Config config = m_configDao.findByName(CONFIG_NAME, ConfigEntity.READSET_FULL);
			String content = config.getContent();

			m_configId = config.getId();
			m_modifyTime = config.getModifyDate().getTime();
			m_config = DefaultSaxParser.parse(content);
		} catch (DalNotFoundException e) {
			try {
				String content = m_fetcher.getConfigContent(CONFIG_NAME);
				Config config = m_configDao.createLocal();

				config.setName(CONFIG_NAME);
				config.setContent(content);
				m_configDao.insert(config);
				m_configId = config.getId();
				m_config = DefaultSaxParser.parse(content);
			} catch (Exception ex) {
				Cat.logError(ex);
			}
		} catch (Exception e) {
			Cat.logError(e);
		}
		if (m_config == null) {
			m_config = new ServerConfig();
		}

		try {
			refreshServer();
		} catch (Exception e) {
			Cat.logError(e);
		}

		ConfigSyncTask.getInstance().register(new SyncHandler() {

			@Override
			public String getName() {
				return CONFIG_NAME;
			}

			@Override
			public void handle() throws Exception {
				refreshConfig();
			}
		});

		prepare();
	}

	public void initialize(File configFile) throws Exception {
		if (configFile != null && configFile.canRead()) {
			m_logger.info(String.format("Loading configuration file(%s) ...", configFile.getCanonicalPath()));

			String xml = Files.forIO().readFrom(configFile, "utf-8");
			ServerConfig config = DefaultSaxParser.parse(xml);

			// do validation
			config.accept(new ServerConfigValidator());
			m_config = config;
		} else {
			if (configFile != null) {
				m_logger.warn(String.format("Configuration file(%s) not found, IGNORED.", configFile.getCanonicalPath()));
			}

			ServerConfig config = new ServerConfig();

			// do validation
			config.accept(new ServerConfigValidator());
			m_config = config;
		}

		refreshServer();

		if (isLocalMode()) {
			m_logger.warn("CAT server is running in LOCAL mode! No HDFS or MySQL will be accessed!");
		}
		m_logger.info("CAT server is running with hdfs," + isHdfsOn());
		m_logger.info("CAT server is running with alert," + isAlertMachine());
		m_logger.info("CAT server is running with job," + isJobMachine());
		m_logger.info(m_config.toString());

		if (isLocalMode()) {
			m_threadPool = Threads.forPool().getFixedThreadPool("Cat-ModelService", 5);
		} else {
			m_threadPool = Threads.forPool().getFixedThreadPool("Cat-ModelService", 100);
		}
	}

	public boolean insert(String xml) {
		try {
			m_config = DefaultSaxParser.parse(xml);

			return storeConfig();
		} catch (Exception e) {
			Cat.logError(e);
			return false;
		}
	}

	public boolean isAlertMachine() {
		boolean alert = Boolean.parseBoolean(getProperty(ServerConstants.ALARM_MACHINE, "false"));
		String ip = NetworkInterfaceManager.INSTANCE.getLocalHostAddress();

		if ("10.1.6.128".equals(ip) || alert) {
			return true;
		} else {
			return false;
		}
	}

	public boolean isHarMode() {
		if (m_server != null) {
			return m_server.getStorage().isHarMode();
		} else {
			return false;
		}
	}

	public boolean isHdfsOn() {
		return Boolean.parseBoolean(getProperty(ServerConstants.HDFS_ENABLED, "false"));
	}

	public boolean isJobMachine() {
		return Boolean.parseBoolean(getProperty(ServerConstants.JOB_MACHINE, "false"));
	}

	public boolean isLocalMode() {
		return Boolean.parseBoolean(getProperty(ServerConstants.LOCAL_MODE, "false"));
	}

	public boolean isRpcClient(String type) {
		return "PigeonCall".equals(type) || "Call".equals(type);
	}

	public boolean isRpcServer(String type) {
		return "PigeonService".equals(type) || "Service".equals(type);
	}

	public boolean isSendMachine() {
		return Boolean.parseBoolean(getProperty(ServerConstants.SEND_MACHINE, "false"));
	}

	private void prepare() {
		if (isLocalMode()) {
			m_logger.warn("CAT server is running in LOCAL mode! No HDFS or MySQL will be accessed!");
		}
		m_logger.info("CAT server is running with hdfs," + isHdfsOn());
		m_logger.info("CAT server is running with alert," + isAlertMachine());
		m_logger.info("CAT server is running with job," + isJobMachine());
		m_logger.info(m_server.toString());

		if (isLocalMode()) {
			m_threadPool = Threads.forPool().getFixedThreadPool("Cat-ModelService", 5);
		} else {
			m_threadPool = Threads.forPool().getFixedThreadPool("Cat-ModelService", 100);
		}
	}

	private void refreshConfig() throws Exception {
		Config config = m_configDao.findByName(CONFIG_NAME, ConfigEntity.READSET_FULL);
		long modifyTime = config.getModifyDate().getTime();

		synchronized (this) {
			if (modifyTime > m_modifyTime) {
				ServerConfig serverConfig = DefaultSaxParser.parse(config.getContent());
				serverConfig.accept(new ServerConfigValidator());

				m_config = serverConfig;
				m_modifyTime = modifyTime;

				refreshServer();
			}
		}
	}

	private void refreshServer() throws SAXException, IOException {
		String ip = NetworkInterfaceManager.INSTANCE.getLocalHostAddress();
		ServerConfig config = DefaultSaxParser.parse(m_config.toString());
		Server defaultServer = config.findServer(DEFAULT).setId(ip);
		Server server = config.findServer(ip);

		if (server != null && defaultServer != null) {
			ServerConfigVisitor visitor = new ServerConfigVisitor(server);

			visitor.visitServer(defaultServer);
		}
		m_server = defaultServer;
	}

	public boolean storeConfig() {
		try {
			Config config = m_configDao.createLocal();

			config.setId(m_configId);
			config.setKeyId(m_configId);
			config.setName(CONFIG_NAME);
			config.setContent(m_config.toString());
			m_configDao.updateByPK(config, ConfigEntity.UPDATESET_FULL);
			refreshServer();
		} catch (Exception e) {
			Cat.logError(e);
			return false;
		}
		return true;
	}

	private long toLong(String str, long defaultValue) {
		long value = 0;
		int len = str == null ? 0 : str.length();

		for (int i = 0; i < len; i++) {
			char ch = str.charAt(i);

			if (Character.isDigit(ch)) {
				value = value * 10L + (ch - '0');
			} else if (ch == 'm' || ch == 'M') {
				value *= 1024 * 1024L;
			} else if (ch == 'k' || ch == 'K') {
				value *= 1024L;
			}
		}

		if (value > 0) {
			return value;
		} else {
			return defaultValue;
		}
	}

	public boolean validateIp(String str) {
		Pattern pattern = Pattern
		      .compile("^((\\d|[1-9]\\d|1\\d\\d|2[0-4]\\d|25[0-5]|[*])\\.){3}(\\d|[1-9]\\d|1\\d\\d|2[0-4]\\d|25[0-5]|[*])$");
		return pattern.matcher(str).matches();
	}

}
