package org.drools.analytics.components;

import org.drools.analytics.report.components.Cause;

/**
 * 
 * @author Toni Rikkola
 */
public class AnalyticsEvalDescr extends AnalyticsComponent implements Cause {

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

	public CauseType getCauseType() {
		return CauseType.EVAL;
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

	@Override
	public String toString() {
		return "Eval, content: " + content;
	}
}
