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

/**
 * A column that sets a field on a new fact.
 */
public class LimitedEntryActionInsertFactCol52 extends ActionInsertFactCol52
        implements
        LimitedEntryCol {

    private static final long serialVersionUID = 510l;

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
        LimitedEntryActionInsertFactCol52 other = (LimitedEntryActionInsertFactCol52) otherColumn;

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

}
