/*
 * Copyright 2014 JBoss Inc
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

package org.drools.workbench.models.guided.dtree.backend;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.Date;

import org.drools.workbench.models.datamodel.oracle.DataType;
import org.drools.workbench.models.guided.dtree.shared.model.GuidedDecisionTree;
import org.drools.workbench.models.guided.dtree.shared.model.nodes.ActionInsertNode;
import org.drools.workbench.models.guided.dtree.shared.model.nodes.ActionRetractNode;
import org.drools.workbench.models.guided.dtree.shared.model.nodes.ActionUpdateNode;
import org.drools.workbench.models.guided.dtree.shared.model.nodes.ConstraintNode;
import org.drools.workbench.models.guided.dtree.shared.model.nodes.TypeNode;
import org.drools.workbench.models.guided.dtree.shared.model.nodes.impl.ActionFieldValueImpl;
import org.drools.workbench.models.guided.dtree.shared.model.nodes.impl.ActionInsertNodeImpl;
import org.drools.workbench.models.guided.dtree.shared.model.nodes.impl.ActionRetractNodeImpl;
import org.drools.workbench.models.guided.dtree.shared.model.nodes.impl.ActionUpdateNodeImpl;
import org.drools.workbench.models.guided.dtree.shared.model.nodes.impl.ConstraintNodeImpl;
import org.drools.workbench.models.guided.dtree.shared.model.nodes.impl.TypeNodeImpl;
import org.drools.workbench.models.guided.dtree.shared.model.values.impl.BigDecimalValue;
import org.drools.workbench.models.guided.dtree.shared.model.values.impl.BigIntegerValue;
import org.drools.workbench.models.guided.dtree.shared.model.values.impl.BooleanValue;
import org.drools.workbench.models.guided.dtree.shared.model.values.impl.ByteValue;
import org.drools.workbench.models.guided.dtree.shared.model.values.impl.DateValue;
import org.drools.workbench.models.guided.dtree.shared.model.values.impl.DoubleValue;
import org.drools.workbench.models.guided.dtree.shared.model.values.impl.EnumValue;
import org.drools.workbench.models.guided.dtree.shared.model.values.impl.FloatValue;
import org.drools.workbench.models.guided.dtree.shared.model.values.impl.IntegerValue;
import org.drools.workbench.models.guided.dtree.shared.model.values.impl.LongValue;
import org.drools.workbench.models.guided.dtree.shared.model.values.impl.ShortValue;
import org.drools.workbench.models.guided.dtree.shared.model.values.impl.StringValue;
import org.junit.Test;

import static org.junit.Assert.*;

public class GuidedDecisionTreeDRLPersistenceUnmarshallingTest extends AbstractGuidedDecisionTreeDRLPersistenceUnmarshallingTest {

    @Test
    public void testSingleRule_ZeroConstraints() throws Exception {
        final String drl = "rule \"test_0\"" +
                "when \n" +
                "  Person()\n" +
                "then \n" +
                "end";

        final GuidedDecisionTree expected = new GuidedDecisionTree();
        expected.setTreeName( "test" );

        final TypeNode type = new TypeNodeImpl( "Person" );
        expected.setRoot( type );

        addModelField( "Person",
                       "this",
                       "Person",
                       DataType.TYPE_THIS );

        final GuidedDecisionTree model = getAndTestUnmarshalledModel( drl, "test", 0 );

        assertEquals( expected.getTreeName(),
                      model.getTreeName() );

        assertNotNull( model.getRoot() );
        assertEquals( type.getClassName(),
                      model.getRoot().getClassName() );
        assertFalse( model.getRoot().isBound() );

        assertEquals( 0,
                      model.getRoot().getChildren().size() );
    }

    @Test
    public void testSingleRule_InPackage() throws Exception {
        final String drl = "package org.drools.workbench.models.guided.dtree.backend; \n" +
                "rule \"test_0\"" +
                "when \n" +
                "  Person( name == \"Michael\" )\n" +
                "then \n" +
                "end";

        final GuidedDecisionTree expected = new GuidedDecisionTree();
        expected.setTreeName( "test" );

        final TypeNode type = new TypeNodeImpl( "Person" );
        final ConstraintNode c1 = new ConstraintNodeImpl( "Person",
                                                          "name",
                                                          "==",
                                                          new StringValue( "Michael" ) );
        expected.setRoot( type );
        type.addChild( c1 );

        addModelField( "org.drools.workbench.models.guided.dtree.backend.Person",
                       "this",
                       "org.drools.workbench.models.guided.dtree.backend.Person",
                       DataType.TYPE_THIS );
        addModelField( "org.drools.workbench.models.guided.dtree.backend.Person",
                       "name",
                       String.class.getName(),
                       DataType.TYPE_STRING );

        final GuidedDecisionTree model = getAndTestUnmarshalledModel( drl, "test", 0 );
        assertEquals( expected.getTreeName(),
                      model.getTreeName() );

        assertNotNull( model.getRoot() );
        assertEquals( type.getClassName(),
                      model.getRoot().getClassName() );
        assertFalse( model.getRoot().isBound() );

        assertEquals( 1,
                      model.getRoot().getChildren().size() );
        assertNotNull( model.getRoot().getChildren().get( 0 ) );
        assertTrue( model.getRoot().getChildren().get( 0 ) instanceof ConstraintNode );

        final ConstraintNode _c1 = (ConstraintNode) model.getRoot().getChildren().get( 0 );

        assertEquals( c1.getClassName(),
                      _c1.getClassName() );
        assertEquals( c1.getFieldName(),
                      _c1.getFieldName() );
        assertEquals( c1.getOperator(),
                      _c1.getOperator() );
        assertEquals( c1.getValue().getValue().toString(),
                      _c1.getValue().getValue().toString() );
    }

    @Test
    public void testSingleRule_WithImport() throws Exception {
        final String drl = "package smurf; \n" +
                "import org.drools.workbench.models.guided.dtree.backend.Person; \n" +
                "rule \"test_0\"" +
                "when \n" +
                "  Person( name == \"Michael\" )\n" +
                "then \n" +
                "end";

        final GuidedDecisionTree expected = new GuidedDecisionTree();
        expected.setTreeName( "test" );

        final TypeNode type = new TypeNodeImpl( "Person" );
        final ConstraintNode c1 = new ConstraintNodeImpl( "Person",
                                                          "name",
                                                          "==",
                                                          new StringValue( "Michael" ) );
        expected.setRoot( type );
        type.addChild( c1 );

        addModelField( "org.drools.workbench.models.guided.dtree.backend.Person",
                       "this",
                       "org.drools.workbench.models.guided.dtree.backend.Person",
                       DataType.TYPE_THIS );
        addModelField( "org.drools.workbench.models.guided.dtree.backend.Person",
                       "name",
                       String.class.getName(),
                       DataType.TYPE_STRING );

        final GuidedDecisionTree model = getAndTestUnmarshalledModel( drl, "test", 0 );
        assertEquals( expected.getTreeName(),
                      model.getTreeName() );

        assertNotNull( model.getRoot() );
        assertEquals( type.getClassName(),
                      model.getRoot().getClassName() );
        assertFalse( model.getRoot().isBound() );

        assertEquals( 1,
                      model.getRoot().getChildren().size() );
        assertNotNull( model.getRoot().getChildren().get( 0 ) );
        assertTrue( model.getRoot().getChildren().get( 0 ) instanceof ConstraintNode );

        final ConstraintNode _c1 = (ConstraintNode) model.getRoot().getChildren().get( 0 );

        assertEquals( c1.getClassName(),
                      _c1.getClassName() );
        assertEquals( c1.getFieldName(),
                      _c1.getFieldName() );
        assertEquals( c1.getOperator(),
                      _c1.getOperator() );
        assertEquals( c1.getValue().getValue().toString(),
                      _c1.getValue().getValue().toString() );
    }

    @Test
    public void testSingleRule_SingleConstraint() throws Exception {
        final String drl = "rule \"test_0\"" +
                "when \n" +
                "  Person( name == \"Michael\" )\n" +
                "then \n" +
                "end";

        final GuidedDecisionTree expected = new GuidedDecisionTree();
        expected.setTreeName( "test" );

        final TypeNode type = new TypeNodeImpl( "Person" );
        final ConstraintNode c1 = new ConstraintNodeImpl( "Person",
                                                          "name",
                                                          "==",
                                                          new StringValue( "Michael" ) );
        expected.setRoot( type );
        type.addChild( c1 );

        addModelField( "Person",
                       "this",
                       "Person",
                       DataType.TYPE_THIS );
        addModelField( "Person",
                       "name",
                       String.class.getName(),
                       DataType.TYPE_STRING );

        final GuidedDecisionTree model = getAndTestUnmarshalledModel( drl, "test", 0 );
        assertEquals( expected.getTreeName(),
                      model.getTreeName() );

        assertNotNull( model.getRoot() );
        assertEquals( type.getClassName(),
                      model.getRoot().getClassName() );
        assertFalse( model.getRoot().isBound() );

        assertEquals( 1,
                      model.getRoot().getChildren().size() );
        assertNotNull( model.getRoot().getChildren().get( 0 ) );
        assertTrue( model.getRoot().getChildren().get( 0 ) instanceof ConstraintNode );

        final ConstraintNode _c1 = (ConstraintNode) model.getRoot().getChildren().get( 0 );

        assertEquals( c1.getClassName(),
                      _c1.getClassName() );
        assertEquals( c1.getFieldName(),
                      _c1.getFieldName() );
        assertEquals( c1.getOperator(),
                      _c1.getOperator() );
        assertEquals( c1.getValue().getValue().toString(),
                      _c1.getValue().getValue().toString() );
    }

    @Test
    public void testSingleRule_SingleConstraintNoOperatorNoValue() throws Exception {
        final String drl = "rule \"test_0\"" +
                "when \n" +
                "  Person( name )\n" +
                "then \n" +
                "end";

        final GuidedDecisionTree expected = new GuidedDecisionTree();
        expected.setTreeName( "test" );

        final TypeNode type = new TypeNodeImpl( "Person" );
        final ConstraintNode c1 = new ConstraintNodeImpl( "Person",
                                                          "name" );
        expected.setRoot( type );
        type.addChild( c1 );

        addModelField( "Person",
                       "this",
                       "Person",
                       DataType.TYPE_THIS );
        addModelField( "Person",
                       "name",
                       String.class.getName(),
                       DataType.TYPE_STRING );

        final GuidedDecisionTree model = getAndTestUnmarshalledModel( drl, "test", 0 );
        assertEquals( expected.getTreeName(),
                      model.getTreeName() );

        assertNotNull( model.getRoot() );
        assertEquals( type.getClassName(),
                      model.getRoot().getClassName() );
        assertFalse( model.getRoot().isBound() );

        assertEquals( 1,
                      model.getRoot().getChildren().size() );
        assertNotNull( model.getRoot().getChildren().get( 0 ) );
        assertTrue( model.getRoot().getChildren().get( 0 ) instanceof ConstraintNode );

        final ConstraintNode _c1 = (ConstraintNode) model.getRoot().getChildren().get( 0 );

        assertEquals( c1.getClassName(),
                      _c1.getClassName() );
        assertEquals( c1.getFieldName(),
                      _c1.getFieldName() );
        assertNull( _c1.getOperator() );
        assertNull( _c1.getValue() );
    }

    @Test
    public void testSingleRule_MultipleConstraints() throws Exception {
        final String drl = "rule \"test_0\"" +
                "when \n" +
                "  Person( name == \"Michael\", age == 41 )\n" +
                "then \n" +
                "end";

        final GuidedDecisionTree expected = new GuidedDecisionTree();
        expected.setTreeName( "test" );

        final TypeNode type = new TypeNodeImpl( "Person" );
        final ConstraintNode c1 = new ConstraintNodeImpl( "Person",
                                                          "name",
                                                          "==",
                                                          new StringValue( "Michael" ) );
        final ConstraintNode c2 = new ConstraintNodeImpl( "Person",
                                                          "age",
                                                          "==",
                                                          new IntegerValue( 41 ) );
        expected.setRoot( type );
        type.addChild( c1 );
        c1.addChild( c2 );

        addModelField( "Person",
                       "this",
                       "Person",
                       DataType.TYPE_THIS );
        addModelField( "Person",
                       "name",
                       String.class.getName(),
                       DataType.TYPE_STRING );
        addModelField( "Person",
                       "age",
                       Integer.class.getName(),
                       DataType.TYPE_NUMERIC_INTEGER );

        final GuidedDecisionTree model = getAndTestUnmarshalledModel( drl, "test", 0 );
        assertEquals( expected.getTreeName(),
                      model.getTreeName() );

        assertNotNull( model.getRoot() );
        final TypeNode _t1 = model.getRoot();

        assertEquals( type.getClassName(),
                      _t1.getClassName() );
        assertFalse( _t1.isBound() );

        assertEquals( 1,
                      _t1.getChildren().size() );
        assertNotNull( _t1.getChildren().get( 0 ) );
        assertTrue( _t1.getChildren().get( 0 ) instanceof ConstraintNode );

        final ConstraintNode _c1 = (ConstraintNode) _t1.getChildren().get( 0 );

        assertEquals( c1.getClassName(),
                      _c1.getClassName() );
        assertEquals( c1.getFieldName(),
                      _c1.getFieldName() );
        assertEquals( c1.getOperator(),
                      _c1.getOperator() );
        assertEquals( c1.getValue().getValue().toString(),
                      _c1.getValue().getValue().toString() );

        assertEquals( 1,
                      _c1.getChildren().size() );
        assertNotNull( _c1.getChildren().get( 0 ) );
        assertTrue( _c1.getChildren().get( 0 ) instanceof ConstraintNode );

        final ConstraintNode _c2 = (ConstraintNode) _c1.getChildren().get( 0 );

        assertEquals( c2.getClassName(),
                      _c2.getClassName() );
        assertEquals( c2.getFieldName(),
                      _c2.getFieldName() );
        assertEquals( c2.getOperator(),
                      _c2.getOperator() );
        assertEquals( c2.getValue().getValue().toString(),
                      _c2.getValue().getValue().toString() );

        assertEquals( 0,
                      _c2.getChildren().size() );
    }

    @Test
    public void testMultipleRules_2Rules() throws Exception {
        final String drl = "rule \"test_0\"" +
                "when \n" +
                "  Person( name == \"Michael\" )\n" +
                "then \n" +
                "end \n" +
                "rule \"test_1\"" +
                "when \n" +
                "  Person( age == 41 )\n" +
                "then \n" +
                "end \n";

        final GuidedDecisionTree expected = new GuidedDecisionTree();
        expected.setTreeName( "test" );

        final TypeNode type = new TypeNodeImpl( "Person" );
        final ConstraintNode c1 = new ConstraintNodeImpl( "Person",
                                                          "name",
                                                          "==",
                                                          new StringValue( "Michael" ) );
        final ConstraintNode c2 = new ConstraintNodeImpl( "Person",
                                                          "age",
                                                          "==",
                                                          new IntegerValue( 41 ) );
        expected.setRoot( type );
        type.addChild( c1 );
        type.addChild( c2 );

        addModelField( "Person",
                       "this",
                       "Person",
                       DataType.TYPE_THIS );
        addModelField( "Person",
                       "name",
                       String.class.getName(),
                       DataType.TYPE_STRING );
        addModelField( "Person",
                       "age",
                       Integer.class.getName(),
                       DataType.TYPE_NUMERIC_INTEGER );

        final GuidedDecisionTree model = getAndTestUnmarshalledModel( drl, "test", 0 );
        assertEquals( expected.getTreeName(),
                      model.getTreeName() );

        assertNotNull( model.getRoot() );
        final TypeNode _t1 = model.getRoot();

        assertEquals( type.getClassName(),
                      _t1.getClassName() );
        assertFalse( _t1.isBound() );

        assertEquals( 2,
                      _t1.getChildren().size() );
        assertNotNull( _t1.getChildren().get( 0 ) );
        assertTrue( _t1.getChildren().get( 0 ) instanceof ConstraintNode );

        final ConstraintNode _c1 = (ConstraintNode) _t1.getChildren().get( 0 );

        assertEquals( c1.getClassName(),
                      _c1.getClassName() );
        assertEquals( c1.getFieldName(),
                      _c1.getFieldName() );
        assertEquals( c1.getOperator(),
                      _c1.getOperator() );
        assertEquals( c1.getValue().getValue().toString(),
                      _c1.getValue().getValue().toString() );

        assertNotNull( _t1.getChildren().get( 1 ) );
        assertTrue( _t1.getChildren().get( 1 ) instanceof ConstraintNode );

        final ConstraintNode _c2 = (ConstraintNode) _t1.getChildren().get( 1 );

        assertEquals( c2.getClassName(),
                      _c2.getClassName() );
        assertEquals( c2.getFieldName(),
                      _c2.getFieldName() );
        assertEquals( c2.getOperator(),
                      _c2.getOperator() );
        assertEquals( c2.getValue().getValue().toString(),
                      _c2.getValue().getValue().toString() );

        assertEquals( 0,
                      _c1.getChildren().size() );
        assertEquals( 0,
                      _c2.getChildren().size() );
    }

    @Test
    public void testMultipleRules_3Rules() throws Exception {
        final String drl = "rule \"test_0\"" +
                "when \n" +
                "  Person( name == \"Michael\" )\n" +
                "then \n" +
                "end \n" +
                "rule \"test_1\"" +
                "when \n" +
                "  Person( age == 41 )\n" +
                "then \n" +
                "end \n" +
                "rule \"test_2\"" +
                "when \n" +
                "  Person( gender == \"Male\" )\n" +
                "then \n" +
                "end \n";

        final GuidedDecisionTree expected = new GuidedDecisionTree();
        expected.setTreeName( "test" );

        final TypeNode type = new TypeNodeImpl( "Person" );
        final ConstraintNode c1 = new ConstraintNodeImpl( "Person",
                                                          "name",
                                                          "==",
                                                          new StringValue( "Michael" ) );
        final ConstraintNode c2 = new ConstraintNodeImpl( "Person",
                                                          "age",
                                                          "==",
                                                          new IntegerValue( 41 ) );
        final ConstraintNode c3 = new ConstraintNodeImpl( "Person",
                                                          "gender",
                                                          "==",
                                                          new StringValue( "Male" ) );
        expected.setRoot( type );
        type.addChild( c1 );
        type.addChild( c2 );
        type.addChild( c3 );

        addModelField( "Person",
                       "this",
                       "Person",
                       DataType.TYPE_THIS );
        addModelField( "Person",
                       "name",
                       String.class.getName(),
                       DataType.TYPE_STRING );
        addModelField( "Person",
                       "age",
                       Integer.class.getName(),
                       DataType.TYPE_NUMERIC_INTEGER );
        addModelField( "Person",
                       "gender",
                       String.class.getName(),
                       DataType.TYPE_STRING );

        final GuidedDecisionTree model = getAndTestUnmarshalledModel( drl, "test", 0 );
        assertEquals( expected.getTreeName(),
                      model.getTreeName() );

        assertNotNull( model.getRoot() );
        final TypeNode _t1 = model.getRoot();

        assertEquals( type.getClassName(),
                      _t1.getClassName() );
        assertFalse( _t1.isBound() );

        assertEquals( 3,
                      _t1.getChildren().size() );
        assertNotNull( _t1.getChildren().get( 0 ) );
        assertTrue( _t1.getChildren().get( 0 ) instanceof ConstraintNode );

        final ConstraintNode _c1 = (ConstraintNode) _t1.getChildren().get( 0 );

        assertEquals( c1.getClassName(),
                      _c1.getClassName() );
        assertEquals( c1.getFieldName(),
                      _c1.getFieldName() );
        assertEquals( c1.getOperator(),
                      _c1.getOperator() );
        assertEquals( c1.getValue().getValue().toString(),
                      _c1.getValue().getValue().toString() );

        assertNotNull( _t1.getChildren().get( 1 ) );
        assertTrue( _t1.getChildren().get( 1 ) instanceof ConstraintNode );

        final ConstraintNode _c2 = (ConstraintNode) _t1.getChildren().get( 1 );

        assertEquals( c2.getClassName(),
                      _c2.getClassName() );
        assertEquals( c2.getFieldName(),
                      _c2.getFieldName() );
        assertEquals( c2.getOperator(),
                      _c2.getOperator() );
        assertEquals( c2.getValue().getValue().toString(),
                      _c2.getValue().getValue().toString() );

        assertNotNull( _t1.getChildren().get( 2 ) );
        assertTrue( _t1.getChildren().get( 2 ) instanceof ConstraintNode );

        final ConstraintNode _c3 = (ConstraintNode) _t1.getChildren().get( 2 );

        assertEquals( c3.getClassName(),
                      _c3.getClassName() );
        assertEquals( c3.getFieldName(),
                      _c3.getFieldName() );
        assertEquals( c3.getOperator(),
                      _c3.getOperator() );
        assertEquals( c3.getValue().getValue().toString(),
                      _c3.getValue().getValue().toString() );

        assertEquals( 0,
                      _c1.getChildren().size() );
        assertEquals( 0,
                      _c2.getChildren().size() );
        assertEquals( 0,
                      _c3.getChildren().size() );
    }

    @Test
    public void testMultipleRules_2Rules_1Simple_1Complex() throws Exception {
        final String drl = "rule \"test_0\"" +
                "when \n" +
                "  Person( name == \"Michael\" )\n" +
                "then \n" +
                "end \n" +
                "rule \"test_1\"" +
                "when \n" +
                "  Person( name == \"Fred\", age == 41 )\n" +
                "then \n" +
                "end \n";

        final GuidedDecisionTree expected = new GuidedDecisionTree();
        expected.setTreeName( "test" );

        final TypeNode type = new TypeNodeImpl( "Person" );
        final ConstraintNode c1 = new ConstraintNodeImpl( "Person",
                                                          "name",
                                                          "==",
                                                          new StringValue( "Michael" ) );
        final ConstraintNode c2 = new ConstraintNodeImpl( "Person",
                                                          "name",
                                                          "==",
                                                          new StringValue( "Fred" ) );
        final ConstraintNode c3 = new ConstraintNodeImpl( "Person",
                                                          "age",
                                                          "==",
                                                          new IntegerValue( 41 ) );
        expected.setRoot( type );
        type.addChild( c1 );
        type.addChild( c2 );
        c2.addChild( c3 );

        addModelField( "Person",
                       "this",
                       "Person",
                       DataType.TYPE_THIS );
        addModelField( "Person",
                       "name",
                       String.class.getName(),
                       DataType.TYPE_STRING );
        addModelField( "Person",
                       "age",
                       Integer.class.getName(),
                       DataType.TYPE_NUMERIC_INTEGER );

        final GuidedDecisionTree model = getAndTestUnmarshalledModel( drl, "test", 0 );
        assertEquals( expected.getTreeName(),
                      model.getTreeName() );

        assertNotNull( model.getRoot() );
        final TypeNode _t1 = model.getRoot();

        assertEquals( type.getClassName(),
                      _t1.getClassName() );
        assertFalse( _t1.isBound() );

        assertEquals( 2,
                      _t1.getChildren().size() );
        assertNotNull( _t1.getChildren().get( 0 ) );
        assertTrue( _t1.getChildren().get( 0 ) instanceof ConstraintNode );

        final ConstraintNode _c1 = (ConstraintNode) _t1.getChildren().get( 0 );

        assertEquals( c1.getClassName(),
                      _c1.getClassName() );
        assertEquals( c1.getFieldName(),
                      _c1.getFieldName() );
        assertEquals( c1.getOperator(),
                      _c1.getOperator() );
        assertEquals( c1.getValue().getValue().toString(),
                      _c1.getValue().getValue().toString() );

        assertNotNull( _t1.getChildren().get( 1 ) );
        assertTrue( _t1.getChildren().get( 1 ) instanceof ConstraintNode );

        final ConstraintNode _c2 = (ConstraintNode) _t1.getChildren().get( 1 );

        assertEquals( c2.getClassName(),
                      _c2.getClassName() );
        assertEquals( c2.getFieldName(),
                      _c2.getFieldName() );
        assertEquals( c2.getOperator(),
                      _c2.getOperator() );
        assertEquals( c2.getValue().getValue().toString(),
                      _c2.getValue().getValue().toString() );

        assertEquals( 0,
                      _c1.getChildren().size() );

        assertEquals( 1,
                      _c2.getChildren().size() );
        assertNotNull( _c2.getChildren().get( 0 ) );
        assertTrue( _c2.getChildren().get( 0 ) instanceof ConstraintNode );

        final ConstraintNode _c3 = (ConstraintNode) _c2.getChildren().get( 0 );

        assertEquals( c3.getClassName(),
                      _c3.getClassName() );
        assertEquals( c3.getFieldName(),
                      _c3.getFieldName() );
        assertEquals( c3.getOperator(),
                      _c3.getOperator() );
        assertEquals( c3.getValue().getValue().toString(),
                      _c3.getValue().getValue().toString() );

        assertEquals( 0,
                      _c3.getChildren().size() );
    }

    @Test
    public void testSingleRule_MultiplePatterns() throws Exception {
        final String drl = "rule \"test_0\"" +
                "when \n" +
                "  Person( name == \"Michael\" )\n" +
                "  Address( country == \"England\" )\n" +
                "then \n" +
                "end";

        final GuidedDecisionTree expected = new GuidedDecisionTree();
        expected.setTreeName( "test" );

        final TypeNode type1 = new TypeNodeImpl( "Person" );
        final ConstraintNode c1 = new ConstraintNodeImpl( "Person",
                                                          "name",
                                                          "==",
                                                          new StringValue( "Michael" ) );
        final TypeNode type2 = new TypeNodeImpl( "Address" );
        final ConstraintNode c2 = new ConstraintNodeImpl( "Address",
                                                          "country",
                                                          "==",
                                                          new StringValue( "England" ) );
        expected.setRoot( type1 );
        type1.addChild( c1 );
        c1.addChild( type2 );
        type2.addChild( c2 );

        addModelField( "Person",
                       "this",
                       "Person",
                       DataType.TYPE_THIS );
        addModelField( "Person",
                       "name",
                       String.class.getName(),
                       DataType.TYPE_STRING );
        addModelField( "Address",
                       "this",
                       "Address",
                       DataType.TYPE_THIS );
        addModelField( "Address",
                       "country",
                       String.class.getName(),
                       DataType.TYPE_STRING );

        final GuidedDecisionTree model = getAndTestUnmarshalledModel( drl, "test", 0 );
        assertEquals( expected.getTreeName(),
                      model.getTreeName() );

        assertNotNull( model.getRoot() );
        final TypeNode _t1 = model.getRoot();

        assertEquals( type1.getClassName(),
                      _t1.getClassName() );
        assertFalse( _t1.isBound() );

        assertEquals( 1,
                      _t1.getChildren().size() );
        assertNotNull( _t1.getChildren().get( 0 ) );
        assertTrue( _t1.getChildren().get( 0 ) instanceof ConstraintNode );

        final ConstraintNode _c1 = (ConstraintNode) _t1.getChildren().get( 0 );

        assertEquals( c1.getClassName(),
                      _c1.getClassName() );
        assertEquals( c1.getFieldName(),
                      _c1.getFieldName() );
        assertEquals( c1.getOperator(),
                      _c1.getOperator() );
        assertEquals( c1.getValue().getValue().toString(),
                      _c1.getValue().getValue().toString() );

        assertEquals( 1,
                      _c1.getChildren().size() );
        assertNotNull( _c1.getChildren().get( 0 ) );
        assertTrue( _c1.getChildren().get( 0 ) instanceof TypeNode );

        final TypeNode _t2 = (TypeNode) _c1.getChildren().get( 0 );

        assertEquals( type2.getClassName(),
                      _t2.getClassName() );
        assertFalse( _t2.isBound() );

        assertEquals( 1,
                      _t2.getChildren().size() );
        assertNotNull( _t2.getChildren().get( 0 ) );
        assertTrue( _t2.getChildren().get( 0 ) instanceof ConstraintNode );

        final ConstraintNode _c2 = (ConstraintNode) _t2.getChildren().get( 0 );

        assertEquals( c2.getClassName(),
                      _c2.getClassName() );
        assertEquals( c2.getFieldName(),
                      _c2.getFieldName() );
        assertEquals( c2.getOperator(),
                      _c2.getOperator() );
        assertEquals( c2.getValue().getValue().toString(),
                      _c2.getValue().getValue().toString() );

        assertEquals( 0,
                      _c2.getChildren().size() );
    }

    @Test
    public void testMultipleRules_MultiplePatterns() throws Exception {
        final String drl = "rule \"test_0\"" +
                "when \n" +
                "  Person( name == \"Michael\" )\n" +
                "  Address( country == \"England\" )\n" +
                "then \n" +
                "end \n" +
                "rule \"test_1\"" +
                "when \n" +
                "  Person( name == \"Michael\" )\n" +
                "  Address( country == \"Norway\" )\n" +
                "then \n" +
                "end \n" +
                "rule \"test_2\"" +
                "when \n" +
                "  Person( name == \"Fred\" )\n" +
                "then \n" +
                "end \n";

        final GuidedDecisionTree expected = new GuidedDecisionTree();
        expected.setTreeName( "test" );

        final TypeNode type1 = new TypeNodeImpl( "Person" );
        final ConstraintNode c1a = new ConstraintNodeImpl( "Person",
                                                           "name",
                                                           "==",
                                                           new StringValue( "Michael" ) );
        final ConstraintNode c1b = new ConstraintNodeImpl( "Person",
                                                           "name",
                                                           "==",
                                                           new StringValue( "Fred" ) );
        final TypeNode type2 = new TypeNodeImpl( "Address" );
        final ConstraintNode c2a = new ConstraintNodeImpl( "Address",
                                                           "country",
                                                           "==",
                                                           new StringValue( "England" ) );
        final ConstraintNode c2b = new ConstraintNodeImpl( "Address",
                                                           "country",
                                                           "==",
                                                           new StringValue( "Norway" ) );
        expected.setRoot( type1 );
        type1.addChild( c1a );
        type1.addChild( c1b );
        c1a.addChild( type2 );
        type2.addChild( c2a );
        type2.addChild( c2b );

        addModelField( "Person",
                       "this",
                       "Person",
                       DataType.TYPE_THIS );
        addModelField( "Person",
                       "name",
                       String.class.getName(),
                       DataType.TYPE_STRING );
        addModelField( "Address",
                       "this",
                       "Address",
                       DataType.TYPE_THIS );
        addModelField( "Address",
                       "country",
                       String.class.getName(),
                       DataType.TYPE_STRING );

        final GuidedDecisionTree model = getAndTestUnmarshalledModel( drl, "test", 0 );
        assertEquals( expected.getTreeName(),
                      model.getTreeName() );

        assertNotNull( model.getRoot() );
        final TypeNode _t1 = model.getRoot();

        assertEquals( type1.getClassName(),
                      _t1.getClassName() );
        assertFalse( _t1.isBound() );

        assertEquals( 2,
                      _t1.getChildren().size() );
        assertNotNull( _t1.getChildren().get( 0 ) );
        assertTrue( _t1.getChildren().get( 0 ) instanceof ConstraintNode );

        final ConstraintNode _c1a = (ConstraintNode) _t1.getChildren().get( 0 );

        assertEquals( c1a.getClassName(),
                      _c1a.getClassName() );
        assertEquals( c1a.getFieldName(),
                      _c1a.getFieldName() );
        assertEquals( c1a.getOperator(),
                      _c1a.getOperator() );
        assertEquals( c1a.getValue().getValue().toString(),
                      _c1a.getValue().getValue().toString() );

        assertNotNull( _t1.getChildren().get( 1 ) );
        assertTrue( _t1.getChildren().get( 1 ) instanceof ConstraintNode );

        final ConstraintNode _c1b = (ConstraintNode) _t1.getChildren().get( 1 );

        assertEquals( c1b.getClassName(),
                      _c1b.getClassName() );
        assertEquals( c1b.getFieldName(),
                      _c1b.getFieldName() );
        assertEquals( c1b.getOperator(),
                      _c1b.getOperator() );
        assertEquals( c1b.getValue().getValue().toString(),
                      _c1b.getValue().getValue().toString() );

        assertEquals( 1,
                      _c1a.getChildren().size() );
        assertNotNull( _c1a.getChildren().get( 0 ) );
        assertTrue( _c1a.getChildren().get( 0 ) instanceof TypeNode );

        final TypeNode _t2 = (TypeNode) _c1a.getChildren().get( 0 );

        assertEquals( type2.getClassName(),
                      _t2.getClassName() );
        assertFalse( _t2.isBound() );

        assertEquals( 2,
                      _t2.getChildren().size() );
        assertNotNull( _t2.getChildren().get( 0 ) );
        assertTrue( _t2.getChildren().get( 0 ) instanceof ConstraintNode );

        final ConstraintNode _c2a = (ConstraintNode) _t2.getChildren().get( 0 );

        assertEquals( c2a.getClassName(),
                      _c2a.getClassName() );
        assertEquals( c2a.getFieldName(),
                      _c2a.getFieldName() );
        assertEquals( c2a.getOperator(),
                      _c2a.getOperator() );
        assertEquals( c2a.getValue().getValue().toString(),
                      _c2a.getValue().getValue().toString() );

        assertNotNull( _t2.getChildren().get( 1 ) );
        assertTrue( _t2.getChildren().get( 1 ) instanceof ConstraintNode );

        final ConstraintNode _c2b = (ConstraintNode) _t2.getChildren().get( 1 );

        assertEquals( c2b.getClassName(),
                      _c2b.getClassName() );
        assertEquals( c2b.getFieldName(),
                      _c2b.getFieldName() );
        assertEquals( c2b.getOperator(),
                      _c2b.getOperator() );
        assertEquals( c2b.getValue().getValue().toString(),
                      _c2b.getValue().getValue().toString() );

        assertEquals( 0,
                      _c2a.getChildren().size() );
        assertEquals( 0,
                      _c2b.getChildren().size() );
    }

    @Test
    public void testValue_BigDecimal() throws Exception {
        final String drl = "rule \"test_0\"" +
                "when \n" +
                "  Person( bigDecimalField == 1000000B )\n" +
                "then \n" +
                "end";

        final GuidedDecisionTree expected = new GuidedDecisionTree();
        expected.setTreeName( "test" );

        final TypeNode type = new TypeNodeImpl( "Person" );
        final ConstraintNode c1 = new ConstraintNodeImpl( "Person",
                                                          "bigDecimalField",
                                                          "==",
                                                          new BigDecimalValue( new BigDecimal( 1000000 ) ) );
        expected.setRoot( type );
        type.addChild( c1 );

        addModelField( "Person",
                       "this",
                       "Person",
                       DataType.TYPE_THIS );
        addModelField( "Person",
                       "bigDecimalField",
                       BigDecimal.class.getName(),
                       DataType.TYPE_NUMERIC_BIGDECIMAL );

        final GuidedDecisionTree model = getAndTestUnmarshalledModel( drl, "test", 0 );
        assertEquals( expected.getTreeName(),
                      model.getTreeName() );

        assertNotNull( model.getRoot() );
        assertEquals( type.getClassName(),
                      model.getRoot().getClassName() );
        assertFalse( model.getRoot().isBound() );

        assertEquals( 1,
                      model.getRoot().getChildren().size() );
        assertNotNull( model.getRoot().getChildren().get( 0 ) );
        assertTrue( model.getRoot().getChildren().get( 0 ) instanceof ConstraintNode );

        final ConstraintNode _c1 = (ConstraintNode) model.getRoot().getChildren().get( 0 );

        assertEquals( c1.getClassName(),
                      _c1.getClassName() );
        assertEquals( c1.getFieldName(),
                      _c1.getFieldName() );
        assertEquals( c1.getOperator(),
                      _c1.getOperator() );
        assertEquals( c1.getValue().getValue().toString(),
                      _c1.getValue().getValue().toString() );
    }

    @Test
    public void testValue_BigInteger() throws Exception {
        final String drl = "rule \"test_0\"" +
                "when \n" +
                "  Person( bigIntegerField == 1000000I )\n" +
                "then \n" +
                "end";

        final GuidedDecisionTree expected = new GuidedDecisionTree();
        expected.setTreeName( "test" );

        final TypeNode type = new TypeNodeImpl( "Person" );
        final ConstraintNode c1 = new ConstraintNodeImpl( "Person",
                                                          "bigIntegerField",
                                                          "==",
                                                          new BigIntegerValue( new BigInteger( "1000000" ) ) );
        expected.setRoot( type );
        type.addChild( c1 );

        addModelField( "Person",
                       "this",
                       "Person",
                       DataType.TYPE_THIS );
        addModelField( "Person",
                       "bigIntegerField",
                       BigInteger.class.getName(),
                       DataType.TYPE_NUMERIC_BIGINTEGER );

        final GuidedDecisionTree model = getAndTestUnmarshalledModel( drl, "test", 0 );
        assertEquals( expected.getTreeName(),
                      model.getTreeName() );

        assertNotNull( model.getRoot() );
        assertEquals( type.getClassName(),
                      model.getRoot().getClassName() );
        assertFalse( model.getRoot().isBound() );

        assertEquals( 1,
                      model.getRoot().getChildren().size() );
        assertNotNull( model.getRoot().getChildren().get( 0 ) );
        assertTrue( model.getRoot().getChildren().get( 0 ) instanceof ConstraintNode );

        final ConstraintNode _c1 = (ConstraintNode) model.getRoot().getChildren().get( 0 );

        assertEquals( c1.getClassName(),
                      _c1.getClassName() );
        assertEquals( c1.getFieldName(),
                      _c1.getFieldName() );
        assertEquals( c1.getOperator(),
                      _c1.getOperator() );
        assertEquals( c1.getValue().getValue().toString(),
                      _c1.getValue().getValue().toString() );
    }

    @Test
    public void testValue_Boolean() throws Exception {
        final String drl = "rule \"test_0\"" +
                "when \n" +
                "  Person( booleanField == true )\n" +
                "then \n" +
                "end";

        final GuidedDecisionTree expected = new GuidedDecisionTree();
        expected.setTreeName( "test" );

        final TypeNode type = new TypeNodeImpl( "Person" );
        final ConstraintNode c1 = new ConstraintNodeImpl( "Person",
                                                          "booleanField",
                                                          "==",
                                                          new BooleanValue( true ) );
        expected.setRoot( type );
        type.addChild( c1 );

        addModelField( "Person",
                       "this",
                       "Person",
                       DataType.TYPE_THIS );
        addModelField( "Person",
                       "booleanField",
                       Boolean.class.getName(),
                       DataType.TYPE_BOOLEAN );

        final GuidedDecisionTree model = getAndTestUnmarshalledModel( drl, "test", 0 );
        assertEquals( expected.getTreeName(),
                      model.getTreeName() );

        assertNotNull( model.getRoot() );
        assertEquals( type.getClassName(),
                      model.getRoot().getClassName() );
        assertFalse( model.getRoot().isBound() );

        assertEquals( 1,
                      model.getRoot().getChildren().size() );
        assertNotNull( model.getRoot().getChildren().get( 0 ) );
        assertTrue( model.getRoot().getChildren().get( 0 ) instanceof ConstraintNode );

        final ConstraintNode _c1 = (ConstraintNode) model.getRoot().getChildren().get( 0 );

        assertEquals( c1.getClassName(),
                      _c1.getClassName() );
        assertEquals( c1.getFieldName(),
                      _c1.getFieldName() );
        assertEquals( c1.getOperator(),
                      _c1.getOperator() );
        assertEquals( c1.getValue().getValue().toString(),
                      _c1.getValue().getValue().toString() );
    }

    @Test
    public void testValue_Byte() throws Exception {
        final String drl = "rule \"test_0\"" +
                "when \n" +
                "  Person( byteField == 100 )\n" +
                "then \n" +
                "end";

        final GuidedDecisionTree expected = new GuidedDecisionTree();
        expected.setTreeName( "test" );

        final TypeNode type = new TypeNodeImpl( "Person" );
        final ConstraintNode c1 = new ConstraintNodeImpl( "Person",
                                                          "byteField",
                                                          "==",
                                                          new ByteValue( new Byte( "100" ) ) );
        expected.setRoot( type );
        type.addChild( c1 );

        addModelField( "Person",
                       "this",
                       "Person",
                       DataType.TYPE_THIS );
        addModelField( "Person",
                       "byteField",
                       Byte.class.getName(),
                       DataType.TYPE_NUMERIC_BYTE );

        final GuidedDecisionTree model = getAndTestUnmarshalledModel( drl, "test", 0 );
        assertEquals( expected.getTreeName(),
                      model.getTreeName() );

        assertNotNull( model.getRoot() );
        assertEquals( type.getClassName(),
                      model.getRoot().getClassName() );
        assertFalse( model.getRoot().isBound() );

        assertEquals( 1,
                      model.getRoot().getChildren().size() );
        assertNotNull( model.getRoot().getChildren().get( 0 ) );
        assertTrue( model.getRoot().getChildren().get( 0 ) instanceof ConstraintNode );

        final ConstraintNode _c1 = (ConstraintNode) model.getRoot().getChildren().get( 0 );

        assertEquals( c1.getClassName(),
                      _c1.getClassName() );
        assertEquals( c1.getFieldName(),
                      _c1.getFieldName() );
        assertEquals( c1.getOperator(),
                      _c1.getOperator() );
        assertEquals( c1.getValue().getValue().toString(),
                      _c1.getValue().getValue().toString() );
    }

    @Test
    public void testValue_Date() throws Exception {
        final String drl = "rule \"test_0\"" +
                "when \n" +
                "  Person( dateField == \"15-Jul-1984\" )\n" +
                "then \n" +
                "end";

        final GuidedDecisionTree expected = new GuidedDecisionTree();
        expected.setTreeName( "test" );

        final TypeNode type = new TypeNodeImpl( "Person" );
        final ConstraintNode c1 = new ConstraintNodeImpl( "Person",
                                                          "dateField",
                                                          "==",
                                                          new DateValue( new Date( 84, 6, 15 ) ) );
        expected.setRoot( type );
        type.addChild( c1 );

        addModelField( "Person",
                       "this",
                       "Person",
                       DataType.TYPE_THIS );
        addModelField( "Person",
                       "dateField",
                       Date.class.getName(),
                       DataType.TYPE_DATE );

        final GuidedDecisionTree model = getAndTestUnmarshalledModel( drl, "test", 0 );
        assertEquals( expected.getTreeName(),
                      model.getTreeName() );

        assertNotNull( model.getRoot() );
        assertEquals( type.getClassName(),
                      model.getRoot().getClassName() );
        assertFalse( model.getRoot().isBound() );

        assertEquals( 1,
                      model.getRoot().getChildren().size() );
        assertNotNull( model.getRoot().getChildren().get( 0 ) );
        assertTrue( model.getRoot().getChildren().get( 0 ) instanceof ConstraintNode );

        final ConstraintNode _c1 = (ConstraintNode) model.getRoot().getChildren().get( 0 );

        assertEquals( c1.getClassName(),
                      _c1.getClassName() );
        assertEquals( c1.getFieldName(),
                      _c1.getFieldName() );
        assertEquals( c1.getOperator(),
                      _c1.getOperator() );
        assertEquals( c1.getValue().getValue().toString(),
                      _c1.getValue().getValue().toString() );
    }

    @Test
    public void testValue_Double() throws Exception {
        final String drl = "rule \"test_0\"" +
                "when \n" +
                "  Person( doubleField == 1000.56 )\n" +
                "then \n" +
                "end";

        final GuidedDecisionTree expected = new GuidedDecisionTree();
        expected.setTreeName( "test" );

        final TypeNode type = new TypeNodeImpl( "Person" );
        final ConstraintNode c1 = new ConstraintNodeImpl( "Person",
                                                          "doubleField",
                                                          "==",
                                                          new DoubleValue( 1000.56 ) );
        expected.setRoot( type );
        type.addChild( c1 );

        addModelField( "Person",
                       "this",
                       "Person",
                       DataType.TYPE_THIS );
        addModelField( "Person",
                       "doubleField",
                       Double.class.getName(),
                       DataType.TYPE_NUMERIC_DOUBLE );

        final GuidedDecisionTree model = getAndTestUnmarshalledModel( drl, "test", 0 );
        assertEquals( expected.getTreeName(),
                      model.getTreeName() );

        assertNotNull( model.getRoot() );
        assertEquals( type.getClassName(),
                      model.getRoot().getClassName() );
        assertFalse( model.getRoot().isBound() );

        assertEquals( 1,
                      model.getRoot().getChildren().size() );
        assertNotNull( model.getRoot().getChildren().get( 0 ) );
        assertTrue( model.getRoot().getChildren().get( 0 ) instanceof ConstraintNode );

        final ConstraintNode _c1 = (ConstraintNode) model.getRoot().getChildren().get( 0 );

        assertEquals( c1.getClassName(),
                      _c1.getClassName() );
        assertEquals( c1.getFieldName(),
                      _c1.getFieldName() );
        assertEquals( c1.getOperator(),
                      _c1.getOperator() );
        assertEquals( c1.getValue().getValue().toString(),
                      _c1.getValue().getValue().toString() );
    }

    @Test
    public void testValue_Float() throws Exception {
        final String drl = "rule \"test_0\"" +
                "when \n" +
                "  Person( floatField == 1000.56 )\n" +
                "then \n" +
                "end";

        final GuidedDecisionTree expected = new GuidedDecisionTree();
        expected.setTreeName( "test" );

        final TypeNode type = new TypeNodeImpl( "Person" );
        final ConstraintNode c1 = new ConstraintNodeImpl( "Person",
                                                          "floatField",
                                                          "==",
                                                          new FloatValue( 1000.56f ) );
        expected.setRoot( type );
        type.addChild( c1 );

        addModelField( "Person",
                       "this",
                       "Person",
                       DataType.TYPE_THIS );
        addModelField( "Person",
                       "floatField",
                       Float.class.getName(),
                       DataType.TYPE_NUMERIC_FLOAT );

        final GuidedDecisionTree model = getAndTestUnmarshalledModel( drl, "test", 0 );
        assertEquals( expected.getTreeName(),
                      model.getTreeName() );

        assertNotNull( model.getRoot() );
        assertEquals( type.getClassName(),
                      model.getRoot().getClassName() );
        assertFalse( model.getRoot().isBound() );

        assertEquals( 1,
                      model.getRoot().getChildren().size() );
        assertNotNull( model.getRoot().getChildren().get( 0 ) );
        assertTrue( model.getRoot().getChildren().get( 0 ) instanceof ConstraintNode );

        final ConstraintNode _c1 = (ConstraintNode) model.getRoot().getChildren().get( 0 );

        assertEquals( c1.getClassName(),
                      _c1.getClassName() );
        assertEquals( c1.getFieldName(),
                      _c1.getFieldName() );
        assertEquals( c1.getOperator(),
                      _c1.getOperator() );
        assertEquals( c1.getValue().getValue().toString(),
                      _c1.getValue().getValue().toString() );
    }

    @Test
    public void testValue_Integer() throws Exception {
        final String drl = "rule \"test_0\"" +
                "when \n" +
                "  Person( integerField == 1000000 )\n" +
                "then \n" +
                "end";

        final GuidedDecisionTree expected = new GuidedDecisionTree();
        expected.setTreeName( "test" );

        final TypeNode type = new TypeNodeImpl( "Person" );
        final ConstraintNode c1 = new ConstraintNodeImpl( "Person",
                                                          "integerField",
                                                          "==",
                                                          new IntegerValue( 1000000 ) );
        expected.setRoot( type );
        type.addChild( c1 );

        addModelField( "Person",
                       "this",
                       "Person",
                       DataType.TYPE_THIS );
        addModelField( "Person",
                       "integerField",
                       Integer.class.getName(),
                       DataType.TYPE_NUMERIC_INTEGER );

        final GuidedDecisionTree model = getAndTestUnmarshalledModel( drl, "test", 0 );
        assertEquals( expected.getTreeName(),
                      model.getTreeName() );

        assertNotNull( model.getRoot() );
        assertEquals( type.getClassName(),
                      model.getRoot().getClassName() );
        assertFalse( model.getRoot().isBound() );

        assertEquals( 1,
                      model.getRoot().getChildren().size() );
        assertNotNull( model.getRoot().getChildren().get( 0 ) );
        assertTrue( model.getRoot().getChildren().get( 0 ) instanceof ConstraintNode );

        final ConstraintNode _c1 = (ConstraintNode) model.getRoot().getChildren().get( 0 );

        assertEquals( c1.getClassName(),
                      _c1.getClassName() );
        assertEquals( c1.getFieldName(),
                      _c1.getFieldName() );
        assertEquals( c1.getOperator(),
                      _c1.getOperator() );
        assertEquals( c1.getValue().getValue().toString(),
                      _c1.getValue().getValue().toString() );
    }

    @Test
    public void testValue_Long() throws Exception {
        final String drl = "rule \"test_0\"" +
                "when \n" +
                "  Person( longField == 1000000 )\n" +
                "then \n" +
                "end";

        final GuidedDecisionTree expected = new GuidedDecisionTree();
        expected.setTreeName( "test" );

        final TypeNode type = new TypeNodeImpl( "Person" );
        final ConstraintNode c1 = new ConstraintNodeImpl( "Person",
                                                          "longField",
                                                          "==",
                                                          new LongValue( 1000000L ) );
        expected.setRoot( type );
        type.addChild( c1 );

        addModelField( "Person",
                       "this",
                       "Person",
                       DataType.TYPE_THIS );
        addModelField( "Person",
                       "longField",
                       Long.class.getName(),
                       DataType.TYPE_NUMERIC_LONG );

        final GuidedDecisionTree model = getAndTestUnmarshalledModel( drl, "test", 0 );
        assertEquals( expected.getTreeName(),
                      model.getTreeName() );

        assertNotNull( model.getRoot() );
        assertEquals( type.getClassName(),
                      model.getRoot().getClassName() );
        assertFalse( model.getRoot().isBound() );

        assertEquals( 1,
                      model.getRoot().getChildren().size() );
        assertNotNull( model.getRoot().getChildren().get( 0 ) );
        assertTrue( model.getRoot().getChildren().get( 0 ) instanceof ConstraintNode );

        final ConstraintNode _c1 = (ConstraintNode) model.getRoot().getChildren().get( 0 );

        assertEquals( c1.getClassName(),
                      _c1.getClassName() );
        assertEquals( c1.getFieldName(),
                      _c1.getFieldName() );
        assertEquals( c1.getOperator(),
                      _c1.getOperator() );
        assertEquals( c1.getValue().getValue().toString(),
                      _c1.getValue().getValue().toString() );
    }

    @Test
    public void testValue_Short() throws Exception {
        final String drl = "rule \"test_0\"" +
                "when \n" +
                "  Person( shortField == 1000 )\n" +
                "then \n" +
                "end";

        final GuidedDecisionTree expected = new GuidedDecisionTree();
        expected.setTreeName( "test" );

        final TypeNode type = new TypeNodeImpl( "Person" );
        final ConstraintNode c1 = new ConstraintNodeImpl( "Person",
                                                          "shortField",
                                                          "==",
                                                          new ShortValue( new Short( "1000" ) ) );
        expected.setRoot( type );
        type.addChild( c1 );

        addModelField( "Person",
                       "this",
                       "Person",
                       DataType.TYPE_THIS );
        addModelField( "Person",
                       "shortField",
                       Short.class.getName(),
                       DataType.TYPE_NUMERIC_SHORT );

        final GuidedDecisionTree model = getAndTestUnmarshalledModel( drl, "test", 0 );
        assertEquals( expected.getTreeName(),
                      model.getTreeName() );

        assertNotNull( model.getRoot() );
        assertEquals( type.getClassName(),
                      model.getRoot().getClassName() );
        assertFalse( model.getRoot().isBound() );

        assertEquals( 1,
                      model.getRoot().getChildren().size() );
        assertNotNull( model.getRoot().getChildren().get( 0 ) );
        assertTrue( model.getRoot().getChildren().get( 0 ) instanceof ConstraintNode );

        final ConstraintNode _c1 = (ConstraintNode) model.getRoot().getChildren().get( 0 );

        assertEquals( c1.getClassName(),
                      _c1.getClassName() );
        assertEquals( c1.getFieldName(),
                      _c1.getFieldName() );
        assertEquals( c1.getOperator(),
                      _c1.getOperator() );
        assertEquals( c1.getValue().getValue().toString(),
                      _c1.getValue().getValue().toString() );
    }

    @Test
    public void testValue_String() throws Exception {
        final String drl = "rule \"test_0\"" +
                "when \n" +
                "  Person( stringField == \"Michael\" )\n" +
                "then \n" +
                "end";

        final GuidedDecisionTree expected = new GuidedDecisionTree();
        expected.setTreeName( "test" );

        final TypeNode type = new TypeNodeImpl( "Person" );
        final ConstraintNode c1 = new ConstraintNodeImpl( "Person",
                                                          "stringField",
                                                          "==",
                                                          new StringValue( "Michael" ) );
        expected.setRoot( type );
        type.addChild( c1 );

        addModelField( "Person",
                       "this",
                       "Person",
                       DataType.TYPE_THIS );
        addModelField( "Person",
                       "stringField",
                       String.class.getName(),
                       DataType.TYPE_STRING );

        final GuidedDecisionTree model = getAndTestUnmarshalledModel( drl, "test", 0 );
        assertEquals( expected.getTreeName(),
                      model.getTreeName() );

        assertNotNull( model.getRoot() );
        assertEquals( type.getClassName(),
                      model.getRoot().getClassName() );
        assertFalse( model.getRoot().isBound() );

        assertEquals( 1,
                      model.getRoot().getChildren().size() );
        assertNotNull( model.getRoot().getChildren().get( 0 ) );
        assertTrue( model.getRoot().getChildren().get( 0 ) instanceof ConstraintNode );

        final ConstraintNode _c1 = (ConstraintNode) model.getRoot().getChildren().get( 0 );

        assertEquals( c1.getClassName(),
                      _c1.getClassName() );
        assertEquals( c1.getFieldName(),
                      _c1.getFieldName() );
        assertEquals( c1.getOperator(),
                      _c1.getOperator() );
        assertEquals( c1.getValue().getValue().toString(),
                      _c1.getValue().getValue().toString() );
    }

    @Test
    public void testSingleRule_TypeBinding() throws Exception {
        final String drl = "rule \"test_0\"" +
                "when \n" +
                "  $p : Person( )\n" +
                "then \n" +
                "end";

        final GuidedDecisionTree expected = new GuidedDecisionTree();
        expected.setTreeName( "test" );

        final TypeNode type = new TypeNodeImpl( "Person" );
        type.setBinding( "$p" );
        expected.setRoot( type );

        addModelField( "Person",
                       "this",
                       "Person",
                       DataType.TYPE_THIS );

        final GuidedDecisionTree model = getAndTestUnmarshalledModel( drl, "test", 0 );
        assertEquals( expected.getTreeName(),
                      model.getTreeName() );

        assertNotNull( model.getRoot() );
        assertEquals( type.getClassName(),
                      model.getRoot().getClassName() );
        assertTrue( model.getRoot().isBound() );
        assertEquals( type.getBinding(),
                      model.getRoot().getBinding() );

        assertEquals( 0,
                      model.getRoot().getChildren().size() );
    }

    @Test
    public void testSingleRule_FieldBinding() throws Exception {
        final String drl = "rule \"test_0\"" +
                "when \n" +
                "  Person( $n : name )\n" +
                "then \n" +
                "end";

        final GuidedDecisionTree expected = new GuidedDecisionTree();
        expected.setTreeName( "test" );

        final TypeNode type = new TypeNodeImpl( "Person" );
        final ConstraintNode c1 = new ConstraintNodeImpl( "Person",
                                                          "name" );
        c1.setBinding( "$n" );
        expected.setRoot( type );
        type.addChild( c1 );

        addModelField( "Person",
                       "this",
                       "Person",
                       DataType.TYPE_THIS );
        addModelField( "Person",
                       "name",
                       String.class.getName(),
                       DataType.TYPE_STRING );

        final GuidedDecisionTree model = getAndTestUnmarshalledModel( drl, "test", 0 );
        assertEquals( expected.getTreeName(),
                      model.getTreeName() );

        assertNotNull( model.getRoot() );
        assertEquals( type.getClassName(),
                      model.getRoot().getClassName() );
        assertFalse( model.getRoot().isBound() );

        assertEquals( 1,
                      model.getRoot().getChildren().size() );
        assertNotNull( model.getRoot().getChildren().get( 0 ) );
        assertTrue( model.getRoot().getChildren().get( 0 ) instanceof ConstraintNode );

        final ConstraintNode _c1 = (ConstraintNode) model.getRoot().getChildren().get( 0 );

        assertEquals( c1.getClassName(),
                      _c1.getClassName() );
        assertEquals( c1.getFieldName(),
                      _c1.getFieldName() );
        assertNull( _c1.getOperator() );
        assertNull( _c1.getValue() );
        assertTrue( _c1.isBound() );
        assertEquals( c1.getBinding(),
                      _c1.getBinding() );
    }

    @Test
    public void testSingleRule_SingleConstraintJavaEnum() throws Exception {
        final String drl = "rule \"test_0\"" +
                "when \n" +
                "  Person( name == Names.FRED )\n" +
                "then \n" +
                "end";

        final GuidedDecisionTree expected = new GuidedDecisionTree();
        expected.setTreeName( "test" );

        final TypeNode type = new TypeNodeImpl( "Person" );
        final ConstraintNode c1 = new ConstraintNodeImpl( "Person",
                                                          "name",
                                                          "==",
                                                          new EnumValue( "Names.FRED" ) );
        expected.setRoot( type );
        type.addChild( c1 );

        addModelField( "Person",
                       "this",
                       "Person",
                       DataType.TYPE_THIS );
        addModelField( "Person",
                       "name",
                       String.class.getName(),
                       DataType.TYPE_COMPARABLE );

        addJavaEnumDefinition( "Person",
                               "name",
                               new String[]{ "Names.FRED=Names.FRED" } );

        final GuidedDecisionTree model = getAndTestUnmarshalledModel( drl, "test", 0 );
        assertEquals( expected.getTreeName(),
                      model.getTreeName() );

        assertNotNull( model.getRoot() );
        assertEquals( type.getClassName(),
                      model.getRoot().getClassName() );
        assertFalse( model.getRoot().isBound() );

        assertEquals( 1,
                      model.getRoot().getChildren().size() );
        assertNotNull( model.getRoot().getChildren().get( 0 ) );
        assertTrue( model.getRoot().getChildren().get( 0 ) instanceof ConstraintNode );

        final ConstraintNode _c1 = (ConstraintNode) model.getRoot().getChildren().get( 0 );

        assertEquals( c1.getClassName(),
                      _c1.getClassName() );
        assertEquals( c1.getFieldName(),
                      _c1.getFieldName() );
        assertEquals( c1.getOperator(),
                      _c1.getOperator() );
        assertNotNull( _c1.getValue() );
        assertTrue( _c1.getValue() instanceof EnumValue );
        assertEquals( c1.getValue().getValue().toString(),
                      _c1.getValue().getValue().toString() );
    }

    @Test
    public void testSingleRule_SingleConstraintNotNullOperator() throws Exception {
        final String drl = "rule \"test_0\"" +
                "when \n" +
                "  Person( name != null )\n" +
                "then \n" +
                "end";

        final GuidedDecisionTree expected = new GuidedDecisionTree();
        expected.setTreeName( "test" );

        final TypeNode type = new TypeNodeImpl( "Person" );
        final ConstraintNode c1 = new ConstraintNodeImpl( "Person",
                                                          "name",
                                                          "!= null",
                                                          null );
        expected.setRoot( type );
        type.addChild( c1 );

        addModelField( "Person",
                       "this",
                       "Person",
                       DataType.TYPE_THIS );
        addModelField( "Person",
                       "name",
                       String.class.getName(),
                       DataType.TYPE_STRING );

        final GuidedDecisionTree model = getAndTestUnmarshalledModel( drl, "test", 0 );
        assertEquals( expected.getTreeName(),
                      model.getTreeName() );

        assertNotNull( model.getRoot() );
        assertEquals( type.getClassName(),
                      model.getRoot().getClassName() );
        assertFalse( model.getRoot().isBound() );

        assertEquals( 1,
                      model.getRoot().getChildren().size() );
        assertNotNull( model.getRoot().getChildren().get( 0 ) );
        assertTrue( model.getRoot().getChildren().get( 0 ) instanceof ConstraintNode );

        final ConstraintNode _c1 = (ConstraintNode) model.getRoot().getChildren().get( 0 );

        assertEquals( c1.getClassName(),
                      _c1.getClassName() );
        assertEquals( c1.getFieldName(),
                      _c1.getFieldName() );
        assertEquals( c1.getOperator(),
                      _c1.getOperator() );
        assertNull( _c1.getValue() );
    }

    @Test
    public void testSingleRule_SingleConstraintNullOperator() throws Exception {
        final String drl = "rule \"test_0\"" +
                "when \n" +
                "  Person( name == null )\n" +
                "then \n" +
                "end";

        final GuidedDecisionTree expected = new GuidedDecisionTree();
        expected.setTreeName( "test" );

        final TypeNode type = new TypeNodeImpl( "Person" );
        final ConstraintNode c1 = new ConstraintNodeImpl( "Person",
                                                          "name",
                                                          "== null",
                                                          null );
        expected.setRoot( type );
        type.addChild( c1 );

        addModelField( "Person",
                       "this",
                       "Person",
                       DataType.TYPE_THIS );
        addModelField( "Person",
                       "name",
                       String.class.getName(),
                       DataType.TYPE_STRING );

        final GuidedDecisionTree model = getAndTestUnmarshalledModel( drl, "test", 0 );
        assertEquals( expected.getTreeName(),
                      model.getTreeName() );

        assertNotNull( model.getRoot() );
        assertEquals( type.getClassName(),
                      model.getRoot().getClassName() );
        assertFalse( model.getRoot().isBound() );

        assertEquals( 1,
                      model.getRoot().getChildren().size() );
        assertNotNull( model.getRoot().getChildren().get( 0 ) );
        assertTrue( model.getRoot().getChildren().get( 0 ) instanceof ConstraintNode );

        final ConstraintNode _c1 = (ConstraintNode) model.getRoot().getChildren().get( 0 );

        assertEquals( c1.getClassName(),
                      _c1.getClassName() );
        assertEquals( c1.getFieldName(),
                      _c1.getFieldName() );
        assertEquals( c1.getOperator(),
                      _c1.getOperator() );
        assertNull( _c1.getValue() );
    }

    @Test
    public void testSingleRule_ActionRetract() throws Exception {
        final String drl = "rule \"test_0\"\n" +
                "when \n" +
                "  $p : Person( )\n" +
                "then \n" +
                "  retract( $p );\n" +
                "end";

        final GuidedDecisionTree expected = new GuidedDecisionTree();
        expected.setTreeName( "test" );

        final TypeNode type = new TypeNodeImpl( "Person" );
        type.setBinding( "$p" );
        expected.setRoot( type );

        final ActionRetractNode action = new ActionRetractNodeImpl( type );
        type.addChild( action );

        addModelField( "Person",
                       "this",
                       "Person",
                       DataType.TYPE_THIS );
        addModelField( "Person",
                       "name",
                       String.class.getName(),
                       DataType.TYPE_STRING );

        final GuidedDecisionTree model = getAndTestUnmarshalledModel( drl, "test", 0 );
        assertEquals( expected.getTreeName(),
                      model.getTreeName() );

        assertNotNull( model.getRoot() );
        assertEquals( type.getClassName(),
                      model.getRoot().getClassName() );
        assertTrue( model.getRoot().isBound() );
        assertEquals( type.getBinding(),
                      model.getRoot().getBinding() );

        assertEquals( 1,
                      model.getRoot().getChildren().size() );
        assertNotNull( model.getRoot().getChildren().get( 0 ) );
        assertTrue( model.getRoot().getChildren().get( 0 ) instanceof ActionRetractNode );

        final ActionRetractNode _a1 = (ActionRetractNode) model.getRoot().getChildren().get( 0 );

        assertEquals( action.getBoundNode().getBinding(),
                      _a1.getBoundNode().getBinding() );
    }

    @Test
    public void testSingleRule_ActionRetractWithConstraint() throws Exception {
        final String drl = "rule \"test_0\"\n" +
                "when \n" +
                "  $p : Person( $n : name )\n" +
                "then \n" +
                "  retract( $p );\n" +
                "end";

        final GuidedDecisionTree expected = new GuidedDecisionTree();
        expected.setTreeName( "test" );

        final TypeNode type = new TypeNodeImpl( "Person" );
        type.setBinding( "$p" );
        final ConstraintNode c1 = new ConstraintNodeImpl( "Person",
                                                          "name" );
        c1.setBinding( "$n" );
        expected.setRoot( type );
        type.addChild( c1 );

        final ActionRetractNode action = new ActionRetractNodeImpl( type );
        c1.addChild( action );

        addModelField( "Person",
                       "this",
                       "Person",
                       DataType.TYPE_THIS );
        addModelField( "Person",
                       "name",
                       String.class.getName(),
                       DataType.TYPE_STRING );

        final GuidedDecisionTree model = getAndTestUnmarshalledModel( drl, "test", 0 );
        assertEquals( expected.getTreeName(),
                      model.getTreeName() );

        assertNotNull( model.getRoot() );
        assertEquals( type.getClassName(),
                      model.getRoot().getClassName() );
        assertTrue( model.getRoot().isBound() );
        assertEquals( type.getBinding(),
                      model.getRoot().getBinding() );

        assertEquals( 1,
                      model.getRoot().getChildren().size() );
        assertNotNull( model.getRoot().getChildren().get( 0 ) );
        assertTrue( model.getRoot().getChildren().get( 0 ) instanceof ConstraintNode );

        final ConstraintNode _c1 = (ConstraintNode) model.getRoot().getChildren().get( 0 );

        assertEquals( c1.getClassName(),
                      _c1.getClassName() );
        assertEquals( c1.getFieldName(),
                      _c1.getFieldName() );
        assertEquals( c1.getOperator(),
                      _c1.getOperator() );
        assertNull( _c1.getValue() );

        assertEquals( 1,
                      _c1.getChildren().size() );
        assertNotNull( _c1.getChildren().get( 0 ) );
        assertTrue( _c1.getChildren().get( 0 ) instanceof ActionRetractNode );

        final ActionRetractNode _a1 = (ActionRetractNode) _c1.getChildren().get( 0 );

        assertEquals( action.getBoundNode().getBinding(),
                      _a1.getBoundNode().getBinding() );
    }

    @Test
    public void testSingleRule_ActionModify() throws Exception {
        final String drl = "rule \"test_0\"\n" +
                "when \n" +
                "  $p : Person( )\n" +
                "then \n" +
                "  modify( $p ) {\n" +
                "    setAge( 25 )\n" +
                "  }\n" +
                "end";

        final GuidedDecisionTree expected = new GuidedDecisionTree();
        expected.setTreeName( "test" );

        final TypeNode type = new TypeNodeImpl( "Person" );
        type.setBinding( "$p" );
        expected.setRoot( type );

        final ActionUpdateNode action = new ActionUpdateNodeImpl( type );
        action.setModify( true );
        action.getFieldValues().add( new ActionFieldValueImpl( "age",
                                                               new IntegerValue( 25 ) ) );
        type.addChild( action );

        addModelField( "Person",
                       "this",
                       "Person",
                       DataType.TYPE_THIS );
        addModelField( "Person",
                       "age",
                       Integer.class.getName(),
                       DataType.TYPE_NUMERIC_INTEGER );

        final GuidedDecisionTree model = getAndTestUnmarshalledModel( drl, "test", 0 );
        assertEquals( expected.getTreeName(),
                      model.getTreeName() );

        assertNotNull( model.getRoot() );
        assertEquals( type.getClassName(),
                      model.getRoot().getClassName() );
        assertTrue( model.getRoot().isBound() );
        assertEquals( type.getBinding(),
                      model.getRoot().getBinding() );

        assertEquals( 1,
                      model.getRoot().getChildren().size() );
        assertNotNull( model.getRoot().getChildren().get( 0 ) );
        assertTrue( model.getRoot().getChildren().get( 0 ) instanceof ActionUpdateNode );

        final ActionUpdateNode _action = (ActionUpdateNode) model.getRoot().getChildren().get( 0 );

        assertEquals( action.getBoundNode().getBinding(),
                      _action.getBoundNode().getBinding() );
        assertEquals( action.isModify(),
                      _action.isModify() );
        assertEquals( action.getFieldValues().size(),
                      _action.getFieldValues().size() );
        assertEquals( 1,
                      _action.getFieldValues().size() );
        assertEquals( action.getFieldValues().get( 0 ).getFieldName(),
                      _action.getFieldValues().get( 0 ).getFieldName() );
        assertEquals( action.getFieldValues().get( 0 ).getValue().getValue().toString(),
                      _action.getFieldValues().get( 0 ).getValue().getValue().toString() );

        assertEquals( 0,
                      _action.getChildren().size() );
    }

    @Test
    public void testSingleRule_ActionModifyWithConstraint() throws Exception {
        final String drl = "rule \"test_0\"\n" +
                "when \n" +
                "  $p : Person( $n : name )\n" +
                "then \n" +
                "  modify( $p ) {\n" +
                "    setAge( 25 )\n" +
                "  }\n" +
                "end";

        final GuidedDecisionTree expected = new GuidedDecisionTree();
        expected.setTreeName( "test" );

        final TypeNode type = new TypeNodeImpl( "Person" );
        type.setBinding( "$p" );
        final ConstraintNode c1 = new ConstraintNodeImpl( "Person",
                                                          "name" );
        c1.setBinding( "$n" );
        expected.setRoot( type );
        type.addChild( c1 );

        final ActionUpdateNode action = new ActionUpdateNodeImpl( type );
        action.setModify( true );
        action.getFieldValues().add( new ActionFieldValueImpl( "age",
                                                               new IntegerValue( 25 ) ) );
        c1.addChild( action );

        addModelField( "Person",
                       "this",
                       "Person",
                       DataType.TYPE_THIS );
        addModelField( "Person",
                       "name",
                       String.class.getName(),
                       DataType.TYPE_STRING );
        addModelField( "Person",
                       "age",
                       Integer.class.getName(),
                       DataType.TYPE_NUMERIC_INTEGER );

        final GuidedDecisionTree model = getAndTestUnmarshalledModel( drl, "test", 0 );
        assertEquals( expected.getTreeName(),
                      model.getTreeName() );

        assertNotNull( model.getRoot() );
        assertEquals( type.getClassName(),
                      model.getRoot().getClassName() );
        assertTrue( model.getRoot().isBound() );
        assertEquals( type.getBinding(),
                      model.getRoot().getBinding() );

        assertEquals( 1,
                      model.getRoot().getChildren().size() );
        assertNotNull( model.getRoot().getChildren().get( 0 ) );
        assertTrue( model.getRoot().getChildren().get( 0 ) instanceof ConstraintNode );

        final ConstraintNode _c1 = (ConstraintNode) model.getRoot().getChildren().get( 0 );

        assertEquals( c1.getClassName(),
                      _c1.getClassName() );
        assertEquals( c1.getFieldName(),
                      _c1.getFieldName() );
        assertNull( _c1.getOperator() );
        assertNull( _c1.getValue() );
        assertTrue( _c1.isBound() );
        assertEquals( c1.getBinding(),
                      _c1.getBinding() );

        assertEquals( 1,
                      _c1.getChildren().size() );
        assertNotNull( _c1.getChildren().get( 0 ) );
        assertTrue( _c1.getChildren().get( 0 ) instanceof ActionUpdateNode );

        final ActionUpdateNode _action = (ActionUpdateNode) _c1.getChildren().get( 0 );

        assertEquals( action.getBoundNode().getBinding(),
                      _action.getBoundNode().getBinding() );
        assertEquals( action.isModify(),
                      _action.isModify() );
        assertEquals( action.getFieldValues().size(),
                      _action.getFieldValues().size() );
        assertEquals( 1,
                      _action.getFieldValues().size() );
        assertEquals( action.getFieldValues().get( 0 ).getFieldName(),
                      _action.getFieldValues().get( 0 ).getFieldName() );
        assertEquals( action.getFieldValues().get( 0 ).getValue().getValue().toString(),
                      _action.getFieldValues().get( 0 ).getValue().getValue().toString() );

        assertEquals( 0,
                      _action.getChildren().size() );
    }

    @Test
    public void testSingleRule_ActionModifyActionRetract() throws Exception {
        final String drl = "rule \"test_0\"\n" +
                "when \n" +
                "  $p : Person( )\n" +
                "then \n" +
                "  modify( $p ) {\n" +
                "    setAge( 25 )\n" +
                "  }\n" +
                "  retract( $p );\n" +
                "end";

        final GuidedDecisionTree expected = new GuidedDecisionTree();
        expected.setTreeName( "test" );

        final TypeNode type = new TypeNodeImpl( "Person" );
        type.setBinding( "$p" );
        expected.setRoot( type );

        final ActionUpdateNode action1 = new ActionUpdateNodeImpl( type );
        action1.setModify( true );
        action1.getFieldValues().add( new ActionFieldValueImpl( "age",
                                                                new IntegerValue( 25 ) ) );
        type.addChild( action1 );

        final ActionRetractNode action2 = new ActionRetractNodeImpl( type );
        action1.addChild( action2 );

        addModelField( "Person",
                       "this",
                       "Person",
                       DataType.TYPE_THIS );
        addModelField( "Person",
                       "age",
                       Integer.class.getName(),
                       DataType.TYPE_NUMERIC_INTEGER );

        final GuidedDecisionTree model = getAndTestUnmarshalledModel( drl, "test", 0 );
        assertEquals( expected.getTreeName(),
                      model.getTreeName() );

        assertNotNull( model.getRoot() );
        assertEquals( type.getClassName(),
                      model.getRoot().getClassName() );
        assertTrue( model.getRoot().isBound() );
        assertEquals( type.getBinding(),
                      model.getRoot().getBinding() );

        assertEquals( 1,
                      model.getRoot().getChildren().size() );
        assertNotNull( model.getRoot().getChildren().get( 0 ) );
        assertTrue( model.getRoot().getChildren().get( 0 ) instanceof ActionUpdateNode );

        final ActionUpdateNode _action1 = (ActionUpdateNode) model.getRoot().getChildren().get( 0 );

        assertEquals( action1.getBoundNode().getBinding(),
                      _action1.getBoundNode().getBinding() );
        assertEquals( action1.isModify(),
                      _action1.isModify() );
        assertEquals( action1.getFieldValues().size(),
                      _action1.getFieldValues().size() );
        assertEquals( 1,
                      _action1.getFieldValues().size() );
        assertEquals( action1.getFieldValues().get( 0 ).getFieldName(),
                      _action1.getFieldValues().get( 0 ).getFieldName() );
        assertEquals( action1.getFieldValues().get( 0 ).getValue().getValue().toString(),
                      _action1.getFieldValues().get( 0 ).getValue().getValue().toString() );

        assertEquals( 1,
                      _action1.getChildren().size() );
        assertNotNull( _action1.getChildren().get( 0 ) );
        assertTrue( _action1.getChildren().get( 0 ) instanceof ActionRetractNode );

        final ActionRetractNode _action2 = (ActionRetractNode) _action1.getChildren().get( 0 );

        assertEquals( action2.getBoundNode().getBinding(),
                      _action2.getBoundNode().getBinding() );
    }

    @Test
    public void testSingleRule_ActionModifyMultipleFields() throws Exception {
        final String drl = "rule \"test_0\"\n" +
                "when \n" +
                "  $p : Person( )\n" +
                "then \n" +
                "  modify( $p ) {\n" +
                "    setAge( 25 ), \n" +
                "    setName( \"Michael\" )\n" +
                "  }\n" +
                "end";

        final GuidedDecisionTree expected = new GuidedDecisionTree();
        expected.setTreeName( "test" );

        final TypeNode type = new TypeNodeImpl( "Person" );
        type.setBinding( "$p" );
        expected.setRoot( type );

        final ActionUpdateNode action = new ActionUpdateNodeImpl( type );
        action.setModify( true );
        action.getFieldValues().add( new ActionFieldValueImpl( "age",
                                                               new IntegerValue( 25 ) ) );
        action.getFieldValues().add( new ActionFieldValueImpl( "name",
                                                               new StringValue( "Michael" ) ) );
        type.addChild( action );

        addModelField( "Person",
                       "this",
                       "Person",
                       DataType.TYPE_THIS );
        addModelField( "Person",
                       "name",
                       String.class.getName(),
                       DataType.TYPE_STRING );
        addModelField( "Person",
                       "age",
                       Integer.class.getName(),
                       DataType.TYPE_NUMERIC_INTEGER );

        final GuidedDecisionTree model = getAndTestUnmarshalledModel( drl, "test", 0 );
        assertEquals( expected.getTreeName(),
                      model.getTreeName() );

        assertNotNull( model.getRoot() );
        assertEquals( type.getClassName(),
                      model.getRoot().getClassName() );
        assertTrue( model.getRoot().isBound() );
        assertEquals( type.getBinding(),
                      model.getRoot().getBinding() );

        assertEquals( 1,
                      model.getRoot().getChildren().size() );
        assertNotNull( model.getRoot().getChildren().get( 0 ) );
        assertTrue( model.getRoot().getChildren().get( 0 ) instanceof ActionUpdateNode );

        final ActionUpdateNode _action = (ActionUpdateNode) model.getRoot().getChildren().get( 0 );

        assertEquals( action.getBoundNode().getBinding(),
                      _action.getBoundNode().getBinding() );
        assertEquals( action.isModify(),
                      _action.isModify() );
        assertEquals( action.getFieldValues().size(),
                      _action.getFieldValues().size() );
        assertEquals( 2,
                      _action.getFieldValues().size() );
        assertEquals( action.getFieldValues().get( 0 ).getFieldName(),
                      _action.getFieldValues().get( 0 ).getFieldName() );
        assertEquals( action.getFieldValues().get( 0 ).getValue().getValue().toString(),
                      _action.getFieldValues().get( 0 ).getValue().getValue().toString() );
        assertEquals( action.getFieldValues().get( 1 ).getFieldName(),
                      _action.getFieldValues().get( 1 ).getFieldName() );
        assertEquals( action.getFieldValues().get( 1 ).getValue().getValue().toString(),
                      _action.getFieldValues().get( 1 ).getValue().getValue().toString() );

        assertEquals( 0,
                      _action.getChildren().size() );
    }

    @Test
    public void testSingleRule_ActionModifyZeroFields() throws Exception {
        final String drl = "rule \"test_0\"" +
                "when \n" +
                "  $p : Person( )\n" +
                "then \n" +
                "end";

        final GuidedDecisionTree expected = new GuidedDecisionTree();
        expected.setTreeName( "test" );

        final TypeNode type = new TypeNodeImpl( "Person" );
        type.setBinding( "$p" );
        expected.setRoot( type );

        final ActionUpdateNode action = new ActionUpdateNodeImpl( type );
        action.setModify( true );
        type.addChild( action );

        addModelField( "Person",
                       "this",
                       "Person",
                       DataType.TYPE_THIS );

        final GuidedDecisionTree model = getAndTestUnmarshalledModel( drl, "test", 0 );
        assertEquals( expected.getTreeName(),
                      model.getTreeName() );

        assertNotNull( model.getRoot() );
        assertEquals( type.getClassName(),
                      model.getRoot().getClassName() );
        assertTrue( model.getRoot().isBound() );
        assertEquals( type.getBinding(),
                      model.getRoot().getBinding() );

        assertEquals( 0,
                      model.getRoot().getChildren().size() );
    }

    @Test
    public void testSingleRule_ActionModifyDateFieldValue() throws Exception {
        final String drl = "rule \"test_0\"\n" +
                "when \n" +
                "  $p : Person()\n" +
                "then \n" +
                "  java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(\"dd-MMM-yyyy\");\n" +
                "  modify( $p ) {\n" +
                "    setDateOfBirth( sdf.parse(\"15-Jul-1985\") )\n" +
                "  }\n" +
                "end";

        final GuidedDecisionTree expected = new GuidedDecisionTree();
        expected.setTreeName( "test" );

        final TypeNode type = new TypeNodeImpl( "Person" );
        type.setBinding( "$p" );
        expected.setRoot( type );

        final ActionUpdateNode action = new ActionUpdateNodeImpl( type );
        action.setModify( true );
        final Calendar dob = Calendar.getInstance();
        dob.set( Calendar.YEAR,
                 1985 );
        dob.set( Calendar.MONTH,
                 6 );
        dob.set( Calendar.DATE,
                 15 );
        dob.set( Calendar.HOUR_OF_DAY,
                 0 );
        dob.set( Calendar.MINUTE,
                 0 );
        dob.set( Calendar.SECOND,
                 0 );
        action.getFieldValues().add( new ActionFieldValueImpl( "dateOfBirth",
                                                               new DateValue( dob.getTime() ) ) );
        type.addChild( action );

        addModelField( "Person",
                       "this",
                       "Person",
                       DataType.TYPE_THIS );
        addModelField( "Person",
                       "dateOfBirth",
                       Date.class.getName(),
                       DataType.TYPE_DATE );

        final GuidedDecisionTree model = getAndTestUnmarshalledModel( drl, "test", 0 );
        assertEquals( expected.getTreeName(),
                      model.getTreeName() );

        assertNotNull( model.getRoot() );
        assertEquals( type.getClassName(),
                      model.getRoot().getClassName() );
        assertTrue( model.getRoot().isBound() );
        assertEquals( type.getBinding(),
                      model.getRoot().getBinding() );

        assertEquals( 1,
                      model.getRoot().getChildren().size() );
        assertNotNull( model.getRoot().getChildren().get( 0 ) );
        assertTrue( model.getRoot().getChildren().get( 0 ) instanceof ActionUpdateNode );

        final ActionUpdateNode _action = (ActionUpdateNode) model.getRoot().getChildren().get( 0 );

        assertEquals( action.getBoundNode().getBinding(),
                      _action.getBoundNode().getBinding() );
        assertEquals( action.isModify(),
                      _action.isModify() );
        assertEquals( action.getFieldValues().size(),
                      _action.getFieldValues().size() );
        assertEquals( 1,
                      _action.getFieldValues().size() );
        assertEquals( action.getFieldValues().get( 0 ).getFieldName(),
                      _action.getFieldValues().get( 0 ).getFieldName() );
        assertEquals( action.getFieldValues().get( 0 ).getValue().getValue().toString(),
                      _action.getFieldValues().get( 0 ).getValue().getValue().toString() );

        assertEquals( 0,
                      _action.getChildren().size() );
    }

    @Test
    public void testSingleRule_ActionSet() throws Exception {
        final String drl = "rule \"test_0\"\n" +
                "when \n" +
                "  $p : Person( )\n" +
                "then \n" +
                "  $p.setAge( 25 );\n" +
                "end";

        final GuidedDecisionTree expected = new GuidedDecisionTree();
        expected.setTreeName( "test" );

        final TypeNode type = new TypeNodeImpl( "Person" );
        type.setBinding( "$p" );
        expected.setRoot( type );

        final ActionUpdateNode action = new ActionUpdateNodeImpl( type );
        action.setModify( false );
        action.getFieldValues().add( new ActionFieldValueImpl( "age",
                                                               new IntegerValue( 25 ) ) );
        type.addChild( action );

        addModelField( "Person",
                       "this",
                       "Person",
                       DataType.TYPE_THIS );
        addModelField( "Person",
                       "age",
                       Integer.class.getName(),
                       DataType.TYPE_NUMERIC_INTEGER );

        final GuidedDecisionTree model = getAndTestUnmarshalledModel( drl, "test", 0 );
        assertEquals( expected.getTreeName(),
                      model.getTreeName() );

        assertNotNull( model.getRoot() );
        assertEquals( type.getClassName(),
                      model.getRoot().getClassName() );
        assertTrue( model.getRoot().isBound() );
        assertEquals( type.getBinding(),
                      model.getRoot().getBinding() );

        assertEquals( 1,
                      model.getRoot().getChildren().size() );
        assertNotNull( model.getRoot().getChildren().get( 0 ) );
        assertTrue( model.getRoot().getChildren().get( 0 ) instanceof ActionUpdateNode );

        final ActionUpdateNode _action = (ActionUpdateNode) model.getRoot().getChildren().get( 0 );

        assertEquals( action.getBoundNode().getBinding(),
                      _action.getBoundNode().getBinding() );
        assertEquals( action.isModify(),
                      _action.isModify() );
        assertEquals( action.getFieldValues().size(),
                      _action.getFieldValues().size() );
        assertEquals( 1,
                      _action.getFieldValues().size() );
        assertEquals( action.getFieldValues().get( 0 ).getFieldName(),
                      _action.getFieldValues().get( 0 ).getFieldName() );
        assertEquals( action.getFieldValues().get( 0 ).getValue().getValue().toString(),
                      _action.getFieldValues().get( 0 ).getValue().getValue().toString() );

        assertEquals( 0,
                      _action.getChildren().size() );
    }

    @Test
    public void testSingleRule_ActionSetWithConstraint() throws Exception {
        final String drl = "rule \"test_0\"\n" +
                "when \n" +
                "  $p : Person( $n : name )\n" +
                "then \n" +
                "  $p.setAge( 25 );\n" +
                "end";

        final GuidedDecisionTree expected = new GuidedDecisionTree();
        expected.setTreeName( "test" );

        final TypeNode type = new TypeNodeImpl( "Person" );
        type.setBinding( "$p" );
        final ConstraintNode c1 = new ConstraintNodeImpl( "Person",
                                                          "name" );
        c1.setBinding( "$n" );
        expected.setRoot( type );
        type.addChild( c1 );

        final ActionUpdateNode action = new ActionUpdateNodeImpl( type );
        action.setModify( false );
        action.getFieldValues().add( new ActionFieldValueImpl( "age",
                                                               new IntegerValue( 25 ) ) );
        c1.addChild( action );

        addModelField( "Person",
                       "this",
                       "Person",
                       DataType.TYPE_THIS );
        addModelField( "Person",
                       "name",
                       String.class.getName(),
                       DataType.TYPE_STRING );
        addModelField( "Person",
                       "age",
                       Integer.class.getName(),
                       DataType.TYPE_NUMERIC_INTEGER );

        final GuidedDecisionTree model = getAndTestUnmarshalledModel( drl, "test", 0 );
        assertEquals( expected.getTreeName(),
                      model.getTreeName() );

        assertNotNull( model.getRoot() );
        assertEquals( type.getClassName(),
                      model.getRoot().getClassName() );
        assertTrue( model.getRoot().isBound() );
        assertEquals( type.getBinding(),
                      model.getRoot().getBinding() );

        assertEquals( 1,
                      model.getRoot().getChildren().size() );
        assertNotNull( model.getRoot().getChildren().get( 0 ) );
        assertTrue( model.getRoot().getChildren().get( 0 ) instanceof ConstraintNode );

        final ConstraintNode _c1 = (ConstraintNode) model.getRoot().getChildren().get( 0 );

        assertEquals( c1.getClassName(),
                      _c1.getClassName() );
        assertEquals( c1.getFieldName(),
                      _c1.getFieldName() );
        assertNull( _c1.getOperator() );
        assertNull( _c1.getValue() );
        assertTrue( _c1.isBound() );
        assertEquals( c1.getBinding(),
                      _c1.getBinding() );

        assertEquals( 1,
                      _c1.getChildren().size() );
        assertNotNull( _c1.getChildren().get( 0 ) );
        assertTrue( _c1.getChildren().get( 0 ) instanceof ActionUpdateNode );

        final ActionUpdateNode _action = (ActionUpdateNode) _c1.getChildren().get( 0 );

        assertEquals( action.getBoundNode().getBinding(),
                      _action.getBoundNode().getBinding() );
        assertEquals( action.isModify(),
                      _action.isModify() );
        assertEquals( action.getFieldValues().size(),
                      _action.getFieldValues().size() );
        assertEquals( 1,
                      _action.getFieldValues().size() );
        assertEquals( action.getFieldValues().get( 0 ).getFieldName(),
                      _action.getFieldValues().get( 0 ).getFieldName() );
        assertEquals( action.getFieldValues().get( 0 ).getValue().getValue().toString(),
                      _action.getFieldValues().get( 0 ).getValue().getValue().toString() );

        assertEquals( 0,
                      _action.getChildren().size() );
    }

    @Test
    public void testSingleRule_ActionSetActionRetract() throws Exception {
        final String drl = "rule \"test_0\"\n" +
                "when \n" +
                "  $p : Person( )\n" +
                "then \n" +
                "  $p.setAge( 25 );\n" +
                "  retract( $p );\n" +
                "end";

        final GuidedDecisionTree expected = new GuidedDecisionTree();
        expected.setTreeName( "test" );

        final TypeNode type = new TypeNodeImpl( "Person" );
        type.setBinding( "$p" );
        expected.setRoot( type );

        final ActionUpdateNode action1 = new ActionUpdateNodeImpl( type );
        action1.setModify( false );
        action1.getFieldValues().add( new ActionFieldValueImpl( "age",
                                                                new IntegerValue( 25 ) ) );
        type.addChild( action1 );

        final ActionRetractNode action2 = new ActionRetractNodeImpl( type );
        action1.addChild( action2 );

        addModelField( "Person",
                       "this",
                       "Person",
                       DataType.TYPE_THIS );
        addModelField( "Person",
                       "age",
                       Integer.class.getName(),
                       DataType.TYPE_NUMERIC_INTEGER );

        final GuidedDecisionTree model = getAndTestUnmarshalledModel( drl, "test", 0 );
        assertEquals( expected.getTreeName(),
                      model.getTreeName() );

        assertNotNull( model.getRoot() );
        assertEquals( type.getClassName(),
                      model.getRoot().getClassName() );
        assertTrue( model.getRoot().isBound() );
        assertEquals( type.getBinding(),
                      model.getRoot().getBinding() );

        assertEquals( 1,
                      model.getRoot().getChildren().size() );
        assertNotNull( model.getRoot().getChildren().get( 0 ) );
        assertTrue( model.getRoot().getChildren().get( 0 ) instanceof ActionUpdateNode );

        final ActionUpdateNode _action1 = (ActionUpdateNode) model.getRoot().getChildren().get( 0 );

        assertEquals( action1.getBoundNode().getBinding(),
                      _action1.getBoundNode().getBinding() );
        assertEquals( action1.isModify(),
                      _action1.isModify() );
        assertEquals( action1.getFieldValues().size(),
                      _action1.getFieldValues().size() );
        assertEquals( 1,
                      _action1.getFieldValues().size() );
        assertEquals( action1.getFieldValues().get( 0 ).getFieldName(),
                      _action1.getFieldValues().get( 0 ).getFieldName() );
        assertEquals( action1.getFieldValues().get( 0 ).getValue().getValue().toString(),
                      _action1.getFieldValues().get( 0 ).getValue().getValue().toString() );

        assertEquals( 1,
                      _action1.getChildren().size() );
        assertNotNull( _action1.getChildren().get( 0 ) );
        assertTrue( _action1.getChildren().get( 0 ) instanceof ActionRetractNode );

        final ActionRetractNode _action2 = (ActionRetractNode) _action1.getChildren().get( 0 );

        assertEquals( action2.getBoundNode().getBinding(),
                      _action2.getBoundNode().getBinding() );
    }

    @Test
    public void testSingleRule_ActionSetMultipleFields() throws Exception {
        final String drl = "rule \"test_0\"\n" +
                "when \n" +
                "  $p : Person( )\n" +
                "then \n" +
                "  $p.setAge( 25 );\n" +
                "  $p.setName( \"Michael\" );\n" +
                "end";

        final GuidedDecisionTree expected = new GuidedDecisionTree();
        expected.setTreeName( "test" );

        final TypeNode type = new TypeNodeImpl( "Person" );
        type.setBinding( "$p" );
        expected.setRoot( type );

        final ActionUpdateNode action = new ActionUpdateNodeImpl( type );
        action.setModify( false );
        action.getFieldValues().add( new ActionFieldValueImpl( "age",
                                                               new IntegerValue( 25 ) ) );
        action.getFieldValues().add( new ActionFieldValueImpl( "name",
                                                               new StringValue( "Michael" ) ) );
        type.addChild( action );

        addModelField( "Person",
                       "this",
                       "Person",
                       DataType.TYPE_THIS );
        addModelField( "Person",
                       "name",
                       String.class.getName(),
                       DataType.TYPE_STRING );
        addModelField( "Person",
                       "age",
                       Integer.class.getName(),
                       DataType.TYPE_NUMERIC_INTEGER );

        final GuidedDecisionTree model = getAndTestUnmarshalledModel( drl, "test", 0 );
        assertEquals( expected.getTreeName(),
                      model.getTreeName() );

        assertNotNull( model.getRoot() );
        assertEquals( type.getClassName(),
                      model.getRoot().getClassName() );
        assertTrue( model.getRoot().isBound() );
        assertEquals( type.getBinding(),
                      model.getRoot().getBinding() );

        assertEquals( 1,
                      model.getRoot().getChildren().size() );
        assertNotNull( model.getRoot().getChildren().get( 0 ) );
        assertTrue( model.getRoot().getChildren().get( 0 ) instanceof ActionUpdateNode );

        final ActionUpdateNode _action = (ActionUpdateNode) model.getRoot().getChildren().get( 0 );

        assertEquals( action.getBoundNode().getBinding(),
                      _action.getBoundNode().getBinding() );
        assertEquals( action.isModify(),
                      _action.isModify() );
        assertEquals( action.getFieldValues().size(),
                      _action.getFieldValues().size() );
        assertEquals( 2,
                      _action.getFieldValues().size() );
        assertEquals( action.getFieldValues().get( 0 ).getFieldName(),
                      _action.getFieldValues().get( 0 ).getFieldName() );
        assertEquals( action.getFieldValues().get( 0 ).getValue().getValue().toString(),
                      _action.getFieldValues().get( 0 ).getValue().getValue().toString() );
        assertEquals( action.getFieldValues().get( 1 ).getFieldName(),
                      _action.getFieldValues().get( 1 ).getFieldName() );
        assertEquals( action.getFieldValues().get( 1 ).getValue().getValue().toString(),
                      _action.getFieldValues().get( 1 ).getValue().getValue().toString() );

        assertEquals( 0,
                      _action.getChildren().size() );
    }

    @Test
    public void testSingleRule_ActionSetZeroFields() throws Exception {
        final String drl = "rule \"test_0\"" +
                "when \n" +
                "  $p : Person( )\n" +
                "then \n" +
                "end";

        final GuidedDecisionTree expected = new GuidedDecisionTree();
        expected.setTreeName( "test" );

        final TypeNode type = new TypeNodeImpl( "Person" );
        type.setBinding( "$p" );
        expected.setRoot( type );

        final ActionUpdateNode action = new ActionUpdateNodeImpl( type );
        action.setModify( false );
        type.addChild( action );

        addModelField( "Person",
                       "this",
                       "Person",
                       DataType.TYPE_THIS );

        final GuidedDecisionTree model = getAndTestUnmarshalledModel( drl, "test", 0 );
        assertEquals( expected.getTreeName(),
                      model.getTreeName() );

        assertNotNull( model.getRoot() );
        assertEquals( type.getClassName(),
                      model.getRoot().getClassName() );
        assertTrue( model.getRoot().isBound() );
        assertEquals( type.getBinding(),
                      model.getRoot().getBinding() );

        assertEquals( 0,
                      model.getRoot().getChildren().size() );
    }

    @Test
    public void testSingleRule_ActionSetDateFieldValue() throws Exception {
        final String drl = "rule \"test_0\"\n" +
                "when \n" +
                "  $p : Person()\n" +
                "then \n" +
                "  java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(\"dd-MMM-yyyy\");\n" +
                "  $p.setDateOfBirth( sdf.parse(\"15-Jul-1985\") );\n" +
                "end";

        final GuidedDecisionTree expected = new GuidedDecisionTree();
        expected.setTreeName( "test" );

        final TypeNode type = new TypeNodeImpl( "Person" );
        type.setBinding( "$p" );
        expected.setRoot( type );

        final ActionUpdateNode action = new ActionUpdateNodeImpl( type );
        action.setModify( false );
        final Calendar dob = Calendar.getInstance();
        dob.set( Calendar.YEAR,
                 1985 );
        dob.set( Calendar.MONTH,
                 6 );
        dob.set( Calendar.DATE,
                 15 );
        dob.set( Calendar.HOUR_OF_DAY,
                 0 );
        dob.set( Calendar.MINUTE,
                 0 );
        dob.set( Calendar.SECOND,
                 0 );

        action.getFieldValues().add( new ActionFieldValueImpl( "dateOfBirth",
                                                               new DateValue( dob.getTime() ) ) );
        type.addChild( action );

        addModelField( "Person",
                       "this",
                       "Person",
                       DataType.TYPE_THIS );
        addModelField( "Person",
                       "dateOfBirth",
                       Date.class.getName(),
                       DataType.TYPE_DATE );

        final GuidedDecisionTree model = getAndTestUnmarshalledModel( drl, "test", 0 );
        assertEquals( expected.getTreeName(),
                      model.getTreeName() );

        assertNotNull( model.getRoot() );
        assertEquals( type.getClassName(),
                      model.getRoot().getClassName() );
        assertTrue( model.getRoot().isBound() );
        assertEquals( type.getBinding(),
                      model.getRoot().getBinding() );

        assertEquals( 1,
                      model.getRoot().getChildren().size() );
        assertNotNull( model.getRoot().getChildren().get( 0 ) );
        assertTrue( model.getRoot().getChildren().get( 0 ) instanceof ActionUpdateNode );

        final ActionUpdateNode _action = (ActionUpdateNode) model.getRoot().getChildren().get( 0 );

        assertEquals( action.getBoundNode().getBinding(),
                      _action.getBoundNode().getBinding() );
        assertEquals( action.isModify(),
                      _action.isModify() );
        assertEquals( action.getFieldValues().size(),
                      _action.getFieldValues().size() );
        assertEquals( 1,
                      _action.getFieldValues().size() );
        assertEquals( action.getFieldValues().get( 0 ).getFieldName(),
                      _action.getFieldValues().get( 0 ).getFieldName() );
        assertEquals( action.getFieldValues().get( 0 ).getValue().getValue().toString(),
                      _action.getFieldValues().get( 0 ).getValue().getValue().toString() );

        assertEquals( 0,
                      _action.getChildren().size() );
    }

    @Test
    public void testSingleRule_ActionInsert() throws Exception {
        final String drl = "rule \"test_0\"\n" +
                "when \n" +
                "  Person( )\n" +
                "then \n" +
                "  Person $var0 = new Person();\n" +
                "  $var0.setAge( 25 );\n" +
                "  insert( $var0 );\n" +
                "end";

        final GuidedDecisionTree expected = new GuidedDecisionTree();
        expected.setTreeName( "test" );

        final TypeNode type = new TypeNodeImpl( "Person" );
        expected.setRoot( type );

        final ActionInsertNode action = new ActionInsertNodeImpl( "Person" );
        action.setLogicalInsertion( false );
        action.getFieldValues().add( new ActionFieldValueImpl( "age",
                                                               new IntegerValue( 25 ) ) );
        type.addChild( action );

        addModelField( "Person",
                       "this",
                       "Person",
                       DataType.TYPE_THIS );
        addModelField( "Person",
                       "age",
                       Integer.class.getName(),
                       DataType.TYPE_NUMERIC_INTEGER );

        final GuidedDecisionTree model = getAndTestUnmarshalledModel( drl, "test", 0 );
        assertEquals( expected.getTreeName(),
                      model.getTreeName() );

        assertNotNull( model.getRoot() );
        assertEquals( type.getClassName(),
                      model.getRoot().getClassName() );
        assertFalse( model.getRoot().isBound() );
        assertEquals( type.getBinding(),
                      model.getRoot().getBinding() );

        assertEquals( 1,
                      model.getRoot().getChildren().size() );
        assertNotNull( model.getRoot().getChildren().get( 0 ) );
        assertTrue( model.getRoot().getChildren().get( 0 ) instanceof ActionInsertNode );

        final ActionInsertNode _action = (ActionInsertNode) model.getRoot().getChildren().get( 0 );

        assertEquals( action.getClassName(),
                      _action.getClassName() );
        assertEquals( action.isLogicalInsertion(),
                      _action.isLogicalInsertion() );
        assertEquals( action.getFieldValues().size(),
                      _action.getFieldValues().size() );
        assertEquals( 1,
                      _action.getFieldValues().size() );
        assertEquals( action.getFieldValues().get( 0 ).getFieldName(),
                      _action.getFieldValues().get( 0 ).getFieldName() );
        assertEquals( action.getFieldValues().get( 0 ).getValue().getValue().toString(),
                      _action.getFieldValues().get( 0 ).getValue().getValue().toString() );

        assertEquals( 0,
                      _action.getChildren().size() );
    }

    @Test
    public void testSingleRule_ActionInsertLogical() throws Exception {
        final String drl = "rule \"test_0\"\n" +
                "when \n" +
                "  Person( )\n" +
                "then \n" +
                "  Person $var0 = new Person();\n" +
                "  $var0.setAge( 25 );\n" +
                "  insertLogical( $var0 );\n" +
                "end";

        final GuidedDecisionTree expected = new GuidedDecisionTree();
        expected.setTreeName( "test" );

        final TypeNode type = new TypeNodeImpl( "Person" );
        expected.setRoot( type );

        final ActionInsertNode action = new ActionInsertNodeImpl( "Person" );
        action.setLogicalInsertion( true );
        action.getFieldValues().add( new ActionFieldValueImpl( "age",
                                                               new IntegerValue( 25 ) ) );
        type.addChild( action );

        addModelField( "Person",
                       "this",
                       "Person",
                       DataType.TYPE_THIS );
        addModelField( "Person",
                       "age",
                       Integer.class.getName(),
                       DataType.TYPE_NUMERIC_INTEGER );

        final GuidedDecisionTree model = getAndTestUnmarshalledModel( drl, "test", 0 );
        assertEquals( expected.getTreeName(),
                      model.getTreeName() );

        assertNotNull( model.getRoot() );
        assertEquals( type.getClassName(),
                      model.getRoot().getClassName() );
        assertFalse( model.getRoot().isBound() );
        assertEquals( type.getBinding(),
                      model.getRoot().getBinding() );

        assertEquals( 1,
                      model.getRoot().getChildren().size() );
        assertNotNull( model.getRoot().getChildren().get( 0 ) );
        assertTrue( model.getRoot().getChildren().get( 0 ) instanceof ActionInsertNode );

        final ActionInsertNode _action = (ActionInsertNode) model.getRoot().getChildren().get( 0 );

        assertEquals( action.getClassName(),
                      _action.getClassName() );
        assertEquals( action.isLogicalInsertion(),
                      _action.isLogicalInsertion() );
        assertEquals( action.getFieldValues().size(),
                      _action.getFieldValues().size() );
        assertEquals( 1,
                      _action.getFieldValues().size() );
        assertEquals( action.getFieldValues().get( 0 ).getFieldName(),
                      _action.getFieldValues().get( 0 ).getFieldName() );
        assertEquals( action.getFieldValues().get( 0 ).getValue().getValue().toString(),
                      _action.getFieldValues().get( 0 ).getValue().getValue().toString() );

        assertEquals( 0,
                      _action.getChildren().size() );
    }

    @Test
    public void testSingleRule_ActionInsertDateFieldValue() throws Exception {
        final String drl = "rule \"test_0\"\n" +
                "when \n" +
                "  Person( )\n" +
                "then \n" +
                "  java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(\"dd-MMM-yyyy\");\n" +
                "  Person $var0 = new Person();\n" +
                "  $var0.setDateOfBirth( sdf.parse(\"15-Jul-1985\") );\n" +
                "  insert( $var0 );\n" +
                "end";

        final GuidedDecisionTree expected = new GuidedDecisionTree();
        expected.setTreeName( "test" );

        final TypeNode type = new TypeNodeImpl( "Person" );
        expected.setRoot( type );

        final ActionInsertNode action = new ActionInsertNodeImpl( "Person" );
        action.setLogicalInsertion( false );
        final Calendar dob = Calendar.getInstance();
        dob.set( Calendar.YEAR,
                 1985 );
        dob.set( Calendar.MONTH,
                 6 );
        dob.set( Calendar.DATE,
                 15 );
        dob.set( Calendar.HOUR_OF_DAY,
                 0 );
        dob.set( Calendar.MINUTE,
                 0 );
        dob.set( Calendar.SECOND,
                 0 );
        action.getFieldValues().add( new ActionFieldValueImpl( "dateOfBirth",
                                                               new DateValue( dob.getTime() ) ) );
        type.addChild( action );

        addModelField( "Person",
                       "this",
                       "Person",
                       DataType.TYPE_THIS );
        addModelField( "Person",
                       "dateOfBirth",
                       Date.class.getName(),
                       DataType.TYPE_DATE );

        final GuidedDecisionTree model = getAndTestUnmarshalledModel( drl, "test", 0 );
        assertEquals( expected.getTreeName(),
                      model.getTreeName() );

        assertNotNull( model.getRoot() );
        assertEquals( type.getClassName(),
                      model.getRoot().getClassName() );
        assertFalse( model.getRoot().isBound() );

        assertEquals( 1,
                      model.getRoot().getChildren().size() );
        assertNotNull( model.getRoot().getChildren().get( 0 ) );
        assertTrue( model.getRoot().getChildren().get( 0 ) instanceof ActionInsertNode );

        final ActionInsertNode _action = (ActionInsertNode) model.getRoot().getChildren().get( 0 );

        assertEquals( action.getClassName(),
                      _action.getClassName() );
        assertEquals( action.isLogicalInsertion(),
                      _action.isLogicalInsertion() );
        assertEquals( action.getFieldValues().size(),
                      _action.getFieldValues().size() );
        assertEquals( 1,
                      _action.getFieldValues().size() );
        assertEquals( action.getFieldValues().get( 0 ).getFieldName(),
                      _action.getFieldValues().get( 0 ).getFieldName() );
        assertEquals( action.getFieldValues().get( 0 ).getValue().getValue().toString(),
                      _action.getFieldValues().get( 0 ).getValue().getValue().toString() );

        assertEquals( 0,
                      _action.getChildren().size() );
    }

    @Test
    public void testSingleRule_ActionInsertLogicalDateFieldValue() throws Exception {
        final String drl = "rule \"test_0\"\n" +
                "when \n" +
                "  Person( )\n" +
                "then \n" +
                "  java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(\"dd-MMM-yyyy\");\n" +
                "  Person $var0 = new Person();\n" +
                "  $var0.setDateOfBirth( sdf.parse(\"15-Jul-1985\") );\n" +
                "  insertLogical( $var0 );\n" +
                "end";

        final GuidedDecisionTree expected = new GuidedDecisionTree();
        expected.setTreeName( "test" );

        final TypeNode type = new TypeNodeImpl( "Person" );
        expected.setRoot( type );

        final ActionInsertNode action = new ActionInsertNodeImpl( "Person" );
        action.setLogicalInsertion( true );
        final Calendar dob = Calendar.getInstance();
        dob.set( Calendar.YEAR,
                 1985 );
        dob.set( Calendar.MONTH,
                 6 );
        dob.set( Calendar.DATE,
                 15 );
        dob.set( Calendar.HOUR_OF_DAY,
                 0 );
        dob.set( Calendar.MINUTE,
                 0 );
        dob.set( Calendar.SECOND,
                 0 );
        action.getFieldValues().add( new ActionFieldValueImpl( "dateOfBirth",
                                                               new DateValue( dob.getTime() ) ) );
        type.addChild( action );

        addModelField( "Person",
                       "this",
                       "Person",
                       DataType.TYPE_THIS );
        addModelField( "Person",
                       "dateOfBirth",
                       Date.class.getName(),
                       DataType.TYPE_DATE );

        final GuidedDecisionTree model = getAndTestUnmarshalledModel( drl, "test", 0 );
        assertEquals( expected.getTreeName(),
                      model.getTreeName() );

        assertNotNull( model.getRoot() );
        assertEquals( type.getClassName(),
                      model.getRoot().getClassName() );
        assertFalse( model.getRoot().isBound() );

        assertEquals( 1,
                      model.getRoot().getChildren().size() );
        assertNotNull( model.getRoot().getChildren().get( 0 ) );
        assertTrue( model.getRoot().getChildren().get( 0 ) instanceof ActionInsertNode );

        final ActionInsertNode _action = (ActionInsertNode) model.getRoot().getChildren().get( 0 );

        assertEquals( action.getClassName(),
                      _action.getClassName() );
        assertEquals( action.isLogicalInsertion(),
                      _action.isLogicalInsertion() );
        assertEquals( action.getFieldValues().size(),
                      _action.getFieldValues().size() );
        assertEquals( 1,
                      _action.getFieldValues().size() );
        assertEquals( action.getFieldValues().get( 0 ).getFieldName(),
                      _action.getFieldValues().get( 0 ).getFieldName() );
        assertEquals( action.getFieldValues().get( 0 ).getValue().getValue().toString(),
                      _action.getFieldValues().get( 0 ).getValue().getValue().toString() );

        assertEquals( 0,
                      _action.getChildren().size() );
    }

    @Test
    public void testRuleWithLinesWithSpaces() throws Exception {
        final String drl = "rule \"test1\" \n" +
                "when \n" +
                " \n" +
                " \n" +
                " Person( integerField == 10 ) \n" +
                "then \n" +
                "end \n";

        addModelField( "Person",
                       "this",
                       "Person",
                       DataType.TYPE_THIS );
        addModelField( "Person",
                       "integerField",
                       Integer.class.getName(),
                       DataType.TYPE_NUMERIC_INTEGER );

        getAndTestUnmarshalledModel( drl, "test", 0 );
    }
}
