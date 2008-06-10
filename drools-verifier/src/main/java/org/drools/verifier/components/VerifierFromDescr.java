package org.drools.verifier.components;

/**
 * 
 * @author Toni Rikkola
 */
public class VerifierFromDescr extends VerifierComponent {

	private static int index = 0;

	private VerifierComponentType dataSourceType;
	private int dataSourceId;

	public VerifierFromDescr() {
		super(index++);
	}

	@Override
	public VerifierComponentType getComponentType() {
		return VerifierComponentType.FROM;
	}

	public int getDataSourceId() {
		return dataSourceId;
	}

	public void setDataSourceId(int dataSourceId) {
		this.dataSourceId = dataSourceId;
	}

	public VerifierComponentType getDataSourceType() {
		return dataSourceType;
	}

	public void setDataSourceType(VerifierComponentType dataSourceType) {
		this.dataSourceType = dataSourceType;
	}
}
