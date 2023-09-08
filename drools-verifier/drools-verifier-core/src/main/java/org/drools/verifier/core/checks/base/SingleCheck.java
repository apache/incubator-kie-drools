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

import org.drools.verifier.api.reporting.CheckType;
import org.drools.verifier.core.cache.inspectors.RuleInspector;
import org.drools.verifier.core.configuration.AnalyzerConfiguration;

public abstract class SingleCheck
        extends CheckBase
        implements Comparable<SingleCheck> {

    protected final RuleInspector ruleInspector;
    private final CheckType checkType;

    public SingleCheck(final RuleInspector ruleInspector,
                       final AnalyzerConfiguration configuration,
                       final CheckType checkType) {
        super(configuration);
        this.ruleInspector = ruleInspector;
        this.checkType = checkType;
    }

    @Override
    protected CheckType getCheckType() {
        return checkType;
    }

    public RuleInspector getRuleInspector() {
        return ruleInspector;
    }

    @Override
    public int compareTo(final SingleCheck singleCheck) {
        return ruleInspector.getRowIndex() - singleCheck.getRuleInspector().getRowIndex();
    }
}
