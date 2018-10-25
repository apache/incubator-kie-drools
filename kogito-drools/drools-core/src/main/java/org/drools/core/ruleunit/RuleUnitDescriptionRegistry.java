/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.drools.core.definitions.rule.impl.RuleImpl;
import org.kie.api.runtime.rule.RuleUnit;

import static org.drools.core.ruleunit.RuleUnitUtil.getUnitName;

public class RuleUnitDescriptionRegistry {

    private State state = State.UNKNOWN;

    private final Map<String, RuleUnitDescription> ruleUnits = new ConcurrentHashMap<>();

    public RuleUnitDescriptionRegistry() { }

    public RuleUnitDescription getDescriptionForUnit(final RuleUnit ruleUnit) {
        final RuleUnitDescription ruleUnitDescr = ruleUnits.get(getUnitName(ruleUnit));
        if (ruleUnitDescr == null) {
            throw new IllegalStateException("Unknown RuleUnitDescription: " + getUnitName(ruleUnit));
        }
        return ruleUnitDescr;
    }

    public Optional<RuleUnitDescription> getDescription(final String unitClassName) {
        return Optional.ofNullable(ruleUnits.get(unitClassName));
    }

    public Optional<RuleUnitDescription> getDescription(final RuleImpl rule) {
        return getDescription(rule.getRuleUnitClassName());
    }

    public void add(final RuleUnitDescriptionLoader loader) {
        if (loader != null) {
            ruleUnits.putAll(loader.getDescriptions());
            state = state.merge(loader.getState());
        }
    }

    public boolean hasUnits() {
        return !ruleUnits.isEmpty();
    }
}
