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

public class GuidedDecisionTreeDRLPersistenceMarshallingTest {

    @Test
    public void testSingleRule_ZeroConstraints() throws Exception {
        final String expected = "rule \"test_0\"" +
                "when\n" +
                "  Person()\n" +
                "then\n" +
                "end";

        final GuidedDecisionTree model = new GuidedDecisionTree();
        model.setTreeName( "test" );

        final TypeNode type = new TypeNodeImpl( "Person" );
        model.setRoot( type );

        final String drl = GuidedDecisionTreeDRLPersistence.getInstance().marshal( model );
        assertEqualsIgnoreWhitespace( expected,
                                      drl );
    }

    @Test
    public void testSingleRule_SingleConstraint() throws Exception {
        final String expected = "rule \"test_0\"" +
                "when\n" +
                "  Person( name == \"Michael\" )\n" +
                "then\n" +
                "end";

        final GuidedDecisionTree model = new GuidedDecisionTree();
        model.setTreeName( "test" );

        final TypeNode type = new TypeNodeImpl( "Person" );
        final ConstraintNode c1 = new ConstraintNodeImpl( "Person",
                                                          "name",
                                                          "==",
                                                          new StringValue( "Michael" ) );
        model.setRoot( type );
        type.addChild( c1 );

        final String drl = GuidedDecisionTreeDRLPersistence.getInstance().marshal( model );
        assertEqualsIgnoreWhitespace( expected,
                                      drl );
    }

    @Test
    public void testSingleRule_SingleConstraintNoOperatorNoValue() throws Exception {
        final String expected = "rule \"test_0\"" +
                "when\n" +
                "  Person( name )\n" +
                "then\n" +
                "end";

        final GuidedDecisionTree model = new GuidedDecisionTree();
        model.setTreeName( "test" );

        final TypeNode type = new TypeNodeImpl( "Person" );
        final ConstraintNode c1 = new ConstraintNodeImpl( "Person",
                                                          "name" );
        model.setRoot( type );
        type.addChild( c1 );

        final String drl = GuidedDecisionTreeDRLPersistence.getInstance().marshal( model );
        assertEqualsIgnoreWhitespace( expected,
                                      drl );
    }

    @Test
    public void testSingleRule_MultipleConstraints() throws Exception {
        final String expected = "rule \"test_0\"" +
                "when\n" +
                "  Person( name == \"Michael\", age == 41 )\n" +
                "then\n" +
                "end";

        final GuidedDecisionTree model = new GuidedDecisionTree();
        model.setTreeName( "test" );

        final TypeNode type = new TypeNodeImpl( "Person" );
        final ConstraintNode c1 = new ConstraintNodeImpl( "Person",
                                                          "name",
                                                          "==",
                                                          new StringValue( "Michael" ) );
        final ConstraintNode c2 = new ConstraintNodeImpl( "Person",
                                                          "age",
                                                          "==",
                                                          new IntegerValue( 41 ) );
        model.setRoot( type );
        type.addChild( c1 );
        c1.addChild( c2 );

        final String drl = GuidedDecisionTreeDRLPersistence.getInstance().marshal( model );
        assertEqualsIgnoreWhitespace( expected,
                                      drl );
    }

    @Test
    public void testMultipleRules_2Rules() throws Exception {
        final String expected = "rule \"test_0\"" +
                "when\n" +
                "  Person( name == \"Michael\" )\n" +
                "then\n" +
                "end" +
                "rule \"test_1\"" +
                "when\n" +
                "  Person( age == 41 )\n" +
                "then\n" +
                "end";

        final GuidedDecisionTree model = new GuidedDecisionTree();
        model.setTreeName( "test" );

        final TypeNode type = new TypeNodeImpl( "Person" );
        final ConstraintNode c1 = new ConstraintNodeImpl( "Person",
                                                          "name",
                                                          "==",
                                                          new StringValue( "Michael" ) );
        final ConstraintNode c2 = new ConstraintNodeImpl( "Person",
                                                          "age",
                                                          "==",
                                                          new IntegerValue( 41 ) );
        model.setRoot( type );
        type.addChild( c1 );
        type.addChild( c2 );

        final String drl = GuidedDecisionTreeDRLPersistence.getInstance().marshal( model );
        assertEqualsIgnoreWhitespace( expected,
                                      drl );
    }

    @Test
    public void testMultipleRules_3Rules() throws Exception {
        final String expected = "rule \"test_0\"" +
                "when\n" +
                "  Person( name == \"Michael\" )\n" +
                "then\n" +
                "end" +
                "rule \"test_1\"" +
                "when\n" +
                "  Person( age == 41 )\n" +
                "then\n" +
                "end" +
                "rule \"test_2\"" +
                "when\n" +
                "  Person( gender == \"Male\" )\n" +
                "then\n" +
                "end";

        final GuidedDecisionTree model = new GuidedDecisionTree();
        model.setTreeName( "test" );

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
        model.setRoot( type );
        type.addChild( c1 );
        type.addChild( c2 );
        type.addChild( c3 );

        final String drl = GuidedDecisionTreeDRLPersistence.getInstance().marshal( model );
        assertEqualsIgnoreWhitespace( expected,
                                      drl );
    }

