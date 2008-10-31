package org.drools.rule;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Calendar;

/**
 * This class allows the time for "now" to be defined outside of the Rule.
 * Mainly for external testing tools (testing of rules, not drools).
 *
 * @author Michael Neale
 */
public class TimeMachine implements Externalizable {

    private static final long serialVersionUID = 400L;

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    }

    public void writeExternal(ObjectOutput out) throws IOException {
    }

	public Calendar getNow() {
		return Calendar.getInstance();
	}

}
