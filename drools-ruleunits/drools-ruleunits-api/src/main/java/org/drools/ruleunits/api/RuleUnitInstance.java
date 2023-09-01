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

import org.kie.api.runtime.rule.AgendaFilter;
import org.kie.api.runtime.rule.QueryResults;
import org.kie.api.time.SessionClock;

/**
 * An instance of a {@link RuleUnit} working on the data contained in a specific {@link RuleUnitData}.
 *
 * @param <T> The {@link RuleUnitData} for which this rule unit is generated.
 */
public interface RuleUnitInstance<T extends RuleUnitData> extends AutoCloseable {

    /**
     * The {@link RuleUnit} from which this RuleUnitInstance has been created.
     */
    RuleUnit<T> unit();

    /**
     * The instance of {@link RuleUnitData} containing the data used by this RuleUnitInstance.
     */
    T ruleUnitData();

    /**
     * Trigger the pattern matching algorithm on all the facts contained in the {@link DataSource}s of the {@link RuleUnitData}
     * used by this RuleUnitInstance and fires all the rules activated by them.
     * @return The number of fired rules.
     */
    int fire();

    /**
     * Trigger the pattern matching algorithm on all the facts contained in the {@link DataSource}s of the {@link RuleUnitData}
     * used by this RuleUnitInstance and fires all the rules activated by them.
     *
     * @param agendaFilter
     *      filters the Matches that may fire
     * @return
     *      The number of fired rules.
     */
    int fire(AgendaFilter agendaFilter);

    /**
     * Executes the query with the given name on this instance, using the given set of arguments
     * @param query The name of the query to be executed
     * @param arguments The arguments to be passed to the query
     * @return query results
     */
    QueryResults executeQuery(String query, Object... arguments);

    /**
     * @return the session clock instance used by this RuleUnitInstance
     */
    <T extends SessionClock> T getClock();

    /**
     * Releases all resources used by this RuleUnitInstance, setting it up for garbage collection.
     * This method <b>must</b> always be called after finishing using the RuleUnitInstance, or the engine
     * will not free the memory it uses.
     */
    @Override
    void close();
}
