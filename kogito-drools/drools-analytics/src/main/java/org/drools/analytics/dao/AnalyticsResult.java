package org.drools.analytics.dao;

import java.util.Collection;

import org.drools.analytics.report.components.AnalyticsMessageBase;
import org.drools.analytics.report.components.Gap;
import org.drools.analytics.report.components.MissingNumberPattern;
import org.drools.analytics.report.components.RangeCheckCause;
import org.drools.analytics.report.components.AnalyticsMessageBase.Severity;

/**
 * 
 * @author Toni Rikkola
 */
public interface AnalyticsResult {

	public void save(Gap gap);

	public void remove(Gap gap);

	public void save(MissingNumberPattern missingNumberPattern);

	public Collection<RangeCheckCause> getRangeCheckCauses();

	public Collection<RangeCheckCause> getRangeCheckCausesByFieldId(int id);

	public Collection<Gap> getGapsByFieldId(int fieldId);

	public void save(AnalyticsMessageBase note);

	/**
	 * Return all the items that have given severity value.
	 * 
	 * @param severity
	 *            Severity level of item.
	 * @return Collection of items or an empty list if none was found.
	 */
	public Collection<AnalyticsMessageBase> getBySeverity(Severity severity);

}
