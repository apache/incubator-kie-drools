package org.drools.verifier.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.drools.verifier.report.components.VerifierMessageBase;
import org.drools.verifier.report.components.Gap;
import org.drools.verifier.report.components.MissingNumberPattern;
import org.drools.verifier.report.components.RangeCheckCause;
import org.drools.verifier.report.components.Severity;

/**
 *
 * @author Toni Rikkola
 */
class VerifierResultNormal implements VerifierResult {
	private static final long serialVersionUID = -6207688526236713721L;

	private Map<Integer, Gap> gapsById = new TreeMap<Integer, Gap>();
	private DataTree<Integer, Gap> gapsByFieldId = new DataTree<Integer, Gap>();
	private Map<Integer, MissingNumberPattern> missingNumberPatternsById = new TreeMap<Integer, MissingNumberPattern>();
	private DataTree<Integer, MissingNumberPattern> missingNumberPatternsByFieldId = new DataTree<Integer, MissingNumberPattern>();

	private List<VerifierMessageBase> messages = new ArrayList<VerifierMessageBase>();
	private DataTree<Severity, VerifierMessageBase> messagesBySeverity = new DataTree<Severity, VerifierMessageBase>();

	private VerifierData data = new VerifierDataMaps();

	public void add(VerifierMessageBase message) {
		messages.add(message);
		messagesBySeverity.put(message.getSeverity(), message);
	}

	public Collection<VerifierMessageBase> getBySeverity(
			Severity severity) {
		Collection<VerifierMessageBase> result = messagesBySeverity
				.getBranch(severity);

		if (result == null) {
			return Collections.emptyList();
		} else {
			return result;
		}
	}

	public void add(Gap gap) {
		gapsById.put(gap.getId(), gap);

		// Put by field id.
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

	public void add(MissingNumberPattern missingNumberPattern) {
		missingNumberPatternsById.put(missingNumberPattern.getId(),
				missingNumberPattern);

		// Put by field id.
		missingNumberPatternsByFieldId.put(missingNumberPattern.getField()
				.getId(), missingNumberPattern);
	}

	public Collection<RangeCheckCause> getRangeCheckCausesByFieldId(int id) {
		Collection<RangeCheckCause> result = new ArrayList<RangeCheckCause>();

		result.addAll(gapsByFieldId.getBranch(id));

		result.addAll(missingNumberPatternsByFieldId.getBranch(id));

		return result;
	}

	public VerifierData getVerifierData() {
		return data;
	}
}
