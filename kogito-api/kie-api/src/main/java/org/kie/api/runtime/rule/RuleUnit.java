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

import java.util.Arrays;

/**
 * Rule units represent a purely declarative approach to partition a rules set into smaller units,
 * binding different data sources to those units and orchestrate the execution of the individual unit.
 * A rule unit is an aggregate of data sources, global variables and rules.
 */
public interface RuleUnit {

    /**
     * Defines the identity of this RuleUnit. By default it corresponds to the unit's class.
     * @return The identity of this RuleUnit
     */
    default Identity getUnitIdentity() {
        return new Identity( getClass() );
    }

    /**
     * Called when the rule engine starts evaluating the unit
     */
    default void onStart() { }

    /**
     * Called when the evaluation of this unit terminates
     */
    default void onEnd() { }

    /**
     * Called when the execution of unit is suspended (only for runUntilHalt)
     */
    default void onSuspend() { }

    /**
     * Called when the execution of unit is resumed (only for runUntilHalt)
     */
    default void onResume() { }

    /**
     * Called when the consequence of rule in this rule unit triggers the execution of a different unit
     * @param other The called unit
     */
    default void onYield(RuleUnit other) { }

    class Identity {
        private final Class<? extends RuleUnit> cls;
        private final Object[] keys;

        public Identity(Class<? extends RuleUnit> cls, Object... keys) {
            this.cls = cls;
            this.keys = keys;
        }

        @Override
        public boolean equals( Object o ) {
            if ( this == o ) {
                return true;
            }
            if ( o == null || getClass() != o.getClass() ) {
                return false;
            }

            Identity identity = (Identity) o;
            return cls.equals( identity.cls ) && Arrays.equals( keys, identity.keys );
        }

        @Override
        public int hashCode() {
            int result = cls.hashCode();
            result = 31 * result + Arrays.hashCode( keys );
            return result;
        }
    }
}
