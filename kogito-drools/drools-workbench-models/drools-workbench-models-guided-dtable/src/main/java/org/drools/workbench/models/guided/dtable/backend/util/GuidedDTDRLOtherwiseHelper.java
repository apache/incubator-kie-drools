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
package org.drools.workbench.models.guided.dtable.backend.util;

import org.drools.workbench.models.datamodel.rule.FieldConstraint;
import org.drools.workbench.models.datamodel.rule.SingleFieldConstraint;
import org.drools.workbench.models.guided.dtable.shared.model.BaseColumn;
import org.drools.workbench.models.guided.dtable.shared.model.ConditionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.DTCellValue52;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class to build Field Constraints for cells with "Otherwise" values
 */
public class GuidedDTDRLOtherwiseHelper {

    /**
     * OtherwiseBuilder for equals operators. This assembles a comma separated
     * list of non-null values contained in other cells in the column and
     * constructs a FieldConstraint with operator "not in" and the derived list:
     * e.g. not in ("a", "b", "c")
     */
    public static class EqualsOtherwiseBuilder extends AbstractOtherwiseBuilder {

        private EqualsOtherwiseBuilder() {
        }

        @Override
        FieldConstraint constructSingleFieldConstraint( ConditionCol52 c,
                                                        List<DTCellValue52> columnData ) {
            SingleFieldConstraint sfc = new SingleFieldConstraint( c.getFactField() );
            sfc.setConstraintValueType( c.getConstraintValueType() );
            sfc.setFieldType( c.getFieldType() );
            sfc.setOperator( "not in" );

            List<String> consumedValues = new ArrayList<String>();
            StringBuilder value = new StringBuilder();
            value.append( "( " );
            for ( DTCellValue52 cv : columnData ) {

                //Ensure cell values start and end with quotes
                String scv = GuidedDTDRLUtilities.convertDTCellValueToString( cv );
                if ( scv != null ) {
                    if ( !consumedValues.contains( scv ) ) {
                        value.append( scv ).append( ", " );
                    }
                    consumedValues.add( scv );
                }
            }
            value.delete( value.lastIndexOf( "," ),
                          value.length() - 1 );
            value.append( ")" );
            sfc.setValue( value.toString() );
            return sfc;
        }

    }

    /**
     * OtherwiseBuilder for not-equals operators. This assembles a comma
     * separated list of non-null values contained in other cells in the column
     * and constructs a FieldConstraint with operator "in" and the derived list:
     * e.g. in ("a", "b", "c")
     */
    public static class NotEqualsOtherwiseBuilder extends AbstractOtherwiseBuilder {

        private NotEqualsOtherwiseBuilder() {
        }

        @Override
        SingleFieldConstraint constructSingleFieldConstraint( ConditionCol52 c,
                                                              List<DTCellValue52> columnData ) {
            SingleFieldConstraint sfc = new SingleFieldConstraint( c.getFactField() );
            sfc.setConstraintValueType( c.getConstraintValueType() );
            sfc.setFieldType( c.getFieldType() );
            sfc.setOperator( "in" );

            List<String> consumedValues = new ArrayList<String>();
            StringBuilder value = new StringBuilder();
            value.append( "( " );
            for ( DTCellValue52 cv : columnData ) {

                //Ensure cell values start and end with quotes
                String scv = GuidedDTDRLUtilities.convertDTCellValueToString( cv );
                if ( scv != null ) {
                    if ( !consumedValues.contains( scv ) ) {
                        value.append( scv ).append( ", " );
                    }
                    consumedValues.add( scv );
                }
            }
            value.delete( value.lastIndexOf( "," ),
                          value.length() - 1 );
            value.append( ")" );
            sfc.setValue( value.toString() );
            return sfc;
        }

    }

    /**
     * Interface defining a factory method to build a FieldConstraint
     */
    public interface OtherwiseBuilder {

        /**
         * Build a Field Constraint
         * @param c Condition Column that contains the "Otherwise" cell
         * @param allColumns All Decision Table columns. Decision Tables have an
         * implied "and" between multiple SingleFieldConstraints for
         * the same Fact field. OtherwiseBuilders for less-than,
         * greater-than etc need access to other Condition Columns
         * bound to the same Fact and Field to construct a
         * CompositeFieldConstraint.
         * @param data Decision Table values
         * @return
         */
        FieldConstraint makeFieldConstraint( ConditionCol52 c,
                                             List<BaseColumn> allColumns,
                                             List<List<DTCellValue52>> data );

    }

    /**
     * Base OtherwiseBuilder that extracts a single column of data relating to
     * the ConditionCol from which the FieldConstraint will be constructed. This
     * will need to be re-factored if it is agreed that the implementation of
     * "Otherwise" for certain operators needs to provide
     * CompositeFieldConstraints.
     */
    static abstract class AbstractOtherwiseBuilder
            implements
            OtherwiseBuilder {

        public FieldConstraint makeFieldConstraint( ConditionCol52 c,
                                                    List<BaseColumn> allColumns,
                                                    List<List<DTCellValue52>> data ) {
            int index = allColumns.indexOf( c );
            List<DTCellValue52> columnData = extractColumnData( data,
                                                                index );
            return constructSingleFieldConstraint( c,
                                                   columnData );
        }

        //Template pattern, provide method for implementations to override
        abstract FieldConstraint constructSingleFieldConstraint( ConditionCol52 c,
                                                                 List<DTCellValue52> columnData );

    }

    /**
     * Retrieve the correct OtherwiseBuilder for the given column
     * @param c
     * @return
     */
    public static OtherwiseBuilder getBuilder( ConditionCol52 c ) {

        if ( c.getOperator().equals( "==" ) ) {
            return new EqualsOtherwiseBuilder();
        } else if ( c.getOperator().equals( "!=" ) ) {
            return new NotEqualsOtherwiseBuilder();
        }
        throw new IllegalArgumentException( "ConditionCol operator does not support Otherwise values" );
    }

    //Extract data relating to a single column
    private static List<DTCellValue52> extractColumnData( List<List<DTCellValue52>> data,
                                                          int columnIndex ) {
        List<DTCellValue52> columnData = new ArrayList<DTCellValue52>();
        for ( List<DTCellValue52> row : data ) {
            columnData.add( row.get( columnIndex ) );
        }
        return columnData;
    }

    //Utility factory class, no constructor
    private GuidedDTDRLOtherwiseHelper() {
    }

}
