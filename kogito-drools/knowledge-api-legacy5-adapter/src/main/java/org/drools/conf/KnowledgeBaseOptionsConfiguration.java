/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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
 * A base interface for type safe configurations
 */
public interface KnowledgeBaseOptionsConfiguration {
    
    /**
     * Sets an option
     * 
     * @param option the option to be set. As options are type safe, the option
     *               itself contains the option key, and so a single parameter
     *               is enough.
     */
    public <T extends KnowledgeBaseOption> void setOption( T option );

    /**
     * Gets an option value
     * 
     * @param option the option class for the option being requested
     * 
     * @return the Option value for the given option. Returns null if option is 
     *         not configured.
     */
    public <T extends SingleValueKnowledgeBaseOption> T getOption( Class<T> option );
    
    
    /**
     * Gets an option value for the given option + key. This method should
     * be used for multi-value options, like accumulate functions configuration
     * where one option has multiple values, distinguished by a sub-key.
     * 
     * @param option the option class for the option being requested
     * @param key the key for the option being requested
     * 
     * @return the Option value for the given option + key. Returns null if option is 
     *         not configured.
     */
    public <T extends MultiValueKnowledgeBaseOption> T getOption( Class<T> option, String key );
    

}
