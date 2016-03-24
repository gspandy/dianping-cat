package com.dianping.cat.influxdb;

import org.junit.Test;
import org.unidal.lookup.ComponentTestCase;

import com.dianping.cat.influxdb.dto.Query;
import com.dianping.cat.influxdb.dto.QueryResult;

public class InfluxdbDemo extends ComponentTestCase {

	@Test
	public void test() {
		InfluxDB influxDB = InfluxDBFactory.connect("http://172.24.53.30:8086", "root", "root");
		String dbName = "aTimeSeries";
		influxDB.createDatabase(dbName);
		//
		// BatchPoints batchPoints = BatchPoints.database(dbName).retentionPolicy("default")
		// .consistency(ConsistencyLevel.ALL).build();
		//
		// Point point1 = Point.measurement("cpu").time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
		// .tag("name", "jlsun").field("idle", 90L).field("system", 9L).field("system", 1L).build();
		// Point point2 = Point.measurement("disk").time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
		// .tag("sex", "man").field("used", 80L).field("free", 1L).build();
		// batchPoints.point(point1);
		// batchPoints.point(point2);
		// influxDB.write(batchPoints);
		QueryResult result = influxDB.query(new Query("SELECT * FROM cpu", dbName));
		QueryResult result2 = influxDB.query(new Query("SELECT * FROM disk", dbName));

		System.out.println(result);
		System.out.println(result2);
		// influxDB.deleteDatabase(dbName);
	}
}
