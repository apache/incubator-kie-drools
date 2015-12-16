/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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

import org.drools.workbench.models.datamodel.oracle.DataType;
import org.drools.workbench.models.guided.dtree.shared.model.GuidedDecisionTree;
import org.drools.workbench.models.guided.dtree.shared.model.nodes.TypeNode;
import org.drools.workbench.models.guided.dtree.shared.model.nodes.impl.TypeNodeImpl;
import org.drools.workbench.models.guided.dtree.shared.model.parser.messages.AmbiguousRootParserMessage;
import org.drools.workbench.models.guided.dtree.shared.model.parser.messages.BindingNotFoundParserMessage;
import org.drools.workbench.models.guided.dtree.shared.model.parser.messages.DataTypeConversionErrorParserMessage;
import org.drools.workbench.models.guided.dtree.shared.model.parser.messages.DataTypeNotFoundParserMessage;
import org.drools.workbench.models.guided.dtree.shared.model.parser.messages.UnsupportedFieldConstraintParserMessage;
import org.drools.workbench.models.guided.dtree.shared.model.parser.messages.UnsupportedFieldConstraintTypeParserMessage;
import org.drools.workbench.models.guided.dtree.shared.model.parser.messages.UnsupportedFieldNatureTypeParserMessage;
import org.drools.workbench.models.guided.dtree.shared.model.parser.messages.UnsupportedIActionParserMessage;
import org.junit.Test;

import static org.junit.Assert.*;

public class GuidedDecisionTreeDRLPersistenceUnmarshallingMessagesTest extends AbstractGuidedDecisionTreeDRLPersistenceUnmarshallingTest {

    @Test
    public void testSingleRule_UnsupportedFieldConstraintExpression() throws Exception {
        final String drl = "rule \"test_0\"\n" +
                "when \n" +
                "  Person( this.name == \"Michael\" )\n" +
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
        addModelField( "Person",
                       "name",
                       String.class.getName(),
                       DataType.TYPE_STRING );

        final GuidedDecisionTree model = getAndTestUnmarshalledModel( drl, "test", 1 );

        assertEquals( expected.getTreeName(),
                      model.getTreeName() );
        assertNull( model.getRoot() );

        assertEquals( "test_0",
                      model.getParserErrors().get( 0 ).getOriginalRuleName() );
        assertEqualsIgnoreWhitespace( drl,
                                      model.getParserErrors().get( 0 ).getOriginalDrl() );
        assertNotNull( model.getParserErrors().get( 0 ).getMessages() );
        assertEquals( 1,
                      model.getParserErrors().get( 0 ).getMessages().size() );
        assertTrue( model.getParserErrors().get( 0 ).getMessages().get( 0 ) instanceof UnsupportedFieldConstraintParserMessage );

        final String drl2 = GuidedDecisionTreeDRLPersistence.getInstance().marshal( model );
        assertEqualsIgnoreWhitespace( drl,
                                      drl2 );
    }

    @Test
    public void testSingleRule_UnsupportedFieldConstraintCompositeFieldConstraint() throws Exception {
        final String drl = "rule \"test_0\"\n" +
                "when \n" +
                "  Person( name == \"Michael\" || name == \"John\" )\n" +
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
        addModelField( "Person",
                       "name",
                       String.class.getName(),
                       DataType.TYPE_STRING );

        final GuidedDecisionTree model = getAndTestUnmarshalledModel( drl, "test", 1 );

        assertEquals( expected.getTreeName(),
                      model.getTreeName() );

        assertNull( model.getRoot() );

        assertEquals( "test_0",
                      model.getParserErrors().get( 0 ).getOriginalRuleName() );
        assertEqualsIgnoreWhitespace( drl,
                                      model.getParserErrors().get( 0 ).getOriginalDrl() );
        assertNotNull( model.getParserErrors().get( 0 ).getMessages() );
        assertEquals( 1,
                      model.getParserErrors().get( 0 ).getMessages().size() );
        assertTrue( model.getParserErrors().get( 0 ).getMessages().get( 0 ) instanceof UnsupportedFieldConstraintParserMessage );

        final String drl2 = GuidedDecisionTreeDRLPersistence.getInstance().marshal( model );
        assertEqualsIgnoreWhitespace( drl,
                                      drl2 );
    }

