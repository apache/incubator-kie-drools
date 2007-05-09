/*
 * Created on 26/05/2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.drools.testing.core.configuration;

import org.drools.testing.core.exception.RuleTestLanguageException;

/**
 * @author mshaw
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class PropertyNotFoundException extends RuleTestLanguageException {

	public PropertyNotFoundException() {
		super();
	}

	public PropertyNotFoundException(String message) {
		super(message);
	}

	public PropertyNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}
}
