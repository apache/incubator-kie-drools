/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.internal.builder.fluent;

/**
 * See {@link DMNRuntimeFluent}
 */
public interface DMNFluent<T extends DMNFluent, U> {

    T setInput(String name, Object value);

    T setActiveModel(String namespace, String modelName);

    T setActiveModel(String resourcePath);

    T getModel(String namespace, String modelName);

    T getModel(String resourcePath);

    T evaluateModel();

    T getAllContext();

    T getDecisionResults();

    T getMessages();

    U end();
}
