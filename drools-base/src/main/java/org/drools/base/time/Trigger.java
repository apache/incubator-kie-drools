/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.base.time;

import java.io.Serializable;
import java.util.Date;

import org.drools.base.base.ValueResolver;

/**
 * A trigger interface for scheduling jobs
 */
public interface Trigger extends Serializable {

    /**
     * this method will be called before any job being called to provide some context 
     * to trigger
     * @param valueResolver
     */
    default void initialize(ValueResolver valueResolver) {}
    /**
     * This method is used to query the trigger about the existence of a
     * possible next fire time, but WITHOUT changing any internal state 
     * of the trigger.  In other words, this method MUST not have side
     * effects. As an analogy, if a trigger was a stack, this method would
     * be the equivalent of a "peek()" call.
     * 
     * @return the Date of the next fire time or null if there is no  
     *         next fire time.
     */
    Date hasNextFireTime();
    
    /**
     * This method returns the date of the next fire time and updates
     * the internal state of the Trigger to the following fire time
     * if one exists. As an analogy, if a trigger was a stack, this method
     * would be the equivalent of a "pop()" call.
     * 
     * @return the Date of the next fire time or null if there is no
     *         next fire time.
     */
    Date nextFireTime();
}
