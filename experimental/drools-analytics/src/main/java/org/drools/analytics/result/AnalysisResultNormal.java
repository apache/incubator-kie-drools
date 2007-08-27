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

	public void addError( String ruleName,
			String message, int lineNumber) {
		AnalysisError error = new AnalysisError();
		error.setRuleName(ruleName);
		error.setMessage(message);
		error.setLineNumber(lineNumber);

		errors.add(error);
	}

	public void addNote( String ruleName,
			String message, int lineNumber) {
		AnalysisNote note = new AnalysisNote();
		note.setRuleName(ruleName);
		note.setMessage(message);
		note.setLineNumber(lineNumber);

		notes.add(note);
	}

	public void addWarning( String ruleName,
			String message, int lineNumber) {
		AnalysisWarning warning = new AnalysisWarning();
		warning.setRuleName(ruleName);
		warning.setMessage(message);
		warning.setLineNumber(lineNumber);

		warnings.add(warning);
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

	public void addError(Cause[] causes, String message, int lineNumber) {
		// TODO Auto-generated method stub
		
	}

	public void addNote(Cause[] causes, String message, int lineNumber) {
		// TODO Auto-generated method stub
		
	}

	public void addWarning(Cause[] causes, String message, int lineNumber) {
		// TODO Auto-generated method stub
		
	}
}
