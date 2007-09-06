package org.drools.analytics.result;

import java.io.Serializable;
import java.util.List;

/**
 * 
 * @author Toni Rikkola
 */
abstract class AnalysisMessage implements Serializable {

	protected int id;

	protected String ruleName;
	protected String message;
	protected List<Cause> causes;

	public AnalysisMessage(String ruleName, String message, List<Cause> reasons) {
		this.ruleName = ruleName;
		this.message = message;
		this.causes = reasons;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getRuleName() {
		return ruleName;
	}

	public void setRuleName(String ruleName) {
		this.ruleName = ruleName;
	}

	public List<Cause> getCauses() {
		return causes;
	}

	public void setCauses(List<Cause> reasons) {
		this.causes = reasons;
	}
}
