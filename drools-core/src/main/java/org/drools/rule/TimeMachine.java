package org.drools.rule;

import java.io.Serializable;
import java.util.Calendar;

/**
 * This class allows the time for "now" to be defined outside of the Rule.
 * Mainly for external testing tools (testing of rules, not drools).
 *
 * @author Michael Neale
 */
public class TimeMachine implements Serializable {

    private static final long serialVersionUID = 400L;

	public Calendar getNow() {
		return Calendar.getInstance();
	}

}
