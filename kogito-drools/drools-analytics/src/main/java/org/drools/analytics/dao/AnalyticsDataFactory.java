package org.drools.analytics.dao;

public class AnalyticsDataFactory {

	private static AnalyticsData data;
	private static AnalyticsResult result;

	public static AnalyticsData getAnalyticsData() {
		if (data == null) {
			data = new AnalyticsDataMaps();
		}

		return data;
	}

	public static AnalyticsResult getAnalyticsResult() {
		if (result == null) {
			result = new AnalyticsResultNormal();
		}

		return result;
	}
}
