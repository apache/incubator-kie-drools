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
package org.drools.verifier.core.checks.base;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.drools.verifier.core.cache.inspectors.RuleInspector;
import org.drools.verifier.core.maps.MultiSet;

/**
 * Stores the Checks. When a rule is added or removed, makes sure that all the necessary relations are added or removed.
 */
public class CheckStorage {

    private final PairCheckStorage pairCheckStorage = new PairCheckStorage();
    private final MultiSet<RuleInspector, Check> ruleInspectorChecks = new MultiSet<>();
    private final MultiSet<RuleInspector, OneToManyCheck> oneToManyChecks = new MultiSet<>();
    private final CheckFactory checkFactory;

    public CheckStorage(final CheckFactory checkFactory) {
        this.checkFactory = checkFactory;
    }

    private void makeSingleRowChecks(final RuleInspector ruleInspector) {
        final Set<Check> checks = checkFactory.makeSingleChecks(ruleInspector);

        for (final Check check : checks) {
            if (check instanceof OneToManyCheck) {
                oneToManyChecks.put(ruleInspector,
                                    (OneToManyCheck) check);
            }
        }

        ruleInspectorChecks.addAllValues(ruleInspector,
                                         checks);
    }

    private void makePairRowChecks(final RuleInspector ruleInspector,
                                   final Collection<RuleInspector> all) {

        for (final RuleInspector other : all) {
            if (!ruleInspector.equals(other)) {
                checkFactory.makePairRowCheck(ruleInspector, other).ifPresent(pairCheckStorage::add);
            }
        }
    }

    public Set<Check> getChecks(final RuleInspector ruleInspector) {
        final HashSet<Check> result = new HashSet<>();

        result.addAll(getRuleInspectorChecks(ruleInspector));
        result.addAll(getReferencingChecks(ruleInspector));
        result.addAll(oneToManyChecks.allValues());

        return result;
    }

    private Collection<PairCheckBundle> getReferencingChecks(final RuleInspector ruleInspector) {
        final Collection<PairCheckBundle> checks = pairCheckStorage.get(ruleInspector);
        return checks == null ? Collections.emptyList() : checks;
    }

    private Collection<Check> getRuleInspectorChecks(final RuleInspector ruleInspector) {
        final Collection<Check> checks = ruleInspectorChecks.get(ruleInspector);
        return checks == null ? Collections.emptyList() : checks;
    }

    public Set<Check> remove(final RuleInspector ruleInspector) {
        final HashSet<Check> result = new HashSet<>();

        result.addAll(removeRuleInspectorChecks(ruleInspector));
        result.addAll(pairCheckStorage.remove(ruleInspector));
        result.addAll(removeOneToMany(ruleInspector));

        return result;
    }

    private Collection<Check> removeRuleInspectorChecks(final RuleInspector ruleInspector) {
        final Collection<Check> remove = ruleInspectorChecks.remove(ruleInspector);
        return remove == null ? Collections.emptyList() : remove;
    }

    private Collection<OneToManyCheck> removeOneToMany(final RuleInspector ruleInspector) {
        final Collection<OneToManyCheck> remove = oneToManyChecks.remove(ruleInspector);
        return remove == null ? Collections.emptyList() : remove;
    }

    public void makeChecks(final RuleInspector ruleInspector) {

        makeSingleRowChecks(ruleInspector);

        final Set<RuleInspector> knownRuleInspectors = ruleInspectorChecks.keys();

        makePairRowChecks(ruleInspector,
                          knownRuleInspectors);

        for (final RuleInspector other : knownRuleInspectors) {
            if (!other.equals(ruleInspector)) {
                // Add pair inspector for old values.
                checkFactory.makePairRowCheck(other, ruleInspector).ifPresent(pairCheckStorage::add);
            }
        }
    }
}
