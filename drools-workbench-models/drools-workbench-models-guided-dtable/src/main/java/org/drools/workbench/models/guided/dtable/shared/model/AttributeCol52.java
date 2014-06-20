/*
 * Copyright 2011 JBoss Inc
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
package org.drools.workbench.models.guided.dtable.shared.model;

import java.util.List;

/**
 * This is a rule attribute - eg salience, no-loop etc.
 */
public class AttributeCol52 extends DTColumnConfig52 {

    //Attribute name
    private String attribute;

    // To use the reverse order of the row number as the salience attribute
    private boolean reverseOrder = false;

    // To use the row number as number for the salience attribute.
    private boolean useRowNumber = false;

    /**
     * Available fields for this type of column.
     */
    public static final String FIELD_ATTRIBUTE = "attribute";

    public static final String FIELD_REVERSE_ORDER = "reverseOrder";

    public static final String FIELD_USE_ROW_NUMBER = "useRowNumber";

    @Override
    public List<BaseColumnFieldDiff> diff( BaseColumn otherColumn ) {
        if ( otherColumn == null ) {
            return null;
        }

        List<BaseColumnFieldDiff> result = super.diff( otherColumn );
        AttributeCol52 other = (AttributeCol52) otherColumn;

        // Field: attribute.
        if ( !isEqualOrNull( this.getAttribute(),
                             other.getAttribute() ) ) {
            result.add( new BaseColumnFieldDiffImpl( FIELD_ATTRIBUTE,
                                                     this.getAttribute(),
                                                     other.getAttribute() ) );
        }

        // Field: reverseOrder.
        if ( this.isReverseOrder() != other.isReverseOrder() ) {
            result.add( new BaseColumnFieldDiffImpl( FIELD_REVERSE_ORDER,
                                                     this.isReverseOrder(),
                                                     other.isReverseOrder() ) );
        }

        // Field: useRowNumber.
        if ( this.isUseRowNumber() != other.isUseRowNumber() ) {
            result.add( new BaseColumnFieldDiffImpl( FIELD_USE_ROW_NUMBER,
                                                     this.isUseRowNumber(),
                                                     other.isUseRowNumber() ) );
        }

        return result;
    }

    /**
     * Clones this metadata column instance.
     * @return The cloned instance.
     */
    public AttributeCol52 cloneColumn() {
        AttributeCol52 cloned = new AttributeCol52();
        cloned.setAttribute( getAttribute() );
        cloned.setReverseOrder( isReverseOrder() );
        cloned.setUseRowNumber( isUseRowNumber() );
        cloned.setWidth( getWidth() );
        cloned.setHideColumn( isHideColumn() );
        cloned.setHeader( getHeader() );
        cloned.setDefaultValue( getDefaultValue() != null ? getDefaultValue().cloneDefaultValueCell() : null );
        return cloned;
    }

    public String getAttribute() {
        return attribute;
    }

    public void setAttribute( String attribute ) {
        this.attribute = attribute;
    }

    public boolean isUseRowNumber() {
        return useRowNumber;
    }

    public void setUseRowNumber( boolean useRowNumber ) {
        this.useRowNumber = useRowNumber;
    }

    public boolean isReverseOrder() {
        return reverseOrder;
    }

    public void setReverseOrder( boolean reverseOrder ) {
        this.reverseOrder = reverseOrder;
    }

}
