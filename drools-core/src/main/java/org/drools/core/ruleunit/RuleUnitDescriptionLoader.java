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

package org.drools.core.ruleunit;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.drools.core.definitions.InternalKnowledgePackage;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.kie.internal.ruleunit.RuleUnitComponentFactory;
import org.kie.internal.ruleunit.RuleUnitDescription;

public class RuleUnitDescriptionLoader {

    private RuleUnitDescriptionRegistry.State state = RuleUnitDescriptionRegistry.State.UNKNOWN;

    private transient final InternalKnowledgePackage pkg;
    private final Map<String, RuleUnitDescription> ruleUnitDescriptionsCache = new ConcurrentHashMap<>();
    private final Set<String> nonExistingUnits = new HashSet<>();

    public RuleUnitDescriptionLoader(InternalKnowledgePackage pkg) {
        this.pkg = pkg;
    }

    public RuleUnitDescriptionRegistry.State getState() {
        return state;
    }

    public Map<String, RuleUnitDescription> getDescriptions() {
        return ruleUnitDescriptionsCache;
    }

    public Optional<RuleUnitDescription> getDescription(final RuleImpl rule) {
        return getDescription(rule.getRuleUnitClassName());
    }

    public Optional<RuleUnitDescription> getDescription(final String unitClassName) {
        final Optional<RuleUnitDescription> result = Optional.ofNullable(unitClassName)
                .map(name -> ruleUnitDescriptionsCache.computeIfAbsent(name, this::findDescription));
        state = state.hasUnit(result.isPresent());
        return result;
    }

    private RuleUnitDescription findDescription(final String ruleUnit) {
        if (nonExistingUnits.contains(ruleUnit)) {
            return null;
        }
        try {
            return RuleUnitComponentFactory.get().createRuleUnitDescription( pkg, (Class<?>) pkg.getTypeResolver().resolveType(ruleUnit) );
        } catch (final ClassNotFoundException e) {
            nonExistingUnits.add(ruleUnit);
            return null;
        }
    }
}
