/*
 * Copyright 2011 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.workbench.models.guided.dtable.shared.model;

import java.util.List;
import java.util.Objects;

/**
 * A column that sets the value of an existing fact.
 */
public class LimitedEntryActionSetFieldCol52 extends ActionSetFieldCol52
        implements
        LimitedEntryCol {

    private static final long serialVersionUID = 729l;

    /**
     * Available fields for this type of column.
     */
    public static final String FIELD_VALUE = "value";

    private DTCellValue52 value;

    @Override
    public List<BaseColumnFieldDiff> diff( BaseColumn otherColumn ) {
        if ( otherColumn == null ) {
            return null;
        }

        List<BaseColumnFieldDiff> result = super.diff( otherColumn );
        LimitedEntryActionSetFieldCol52 other = (LimitedEntryActionSetFieldCol52) otherColumn;

        // Field: default value.
        if ( !BaseColumnFieldDiffImpl.isEqualOrNull( this.getValue(),
                                                     other.getValue() ) ) {
            result.add( new BaseColumnFieldDiffImpl( FIELD_VALUE,
                                                     extractDefaultValue( this.getValue() ),
                                                     extractDefaultValue( other.getValue() ) ) );
        }

        return result;
    }

    public DTCellValue52 getValue() {
        return value;
    }

    public void setValue( DTCellValue52 value ) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof LimitedEntryActionSetFieldCol52)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        LimitedEntryActionSetFieldCol52 that = (LimitedEntryActionSetFieldCol52) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result=~~result;
        result = 31 * result + (value != null ? value.hashCode() : 0);
        result=~~result;
        return result;
    }
}