    @Test
    public void testMultipleRules_2Rules_1Simple_1Complex() throws Exception {
        final String expected = "rule \"test_0\"" +
                "when\n" +
                "  Person( name == \"Michael\" )\n" +
                "then\n" +
                "end" +
                "rule \"test_1\"" +
                "when\n" +
                "  Person( name == \"Fred\", age == 41 )\n" +
                "then\n" +
                "end";

        final GuidedDecisionTree model = new GuidedDecisionTree();
        model.setTreeName( "test" );

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
        model.setRoot( type );
        type.addChild( c1 );
        type.addChild( c2 );
        c2.addChild( c3 );

        final String drl = GuidedDecisionTreeDRLPersistence.getInstance().marshal( model );
        assertEqualsIgnoreWhitespace( expected,
                                      drl );
    }

    @Test
    public void testSingleRule_MultiplePatterns() throws Exception {
        final String expected = "rule \"test_0\"" +
                "when\n" +
                "  Person( name == \"Michael\" )\n" +
                "  Address( country == \"England\" )\n" +
                "then\n" +
                "end";

        final GuidedDecisionTree model = new GuidedDecisionTree();
        model.setTreeName( "test" );

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
        model.setRoot( type1 );
        type1.addChild( c1 );
        c1.addChild( type2 );
        type2.addChild( c2 );

        final String drl = GuidedDecisionTreeDRLPersistence.getInstance().marshal( model );
        assertEqualsIgnoreWhitespace( expected,
                                      drl );
    }

    @Test
    public void testMultipleRules_MultiplePatterns() throws Exception {
        final String expected = "rule \"test_0\"" +
                "when\n" +
                "  Person( name == \"Michael\" )\n" +
                "  Address( country == \"England\" )\n" +
                "then\n" +
                "end" +
                "rule \"test_1\"" +
                "when\n" +
                "  Person( name == \"Michael\" )\n" +
                "  Address( country == \"Norway\" )\n" +
                "then\n" +
                "end" +
                "rule \"test_2\"" +
                "when\n" +
                "  Person( name == \"Fred\" )\n" +
                "then\n" +
                "end";

        final GuidedDecisionTree model = new GuidedDecisionTree();
        model.setTreeName( "test" );

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
        model.setRoot( type1 );
        type1.addChild( c1a );
        type1.addChild( c1b );
        c1a.addChild( type2 );
        type2.addChild( c2a );
        type2.addChild( c2b );

        final String drl = GuidedDecisionTreeDRLPersistence.getInstance().marshal( model );
        assertEqualsIgnoreWhitespace( expected,
                                      drl );
    }

    @Test
    public void testValue_BigDecimal() throws Exception {
        final String expected = "rule \"test_0\"" +
                "when\n" +
                "  Person( bigDecimalField == 1000000B )\n" +
                "then\n" +
                "end";

        final GuidedDecisionTree model = new GuidedDecisionTree();
        model.setTreeName( "test" );

        final TypeNode type = new TypeNodeImpl( "Person" );
        final ConstraintNode c1 = new ConstraintNodeImpl( "Person",
                                                          "bigDecimalField",
                                                          "==",
                                                          new BigDecimalValue( new BigDecimal( 1000000 ) ) );
        model.setRoot( type );
        type.addChild( c1 );

        final String drl = GuidedDecisionTreeDRLPersistence.getInstance().marshal( model );
        assertEqualsIgnoreWhitespace( expected,
                                      drl );
    }

    @Test
    public void testValue_BigInteger() throws Exception {
        final String expected = "rule \"test_0\"" +
                "when\n" +
                "  Person( bigIntegerField == 1000000I )\n" +
                "then\n" +
                "end";

        final GuidedDecisionTree model = new GuidedDecisionTree();
        model.setTreeName( "test" );

        final TypeNode type = new TypeNodeImpl( "Person" );
        final ConstraintNode c1 = new ConstraintNodeImpl( "Person",
                                                          "bigIntegerField",
                                                          "==",
                                                          new BigIntegerValue( new BigInteger( "1000000" ) ) );
        model.setRoot( type );
        type.addChild( c1 );

        final String drl = GuidedDecisionTreeDRLPersistence.getInstance().marshal( model );
        assertEqualsIgnoreWhitespace( expected,
                                      drl );
    }

