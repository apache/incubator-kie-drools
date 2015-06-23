/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.jbpm.kie.services.impl.bpmn2.builder;


/**
 * This is a generic extension of the {@link ThreadLocal} class that makes sure that an intial value 
 * is available for each call of {@link ThreadLocal#get()}. 
 * 
 * @see {@link ThreadLocal}
 * @param <T> The initial value class
 */
public class GenericDefaultThreadLocal<T> extends ThreadLocal<T> {

    private final T defaultInstance;
    
    public GenericDefaultThreadLocal(T defaultInstance) { 
       this.defaultInstance = defaultInstance; 
    }

    @Override
    protected T initialValue() {
        return defaultInstance;
    }
    
}
