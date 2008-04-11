package org.drools.verifier.dao;

public class AnalyticsResultFactory {

	public static AnalyticsResult createAnalyticsResult() {

		return new AnalyticsResultNormal();
	}
}