    @Test
    public void testValue_Boolean() throws Exception {
        final String expected = "rule \"test_0\"" +
                "when\n" +
                "  Person( booleanField == true )\n" +
                "then\n" +
                "end";

        final GuidedDecisionTree model = new GuidedDecisionTree();
        model.setTreeName( "test" );

        final TypeNode type = new TypeNodeImpl( "Person" );
        final ConstraintNode c1 = new ConstraintNodeImpl( "Person",
                                                          "booleanField",
                                                          "==",
                                                          new BooleanValue( true ) );
        model.setRoot( type );
        type.addChild( c1 );

        final String drl = GuidedDecisionTreeDRLPersistence.getInstance().marshal( model );
        assertEqualsIgnoreWhitespace( expected,
                                      drl );
    }

    @Test
    public void testValue_Byte() throws Exception {
        final String expected = "rule \"test_0\"" +
                "when\n" +
                "  Person( byteField == 100 )\n" +
                "then\n" +
                "end";

        final GuidedDecisionTree model = new GuidedDecisionTree();
        model.setTreeName( "test" );

        final TypeNode type = new TypeNodeImpl( "Person" );
        final ConstraintNode c1 = new ConstraintNodeImpl( "Person",
                                                          "byteField",
                                                          "==",
                                                          new ByteValue( new Byte( "100" ) ) );
        model.setRoot( type );
        type.addChild( c1 );

        final String drl = GuidedDecisionTreeDRLPersistence.getInstance().marshal( model );
        assertEqualsIgnoreWhitespace( expected,
                                      drl );
    }

    @Test
    public void testValue_Date() throws Exception {
        final String expected = "rule \"test_0\"" +
                "when\n" +
                "  Person( dateField == \"15-Jul-1984\" )\n" +
                "then\n" +
                "end";

        final GuidedDecisionTree model = new GuidedDecisionTree();
        model.setTreeName( "test" );

        final TypeNode type = new TypeNodeImpl( "Person" );
        final ConstraintNode c1 = new ConstraintNodeImpl( "Person",
                                                          "dateField",
                                                          "==",
                                                          new DateValue( new Date( 84, 6, 15 ) ) );
        model.setRoot( type );
        type.addChild( c1 );

        final String drl = GuidedDecisionTreeDRLPersistence.getInstance().marshal( model );
        assertEqualsIgnoreWhitespace( expected,
                                      drl );
    }

    @Test
    public void testValue_Double() throws Exception {
        final String expected = "rule \"test_0\"" +
                "when\n" +
                "  Person( doubleField == 1000.56 )\n" +
                "then\n" +
                "end";

        final GuidedDecisionTree model = new GuidedDecisionTree();
        model.setTreeName( "test" );

        final TypeNode type = new TypeNodeImpl( "Person" );
        final ConstraintNode c1 = new ConstraintNodeImpl( "Person",
                                                          "doubleField",
                                                          "==",
                                                          new DoubleValue( 1000.56 ) );
        model.setRoot( type );
        type.addChild( c1 );

        final String drl = GuidedDecisionTreeDRLPersistence.getInstance().marshal( model );
        assertEqualsIgnoreWhitespace( expected,
                                      drl );
    }

    @Test
    public void testValue_Float() throws Exception {
        final String expected = "rule \"test_0\"" +
                "when\n" +
                "  Person( floatField == 1000.56 )\n" +
                "then\n" +
                "end";

        final GuidedDecisionTree model = new GuidedDecisionTree();
        model.setTreeName( "test" );

        final TypeNode type = new TypeNodeImpl( "Person" );
        final ConstraintNode c1 = new ConstraintNodeImpl( "Person",
                                                          "floatField",
                                                          "==",
                                                          new FloatValue( 1000.56f ) );
        model.setRoot( type );
        type.addChild( c1 );

        final String drl = GuidedDecisionTreeDRLPersistence.getInstance().marshal( model );
        assertEqualsIgnoreWhitespace( expected,
                                      drl );
    }

    @Test
    public void testValue_Integer() throws Exception {
        final String expected = "rule \"test_0\"" +
                "when\n" +
                "  Person( integerField == 1000000 )\n" +
                "then\n" +
                "end";

        final GuidedDecisionTree model = new GuidedDecisionTree();
        model.setTreeName( "test" );

        final TypeNode type = new TypeNodeImpl( "Person" );
        final ConstraintNode c1 = new ConstraintNodeImpl( "Person",
                                                          "integerField",
                                                          "==",
                                                          new IntegerValue( 1000000 ) );
        model.setRoot( type );
        type.addChild( c1 );

        final String drl = GuidedDecisionTreeDRLPersistence.getInstance().marshal( model );
        assertEqualsIgnoreWhitespace( expected,
                                      drl );
    }

