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

package org.drools.conf;


/**
 * An Enum for ShareBetaNodes option.
 * 
 * drools.shareBetaNodes = &lt;true|false&gt; 
 * 
 * DEFAULT = true
 * 
 * @author etirelli
 */
public enum ShareBetaNodesOption implements SingleValueKnowledgeBaseOption {
    
    YES(true),
    NO(false);

    /**
     * The property name for the share beta nodes option
     */
    public static final String PROPERTY_NAME = "drools.shareBetaNodes";
    
    private boolean value;
    
    ShareBetaNodesOption( final boolean value ) {
        this.value = value;
    }
    
    /**
     * {@inheritDoc}
     */
    public String getPropertyName() {
        return PROPERTY_NAME;
    }
    
    public boolean isShareBetaNodes() {
        return this.value;
    }

}
