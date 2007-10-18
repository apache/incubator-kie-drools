package org.drools.analytics.report.components;

import java.util.Collection;
import java.util.Collections;

/**
 * 
 * @author Toni Rikkola
 */
public class AnalyticsMessage extends AnalyticsMessageBase {
	private static final long serialVersionUID = 9190003495068712452L;

	protected Collection<Cause> causes;

	public AnalyticsMessage(Severity severity, MessageType messageType,
			Cause faulty, String message, Collection<Cause> causes) {
		super(severity, messageType, faulty, message);

		this.causes = causes;
	}

	public AnalyticsMessage(Severity severity, MessageType messageType,
			Cause faulty, String message) {
		super(severity, messageType, faulty, message);

		this.causes = Collections.emptyList();
	}

	public Collection<Cause> getCauses() {
		return causes;
	}

	public void setCauses(Collection<Cause> reasons) {
		this.causes = reasons;
	}

	@Override
	public String toString() {
		StringBuffer str = new StringBuffer(severity.getSingular());

		str.append(" id = ");
		str.append(id);
		str.append(":\n");

		if (faulty != null) {
			str.append("faulty : ");
			str.append(faulty);
			str.append(", ");
		}

		str.append(message);
		str.append(" \n\tCauses are [ \n");

		for (Cause cause : causes) {
			str.append("\t\t");
			str.append(cause);
			str.append("\n");
		}
		str.append("\t]");

		return str.toString();
	}
}