    @Test
    public void testSingleRule_UnsupportedFieldConstraintType() throws Exception {
        final String drl = "rule \"test_0\"\n" +
                "when \n" +
                "  Person( age == (25 + 10) )\n" +
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
        addModelField( "Person",
                       "age",
                       Integer.class.getName(),
                       DataType.TYPE_NUMERIC_INTEGER );

        final GuidedDecisionTree model = getAndTestUnmarshalledModel( drl, "test", 1 );

        assertEquals( expected.getTreeName(),
                      model.getTreeName() );
        assertNull( model.getRoot() );

        assertEquals( "test_0",
                      model.getParserErrors().get( 0 ).getOriginalRuleName() );
        assertEqualsIgnoreWhitespace( drl,
                                      model.getParserErrors().get( 0 ).getOriginalDrl() );
        assertNotNull( model.getParserErrors().get( 0 ).getMessages() );
        assertEquals( 1,
                      model.getParserErrors().get( 0 ).getMessages().size() );
        assertTrue( model.getParserErrors().get( 0 ).getMessages().get( 0 ) instanceof UnsupportedFieldConstraintTypeParserMessage );

        final String drl2 = GuidedDecisionTreeDRLPersistence.getInstance().marshal( model );
        assertEqualsIgnoreWhitespace( drl,
                                      drl2 );
    }

    @Test
    public void testSingleRule_UnsupportedFieldNatureType() throws Exception {
        final String drl = "rule \"test_0\"\n" +
                "when \n" +
                "  $p : Person()\n" +
                "then \n" +
                "modify( $p ) { \n" +
                "  setAge( 25 + 10 ) \n" +
                "} \n" +
                "end";

        final GuidedDecisionTree expected = new GuidedDecisionTree();
        expected.setTreeName( "test" );

        final TypeNode type = new TypeNodeImpl( "Person" );
        expected.setRoot( type );

        addModelField( "Person",
                       "this",
                       "Person",
                       DataType.TYPE_THIS );
        addModelField( "Person",
                       "age",
                       Integer.class.getName(),
                       DataType.TYPE_NUMERIC_INTEGER );

        final GuidedDecisionTree model = getAndTestUnmarshalledModel( drl, "test", 1 );

        assertEquals( expected.getTreeName(),
                      model.getTreeName() );

        assertNull( model.getRoot() );

        assertEquals( "test_0",
                      model.getParserErrors().get( 0 ).getOriginalRuleName() );
        assertEqualsIgnoreWhitespace( drl,
                                      model.getParserErrors().get( 0 ).getOriginalDrl() );
        assertNotNull( model.getParserErrors().get( 0 ).getMessages() );
        assertEquals( 1,
                      model.getParserErrors().get( 0 ).getMessages().size() );
        assertTrue( model.getParserErrors().get( 0 ).getMessages().get( 0 ) instanceof UnsupportedFieldNatureTypeParserMessage );

        final String drl2 = GuidedDecisionTreeDRLPersistence.getInstance().marshal( model );
        assertEqualsIgnoreWhitespace( drl,
                                      drl2 );
    }

    @Test
    public void testSingleRule_BindingNotFoundWithModify() throws Exception {
        final String drl = "rule \"test_0\"\n" +
                "when \n" +
                "  $p : Person()\n" +
                "then \n" +
                "modify( $p2 ) { \n" +
                "  setAge( 25 + 10 ) \n" +
                "} \n" +
                "end";

        final GuidedDecisionTree expected = new GuidedDecisionTree();
        expected.setTreeName( "test" );

        final TypeNode type = new TypeNodeImpl( "Person" );
        expected.setRoot( type );

        addModelField( "Person",
                       "this",
                       "Person",
                       DataType.TYPE_THIS );
        addModelField( "Person",
                       "age",
                       Integer.class.getName(),
                       DataType.TYPE_NUMERIC_INTEGER );

        final GuidedDecisionTree model = getAndTestUnmarshalledModel( drl, "test", 1 );

        assertEquals( expected.getTreeName(),
                      model.getTreeName() );

        assertNull( model.getRoot() );

        assertEquals( "test_0",
                      model.getParserErrors().get( 0 ).getOriginalRuleName() );
        assertEqualsIgnoreWhitespace( drl,
                                      model.getParserErrors().get( 0 ).getOriginalDrl() );
        assertNotNull( model.getParserErrors().get( 0 ).getMessages() );
        assertEquals( 1,
                      model.getParserErrors().get( 0 ).getMessages().size() );
        assertTrue( model.getParserErrors().get( 0 ).getMessages().get( 0 ) instanceof BindingNotFoundParserMessage );

        final String drl2 = GuidedDecisionTreeDRLPersistence.getInstance().marshal( model );
        assertEqualsIgnoreWhitespace( drl,
                                      drl2 );
    }

