package org.drools.compiler.integrationtests.facts;

import java.io.Serializable;

/**
 * A simple (poor) example of order to show rule execution problems.
 * Don't pay attention to the bad model design, it's only done for testing purpose
 */
public class Poc implements Serializable {

	private static final long serialVersionUID = 1L;

	private long id = 1;
	private boolean test1 = false;
	private boolean test2 = false;
	private int foundTest = 0;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public boolean isTest1() {
		return test1;
	}
	public void setTest1(boolean test1) {
		this.test1 = test1;
	}
	public boolean isTest2() {
		return test2;
	}
	public void setTest2(boolean test2) {
		this.test2 = test2;
	}
	public int getFoundTest() {
		return foundTest;
	}
	public void setFoundTest(int foundTest) {
		this.foundTest = foundTest;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Poc other = (Poc) obj;
		if (id != other.id)
			return false;
		return true;
	}
	



}
