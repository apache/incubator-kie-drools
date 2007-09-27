package org.drools.analytics.components;

/**
 * 
 * @author Toni Rikkola
 */
public class AnalyticsPredicateDescr extends AnalyticsComponent {

	private static int index = 0;

	private String content;
	private String classMethodName;

	public AnalyticsPredicateDescr() {
		super(index++);
	}

	@Override
	public AnalyticsComponentType getComponentType() {
		return AnalyticsComponentType.PREDICATE;
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
