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
import java.util.Date;

import org.drools.workbench.models.guided.dtree.shared.model.GuidedDecisionTree;
import org.drools.workbench.models.guided.dtree.shared.model.nodes.ConstraintNode;
import org.drools.workbench.models.guided.dtree.shared.model.nodes.TypeNode;
import org.drools.workbench.models.guided.dtree.shared.model.nodes.impl.ConstraintNodeImpl;
import org.drools.workbench.models.guided.dtree.shared.model.nodes.impl.TypeNodeImpl;
import org.drools.workbench.models.guided.dtree.shared.model.values.impl.BigDecimalValue;
import org.drools.workbench.models.guided.dtree.shared.model.values.impl.BigIntegerValue;
import org.drools.workbench.models.guided.dtree.shared.model.values.impl.BooleanValue;
import org.drools.workbench.models.guided.dtree.shared.model.values.impl.ByteValue;
import org.drools.workbench.models.guided.dtree.shared.model.values.impl.DateValue;
import org.drools.workbench.models.guided.dtree.shared.model.values.impl.DoubleValue;
import org.drools.workbench.models.guided.dtree.shared.model.values.impl.FloatValue;
import org.drools.workbench.models.guided.dtree.shared.model.values.impl.IntegerValue;
import org.drools.workbench.models.guided.dtree.shared.model.values.impl.LongValue;
import org.drools.workbench.models.guided.dtree.shared.model.values.impl.ShortValue;
import org.drools.workbench.models.guided.dtree.shared.model.values.impl.StringValue;
import org.junit.Test;

import static org.junit.Assert.*;

public class GuidedDecisionTreeDRLPersistenceTest {

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
        type.getChildren().add( c1 );

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
        type.getChildren().add( c1 );
        c1.getChildren().add( c2 );

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
        type.getChildren().add( c1 );
        type.getChildren().add( c2 );

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
        type.getChildren().add( c1 );
        type.getChildren().add( c2 );
        type.getChildren().add( c3 );

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
        type.getChildren().add( c1 );
        type.getChildren().add( c2 );
        c2.getChildren().add( c3 );

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
        type1.getChildren().add( c1 );
        c1.getChildren().add( type2 );
        type2.getChildren().add( c2 );

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
        type1.getChildren().add( c1a );
        type1.getChildren().add( c1b );
        c1a.getChildren().add( type2 );
        type2.getChildren().add( c2a );
        type2.getChildren().add( c2b );

        final String drl = GuidedDecisionTreeDRLPersistence.getInstance().marshal( model );
        assertEqualsIgnoreWhitespace( expected,
                                      drl );
    }

    @Test
    public void testValue_BigDecimal() throws Exception {
        final String expected = "rule \"test_0\"" +
                "when\n" +
                "  Person( bigDecimalField == 1000000 )\n" +
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
        type.getChildren().add( c1 );

        final String drl = GuidedDecisionTreeDRLPersistence.getInstance().marshal( model );
        assertEqualsIgnoreWhitespace( expected,
                                      drl );
    }

    @Test
    public void testValue_BigInteger() throws Exception {
        final String expected = "rule \"test_0\"" +
                "when\n" +
                "  Person( bigIntegerField == 1000000 )\n" +
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
        type.getChildren().add( c1 );

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
        type.getChildren().add( c1 );

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
        type.getChildren().add( c1 );

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
        type.getChildren().add( c1 );

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
        type.getChildren().add( c1 );

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
        type.getChildren().add( c1 );

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
        type.getChildren().add( c1 );

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
        type.getChildren().add( c1 );

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
        type.getChildren().add( c1 );

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
        type.getChildren().add( c1 );

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
