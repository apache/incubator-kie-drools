package org.drools.verifier.report.components;

/**
 * Pattern, rule or similar that is always satisfied.
 * 
 * @author trikkola
 * 
 */
public class AlwaysTrue implements Cause {

	private static int index = 0;

	private final String guid = String.valueOf( index++ );

	private final Cause cause;

	/**
	 * 
	 * @param cause
	 *            Component that is always satisfied.
	 */
	public AlwaysTrue(Cause cause) {
		this.cause = cause;
	}

	public CauseType getCauseType() {
		return CauseType.ALWAYS_TRUE;
	}

	public String getGuid() {
		return guid;
	}

	public Cause getCause() {
		return cause;
	}
}
