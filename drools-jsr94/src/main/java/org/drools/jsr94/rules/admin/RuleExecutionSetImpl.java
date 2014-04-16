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


import org.drools.core.RuleBaseConfiguration;
import org.drools.core.SessionConfiguration;
import org.drools.core.definitions.InternalKnowledgePackage;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.jsr94.rules.Constants;
import org.drools.jsr94.rules.Jsr94FactHandleFactory;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.StatelessKieSession;
import org.kie.internal.KnowledgeBaseFactory;

import javax.rules.ObjectFilter;
import javax.rules.admin.RuleExecutionSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The Drools implementation of the <code>RuleExecutionSet</code> interface
 * which defines a named set of executable <code>Rule</code> instances. A
 * <code>RuleExecutionSet</code> can be executed by a rules engine via the
 * <code>RuleSession</code> interface.
 *
 * @see RuleExecutionSet
 */
public class RuleExecutionSetImpl
    implements
    RuleExecutionSet {
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
    private InternalKnowledgeBase kBase;

    /**
     * The <code>Package</code> associated with this
     * <code>RuleExecutionSet</code>.
     */
    private InternalKnowledgePackage pkg;

    /**
     * The default ObjectFilter class name
     * associated with this rule execution set.
     */
    private ObjectFilter      objectFilter;

    /**
     * Instances of this class should be obtained from the
     * <code>LocalRuleExecutionSetProviderImpl</code>. Each
     * <code>RuleExecutionSetImpl</code> corresponds with an
     * <code>org.kie.Package</code> object.
     *
     * @param pkg The <code>Package</code> to associate with this
     *        <code>RuleExecutionSet</code>.
     * @param properties A <code>Map</code> of user-defined and
     *        Drools-defined properties. May be <code>null</code>.
     */
    RuleExecutionSetImpl(final InternalKnowledgePackage pkg,
                         final Map properties) {
        if ( null == properties ) {
            this.properties = new HashMap();
        } else {
            this.properties = properties;
        }
        this.pkg = pkg;
        this.description = pkg.getName();//..getDocumentation( );
        
        RuleBaseConfiguration config = ( RuleBaseConfiguration ) this.properties.get( Constants.RES_RULEBASE_CONFIG );
        if ( config == null ) {
            config =  new RuleBaseConfiguration();
        }
        config.getComponentFactory().setHandleFactoryProvider(new Jsr94FactHandleFactory());
        kBase = (InternalKnowledgeBase) KnowledgeBaseFactory.newKnowledgeBase(config);
        kBase.addPackage( pkg );

        this.kBase = kBase;
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
    public KieSession newStatefulSession(SessionConfiguration conf) {
        return this.kBase.newKieSession(conf, null);
    }
    
    /**
     * Returns a new WorkingMemory object.
     *
     * @return A new WorkingMemory object.
     */
    public StatelessKieSession newStatelessSession() {
        return this.kBase.newStatelessKieSession();
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

        for ( org.kie.api.definition.rule.Rule rule : pkg.getRules() ) {
            jsr94Rules.add( new RuleImpl( (org.drools.core.definitions.rule.impl.RuleImpl)rule ) );
        }

        return jsr94Rules;
    }
}
