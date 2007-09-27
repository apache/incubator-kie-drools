package org.drools.analytics.result;

import java.util.List;

/**
 * 
 * @author Toni Rikkola
 */
public interface AnalysisResult {

	public void add(AnalysisMessage notification);

	public List<AnalysisError> getErrors();

	public List<AnalysisNote> getNotes();

	public List<AnalysisWarning> getWarnings();
}
