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
package org.drools.ruleunits.api;

/**
 * A rule unit is an atomic module defining a set of rules and a set of strongly typed {@link DataSource}s through which
 * the facts processed by the rule engine are inserted. Users never need to implement this interface since the concrete
 * implementation, reflecting what has been defined in the corresponding {@link RuleUnitData} is automatically generated
 * by the engine. It is possible to obtain an instance of the generated rule unit programmatically via the {@link RuleUnitProvider}
 * or declaratively via dependency injection.
 *
 * @param <T> The {@link RuleUnitData} for which this rule unit is generated.
 */
public interface RuleUnit<T extends RuleUnitData> {

    // TODO this method is a leaky abstraction and should be removed from the public API
    String id();

    default RuleUnitInstance<T> createInstance(T data) {
        return createInstance(data, null);
    }

    RuleUnitInstance<T> createInstance(T data, String name);
}
