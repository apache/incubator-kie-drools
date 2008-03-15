package org.drools.brms.client.modeldriven.testing;

import java.io.Serializable;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectInput;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This contains lists of rules to include in the scenario (or exclude, as the case may be !).
 * This will be used to filter the rule engines behaviour under test.
 * @author Michael Neale
 */
public class ExecutionTrace implements Fixture {



	/**
	 * This is the simulated date - leaving it as null means it will use
	 * the current time.
	 */
	public Date		scenarioSimulatedDate = null;


	/**
	 * The time taken for execution.
	 */
	public Long executionTimeResult;

	/**
	 * This is pretty obvious really. The total firing count of all rules.
	 */
	public Long numberOfRulesFired;

	public ExecutionTrace() {}

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        scenarioSimulatedDate   = (Date)in.readObject();
        executionTimeResult     = in.readLong();
        numberOfRulesFired      = in.readLong();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(scenarioSimulatedDate);
        out.writeLong(executionTimeResult);
        out.writeLong(numberOfRulesFired);
    }

}
