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
package org.drools.verifier.core.index.model;

import java.util.Date;

public class ActivationTime {

    private final Date start;
    private final Date end;

    /**
     * Accepts null. Null is infinite
     */
    public ActivationTime(final Date start,
                          final Date end) {
        this.start = start;
        this.end = end;
    }

    public Date getStart() {
        return start;
    }

    public Date getEnd() {
        return end;
    }

    public boolean overlaps(final ActivationTime other) {
        final Date max = findMaxDate(start, other.start);
        final Date min = findMinDate(end, other.end);

        if (min == null || max == null) {
            return true;
        } else {
            return min.compareTo(max) >= 0;
        }
    }

    private Date findMaxDate(final Date date,
                             final Date other) {
        if (date == null && other == null) {
            return null;
        } else if (other == null || (date != null && date.after(other))) {
            return date;
        } else {
            return other;
        }
    }

    private Date findMinDate(final Date date,
                             final Date other) {
        if (date == null && other == null) {
            return null;
        } else if (other == null || (date != null && date.before(other))) {
            return date;
        } else {
            return other;
        }
    }
}
