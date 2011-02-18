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

package org.drools.jsr94.rules.admin;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.rules.ObjectFilter;
import javax.rules.admin.RuleExecutionSet;

import org.drools.IntegrationException;
import org.drools.RuleBase;
import org.drools.RuleBaseConfiguration;
import org.drools.RuleIntegrationException;
import org.drools.SessionConfiguration;
import org.drools.StatefulSession;
import org.drools.StatelessSession;
import org.drools.jsr94.rules.Constants;
import org.drools.jsr94.rules.Jsr94FactHandleFactory;
import org.drools.rule.Package;
import org.drools.rule.Rule;

/**
 * The Drools implementation of the <code>RuleExecutionSet</code> interface
 * which defines a named set of executable <code>Rule</code> instances. A
 * <code>RuleExecutionSet</code> can be executed by a rules engine via the
 * <code>RuleSession</code> interface.
 *
 * @see RuleExecutionSet
 *
 * @author N. Alex Rupp (n_alex <at>codehaus.org)
 * @author <a href="mailto:thomas.diesler@softcon-itec.de">thomas diesler </a>
 * @author <a href="mailto:michael.frandsen@syngenio.de">michael frandsen </a>
 */
public class RuleExecutionSetImpl
    implements
    RuleExecutionSet {
    /**
     * 
     */
    private static final long serialVersionUID = 510l;

    /**
     * A description of this rule execution set or null if no
     * description is specified.
     */
    private String            description;

    /**
     * The default ObjectFilter class name
     * associated with this rule execution set.
     */
    private String            defaultObjectFilterClassName;

    /** A <code>Map</code> of user-defined and Drools-defined properties. */
    private Map               properties;

    /**
     * The <code>RuleBase</code> associated with this
     * <code>RuleExecutionSet</code>.
     */
    private RuleBase          ruleBase;

    /**
     * The <code>Package</code> associated with this
     * <code>RuleExecutionSet</code>.
     */
    private Package           pkg;

    /**
     * The default ObjectFilter class name
     * associated with this rule execution set.
     */
    private ObjectFilter      objectFilter;

    /**
     * Instances of this class should be obtained from the
     * <code>LocalRuleExecutionSetProviderImpl</code>. Each
     * <code>RuleExecutionSetImpl</code> corresponds with an
     * <code>org.drools.Package</code> object.
     *
     * @param package The <code>Package</code> to associate with this
     *        <code>RuleExecutionSet</code>.
     * @param properties A <code>Map</code> of user-defined and
     *        Drools-defined properties. May be <code>null</code>.
     *
     * @throws RuleIntegrationException if an error occurs integrating
     *         a <code>Rule</code> or <code>Package</code>
     *         into the <code>RuleBase</code>
     * @throws RuleSetIntegrationException if an error occurs integrating
     *         a <code>Rule</code> or <code>Package</code>
     *         into the <code>RuleBase</code>
     */
    RuleExecutionSetImpl(final Package pkg,
                         final Map properties) throws IntegrationException {
        if ( null == properties ) {
            this.properties = new HashMap();
        } else {
            this.properties = properties;
        }
        this.pkg = pkg;
        this.description = pkg.getName();//..getDocumentation( );
        
        RuleBaseConfiguration config = ( RuleBaseConfiguration ) this.properties.get( Constants.RES_RULEBASE_CONFIG );
        org.drools.reteoo.ReteooRuleBase ruleBase;
        if ( config != null ) {
            ruleBase = new org.drools.reteoo.ReteooRuleBase( null,
                                                             config,
                                                             new Jsr94FactHandleFactory() );
        } else {
            ruleBase = new org.drools.reteoo.ReteooRuleBase( null,
                                                             new Jsr94FactHandleFactory() );
        }
        ruleBase.addPackage( pkg );

        this.ruleBase = ruleBase;
    }

    /**
     * Get an instance of the default filter, or null.
     *
     * @return An instance of the default filter, or null.
     */
    public synchronized ObjectFilter getObjectFilter() {
        if ( this.objectFilter != null ) {
            return this.objectFilter;
        }

        if ( this.defaultObjectFilterClassName != null ) {
            ClassLoader cl = Thread.currentThread().getContextClassLoader();

            if ( cl == null ) {
                cl = RuleExecutionSetImpl.class.getClassLoader();
            }

            try {
                final Class filterClass = cl.loadClass( this.defaultObjectFilterClassName );
                this.objectFilter = (ObjectFilter) filterClass.newInstance();
            } catch ( final ClassNotFoundException e ) {
                throw new RuntimeException( e.toString() );
            } catch ( final InstantiationException e ) {
                throw new RuntimeException( e.toString() );
            } catch ( final IllegalAccessException e ) {
                throw new RuntimeException( e.toString() );
            }
        }

        return this.objectFilter;
    }

    /**
     * Returns a new WorkingMemory object.
     *
     * @return A new WorkingMemory object.
     */
    public StatefulSession newStatefulSession(SessionConfiguration conf) {
        return this.ruleBase.newStatefulSession(conf, null);
    }
    
    /**
     * Returns a new WorkingMemory object.
     *
     * @return A new WorkingMemory object.
     */
    public StatelessSession newStatelessSession() {
        return this.ruleBase.newStatelessSession();
    }

    // JSR94 interface methods start here -------------------------------------

    /**
     * Get the name of this rule execution set.
     *
     * @return The name of this rule execution set.
     */
    public String getName() {
        return this.pkg.getName();
    }

    /**
     * Get a description of this rule execution set.
     *
     * @return A description of this rule execution set or null of no
     *         description is specified.
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Get a user-defined or Drools-defined property.
     *
     * @param key the key to use to retrieve the property
     *
     * @return the value bound to the key or null
     */
    public Object getProperty(final Object key) {
        return this.properties.get( key );
    }

    /**
     * Set a user-defined or Drools-defined property.
     *
     * @param key the key for the property value
     * @param value the value to associate with the key
     */
    public void setProperty(final Object key,
                            final Object value) {
        this.properties.put( key,
                             value );
    }

    /**
     * Set the default <code>ObjectFilter</code> class. This class is
     * instantiated at runtime and used to filter result objects unless
     * another filter is specified using the available APIs in the runtime
     * view of a rule engine.
     * <p/>
     * Setting the class name to null removes the default
     * <code>ObjectFilter</code>.
     *
     * @param objectFilterClassname the default <code>ObjectFilter</code> class
     */
    public void setDefaultObjectFilter(final String objectFilterClassname) {
        this.defaultObjectFilterClassName = objectFilterClassname;
    }

    /**
     * Returns the default ObjectFilter class name
     * associated with this rule execution set.
     *
     * @return the default ObjectFilter class name
     */
    public String getDefaultObjectFilter() {
        return this.defaultObjectFilterClassName;
    }

    /**
     * Return a list of all <code>Rule</code>s that are part of the
     * <code>RuleExecutionSet</code>.
     *
     * @return a list of all <code>Rule</code>s that are part of the
     *         <code>RuleExecutionSet</code>.
     */
    public List getRules() {
        final List jsr94Rules = new ArrayList();

        final Rule[] rules = (this.pkg.getRules());
        for ( int i = 0; i < rules.length; ++i ) {
            jsr94Rules.add( new RuleImpl( rules[i] ) );
        }

        return jsr94Rules;
    }
}
