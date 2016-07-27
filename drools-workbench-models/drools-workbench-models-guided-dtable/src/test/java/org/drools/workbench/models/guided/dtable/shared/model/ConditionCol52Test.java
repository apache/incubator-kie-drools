/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.workbench.models.guided.dtable.shared.model;

import java.util.List;

import org.drools.workbench.models.datamodel.rule.BaseSingleFieldConstraint;
import org.junit.Before;
import org.junit.Test;

import static org.drools.workbench.models.guided.dtable.shared.model.ConditionCol52.*;
import static org.drools.workbench.models.guided.dtable.shared.model.DTColumnConfig52.FIELD_DEFAULT_VALUE;
import static org.drools.workbench.models.guided.dtable.shared.model.DTColumnConfig52.FIELD_HEADER;
import static org.drools.workbench.models.guided.dtable.shared.model.DTColumnConfig52.FIELD_HIDE_COLUMN;
import static org.junit.Assert.*;

public class ConditionCol52Test extends ColumnTestBase {

    private ConditionCol52 column1;
    private ConditionCol52 column2;

    @Before
    public void setup() {
        column1 = new ConditionCol52();
        column1.setFactField( "field" );
        column1.setFieldType( "Type" );
        column1.setOperator( "==" );
        column1.setValueList( "a,b,c" );
        column1.setBinding( "$var" );
        column1.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        column1.setHeader( "header" );
        column1.setHideColumn( false );
        column1.setDefaultValue( new DTCellValue52( "default" ) );

        column2 = new ConditionCol52();
        column2.setFactField( "field" );
        column2.setFieldType( "Type" );
        column2.setOperator( "==" );
        column2.setValueList( "a,b,c" );
        column2.setBinding( "$var" );
        column2.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        column2.setHeader( "header" );
        column2.setHideColumn( false );
        column2.setDefaultValue( new DTCellValue52( "default" ) );
    }

    @Test
    public void testDiffEmpty() {
        checkDiffEmpty( column1, column2 );
    }

    @Test
    public void testDiffFactField() {
        column1.setFactField( "field1" );
        column2.setFactField( "field2" );

        checkSingleDiff( FIELD_FACT_FIELD,
                         "field1",
                         "field2",
                         column1,
                         column2 );
    }

    @Test
    public void testDiffFieldType() {
        column1.setFieldType( "Type1" );
        column2.setFieldType( "Type2" );

        checkSingleDiff( FIELD_FIELD_TYPE,
                         "Type1",
                         "Type2",
                         column1,
                         column2 );
    }

    @Test
    public void testDiffOperator() {
        column1.setOperator( "<" );
        column2.setOperator( ">" );

        checkSingleDiff( FIELD_OPERATOR,
                         "<",
                         ">",
                         column1,
                         column2 );
    }

    @Test
    public void testDiffValueList() {
        column1.setValueList( "v,a,l,u,e" );
        column2.setValueList( "l,i,s,t" );

        checkSingleDiff( FIELD_VALUE_LIST,
                         "v,a,l,u,e",
                         "l,i,s,t",
                         column1,
                         column2 );
    }

    @Test
    public void testDiffBinding() {
        column1.setBinding( "$var1" );
        column2.setBinding( "$var2" );

        checkSingleDiff( FIELD_BINDING,
                         "$var1",
                         "$var2",
                         column1,
                         column2 );
    }

    @Test
    public void testDiffConstraintType() {
        column1.setConstraintValueType( BaseSingleFieldConstraint.TYPE_PREDICATE );
        column2.setConstraintValueType( BaseSingleFieldConstraint.TYPE_RET_VALUE );

        checkSingleDiff( FIELD_CONSTRAINT_VALUE_TYPE,
                         BaseSingleFieldConstraint.TYPE_PREDICATE,
                         BaseSingleFieldConstraint.TYPE_RET_VALUE,
                         column1,
                         column2 );
    }

    @Test
    public void testDiffDefaultValueOriginalValueIsNull() {
        column1.setDefaultValue( null );
        column2.setDefaultValue( new DTCellValue52( "default" ) );

        checkSingleDiff( FIELD_DEFAULT_VALUE,
                         null,
                         "default",
                         column1,
                         column2 );
    }

