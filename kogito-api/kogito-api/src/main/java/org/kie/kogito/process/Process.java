/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.process;

import java.util.Collection;
import java.util.function.Predicate;

import org.kie.kogito.Model;
import org.kie.kogito.internal.process.runtime.KogitoNode;

public interface Process<T> {

    ProcessInstance<T> createInstance(T workingMemory);

    ProcessInstance<T> createInstance(String businessKey, T workingMemory);

    ProcessInstances<T> instances();

    Collection<KogitoNode> findNodes(Predicate<KogitoNode> filter);

    <S> void send(Signal<S> sig);

    T createModel();

    ProcessInstance<? extends Model> createInstance(Model m);

    ProcessInstance<? extends Model> createInstance(String businessKey, Model m);

    String id();

    String name();

    void activate();

    void deactivate();
}
