/*
 * Copyright 2011 Red Hat, Inc. and/or its affiliates.
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

import java.util.ArrayList;
import java.util.List;

import org.drools.workbench.models.datamodel.rule.IPattern;

import static java.lang.Math.*;

/**
 * A Condition column defined with a BRL fragment
 */
public class BRLConditionColumn extends ConditionCol52
        implements
        BRLColumn<IPattern, BRLConditionVariableColumn> {

    private static final long serialVersionUID = 540l;

    private List<IPattern> definition = new ArrayList<IPattern>();

    private List<BRLConditionVariableColumn> childColumns = new ArrayList<BRLConditionVariableColumn>();

    /**
     * Available fields for this type of column.
     */
    public static final String FIELD_DEFINITION = "definition";

    public static final String FIELD_CHILD_COLUMNS = "childColumns";

    @Override
    public List<BaseColumnFieldDiff> diff( BaseColumn otherColumn ) {
        if ( otherColumn == null ) {
            return null;
        }

        List<BaseColumnFieldDiff> result = super.diff( otherColumn );
        BRLConditionColumn other = (BRLConditionColumn) otherColumn;

        // Field: definition.
        if ( !isEqualOrNull( this.getDefinition(),
                             other.getDefinition() ) ) {
            result.add( new BaseColumnFieldDiffImpl( FIELD_DEFINITION,
                                                     this.getDefinition(),
                                                     other.getDefinition() ) );
        }

        // Field: childColumns.
        if ( !isEqualOrNull( this.getChildColumns(),
                             other.getChildColumns() ) ) {
            result.addAll( getColumnDiffs( other.getChildColumns() ) );
        }

        return result;
    }

    private List<BaseColumnFieldDiff> getColumnDiffs( List<BRLConditionVariableColumn> otherChildColumns ) {
        int commonLength = min( this.childColumns.size(), otherChildColumns.size() );
        List<BaseColumnFieldDiff> result = new ArrayList<>();
        for ( int i = 0; i < commonLength; i++ ) {
            result.addAll( this.childColumns.get( i ).diff( otherChildColumns.get( i ) ) );
        }
        result.addAll( getDiffsForUnpairedColumns( this.childColumns, commonLength, false ) );
        result.addAll( getDiffsForUnpairedColumns( otherChildColumns, commonLength, true ) );
        return result;
    }

    private List<BaseColumnFieldDiff> getDiffsForUnpairedColumns( List<BRLConditionVariableColumn> addedChildColumns,
                                                                  int commonLength,
                                                                  boolean added ) {
        List<BaseColumnFieldDiff> result = new ArrayList<>();
        if ( addedChildColumns.size() > commonLength ) {
            for ( BRLConditionVariableColumn column : addedChildColumns.subList( commonLength, addedChildColumns.size() ) ) {
                result.add( new BaseColumnFieldDiffImpl( FIELD_CHILD_COLUMNS,
                                                         ( added ) ? null : column,
                                                         ( added ) ? column : null ) );
            }
        }
        return result;
    }

    public List<IPattern> getDefinition() {
        return this.definition;
    }

    public void setDefinition( List<IPattern> definition ) {
        this.definition = definition;
    }

    public List<BRLConditionVariableColumn> getChildColumns() {
        return this.childColumns;
    }

    public void setChildColumns( List<BRLConditionVariableColumn> childColumns ) {
        this.childColumns = childColumns;
    }

    @Override
    public void setHideColumn( boolean hideColumn ) {
        super.setHideColumn( hideColumn );
        for ( BRLConditionVariableColumn variable : this.childColumns ) {
            variable.setHideColumn( hideColumn );
        }
    }

}
