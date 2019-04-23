/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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
package org.drools.verifier.api.reporting.gaps;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.drools.verifier.api.reporting.CheckType;
import org.drools.verifier.api.reporting.Issue;
import org.drools.verifier.api.reporting.Severity;

public class MissingRangeIssue
        extends Issue {

    private Collection<MissingRange> uncoveredRanges;
    private List<PartitionCondition> partition;

    public MissingRangeIssue() {
    }

    public MissingRangeIssue(final Severity severity,
                             final CheckType checkType,
                             final List<PartitionCondition> partition,
                             final Collection<MissingRange> uncoveredRanges,
                             final Set<Integer> rowNumbers) {
        super(severity,
              checkType,
              rowNumbers);
        this.partition = partition;
        this.uncoveredRanges = uncoveredRanges;
    }

    public List<PartitionCondition> getPartition() {
        return partition;
    }

    public Collection<MissingRange> getUncoveredRanges() {
        return uncoveredRanges;
    }
}
