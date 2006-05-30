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

package org.drools;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * RuleBaseConfiguration
 * 
 * A class to store RuleBase related configuration. It must be used at rule base instantiation time
 * or not used at all.
 * This class will automatically load default values from system properties, so if you want to set
 * a default configuration value for all your new rule bases, you can simply set the property as 
 * a System property.
 * 
 * After RuleBase is created, it makes the configuration immutable and there is no way to make it 
 * mutable again. This is to avoid inconsistent behavior inside rulebase.
 * 
 * NOTE: This API is under review and may change in the future.
 *
 * Created: 16/05/2006
 * @author <a href="mailto:tirelli@post.com">Edson Tirelli</a> 
 *
 * @version $Id$
 */
public class RuleBaseConfiguration
    implements
    Serializable {
    private Map                properties;

    private boolean            immutable;

    /**
     * Property to enable/disable left beta memory indexing
     * Defaults to false 
     */
    public static final String PROPERTY_INDEX_LEFT_BETA_MEMORY    = "org.drools.reteoo.beta.index-left";

    /**
     * Property to enable/disable right beta memory indexing
     * Defaults to true 
     */
    public static final String PROPERTY_INDEX_RIGHT_BETA_MEMORY   = "org.drools.reteoo.beta.index-right";

    /**
     * Property to enable/disable alpha node hashing inside object type nodes
     * Defaults to true 
     */
    public static final String PROPERTY_HASH_OBJECT_TYPE_NODES    = "org.drools.reteoo.alpha.hash-type-node";

    /**
     * Property to enable/disable alpha node hashing inside alpha nodes
     * Defaults to false 
     */
    public static final String PROPERTY_HASH_ALPHA_NODES          = "org.drools.reteoo.alpha.hash-alpha-node";

    /**
     * Property to define working memory assert behavior. Valid values are "identity" or "equality".
     * Defaults to identity 
     */
    public static final String PROPERTY_ASSERT_BEHAVIOR           = "org.drools.wm.assert-behavior";

    public static final String PROPERTY_LOGICAL_OVERRIDE_BEHAVIOR = "org.drools.wm.logical-override-behavior";

    public static final String WM_BEHAVIOR_IDENTITY               = "identity";
    public static final String WM_BEHAVIOR_EQUALITY               = "equality";

    public static final String WM_BEHAVIOR_PRESERVE               = "preserve";
    public static final String WM_BEHAVIOR_DISCARD                = "discard";

    // a generated serial version id
    private static final long  serialVersionUID                   = 2989084670778336973L;

    public RuleBaseConfiguration() {
        this.properties = new HashMap();
        this.immutable = false;

        // default values
        this.properties.put( RuleBaseConfiguration.PROPERTY_INDEX_LEFT_BETA_MEMORY,
                             System.getProperty( RuleBaseConfiguration.PROPERTY_INDEX_LEFT_BETA_MEMORY,
                                                 "false" ) );
        this.properties.put( RuleBaseConfiguration.PROPERTY_INDEX_RIGHT_BETA_MEMORY,
                             System.getProperty( RuleBaseConfiguration.PROPERTY_INDEX_RIGHT_BETA_MEMORY,
                                                 "true" ) );
        this.properties.put( RuleBaseConfiguration.PROPERTY_HASH_OBJECT_TYPE_NODES,
                             System.getProperty( RuleBaseConfiguration.PROPERTY_HASH_OBJECT_TYPE_NODES,
                                                 "true" ) );
        this.properties.put( RuleBaseConfiguration.PROPERTY_HASH_ALPHA_NODES,
                             System.getProperty( RuleBaseConfiguration.PROPERTY_HASH_ALPHA_NODES,
                                                 "false" ) );
        this.properties.put( RuleBaseConfiguration.PROPERTY_ASSERT_BEHAVIOR,
                             System.getProperty( RuleBaseConfiguration.PROPERTY_ASSERT_BEHAVIOR,
                                                 RuleBaseConfiguration.WM_BEHAVIOR_IDENTITY ) );
        this.properties.put( RuleBaseConfiguration.PROPERTY_LOGICAL_OVERRIDE_BEHAVIOR,
                             System.getProperty( RuleBaseConfiguration.PROPERTY_HASH_OBJECT_TYPE_NODES,
                                                 RuleBaseConfiguration.WM_BEHAVIOR_DISCARD ) );
    }

    /**
     * Returns the current value for the given property or null if it is not set 
     * @param prop
     * @return
     */
    public String getProperty(final String prop) {
        return (String) this.properties.get( prop );
    }

    /**
     * Convenience method that calls get() method and returns a boolean for the 
     * given property.
     *  
     * @param prop
     * @return
     */
    public boolean getBooleanProperty(final String prop) {
        return Boolean.valueOf( (String) this.properties.get( prop ) ).booleanValue();
    }

    /**
     * Sets the value of the given property
     * 
     * @param prop
     * @param value
     */
    public void setProperty(final String prop,
                            final String value) {
        if ( !this.immutable ) {
            this.properties.put( prop,
                                 value );
        } else {
            throw new UnsupportedOperationException( "Can't set a property after configuration becomes immutable" );
        }
    }

    /**
     * Makes the configuration object immutable. Once it becomes immutable, 
     * there is no way to make it mutable again. 
     * This is done to keep consistency.
     */
    public void makeImmutable() {
        this.immutable = true;
    }

    /**
     * Returns true if this configuration object is immutable or false otherwise.
     * @return
     */
    public boolean isImmutable() {
        return this.immutable;
    }

}
