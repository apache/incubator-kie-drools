package org.drools.analytics.components;

/**
 * 
 * @author Toni Rikkola
 */
public class AnalyticsEvalDescr extends AnalyticsComponent {

	private static int index = 0;

	private String content;
	private String classMethodName;

	public AnalyticsEvalDescr() {
		super(index++);
	}

	@Override
	public AnalyticsComponentType getComponentType() {
		return AnalyticsComponentType.EVAL;
	}

	public String getClassMethodName() {
		return classMethodName;
	}

	public void setClassMethodName(String classMethodName) {
		this.classMethodName = classMethodName;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
}