    @Test
    public void testValue_Long() throws Exception {
        final String expected = "rule \"test_0\"" +
                "when\n" +
                "  Person( longField == 1000000 )\n" +
                "then\n" +
                "end";

        final GuidedDecisionTree model = new GuidedDecisionTree();
        model.setTreeName( "test" );

        final TypeNode type = new TypeNodeImpl( "Person" );
        final ConstraintNode c1 = new ConstraintNodeImpl( "Person",
                                                          "longField",
                                                          "==",
                                                          new LongValue( 1000000L ) );
        model.setRoot( type );
        type.addChild( c1 );

        final String drl = GuidedDecisionTreeDRLPersistence.getInstance().marshal( model );
        assertEqualsIgnoreWhitespace( expected,
                                      drl );
    }

    @Test
    public void testValue_Short() throws Exception {
        final String expected = "rule \"test_0\"" +
                "when\n" +
                "  Person( shortField == 1000 )\n" +
                "then\n" +
                "end";

        final GuidedDecisionTree model = new GuidedDecisionTree();
        model.setTreeName( "test" );

        final TypeNode type = new TypeNodeImpl( "Person" );
        final ConstraintNode c1 = new ConstraintNodeImpl( "Person",
                                                          "shortField",
                                                          "==",
                                                          new ShortValue( new Short( "1000" ) ) );
        model.setRoot( type );
        type.addChild( c1 );

        final String drl = GuidedDecisionTreeDRLPersistence.getInstance().marshal( model );
        assertEqualsIgnoreWhitespace( expected,
                                      drl );
    }

    @Test
    public void testValue_String() throws Exception {
        final String expected = "rule \"test_0\"" +
                "when\n" +
                "  Person( stringField == \"Michael\" )\n" +
                "then\n" +
                "end";

        final GuidedDecisionTree model = new GuidedDecisionTree();
        model.setTreeName( "test" );

        final TypeNode type = new TypeNodeImpl( "Person" );
        final ConstraintNode c1 = new ConstraintNodeImpl( "Person",
                                                          "stringField",
                                                          "==",
                                                          new StringValue( "Michael" ) );
        model.setRoot( type );
        type.addChild( c1 );

        final String drl = GuidedDecisionTreeDRLPersistence.getInstance().marshal( model );
        assertEqualsIgnoreWhitespace( expected,
                                      drl );
    }

    @Test
    public void testSingleRule_TypeBinding() throws Exception {
        final String expected = "rule \"test_0\"" +
                "when\n" +
                "  $p : Person( )\n" +
                "then\n" +
                "end";

        final GuidedDecisionTree model = new GuidedDecisionTree();
        model.setTreeName( "test" );

        final TypeNode type = new TypeNodeImpl( "Person" );
        type.setBinding( "$p" );
        model.setRoot( type );

        final String drl = GuidedDecisionTreeDRLPersistence.getInstance().marshal( model );
        assertEqualsIgnoreWhitespace( expected,
                                      drl );
    }

    @Test
    public void testSingleRule_FieldBinding() throws Exception {
        final String expected = "rule \"test_0\"" +
                "when\n" +
                "  Person( $n : name )\n" +
                "then\n" +
                "end";

        final GuidedDecisionTree model = new GuidedDecisionTree();
        model.setTreeName( "test" );

        final TypeNode type = new TypeNodeImpl( "Person" );
        final ConstraintNode c1 = new ConstraintNodeImpl( "Person",
                                                          "name" );
        c1.setBinding( "$n" );
        model.setRoot( type );
        type.addChild( c1 );

        final String drl = GuidedDecisionTreeDRLPersistence.getInstance().marshal( model );
        assertEqualsIgnoreWhitespace( expected,
                                      drl );
    }

    @Test
    public void testSingleRule_SingleConstraintJavaEnum() throws Exception {
        final String expected = "rule \"test_0\"" +
                "when \n" +
                "  Person( name == Names.FRED )\n" +
                "then \n" +
                "end";

        final GuidedDecisionTree model = new GuidedDecisionTree();
        model.setTreeName( "test" );

        final TypeNode type = new TypeNodeImpl( "Person" );
        final ConstraintNode c1 = new ConstraintNodeImpl( "Person",
                                                          "name",
                                                          "==",
                                                          new EnumValue( "Names.FRED" ) );
        model.setRoot( type );
        type.addChild( c1 );

        final String drl = GuidedDecisionTreeDRLPersistence.getInstance().marshal( model );
        assertEqualsIgnoreWhitespace( expected,
                                      drl );
    }

    @Test
    public void testSingleRule_ActionRetract() throws Exception {
        final String expected = "rule \"test_0\"" +
                "when\n" +
                "  $p : Person( )\n" +
                "then\n" +
                "  retract( $p );\n" +
                "end";

        final GuidedDecisionTree model = new GuidedDecisionTree();
        model.setTreeName( "test" );

        final TypeNode type = new TypeNodeImpl( "Person" );
        type.setBinding( "$p" );
        model.setRoot( type );

        final ActionRetractNode action = new ActionRetractNodeImpl( type );
        type.addChild( action );

        final String drl = GuidedDecisionTreeDRLPersistence.getInstance().marshal( model );
        assertEqualsIgnoreWhitespace( expected,
                                      drl );
    }

