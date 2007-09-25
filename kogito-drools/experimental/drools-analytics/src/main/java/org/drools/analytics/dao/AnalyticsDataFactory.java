package org.drools.analytics.dao;

public class AnalyticsDataFactory {

	private static AnalyticsDataMaps map;

	public static AnalyticsData getAnalyticsData() {
		if (map == null) {
			map = new AnalyticsDataMaps();
		}
		return map;
	}
}
