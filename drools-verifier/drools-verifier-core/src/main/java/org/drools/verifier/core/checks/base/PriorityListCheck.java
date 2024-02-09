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

import org.drools.verifier.api.reporting.Issue;
import org.drools.verifier.core.configuration.CheckConfiguration;

/**
 * Wraps more than one check into one.
 * Each check will look for failure in the given order.
 * Once failure is found the rest of the checks are ignored.
 * <br>
 * <br>
 * This is used for example by the conflict-subsubsumption-redundancy chain.
 * Where conflict, when found, blocks subsumption.
 */
public class PriorityListCheck
        implements Check {

    private final List<Check> filteredSet;

    private Check checkWithIssues;

    public PriorityListCheck(final List<Check> filteredSet) {
        this.filteredSet = filteredSet;
    }

    @Override
    public Issue getIssue() {
        return checkWithIssues.getIssue();
    }

    @Override
    public boolean hasIssues() {
        return checkWithIssues != null;
    }

    @Override
    public boolean isActive(final CheckConfiguration checkConfiguration) {
        return !filteredSet.isEmpty();
    }

    @Override
    public boolean check() {
        checkWithIssues = filteredSet.stream().filter(Check::check).findFirst().orElse(null);
        return checkWithIssues != null;
    }
}