    @Test
    public void testSingleRule_BindingNotFoundWithUpdate() throws Exception {
        final String drl = "rule \"test_0\"\n" +
                "when \n" +
                "  $p : Person()\n" +
                "then \n" +
                "  $p2.setAge( 25 + 10 ); \n" +
                "end";

        final GuidedDecisionTree expected = new GuidedDecisionTree();
        expected.setTreeName( "test" );

        final TypeNode type = new TypeNodeImpl( "Person" );
        expected.setRoot( type );

        addModelField( "Person",
                       "this",
                       "Person",
                       DataType.TYPE_THIS );
        addModelField( "Person",
                       "age",
                       Integer.class.getName(),
                       DataType.TYPE_NUMERIC_INTEGER );

        final GuidedDecisionTree model = getAndTestUnmarshalledModel( drl, "test", 1 );

        assertEquals( expected.getTreeName(),
                      model.getTreeName() );

        assertNull( model.getRoot() );

        assertEquals( "test_0",
                      model.getParserErrors().get( 0 ).getOriginalRuleName() );
        assertEqualsIgnoreWhitespace( drl,
                                      model.getParserErrors().get( 0 ).getOriginalDrl() );
        assertNotNull( model.getParserErrors().get( 0 ).getMessages() );
        assertEquals( 1,
                      model.getParserErrors().get( 0 ).getMessages().size() );
        assertTrue( model.getParserErrors().get( 0 ).getMessages().get( 0 ) instanceof UnsupportedIActionParserMessage );

        final String drl2 = GuidedDecisionTreeDRLPersistence.getInstance().marshal( model );
        assertEqualsIgnoreWhitespace( drl,
                                      drl2 );
    }

    @Test
    public void testSingleRule_DataTypeNotFound() throws Exception {
        final String drl = "rule \"test_0\"\n" +
                "when \n" +
                "  Person( name == \"Michael\" )\n" +
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

        final GuidedDecisionTree model = getAndTestUnmarshalledModel( drl, "test", 1 );

        assertEquals( expected.getTreeName(),
                      model.getTreeName() );

        assertNull( model.getRoot() );

        assertEquals( "test_0",
                      model.getParserErrors().get( 0 ).getOriginalRuleName() );
        assertEqualsIgnoreWhitespace( drl,
                                      model.getParserErrors().get( 0 ).getOriginalDrl() );
        assertNotNull( model.getParserErrors().get( 0 ).getMessages() );
        assertEquals( 1,
                      model.getParserErrors().get( 0 ).getMessages().size() );
        assertTrue( model.getParserErrors().get( 0 ).getMessages().get( 0 ) instanceof DataTypeNotFoundParserMessage );

        final String drl2 = GuidedDecisionTreeDRLPersistence.getInstance().marshal( model );
        assertEqualsIgnoreWhitespace( drl,
                                      drl2 );
    }

    @Test
    public void testValue_BigDecimal() throws Exception {
        final String drl = "rule \"test_0\"\n" +
                "when \n" +
                "  Person( bigDecimalField == \"abc\" )\n" +
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
        addModelField( "Person",
                       "bigDecimalField",
                       BigDecimal.class.getName(),
                       DataType.TYPE_NUMERIC_BIGDECIMAL );

        final GuidedDecisionTree model = getAndTestUnmarshalledModel( drl, "test", 1 );

        assertEquals( expected.getTreeName(),
                      model.getTreeName() );

        assertNull( model.getRoot() );

        assertEquals( "test_0",
                      model.getParserErrors().get( 0 ).getOriginalRuleName() );
        assertEqualsIgnoreWhitespace( drl,
                                      model.getParserErrors().get( 0 ).getOriginalDrl() );
        assertNotNull( model.getParserErrors().get( 0 ).getMessages() );
        assertEquals( 1,
                      model.getParserErrors().get( 0 ).getMessages().size() );
        assertTrue( model.getParserErrors().get( 0 ).getMessages().get( 0 ) instanceof DataTypeConversionErrorParserMessage );

        final String drl2 = GuidedDecisionTreeDRLPersistence.getInstance().marshal( model );
        assertEqualsIgnoreWhitespace( drl,
                                      drl2 );
    }

