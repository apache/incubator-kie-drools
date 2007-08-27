package org.drools.analytics.result;

/**
 * 
 * @author Toni Rikkola
 */
public interface AnalysisResult {

	public void addError(Cause[] causes, String message, int lineNumber);

	public void addNote(Cause[] causes, String message, int lineNumber);

	public void addWarning(Cause[] causes, String message, int lineNumber);
}
