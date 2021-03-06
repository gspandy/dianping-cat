CREATE TABLE `ajax_data_1` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '自增长ID',
  `period` date NOT NULL COMMENT '时间',
  `minute_order` smallint(6) NOT NULL COMMENT '分钟',
  `city` smallint(6) NOT NULL COMMENT '城市',
  `operator` tinyint(4) NOT NULL COMMENT '运营商',
  `code` smallint(6) NOT NULL COMMENT '返回码',
  `network` tinyint(4) NOT NULL COMMENT '网络类型',
  `access_number` bigint(20) NOT NULL COMMENT '访问量',
  `response_sum_time` bigint(20) NOT NULL COMMENT '响应时间大小',
  `request_sum_byte` bigint(20) NOT NULL COMMENT '发送字节',
  `response_sum_byte` bigint(20) NOT NULL COMMENT '返回字节',
  `updatetime` datetime NOT NULL COMMENT '数据更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `IX_condition` (`period`,`minute_order`,`city`,`operator`,`code`,`network`),
  KEY `updatetime` (`updatetime`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='web基本数据';

CREATE TABLE `js_error_log` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `module` varchar(50) NOT NULL DEFAULT '' COMMENT '出错的js模块',
  `browser` varchar(50) DEFAULT NULL COMMENT '浏览器',
  `level` tinyint(4) NOT NULL COMMENT '错误级别',
  `msg` varchar(200) NOT NULL DEFAULT '' COMMENT '出错的简要信息，用于分类',
  `creation_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'log创建时间',
  `error_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '错误发生时间',
  `updatetime` datetime NOT NULL COMMENT '数据更新时间',
  `dpid` varchar(200) DEFAULT NULL COMMENT '用户ID值',
  PRIMARY KEY (`id`),
  KEY `IX_CONDITION` (`error_time`,`module`),
  KEY `updatetime` (`updatetime`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `js_error_log_content` (
  `id` int(11) unsigned NOT NULL,
  `content` longblob COMMENT '出错的详细信息',
  `creation_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'log创建时间',
  `updatetime` datetime NOT NULL COMMENT '数据更新时间',
  PRIMARY KEY (`id`),
  KEY `updatetime` (`updatetime`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `web_speed_data_1` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '自增长ID',
  `period` date NOT NULL COMMENT '时间',
  `minute_order` smallint(6) NOT NULL COMMENT '分钟',
  `city` smallint(6) NOT NULL COMMENT '城市',
  `platform` smallint(6) NOT NULL COMMENT '平台',
  `operator` smallint(6) NOT NULL COMMENT '运营商',
  `network` smallint(6) NOT NULL COMMENT '网络类型',
  `source` smallint(6) NOT NULL COMMENT '来源',
  `access_number1` bigint(20) DEFAULT '0' COMMENT '访问量',
  `access_number2` bigint(20) DEFAULT '0' COMMENT '访问量',
  `access_number3` bigint(20) DEFAULT '0' COMMENT '访问量',
  `access_number4` bigint(20) DEFAULT '0' COMMENT '访问量',
  `access_number5` bigint(20) DEFAULT '0' COMMENT '访问量',
  `access_number6` bigint(20) DEFAULT '0' COMMENT '访问量',
  `access_number7` bigint(20) DEFAULT '0' COMMENT '访问量',
  `access_number8` bigint(20) DEFAULT '0' COMMENT '访问量',
  `access_number9` bigint(20) DEFAULT '0' COMMENT '访问量',
  `access_number10` bigint(20) DEFAULT '0' COMMENT '访问量',
  `access_number11` bigint(20) DEFAULT '0' COMMENT '访问量',
  `access_number12` bigint(20) DEFAULT '0' COMMENT '访问量',
  `access_number13` bigint(20) DEFAULT '0' COMMENT '访问量',
  `access_number14` bigint(20) DEFAULT '0' COMMENT '访问量',
  `access_number15` bigint(20) DEFAULT '0' COMMENT '访问量',
  `access_number16` bigint(20) DEFAULT '0' COMMENT '访问量',
  `access_number17` bigint(20) DEFAULT '0' COMMENT '访问量',
  `access_number18` bigint(20) DEFAULT '0' COMMENT '访问量',
  `access_number19` bigint(20) DEFAULT '0' COMMENT '访问量',
  `access_number20` bigint(20) DEFAULT '0' COMMENT '访问量',
  `access_number21` bigint(20) DEFAULT '0' COMMENT '访问量',
  `access_number22` bigint(20) DEFAULT '0' COMMENT '访问量',
  `access_number23` bigint(20) DEFAULT '0' COMMENT '访问量',
  `access_number24` bigint(20) DEFAULT '0' COMMENT '访问量',
  `access_number25` bigint(20) DEFAULT '0' COMMENT '访问量',
  `access_number26` bigint(20) DEFAULT '0' COMMENT '访问量',
  `access_number27` bigint(20) DEFAULT '0' COMMENT '访问量',
  `access_number28` bigint(20) DEFAULT '0' COMMENT '访问量',
  `access_number29` bigint(20) DEFAULT '0' COMMENT '访问量',
  `access_number30` bigint(20) DEFAULT '0' COMMENT '访问量',
  `access_number31` bigint(20) DEFAULT '0' COMMENT '访问量',
  `access_number32` bigint(20) DEFAULT '0' COMMENT '访问量',
  `response_sum_time1` bigint(20) DEFAULT '0' COMMENT '响应时间大小',
  `response_sum_time2` bigint(20) DEFAULT '0' COMMENT '响应时间大小',
  `response_sum_time3` bigint(20) DEFAULT '0' COMMENT '响应时间大小',
  `response_sum_time4` bigint(20) DEFAULT '0' COMMENT '响应时间大小',
  `response_sum_time5` bigint(20) DEFAULT '0' COMMENT '响应时间大小',
  `response_sum_time6` bigint(20) DEFAULT '0' COMMENT '响应时间大小',
  `response_sum_time7` bigint(20) DEFAULT '0' COMMENT '响应时间大小',
  `response_sum_time8` bigint(20) DEFAULT '0' COMMENT '响应时间大小',
  `response_sum_time9` bigint(20) DEFAULT '0' COMMENT '响应时间大小',
  `response_sum_time10` bigint(20) DEFAULT '0' COMMENT '响应时间大小',
  `response_sum_time11` bigint(20) DEFAULT '0' COMMENT '响应时间大小',
  `response_sum_time12` bigint(20) DEFAULT '0' COMMENT '响应时间大小',
  `response_sum_time13` bigint(20) DEFAULT '0' COMMENT '响应时间大小',
  `response_sum_time14` bigint(20) DEFAULT '0' COMMENT '响应时间大小',
  `response_sum_time15` bigint(20) DEFAULT '0' COMMENT '响应时间大小',
  `response_sum_time16` bigint(20) DEFAULT '0' COMMENT '响应时间大小',
  `response_sum_time17` bigint(20) DEFAULT '0' COMMENT '响应时间大小',
  `response_sum_time18` bigint(20) DEFAULT '0' COMMENT '响应时间大小',
  `response_sum_time19` bigint(20) DEFAULT '0' COMMENT '响应时间大小',
  `response_sum_time20` bigint(20) DEFAULT '0' COMMENT '响应时间大小',
  `response_sum_time21` bigint(20) DEFAULT '0' COMMENT '响应时间大小',
  `response_sum_time22` bigint(20) DEFAULT '0' COMMENT '响应时间大小',
  `response_sum_time23` bigint(20) DEFAULT '0' COMMENT '响应时间大小',
  `response_sum_time24` bigint(20) DEFAULT '0' COMMENT '响应时间大小',
  `response_sum_time25` bigint(20) DEFAULT '0' COMMENT '响应时间大小',
  `response_sum_time26` bigint(20) DEFAULT '0' COMMENT '响应时间大小',
  `response_sum_time27` bigint(20) DEFAULT '0' COMMENT '响应时间大小',
  `response_sum_time28` bigint(20) DEFAULT '0' COMMENT '响应时间大小',
  `response_sum_time29` bigint(20) DEFAULT '0' COMMENT '响应时间大小',
  `response_sum_time30` bigint(20) DEFAULT '0' COMMENT '响应时间大小',
  `response_sum_time31` bigint(20) DEFAULT '0' COMMENT '响应时间大小',
  `response_sum_time32` bigint(20) DEFAULT '0' COMMENT '响应时间大小',
  `status` smallint(6) NOT NULL COMMENT '数据状态',
  `updatetime` datetime NOT NULL COMMENT '数据更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `IX_condition` (`period`,`minute_order`,`city`,`operator`,`network`,`platform`,`source`),
  KEY `updatetime` (`updatetime`)
) ENGINE=InnoDB AUTO_INCREMENT=227 DEFAULT CHARSET=utf8 COMMENT='web测速数据';