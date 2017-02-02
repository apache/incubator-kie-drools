/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.drools.workbench.models.guided.dtable.shared.hitpolicy;

/**
 * The row number used in this class is the actual row number declared in the dtable data.
 * Not the row index.
 * <p>
 * Priority is declared with Row X has priority over Row Y.
 * This Over class contains the row number of Y.
 */
class Over
        implements Comparable<Over> {

    private Integer over;

    public Over(final Integer value) {
        over = value;
    }

    public Over(final RowNumber rowNumber) {
        this(rowNumber.getRowNumber());
    }

    public Over() {
        over = null;
    }

    public Integer getOver() {
        return over;
    }

    @Override
    public int compareTo(final Over other) {
        if (other == null) {
            throw  new NullPointerException("We should never have an Over class that is null." );
        }
        if (over == null && other.getOver() == null) {
            return 0;
        }
        if (over == null) {
            return 1;
        } else {
            return over.compareTo(other.getOver());
        }
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final Over over1 = (Over) o;

        return over != null ? over.equals(over1.over) : over1.over == null;
    }

    @Override
    public int hashCode() {
        return over != null ? ~~over.hashCode() : 0;
    }
}
