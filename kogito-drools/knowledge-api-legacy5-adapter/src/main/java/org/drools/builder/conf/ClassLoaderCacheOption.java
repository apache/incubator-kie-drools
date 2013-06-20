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

package org.drools.builder.conf;

import org.drools.conf.SingleValueKnowledgeBaseOption;


/**
 * An Enum for ClassLoaderCacheOption option.
 * 
 * drools.classLoaderCacheEnabled = &lt;true|false&gt;
 * 
 * DEFAULT = true
 * 
 * When resolving classes on the classpath, drools calls the parent classloader to
 * resolve them. Usually classloaders do not cache results, making compilation very
 * expensive on IO. By default, Drools will cache results of class resolution on the
 * external parent classloader in order to improve compilation performance. This
 * cache can be disabled by setting this option to false.
 * 
 * This option is new to Drools 5.1. Before 5.1, Drools would never cache class 
 * resolution results.
 */
public enum ClassLoaderCacheOption implements SingleValueKnowledgeBuilderOption, SingleValueKnowledgeBaseOption {
    
    ENABLED(true),
    DISABLED(false);

    /**
     * The property name for the process string escapes option
     */
    public static final String PROPERTY_NAME = "drools.classLoaderCacheEnabled";
    
    private boolean value;
    
    ClassLoaderCacheOption( final boolean value ) {
        this.value = value;
    }
    
    /**
     * {@inheritDoc}
     */
    public String getPropertyName() {
        return PROPERTY_NAME;
    }
    
    public boolean isClassLoaderCacheEnabled() {
        return this.value;
    }

}
