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
package org.drools.verifier.api.reporting;

import java.util.Set;

public class MultipleValuesForOneActionIssue
        extends Issue {

    private String conflictedItem;
    private String conflictingItem;

    public MultipleValuesForOneActionIssue() {
    }

    public MultipleValuesForOneActionIssue(final Severity severity,
                                           final CheckType checkType,
                                           final String conflictedItem,
                                           final String conflictingItem,
                                           final Set<Integer> rowNumbers) {
        super(severity,
              checkType,
              rowNumbers);

        this.conflictedItem = conflictedItem;
        this.conflictingItem = conflictingItem;
    }

    public void setConflictedItem(final String conflictedItem) {
        this.conflictedItem = conflictedItem;
    }

    public void setConflictingItem(final String conflictingItem) {
        this.conflictingItem = conflictingItem;
    }

    public String getConflictedItem() {
        return conflictedItem;
    }

    public String getConflictingItem() {
        return conflictingItem;
    }

    @Override
    public boolean equals(final Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
