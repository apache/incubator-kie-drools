/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.api.runtime.rule;

import org.kie.api.KieBase;

/**
 * RuleUnitExecutor allows to execute different {@link RuleUnit}s.
 */
public interface RuleUnitExecutor {

    /**
     * Bind this executor to a {@link KieBase} in order to execute the units defined in it.
     */
    RuleUnitExecutor bind( KieBase kiebase );

    /**
     * Runs a RuleUnit of the given class.
     */
    int run( Class<? extends RuleUnit> ruleUnitClass );

    /**
     * Runs a RuleUnit of the given class.
     */
    int run( RuleUnit ruleUnit );

    /**
     * Runs until halt a RuleUnit of the given class.
     */
    void runUntilHalt( Class<? extends RuleUnit> ruleUnitClass );

    /**
     * Runs until halt a RuleUnit of the given class.
     */
    void runUntilHalt( RuleUnit ruleUnit );

    /**
     * Requests the executor to stop running units.
     */
    void halt();

    /**
     * Creates a DataSource, optionally containing some items, and bind it this executor with the given name.
     *
     * @param name the name with which the newly created DataSource will be bound to this executor.
     * @param items the items contained in the DataSource.
     */
    <T> DataSource<T> newDataSource( String name, T... items );

    /**
     * Binds a variable to this executor with a given name. This named variable will be then injected into
     * {@link RuleUnit}s executed on this executor using a naming convention.
     *
     * @param name the name with which the value will be bound to this executor.
     * @param value the value to bind.
     */
    RuleUnitExecutor bindVariable(String name, Object value);

    /**
     * Releases all the current executor resources, setting up the session for garbage collection.
     * This method <b>must</b> always be called after finishing using the executor, or the engine
     * will not free the memory used by the executor.
     */
    void dispose();

    /**
     * Creates a brand new RuleUnitExecutor
     */
    static RuleUnitExecutor create() {
        try {
            return ( RuleUnitExecutor ) Class.forName( "org.drools.core.impl.RuleUnitExecutorSession" ).newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Unable to instance KieServices", e);
        }
    }
}
