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

import java.util.function.Predicate;

import org.drools.verifier.api.reporting.CheckType;
import org.drools.verifier.core.cache.inspectors.RuleInspector;
import org.drools.verifier.core.configuration.AnalyzerConfiguration;
import org.drools.verifier.core.maps.InspectorList;

public abstract class OneToManyCheck
        extends SingleCheck {

    private final InspectorList<RuleInspector> ruleInspectors;
    private Predicate<RuleInspector> filter;

    public OneToManyCheck(final RuleInspector ruleInspector,
                          final Predicate<RuleInspector> filter,
                          final AnalyzerConfiguration configuration,
                          final CheckType checkType) {
        this(ruleInspector,
             configuration,
             checkType);
        this.filter = filter;
    }

    public OneToManyCheck(final RuleInspector ruleInspector,
                          final AnalyzerConfiguration configuration,
                          final CheckType checkType) {
        super(ruleInspector,
              configuration,
              checkType);
        ruleInspectors = new InspectorList<>(configuration);
    }

    protected boolean thereIsAtLeastOneRow() {
        return getOtherRows().size() >= 1;
    }

    public RuleInspector getRuleInspector() {
        return ruleInspector;
    }

    public InspectorList<RuleInspector> getOtherRows() {
        ruleInspectors.clear();
        ruleInspectors.addAll(ruleInspector.getCache().all(filter));
        return ruleInspectors;
    }
}
