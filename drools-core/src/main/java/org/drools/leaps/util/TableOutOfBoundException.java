package org.drools.leaps.util;

public class TableOutOfBoundException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public TableOutOfBoundException() {
		super();
	}

	public TableOutOfBoundException(String msg) {
		super(msg);
	}

	public TableOutOfBoundException(Exception ex) {
		super(ex);
	}
}
