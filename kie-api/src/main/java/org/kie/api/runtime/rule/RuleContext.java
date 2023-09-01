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
package org.kie.api.runtime.rule;

import org.kie.api.definition.rule.Rule;
import org.kie.api.runtime.KieContext;

public interface RuleContext extends KieContext {

    /**
     * @return the active Rule for the current context
     */
    Rule getRule();

    /**
     * @return the current Match for the current context
     */
    Match getMatch();

    /**
     * Logically inserts a fact into the KieSession, justified by the current
     * rule context.
     *
     * @param object the fact to insert into the kie session
     */
    FactHandle insertLogical(Object object);

    /**
     * Logically inserts a fact into the given EntryPoint, justified by the current
     * rule context.
     *
     * @param entryPoint the EntryPoint where to logical inserting the given fact
     * @param object the fact to insert into the kie session
     */
    FactHandle insertLogical(EntryPoint entryPoint, Object object);

    /**
     * This is an experimental feature that must be explicitly enabled via DeclarativeAgendaOption, which is off by default. This method may change or disable at any time.
     * @param match
     */
    void blockMatch(Match match);

    /**
     * This is an experimental feature that must be explicitly enabled via DeclarativeAgendaOption, which is off by default. This method may change or disable at any time.
     * @param match
     */
    void unblockAllMatches(Match match);

    /**
     * This is an experimental feature that must be explicitly enabled via DeclarativeAgendaOption, which is off by default. This method may change or disable at any time.
     * @param match
     */
    void cancelMatch(Match match);

}
