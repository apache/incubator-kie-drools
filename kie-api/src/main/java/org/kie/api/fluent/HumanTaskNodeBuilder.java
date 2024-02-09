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

public interface HumanTaskNodeBuilder<T extends NodeContainerBuilder<T, ?>> extends NodeBuilder<HumanTaskNodeBuilder<T>, T>, HumanNodeOperations<HumanTaskNodeBuilder<T>, T> {

    HumanTaskNodeBuilder<T> taskName(String taskName);

    HumanTaskNodeBuilder<T> actorId(String actorId);

    HumanTaskNodeBuilder<T> priority(String priority);

    HumanTaskNodeBuilder<T> comment(String comment);

    HumanTaskNodeBuilder<T> skippable(boolean skippable);

    HumanTaskNodeBuilder<T> content(String content);

    HumanTaskNodeBuilder<T> inMapping(String parameterName, String variableName);

    HumanTaskNodeBuilder<T> outMapping(String parameterName, String variableName);

    HumanTaskNodeBuilder<T> waitForCompletion(boolean waitForCompletion);

    HumanTaskNodeBuilder<T> swimlane(String swimlane);

    HumanTaskNodeBuilder<T> workParameter(String name, Object value);
}
