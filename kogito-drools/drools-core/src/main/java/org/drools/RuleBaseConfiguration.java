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
public class RuleBaseConfiguration implements Serializable {
    private Map properties;
    
    private boolean immutable;
    
    /**
     * Property to enable/disable left beta memory indexing
     * Defaults to false 
     */
    public static final String PROPERTY_INDEX_LEFT_BETA_MEMORY  = "org.drools.reteoo.beta.index-left";
    
    /**
     * Property to enable/disable right beta memory indexing
     * Defaults to true 
     */
    public static final String PROPERTY_INDEX_RIGHT_BETA_MEMORY = "org.drools.reteoo.beta.index-right";
    
    /**
     * Property to enable/disable alpha node hashing inside object type nodes
     * Defaults to true 
     */
    public static final String PROPERTY_HASH_OBJECT_TYPE_NODES  = "org.drools.reteoo.alpha.hash-type-node";
    
    /**
     * Property to enable/disable alpha node hashing inside alpha nodes
     * Defaults to false 
     */
    public static final String PROPERTY_HASH_ALPHA_NODES        = "org.drools.reteoo.alpha.hash-alpha-node";
    
    /**
     * Property to define working memory assert behavior. Valid values are "identity" or "equals".
     * Defaults to identity 
     */
    public static final String PROPERTY_ASSERT_BEHAVIOR         = "org.drools.wm.assert-behavior";
    
    /**
     * Property to define working memory logical assert behavior. Valid values are "identity" or "equals".
     * 
     * IMPORTANT NODE: if you set LOGICAL_ASSERT_BEHAVIOR to IDENTITY, then ASSERT_BEHAVIOR will be 
     * automatically set to IDENTITY, as it is not possible to have ASSERT_BEHAVIOR EQUALS and 
     * LOGICAL_ASSERT_BEHAVIOR IDENTITY. In a symetrical way, if you set ASSERT_BEHAVIOR to EQUALS,
     * LOGICAL_ASSERT_BEHAVIOR will automatically be set to EQUALS.
     *  
     * Defaults to equals 
     */
    public static final String PROPERTY_LOGICAL_ASSERT_BEHAVIOR = "org.drools.wm.logical-assert-behavior";
    
    public static final String WM_BEHAVIOR_IDENTITY = "identity";
    public static final String WM_BEHAVIOR_EQUALS   = "equals";

    // a generated serial version id
    private static final long  serialVersionUID        = 2989084670778336973L;

    public RuleBaseConfiguration() {
        this.properties = new HashMap();
        this.immutable = false;
        
        // default values
        this.properties.put( PROPERTY_INDEX_LEFT_BETA_MEMORY, 
                          System.getProperty( PROPERTY_INDEX_LEFT_BETA_MEMORY, "false" ) );
        this.properties.put( PROPERTY_INDEX_RIGHT_BETA_MEMORY,
                          System.getProperty( PROPERTY_INDEX_RIGHT_BETA_MEMORY, "true" ) );
        this.properties.put( PROPERTY_HASH_OBJECT_TYPE_NODES,
                          System.getProperty( PROPERTY_HASH_OBJECT_TYPE_NODES, "true" ) );
        this.properties.put( PROPERTY_HASH_ALPHA_NODES,
                          System.getProperty( PROPERTY_HASH_ALPHA_NODES, "false" ) );
        this.properties.put( PROPERTY_ASSERT_BEHAVIOR,
                          System.getProperty( PROPERTY_ASSERT_BEHAVIOR, WM_BEHAVIOR_IDENTITY) );
        if (WM_BEHAVIOR_IDENTITY.equals( this.properties.get( PROPERTY_ASSERT_BEHAVIOR ) ) ) {
            // if assert behavior is IDENTITY, logical assert can be either EQUALS or IDENTITY
            this.properties.put( PROPERTY_LOGICAL_ASSERT_BEHAVIOR,
                              System.getProperty( PROPERTY_LOGICAL_ASSERT_BEHAVIOR, WM_BEHAVIOR_EQUALS ) );
        } else {
            // if assert behavior is EQUALS, logical assert must also be EQUALS
            this.properties.put( PROPERTY_LOGICAL_ASSERT_BEHAVIOR,
                              WM_BEHAVIOR_EQUALS);
        }
    }

    /**
     * Returns the current value for the given property or null if it is not set 
     * @param prop
     * @return
     */
    public String getProperty( String prop ) {
        return (String) this.properties.get( prop );
    }
    
    /**
     * Convenience method that calls get() method and returns a boolean for the 
     * given property.
     *  
     * @param prop
     * @return
     */
    public boolean getBooleanProperty( String prop ) {
        return Boolean.valueOf( (String) this.properties.get( prop ) ).booleanValue();
    }
    
    /**
     * Sets the value of the given property
     * 
     * IMPORTANT NODE: if you set LOGICAL_ASSERT_BEHAVIOR to IDENTITY, then ASSERT_BEHAVIOR will be 
     * automatically set to IDENTITY, as it is not possible to have ASSERT_BEHAVIOR EQUALS and 
     * LOGICAL_ASSERT_BEHAVIOR IDENTITY. In a symetrical way, if you set ASSERT_BEHAVIOR to EQUALS,
     * LOGICAL_ASSERT_BEHAVIOR will automatically be set to EQUALS.
     * 
     * @param prop
     * @param value
     */
    public void setProperty(String prop, String value) {
        if( ! this.immutable ) {
            // if setting logical assert behavior to identity then
            // assert behavior is also set to identity 
            if( PROPERTY_LOGICAL_ASSERT_BEHAVIOR.equals( prop ) &&
                    WM_BEHAVIOR_IDENTITY.equals( value ) ) {
                this.properties.put( PROPERTY_ASSERT_BEHAVIOR,
                                     WM_BEHAVIOR_IDENTITY);
            } else if( PROPERTY_ASSERT_BEHAVIOR.equals( prop ) &&
                    WM_BEHAVIOR_EQUALS.equals( value ) ) {
                this.properties.put( PROPERTY_LOGICAL_ASSERT_BEHAVIOR,
                                     WM_BEHAVIOR_EQUALS);
            }
            this.properties.put( prop, value );
        } else {
            throw new UnsupportedOperationException("Can't set a property after configuration becomes immutable");
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
