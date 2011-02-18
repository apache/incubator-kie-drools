/*
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.jsr94.rules.repository;

import javax.rules.RuleExecutionException;

/**
 * TODO
 * 
 * @version $Revision$ $Date$
 * @author <a href="mailto:rlangbehn@users.sourceforge.net">Rainer Langbehn</a>
 */
public class RuleExecutionSetRepositoryException extends RuleExecutionException
{
	/**
	 * Default serial version UID. 
	 */
	private static final long serialVersionUID = 510l;

	/**
	 * TODO
	 * 
	 * @param message
	 */
	public RuleExecutionSetRepositoryException(String message) {
		super(message);
	}

	/**
	 * TODO
	 * 
	 * @param message
	 * @param exception
	 */
	public RuleExecutionSetRepositoryException(
			String message,
			Exception exception) {
		super(message, exception);
	}
}
