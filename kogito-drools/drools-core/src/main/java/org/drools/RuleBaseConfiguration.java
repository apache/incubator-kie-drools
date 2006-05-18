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

import java.util.Properties;

/**
 * RuleBaseConfiguration
 * A class to store RuleBase related configuration
 *
 * Created: 16/05/2006
 * @author <a href="mailto:tirelli@post.com">Edson Tirelli</a> 
 *
 * @version $Id$
 */
public class RuleBaseConfiguration extends Properties {
    /**
     * Property to enable/disable left beta memory indexing
     * Defaults to false 
     */
    public static final String INDEX_LEFT_BETA_MEMORY  = "org.drools.reteoo.beta.index-left";
    /**
     * Property to enable/disable right beta memory indexing
     * Defaults to true 
     */
    public static final String INDEX_RIGHT_BETA_MEMORY = "org.drools.reteoo.beta.index-right";
    /**
     * Property to enable/disable alpha node hashing inside object type nodes
     * Defaults to true 
     */
    public static final String HASH_OBJECT_TYPE_NODES  = "org.drools.reteoo.alpha.hash-type-node";
    /**
     * Property to enable/disable alpha node hashing inside alpha nodes
     * Defaults to false 
     */
    public static final String HASH_ALPHA_NODES        = "org.drools.reteoo.alpha.hash-alpha-node";

    // a generated serial version id
    private static final long  serialVersionUID        = 2989084670778336973L;

    public RuleBaseConfiguration() {
        // default values
        this.setProperty( INDEX_LEFT_BETA_MEMORY, 
                          System.getProperty( INDEX_LEFT_BETA_MEMORY, "false" ) );
        this.setProperty( INDEX_RIGHT_BETA_MEMORY,
                          System.getProperty( INDEX_RIGHT_BETA_MEMORY, "true" ) );
        this.setProperty( HASH_OBJECT_TYPE_NODES,
                          System.getProperty( HASH_OBJECT_TYPE_NODES, "true" ) );
        this.setProperty( HASH_ALPHA_NODES,
                          System.getProperty( HASH_ALPHA_NODES, "false" ) );
    }

    public boolean getBooleanProperty(String prop) {
        return Boolean.valueOf( prop ).booleanValue();
    }
    
}
