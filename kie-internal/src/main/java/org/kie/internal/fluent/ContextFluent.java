/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
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

package org.kie.internal.fluent;


public interface ContextFluent<T>{

    /**
     * The last executed command, if it returns a value, is set to a name in this executing context.
     * @param name
     * @return this
     */
    T set(String name);
    
    /**
     * Indicates that output from the last command should be returned (default is no).
     * <br>
     * A call to this method <i>must</i> follow a call to {@link #set(String)} method in order to 
     * set the name for the result.
     * @return this
     */
    T out();
    
    /**
     * Indicates that the output from the last executed command should be returned and set to the given name in the context
     * @param name
     * @return this
     */
    T out(String name);
} 
