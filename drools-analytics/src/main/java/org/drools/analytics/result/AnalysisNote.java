package org.drools.analytics.result;

import java.io.Serializable;
import java.util.List;

import org.drools.analytics.components.AnalyticsComponent;

/**
 * 
 * @author Toni Rikkola
 */
public class AnalysisNote extends AnalysisMessage implements Serializable {
	private static final long serialVersionUID = 5853338910928403832L;

	private static int noteIndex = 0;

	public AnalysisNote(AnalyticsComponent faulty, String message,
			List<Cause> reasons) {
		super(faulty, message, reasons);
		id = noteIndex++;
	}
}
