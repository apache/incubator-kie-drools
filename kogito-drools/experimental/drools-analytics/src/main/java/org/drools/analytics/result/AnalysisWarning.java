package org.drools.analytics.result;

import java.io.Serializable;


/**
 * 
 * @author Toni Rikkola
 */
public class AnalysisWarning extends AnalysisMessage implements Serializable {
	private static final long serialVersionUID = 1791682284155920123L;

	private static int warningIndex = 0;

	public AnalysisWarning() {
		id = warningIndex++;
	}
}
