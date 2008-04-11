package org.drools.verifier.components;

/**
 * 
 * @author Toni Rikkola
 */
public class AnalyticsFromDescr extends AnalyticsComponent {

	private static int index = 0;

	private AnalyticsComponentType dataSourceType;
	private int dataSourceId;

	public AnalyticsFromDescr() {
		super(index++);
	}

	@Override
	public AnalyticsComponentType getComponentType() {
		return AnalyticsComponentType.FROM;
	}

	public int getDataSourceId() {
		return dataSourceId;
	}

	public void setDataSourceId(int dataSourceId) {
		this.dataSourceId = dataSourceId;
	}

	public AnalyticsComponentType getDataSourceType() {
		return dataSourceType;
	}

	public void setDataSourceType(AnalyticsComponentType dataSourceType) {
		this.dataSourceType = dataSourceType;
	}
}
