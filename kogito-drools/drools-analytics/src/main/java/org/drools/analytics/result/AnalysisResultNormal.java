package org.drools.analytics.result;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Toni Rikkola
 */
public class AnalysisResultNormal implements AnalysisResult {
	private static final long serialVersionUID = -6207688526236713721L;

	private List<AnalysisNote> notes = new ArrayList<AnalysisNote>();
	private List<AnalysisWarning> warnings = new ArrayList<AnalysisWarning>();
	private List<AnalysisError> errors = new ArrayList<AnalysisError>();

	public void add(AnalysisMessage notification) {
		if (notification instanceof AnalysisError) {
			errors.add((AnalysisError) notification);
		} else if (notification instanceof AnalysisWarning) {
			warnings.add((AnalysisWarning) notification);
		} else if (notification instanceof AnalysisNote) {
			notes.add((AnalysisNote) notification);
		}

	}

	public List<AnalysisError> getErrors() {
		return errors;
	}

	public List<AnalysisNote> getNotes() {
		return notes;
	}

	public List<AnalysisWarning> getWarnings() {
		return warnings;
	}
}
