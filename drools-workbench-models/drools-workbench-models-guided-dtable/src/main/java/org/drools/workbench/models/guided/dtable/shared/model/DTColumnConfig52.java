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

import java.util.ArrayList;
import java.util.List;

public class DTColumnConfig52
        implements BaseColumn,
                   DiffColumn {

    private static final long serialVersionUID = 510l;

    /**
     * Available fields for this type of column.
     */
    public static final String FIELD_DEFAULT_VALUE = "defaultValue";

    public static final String FIELD_HIDE_COLUMN = "hideColumn";

    public static final String FIELD_WIDTH = "width";

    public static final String FIELD_HEADER = "header";

    // Legacy Default Values were String however since 5.4 they are stored in a DTCellValue52 object
    public String defaultValue;

    // For a default value ! Will still be in the array of course, just use this value if its empty
    private DTCellValue52 typedDefaultValue = null;

    // To hide the column (eg if it has a mandatory default)
    private boolean hideColumn = false;

    //Column width
    private int width = -1;

    // The header to be displayed.
    private String header;

    public DTCellValue52 getDefaultValue() {
        return typedDefaultValue;
    }

    public int getWidth() {
        return width;
    }

    public boolean isHideColumn() {
        return hideColumn;
    }

    public void setDefaultValue( DTCellValue52 defaultValue ) {
        this.typedDefaultValue = defaultValue;
    }

    @Override
    public List<BaseColumnFieldDiff> diff( BaseColumn otherColumn ) {
        if ( otherColumn == null ) {
            return null;
        }

        List<BaseColumnFieldDiff> result = new ArrayList<BaseColumnFieldDiff>();
        DTColumnConfig52 other = (DTColumnConfig52) otherColumn;

        // Field: hide column.
        if ( this.isHideColumn() != other.isHideColumn() ) {
            result.add( new BaseColumnFieldDiffImpl( FIELD_HIDE_COLUMN,
                                                     this.isHideColumn(),
                                                     other.isHideColumn() ) );
        }

        // Field: default value.
        if ( !BaseColumnFieldDiffImpl.isEqualOrNull( this.getDefaultValue(),
                                                     other.getDefaultValue() ) ) {
            result.add( new BaseColumnFieldDiffImpl( FIELD_DEFAULT_VALUE,
                                                     extractDefaultValue( this.getDefaultValue() ),
                                                     extractDefaultValue( other.getDefaultValue() ) ) );
        }

        // Field: width.
        if ( this.getWidth() != other.getWidth() ) {
            result.add( new BaseColumnFieldDiffImpl( FIELD_WIDTH,
                                                     this.getWidth(),
                                                     other.getWidth() ) );
        }

        // Field: header.
        if ( !isEqualOrNull( this.getHeader(),
                             other.getHeader() ) ) {
            result.add( new BaseColumnFieldDiffImpl( FIELD_HEADER,
                                                     this.getHeader(),
                                                     other.getHeader() ) );
        }

        return result;
    }

    protected Object extractDefaultValue( final DTCellValue52 dcv ) {
        switch ( dcv.getDataType() ) {
            case BOOLEAN:
                return dcv.getBooleanValue();
            case DATE:
                return dcv.getDateValue();
            case NUMERIC:
            case NUMERIC_BIGDECIMAL:
            case NUMERIC_BIGINTEGER:
            case NUMERIC_BYTE:
            case NUMERIC_DOUBLE:
            case NUMERIC_FLOAT:
            case NUMERIC_INTEGER:
            case NUMERIC_LONG:
            case NUMERIC_SHORT:
                return dcv.getNumericValue();
            default:
                return dcv.getStringValue();
        }
    }

    // Check whether two Objects are equal or both null
    public static boolean isEqualOrNull( final Object s1,
                                         final Object s2 ) {
        return BaseColumnFieldDiffImpl.isEqualOrNull( s1, s2 );
    }

    public void setHideColumn( boolean hideColumn ) {
        this.hideColumn = hideColumn;
    }

    public void setWidth( int width ) {
        this.width = width;
    }

    public void setHeader( String header ) {
        this.header = header;
    }

    public String getHeader() {
        return header;
    }

}
