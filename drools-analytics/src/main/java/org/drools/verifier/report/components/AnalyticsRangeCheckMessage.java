package org.drools.verifier.report.components;

import java.util.Collection;

public class AnalyticsRangeCheckMessage extends AnalyticsMessageBase {
	private static final long serialVersionUID = -2403507929285633672L;

	private Collection<RangeCheckCause> causes;

	public AnalyticsRangeCheckMessage(Severity severity, Cause faulty,
			String message, Collection<RangeCheckCause> causes) {
		super(severity, MessageType.RANGE_CHECK, faulty, message);

		this.causes = causes;
	}

	public Collection<RangeCheckCause> getCauses() {
		return causes;
	}

	public void setCauses(Collection<RangeCheckCause> reasons) {
		this.causes = reasons;
	}
}
