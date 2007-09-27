package org.drools.analytics.result;

import java.io.Serializable;
import java.util.Collection;

import org.drools.analytics.components.AnalyticsComponent;

/**
 * 
 * @author Toni Rikkola
 */
abstract class AnalysisMessage implements Serializable {

	protected int id;

	protected AnalyticsComponent faulty;
	protected String message;
	protected Collection<Cause> causes;

	public AnalysisMessage(AnalyticsComponent  faulty, String message, Collection<Cause> causes) {
		this.faulty= faulty;
		this.message = message;
		this.causes = causes;
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

	public AnalyticsComponent getFaulty() {
		return faulty;
	}

	public void setFaulty(AnalyticsComponent faulty) {
		this.faulty = faulty;
	}

	public Collection<Cause> getCauses() {
		return causes;
	}

	public void setCauses(Collection<Cause> reasons) {
		this.causes = reasons;
	}
}
