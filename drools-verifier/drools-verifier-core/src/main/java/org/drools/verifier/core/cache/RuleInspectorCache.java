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

package org.drools.verifier.core.cache;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

import org.drools.verifier.api.reporting.Issue;
import org.drools.verifier.core.cache.inspectors.RuleInspector;
import org.drools.verifier.core.cache.inspectors.SingleRunInspectedRule;
import org.drools.verifier.core.checks.base.Check;
import org.drools.verifier.core.checks.base.CheckFactory;
import org.drools.verifier.core.checks.base.CheckStorage;
import org.drools.verifier.core.checks.gaps.SingleRangeCheck;
import org.drools.verifier.core.configuration.AnalyzerConfiguration;
import org.drools.verifier.core.index.model.Rule;
import org.drools.verifier.core.util.PortablePreconditions;

import static java.util.stream.Collectors.toList;

public class RuleInspectorCache {

    protected final Map<Rule, RuleInspector> ruleInspectors = new HashMap<>();
    protected final Set<Check> generalChecks = new HashSet<>();
    private Collection<Rule> rules;
    protected final CheckStorage checkStorage;
    protected final AnalyzerConfiguration configuration;

    public RuleInspectorCache(final Collection<Rule> rules,
                              final AnalyzerConfiguration configuration) {
        this.rules = rules;
        this.checkStorage = new CheckStorage(new CheckFactory(PortablePreconditions.checkNotNull("configuration",
                                                                                                 configuration)));
        this.configuration = configuration;
    }

    public void reset() {

        for (final RuleInspector ruleInspector : ruleInspectors.values()) {
            ruleInspector.clearChecks();
        }

        ruleInspectors.clear();
        generalChecks.clear();

        for (final Rule rule : getRules()) {
            addInspector(rule);
        }

        final SingleRangeCheck rangeCheck = new SingleRangeCheck(configuration,
                                                                 ruleInspectors.values());
        if (rangeCheck.isActive(configuration.getCheckConfiguration())) {
            generalChecks.add(rangeCheck);
        }
    }

    protected RuleInspector addInspector(final Rule rule) {
        final RuleInspector ruleInspector = makeRuleInspector(rule);
        ruleInspectors.put(rule,
                           ruleInspector);

        return ruleInspector;
    }

    protected RuleInspector makeRuleInspector(final Rule rule) {
        return new RuleInspector(new SingleRunInspectedRule(rule),
                                 checkStorage,
                                 this,
                                 configuration);
    }

    protected Collection<Rule> getRules() {
        return rules;
    }

    public Set<Check> getGeneralChecks() {
        return generalChecks;
    }

    public Collection<RuleInspector> all() {
        return ruleInspectors.values();
    }

    public Set<Issue> getAllIssues() {
        final Set<Issue> issues = new HashSet<>();
        all().stream()
                .flatMap(inspector -> inspector.getChecks().stream())
                .filter(Check::hasIssues)
                .map(Check::getIssues)
                .forEach(issues::addAll);

        for (final Check generalCheck : generalChecks) {
            if (generalCheck.hasIssues()) {
                issues.addAll(generalCheck.getIssues());
            }
        }

        return issues;
    }

    public Collection<RuleInspector> all(Predicate<RuleInspector> filter) {
        return all().stream().filter(filter).collect(toList());
    }

    public AnalyzerConfiguration getConfiguration() {
        return configuration;
    }
}