package org.drools.jsr94.rules;

/*
 * Copyright 2005 JBoss Inc
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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.rules.InvalidRuleSessionException;
import javax.rules.ObjectFilter;
import javax.rules.RuleExecutionSetNotFoundException;
import javax.rules.RuleRuntime;
import javax.rules.StatelessRuleSession;

import org.drools.FactException;
import org.drools.WorkingMemory;
import org.drools.jsr94.rules.admin.RuleExecutionSetImpl;
import org.drools.jsr94.rules.admin.RuleExecutionSetRepository;

/**
 * The Drools implementation of the <code>StatelessRuleSession</code>
 * interface which is a representation of a stateless rules engine session. A
 * stateless rules engine session exposes a stateless rule execution API to an
 * underlying rules engine.
 * 
 * @see StatelessRuleSession
 * 
 * @author <a href="mailto:thomas.diesler@softcon-itec.de">thomas diesler </a>
 */
public class StatelessRuleSessionImpl extends AbstractRuleSessionImpl
    implements
    StatelessRuleSession {
    /**
     * Gets the <code>RuleExecutionSet</code> for this URI and associates it
     * with a RuleBase.
     * 
     * @param bindUri
     *            the URI the <code>RuleExecutionSet</code> has been bound to
     * @param properties
     *            additional properties used to create the
     *            <code>RuleSession</code> implementation.
     * 
     * @throws RuleExecutionSetNotFoundException
     *             if there is no rule set under the given URI
     */
    StatelessRuleSessionImpl(final String bindUri,
                             final Map properties,
                             final RuleExecutionSetRepository repository) throws RuleExecutionSetNotFoundException {
        super( repository );
        setProperties( properties );

        final RuleExecutionSetImpl ruleSet = (RuleExecutionSetImpl) repository.getRuleExecutionSet( bindUri );

        if ( ruleSet == null ) {
            throw new RuleExecutionSetNotFoundException( "RuleExecutionSet unbound: " + bindUri );
        }

        setRuleExecutionSet( ruleSet );
    }

    /**
     * Executes the rules in the bound rule execution set using the supplied
     * list of objects. A <code>List</code> is returned containing the objects
     * created by (or passed into the rule session) the executed rules that pass
     * the filter test of the default <code>RuleExecutionSet</code>
     * <code>ObjectFilter</code>
     * (if present). <p/> The returned list may not neccessarily include all
     * objects passed, and may include <code>Object</code>s created by
     * side-effects. The execution of a <code>RuleExecutionSet</code> can add,
     * remove and update objects. Therefore the returned object list is
     * dependent on the rules that are part of the executed
     * <code>RuleExecutionSet</code> as well as Drools specific rule engine
     * behavior.
     * 
     * @param objects
     *            the objects used to execute rules.
     * 
     * @return a <code>List</code> containing the objects as a result of
     *         executing the rules.
     * 
     * @throws InvalidRuleSessionException
     *             on illegal rule session state.
     */
    public List executeRules(final List objects) throws InvalidRuleSessionException {
        return executeRules( objects,
                             this.getRuleExecutionSet().getObjectFilter() );
    }

    /**
     * Executes the rules in the bound rule execution set using the supplied
     * list of objects. A <code>List</code> is returned containing the objects
     * created by (or passed into the rule engine) the executed rules and
     * filtered with the supplied object filter. <p/> The returned list may not
     * neccessarily include all objects passed, and may include
     * <code>Object</code>s created by side-effects. The execution of a
     * <code>RuleExecutionSet</code> can add, remove and update objects.
     * Therefore the returned object list is dependent on the rules that are
     * part of the executed <code>RuleExecutionSet</code> as well as Drools
     * specific rule engine behavior.
     * 
     * @param objects
     *            the objects used to execute rules.
     * @param filter
     *            the object filter.
     * 
     * @return a <code>List</code> containing the objects as a result of
     *         executing rules, after passing through the supplied object
     *         filter.
     * 
     * @throws InvalidRuleSessionException
     *             on illegal rule session state.
     */
    public List executeRules(final List objects,
                             final ObjectFilter filter) throws InvalidRuleSessionException {
        // get a new working memory with no references kept (i.e. it doesn't need to be disposed)
        initWorkingMemory( false );

        WorkingMemory workingMemory = getWorkingMemory();
        for ( final Iterator objectIter = objects.iterator(); objectIter.hasNext(); ) {
            workingMemory.assertObject( objectIter.next() );
        }
        workingMemory.fireAllRules();            

        final List results = getObjects(filter);

        return results;
    }
    
    public int getType() throws InvalidRuleSessionException {
        return RuleRuntime.STATELESS_SESSION_TYPE;
    }
}
