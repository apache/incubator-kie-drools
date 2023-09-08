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

/**
 * A marker interface that has to be implemented by a POJO defining the set of data belonging to and used by a {@link RuleUnit}.
 * All fields of the implementing POJO that are instance of {@link DataSource} are equivalent to typed {@link org.kie.api.runtime.rule.EntryPoint}s
 * through which inserting (and update and remove when allowed) the facts on which the rule engine will attempt a pattern matching.
 * All other fields are equivalent to {@link org.kie.api.definition.rule.Global} for this rule unit.
 */
public interface RuleUnitData {

}
