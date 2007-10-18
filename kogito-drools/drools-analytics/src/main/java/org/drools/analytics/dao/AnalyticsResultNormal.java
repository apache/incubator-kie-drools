package org.drools.analytics.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.drools.analytics.components.Field;
import org.drools.analytics.report.components.AnalyticsMessageBase;
import org.drools.analytics.report.components.Gap;
import org.drools.analytics.report.components.MissingNumberPattern;
import org.drools.analytics.report.components.RangeCheckCause;

/**
 * 
 * @author Toni Rikkola
 */
class AnalyticsResultNormal implements AnalyticsResult {
	private static final long serialVersionUID = -6207688526236713721L;

	private Map<Integer, Gap> gapsById = new TreeMap<Integer, Gap>();
	private DataTree<Integer, Gap> gapsByFieldId = new DataTree<Integer, Gap>();
	private Map<Integer, MissingNumberPattern> missingNumberPatternsById = new TreeMap<Integer, MissingNumberPattern>();
	private DataTree<Integer, MissingNumberPattern> missingNumberPatternsByFieldId = new DataTree<Integer, MissingNumberPattern>();

	private List<AnalyticsMessageBase> messages = new ArrayList<AnalyticsMessageBase>();
	private DataTree<AnalyticsMessageBase.Severity, AnalyticsMessageBase> messagesBySeverity = new DataTree<AnalyticsMessageBase.Severity, AnalyticsMessageBase>();

	@Override
	public void save(AnalyticsMessageBase message) {
		messages.add(message);
		messagesBySeverity.put(message.getSeverity(), message);
	}

	@Override
	public Collection<AnalyticsMessageBase> getBySeverity(
			AnalyticsMessageBase.Severity severity) {
		Collection<AnalyticsMessageBase> result = messagesBySeverity
				.getBranch(severity);

		if (result == null) {
			return Collections.emptyList();
		} else {
			return result;
		}
	}

	public void save(Gap gap) {
		gapsById.put(gap.getId(), gap);

		// Save by field id.
		gapsByFieldId.put(gap.getField().getId(), gap);
	}

	public void remove(Gap gap) {
		gapsById.remove(gap.getId());

		gapsByFieldId.remove(gap.getField().getId(), gap);
	}

	public Collection<Gap> getGapsByFieldId(int fieldId) {
		return gapsByFieldId.getBranch(fieldId);
	}

	public Collection<RangeCheckCause> getRangeCheckCauses() {
		Collection<RangeCheckCause> result = new ArrayList<RangeCheckCause>();

		result.addAll(gapsById.values());
		result.addAll(missingNumberPatternsById.values());

		return result;
	}

	public void save(MissingNumberPattern missingNumberPattern) {
		missingNumberPatternsById.put(missingNumberPattern.getId(),
				missingNumberPattern);

		// Save by field id.
		missingNumberPatternsByFieldId.put(missingNumberPattern.getField()
				.getId(), missingNumberPattern);
	}

	public Collection<RangeCheckCause> getRangeCheckCausesByFieldId(int id) {
		Collection<RangeCheckCause> result = new ArrayList<RangeCheckCause>();

		result.addAll(gapsByFieldId.getBranch(id));

		result.addAll(missingNumberPatternsByFieldId.getBranch(id));

		return result;
	}
}