    @Test
    public void testValue_BigInteger() throws Exception {
        final String drl = "rule \"test_0\"\n" +
                "when \n" +
                "  Person( bigIntegerField == \"abc\" )\n" +
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
        addModelField( "Person",
                       "bigIntegerField",
                       BigInteger.class.getName(),
                       DataType.TYPE_NUMERIC_BIGINTEGER );

        final GuidedDecisionTree model = getAndTestUnmarshalledModel( drl, "test", 1 );
        assertEquals( expected.getTreeName(),
                      model.getTreeName() );

        assertNull( model.getRoot() );

        assertEquals( "test_0",
                      model.getParserErrors().get( 0 ).getOriginalRuleName() );
        assertEqualsIgnoreWhitespace( drl,
                                      model.getParserErrors().get( 0 ).getOriginalDrl() );
        assertNotNull( model.getParserErrors().get( 0 ).getMessages() );
        assertEquals( 1,
                      model.getParserErrors().get( 0 ).getMessages().size() );
        assertTrue( model.getParserErrors().get( 0 ).getMessages().get( 0 ) instanceof DataTypeConversionErrorParserMessage );

        final String drl2 = GuidedDecisionTreeDRLPersistence.getInstance().marshal( model );
        assertEqualsIgnoreWhitespace( drl,
                                      drl2 );
    }

    @Test
    public void testValue_Boolean() throws Exception {
        final String drl = "rule \"test_0\"\n" +
                "when \n" +
                "  Person( booleanField == \"abc\" )\n" +
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
        addModelField( "Person",
                       "booleanField",
                       Boolean.class.getName(),
                       DataType.TYPE_BOOLEAN );

        final GuidedDecisionTree model = getAndTestUnmarshalledModel( drl, "test", 1 );
        assertEquals( expected.getTreeName(),
                      model.getTreeName() );

        assertNull( model.getRoot() );

        assertEquals( "test_0",
                      model.getParserErrors().get( 0 ).getOriginalRuleName() );
        assertEqualsIgnoreWhitespace( drl,
                                      model.getParserErrors().get( 0 ).getOriginalDrl() );
        assertNotNull( model.getParserErrors().get( 0 ).getMessages() );
        assertEquals( 1,
                      model.getParserErrors().get( 0 ).getMessages().size() );
        assertTrue( model.getParserErrors().get( 0 ).getMessages().get( 0 ) instanceof DataTypeConversionErrorParserMessage );

        final String drl2 = GuidedDecisionTreeDRLPersistence.getInstance().marshal( model );
        assertEqualsIgnoreWhitespace( drl,
                                      drl2 );
    }

    @Test
    public void testValue_Byte() throws Exception {
        final String drl = "rule \"test_0\"\n" +
                "when \n" +
                "  Person( byteField == \"abc\" )\n" +
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
        addModelField( "Person",
                       "byteField",
                       Byte.class.getName(),
                       DataType.TYPE_NUMERIC_BYTE );

        final GuidedDecisionTree model = getAndTestUnmarshalledModel( drl, "test", 1 );
        assertEquals( expected.getTreeName(),
                      model.getTreeName() );

        assertNull( model.getRoot() );

        assertEquals( "test_0",
                      model.getParserErrors().get( 0 ).getOriginalRuleName() );
        assertEqualsIgnoreWhitespace( drl,
                                      model.getParserErrors().get( 0 ).getOriginalDrl() );
        assertNotNull( model.getParserErrors().get( 0 ).getMessages() );
        assertEquals( 1,
                      model.getParserErrors().get( 0 ).getMessages().size() );
        assertTrue( model.getParserErrors().get( 0 ).getMessages().get( 0 ) instanceof DataTypeConversionErrorParserMessage );

        final String drl2 = GuidedDecisionTreeDRLPersistence.getInstance().marshal( model );
        assertEqualsIgnoreWhitespace( drl,
                                      drl2 );
    }

    @Test
    public void testValue_Date() throws Exception {
        final String drl = "rule \"test_0\"\n" +
                "when \n" +
                "  Person( dateField == \"abc\" )\n" +
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
        addModelField( "Person",
                       "dateField",
                       Date.class.getName(),
                       DataType.TYPE_DATE );

        final GuidedDecisionTree model = getAndTestUnmarshalledModel( drl, "test", 1 );

        assertNotNull( model );
        assertEquals( expected.getTreeName(),
                      model.getTreeName() );

        assertNull( model.getRoot() );

        assertEquals( "test_0",
                      model.getParserErrors().get( 0 ).getOriginalRuleName() );
        assertEqualsIgnoreWhitespace( drl,
                                      model.getParserErrors().get( 0 ).getOriginalDrl() );
        assertNotNull( model.getParserErrors().get( 0 ).getMessages() );
        assertEquals( 1,
                      model.getParserErrors().get( 0 ).getMessages().size() );
        assertTrue( model.getParserErrors().get( 0 ).getMessages().get( 0 ) instanceof DataTypeConversionErrorParserMessage );

        final String drl2 = GuidedDecisionTreeDRLPersistence.getInstance().marshal( model );
        assertEqualsIgnoreWhitespace( drl,
                                      drl2 );
    }

