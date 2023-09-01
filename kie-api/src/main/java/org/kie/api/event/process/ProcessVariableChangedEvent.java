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
package org.kie.api.event.process;

import java.util.List;

/**
 * An event when a variable inside a process instance has been changed.
 */
public interface ProcessVariableChangedEvent
    extends
    ProcessEvent {

    /**
     * The unique id of the process variable (definition).
     *
     * @return the variable id
     */
    String getVariableId();

    /**
     * The unique id of the process variable instance (as multiple node instances with the
     * same process variable definition exists).  This is an aggregation of the unique id of
     * the instance that contains the variable scope and the variable id.
     *
     * @return the variable instance id
     */
    String getVariableInstanceId();

    /**
     * The old value of the variable.
     * This may be null.
     *
     * @return the old value
     */
    Object getOldValue();

    /**
     * The new value of the variable.
     * This may be null.
     *
     * @return the new value
     */
    Object getNewValue();
    
    /**
     * List of tags associated with variable that is being changed.
     * @return list of tags if there are any otherwise empty list
     */
    List<String> getTags();

    /**
     * Determines if variable that is being changed has given tag associated with it
     * @param tag name of the tag
     * @return returns true if given tag is associated with variable otherwise false
     */
    boolean hasTag(String tag);

}