    @Test
    public void testDiffDefaultValueNewValueIsNull() {
        column1.setDefaultValue( new DTCellValue52( "default" ) );
        column2.setDefaultValue( null );

        checkSingleDiff( FIELD_DEFAULT_VALUE,
                         "default",
                         null,
                         column1,
                         column2 );
    }

    @Test
    public void testDiffAll() {
        column1.setFactField( "field1" );
        column1.setFieldType( "Type1" );
        column1.setOperator( "<" );
        column1.setValueList( "v,a,l,u,e" );
        column1.setBinding( "$var1" );
        column1.setConstraintValueType( BaseSingleFieldConstraint.TYPE_PREDICATE );
        column1.setHeader( "header1" );
        column1.setHideColumn( false );
        column1.setDefaultValue( new DTCellValue52( "default1" ) );
        column2.setFactField( "field2" );
        column2.setFieldType( "Type2" );
        column2.setOperator( ">" );
        column2.setValueList( "l,i,s,t" );
        column2.setBinding( "$var2" );
        column2.setConstraintValueType( BaseSingleFieldConstraint.TYPE_RET_VALUE );
        column2.setHeader( "header2" );
        column2.setHideColumn( true );
        column2.setDefaultValue( new DTCellValue52( "default2" ) );

        List<BaseColumnFieldDiff> diff = column1.diff( column2 );
        assertNotNull( diff );
        assertEquals( 9,
                      diff.size() );
        assertEquals( FIELD_HIDE_COLUMN,
                      diff.get( 0 ).getFieldName() );
        assertEquals( false,
                      diff.get( 0 ).getOldValue() );
        assertEquals( true,
                      diff.get( 0 ).getValue() );
        assertEquals( FIELD_DEFAULT_VALUE,
                      diff.get( 1 ).getFieldName() );
        assertEquals( "default1",
                      diff.get( 1 ).getOldValue() );
        assertEquals( "default2",
                      diff.get( 1 ).getValue() );
        assertEquals( FIELD_HEADER,
                      diff.get( 2 ).getFieldName() );
        assertEquals( "header1",
                      diff.get( 2 ).getOldValue() );
        assertEquals( "header2",
                      diff.get( 2 ).getValue() );
        assertEquals( FIELD_FACT_FIELD,
                      diff.get( 3 ).getFieldName() );
        assertEquals( "field1",
                      diff.get( 3 ).getOldValue() );
        assertEquals( "field2",
                      diff.get( 3 ).getValue() );
        assertEquals( FIELD_FIELD_TYPE,
                      diff.get( 4 ).getFieldName() );
        assertEquals( "Type1",
                      diff.get( 4 ).getOldValue() );
        assertEquals( "Type2",
                      diff.get( 4 ).getValue() );
        assertEquals( FIELD_OPERATOR,
                      diff.get( 5 ).getFieldName() );
        assertEquals( "<",
                      diff.get( 5 ).getOldValue() );
        assertEquals( ">",
                      diff.get( 5 ).getValue() );
        assertEquals( FIELD_VALUE_LIST,
                      diff.get( 6 ).getFieldName() );
        assertEquals( "v,a,l,u,e",
                      diff.get( 6 ).getOldValue() );
        assertEquals( "l,i,s,t",
                      diff.get( 6 ).getValue() );
        assertEquals( FIELD_BINDING,
                      diff.get( 7 ).getFieldName() );
        assertEquals( "$var1",
                      diff.get( 7 ).getOldValue() );
        assertEquals( "$var2",
                      diff.get( 7 ).getValue() );
        assertEquals( FIELD_CONSTRAINT_VALUE_TYPE,
                      diff.get( 8 ).getFieldName() );
        assertEquals( BaseSingleFieldConstraint.TYPE_PREDICATE,
                      diff.get( 8 ).getOldValue() );
        assertEquals( BaseSingleFieldConstraint.TYPE_RET_VALUE,
                      diff.get( 8 ).getValue() );
    }
}