    @Test
    public void testValue_Double() throws Exception {
        final String drl = "rule \"test_0\"\n" +
                "when \n" +
                "  Person( doubleField == \"abc\" )\n" +
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
        addModelField( "Person",
                       "doubleField",
                       Double.class.getName(),
                       DataType.TYPE_NUMERIC_DOUBLE );

        final GuidedDecisionTree model = getAndTestUnmarshalledModel( drl, "test", 1 );

        assertNotNull( model );
        assertEquals( expected.getTreeName(),
                      model.getTreeName() );

        assertNull( model.getRoot() );

        assertEquals( "test_0",
                      model.getParserErrors().get( 0 ).getOriginalRuleName() );
        assertEqualsIgnoreWhitespace( drl,
                                      model.getParserErrors().get( 0 ).getOriginalDrl() );
        assertNotNull( model.getParserErrors().get( 0 ).getMessages() );
        assertEquals( 1,
                      model.getParserErrors().get( 0 ).getMessages().size() );
        assertTrue( model.getParserErrors().get( 0 ).getMessages().get( 0 ) instanceof DataTypeConversionErrorParserMessage );

        final String drl2 = GuidedDecisionTreeDRLPersistence.getInstance().marshal( model );
        assertEqualsIgnoreWhitespace( drl,
                                      drl2 );
    }

    @Test
    public void testValue_Float() throws Exception {
        final String drl = "rule \"test_0\"\n" +
                "when \n" +
                "  Person( floatField == \"abc\" )\n" +
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
        addModelField( "Person",
                       "floatField",
                       Float.class.getName(),
                       DataType.TYPE_NUMERIC_FLOAT );

        final GuidedDecisionTree model = getAndTestUnmarshalledModel( drl, "test", 1 );

        assertEquals( expected.getTreeName(),
                      model.getTreeName() );

        assertNull( model.getRoot() );

        assertEquals( "test_0",
                      model.getParserErrors().get( 0 ).getOriginalRuleName() );
        assertEqualsIgnoreWhitespace( drl,
                                      model.getParserErrors().get( 0 ).getOriginalDrl() );
        assertNotNull( model.getParserErrors().get( 0 ).getMessages() );
        assertEquals( 1,
                      model.getParserErrors().get( 0 ).getMessages().size() );
        assertTrue( model.getParserErrors().get( 0 ).getMessages().get( 0 ) instanceof DataTypeConversionErrorParserMessage );

        final String drl2 = GuidedDecisionTreeDRLPersistence.getInstance().marshal( model );
        assertEqualsIgnoreWhitespace( drl,
                                      drl2 );
    }

    @Test
    public void testValue_Integer() throws Exception {
        final String drl = "rule \"test_0\"\n" +
                "when \n" +
                "  Person( integerField == \"abc\" )\n" +
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
        addModelField( "Person",
                       "integerField",
                       Integer.class.getName(),
                       DataType.TYPE_NUMERIC_INTEGER );

        final GuidedDecisionTree model = getAndTestUnmarshalledModel( drl, "test", 1 );
        assertEquals( expected.getTreeName(),
                      model.getTreeName() );

        assertNull( model.getRoot() );

        assertEquals( "test_0",
                      model.getParserErrors().get( 0 ).getOriginalRuleName() );
        assertEqualsIgnoreWhitespace( drl,
                                      model.getParserErrors().get( 0 ).getOriginalDrl() );
        assertNotNull( model.getParserErrors().get( 0 ).getMessages() );
        assertEquals( 1,
                      model.getParserErrors().get( 0 ).getMessages().size() );
        assertTrue( model.getParserErrors().get( 0 ).getMessages().get( 0 ) instanceof DataTypeConversionErrorParserMessage );

        final String drl2 = GuidedDecisionTreeDRLPersistence.getInstance().marshal( model );
        assertEqualsIgnoreWhitespace( drl,
                                      drl2 );
    }

