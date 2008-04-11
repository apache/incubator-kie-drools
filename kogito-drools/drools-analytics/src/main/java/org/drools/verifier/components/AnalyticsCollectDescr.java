package org.drools.verifier.components;

/**
 * 
 * @author Toni Rikkola
 */
public class AnalyticsCollectDescr extends AnalyticsComponent {

	private static int index = 0;

	private int insidePatternId;
	private String classMethodName;

	@Override
	public AnalyticsComponentType getComponentType() {
		return AnalyticsComponentType.COLLECT;
	}

	public int getInsidePatternId() {
		return insidePatternId;
	}

	public void setInsidePatternId(int insidePatternId) {
		this.insidePatternId = insidePatternId;
	}

	public String getClassMethodName() {
		return classMethodName;
	}

	public void setClassMethodName(String classMethodName) {
		this.classMethodName = classMethodName;
	}

	public AnalyticsCollectDescr() {
		super(index++);
	}
}
