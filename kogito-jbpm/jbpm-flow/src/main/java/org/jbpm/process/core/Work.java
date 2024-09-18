/*
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
package org.jbpm.process.core;

import java.util.Map;
import java.util.Set;

import org.kie.kogito.process.workitems.WorkParametersFactory;

public interface Work {

    static final String PARAMETER_UNIQUE_TASK_ID = "UNIQUE_TASK_ID";

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

    void setWorkParametersFactory(WorkParametersFactory factory);

    WorkParametersFactory getWorkParametersFactory();

}
