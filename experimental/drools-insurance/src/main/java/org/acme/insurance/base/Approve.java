package org.acme.insurance.base;

/**
 * This is a simple fact class to mark something as approved.
 * @author Michael Neale
 *
 */
public class Approve {

	private String reason;

	public Approve(String reason) {
		this.reason = reason;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

}