    @Test
    public void testSingleRule_ActionRetractWithConstraint() throws Exception {
        final String expected = "rule \"test_0\"" +
                "when\n" +
                "  $p : Person( $n : name )\n" +
                "then\n" +
                "  retract( $p );\n" +
                "end";

        final GuidedDecisionTree model = new GuidedDecisionTree();
        model.setTreeName( "test" );

        final TypeNode type = new TypeNodeImpl( "Person" );
        type.setBinding( "$p" );
        final ConstraintNode c1 = new ConstraintNodeImpl( "Person",
                                                          "name" );
        c1.setBinding( "$n" );
        model.setRoot( type );
        type.addChild( c1 );

        final ActionRetractNode action = new ActionRetractNodeImpl( type );
        c1.addChild( action );

        final String drl = GuidedDecisionTreeDRLPersistence.getInstance().marshal( model );
        assertEqualsIgnoreWhitespace( expected,
                                      drl );
    }

    @Test
    public void testSingleRule_ActionModify() throws Exception {
        final String expected = "rule \"test_0\"" +
                "when\n" +
                "  $p : Person( )\n" +
                "then\n" +
                "  modify( $p ) {\n" +
                "    setAge( 25 )\n" +
                "  }\n" +
                "end";

        final GuidedDecisionTree model = new GuidedDecisionTree();
        model.setTreeName( "test" );

        final TypeNode type = new TypeNodeImpl( "Person" );
        type.setBinding( "$p" );
        model.setRoot( type );

        final ActionUpdateNode action = new ActionUpdateNodeImpl( type );
        action.setModify( true );
        action.getFieldValues().add( new ActionFieldValueImpl( "age",
                                                               new IntegerValue( 25 ) ) );
        type.addChild( action );

        final String drl = GuidedDecisionTreeDRLPersistence.getInstance().marshal( model );
        assertEqualsIgnoreWhitespace( expected,
                                      drl );
    }

    @Test
    public void testSingleRule_ActionModifyWithConstraint() throws Exception {
        final String expected = "rule \"test_0\"" +
                "when\n" +
                "  $p : Person( $n : name )\n" +
                "then\n" +
                "  modify( $p ) {\n" +
                "    setAge( 25 )\n" +
                "  }\n" +
                "end";

        final GuidedDecisionTree model = new GuidedDecisionTree();
        model.setTreeName( "test" );

        final TypeNode type = new TypeNodeImpl( "Person" );
        type.setBinding( "$p" );
        final ConstraintNode c1 = new ConstraintNodeImpl( "Person",
                                                          "name" );
        c1.setBinding( "$n" );
        model.setRoot( type );
        type.addChild( c1 );

        final ActionUpdateNode action = new ActionUpdateNodeImpl( type );
        action.setModify( true );
        action.getFieldValues().add( new ActionFieldValueImpl( "age",
                                                               new IntegerValue( 25 ) ) );
        c1.addChild( action );

        final String drl = GuidedDecisionTreeDRLPersistence.getInstance().marshal( model );
        assertEqualsIgnoreWhitespace( expected,
                                      drl );
    }

    @Test
    public void testSingleRule_ActionModifyActionRetract() throws Exception {
        final String expected = "rule \"test_0\"" +
                "when\n" +
                "  $p : Person( )\n" +
                "then\n" +
                "  modify( $p ) {\n" +
                "    setAge( 25 )\n" +
                "  }\n" +
                "  retract( $p );\n" +
                "end";

        final GuidedDecisionTree model = new GuidedDecisionTree();
        model.setTreeName( "test" );

        final TypeNode type = new TypeNodeImpl( "Person" );
        type.setBinding( "$p" );
        model.setRoot( type );

        final ActionUpdateNode action1 = new ActionUpdateNodeImpl( type );
        action1.setModify( true );
        action1.getFieldValues().add( new ActionFieldValueImpl( "age",
                                                                new IntegerValue( 25 ) ) );
        type.addChild( action1 );

        final ActionRetractNode action2 = new ActionRetractNodeImpl( type );
        action1.addChild( action2 );

        final String drl = GuidedDecisionTreeDRLPersistence.getInstance().marshal( model );
        assertEqualsIgnoreWhitespace( expected,
                                      drl );
    }