    @Test
    public void testValue_Long() throws Exception {
        final String drl = "rule \"test_0\"\n" +
                "when \n" +
                "  Person( longField == \"abc\" )\n" +
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
        addModelField( "Person",
                       "longField",
                       Long.class.getName(),
                       DataType.TYPE_NUMERIC_LONG );

        final GuidedDecisionTree model = getAndTestUnmarshalledModel( drl, "test", 1 );
        assertEquals( expected.getTreeName(),
                      model.getTreeName() );

        assertNull( model.getRoot() );

        assertEquals( "test_0",
                      model.getParserErrors().get( 0 ).getOriginalRuleName() );
        assertEqualsIgnoreWhitespace( drl,
                                      model.getParserErrors().get( 0 ).getOriginalDrl() );
        assertNotNull( model.getParserErrors().get( 0 ).getMessages() );
        assertEquals( 1,
                      model.getParserErrors().get( 0 ).getMessages().size() );
        assertTrue( model.getParserErrors().get( 0 ).getMessages().get( 0 ) instanceof DataTypeConversionErrorParserMessage );

        final String drl2 = GuidedDecisionTreeDRLPersistence.getInstance().marshal( model );
        assertEqualsIgnoreWhitespace( drl,
                                      drl2 );
    }

    @Test
    public void testValue_Short() throws Exception {
        final String drl = "rule \"test_0\"\n" +
                "when \n" +
                "  Person( shortField == \"abc\" )\n" +
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
        addModelField( "Person",
                       "shortField",
                       Short.class.getName(),
                       DataType.TYPE_NUMERIC_SHORT );

        final GuidedDecisionTree model = getAndTestUnmarshalledModel( drl, "test", 1 );
        assertEquals( expected.getTreeName(),
                      model.getTreeName() );

        assertNull( model.getRoot() );

        assertEquals( "test_0",
                      model.getParserErrors().get( 0 ).getOriginalRuleName() );
        assertEqualsIgnoreWhitespace( drl,
                                      model.getParserErrors().get( 0 ).getOriginalDrl() );
        assertNotNull( model.getParserErrors().get( 0 ).getMessages() );
        assertEquals( 1,
                      model.getParserErrors().get( 0 ).getMessages().size() );
        assertTrue( model.getParserErrors().get( 0 ).getMessages().get( 0 ) instanceof DataTypeConversionErrorParserMessage );

        final String drl2 = GuidedDecisionTreeDRLPersistence.getInstance().marshal( model );
        assertEqualsIgnoreWhitespace( drl,
                                      drl2 );
    }

    @Test
    public void testAmbiguousRoot() throws Exception {
        final String drl1 = "rule \"test_0\"\n" +
                "when \n" +
                "  Person( )\n" +
                "then \n" +
                "end \n";
        final String drl2 = "rule \"test_1\"\n" +
                "when \n" +
                "  Cheese( )\n" +
                "then \n" +
                "end \n";

        final GuidedDecisionTree expected = new GuidedDecisionTree();
        expected.setTreeName( "test" );

        final TypeNode type = new TypeNodeImpl( "Person" );
        expected.setRoot( type );

        addModelField( "Person",
                       "this",
                       "Person",
                       DataType.TYPE_THIS );
        addModelField( "Cheese",
                       "this",
                       "Cheese",
                       DataType.TYPE_THIS );

        final GuidedDecisionTree model = getAndTestUnmarshalledModel( drl1 + drl2, "test", 1 );

        assertEquals( expected.getTreeName(),
                      model.getTreeName() );

        assertNotNull( model.getRoot() );
        assertEquals( type.getClassName(),
                      model.getRoot().getClassName() );
        assertFalse( model.getRoot().isBound() );

        assertEquals( 0,
                      model.getRoot().getChildren().size() );

        assertEquals( "test_1",
                      model.getParserErrors().get( 0 ).getOriginalRuleName() );
        assertEqualsIgnoreWhitespace( drl2,
                                      model.getParserErrors().get( 0 ).getOriginalDrl() );
        assertNotNull( model.getParserErrors().get( 0 ).getMessages() );
        assertEquals( 1,
                      model.getParserErrors().get( 0 ).getMessages().size() );
        assertTrue( model.getParserErrors().get( 0 ).getMessages().get( 0 ) instanceof AmbiguousRootParserMessage );

        final String drl3 = GuidedDecisionTreeDRLPersistence.getInstance().marshal( model );
        assertEqualsIgnoreWhitespace( drl1 + drl2,
                                      drl3 );
    }
}
