package org.drools.analytics.result;

import java.io.Serializable;
import java.util.List;

import org.drools.analytics.components.AnalyticsComponent;

/**
 * 
 * @author Toni Rikkola
 */
public class AnalysisError extends AnalysisMessage implements Serializable {
	private static final long serialVersionUID = -7589664008092901491L;

	private static int errorIndex = 0;

	public AnalysisError(AnalyticsComponent faulty, String message,
			List<Cause> reasons) {
		super(faulty, message, reasons);
		id = errorIndex++;
	}
}
