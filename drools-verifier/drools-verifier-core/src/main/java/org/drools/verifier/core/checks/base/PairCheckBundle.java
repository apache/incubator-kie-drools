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

import java.util.List;

import org.drools.verifier.core.cache.inspectors.RuleInspector;
import org.drools.verifier.core.util.PortablePreconditions;

public class PairCheckBundle
        extends PriorityListCheck {

    protected final RuleInspector ruleInspector;
    protected final RuleInspector other;

    public PairCheckBundle(final RuleInspector ruleInspector,
                           final RuleInspector other,
                           final List<Check> filteredSet) {
        super(filteredSet);

        this.ruleInspector = PortablePreconditions.checkNotNull("ruleInspector",
                                                                ruleInspector);
        this.other = PortablePreconditions.checkNotNull("other",
                                                        other);
    }

    public RuleInspector getRuleInspector() {
        return ruleInspector;
    }

    public RuleInspector getOther() {
        return other;
    }
}
