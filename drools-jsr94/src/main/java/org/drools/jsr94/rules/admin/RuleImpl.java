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


import java.util.HashMap;
import java.util.Map;

import javax.rules.admin.Rule;

/**
 * The Drools implementation of the <code>Rule</code> interface which provides
 * access to simple metadata for a rule. Related <code>Rule</code>
 * instances are assembled into <code>RuleExecutionSet</code>s, which in
 * turn, can be executed by a rules engine via the <code>RuleSession</code>
 * interface.
 *
 * @see Rule
 *
 */
public class RuleImpl
    implements
    Rule {
    private static final long    serialVersionUID = 510l;

    /** The name of this rule. */
    private String               name;

    /** A description of the rule or null if no description is specified. */
    private String               description;

    /** A <code>Map</code> of user-defined and Drools-defined properties. */
    private final Map            properties       = new HashMap();

    /**
     * The <code>org.drools.rule.Rule</code> that lies at the core of
     * this <code>javax.rules.admin.Rule</code> object.
     */
    private org.drools.rule.Rule rule;

    /**
     * Creates a <code>RuleImpl</code> object by wrapping an
     * <code>org.drools.rule.Rule</code> object.
     *
     * @param rule the <code>org.drools.rule.Rule</code> object to be wrapped.
     */
    RuleImpl(final org.drools.rule.Rule rule) {
        this.rule = rule;
        this.name = rule.getName();
        this.description = rule.getName();// the name of a rule is the only description
    }

    /**
     * Returns the <code>org.drools.rule.Rule</code> that lies at the core of
     * this <code>javax.rules.admin.Rule</code> object. This method is package
     * private.
     *
     * @return <code>org.drools.rule.Rule</code> at the core of this object.
     */
    org.drools.rule.Rule getRule() {
        return this.rule;
    }

    /* Rule interface methods */

    /**
     * Get the name of this rule.
     *
     * @return The name of this rule.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Get a description of the rule.
     *
     * @return A description of the rule or null of no description is specified.
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Get a user-defined or Drools-defined property.
     *
     * @param key the key to use to retrieve the property
     *
     * @return the value bound to the key or <code>null</code>
     */
    public Object getProperty(final Object key) {
        // TODO certain keys should reference internal rule accessor methods
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
        // TODO certain keys should alter internal rule accessor methods
        this.properties.put( key,
                             value );
    }
}
