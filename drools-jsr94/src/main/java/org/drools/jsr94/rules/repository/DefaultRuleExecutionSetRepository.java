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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.rules.admin.RuleExecutionSet;
import javax.rules.admin.RuleExecutionSetDeregistrationException;
import javax.rules.admin.RuleExecutionSetRegisterException;

/**
 * Stores the registered <code>RuleExecutionSet</code> objects.
 * 
 */
public class DefaultRuleExecutionSetRepository
    implements
    RuleExecutionSetRepository
{
    private static final long serialVersionUID = 510l;

    /**
     * Holds the registered <code>RuleExecutionSet</code> objects.
     */
    private Map map = new HashMap();

    public DefaultRuleExecutionSetRepository() {
    }

    /* (non-Javadoc)
     * @see org.drools.jsr94.rules.admin.RuleExecutionSetRepository#getRegistrations()
     */
    public List getRegistrations()
    throws RuleExecutionSetRepositoryException {
        List list = new ArrayList();
        list.addAll(map.keySet());
        return list;
    }

    /* (non-Javadoc)
     * @see org.drools.jsr94.rules.repository.RuleExecutionSetRepository#getRuleExecutionSet(java.lang.String, java.util.Map)
     */
    public RuleExecutionSet getRuleExecutionSet(
            String bindUri,
            Map properties)
    throws RuleExecutionSetRepositoryException {
        return (RuleExecutionSet)map.get(bindUri);
    }

    /* (non-Javadoc)
     * @see org.drools.jsr94.rules.admin.RuleExecutionSetRepository#registerRuleExecutionSet(java.lang.String, javax.rules.admin.RuleExecutionSet, java.util.Map)
     */
    public void registerRuleExecutionSet(
            String bindUri,
            RuleExecutionSet ruleSet,
            Map properties)
    throws RuleExecutionSetRegisterException {

        if (bindUri == null) {
            throw new RuleExecutionSetRegisterException("bindUri cannot be null");
        }
        
        if (ruleSet == null) {
            throw new RuleExecutionSetRegisterException("ruleSet cannot be null");
        }
        
        map.put(bindUri, ruleSet);
    }

    /* (non-Javadoc)
     * @see org.drools.jsr94.rules.admin.RuleExecutionSetRepository#unregisterRuleExecutionSet(java.lang.String, java.util.Map)
     */
    public void unregisterRuleExecutionSet(
            String bindUri,
            Map properties)
    throws RuleExecutionSetDeregistrationException {

        if (bindUri == null) {
            throw new RuleExecutionSetDeregistrationException("bindUri cannot be null");
        }
        
        map.remove(bindUri);
    }
}