    @Test
    public void testSingleRule_ActionModifyMultipleFields() throws Exception {
        final String expected = "rule \"test_0\"" +
                "when\n" +
                "  $p : Person( )\n" +
                "then\n" +
                "  modify( $p ) {\n" +
                "    setAge( 25 ), \n" +
                "    setName( \"Michael\" )\n" +
                "  }\n" +
                "end";

        final GuidedDecisionTree model = new GuidedDecisionTree();
        model.setTreeName( "test" );

        final TypeNode type = new TypeNodeImpl( "Person" );
        type.setBinding( "$p" );
        model.setRoot( type );

        final ActionUpdateNode action = new ActionUpdateNodeImpl( type );
        action.setModify( true );
        action.getFieldValues().add( new ActionFieldValueImpl( "age",
                                                               new IntegerValue( 25 ) ) );
        action.getFieldValues().add( new ActionFieldValueImpl( "name",
                                                               new StringValue( "Michael" ) ) );
        type.addChild( action );

        final String drl = GuidedDecisionTreeDRLPersistence.getInstance().marshal( model );
        assertEqualsIgnoreWhitespace( expected,
                                      drl );
    }

    @Test
    public void testSingleRule_ActionModifyZeroFields() throws Exception {
        final String expected = "rule \"test_0\"" +
                "when\n" +
                "  $p : Person( )\n" +
                "then\n" +
                "end";

        final GuidedDecisionTree model = new GuidedDecisionTree();
        model.setTreeName( "test" );

        final TypeNode type = new TypeNodeImpl( "Person" );
        type.setBinding( "$p" );
        model.setRoot( type );

        final ActionUpdateNode action = new ActionUpdateNodeImpl( type );
        action.setModify( true );
        type.addChild( action );

        final String drl = GuidedDecisionTreeDRLPersistence.getInstance().marshal( model );
        assertEqualsIgnoreWhitespace( expected,
                                      drl );
    }

    @Test
    public void testSingleRule_ActionModifyDateFieldValue() throws Exception {
        final String expected = "rule \"test_0\"" +
                "when\n" +
                "  $p : Person()\n" +
                "then\n" +
                "  java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(\"dd-MMM-yyyy\");\n" +
                "  modify( $p ) {\n" +
                "    setDateOfBirth( sdf.parse(\"15-Jul-1985\") )\n" +
                "  }\n" +
                "end";

        final GuidedDecisionTree model = new GuidedDecisionTree();
        model.setTreeName( "test" );

        final TypeNode type = new TypeNodeImpl( "Person" );
        type.setBinding( "$p" );
        model.setRoot( type );

        final ActionUpdateNode action = new ActionUpdateNodeImpl( type );
        action.setModify( true );
        final Calendar dob = Calendar.getInstance();
        dob.set( Calendar.YEAR,
                 1985 );
        dob.set( Calendar.MONTH,
                 6 );
        dob.set( Calendar.DATE,
                 15 );
        action.getFieldValues().add( new ActionFieldValueImpl( "dateOfBirth",
                                                               new DateValue( dob.getTime() ) ) );
        type.addChild( action );

        final String drl = GuidedDecisionTreeDRLPersistence.getInstance().marshal( model );
        assertEqualsIgnoreWhitespace( expected,
                                      drl );
    }

    @Test
    public void testSingleRule_ActionSet() throws Exception {
        final String expected = "rule \"test_0\"" +
                "when\n" +
                "  $p : Person( )\n" +
                "then\n" +
                "  $p.setAge( 25 );\n" +
                "end";

        final GuidedDecisionTree model = new GuidedDecisionTree();
        model.setTreeName( "test" );

        final TypeNode type = new TypeNodeImpl( "Person" );
        type.setBinding( "$p" );
        model.setRoot( type );

        final ActionUpdateNode action = new ActionUpdateNodeImpl( type );
        action.setModify( false );
        action.getFieldValues().add( new ActionFieldValueImpl( "age",
                                                               new IntegerValue( 25 ) ) );
        type.addChild( action );

        final String drl = GuidedDecisionTreeDRLPersistence.getInstance().marshal( model );
        assertEqualsIgnoreWhitespace( expected,
                                      drl );
    }

    @Test
    public void testSingleRule_ActionSetWithConstraint() throws Exception {
        final String expected = "rule \"test_0\"" +
                "when\n" +
                "  $p : Person( $n : name )\n" +
                "then\n" +
                "  $p.setAge( 25 );\n" +
                "end";

        final GuidedDecisionTree model = new GuidedDecisionTree();
        model.setTreeName( "test" );

        final TypeNode type = new TypeNodeImpl( "Person" );
        type.setBinding( "$p" );
        final ConstraintNode c1 = new ConstraintNodeImpl( "Person",
                                                          "name" );
        c1.setBinding( "$n" );
        model.setRoot( type );
        type.addChild( c1 );

        final ActionUpdateNode action = new ActionUpdateNodeImpl( type );
        action.setModify( false );
        action.getFieldValues().add( new ActionFieldValueImpl( "age",
                                                               new IntegerValue( 25 ) ) );
        c1.addChild( action );

        final String drl = GuidedDecisionTreeDRLPersistence.getInstance().marshal( model );
        assertEqualsIgnoreWhitespace( expected,
                                      drl );
    }

