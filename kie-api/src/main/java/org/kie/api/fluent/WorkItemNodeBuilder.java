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
package org.kie.api.fluent;

public interface WorkItemNodeBuilder<T extends NodeContainerBuilder<T, ?>> extends NodeBuilder<WorkItemNodeBuilder<T>, T>, HumanNodeOperations<WorkItemNodeBuilder<T>, T> {

    WorkItemNodeBuilder<T> waitForCompletion(boolean waitForCompletion);

    WorkItemNodeBuilder<T> inMapping(String parameterName, String variableName);

    WorkItemNodeBuilder<T> outMapping(String parameterName, String variableName);

    WorkItemNodeBuilder<T> workName(String name);

    WorkItemNodeBuilder<T> workParameter(String name, Object value);

    WorkItemNodeBuilder<T> workParameterDefinition(String name, Class<?> type);
}
