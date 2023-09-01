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
package org.drools.impact.analysis.graph;

public enum LinkFilter {

    POSITIVE(true, false, false),
    POSITIVE_NEGATIVE(true, true, false),
    POSITIVE_UNKNOWN(true, false, true),
    NEGATIVE(false, true, false),
    NEGATIVE_UNKNOWN(false, true, true),
    UNKNOWN(false, false, true),
    ALL(true, true, true);

    private final boolean viewPositive;
    private final boolean viewNegative;
    private final boolean viewUnknown;

    private LinkFilter(boolean viewPositive, boolean viewNegative, boolean viewUnknown) {
        this.viewPositive = viewPositive;
        this.viewNegative = viewNegative;
        this.viewUnknown = viewUnknown;
    }

    public boolean isViewPositive() {
        return viewPositive;
    }

    public boolean isViewNegative() {
        return viewNegative;
    }

    public boolean isViewUnknown() {
        return viewUnknown;
    }

    boolean accept(ReactivityType type) {
        return (viewPositive && type == ReactivityType.POSITIVE) ||
               (viewNegative && type == ReactivityType.NEGATIVE) ||
               (viewUnknown && type == ReactivityType.UNKNOWN);
    }
}
