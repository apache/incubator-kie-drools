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
package org.drools.ruleunits.api;

import org.drools.ruleunits.api.conf.RuleConfig;

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

    /**
     * Creates a {@link RuleUnitInstance} using the given {@link RuleUnitData}.
     */
    RuleUnitInstance<T> createInstance(T data);

    /**
     * Creates a {@link RuleUnitInstance} using the given {@link RuleUnitData} and {@link RuleConfig}.
     */
    RuleUnitInstance<T> createInstance(T data, RuleConfig ruleConfig);
}