    @Test
    public void testSingleRule_ActionSetActionRetract() throws Exception {
        final String expected = "rule \"test_0\"" +
                "when\n" +
                "  $p : Person( )\n" +
                "then\n" +
                "  $p.setAge( 25 );\n" +
                "  retract( $p );\n" +
                "end";

        final GuidedDecisionTree model = new GuidedDecisionTree();
        model.setTreeName( "test" );

        final TypeNode type = new TypeNodeImpl( "Person" );
        type.setBinding( "$p" );
        model.setRoot( type );

        final ActionUpdateNode action1 = new ActionUpdateNodeImpl( type );
        action1.setModify( false );
        action1.getFieldValues().add( new ActionFieldValueImpl( "age",
                                                                new IntegerValue( 25 ) ) );
        type.addChild( action1 );

        final ActionRetractNode action2 = new ActionRetractNodeImpl( type );
        action1.addChild( action2 );

        final String drl = GuidedDecisionTreeDRLPersistence.getInstance().marshal( model );
        assertEqualsIgnoreWhitespace( expected,
                                      drl );
    }

    @Test
    public void testSingleRule_ActionSetMultipleFields() throws Exception {
        final String expected = "rule \"test_0\"" +
                "when\n" +
                "  $p : Person( )\n" +
                "then\n" +
                "  $p.setAge( 25 );\n" +
                "  $p.setName( \"Michael\" );\n" +
                "end";

        final GuidedDecisionTree model = new GuidedDecisionTree();
        model.setTreeName( "test" );

        final TypeNode type = new TypeNodeImpl( "Person" );
        type.setBinding( "$p" );
        model.setRoot( type );

        final ActionUpdateNode action = new ActionUpdateNodeImpl( type );
        action.setModify( false );
        action.getFieldValues().add( new ActionFieldValueImpl( "age",
                                                               new IntegerValue( 25 ) ) );
        action.getFieldValues().add( new ActionFieldValueImpl( "name",
                                                               new StringValue( "Michael" ) ) );
        type.addChild( action );

        final String drl = GuidedDecisionTreeDRLPersistence.getInstance().marshal( model );
        assertEqualsIgnoreWhitespace( expected,
                                      drl );
    }

    @Test
    public void testSingleRule_ActionSetZeroFields() throws Exception {
        final String expected = "rule \"test_0\"" +
                "when\n" +
                "  $p : Person( )\n" +
                "then\n" +
                "end";

        final GuidedDecisionTree model = new GuidedDecisionTree();
        model.setTreeName( "test" );

        final TypeNode type = new TypeNodeImpl( "Person" );
        type.setBinding( "$p" );
        model.setRoot( type );

        final ActionUpdateNode action = new ActionUpdateNodeImpl( type );
        action.setModify( false );
        type.addChild( action );

        final String drl = GuidedDecisionTreeDRLPersistence.getInstance().marshal( model );
        assertEqualsIgnoreWhitespace( expected,
                                      drl );
    }

    @Test
    public void testSingleRule_ActionSetDateFieldValue() throws Exception {
        final String expected = "rule \"test_0\"" +
                "when\n" +
                "  $p : Person()\n" +
                "then\n" +
                "  java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(\"dd-MMM-yyyy\");\n" +
                "  $p.setDateOfBirth( sdf.parse(\"15-Jul-1985\") );\n" +
                "end";

        final GuidedDecisionTree model = new GuidedDecisionTree();
        model.setTreeName( "test" );

        final TypeNode type = new TypeNodeImpl( "Person" );
        type.setBinding( "$p" );
        model.setRoot( type );

        final ActionUpdateNode action = new ActionUpdateNodeImpl( type );
        action.setModify( false );
        final Calendar dob = Calendar.getInstance();
        dob.set( Calendar.YEAR,
                 1985 );
        dob.set( Calendar.MONTH,
                 6 );
        dob.set( Calendar.DATE,
                 15 );
        action.getFieldValues().add( new ActionFieldValueImpl( "dateOfBirth",
                                                               new DateValue( dob.getTime() ) ) );
        type.addChild( action );

        final String drl = GuidedDecisionTreeDRLPersistence.getInstance().marshal( model );
        assertEqualsIgnoreWhitespace( expected,
                                      drl );
    }

