/**
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

import java.util.List;
import java.util.Map;

import javax.rules.admin.RuleExecutionSet;
import javax.rules.admin.RuleExecutionSetDeregistrationException;
import javax.rules.admin.RuleExecutionSetRegisterException;

/**
 * TODO
 * 
 * @version $Revision$ $Date$
 * @author <a href="mailto:rlangbehn@users.sourceforge.net">Rainer Langbehn</a>
 */
public class TestRuleExecutionSetRepository
	implements RuleExecutionSetRepository
{
	/**
	 * Default serial version UID.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Required default ctor. 
	 */
	public TestRuleExecutionSetRepository() {
	}

	/* (non-Javadoc)
	 * @see org.drools.jsr94.rules.repository.RuleExecutionSetRepository#getRegistrations()
	 */
	public List getRegistrations()
	throws RuleExecutionSetRepositoryException {
		String s = "Implementation outstanding";
		throw new UnsupportedOperationException(s);
	}

	/* (non-Javadoc)
	 * @see org.drools.jsr94.rules.repository.RuleExecutionSetRepository#getRuleExecutionSet(java.lang.String, java.util.Map)
	 */
	public RuleExecutionSet getRuleExecutionSet(
			String bindUri,
			Map properties)
	throws RuleExecutionSetRepositoryException {
		String s = "Implementation outstanding";
		throw new UnsupportedOperationException(s);
	}

	/* (non-Javadoc)
	 * @see org.drools.jsr94.rules.repository.RuleExecutionSetRepository#registerRuleExecutionSet(java.lang.String, javax.rules.admin.RuleExecutionSet, java.util.Map)
	 */
	public void registerRuleExecutionSet(
			String bindUri,
			RuleExecutionSet ruleSet,
			Map properties)
	throws RuleExecutionSetRegisterException {
		String s = "Implementation outstanding";
		throw new UnsupportedOperationException(s);
	}

	/* (non-Javadoc)
	 * @see org.drools.jsr94.rules.repository.RuleExecutionSetRepository#unregisterRuleExecutionSet(java.lang.String, java.util.Map)
	 */
	public void unregisterRuleExecutionSet(
			String bindUri,
			Map properties)
	throws RuleExecutionSetDeregistrationException {
		String s = "Implementation outstanding";
		throw new UnsupportedOperationException(s);
	}
}
