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

public class ValueForActionIsSetTwiceIssue
        extends Issue {

    private String firstItem;
    private String secondItem;

    public ValueForActionIsSetTwiceIssue() {
    }

    public ValueForActionIsSetTwiceIssue(final Severity severity,
                                         final CheckType checkType,
                                         final String firstItem,
                                         final String secondItem,
                                         final Set<Integer> rowNumbers) {
        super(severity,
              checkType,
              rowNumbers);

        this.firstItem = firstItem;
        this.secondItem = secondItem;
    }

    public void setFirstItem(final String firstItem) {
        this.firstItem = firstItem;
    }

    public void setSecondItem(final String secondItem) {
        this.secondItem = secondItem;
    }

    public String getFirstItem() {
        return firstItem;
    }

    public String getSecondItem() {
        return secondItem;
    }
}