    @Test
    public void testSingleRule_ActionInsert() throws Exception {
        final String expected = "rule \"test_0\"" +
                "when\n" +
                "  Person( )\n" +
                "then\n" +
                "  Person $var0 = new Person();\n" +
                "  $var0.setAge( 25 );\n" +
                "  insert( $var0 );\n" +
                "end";

        final GuidedDecisionTree model = new GuidedDecisionTree();
        model.setTreeName( "test" );

        final TypeNode type = new TypeNodeImpl( "Person" );
        model.setRoot( type );

        final ActionInsertNode action = new ActionInsertNodeImpl( "Person" );
        action.setLogicalInsertion( false );
        action.getFieldValues().add( new ActionFieldValueImpl( "age",
                                                               new IntegerValue( 25 ) ) );
        type.addChild( action );

        final String drl = GuidedDecisionTreeDRLPersistence.getInstance().marshal( model );
        assertEqualsIgnoreWhitespace( expected,
                                      drl );
    }

    @Test
    public void testSingleRule_ActionInsertLogical() throws Exception {
        final String expected = "rule \"test_0\"" +
                "when\n" +
                "  Person( )\n" +
                "then\n" +
                "  Person $var0 = new Person();\n" +
                "  $var0.setAge( 25 );\n" +
                "  insertLogical( $var0 );\n" +
                "end";

        final GuidedDecisionTree model = new GuidedDecisionTree();
        model.setTreeName( "test" );

        final TypeNode type = new TypeNodeImpl( "Person" );
        model.setRoot( type );

        final ActionInsertNode action = new ActionInsertNodeImpl( "Person" );
        action.setLogicalInsertion( true );
        action.getFieldValues().add( new ActionFieldValueImpl( "age",
                                                               new IntegerValue( 25 ) ) );
        type.addChild( action );

        final String drl = GuidedDecisionTreeDRLPersistence.getInstance().marshal( model );
        assertEqualsIgnoreWhitespace( expected,
                                      drl );
    }

    @Test
    public void testSingleRule_ActionInsertDateFieldValue() throws Exception {
        final String expected = "rule \"test_0\"" +
                "when\n" +
                "  Person( )\n" +
                "then\n" +
                "  java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(\"dd-MMM-yyyy\");\n" +
                "  Person $var0 = new Person();\n" +
                "  $var0.setDateOfBirth( sdf.parse(\"15-Jul-1985\") );\n" +
                "  insert( $var0 );\n" +
                "end";

        final GuidedDecisionTree model = new GuidedDecisionTree();
        model.setTreeName( "test" );

        final TypeNode type = new TypeNodeImpl( "Person" );
        model.setRoot( type );

        final ActionInsertNode action = new ActionInsertNodeImpl( "Person" );
        action.setLogicalInsertion( false );
        final Calendar dob = Calendar.getInstance();
        dob.set( Calendar.YEAR,
                 1985 );
        dob.set( Calendar.MONTH,
                 6 );
        dob.set( Calendar.DATE,
                 15 );
        action.getFieldValues().add( new ActionFieldValueImpl( "dateOfBirth",
                                                               new DateValue( dob.getTime() ) ) );
        type.addChild( action );

        final String drl = GuidedDecisionTreeDRLPersistence.getInstance().marshal( model );
        assertEqualsIgnoreWhitespace( expected,
                                      drl );
    }

    @Test
    public void testSingleRule_ActionInsertLogicalDateFieldValue() throws Exception {
        final String expected = "rule \"test_0\"" +
                "when\n" +
                "  Person( )\n" +
                "then\n" +
                "  java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(\"dd-MMM-yyyy\");\n" +
                "  Person $var0 = new Person();\n" +
                "  $var0.setDateOfBirth( sdf.parse(\"15-Jul-1985\") );\n" +
                "  insertLogical( $var0 );\n" +
                "end";

        final GuidedDecisionTree model = new GuidedDecisionTree();
        model.setTreeName( "test" );

        final TypeNode type = new TypeNodeImpl( "Person" );
        model.setRoot( type );

        final ActionInsertNode action = new ActionInsertNodeImpl( "Person" );
        action.setLogicalInsertion( true );
        final Calendar dob = Calendar.getInstance();
        dob.set( Calendar.YEAR,
                 1985 );
        dob.set( Calendar.MONTH,
                 6 );
        dob.set( Calendar.DATE,
                 15 );
        action.getFieldValues().add( new ActionFieldValueImpl( "dateOfBirth",
                                                               new DateValue( dob.getTime() ) ) );
        type.addChild( action );

        final String drl = GuidedDecisionTreeDRLPersistence.getInstance().marshal( model );
        assertEqualsIgnoreWhitespace( expected,
                                      drl );
    }

    private void assertEqualsIgnoreWhitespace( final String expected,
                                               final String actual ) {
        final String cleanExpected = expected.replaceAll( "\\s+",
                                                          "" );
        final String cleanActual = actual.replaceAll( "\\s+",
                                                      "" );

        assertEquals( cleanExpected,
                      cleanActual );
    }

}
