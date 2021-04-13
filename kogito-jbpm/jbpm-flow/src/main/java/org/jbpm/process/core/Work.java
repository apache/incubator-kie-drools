/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.process.core;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.jbpm.process.instance.impl.humantask.DeadlineInfo;
import org.jbpm.process.instance.impl.humantask.Reassignment;

public interface Work {

    void setName(String name);

    String getName();

    void setParameter(String name, Object value);

    void setParameters(Map<String, Object> parameters);

    Object getParameter(String name);

    Map<String, Object> getParameters();

    void addParameterDefinition(ParameterDefinition parameterDefinition);

    void setParameterDefinitions(Set<ParameterDefinition> parameterDefinitions);

    Set<ParameterDefinition> getParameterDefinitions();

    String[] getParameterNames();

    ParameterDefinition getParameterDefinition(String name);

    Set<String> getMetaParameters();

    /**
     * Retrieve information about non started deadlines.
     * <P>
     * Deadline information consist of the expiration date (which can be a exact date
     * or a potentially repeatable duration)and a list of key value pairs with arbitrary
     * information about the notification itself.
     * 
     * @return a collection containing deadline information.
     */
    Collection<DeadlineInfo<Map<String, Object>>> getNotStartedDeadlines();

    /**
     * Retrieve information about non completed deadlines.
     * <P>
     * Deadline information consist of the expiration date (which can be a exact date
     * or a potentially repeatable duration)and a list of key value pairs with arbitrary
     * information about the notification itself.
     * 
     * @return a collection containing deadline information.
     */
    Collection<DeadlineInfo<Map<String, Object>>> getNotCompletedDeadlines();

    Collection<DeadlineInfo<Reassignment>> getNotStartedReassignments();

    Collection<DeadlineInfo<Reassignment>> getNotCompletedReassigments();

}
