/*
 * Created on 30/11/2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.drools.testing.core.exception;

/**
 * @author mshaw
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class RuleTestLanguageException extends Exception {

	private Throwable cause = null;
	
	public RuleTestLanguageException () {
		super();
	}
	
	public RuleTestLanguageException (String message) {
		super(message);
	}
	
	public RuleTestLanguageException (String message, Throwable cause) {
		super(message);
        this.cause = cause;
	}
	
	public Throwable getCause() {
        return cause;
    }

    public void printStackTrace() {
        super.printStackTrace();
        if (cause != null) {
            System.err.println("Caused by:");
            cause.printStackTrace();
        }
    }

    public void printStackTrace(java.io.PrintStream ps) {
        super.printStackTrace(ps);
        if (cause != null) {
            ps.println("Caused by:");
            cause.printStackTrace(ps);
        }
    }

    public void printStackTrace(java.io.PrintWriter pw) {
        super.printStackTrace(pw);
        if (cause != null) {
            pw.println("Caused by:");
            cause.printStackTrace(pw);
        }
    }

    public String toString()
    {
        String exceptionString = super.toString();
        if (cause != null) {
            exceptionString += " Caused By: \n";
            exceptionString += cause.toString();
        }
        return exceptionString;
    }
}
