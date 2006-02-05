package org.drools.leaps;
/**
 * Exception to facilitate <code>seek</code> process in working memory
 * 
 * @author Alexander Bagerman
 * 
 */

class NoMatchesFoundException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

    public NoMatchesFoundException() {
        super();
    }

    public NoMatchesFoundException(String msg) {
        super(msg);
    }

}
