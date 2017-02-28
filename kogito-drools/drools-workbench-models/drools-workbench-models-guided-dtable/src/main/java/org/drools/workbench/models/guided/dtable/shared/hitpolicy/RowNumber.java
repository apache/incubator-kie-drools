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
 *
 * This is the actual number of the row that is defined in the table.
 * If you want the row index you can use the getRowIndex() method.
 *
 * Priority is declared with Row X has priority over Row Y.
 * This RowNumber class contains the row number of X.
 */
class RowNumber
        implements Comparable<RowNumber> {

    private Integer rowNumber;

    public RowNumber( final Integer value ) {
        rowNumber = value;
    }

    public Integer getRowNumber() {
        return rowNumber;
    }

    public int getRowIndex() {
        return rowNumber - 1;
    }

    @Override
    public int compareTo( final RowNumber other ) {
        if (other == null) {
            throw  new NullPointerException("We should never have an RowNumber class that is null." );
        }
        if (other== null && other.getRowNumber() == null) {
            return 0;
        }
        if (this.rowNumber == null) {
            return 1;
        } else {
            return this.rowNumber.compareTo(other.getRowNumber());
        }
    }

    @Override
    public boolean equals( final Object o ) {
        if ( this == o ) return true;
        if ( o == null || getClass() != o.getClass() ) return false;

        final RowNumber rowNumber1 = (RowNumber) o;

        return rowNumber != null ? rowNumber.equals( rowNumber1.rowNumber ) : rowNumber1.rowNumber == null;

    }

    @Override
    public int hashCode() {
        return rowNumber != null ? ~~rowNumber.hashCode() : 0;
    }
}
