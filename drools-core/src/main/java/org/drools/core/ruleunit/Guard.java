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

package org.drools.core.ruleunit;

import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.spi.Activation;
import org.kie.api.runtime.rule.RuleUnit;

import java.util.HashSet;
import java.util.Set;

public class Guard {
    private final RuleUnit guardedUnit;
    private final RuleImpl guardingRule;

    private final Set<Activation> activations = new HashSet<>();

    public Guard( RuleUnit guardedUnit, RuleImpl guardingRule ) {
        this.guardedUnit = guardedUnit;
        this.guardingRule = guardingRule;
    }

    public Set<Activation> getActivations() {
        return activations;
    }

    public void addActivation(Activation activation) {
        activations.add(activation);
    }

    public void removeActivation(Activation activation) {
        activations.remove(activation);
    }

    public boolean isActive() {
        return !activations.isEmpty();
    }

    public RuleUnit getGuardedUnit() {
        return guardedUnit;
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) return true;
        if ( o == null || getClass() != o.getClass() ) return false;

        Guard guard = (Guard) o;
        return guardedUnit.equals( guard.guardedUnit ) && guardingRule.equals( guard.guardingRule );
    }

    @Override
    public int hashCode() {
        int result = guardedUnit.hashCode();
        result = 31 * result + guardingRule.hashCode();
        return result;
    }
}
