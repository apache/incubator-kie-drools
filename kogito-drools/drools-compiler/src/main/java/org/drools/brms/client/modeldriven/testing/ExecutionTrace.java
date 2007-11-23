package org.drools.brms.client.modeldriven.testing;

import java.io.Serializable;
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


}
