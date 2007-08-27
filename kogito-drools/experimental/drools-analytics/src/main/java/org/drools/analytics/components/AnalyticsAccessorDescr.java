package org.drools.analytics.components;

/**
 * 
 * @author Toni Rikkola
 */
public class AnalyticsAccessorDescr extends AnalyticsComponent {

	private static int index = 0;

	public AnalyticsAccessorDescr() {
		super(index++);
	}

	@Override
	public AnalyticsComponentType getComponentType() {
		return AnalyticsComponentType.ACCESSOR;
	}
}
