package org.drools.analytics.dao;

public class AnalyticsResultFactory {

	public static AnalyticsResult createAnalyticsResult() {

		return new AnalyticsResultNormal();
	}
}
