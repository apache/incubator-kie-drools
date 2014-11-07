package org.drools.workbench.models.commons.backend.rule;
/*
 * Copyright 2013 JBoss Inc
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.workbench.models.datamodel.oracle.DataType;
import org.drools.workbench.models.datamodel.oracle.FieldAccessorsAndMutators;
import org.drools.workbench.models.datamodel.oracle.MethodInfo;
import org.drools.workbench.models.datamodel.oracle.ModelField;
import org.drools.workbench.models.datamodel.oracle.PackageDataModelOracle;
import org.drools.workbench.models.datamodel.rule.ActionCallMethod;
import org.drools.workbench.models.datamodel.rule.ActionFieldValue;
import org.drools.workbench.models.datamodel.rule.ActionGlobalCollectionAdd;
import org.drools.workbench.models.datamodel.rule.ActionInsertFact;
import org.drools.workbench.models.datamodel.rule.ActionSetField;
import org.drools.workbench.models.datamodel.rule.ActionUpdateField;
import org.drools.workbench.models.datamodel.rule.BaseSingleFieldConstraint;
import org.drools.workbench.models.datamodel.rule.CEPWindow;
import org.drools.workbench.models.datamodel.rule.CompositeFactPattern;
import org.drools.workbench.models.datamodel.rule.CompositeFieldConstraint;
import org.drools.workbench.models.datamodel.rule.ConnectiveConstraint;
import org.drools.workbench.models.datamodel.rule.DSLComplexVariableValue;
import org.drools.workbench.models.datamodel.rule.DSLSentence;
import org.drools.workbench.models.datamodel.rule.DSLVariableValue;
import org.drools.workbench.models.datamodel.rule.ExpressionCollection;
import org.drools.workbench.models.datamodel.rule.ExpressionField;
import org.drools.workbench.models.datamodel.rule.ExpressionFormLine;
import org.drools.workbench.models.datamodel.rule.ExpressionMethod;
import org.drools.workbench.models.datamodel.rule.ExpressionPart;
import org.drools.workbench.models.datamodel.rule.ExpressionText;
import org.drools.workbench.models.datamodel.rule.ExpressionUnboundFact;
import org.drools.workbench.models.datamodel.rule.ExpressionVariable;
import org.drools.workbench.models.datamodel.rule.FactPattern;
import org.drools.workbench.models.datamodel.rule.FieldConstraint;
import org.drools.workbench.models.datamodel.rule.FieldNatureType;
import org.drools.workbench.models.datamodel.rule.FreeFormLine;
import org.drools.workbench.models.datamodel.rule.FromAccumulateCompositeFactPattern;
import org.drools.workbench.models.datamodel.rule.FromCollectCompositeFactPattern;
import org.drools.workbench.models.datamodel.rule.FromCompositeFactPattern;
import org.drools.workbench.models.datamodel.rule.IAction;
import org.drools.workbench.models.datamodel.rule.IPattern;
import org.drools.workbench.models.datamodel.rule.RuleAttribute;
import org.drools.workbench.models.datamodel.rule.RuleModel;
import org.drools.workbench.models.datamodel.rule.SingleFieldConstraint;
import org.drools.workbench.models.datamodel.rule.SingleFieldConstraintEBLeftSide;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class RuleModelDRLPersistenceUnmarshallingTest {

    private PackageDataModelOracle dmo;
    private Map<String, ModelField[]> packageModelFields = new HashMap<String, ModelField[]>();
    private Map<String, String[]> projectJavaEnumDefinitions = new HashMap<String, String[]>();
    private Map<String, List<MethodInfo>> projectMethodInformation = new HashMap<String, List<MethodInfo>>();

    @Before
    public void setUp() throws Exception {
        dmo = mock( PackageDataModelOracle.class );
        when( dmo.getProjectModelFields() ).thenReturn( packageModelFields );
        when( dmo.getProjectJavaEnumDefinitions() ).thenReturn( projectJavaEnumDefinitions );
        when( dmo.getProjectMethodInformation() ).thenReturn( projectMethodInformation );
    }

    @After
    public void cleanUp() throws Exception {
        packageModelFields.clear();
        projectJavaEnumDefinitions.clear();
        projectMethodInformation.clear();
    }

    private void addModelField( final String factName,
                                final String fieldName,
                                final String clazz,
                                final String type ) {
        ModelField[] modelFields = new ModelField[ 1 ];
        modelFields[ 0 ] = new ModelField( fieldName,
                                           clazz,
                                           ModelField.FIELD_CLASS_TYPE.TYPE_DECLARATION_CLASS,
                                           ModelField.FIELD_ORIGIN.DECLARED,
                                           FieldAccessorsAndMutators.BOTH,
                                           type );
        if ( packageModelFields.containsKey( factName ) ) {
            final List<ModelField> existingModelFields = new ArrayList<ModelField>( Arrays.asList( packageModelFields.get( factName ) ) );
            existingModelFields.add( modelFields[ 0 ] );
            modelFields = existingModelFields.toArray( modelFields );
        }
        packageModelFields.put( factName,
                                modelFields );
    }

    private void addJavaEnumDefinition( final String factName,
                                        final String fieldName,
                                        final String[] values ) {
        final String key = factName + "#" + fieldName;
        projectJavaEnumDefinitions.put( key,
                                        values );
    }

    private void addMethodInformation( final String factName,
                                       final String name,
                                       final List<String> params,
                                       final String returnType,
                                       final String parametricReturnType,
                                       final String genericType ) {
        MethodInfo mi = new MethodInfo( name,
                                        params,
                                        returnType,
                                        parametricReturnType,
                                        genericType );

        List<MethodInfo> existingMethodInfo = projectMethodInformation.get( factName );
        if ( existingMethodInfo == null ) {
            existingMethodInfo = new ArrayList<MethodInfo>();
            projectMethodInformation.put( factName,
                                          existingMethodInfo );
        }
        existingMethodInfo.add( mi );
    }

    @Test
    public void testFactPattern() {
        String drl = "rule \"rule1\"\n"
                + "when\n"
                + "Applicant()\n"
                + "then\n"
                + "end";

        RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal( drl,
                                                                           Collections.EMPTY_LIST,
                                                                           dmo );

        assertNotNull( m );
        assertEquals( "rule1",
                      m.name );

        assertEquals( 1,
                      m.lhs.length );
        IPattern p = m.lhs[ 0 ];
        assertTrue( p instanceof FactPattern );

        FactPattern fp = (FactPattern) p;
        assertEquals( "Applicant",
                      fp.getFactType() );

        assertEquals( 0,
                      fp.getNumberOfConstraints() );
    }

    @Test
    public void testFactPatternWithBinding() {
        String drl = "rule \"rule1\"\n"
                + "when\n"
                + "$a : Applicant()\n"
                + "then\n"
                + "end";

        RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal( drl,
                                                                           Collections.EMPTY_LIST,
                                                                           dmo );

        assertNotNull( m );
        assertEquals( "rule1",
                      m.name );

        assertEquals( 1,
                      m.lhs.length );
        IPattern p = m.lhs[ 0 ];
        assertTrue( p instanceof FactPattern );

        FactPattern fp = (FactPattern) p;
        assertEquals( "Applicant",
                      fp.getFactType() );
        assertEquals( "$a",
                      fp.getBoundName() );

        assertEquals( 0,
                      fp.getNumberOfConstraints() );
    }

    @Test
    public void testSingleFieldConstraint() {
        String drl = "rule \"rule1\"\n"
                + "when\n"
                + "Applicant( age < 55 )\n"
                + "then\n"
                + "end";

        RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal( drl,
                                                                           Collections.EMPTY_LIST,
                                                                           dmo );

        assertNotNull( m );
        assertEquals( "rule1",
                      m.name );

        assertEquals( 1,
                      m.lhs.length );
        IPattern p = m.lhs[ 0 ];
        assertTrue( p instanceof FactPattern );

        FactPattern fp = (FactPattern) p;
        assertEquals( "Applicant",
                      fp.getFactType() );

        assertEquals( 1,
                      fp.getConstraintList().getConstraints().length );
        assertTrue( fp.getConstraint( 0 ) instanceof SingleFieldConstraint );

        SingleFieldConstraint sfp = (SingleFieldConstraint) fp.getConstraint( 0 );
        assertEquals( "Applicant",
                      sfp.getFactType() );
        assertEquals( "age",
                      sfp.getFieldName() );
        assertEquals( "<",
                      sfp.getOperator() );
        assertEquals( "55",
                      sfp.getValue() );
        assertEquals( BaseSingleFieldConstraint.TYPE_LITERAL,
                      sfp.getConstraintValueType() );
    }

    @Test
    public void testSingleFieldConstraintWithBinding() {
        String drl = "rule \"rule1\"\n"
                + "when\n"
                + "Applicant( $a : age < 55 )\n"
                + "then\n"
                + "end";

        RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal( drl,
                                                                           Collections.EMPTY_LIST,
                                                                           dmo );

        assertNotNull( m );
        assertEquals( "rule1",
                      m.name );

        assertEquals( 1,
                      m.lhs.length );
        IPattern p = m.lhs[ 0 ];
        assertTrue( p instanceof FactPattern );

        FactPattern fp = (FactPattern) p;
        assertEquals( "Applicant",
                      fp.getFactType() );

        assertEquals( 1,
                      fp.getConstraintList().getConstraints().length );
        assertTrue( fp.getConstraint( 0 ) instanceof SingleFieldConstraint );

        SingleFieldConstraint sfp = (SingleFieldConstraint) fp.getConstraint( 0 );
        assertEquals( "Applicant",
                      sfp.getFactType() );
        assertEquals( "age",
                      sfp.getFieldName() );
        assertEquals( "<",
                      sfp.getOperator() );
        assertEquals( "55",
                      sfp.getValue() );
        assertEquals( BaseSingleFieldConstraint.TYPE_LITERAL,
                      sfp.getConstraintValueType() );
        assertEquals( "$a",
                      sfp.getFieldBinding() );
    }

    @Test
    public void testSingleFieldConstraintWithTwoFieldsBinding() {
        String drl = "rule \"rule1\"\n"
                + "when\n"
                + "Applicant( $a : age, $n : name )\n"
                + "then\n"
                + "end";

        RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal( drl,
                                                                           Collections.EMPTY_LIST,
                                                                           dmo );

        assertNotNull( m );
        assertEquals( "rule1",
                      m.name );

        assertEquals( 1,
                      m.lhs.length );
        IPattern p = m.lhs[ 0 ];
        assertTrue( p instanceof FactPattern );

        FactPattern fp = (FactPattern) p;
        assertEquals( "Applicant",
                      fp.getFactType() );

        assertEquals( 2,
                      fp.getConstraintList().getConstraints().length );
        assertTrue( fp.getConstraint( 0 ) instanceof SingleFieldConstraint );

        SingleFieldConstraint sfp0 = (SingleFieldConstraint) fp.getConstraint( 0 );
        assertEquals( "Applicant",
                      sfp0.getFactType() );
        assertEquals( "age",
                      sfp0.getFieldName() );
        assertEquals( BaseSingleFieldConstraint.TYPE_UNDEFINED,
                      sfp0.getConstraintValueType() );
        assertEquals( "$a",
                      sfp0.getFieldBinding() );

        assertTrue( fp.getConstraint( 1 ) instanceof SingleFieldConstraint );

        SingleFieldConstraint sfp1 = (SingleFieldConstraint) fp.getConstraint( 1 );
        assertEquals( "Applicant",
                      sfp1.getFactType() );
        assertEquals( "name",
                      sfp1.getFieldName() );
        assertEquals( BaseSingleFieldConstraint.TYPE_UNDEFINED,
                      sfp1.getConstraintValueType() );
        assertEquals( "$n",
                      sfp1.getFieldBinding() );
    }

    @Test
    public void testCompositeFieldConstraint() {
        String drl = "rule \"rule1\"\n"
                + "when\n"
                + "Applicant( age < 55 || age > 70 )\n"
                + "then\n"
                + "end";

        RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal( drl,
                                                                           Collections.EMPTY_LIST,
                                                                           dmo );

        assertNotNull( m );
        assertEquals( "rule1",
                      m.name );

        assertEquals( 1,
                      m.lhs.length );
        IPattern p = m.lhs[ 0 ];
        assertTrue( p instanceof FactPattern );

        FactPattern fp = (FactPattern) p;
        assertEquals( "Applicant",
                      fp.getFactType() );

        assertEquals( 1,
                      fp.getConstraintList().getConstraints().length );
        assertTrue( fp.getConstraint( 0 ) instanceof CompositeFieldConstraint );

        CompositeFieldConstraint cfp = (CompositeFieldConstraint) fp.getConstraint( 0 );
        assertEquals( "||",
                      cfp.getCompositeJunctionType() );
        assertEquals( 2,
                      cfp.getNumberOfConstraints() );
        assertTrue( cfp.getConstraint( 0 ) instanceof SingleFieldConstraint );
        assertTrue( cfp.getConstraint( 1 ) instanceof SingleFieldConstraint );

        SingleFieldConstraint sfp1 = (SingleFieldConstraint) cfp.getConstraint( 0 );
        assertEquals( "Applicant",
                      sfp1.getFactType() );
        assertEquals( "age",
                      sfp1.getFieldName() );
        assertEquals( "<",
                      sfp1.getOperator() );
        assertEquals( "55",
                      sfp1.getValue() );
        assertEquals( BaseSingleFieldConstraint.TYPE_LITERAL,
                      sfp1.getConstraintValueType() );

        SingleFieldConstraint sfp2 = (SingleFieldConstraint) cfp.getConstraint( 1 );
        assertEquals( "Applicant",
                      sfp2.getFactType() );
        assertEquals( "age",
                      sfp2.getFieldName() );
        assertEquals( ">",
                      sfp2.getOperator() );
        assertEquals( "70",
                      sfp2.getValue() );
        assertEquals( BaseSingleFieldConstraint.TYPE_LITERAL,
                      sfp2.getConstraintValueType() );
    }

    @Test
    public void testSingleFieldConstraintIsNullOperator() {
        String drl = "rule \"rule1\"\n"
                + "when\n"
                + "Applicant( age == null )\n"
                + "then\n"
                + "end";

        RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal( drl,
                                                                           Collections.EMPTY_LIST,
                                                                           dmo );

        assertNotNull( m );
        assertEquals( "rule1",
                      m.name );

        assertEquals( 1,
                      m.lhs.length );
        IPattern p = m.lhs[ 0 ];
        assertTrue( p instanceof FactPattern );

        FactPattern fp = (FactPattern) p;
        assertEquals( "Applicant",
                      fp.getFactType() );

        assertEquals( 1,
                      fp.getConstraintList().getConstraints().length );
        assertTrue( fp.getConstraint( 0 ) instanceof SingleFieldConstraint );

        SingleFieldConstraint sfp = (SingleFieldConstraint) fp.getConstraint( 0 );
        assertEquals( "Applicant",
                      sfp.getFactType() );
        assertEquals( "age",
                      sfp.getFieldName() );
        assertEquals( "== null",
                      sfp.getOperator() );
        assertNull( sfp.getValue() );
        assertEquals( BaseSingleFieldConstraint.TYPE_UNDEFINED,
                      sfp.getConstraintValueType() );
    }

    @Test
    public void testSingleFieldConstraintIsNotNullOperator() {
        String drl = "rule \"rule1\"\n"
                + "when\n"
                + "Applicant( age != null )\n"
                + "then\n"
                + "end";

        RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal( drl,
                                                                           Collections.EMPTY_LIST,
                                                                           dmo );

        assertNotNull( m );
        assertEquals( "rule1",
                      m.name );

        assertEquals( 1,
                      m.lhs.length );
        IPattern p = m.lhs[ 0 ];
        assertTrue( p instanceof FactPattern );

        FactPattern fp = (FactPattern) p;
        assertEquals( "Applicant",
                      fp.getFactType() );

        assertEquals( 1,
                      fp.getConstraintList().getConstraints().length );
        assertTrue( fp.getConstraint( 0 ) instanceof SingleFieldConstraint );

        SingleFieldConstraint sfp = (SingleFieldConstraint) fp.getConstraint( 0 );
        assertEquals( "Applicant",
                      sfp.getFactType() );
        assertEquals( "age",
                      sfp.getFieldName() );
        assertEquals( "!= null",
                      sfp.getOperator() );
        assertNull( sfp.getValue() );
        assertEquals( BaseSingleFieldConstraint.TYPE_UNDEFINED,
                      sfp.getConstraintValueType() );
    }

    @Test
    public void testCompositeFieldConstraintWithNotNullOperator() {
        String drl = "rule \"rule1\"\n"
                + "when\n"
                + "Applicant( age != null && age > 70 )\n"
                + "then\n"
                + "end";

        RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal( drl,
                                                                           Collections.EMPTY_LIST,
                                                                           dmo );

        assertNotNull( m );
        assertEquals( "rule1",
                      m.name );

        assertEquals( 1,
                      m.lhs.length );
        IPattern p = m.lhs[ 0 ];
        assertTrue( p instanceof FactPattern );

        FactPattern fp = (FactPattern) p;
        assertEquals( "Applicant",
                      fp.getFactType() );

        assertEquals( 1,
                      fp.getConstraintList().getConstraints().length );
        assertTrue( fp.getConstraint( 0 ) instanceof CompositeFieldConstraint );

        CompositeFieldConstraint cfp = (CompositeFieldConstraint) fp.getConstraint( 0 );
        assertEquals( "&&",
                      cfp.getCompositeJunctionType() );
        assertEquals( 2,
                      cfp.getNumberOfConstraints() );
        assertTrue( cfp.getConstraint( 0 ) instanceof SingleFieldConstraint );
        assertTrue( cfp.getConstraint( 1 ) instanceof SingleFieldConstraint );

        SingleFieldConstraint sfp1 = (SingleFieldConstraint) cfp.getConstraint( 0 );
        assertEquals( "Applicant",
                      sfp1.getFactType() );
        assertEquals( "age",
                      sfp1.getFieldName() );
        assertEquals( "!= null",
                      sfp1.getOperator() );
        assertNull( sfp1.getValue() );
        assertEquals( BaseSingleFieldConstraint.TYPE_UNDEFINED,
                      sfp1.getConstraintValueType() );

        SingleFieldConstraint sfp2 = (SingleFieldConstraint) cfp.getConstraint( 1 );
        assertEquals( "Applicant",
                      sfp2.getFactType() );
        assertEquals( "age",
                      sfp2.getFieldName() );
        assertEquals( ">",
                      sfp2.getOperator() );
        assertEquals( "70",
                      sfp2.getValue() );
        assertEquals( BaseSingleFieldConstraint.TYPE_LITERAL,
                      sfp2.getConstraintValueType() );
    }

    @Test
    public void testSingleFieldConstraintCEPOperator() {
        String drl = "rule \"rule1\"\n"
                + "when\n"
                + "Applicant( dob after \"26-Jun-2013\" )\n"
                + "then\n"
                + "end";

        RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal( drl,
                                                                           Collections.EMPTY_LIST,
                                                                           dmo );

        assertNotNull( m );
        assertEquals( "rule1",
                      m.name );

        assertEquals( 1,
                      m.lhs.length );
        IPattern p = m.lhs[ 0 ];
        assertTrue( p instanceof FactPattern );

        FactPattern fp = (FactPattern) p;
        assertEquals( "Applicant",
                      fp.getFactType() );

        assertEquals( 1,
                      fp.getNumberOfConstraints() );
        assertTrue( fp.getConstraint( 0 ) instanceof SingleFieldConstraint );

        SingleFieldConstraint sfp = (SingleFieldConstraint) fp.getConstraint( 0 );
        assertEquals( "Applicant",
                      sfp.getFactType() );
        assertEquals( "dob",
                      sfp.getFieldName() );
        assertEquals( "after",
                      sfp.getOperator() );
        assertEquals( "26-Jun-2013",
                      sfp.getValue() );
        assertEquals( BaseSingleFieldConstraint.TYPE_LITERAL,
                      sfp.getConstraintValueType() );
    }

    @Test
    public void testSingleFieldConstraintCEPOperator1Parameter() {
        String drl = "rule \"rule1\"\n"
                + "when\n"
                + "$e : Event()\n"
                + "Event( this after[1d] $e )\n"
                + "then\n"
                + "end";

        RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal( drl,
                                                                           Collections.EMPTY_LIST,
                                                                           dmo );

        assertNotNull( m );
        assertEquals( "rule1",
                      m.name );

        assertEquals( 2,
                      m.lhs.length );

        IPattern p1 = m.lhs[ 0 ];
        assertTrue( p1 instanceof FactPattern );
        FactPattern fp1 = (FactPattern) p1;
        assertEquals( "Event",
                      fp1.getFactType() );
        assertEquals( "$e",
                      fp1.getBoundName() );
        assertEquals( 0,
                      fp1.getNumberOfConstraints() );

        IPattern p2 = m.lhs[ 1 ];
        assertTrue( p2 instanceof FactPattern );
        FactPattern fp2 = (FactPattern) p2;
        assertEquals( "Event",
                      fp2.getFactType() );
        assertNull( fp2.getBoundName() );
        assertEquals( 1,
                      fp2.getNumberOfConstraints() );
        SingleFieldConstraint sfp = (SingleFieldConstraint) fp2.getConstraint( 0 );
        assertEquals( "Event",
                      sfp.getFactType() );
        assertEquals( "this",
                      sfp.getFieldName() );
        assertEquals( "after",
                      sfp.getOperator() );
        assertEquals( "$e",
                      sfp.getValue() );
        assertEquals( BaseSingleFieldConstraint.TYPE_VARIABLE,
                      sfp.getConstraintValueType() );
        assertEquals( 3,
                      sfp.getParameters().size() );
        assertEquals( "1d",
                      sfp.getParameter( "0" ) );
        assertEquals( "1",
                      sfp.getParameter( "org.drools.workbench.models.commons.backend.rule.visibleParameterSet" ) );
        assertEquals( "org.drools.workbench.models.commons.backend.rule.CEPOperatorParameterDRLBuilder",
                      sfp.getParameter( "org.drools.workbench.models.commons.backend.rule.operatorParameterGenerator" ) );
    }

    @Test
    public void testReciprocal_SingleFieldConstraintCEPOperator1Parameter() {
        //This is the inverse of "SingleFieldConstraintCEPOperator1Parameter"
        String drl = "rule \"rule1\"\n"
                + "dialect \"mvel\"\n"
                + "when\n"
                + "$e : Event()\n"
                + "Event( this after[1d] $e )\n"
                + "then\n"
                + "end";

        RuleModel m = new RuleModel();
        m.name = "rule1";

        FactPattern fp1 = new FactPattern();
        fp1.setFactType( "Event" );
        fp1.setBoundName( "$e" );

        FactPattern fp2 = new FactPattern();
        fp2.setFactType( "Event" );

        SingleFieldConstraint sfp = new SingleFieldConstraint();
        sfp.setFactType( "Event" );
        sfp.setFieldName( "this" );
        sfp.setOperator( "after" );
        sfp.setValue( "$e" );
        sfp.setConstraintValueType( BaseSingleFieldConstraint.TYPE_VARIABLE );
        sfp.getParameters().put( "0",
                                 "1d" );
        sfp.getParameters().put( "org.drools.workbench.models.commons.backend.rule.visibleParameterSet",
                                 "1" );
        sfp.getParameters().put( "org.drools.workbench.models.commons.backend.rule.operatorParameterGenerator",
                                 "org.drools.workbench.models.commons.backend.rule.CEPOperatorParameterDRLBuilder" );

        fp2.addConstraint( sfp );
        m.addLhsItem( fp1 );
        m.addLhsItem( fp2 );

        String actualDrl = RuleModelDRLPersistenceImpl.getInstance().marshal( m );
        assertEqualsIgnoreWhitespace( drl,
                                      actualDrl );
    }

    @Test
    public void testSingleFieldConstraintCEPOperator2Parameters() {
        String drl = "rule \"rule1\"\n"
                + "when\n"
                + "$e : Event()\n"
                + "Event( this after[1d, 2d] $e )\n"
                + "then\n"
                + "end";

        RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal( drl,
                                                                           Collections.EMPTY_LIST,
                                                                           dmo );

        assertNotNull( m );
        assertEquals( "rule1",
                      m.name );

        assertEquals( 2,
                      m.lhs.length );

        IPattern p1 = m.lhs[ 0 ];
        assertTrue( p1 instanceof FactPattern );
        FactPattern fp1 = (FactPattern) p1;
        assertEquals( "Event",
                      fp1.getFactType() );
        assertEquals( "$e",
                      fp1.getBoundName() );
        assertEquals( 0,
                      fp1.getNumberOfConstraints() );

        IPattern p2 = m.lhs[ 1 ];
        assertTrue( p2 instanceof FactPattern );
        FactPattern fp2 = (FactPattern) p2;
        assertEquals( "Event",
                      fp2.getFactType() );
        assertNull( fp2.getBoundName() );
        assertEquals( 1,
                      fp2.getNumberOfConstraints() );
        SingleFieldConstraint sfp = (SingleFieldConstraint) fp2.getConstraint( 0 );
        assertEquals( "Event",
                      sfp.getFactType() );
        assertEquals( "this",
                      sfp.getFieldName() );
        assertEquals( "after",
                      sfp.getOperator() );
        assertEquals( "$e",
                      sfp.getValue() );
        assertEquals( BaseSingleFieldConstraint.TYPE_VARIABLE,
                      sfp.getConstraintValueType() );
        assertEquals( 4,
                      sfp.getParameters().size() );
        assertEquals( "1d",
                      sfp.getParameter( "0" ) );
        assertEquals( "2d",
                      sfp.getParameter( "1" ) );
        assertEquals( "2",
                      sfp.getParameter( "org.drools.workbench.models.commons.backend.rule.visibleParameterSet" ) );
        assertEquals( "org.drools.workbench.models.commons.backend.rule.CEPOperatorParameterDRLBuilder",
                      sfp.getParameter( "org.drools.workbench.models.commons.backend.rule.operatorParameterGenerator" ) );
    }

    @Test
    public void testReciprocal_SingleFieldConstraintCEPOperator2Parameters() {
        //This is the inverse of "SingleFieldConstraintCEPOperator2Parameters"
        String drl = "rule \"rule1\"\n"
                + "dialect \"mvel\"\n"
                + "when\n"
                + "$e : Event()\n"
                + "Event( this after[1d, 2d] $e )\n"
                + "then\n"
                + "end";

        RuleModel m = new RuleModel();
        m.name = "rule1";

        FactPattern fp1 = new FactPattern();
        fp1.setFactType( "Event" );
        fp1.setBoundName( "$e" );

        FactPattern fp2 = new FactPattern();
        fp2.setFactType( "Event" );

        SingleFieldConstraint sfp = new SingleFieldConstraint();
        sfp.setFactType( "Event" );
        sfp.setFieldName( "this" );
        sfp.setOperator( "after" );
        sfp.setValue( "$e" );
        sfp.setConstraintValueType( BaseSingleFieldConstraint.TYPE_VARIABLE );
        sfp.getParameters().put( "0",
                                 "1d" );
        sfp.getParameters().put( "1",
                                 "2d" );
        sfp.getParameters().put( "org.drools.workbench.models.commons.backend.rule.visibleParameterSet",
                                 "2" );
        sfp.getParameters().put( "org.drools.workbench.models.commons.backend.rule.operatorParameterGenerator",
                                 "org.drools.workbench.models.commons.backend.rule.CEPOperatorParameterDRLBuilder" );

        fp2.addConstraint( sfp );
        m.addLhsItem( fp1 );
        m.addLhsItem( fp2 );

        String actualDrl = RuleModelDRLPersistenceImpl.getInstance().marshal( m );
        assertEqualsIgnoreWhitespace( drl,
                                      actualDrl );
    }

    @Test
    public void testSingleFieldConstraintCEPOperatorTimeWindow() {
        String drl = "rule \"rule1\"\n"
                + "when\n"
                + "Event() over window:time (1d)\n"
                + "then\n"
                + "end";

        RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal( drl,
                                                                           Collections.EMPTY_LIST,
                                                                           dmo );

        assertNotNull( m );
        assertEquals( "rule1",
                      m.name );

        assertEquals( 1,
                      m.lhs.length );

        IPattern p1 = m.lhs[ 0 ];
        assertTrue( p1 instanceof FactPattern );
        FactPattern fp1 = (FactPattern) p1;
        assertEquals( "Event",
                      fp1.getFactType() );
        assertNull( fp1.getBoundName() );
        assertEquals( 0,
                      fp1.getNumberOfConstraints() );

        assertNotNull( fp1.getWindow() );
        CEPWindow window = fp1.getWindow();
        assertEquals( "over window:time",
                      window.getOperator() );
        assertEquals( 2,
                      window.getParameters().size() );
        assertEquals( "1d",
                      window.getParameter( "1" ) );
        assertEquals( "org.drools.workbench.models.commons.backend.rule.CEPWindowOperatorParameterDRLBuilder",
                      window.getParameter( "org.drools.workbench.models.commons.backend.rule.operatorParameterGenerator" ) );
    }

    @Test
    public void testReciprocal_SingleFieldConstraintCEPOperatorTimeWindow() {
        //This is the inverse of "SingleFieldConstraintCEPOperatorTimeWindow"
        String drl = "rule \"rule1\"\n"
                + "dialect \"mvel\"\n"
                + "when\n"
                + "Event() over window:time (1d)\n"
                + "then\n"
                + "end";

        RuleModel m = new RuleModel();
        m.name = "rule1";

        FactPattern fp1 = new FactPattern();
        fp1.setFactType( "Event" );

        CEPWindow window = new CEPWindow();
        window.setOperator( "over window:time" );
        window.getParameters().put( "1",
                                    "1d" );
        window.getParameters().put( "org.drools.workbench.models.commons.backend.rule.operatorParameterGenerator",
                                    "org.drools.workbench.models.commons.backend.rule.CEPWindowOperatorParameterDRLBuilder" );
        fp1.setWindow( window );

        m.addLhsItem( fp1 );

        String actualDrl = RuleModelDRLPersistenceImpl.getInstance().marshal( m );
        assertEqualsIgnoreWhitespace( drl,
                                      actualDrl );
    }

    @Test
    public void testSingleFieldConstraintCEPOperatorTimeLength() {
        String drl = "rule \"rule1\"\n"
                + "when\n"
                + "Event() over window:length (10)\n"
                + "then\n"
                + "end";

        RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal( drl,
                                                                           Collections.EMPTY_LIST,
                                                                           dmo );

        assertNotNull( m );
        assertEquals( "rule1",
                      m.name );

        assertEquals( 1,
                      m.lhs.length );

        IPattern p1 = m.lhs[ 0 ];
        assertTrue( p1 instanceof FactPattern );
        FactPattern fp1 = (FactPattern) p1;
        assertEquals( "Event",
                      fp1.getFactType() );
        assertNull( fp1.getBoundName() );
        assertEquals( 0,
                      fp1.getNumberOfConstraints() );

        assertNotNull( fp1.getWindow() );
        CEPWindow window = fp1.getWindow();
        assertEquals( "over window:length",
                      window.getOperator() );
        assertEquals( 2,
                      window.getParameters().size() );
        assertEquals( "10",
                      window.getParameter( "1" ) );
        assertEquals( "org.drools.workbench.models.commons.backend.rule.CEPWindowOperatorParameterDRLBuilder",
                      window.getParameter( "org.drools.workbench.models.commons.backend.rule.operatorParameterGenerator" ) );
    }

    @Test
    public void testReciprocal_SingleFieldConstraintCEPOperatorTimeLength() {
        //This is the inverse of "SingleFieldConstraintCEPOperatorTimeLength"
        String drl = "rule \"rule1\"\n"
                + "dialect \"mvel\"\n"
                + "when\n"
                + "Event() over window:length (10)\n"
                + "then\n"
                + "end";

        RuleModel m = new RuleModel();
        m.name = "rule1";

        FactPattern fp1 = new FactPattern();
        fp1.setFactType( "Event" );

        CEPWindow window = new CEPWindow();
        window.setOperator( "over window:length" );
        window.getParameters().put( "1",
                                    "10" );
        window.getParameters().put( "org.drools.workbench.models.commons.backend.rule.operatorParameterGenerator",
                                    "org.drools.workbench.models.commons.backend.rule.CEPWindowOperatorParameterDRLBuilder" );
        fp1.setWindow( window );

        m.addLhsItem( fp1 );

        String actualDrl = RuleModelDRLPersistenceImpl.getInstance().marshal( m );
        assertEqualsIgnoreWhitespace( drl,
                                      actualDrl );
    }

    @Test
    public void testExtends() {
        String drl = "rule \"rule1\" extends \"rule2\" \n"
                + "when\n"
                + "then\n"
                + "end";

        RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal( drl,
                                                                           Collections.EMPTY_LIST,
                                                                           dmo );

        assertNotNull( m );
        assertEquals( "rule1",
                      m.name );

        assertEquals( "rule2", m.parentName );
    }

    @Test
    public void testRuleNameWithoutTheQuotes() {
        String drl = "rule rule1\n"
                + "when\n"
                + "then\n"
                + "end";

        RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal( drl,
                                                                           Collections.EMPTY_LIST,
                                                                           dmo );

        assertNotNull( m );
        assertEquals( "rule1",
                      m.name );

    }

    @Test
    public void testMetaData() {
        String drl = "rule rule1\n"
                + "@author( Bob )\n"
                + "when\n"
                + "then\n"
                + "end";

        RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal( drl,
                                                                           Collections.EMPTY_LIST,
                                                                           dmo );

        assertNotNull( m );
        assertEquals( 1,
                      m.metadataList.length );
        assertEquals( "author",
                      m.metadataList[ 0 ].getAttributeName() );
        assertEquals( "Bob",
                      m.metadataList[ 0 ].getValue() );

    }

    @Test
    public void testAttributes() {
        String drl = "rule rule1\n"
                + "salience 42\n"
                + "when\n"
                + "then\n"
                + "end";

        RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal( drl,
                                                                           Collections.EMPTY_LIST,
                                                                           dmo );

        assertNotNull( m );
        assertEquals( 1,
                      m.attributes.length );
        assertEquals( "salience",
                      m.attributes[ 0 ].getAttributeName() );
        assertEquals( "42",
                      m.attributes[ 0 ].getValue() );

    }

    @Test
    public void testEval() {
        String drl = "rule rule1\n"
                + "when\n"
                + "eval( true )\n"
                + "then\n"
                + "end";

        RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal( drl,
                                                                           Collections.EMPTY_LIST,
                                                                           dmo );

        assertNotNull( m );
        assertEquals( 1, m.lhs.length );
        assertTrue( m.lhs[ 0 ] instanceof FreeFormLine );
        assertEquals( "eval( true )", ( (FreeFormLine) m.lhs[ 0 ] ).getText() );
    }

    @Test
    public void testLHSFreeFormLine() {
        String drl = "rule rule1\n"
                + "when\n"
                + "//A comment\n"
                + "then\n"
                + "end";

        RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal( drl,
                                                                           Collections.EMPTY_LIST,
                                                                           dmo );
        assertNotNull( m );
        assertEquals( 1, m.lhs.length );
        assertTrue( m.lhs[ 0 ] instanceof FreeFormLine );
        assertEquals( "//A comment", ( (FreeFormLine) m.lhs[ 0 ] ).getText() );
    }

    @Test
    public void testRHSFreeFormLine() {
        String drl = "rule rule1\n"
                + "when\n"
                + "then\n"
                + "int test = (int)(1-0.8);\n"
                + "System.out.println( \"Hello Mario!\" );\n"
                + "end";

        RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal( drl,
                                                                           Collections.EMPTY_LIST,
                                                                           dmo );
        assertNotNull( m );
        assertEquals( 2, m.rhs.length );
        assertTrue( m.rhs[ 0 ] instanceof FreeFormLine );
        assertEquals( "int test = (int)(1-0.8);", ( (FreeFormLine) m.rhs[ 0 ] ).getText() );
        assertTrue( m.rhs[ 1 ] instanceof FreeFormLine );
        assertEquals( "System.out.println( \"Hello Mario!\" );", ( (FreeFormLine) m.rhs[ 1 ] ).getText() );
    }

    @Test
    public void testLHSFreeFormLineWithDsl() {
        String drl = "rule rule1\n"
                + "when\n"
                + ">//A comment\n"
                + "then\n"
                + "end";

        RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshalUsingDSL( drl,
                                                                                   Collections.EMPTY_LIST,
                                                                                   dmo );
        assertNotNull( m );
        assertEquals( 1, m.lhs.length );
        assertTrue( m.lhs[ 0 ] instanceof FreeFormLine );
        assertEquals( "//A comment", ( (FreeFormLine) m.lhs[ 0 ] ).getText() );
    }

    @Test
    public void testRHSFreeFormLineWithDsl() {
        String drl = "rule rule1\n"
                + "when\n"
                + "then\n"
                + ">int test = (int)(1-0.8);\n"
                + ">System.out.println( \"Hello Mario!\" );\n"
                + "end";

        RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshalUsingDSL( drl,
                                                                                   Collections.EMPTY_LIST,
                                                                                   dmo );
        assertNotNull( m );
        assertEquals( 2, m.rhs.length );
        assertTrue( m.rhs[ 0 ] instanceof FreeFormLine );
        assertEquals( "int test = (int)(1-0.8);", ( (FreeFormLine) m.rhs[ 0 ] ).getText() );
        assertTrue( m.rhs[ 1 ] instanceof FreeFormLine );
        assertEquals( "System.out.println( \"Hello Mario!\" );", ( (FreeFormLine) m.rhs[ 1 ] ).getText() );
    }

    @Test
    public void testVarAssignment() {
        String drl = "rule rule1\n"
                + "when\n"
                + " d : Double()\n"
                + "then\n"
                + "double test = d.doubleValue();\n"
                + "end";

        RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal( drl,
                                                                           Collections.EMPTY_LIST,
                                                                           dmo );
        assertNotNull( m );
        assertEquals( 1, m.rhs.length );
        assertTrue( m.rhs[ 0 ] instanceof FreeFormLine );
        assertEquals( "double test = d.doubleValue();", ( (FreeFormLine) m.rhs[ 0 ] ).getText() );
    }

    @Test
    public void testRHSOrder() {
        String drl =
                "rule \"Low Down Payment based on Appraisal\"\n" +
                        "  dialect \"mvel\"\n" +
                        "  ruleflow-group \"apr-calculation\"\n" +
                        "  salience -3\n" +
                        "  no-loop true\n" +
                        " when\n" +
                        "    appraised : Appraisal( )\n" +
                        "    application : Application( mortgageAmount > ( appraised.value * 8 / 10 ) )\n" +
                        " then\n" +
                        "    double ratio = application.getMortgageAmount().doubleValue() / appraised.getValue().doubleValue();\n" +
                        "    int brackets = (int)((ratio - 0.8) / 0.05);\n" +
                        "    brackets++;\n" +
                        "    double aprSurcharge = 0.75 * brackets;\n" +
                        "    System.out.println( \"aprSurcharge added is \" + aprSurcharge );\n" +
                        "    application.setApr(  application.getApr() + aprSurcharge );\n" +
                        "    System.out.println(\"Executed Rule: \" + drools.getRule().getName() );\n" +
                        "end";

        RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal( drl,
                                                                           Collections.EMPTY_LIST,
                                                                           dmo );
        assertNotNull( m );
        assertEquals( 7, m.rhs.length );
        assertTrue( m.rhs[ 0 ] instanceof FreeFormLine );
        assertTrue( m.rhs[ 1 ] instanceof FreeFormLine );
        assertTrue( m.rhs[ 2 ] instanceof FreeFormLine );
        assertTrue( m.rhs[ 3 ] instanceof FreeFormLine );
        assertTrue( m.rhs[ 4 ] instanceof FreeFormLine );
        assertTrue( m.rhs[ 5 ] instanceof ActionSetField );
        assertTrue( m.rhs[ 6 ] instanceof FreeFormLine );
    }

    @Test
    public void testNestedFieldExpressions() {
        String drl =
                "rule rule1\n"
                        + "when\n"
                        + "Person( address.postalCode == 12345 )\n"
                        + "then\n"
                        + "end";

        addModelField( "org.test.Person",
                       "address",
                       "org.test.Address",
                       "Address" );
        addModelField( "org.test.Address",
                       "postalCode",
                       "java.lang.Integer",
                       "Integer" );

        RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal( drl,
                                                                           Collections.EMPTY_LIST,
                                                                           dmo );

        assertNotNull( m );
        assertEquals( 1, m.lhs.length );
        assertTrue( m.lhs[ 0 ] instanceof FactPattern );
        assertTrue( ( (FactPattern) m.lhs[ 0 ] ).getFieldConstraints()[ 0 ] instanceof SingleFieldConstraintEBLeftSide );

        SingleFieldConstraintEBLeftSide ebLeftSide = (SingleFieldConstraintEBLeftSide) ( (FactPattern) m.lhs[ 0 ] ).getFieldConstraints()[ 0 ];
        assertEquals( "postalCode",
                      ebLeftSide.getFieldName() );
        assertEquals( "java.lang.Integer",
                      ebLeftSide.getFieldType() );
        assertEquals( "==",
                      ebLeftSide.getOperator() );
        assertEquals( "12345",
                      ebLeftSide.getValue() );

        assertEquals( 3,
                      ebLeftSide.getExpressionLeftSide().getParts().size() );
        assertTrue( ebLeftSide.getExpressionLeftSide().getParts().get( 0 ) instanceof ExpressionUnboundFact );
        ExpressionUnboundFact expressionUnboundFact = ( (ExpressionUnboundFact) ebLeftSide.getExpressionLeftSide().getParts().get( 0 ) );
        assertEquals( "Person",
                      expressionUnboundFact.getName() );
        assertEquals( "Person",
                      expressionUnboundFact.getClassType() );
        assertEquals( "Person",
                      expressionUnboundFact.getGenericType() );
        assertEquals( m.lhs[ 0 ],
                      expressionUnboundFact.getFact() );

        assertNull( expressionUnboundFact.getPrevious() );
        assertEquals( ebLeftSide.getExpressionLeftSide().getParts().get( 1 ), expressionUnboundFact.getNext() );

        assertTrue( ebLeftSide.getExpressionLeftSide().getParts().get( 1 ) instanceof ExpressionField );
        ExpressionField expressionField1 = (ExpressionField) ebLeftSide.getExpressionLeftSide().getParts().get( 1 );
        assertEquals( "address",
                      expressionField1.getName() );
        assertEquals( "org.test.Address",
                      expressionField1.getClassType() );
        assertEquals( "Address",
                      expressionField1.getGenericType() );

        assertEquals( ebLeftSide.getExpressionLeftSide().getParts().get( 0 ), expressionField1.getPrevious() );
        assertEquals( ebLeftSide.getExpressionLeftSide().getParts().get( 2 ), expressionField1.getNext() );

        assertTrue( ebLeftSide.getExpressionLeftSide().getParts().get( 2 ) instanceof ExpressionField );
        ExpressionField expressionField2 = (ExpressionField) ebLeftSide.getExpressionLeftSide().getParts().get( 2 );
        assertEquals( "postalCode",
                      expressionField2.getName() );
        assertEquals( "java.lang.Integer",
                      expressionField2.getClassType() );
        assertEquals( DataType.TYPE_NUMERIC_INTEGER,
                      expressionField2.getGenericType() );

        assertEquals( ebLeftSide.getExpressionLeftSide().getParts().get( 1 ), expressionField2.getPrevious() );
        assertNull( expressionField2.getNext() );
    }

    @Test
    public void testNestedFieldExpressionsWithAFunction() {
        String drl =
                "rule rule1\n"
                        + "when\n"
                        + "Person( address.postalCode == myFunction() )\n"
                        + "then\n"
                        + "end";

        addModelField( "org.test.Person",
                       "address",
                       "org.test.Address",
                       "Address" );
        addModelField( "org.test.Address",
                       "postalCode",
                       "java.lang.Integer",
                       "Integer" );

        RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal( drl,
                                                                           Collections.EMPTY_LIST,
                                                                           dmo );

        assertNotNull( m );
        assertEquals( 1, m.lhs.length );
        assertTrue( m.lhs[ 0 ] instanceof FactPattern );
        assertTrue( ( (FactPattern) m.lhs[ 0 ] ).getFieldConstraints()[ 0 ] instanceof SingleFieldConstraintEBLeftSide );

        SingleFieldConstraintEBLeftSide ebLeftSide = (SingleFieldConstraintEBLeftSide) ( (FactPattern) m.lhs[ 0 ] ).getFieldConstraints()[ 0 ];
        assertEquals( "postalCode",
                      ebLeftSide.getFieldName() );
        assertEquals( "java.lang.Integer",
                      ebLeftSide.getFieldType() );
        assertEquals( "==",
                      ebLeftSide.getOperator() );
        assertEquals( "myFunction()",
                      ebLeftSide.getValue() );

        assertEquals( 3, ebLeftSide.getExpressionLeftSide().getParts().size() );
        assertTrue( ebLeftSide.getExpressionLeftSide().getParts().get( 0 ) instanceof ExpressionUnboundFact );
        ExpressionUnboundFact expressionUnboundFact = ( (ExpressionUnboundFact) ebLeftSide.getExpressionLeftSide().getParts().get( 0 ) );
        assertEquals( "Person",
                      expressionUnboundFact.getName() );
        assertEquals( "Person",
                      expressionUnboundFact.getClassType() );
        assertEquals( "Person",
                      expressionUnboundFact.getGenericType() );
        assertEquals( m.lhs[ 0 ],
                      expressionUnboundFact.getFact() );

        assertNull( expressionUnboundFact.getPrevious() );
        assertEquals( ebLeftSide.getExpressionLeftSide().getParts().get( 1 ),
                      expressionUnboundFact.getNext() );

        assertTrue( ebLeftSide.getExpressionLeftSide().getParts().get( 1 ) instanceof ExpressionField );
        ExpressionField expressionField1 = (ExpressionField) ebLeftSide.getExpressionLeftSide().getParts().get( 1 );
        assertEquals( "address",
                      expressionField1.getName() );
        assertEquals( "org.test.Address",
                      expressionField1.getClassType() );
        assertEquals( "Address",
                      expressionField1.getGenericType() );

        assertEquals( ebLeftSide.getExpressionLeftSide().getParts().get( 0 ), expressionField1.getPrevious() );
        assertEquals( ebLeftSide.getExpressionLeftSide().getParts().get( 2 ), expressionField1.getNext() );

        assertTrue( ebLeftSide.getExpressionLeftSide().getParts().get( 2 ) instanceof ExpressionField );
        ExpressionField expressionField2 = (ExpressionField) ebLeftSide.getExpressionLeftSide().getParts().get( 2 );
        assertEquals( "postalCode",
                      expressionField2.getName() );
        assertEquals( "java.lang.Integer",
                      expressionField2.getClassType() );
        assertEquals( "Integer",
                      expressionField2.getGenericType() );

        assertEquals( ebLeftSide.getExpressionLeftSide().getParts().get( 1 ),
                      expressionField2.getPrevious() );
        assertNull( expressionField2.getNext() );
    }

    @Test
    public void testNestedFieldExpressionsWithAnotherExpression() {
        String drl =
                "rule rule1\n"
                        + "when\n"
                        + "p : Person( address.postalCode == p.address.postalCode) )\n"
                        + "then\n"
                        + "end";

        addModelField( "org.test.Person",
                       "address",
                       "org.test.Address",
                       "Address" );
        addModelField( "org.test.Person",
                       "this",
                       "org.test.Person",
                       DataType.TYPE_THIS );
        addModelField( "org.test.Address",
                       "postalCode",
                       "java.lang.Integer",
                       "Integer" );

        RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal( drl,
                                                                           Collections.EMPTY_LIST,
                                                                           dmo );

        assertNotNull( m );
        assertEquals( 1, m.lhs.length );
        assertTrue( m.lhs[ 0 ] instanceof FactPattern );
        assertTrue( ( (FactPattern) m.lhs[ 0 ] ).getFieldConstraints()[ 0 ] instanceof SingleFieldConstraintEBLeftSide );

        SingleFieldConstraintEBLeftSide ebLeftSide = (SingleFieldConstraintEBLeftSide) ( (FactPattern) m.lhs[ 0 ] ).getFieldConstraints()[ 0 ];
        assertEquals( "postalCode",
                      ebLeftSide.getFieldName() );
        assertEquals( "java.lang.Integer",
                      ebLeftSide.getFieldType() );
        assertEquals( "==",
                      ebLeftSide.getOperator() );
        assertEquals( "",
                      ebLeftSide.getValue() );

        assertEquals( 3, ebLeftSide.getExpressionValue().getParts().size() );

        assertTrue( ebLeftSide.getExpressionValue().getParts().get( 0 ) instanceof ExpressionVariable );
        ExpressionVariable expressionVariable = (ExpressionVariable) ebLeftSide.getExpressionValue().getParts().get( 0 );
        assertEquals( "p",
                      expressionVariable.getName() );
        assertEquals( "org.test.Person",
                      expressionVariable.getClassType() );
        assertEquals( DataType.TYPE_THIS,
                      expressionVariable.getGenericType() );

        assertTrue( ebLeftSide.getExpressionValue().getParts().get( 1 ) instanceof ExpressionField );
        ExpressionField ef1 = (ExpressionField) ebLeftSide.getExpressionValue().getParts().get( 1 );
        assertEquals( "address",
                      ef1.getName() );
        assertEquals( "org.test.Address",
                      ef1.getClassType() );
        assertEquals( "Address",
                      ef1.getGenericType() );

        assertTrue( ebLeftSide.getExpressionValue().getParts().get( 2 ) instanceof ExpressionField );
        ExpressionField ef2 = (ExpressionField) ebLeftSide.getExpressionValue().getParts().get( 2 );
        assertEquals( "postalCode",
                      ef2.getName() );
        assertEquals( "java.lang.Integer",
                      ef2.getClassType() );
        assertEquals( DataType.TYPE_NUMERIC_INTEGER,
                      ef2.getGenericType() );

        assertEquals( 3, ebLeftSide.getExpressionLeftSide().getParts().size() );
        assertTrue( ebLeftSide.getExpressionLeftSide().getParts().get( 0 ) instanceof ExpressionUnboundFact );
        ExpressionUnboundFact expressionUnboundFact = ( (ExpressionUnboundFact) ebLeftSide.getExpressionLeftSide().getParts().get( 0 ) );
        assertEquals( "Person",
                      expressionUnboundFact.getName() );
        assertEquals( "Person",
                      expressionUnboundFact.getClassType() );
        assertEquals( "Person",
                      expressionUnboundFact.getGenericType() );
        assertEquals( m.lhs[ 0 ],
                      expressionUnboundFact.getFact() );

        assertNull( expressionUnboundFact.getPrevious() );
        assertEquals( ebLeftSide.getExpressionLeftSide().getParts().get( 1 ), expressionUnboundFact.getNext() );

        assertTrue( ebLeftSide.getExpressionLeftSide().getParts().get( 1 ) instanceof ExpressionField );
        ExpressionField expressionField1 = (ExpressionField) ebLeftSide.getExpressionLeftSide().getParts().get( 1 );
        assertEquals( "address",
                      expressionField1.getName() );
        assertEquals( "org.test.Address",
                      expressionField1.getClassType() );
        assertEquals( "Address",
                      expressionField1.getGenericType() );

        assertEquals( ebLeftSide.getExpressionLeftSide().getParts().get( 0 ), expressionField1.getPrevious() );
        assertEquals( ebLeftSide.getExpressionLeftSide().getParts().get( 2 ), expressionField1.getNext() );

        assertTrue( ebLeftSide.getExpressionLeftSide().getParts().get( 2 ) instanceof ExpressionField );
        ExpressionField expressionField2 = (ExpressionField) ebLeftSide.getExpressionLeftSide().getParts().get( 2 );
        assertEquals( "postalCode",
                      expressionField2.getName() );
        assertEquals( "java.lang.Integer",
                      expressionField2.getClassType() );
        assertEquals( DataType.TYPE_NUMERIC_INTEGER,
                      expressionField2.getGenericType() );

        assertEquals( ebLeftSide.getExpressionLeftSide().getParts().get( 1 ), expressionField2.getPrevious() );
        assertNull( expressionField2.getNext() );
    }

    @Test
    public void testSingleFieldConstraintContainsOperator() {
        String drl = "rule \"rule1\"\n"
                + "when\n"
                + "$is : IncomeSource( )\n"
                + "Applicant( incomes contains $is )\n"
                + "then\n"
                + "end";

        RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal( drl,
                                                                           Collections.EMPTY_LIST,
                                                                           dmo );

        assertNotNull( m );
        assertEquals( "rule1",
                      m.name );

        assertEquals( 2,
                      m.lhs.length );
        IPattern p0 = m.lhs[ 0 ];
        assertTrue( p0 instanceof FactPattern );

        FactPattern fp0 = (FactPattern) p0;
        assertEquals( "IncomeSource",
                      fp0.getFactType() );
        assertEquals( "$is",
                      fp0.getBoundName() );

        IPattern p1 = m.lhs[ 1 ];
        assertTrue( p1 instanceof FactPattern );

        FactPattern fp1 = (FactPattern) p1;
        assertEquals( "Applicant",
                      fp1.getFactType() );

        assertEquals( 0,
                      fp0.getNumberOfConstraints() );

        assertEquals( 1,
                      fp1.getNumberOfConstraints() );
        assertTrue( fp1.getConstraint( 0 ) instanceof SingleFieldConstraint );

        SingleFieldConstraint sfp = (SingleFieldConstraint) fp1.getConstraint( 0 );
        assertEquals( "Applicant",
                      sfp.getFactType() );
        assertEquals( "incomes",
                      sfp.getFieldName() );
        assertEquals( "contains",
                      sfp.getOperator() );
        assertEquals( "$is",
                      sfp.getValue() );
        assertEquals( BaseSingleFieldConstraint.TYPE_VARIABLE,
                      sfp.getConstraintValueType() );
    }

    @Test
    public void testCompositeFactPatternWithOr() {
        String drl = "rule \"rule1\"\n"
                + "when\n"
                + "( Person( age == 42 ) or Person( age == 43 ) )\n"
                + "then\n"
                + "end";

        RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal( drl,
                                                                           Collections.EMPTY_LIST,
                                                                           dmo );

        assertNotNull( m );
        assertEquals( "rule1",
                      m.name );

        //LHS Pattern
        assertEquals( 1,
                      m.lhs.length );
        IPattern p = m.lhs[ 0 ];
        assertTrue( p instanceof CompositeFactPattern );
        CompositeFactPattern cfp = (CompositeFactPattern) p;
        assertEquals( CompositeFactPattern.COMPOSITE_TYPE_OR,
                      cfp.getType() );

        //LHS sub-patterns
        assertEquals( 2,
                      cfp.getPatterns().length );
        IPattern cfp_p1 = cfp.getPatterns()[ 0 ];
        assertTrue( cfp_p1 instanceof FactPattern );
        FactPattern fp1 = (FactPattern) cfp_p1;
        assertEquals( "Person",
                      fp1.getFactType() );
        assertEquals( 1,
                      fp1.getConstraintList().getConstraints().length );
        assertTrue( fp1.getConstraint( 0 ) instanceof SingleFieldConstraint );
        SingleFieldConstraint cfp_sfp1 = (SingleFieldConstraint) fp1.getConstraint( 0 );
        assertEquals( "Person",
                      cfp_sfp1.getFactType() );
        assertEquals( "age",
                      cfp_sfp1.getFieldName() );
        assertEquals( "==",
                      cfp_sfp1.getOperator() );
        assertEquals( "42",
                      cfp_sfp1.getValue() );
        assertEquals( BaseSingleFieldConstraint.TYPE_LITERAL,
                      cfp_sfp1.getConstraintValueType() );

        IPattern cfp_p2 = cfp.getPatterns()[ 1 ];
        assertTrue( cfp_p2 instanceof FactPattern );
        FactPattern fp2 = (FactPattern) cfp_p2;
        assertEquals( "Person",
                      fp2.getFactType() );
        assertTrue( fp2.getConstraint( 0 ) instanceof SingleFieldConstraint );
        SingleFieldConstraint cfp_sfp2 = (SingleFieldConstraint) fp2.getConstraint( 0 );
        assertEquals( "Person",
                      cfp_sfp2.getFactType() );
        assertEquals( "age",
                      cfp_sfp2.getFieldName() );
        assertEquals( "==",
                      cfp_sfp2.getOperator() );
        assertEquals( "43",
                      cfp_sfp2.getValue() );
        assertEquals( BaseSingleFieldConstraint.TYPE_LITERAL,
                      cfp_sfp2.getConstraintValueType() );
    }

    @Test
    public void testReciprocal_CompositeFactPatternWithOr() {
        //This is the inverse of "CompositeFactPatternWithOr"
        String drl = "rule \"rule1\"\n"
                + "dialect \"mvel\"\n"
                + "when\n"
                + "( Person( age == 42 ) or Person( age == 43 ) )\n"
                + "then\n"
                + "end";

        RuleModel m = new RuleModel();
        m.name = "rule1";

        //LHS Patterns
        CompositeFactPattern cfp = new CompositeFactPattern();
        cfp.setType( CompositeFactPattern.COMPOSITE_TYPE_OR );

        //LHS sub-patterns
        FactPattern fp1 = new FactPattern();
        fp1.setFactType( "Person" );

        SingleFieldConstraint cfp_sfp1 = new SingleFieldConstraint();
        cfp_sfp1.setFactType( "Person" );
        cfp_sfp1.setFieldName( "age" );
        cfp_sfp1.setOperator( "==" );
        cfp_sfp1.setValue( "42" );
        cfp_sfp1.setFieldType( DataType.TYPE_NUMERIC_INTEGER );
        cfp_sfp1.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        fp1.addConstraint( cfp_sfp1 );
        cfp.addFactPattern( fp1 );

        FactPattern fp2 = new FactPattern();
        fp2.setFactType( "Person" );

        SingleFieldConstraint cfp_sfp2 = new SingleFieldConstraint();
        cfp_sfp2.setFactType( "Person" );
        cfp_sfp2.setFieldName( "age" );
        cfp_sfp2.setOperator( "==" );
        cfp_sfp2.setValue( "43" );
        cfp_sfp2.setFieldType( DataType.TYPE_NUMERIC_INTEGER );
        cfp_sfp2.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        fp2.addConstraint( cfp_sfp2 );
        cfp.addFactPattern( fp2 );

        m.addLhsItem( cfp );

        String actualDrl = RuleModelDRLPersistenceImpl.getInstance().marshal( m );
        assertEqualsIgnoreWhitespace( drl,
                                      actualDrl );
    }

    @Test
    public void testCompositeFactPatternWithOrAndCompositeFieldConstraint() {
        String drl = "rule \"rule1\"\n"
                + "when\n"
                + "( Person( age == 42 ) or Person( age == 43 || age == 44) )\n"
                + "then\n"
                + "end";

        RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal( drl,
                                                                           Collections.EMPTY_LIST,
                                                                           dmo );

        assertNotNull( m );
        assertEquals( "rule1",
                      m.name );

        //LHS Pattern
        assertEquals( 1,
                      m.lhs.length );
        IPattern p = m.lhs[ 0 ];
        assertTrue( p instanceof CompositeFactPattern );

        CompositeFactPattern cfp = (CompositeFactPattern) p;
        assertEquals( CompositeFactPattern.COMPOSITE_TYPE_OR,
                      cfp.getType() );

        //LHS sub-patterns
        assertEquals( 2,
                      cfp.getPatterns().length );
        IPattern cfp_p1 = cfp.getPatterns()[ 0 ];
        assertTrue( cfp_p1 instanceof FactPattern );
        FactPattern fp1 = (FactPattern) cfp_p1;
        assertEquals( "Person",
                      fp1.getFactType() );
        assertEquals( 1,
                      fp1.getConstraintList().getConstraints().length );
        assertTrue( fp1.getConstraint( 0 ) instanceof SingleFieldConstraint );
        SingleFieldConstraint cfp_sfp1 = (SingleFieldConstraint) fp1.getConstraint( 0 );
        assertEquals( "Person",
                      cfp_sfp1.getFactType() );
        assertEquals( "age",
                      cfp_sfp1.getFieldName() );
        assertEquals( "==",
                      cfp_sfp1.getOperator() );
        assertEquals( "42",
                      cfp_sfp1.getValue() );
        assertEquals( BaseSingleFieldConstraint.TYPE_LITERAL,
                      cfp_sfp1.getConstraintValueType() );

        IPattern cfp_p2 = cfp.getPatterns()[ 1 ];
        assertTrue( cfp_p2 instanceof FactPattern );
        FactPattern fp2 = (FactPattern) cfp_p2;
        assertEquals( 1,
                      fp2.getConstraintList().getConstraints().length );
        assertTrue( fp2.getConstraint( 0 ) instanceof CompositeFieldConstraint );

        CompositeFieldConstraint cfp_p2_cfp = (CompositeFieldConstraint) fp2.getConstraint( 0 );
        assertEquals( "||",
                      cfp_p2_cfp.getCompositeJunctionType() );
        assertEquals( 2,
                      cfp_p2_cfp.getNumberOfConstraints() );
        assertTrue( cfp_p2_cfp.getConstraint( 0 ) instanceof SingleFieldConstraint );
        assertTrue( cfp_p2_cfp.getConstraint( 1 ) instanceof SingleFieldConstraint );

        SingleFieldConstraint cfp_p2_sfp1 = (SingleFieldConstraint) cfp_p2_cfp.getConstraint( 0 );
        assertEquals( "Person",
                      cfp_p2_sfp1.getFactType() );
        assertEquals( "age",
                      cfp_p2_sfp1.getFieldName() );
        assertEquals( "==",
                      cfp_p2_sfp1.getOperator() );
        assertEquals( "43",
                      cfp_p2_sfp1.getValue() );
        assertEquals( BaseSingleFieldConstraint.TYPE_LITERAL,
                      cfp_p2_sfp1.getConstraintValueType() );

        SingleFieldConstraint cfp_p2_sfp2 = (SingleFieldConstraint) cfp_p2_cfp.getConstraint( 1 );
        assertEquals( "Person",
                      cfp_p2_sfp2.getFactType() );
        assertEquals( "age",
                      cfp_p2_sfp2.getFieldName() );
        assertEquals( "==",
                      cfp_p2_sfp2.getOperator() );
        assertEquals( "44",
                      cfp_p2_sfp2.getValue() );
        assertEquals( BaseSingleFieldConstraint.TYPE_LITERAL,
                      cfp_p2_sfp2.getConstraintValueType() );
    }

    @Test
    public void testReciprocal_CompositeFactPatternWithOrAndCompositeFieldConstraint() {
        //This is the inverse of "CompositeFactPatternWithOrAndCompositeFieldConstraint"
        String drl = "rule \"rule1\"\n"
                + "dialect \"mvel\"\n"
                + "when\n"
                + "( Person( age == 42 ) or Person( age == 43 || age == 44) )\n"
                + "then\n"
                + "end";

        RuleModel m = new RuleModel();
        m.name = "rule1";

        //LHS Pattern
        CompositeFactPattern cfp = new CompositeFactPattern();
        cfp.setType( CompositeFactPattern.COMPOSITE_TYPE_OR );

        //LHS sub-patterns
        FactPattern fp1 = new FactPattern();
        fp1.setFactType( "Person" );

        SingleFieldConstraint fp1_sfp1 = new SingleFieldConstraint();
        fp1_sfp1.setFactType( "Person" );
        fp1_sfp1.setFieldName( "age" );
        fp1_sfp1.setOperator( "==" );
        fp1_sfp1.setValue( "42" );
        fp1_sfp1.setFieldType( DataType.TYPE_NUMERIC_INTEGER );
        fp1_sfp1.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        fp1.addConstraint( fp1_sfp1 );

        FactPattern fp2 = new FactPattern();
        fp2.setFactType( "Person" );

        CompositeFieldConstraint fp2_cfp = new CompositeFieldConstraint();
        fp2_cfp.setCompositeJunctionType( CompositeFieldConstraint.COMPOSITE_TYPE_OR );
        fp2.addConstraint( fp2_cfp );

        SingleFieldConstraint fp2_sfp1 = new SingleFieldConstraint();
        fp2_sfp1.setFactType( "Person" );
        fp2_sfp1.setFieldName( "age" );
        fp2_sfp1.setOperator( "==" );
        fp2_sfp1.setValue( "43" );
        fp2_sfp1.setFieldType( DataType.TYPE_NUMERIC_INTEGER );
        fp2_sfp1.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        fp2_cfp.addConstraint( fp2_sfp1 );

        SingleFieldConstraint fp2_sfp2 = new SingleFieldConstraint();
        fp2_sfp2.setFactType( "Person" );
        fp2_sfp2.setFieldName( "age" );
        fp2_sfp2.setOperator( "==" );
        fp2_sfp2.setValue( "44" );
        fp2_sfp2.setFieldType( DataType.TYPE_NUMERIC_INTEGER );
        fp2_sfp2.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        fp2_cfp.addConstraint( fp2_sfp2 );

        cfp.addFactPattern( fp1 );
        cfp.addFactPattern( fp2 );
        m.addLhsItem( cfp );

        String actualDrl = RuleModelDRLPersistenceImpl.getInstance().marshal( m );
        assertEqualsIgnoreWhitespace( drl,
                                      actualDrl );
    }

    @Test
    public void testNestedFieldConstraints() {
        String drl = "rule \"rule1\"\n"
                + "when\n"
                + "ParentType( this != null, this.parentChildField != null, this.parentChildField.childField == \"hello\" )\n"
                + "then\n"
                + "end";

        addModelField( "org.test.ParentType",
                       "parentChildField",
                       "org.test.ChildType",
                       "ChildType" );
        addModelField( "org.test.ChildType",
                       "childField",
                       "java.lang.String",
                       "String" );

        RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal( drl,
                                                                           Collections.EMPTY_LIST,
                                                                           dmo );

        assertNotNull( m );
        assertEquals( "rule1",
                      m.name );

        assertEquals( 1,
                      m.lhs.length );
        IPattern p = m.lhs[ 0 ];
        assertTrue( p instanceof FactPattern );

        FactPattern fp = (FactPattern) p;
        assertEquals( "ParentType",
                      fp.getFactType() );

        assertEquals( 3,
                      fp.getConstraintList().getConstraints().length );

        assertTrue( fp.getConstraint( 0 ) instanceof SingleFieldConstraint );
        SingleFieldConstraint sfp0 = (SingleFieldConstraint) fp.getConstraint( 0 );
        assertEquals( "ParentType",
                      sfp0.getFactType() );
        assertEquals( "this",
                      sfp0.getFieldName() );
        assertEquals( DataType.TYPE_THIS,
                      sfp0.getFieldType() );
        assertEquals( "!= null",
                      sfp0.getOperator() );
        assertNull( sfp0.getValue() );
        assertEquals( BaseSingleFieldConstraint.TYPE_UNDEFINED,
                      sfp0.getConstraintValueType() );
        assertNull( sfp0.getParent() );

        assertTrue( fp.getConstraint( 1 ) instanceof SingleFieldConstraintEBLeftSide );
        SingleFieldConstraintEBLeftSide sfp1 = (SingleFieldConstraintEBLeftSide) fp.getConstraint( 1 );
        assertEquals( "ParentType",
                      sfp1.getFactType() );
        assertEquals( "parentChildField",
                      sfp1.getFieldName() );
        assertEquals( "org.test.ChildType",
                      sfp1.getFieldType() );
        assertEquals( "!= null",
                      sfp1.getOperator() );
        assertNull( sfp1.getValue() );
        assertEquals( BaseSingleFieldConstraint.TYPE_UNDEFINED,
                      sfp1.getConstraintValueType() );
        assertNull( sfp1.getParent() );

        assertTrue( fp.getConstraint( 2 ) instanceof SingleFieldConstraintEBLeftSide );
        SingleFieldConstraintEBLeftSide sfp2 = (SingleFieldConstraintEBLeftSide) fp.getConstraint( 2 );
        assertEquals( "childField",
                      sfp2.getFieldName() );
        assertEquals( "java.lang.String",
                      sfp2.getFieldType() );
        assertEquals( "==",
                      sfp2.getOperator() );
        assertEquals( "hello",
                      sfp2.getValue() );
        assertEquals( BaseSingleFieldConstraint.TYPE_LITERAL,
                      sfp2.getConstraintValueType() );
        assertNull( sfp2.getParent() );
    }

    @Test
    public void testReciprocal_NestedFieldConstraints() {
        //This is the inverse of "NestedFieldConstraints"
        String drl = "rule \"rule1\"\n"
                + "dialect \"mvel\"\n"
                + "when\n"
                + "ParentType( this != null, this.parentChildField != null, this.parentChildField.childField == \"hello\" )\n"
                + "then\n"
                + "end";

        RuleModel m = new RuleModel();
        m.name = "rule1";

        //LHS Pattern
        FactPattern fp1 = new FactPattern();
        fp1.setFactType( "ParentType" );

        SingleFieldConstraint fp1_sfp1 = new SingleFieldConstraint();
        fp1_sfp1.setFactType( "ParentType" );
        fp1_sfp1.setFieldName( "this" );
        fp1_sfp1.setFieldType( DataType.TYPE_THIS );
        fp1_sfp1.setOperator( "!= null" );
        fp1_sfp1.setConstraintValueType( BaseSingleFieldConstraint.TYPE_UNDEFINED );
        fp1.addConstraint( fp1_sfp1 );

        SingleFieldConstraint fp1_sfp2 = new SingleFieldConstraint();
        fp1_sfp2.setFactType( "ParentType" );
        fp1_sfp2.setFieldName( "parentChildField" );
        fp1_sfp2.setFieldType( "ChildType" );
        fp1_sfp2.setOperator( "!= null" );
        fp1_sfp2.setConstraintValueType( BaseSingleFieldConstraint.TYPE_UNDEFINED );
        fp1.addConstraint( fp1_sfp2 );
        fp1_sfp2.setParent( fp1_sfp1 );

        SingleFieldConstraint fp1_sfp3 = new SingleFieldConstraint();
        fp1_sfp3.setFactType( "ChildType" );
        fp1_sfp3.setFieldName( "childField" );
        fp1_sfp3.setFieldType( DataType.TYPE_STRING );
        fp1_sfp3.setOperator( "==" );
        fp1_sfp3.setValue( "hello" );
        fp1_sfp3.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        fp1.addConstraint( fp1_sfp3 );
        fp1_sfp3.setParent( fp1_sfp2 );

        m.addLhsItem( fp1 );

        String actualDrl = RuleModelDRLPersistenceImpl.getInstance().marshal( m );
        assertEqualsIgnoreWhitespace( drl,
                                      actualDrl );
    }

    @Test
    public void testNestedFieldConstraintsAsExpression() {
        String drl = "rule \"rule1\"\n"
                + "when\n"
                + "Person( contact.telephone > 12345 )\n"
                + "then\n"
                + "end";

        addModelField( "org.test.Person",
                       "contact",
                       "org.test.Contact",
                       "Contact" );
        addModelField( "org.test.Contact",
                       "telephone",
                       "java.lang.Integer",
                       DataType.TYPE_NUMERIC_INTEGER );

        RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal( drl,
                                                                           Collections.EMPTY_LIST,
                                                                           dmo );

        assertNotNull( m );
        assertEquals( "rule1",
                      m.name );

        assertEquals( 1,
                      m.lhs.length );
        IPattern p = m.lhs[ 0 ];
        assertTrue( p instanceof FactPattern );

        FactPattern fp = (FactPattern) p;
        assertEquals( "Person",
                      fp.getFactType() );

        assertEquals( 1,
                      fp.getConstraintList().getConstraints().length );

        assertTrue( ( (FactPattern) m.lhs[ 0 ] ).getFieldConstraints()[ 0 ] instanceof SingleFieldConstraintEBLeftSide );

        SingleFieldConstraintEBLeftSide ebLeftSide = (SingleFieldConstraintEBLeftSide) ( (FactPattern) m.lhs[ 0 ] ).getFieldConstraints()[ 0 ];
        assertEquals( "telephone",
                      ebLeftSide.getFieldName() );
        assertEquals( "java.lang.Integer",
                      ebLeftSide.getFieldType() );
        assertEquals( ">",
                      ebLeftSide.getOperator() );
        assertEquals( "12345",
                      ebLeftSide.getValue() );

        assertEquals( 3, ebLeftSide.getExpressionLeftSide().getParts().size() );
        assertTrue( ebLeftSide.getExpressionLeftSide().getParts().get( 0 ) instanceof ExpressionUnboundFact );
        ExpressionUnboundFact expressionUnboundFact = ( (ExpressionUnboundFact) ebLeftSide.getExpressionLeftSide().getParts().get( 0 ) );
        assertEquals( "Person",
                      expressionUnboundFact.getName() );
        assertEquals( "Person",
                      expressionUnboundFact.getClassType() );
        assertEquals( "Person",
                      expressionUnboundFact.getGenericType() );
        assertEquals( m.lhs[ 0 ],
                      expressionUnboundFact.getFact() );

        assertEquals( null, expressionUnboundFact.getPrevious() );
        assertEquals( ebLeftSide.getExpressionLeftSide().getParts().get( 1 ),
                      expressionUnboundFact.getNext() );

        assertTrue( ebLeftSide.getExpressionLeftSide().getParts().get( 1 ) instanceof ExpressionField );
        ExpressionField expressionField1 = (ExpressionField) ebLeftSide.getExpressionLeftSide().getParts().get( 1 );
        assertEquals( "contact",
                      expressionField1.getName() );
        assertEquals( "org.test.Contact",
                      expressionField1.getClassType() );
        assertEquals( "Contact",
                      expressionField1.getGenericType() );

        assertEquals( ebLeftSide.getExpressionLeftSide().getParts().get( 0 ),
                      expressionField1.getPrevious() );
        assertEquals( ebLeftSide.getExpressionLeftSide().getParts().get( 2 ),
                      expressionField1.getNext() );

        assertTrue( ebLeftSide.getExpressionLeftSide().getParts().get( 2 ) instanceof ExpressionField );
        ExpressionField expressionField2 = (ExpressionField) ebLeftSide.getExpressionLeftSide().getParts().get( 2 );
        assertEquals( "telephone",
                      expressionField2.getName() );
        assertEquals( "java.lang.Integer",
                      expressionField2.getClassType() );
        assertEquals( DataType.TYPE_NUMERIC_INTEGER,
                      expressionField2.getGenericType() );

        assertEquals( ebLeftSide.getExpressionLeftSide().getParts().get( 1 ),
                      expressionField2.getPrevious() );
        assertNull( expressionField2.getNext() );
    }

    @Test
    public void testNestedFieldConstraintsOnlyLeafOperator() {
        String drl = "rule \"rule1\"\n"
                + "when\n"
                + "ParentType( parentChildField.childField == \"hello\" )\n"
                + "then\n"
                + "end";

        addModelField( "org.test.ParentType",
                       "parentChildField",
                       "org.test.ChildType",
                       "ChildType" );
        addModelField( "org.test.ChildType",
                       "childField",
                       "java.lang.String",
                       "String" );

        RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal( drl,
                                                                           Collections.EMPTY_LIST,
                                                                           dmo );

        assertNotNull( m );
        assertEquals( 1, m.lhs.length );
        assertTrue( m.lhs[ 0 ] instanceof FactPattern );
        assertTrue( ( (FactPattern) m.lhs[ 0 ] ).getFieldConstraints()[ 0 ] instanceof SingleFieldConstraintEBLeftSide );

        SingleFieldConstraintEBLeftSide ebLeftSide = (SingleFieldConstraintEBLeftSide) ( (FactPattern) m.lhs[ 0 ] ).getFieldConstraints()[ 0 ];
        assertEquals( "childField",
                      ebLeftSide.getFieldName() );
        assertEquals( "java.lang.String",
                      ebLeftSide.getFieldType() );
        assertEquals( "==",
                      ebLeftSide.getOperator() );
        assertEquals( "hello",
                      ebLeftSide.getValue() );

        assertEquals( 3, ebLeftSide.getExpressionLeftSide().getParts().size() );
        assertTrue( ebLeftSide.getExpressionLeftSide().getParts().get( 0 ) instanceof ExpressionUnboundFact );
        ExpressionUnboundFact expressionUnboundFact = ( (ExpressionUnboundFact) ebLeftSide.getExpressionLeftSide().getParts().get( 0 ) );
        assertEquals( "ParentType",
                      expressionUnboundFact.getName() );
        assertEquals( "ParentType",
                      expressionUnboundFact.getClassType() );
        assertEquals( "ParentType",
                      expressionUnboundFact.getGenericType() );
        assertEquals( m.lhs[ 0 ],
                      expressionUnboundFact.getFact() );

        assertNull( expressionUnboundFact.getPrevious() );
        assertEquals( ebLeftSide.getExpressionLeftSide().getParts().get( 1 ), expressionUnboundFact.getNext() );

        assertTrue( ebLeftSide.getExpressionLeftSide().getParts().get( 1 ) instanceof ExpressionField );
        ExpressionField expressionField1 = (ExpressionField) ebLeftSide.getExpressionLeftSide().getParts().get( 1 );
        assertEquals( "parentChildField",
                      expressionField1.getName() );
        assertEquals( "org.test.ChildType",
                      expressionField1.getClassType() );
        assertEquals( "ChildType",
                      expressionField1.getGenericType() );

        assertEquals( ebLeftSide.getExpressionLeftSide().getParts().get( 0 ), expressionField1.getPrevious() );
        assertEquals( ebLeftSide.getExpressionLeftSide().getParts().get( 2 ), expressionField1.getNext() );

        assertTrue( ebLeftSide.getExpressionLeftSide().getParts().get( 2 ) instanceof ExpressionField );
        ExpressionField expressionField2 = (ExpressionField) ebLeftSide.getExpressionLeftSide().getParts().get( 2 );
        assertEquals( "childField",
                      expressionField2.getName() );
        assertEquals( "java.lang.String",
                      expressionField2.getClassType() );
        assertEquals( "String",
                      expressionField2.getGenericType() );

        assertEquals( ebLeftSide.getExpressionLeftSide().getParts().get( 1 ), expressionField2.getPrevious() );
        assertNull( expressionField2.getNext() );
    }

    @Test
    public void testReciprocal_NestedFieldConstraintsOnlyLeafOperator() {
        //This is the inverse of "NestedFieldConstraintsOnlyLeafOperator"
        String drl = "rule \"rule1\"\n"
                + "dialect \"mvel\"\n"
                + "when\n"
                + "ParentType( this.parentChildField.childField == \"hello\" )\n"
                + "then\n"
                + "end";

        RuleModel m = new RuleModel();
        m.name = "rule1";

        //LHS Pattern
        FactPattern fp1 = new FactPattern();
        fp1.setFactType( "ParentType" );

        SingleFieldConstraint fp1_sfp1 = new SingleFieldConstraint();
        fp1_sfp1.setFactType( "ParentType" );
        fp1_sfp1.setFieldName( "this" );
        fp1_sfp1.setFieldType( DataType.TYPE_THIS );
        fp1_sfp1.setConstraintValueType( BaseSingleFieldConstraint.TYPE_UNDEFINED );
        fp1.addConstraint( fp1_sfp1 );

        SingleFieldConstraint fp1_sfp2 = new SingleFieldConstraint();
        fp1_sfp2.setFactType( "ParentType" );
        fp1_sfp2.setFieldName( "parentChildField" );
        fp1_sfp2.setFieldType( "ChildType" );
        fp1_sfp2.setConstraintValueType( BaseSingleFieldConstraint.TYPE_UNDEFINED );
        fp1.addConstraint( fp1_sfp2 );
        fp1_sfp2.setParent( fp1_sfp1 );

        SingleFieldConstraint fp1_sfp3 = new SingleFieldConstraint();
        fp1_sfp3.setFactType( "ChildType" );
        fp1_sfp3.setFieldName( "childField" );
        fp1_sfp3.setFieldType( DataType.TYPE_STRING );
        fp1_sfp3.setOperator( "==" );
        fp1_sfp3.setValue( "hello" );
        fp1_sfp3.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        fp1.addConstraint( fp1_sfp3 );
        fp1_sfp3.setParent( fp1_sfp2 );

        m.addLhsItem( fp1 );

        String actualDrl = RuleModelDRLPersistenceImpl.getInstance().marshal( m );
        assertEqualsIgnoreWhitespace( drl,
                                      actualDrl );
    }

    @Test
    public void testCalendarsAttribute() {
        String drl = "rule \"rule1\"\n"
                + "calendars \"myCalendar\", \"Yet Another Calendar\"\n"
                + "when\n"
                + "then\n"
                + "end";

        RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal( drl,
                                                                           Collections.EMPTY_LIST,
                                                                           dmo );
        assertEquals( 1,
                      m.attributes.length );
        assertEquals( "calendars",
                      m.attributes[ 0 ].getAttributeName() );
        assertEquals( "myCalendar, Yet Another Calendar",
                      m.attributes[ 0 ].getValue() );
    }

    @Test
    public void testFunctionCall() {
        // BZ-1013682
        String drl = "" +
                "package org.mortgages;\n" +
                "import org.mortgages.LoanApplication;\n" +
                "\n" +
                "rule \"my rule\"\n" +
                "  dialect \"mvel\"\n" +
                "  when\n" +
                "    a : LoanApplication( )\n" +
                "  then\n" +
                "    keke.clear(  );\n" +
                "end\n";

        HashMap<String, String> globals = new HashMap<String, String>();
        globals.put( "keke", "java.util.ArrayList" );

        when(
                dmo.getPackageGlobals()
            ).thenReturn(
                globals
                        );

        RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal( drl,
                                                                           Collections.EMPTY_LIST,
                                                                           dmo );

        assertTrue( m.rhs[ 0 ] instanceof ActionCallMethod );
        ActionCallMethod actionGlobalCollectionAdd = (ActionCallMethod) m.rhs[ 0 ];
        assertEquals( "clear", actionGlobalCollectionAdd.getMethodName() );
        assertEquals( "keke", actionGlobalCollectionAdd.getVariable() );
        assertEquals( 1, actionGlobalCollectionAdd.getState() );
        assertEquals( 0, actionGlobalCollectionAdd.getFieldValues().length );

    }

    @Test
    public void testMethodCall() {
        // BZ-1042511
        String drl = "" +
                "package org.mortgages;\n" +
                "import org.mortgages.LoanApplication;\n" +
                "import java.util.Map;\n" +
                "rule \"my rule\"\n" +
                "  dialect \"mvel\"\n" +
                "  when\n" +
                "    a : LoanApplication( )\n" +
                "    m : Map()\n" +
                "  then\n" +
                "    m.put(\"key\", a );\n" +
                "end\n";

        HashMap<String, String> globals = new HashMap<String, String>();

        when( dmo.getPackageGlobals() ).thenReturn( globals );

        RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal( drl,
                                                                           Collections.EMPTY_LIST,
                                                                           dmo );

        assertEquals( 2, m.getImports().getImports().size() );

        assertTrue( m.rhs[ 0 ] instanceof ActionCallMethod );
        ActionCallMethod mc = (ActionCallMethod) m.rhs[ 0 ];
        assertEquals( "put", mc.getMethodName() );
        assertEquals( "m", mc.getVariable() );
        assertEquals( 1, mc.getState() );
        assertEquals( 2, mc.getFieldValues().length );

        ActionFieldValue f1 = mc.getFieldValue( 0 );
        assertEquals( "key", f1.getValue() );
        ActionFieldValue f2 = mc.getFieldValue( 1 );
        assertEquals( "a", f2.getValue() );

        String marshalled = RuleModelDRLPersistenceImpl.getInstance().marshal( m );
        System.out.println( marshalled );

        assertEqualsIgnoreWhitespace( drl,
                                      marshalled );
    }

    @Test
    public void testMethodCallCheckParameterDataTypes1() {
        // BZ-1045423
        String drl = "" +
                "package org.mortgages;\n" +
                "import org.mortgages.LoanApplication;\n" +
                "import java.util.Map;\n" +
                "rule \"my rule\"\n" +
                "  dialect \"mvel\"\n" +
                "  when\n" +
                "    a : LoanApplication( )\n" +
                "    m : Map()\n" +
                "  then\n" +
                "    m.put(\"key\", a );\n" +
                "end\n";

        Map<String, List<MethodInfo>> methodInformation = new HashMap<String, List<MethodInfo>>();
        List<MethodInfo> mapMethodInformation = new ArrayList<MethodInfo>();
        mapMethodInformation.add( new MethodInfo( "put",
                                                  Arrays.asList( new String[]{ "java.lang.Object", "java.lang.Object" } ),
                                                  "void",
                                                  "void",
                                                  "java.util.Map" ) );

        methodInformation.put( "java.util.Map", mapMethodInformation );

        when( dmo.getProjectMethodInformation() ).thenReturn( methodInformation );

        RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal( drl,
                                                                           Collections.EMPTY_LIST,
                                                                           dmo );

        assertTrue( m.rhs[ 0 ] instanceof ActionCallMethod );
        ActionCallMethod mc = (ActionCallMethod) m.rhs[ 0 ];
        assertEquals( "put", mc.getMethodName() );
        assertEquals( "m", mc.getVariable() );
        assertEquals( 1, mc.getState() );
        assertEquals( 2, mc.getFieldValues().length );

        ActionFieldValue f1 = mc.getFieldValue( 0 );
        assertEquals( "\"key\"", f1.getValue() );
        assertEquals( "java.lang.Object", f1.getType() );
        assertEquals( FieldNatureType.TYPE_LITERAL, f1.getNature() );
        ActionFieldValue f2 = mc.getFieldValue( 1 );
        assertEquals( "a", f2.getValue() );
        assertEquals( "java.lang.Object", f2.getType() );
        assertEquals( FieldNatureType.TYPE_VARIABLE, f2.getNature() );

        String marshalled = RuleModelDRLPersistenceImpl.getInstance().marshal( m );
        System.out.println( marshalled );
        assertEqualsIgnoreWhitespace( drl, marshalled );
    }

    @Test
    public void testMethodCallCheckParameterDataTypes2() {
        // BZ-1045423
        String drl = "" +
                "package org.mortgages;\n" +
                "import org.mortgages.MyType;\n" +
                "rule \"my rule\"\n" +
                "  dialect \"mvel\"\n" +
                "  when\n" +
                "    t : MyType( )\n" +
                "  then\n" +
                "    t.doSomething( 1 * 2 );\n" +
                "end\n";

        Map<String, List<MethodInfo>> methodInformation = new HashMap<String, List<MethodInfo>>();
        List<MethodInfo> mapMethodInformation = new ArrayList<MethodInfo>();
        mapMethodInformation.add( new MethodInfo( "doSomething",
                                                  Arrays.asList( new String[]{ DataType.TYPE_NUMERIC_INTEGER } ),
                                                  "void",
                                                  "void",
                                                  "org.mortgages.MyType" ) );

        methodInformation.put( "org.mortgages.MyType", mapMethodInformation );

        when( dmo.getProjectMethodInformation() ).thenReturn( methodInformation );

        RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal( drl,
                                                                           Collections.EMPTY_LIST,
                                                                           dmo );

        assertTrue( m.rhs[ 0 ] instanceof ActionCallMethod );
        ActionCallMethod mc = (ActionCallMethod) m.rhs[ 0 ];
        assertEquals( "doSomething", mc.getMethodName() );
        assertEquals( "t", mc.getVariable() );
        assertEquals( 1, mc.getState() );
        assertEquals( 1, mc.getFieldValues().length );

        ActionFieldValue f1 = mc.getFieldValue( 0 );
        assertEquals( "1 * 2", f1.getValue() );
        assertEquals( DataType.TYPE_NUMERIC_INTEGER, f1.getType() );
        assertEquals( FieldNatureType.TYPE_FORMULA, f1.getNature() );

        String marshalled = RuleModelDRLPersistenceImpl.getInstance().marshal( m );
        System.out.println( marshalled );
        assertEqualsIgnoreWhitespace( drl,
                                      marshalled );
    }

    @Test
    public void testMethodCallCheckParameterDataTypes3() {
        // BZ-1045423
        String drl = "" +
                "package org.mortgages;\n" +
                "import org.mortgages.MyType;\n" +
                "rule \"my rule\"\n" +
                "  dialect \"mvel\"\n" +
                "  when\n" +
                "    i : Integer( )\n" +
                "    t : MyType( )\n" +
                "  then\n" +
                "    t.doSomething( i );\n" +
                "end\n";

        Map<String, List<MethodInfo>> methodInformation = new HashMap<String, List<MethodInfo>>();
        List<MethodInfo> mapMethodInformation = new ArrayList<MethodInfo>();
        mapMethodInformation.add( new MethodInfo( "doSomething",
                                                  Arrays.asList( new String[]{ DataType.TYPE_NUMERIC_INTEGER } ),
                                                  "void",
                                                  "void",
                                                  "org.mortgages.MyType" ) );

        methodInformation.put( "org.mortgages.MyType", mapMethodInformation );

        when( dmo.getProjectMethodInformation() ).thenReturn( methodInformation );

        RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal( drl,
                                                                           Collections.EMPTY_LIST,
                                                                           dmo );

        assertTrue( m.rhs[ 0 ] instanceof ActionCallMethod );
        ActionCallMethod mc = (ActionCallMethod) m.rhs[ 0 ];
        assertEquals( "doSomething", mc.getMethodName() );
        assertEquals( "t", mc.getVariable() );
        assertEquals( 1, mc.getState() );
        assertEquals( 1, mc.getFieldValues().length );

        ActionFieldValue f1 = mc.getFieldValue( 0 );
        assertEquals( "i", f1.getValue() );
        assertEquals( DataType.TYPE_NUMERIC_INTEGER, f1.getType() );
        assertEquals( FieldNatureType.TYPE_VARIABLE, f1.getNature() );

        String marshalled = RuleModelDRLPersistenceImpl.getInstance().marshal( m );
        System.out.println( marshalled );
        assertEqualsIgnoreWhitespace( drl,
                                      marshalled );
    }

    @Test
    public void testGlobalCollectionAdd() {
        // BZ-1013682
        String drl = "package org.mortgages;\n" +
                "\n" +
                "import org.mortgages.LoanApplication;\n" +
                "\n" +
                "rule \"Bankruptcy history\"\n" +
                "  dialect \"mvel\"\n" +
                "  when\n" +
                "    a : LoanApplication( )\n" +
                "  then\n" +
                "    keke.add( a );\n" +
                "end";

        HashMap<String, String> globals = new HashMap<String, String>();
        globals.put( "keke", "java.util.ArrayList" );

        when(
                dmo.getPackageGlobals()
            ).thenReturn(
                globals
                        );

        RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal( drl,
                                                                           Collections.EMPTY_LIST,
                                                                           dmo );

        assertTrue( m.rhs[ 0 ] instanceof ActionGlobalCollectionAdd );
        ActionGlobalCollectionAdd actionGlobalCollectionAdd = (ActionGlobalCollectionAdd) m.rhs[ 0 ];
        assertEquals( "keke", actionGlobalCollectionAdd.getGlobalName() );
        assertEquals( "a", actionGlobalCollectionAdd.getFactName() );

    }

    @Test
    public void testFieldConstraintLessThanOrEqualTo() {
        String drl = "rule \"rule1\"\n"
                + "when\n"
                + "Applicant( age <= 22 )\n"
                + "then\n"
                + "end";

        RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal( drl,
                                                                           Collections.EMPTY_LIST,
                                                                           dmo );

        assertNotNull( m );
        assertEquals( "rule1",
                      m.name );

        assertEquals( 1,
                      m.lhs.length );
        IPattern p = m.lhs[ 0 ];
        assertTrue( p instanceof FactPattern );

        FactPattern fp = (FactPattern) p;
        assertEquals( "Applicant",
                      fp.getFactType() );

        assertEquals( 1,
                      fp.getNumberOfConstraints() );

        FieldConstraint fc = fp.getConstraint( 0 );
        assertNotNull( fc );
        assertTrue( fc instanceof SingleFieldConstraint );

        SingleFieldConstraint sfc = (SingleFieldConstraint) fc;
        assertEquals( "<=",
                      sfc.getOperator() );
        assertEquals( "22",
                      sfc.getValue() );
    }

    @Test
    public void testExpressionWithListSize() throws Exception {
        String drl = "" +
                "rule \"Borked\"\n" +
                "  dialect \"mvel\"\n" +
                "  when\n" +
                "    Company( emps.size() == 0 )\n" +
                "  then\n" +
                "end";

        addModelField( "Company",
                       "emps",
                       "java.util.List",
                       "List" );

        addMethodInformation( "java.util.List",
                              "size",
                              new ArrayList<String>(),
                              "int",
                              null,
                              DataType.TYPE_NUMERIC_INTEGER );

        RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal( drl,
                                                                           Collections.EMPTY_LIST,
                                                                           dmo );
        assertEquals( 1,
                      m.lhs.length );
        assertTrue( m.lhs[ 0 ] instanceof FactPattern );
        FactPattern factPattern = (FactPattern) m.lhs[ 0 ];
        assertEquals( 1,
                      factPattern.getConstraintList().getConstraints().length );
        assertTrue( factPattern.getConstraintList().getConstraints()[ 0 ] instanceof SingleFieldConstraintEBLeftSide );
        SingleFieldConstraintEBLeftSide constraint = (SingleFieldConstraintEBLeftSide) factPattern.getConstraintList().getConstraints()[ 0 ];
        assertEquals( "size", constraint.getFieldName() );
        assertEquals( "int", constraint.getFieldType() );
        assertEquals( "0", constraint.getValue() );
        assertEquals( "==", constraint.getOperator() );
        assertEquals( 1, constraint.getConstraintValueType() );
    }

    @Test
    public void testMVELInlineList() throws Exception {
        String drl = "" +
                "rule \"Borked\"\n" +
                "  dialect \"mvel\"\n" +
                "  when\n" +
                "    c : Company( )\n" +
                "  then\n" +
                "    c.setEmps( [\"item1\", \"item2\"] );\n" +
                "end";

        addModelField( "Company",
                       "emps",
                       "java.util.List",
                       DataType.TYPE_COLLECTION );

        RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal( drl,
                                                                           Collections.EMPTY_LIST,
                                                                           dmo );
        assertEquals( 1,
                      m.rhs.length );
        assertTrue( m.rhs[ 0 ] instanceof ActionSetField );
        ActionSetField actionSetField = (ActionSetField) m.rhs[ 0 ];

        assertEquals( "c", actionSetField.getVariable() );

        assertEquals( 1, actionSetField.getFieldValues().length );

        ActionFieldValue actionFieldValue = actionSetField.getFieldValues()[ 0 ];

        assertEquals( "[\"item1\", \"item2\"]", actionFieldValue.getValue() );
        assertEquals( "emps", actionFieldValue.getField() );
        assertEquals( FieldNatureType.TYPE_FORMULA, actionFieldValue.getNature() );
        assertEquals( DataType.TYPE_COLLECTION, actionFieldValue.getType() );

    }

    @Test
    public void testFunctionInRHS() throws Exception {
        String drl = "" +
                "rule \"Borked\"\n" +
                "  dialect \"mvel\"\n" +
                "  when\n" +
                "    application : Application( )\n" +
                "  then\n" +
                "    application.setApr( application.getApr() + 5 );\n" +
                "    update( application )" +
                "end";

        RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal( drl,
                                                                           Collections.EMPTY_LIST,
                                                                           dmo );

        assertTrue( m.rhs[ 0 ] instanceof ActionUpdateField );

        ActionUpdateField field = (ActionUpdateField) m.rhs[ 0 ];
        assertTrue( field.getFieldValues()[ 0 ] instanceof ActionFieldValue );
        ActionFieldValue value = field.getFieldValues()[ 0 ];
        assertEquals( "apr", value.getField() );
        assertEquals( "application.getApr() + 5", value.getValue() );
        assertEquals( FieldNatureType.TYPE_FORMULA, value.getNature() );
        assertEquals( DataType.TYPE_NUMERIC, value.getType() );
    }

    @Test
    public void testFieldVars() throws Exception {
        String drl = "" +
                "rule \"Borked\"\n" +
                "  dialect \"mvel\"\n" +
                "  when\n" +
                "    Customer( var:contact )\n" +
                "  then\n" +
                "end";

        addModelField( "Customer",
                       "contact",
                       "Contact",
                       "Contact" );

        RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal( drl,
                                                                           Collections.EMPTY_LIST,
                                                                           dmo );

        FactPattern pattern = (FactPattern) m.lhs[ 0 ];
        SingleFieldConstraint constraint = (SingleFieldConstraint) pattern.getFieldConstraints()[ 0 ];

        assertEquals( "var", constraint.getFieldBinding() );
        assertEquals( "Customer", constraint.getFactType() );
        assertEquals( "contact", constraint.getFieldName() );
        assertEquals( "Contact", constraint.getFieldType() );

    }

    @Test
    public void testFieldVarsWithImports() throws Exception {
        String drl = "" +
                "import org.test.Customer\n" +
                "rule \"Borked\"\n" +
                "  dialect \"mvel\"\n" +
                "  when\n" +
                "    Customer( var:contact )\n" +
                "  then\n" +
                "end";

        addModelField( "org.test.Customer",
                       "contact",
                       "org.test.Contact",
                       "org.test.Contact" );

        RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal( drl,
                                                                           Collections.EMPTY_LIST,
                                                                           dmo );

        FactPattern pattern = (FactPattern) m.lhs[ 0 ];
        SingleFieldConstraint constraint = (SingleFieldConstraint) pattern.getFieldConstraints()[ 0 ];

        assertEquals( "var", constraint.getFieldBinding() );
        assertEquals( "Customer", constraint.getFactType() );
        assertEquals( "contact", constraint.getFieldName() );
        assertEquals( "org.test.Contact", constraint.getFieldType() );

    }

    @Test
    public void testFieldVarsFactTypeInTheSamePackage() throws Exception {
        String drl = "" +
                "package org.test\n" +
                "rule \"Borked\"\n" +
                "  dialect \"mvel\"\n" +
                "  when\n" +
                "    Customer( var:contact )\n" +
                "  then\n" +
                "end";

        addModelField( "org.test.Customer",
                       "contact",
                       "org.test.Contact",
                       "org.test.Contact" );

        RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal( drl,
                                                                           Collections.EMPTY_LIST,
                                                                           dmo );

        FactPattern pattern = (FactPattern) m.lhs[ 0 ];
        SingleFieldConstraint constraint = (SingleFieldConstraint) pattern.getFieldConstraints()[ 0 ];

        assertEquals( "var", constraint.getFieldBinding() );
        assertEquals( "Customer", constraint.getFactType() );
        assertEquals( "contact", constraint.getFieldName() );
        assertEquals( "org.test.Contact", constraint.getFieldType() );

    }

    @Test
    public void testSingleFieldConstraintEBLeftSide() throws Exception {
        String drl = "" +
                "rule \" broken \""
                + "dialect \"mvel\""
                + "  when"
                + "    Customer( contact != null , contact.tel1 > \"15\" )"
                + "  then"
                + "end";

        addModelField( "Customer",
                       "contact",
                       "Contact",
                       "Contact" );
        addModelField( "Contact",
                       "tel1",
                       "String",
                       "String" );

        RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal( drl,
                                                                           Collections.EMPTY_LIST,
                                                                           dmo );

        FactPattern pattern = (FactPattern) m.lhs[ 0 ];
        SingleFieldConstraint constraint = (SingleFieldConstraint) pattern.getFieldConstraints()[ 0 ];

        assertEquals( "Customer", constraint.getFactType() );
        assertEquals( "contact", constraint.getFieldName() );
        assertEquals( "Contact", constraint.getFieldType() );

        SingleFieldConstraintEBLeftSide constraint2 = (SingleFieldConstraintEBLeftSide) pattern.getFieldConstraints()[ 1 ];
        assertEquals( "tel1", constraint2.getFieldName() );
        assertEquals( "String", constraint2.getFieldType() );
        assertEquals( "15", constraint2.getValue() );
        assertEquals( ">", constraint2.getOperator() );

        assertEquals( 3, constraint2.getExpressionLeftSide().getParts().size() );
        ExpressionPart part1 = constraint2.getExpressionLeftSide().getParts().get( 0 );
        assertEquals( "Customer", part1.getName() );
        assertEquals( "Customer", part1.getClassType() );
        assertEquals( "Customer", part1.getGenericType() );

        ExpressionPart part2 = constraint2.getExpressionLeftSide().getParts().get( 1 );
        assertEquals( "contact", part2.getName() );
        assertEquals( "Contact", part2.getClassType() );
        assertEquals( "Contact", part2.getGenericType() );

        ExpressionPart part3 = constraint2.getExpressionLeftSide().getParts().get( 2 );
        assertEquals( "tel1", part3.getName() );
        assertEquals( "String", part3.getClassType() );
        assertEquals( "String", part3.getGenericType() );
    }

    @Test
    public void testExpressionEditorLeftToOperator() throws Exception {
        String drl = ""
                + "rule \"r1\"\n"
                + "dialect \"mvel\"\n"
                + "when\n"
                + " a: Applicant()\n"
                + " Applicant( age == a.age )\n"
                + "then\n"
                + "end";

        addModelField( "Applicant",
                       "this",
                       "Applicant",
                       DataType.TYPE_THIS );
        addModelField( "Applicant",
                       "age",
                       "java.lang.Integer",
                       "Integer" );

        RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal( drl,
                                                                           Collections.EMPTY_LIST,
                                                                           dmo );

        FactPattern pattern1 = (FactPattern) m.lhs[ 0 ];
        assertEquals( "a",
                      pattern1.getBoundName() );

        FactPattern pattern2 = (FactPattern) m.lhs[ 1 ];

        assertEquals( 1, pattern2.getConstraintList().getNumberOfConstraints() );

        assertTrue( pattern2.getConstraintList().getConstraint( 0 ) instanceof SingleFieldConstraint );
        SingleFieldConstraint constraint = (SingleFieldConstraint) pattern2.getConstraintList().getConstraint( 0 );
        assertEquals( "age", constraint.getFieldName() );
        assertEquals( "==", constraint.getOperator() );
        assertEquals( BaseSingleFieldConstraint.TYPE_EXPR_BUILDER_VALUE, constraint.getConstraintValueType() );
        assertEquals( "", constraint.getValue() );
        assertEquals( 2, constraint.getExpressionValue().getParts().size() );

        assertTrue( constraint.getExpressionValue().getParts().get( 0 ) instanceof ExpressionVariable );
        ExpressionVariable expressionVariable = (ExpressionVariable) constraint.getExpressionValue().getParts().get( 0 );
        assertEquals( "a", expressionVariable.getName() );
        assertEquals( "Applicant", expressionVariable.getClassType() );
        assertEquals( "this", expressionVariable.getGenericType() );
        assertEquals( constraint.getExpressionValue().getParts().get( 1 ), expressionVariable.getNext() );

        assertTrue( constraint.getExpressionValue().getParts().get( 1 ) instanceof ExpressionField );
        ExpressionField expressionField = (ExpressionField) constraint.getExpressionValue().getParts().get( 1 );
        assertEquals( "age", expressionField.getName() );
        assertEquals( "java.lang.Integer", expressionField.getClassType() );
        assertEquals( "Integer", expressionField.getGenericType() );
    }

    @Test
    public void testEnumeration() throws Exception {
        //https://bugzilla.redhat.com/show_bug.cgi?id=1047879
        String drl = "import org.drools.workbench.models.commons.backend.rule.TestEnum;\n"
                + "rule \"r1\"\n"
                + "dialect \"mvel\"\n"
                + "when\n"
                + "OuterClassWithEnums( outerField == TestEnum.VALUE1 )\n"
                + "then\n"
                + "end";

        addModelField( "OuterClassWithEnums",
                       "outerField",
                       TestEnum.class.getSimpleName(),
                       DataType.TYPE_COMPARABLE );

        addJavaEnumDefinition( "OuterClassWithEnums",
                               "outerField",
                               new String[]{ "TestEnum.VALUE1=TestEnum.VALUE1", "TestEnum.VALUE2=TestEnum.VALUE2" } );

        RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal( drl,
                                                                           Collections.EMPTY_LIST,
                                                                           dmo );

        FactPattern pattern = (FactPattern) m.lhs[ 0 ];
        assertEquals( 1,
                      pattern.getNumberOfConstraints() );
        assertTrue( pattern.getConstraint( 0 ) instanceof SingleFieldConstraint );
        SingleFieldConstraint constraint = (SingleFieldConstraint) pattern.getConstraint( 0 );

        assertEquals( "OuterClassWithEnums",
                      constraint.getFactType() );
        assertEquals( "outerField",
                      constraint.getFieldName() );
        assertEquals( DataType.TYPE_COMPARABLE,
                      constraint.getFieldType() );
        assertEquals( "==",
                      constraint.getOperator() );
        assertEquals( "TestEnum.VALUE1",
                      constraint.getValue() );
        assertEquals( BaseSingleFieldConstraint.TYPE_ENUM,
                      constraint.getConstraintValueType() );

        final String drl2 = RuleModelDRLPersistenceImpl.getInstance().marshal( m );
        assertEqualsIgnoreWhitespace( drl,
                                      drl2 );
    }

    @Test
    public void testFullyQualifiedClassNameEnumeration() throws Exception {
        //https://bugzilla.redhat.com/show_bug.cgi?id=1047879
        String drl = "import org.drools.workbench.models.commons.backend.rule.TestEnum;\n"
                + "import org.drools.workbench.models.commons.backend.rule.OuterClassWithEnums;\n"
                + "rule \"r1\"\n"
                + "dialect \"mvel\"\n"
                + "when\n"
                + "OuterClassWithEnums( outerField == TestEnum.VALUE1 )\n"
                + "then\n"
                + "end";

        addModelField( "org.drools.workbench.models.commons.backend.rule.OuterClassWithEnums",
                       "outerField",
                       TestEnum.class.getSimpleName(),
                       DataType.TYPE_COMPARABLE );

        addJavaEnumDefinition( "org.drools.workbench.models.commons.backend.rule.OuterClassWithEnums",
                               "outerField",
                               new String[]{ "TestEnum.VALUE1=TestEnum.VALUE1", "TestEnum.VALUE2=TestEnum.VALUE2" } );

        RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal( drl,
                                                                           Collections.EMPTY_LIST,
                                                                           dmo );

        FactPattern pattern = (FactPattern) m.lhs[ 0 ];
        assertEquals( 1,
                      pattern.getNumberOfConstraints() );
        assertTrue( pattern.getConstraint( 0 ) instanceof SingleFieldConstraint );
        SingleFieldConstraint constraint = (SingleFieldConstraint) pattern.getConstraint( 0 );

        assertEquals( "OuterClassWithEnums",
                      constraint.getFactType() );
        assertEquals( "outerField",
                      constraint.getFieldName() );
        assertEquals( DataType.TYPE_COMPARABLE,
                      constraint.getFieldType() );
        assertEquals( "==",
                      constraint.getOperator() );
        assertEquals( "TestEnum.VALUE1",
                      constraint.getValue() );
        assertEquals( BaseSingleFieldConstraint.TYPE_ENUM,
                      constraint.getConstraintValueType() );

        final String drl2 = RuleModelDRLPersistenceImpl.getInstance().marshal( m );
        assertEqualsIgnoreWhitespace( drl,
                                      drl2 );
    }

    @Test
    public void testEnumerationNestedClasses() throws Exception {
        //https://bugzilla.redhat.com/show_bug.cgi?id=1047879
        String drl = "import org.drools.workbench.models.commons.backend.rule.TestEnum;\n"
                + "rule \"r1\"\n"
                + "dialect \"mvel\"\n"
                + "when\n"
                + "OuterClassWithEnums( innerClass.innerField == TestEnum.VALUE1 )\n"
                + "then\n"
                + "end";

        addModelField( "OuterClassWithEnums",
                       "innerClass",
                       "InnerClassWithEnums",
                       "InnerClassWithEnums" );
        addModelField( "InnerClassWithEnums",
                       "innerField",
                       TestEnum.class.getSimpleName(),
                       DataType.TYPE_COMPARABLE );

        addJavaEnumDefinition( "InnerClassWithEnums",
                               "innerField",
                               new String[]{ "TestEnum.VALUE1=TestEnum.VALUE1", "TestEnum.VALUE2=TestEnum.VALUE2" } );

        RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal( drl,
                                                                           Collections.EMPTY_LIST,
                                                                           dmo );

        FactPattern pattern = (FactPattern) m.lhs[ 0 ];
        assertEquals( 1,
                      pattern.getNumberOfConstraints() );
        assertTrue( pattern.getConstraint( 0 ) instanceof SingleFieldConstraintEBLeftSide );

        final SingleFieldConstraintEBLeftSide constraint = (SingleFieldConstraintEBLeftSide) pattern.getConstraint( 0 );
        assertEquals( 3,
                      constraint.getExpressionLeftSide().getParts().size() );

        assertTrue( constraint.getExpressionLeftSide().getParts().get( 0 ) instanceof ExpressionUnboundFact );
        final ExpressionUnboundFact eubf = ( (ExpressionUnboundFact) constraint.getExpressionLeftSide().getParts().get( 0 ) );
        assertEquals( "OuterClassWithEnums",
                      eubf.getName() );
        assertEquals( "OuterClassWithEnums",
                      eubf.getClassType() );
        assertEquals( "OuterClassWithEnums",
                      eubf.getGenericType() );

        assertTrue( constraint.getExpressionLeftSide().getParts().get( 1 ) instanceof ExpressionField );
        final ExpressionField ef1 = ( (ExpressionField) constraint.getExpressionLeftSide().getParts().get( 1 ) );
        assertEquals( "innerClass",
                      ef1.getName() );
        assertEquals( "InnerClassWithEnums",
                      ef1.getClassType() );
        assertEquals( "InnerClassWithEnums",
                      ef1.getGenericType() );

        assertTrue( constraint.getExpressionLeftSide().getParts().get( 2 ) instanceof ExpressionField );
        final ExpressionField ef2 = ( (ExpressionField) constraint.getExpressionLeftSide().getParts().get( 2 ) );
        assertEquals( "innerField",
                      ef2.getName() );
        assertEquals( "TestEnum",
                      ef2.getClassType() );
        assertEquals( DataType.TYPE_COMPARABLE,
                      ef2.getGenericType() );

        assertEquals( "OuterClassWithEnums",
                      constraint.getFactType() );
        assertEquals( "innerField",
                      constraint.getFieldName() );
        assertEquals( "TestEnum",
                      constraint.getFieldType() );
        assertEquals( "==",
                      constraint.getOperator() );
        assertEquals( "TestEnum.VALUE1",
                      constraint.getValue() );
        assertEquals( BaseSingleFieldConstraint.TYPE_ENUM,
                      constraint.getConstraintValueType() );

        final String drl2 = RuleModelDRLPersistenceImpl.getInstance().marshal( m );
        assertEqualsIgnoreWhitespace( drl,
                                      drl2 );
    }

    @Test
    public void testFullyQualifiedClassNameEnumerationNestedClasses() throws Exception {
        //https://bugzilla.redhat.com/show_bug.cgi?id=1047879
        String drl = "import org.drools.workbench.models.commons.backend.rule.TestEnum;\n"
                + "import org.drools.workbench.models.commons.backend.rule.OuterClassWithEnums;\n"
                + "import org.drools.workbench.models.commons.backend.rule.OuterClassWithEnums.InnerClassWithEnums;\n"
                + "rule \"r1\"\n"
                + "dialect \"mvel\"\n"
                + "when\n"
                + "OuterClassWithEnums( innerClass.innerField == TestEnum.VALUE1 )\n"
                + "then\n"
                + "end";

        addModelField( "org.drools.workbench.models.commons.backend.rule.OuterClassWithEnums",
                       "innerClass",
                       "org.drools.workbench.models.commons.backend.rule.OuterClassWithEnums$InnerClassWithEnums",
                       "OuterClassWithEnums$InnerClassWithEnums" );
        addModelField( "org.drools.workbench.models.commons.backend.rule.OuterClassWithEnums$InnerClassWithEnums",
                       "innerField",
                       TestEnum.class.getSimpleName(),
                       DataType.TYPE_COMPARABLE );

        addJavaEnumDefinition( "org.drools.workbench.models.commons.backend.rule.OuterClassWithEnums$InnerClassWithEnums",
                               "innerField",
                               new String[]{ "TestEnum.VALUE1=TestEnum.VALUE1", "TestEnum.VALUE2=TestEnum.VALUE2" } );

        RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal( drl,
                                                                           Collections.EMPTY_LIST,
                                                                           dmo );

        FactPattern pattern = (FactPattern) m.lhs[ 0 ];
        assertEquals( 1,
                      pattern.getNumberOfConstraints() );
        assertTrue( pattern.getConstraint( 0 ) instanceof SingleFieldConstraintEBLeftSide );

        final SingleFieldConstraintEBLeftSide constraint = (SingleFieldConstraintEBLeftSide) pattern.getConstraint( 0 );
        assertEquals( 3,
                      constraint.getExpressionLeftSide().getParts().size() );

        assertTrue( constraint.getExpressionLeftSide().getParts().get( 0 ) instanceof ExpressionUnboundFact );
        final ExpressionUnboundFact eubf = ( (ExpressionUnboundFact) constraint.getExpressionLeftSide().getParts().get( 0 ) );
        assertEquals( "OuterClassWithEnums",
                      eubf.getName() );
        assertEquals( "OuterClassWithEnums",
                      eubf.getClassType() );
        assertEquals( "OuterClassWithEnums",
                      eubf.getGenericType() );

        assertTrue( constraint.getExpressionLeftSide().getParts().get( 1 ) instanceof ExpressionField );
        final ExpressionField ef1 = ( (ExpressionField) constraint.getExpressionLeftSide().getParts().get( 1 ) );
        assertEquals( "innerClass",
                      ef1.getName() );
        assertEquals( "org.drools.workbench.models.commons.backend.rule.OuterClassWithEnums$InnerClassWithEnums",
                      ef1.getClassType() );
        assertEquals( "OuterClassWithEnums$InnerClassWithEnums",
                      ef1.getGenericType() );

        assertTrue( constraint.getExpressionLeftSide().getParts().get( 2 ) instanceof ExpressionField );
        final ExpressionField ef2 = ( (ExpressionField) constraint.getExpressionLeftSide().getParts().get( 2 ) );
        assertEquals( "innerField",
                      ef2.getName() );
        assertEquals( "TestEnum",
                      ef2.getClassType() );
        assertEquals( DataType.TYPE_COMPARABLE,
                      ef2.getGenericType() );

        assertEquals( "OuterClassWithEnums",
                      constraint.getFactType() );
        assertEquals( "innerField",
                      constraint.getFieldName() );
        assertEquals( "TestEnum",
                      constraint.getFieldType() );
        assertEquals( "==",
                      constraint.getOperator() );
        assertEquals( "TestEnum.VALUE1",
                      constraint.getValue() );
        assertEquals( BaseSingleFieldConstraint.TYPE_ENUM,
                      constraint.getConstraintValueType() );

        final String drl2 = RuleModelDRLPersistenceImpl.getInstance().marshal( m );
        assertEqualsIgnoreWhitespace( drl,
                                      drl2 );
    }

    @Test
    public void testCalendars() {
        //BZ1059232 - Guided rule editor: calendars attribute is broken when a list of calendars is used
        String drl = "package org.mortgages;\n" +
                "\n" +
                "import java.lang.Number;\n" +
                "rule \"Test\"\n" +
                "  calendars \"a\" ,\"b\"\n" +
                "  dialect \"mvel\"\n" +
                "  when\n" +
                "  then\n" +
                "end\n";

        RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal( drl,
                                                                           Collections.EMPTY_LIST,
                                                                           dmo );

        assertNotNull( m.attributes[ 0 ] );
        RuleAttribute attribute = m.attributes[ 0 ];
        assertEquals( "calendars", attribute.getAttributeName() );
        assertEquals( "a, b", attribute.getValue() );
    }

    @Test
    public void testFromRestrictions() {
        String drl = "package org.mortgages;\n" +
                "\n" +
                "import java.lang.Number;\n" +
                "rule \"Test\"\n" +
                "  dialect \"mvel\"\n" +
                "  when\n" +
                "    reserva : Reserva( )\n" +
                "    itinerario : Itinerario( destino == \"USA\" ) from reserva.itinerarios\n" +
                "  then\n" +
                "end\n";

        addModelField( "org.mortgages.Reserva",
                       "itinerarios",
                       "java.lang.List",
                       DataType.TYPE_COLLECTION );
        addModelField( "org.mortgages.Itinerario",
                       "destino",
                       "String",
                       DataType.TYPE_STRING );

        RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal( drl,
                                                                           Collections.EMPTY_LIST,
                                                                           dmo );
        assertTrue( m.lhs[ 1 ] instanceof FromCompositeFactPattern );
        FromCompositeFactPattern factPattern = (FromCompositeFactPattern) m.lhs[ 1 ];
        assertNotNull( factPattern.getFactPattern().getConstraintList() );
        assertEquals( 1, factPattern.getFactPattern().getConstraintList().getNumberOfConstraints() );
        SingleFieldConstraint constraint = (SingleFieldConstraint) factPattern.getFactPattern().getFieldConstraints()[ 0 ];
        assertEquals( "Itinerario", constraint.getFactType() );
        assertEquals( "destino", constraint.getFieldName() );
        assertEquals( DataType.TYPE_STRING, constraint.getFieldType() );
        assertEquals( "USA", constraint.getValue() );
        assertEquals( "==", constraint.getOperator() );
    }

    @Test
    public void testFactsWithSameName() {
        String drl = "package org.pkg1;\n" +
                "\n" +
                "import java.lang.Number;\n" +
                "rule \"Test\"\n" +
                "  dialect \"mvel\"\n" +
                "  when\n" +
                "    Fact( field.field )\n" +
                "  then\n" +
                "end\n";

        addModelField( "org.pkg1.Fact",
                       "field",
                       "org.pkg1.SubFact",
                       "SubFact" );
        addModelField( "org.pkg1.SubFact",
                       "field",
                       "String",
                       DataType.TYPE_STRING );
        addModelField( "org.pkg2.Fact",
                       "someOtherField",
                       "org.pkg2.SubFact",
                       "SubFact" );

        RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal( drl,
                                                                           Collections.EMPTY_LIST,
                                                                           dmo );
        assertEquals( 1, m.lhs.length );
    }

    @Test
    public void testFactsWithSameNameImports() {
        String drl = "package org.test;\n" +
                "\n" +
                "import org.pkg1.Fact;\n" +
                "rule \"Test\"\n" +
                "  dialect \"mvel\"\n" +
                "  when\n" +
                "    Fact( field.field )\n" +
                "  then\n" +
                "end\n";

        addModelField( "org.pkg1.Fact",
                       "field",
                       "org.pkg1.SubFact",
                       "SubFact" );
        addModelField( "org.pkg1.SubFact",
                       "field",
                       "String",
                       DataType.TYPE_STRING );
        addModelField( "org.pkg2.Fact",
                       "someOtherField",
                       "org.pkg2.SubFact",
                       "SubFact" );

        RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal( drl,
                                                                           Collections.EMPTY_LIST,
                                                                           dmo );
        assertEquals( 1, m.lhs.length );
    }

    @Test
    public void testFromBoundVariable() {
        String drl = "import java.lang.Number;\n"
                + "import org.drools.workbench.models.commons.backend.rule.Counter;\n"
                + "rule \"rule1\"\n"
                + "when\n"
                + "cc : Counter()\n"
                + "Number() from cc.number\n"
                + "then\n"
                + "end";

        addModelField( "org.drools.workbench.models.commons.backend.rule.Counter",
                       "number",
                       "java.lang.Number",
                       DataType.TYPE_NUMERIC );

        RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal( drl,
                                                                           Collections.EMPTY_LIST,
                                                                           dmo );

        assertNotNull( m );
        assertEquals( "rule1",
                      m.name );

        assertEquals( 2,
                      m.lhs.length );
        IPattern p0 = m.lhs[ 0 ];
        assertTrue( p0 instanceof FactPattern );

        FactPattern fp0 = (FactPattern) p0;
        assertEquals( "Counter",
                      fp0.getFactType() );
        assertEquals( "cc",
                      fp0.getBoundName() );
        assertEquals( 0,
                      fp0.getNumberOfConstraints() );

        IPattern p1 = m.lhs[ 1 ];
        assertTrue( p1 instanceof FromCompositeFactPattern );

        FromCompositeFactPattern fcfp1 = (FromCompositeFactPattern) p1;
        FactPattern fp1 = fcfp1.getFactPattern();
        ExpressionFormLine efl1 = fcfp1.getExpression();

        assertNotNull( fp1 );
        assertNotNull( efl1 );

        assertEquals( "Number",
                      fp1.getFactType() );
        assertEquals( 0,
                      fp1.getNumberOfConstraints() );

        assertEquals( 2,
                      efl1.getParts().size() );
        assertTrue( efl1.getParts().get( 0 ) instanceof ExpressionVariable );
        assertTrue( efl1.getParts().get( 1 ) instanceof ExpressionField );

        ExpressionVariable eflv1 = (ExpressionVariable) efl1.getParts().get( 0 );
        assertEquals( "cc",
                      eflv1.getName() );
        assertEquals( "Counter",
                      eflv1.getClassType() );
        assertEquals( DataType.TYPE_NUMERIC,
                      eflv1.getGenericType() );

        ExpressionField eflf1 = (ExpressionField) efl1.getParts().get( 1 );
        assertEquals( "number",
                      eflf1.getName() );
        assertEquals( "java.lang.Number",
                      eflf1.getClassType() );
        assertEquals( DataType.TYPE_NUMERIC,
                      eflf1.getGenericType() );
    }

    @Test
    public void testFromAccumulate() {
        String drl = "import java.lang.Number;\n"
                + "import org.mortgages.Applicant;\n"
                + "rule \"rule1\"\n"
                + "when\n"
                + "  total : Number( intValue > 0 ) from accumulate ( Applicant( age < 30 ), count() )\n"
                + "then\n"
                + "end";

        addModelField( "java.lang.Number",
                       "intValue",
                       "java.lang.Integer",
                       DataType.TYPE_NUMERIC );

        addModelField( "org.mortgages.Applicant",
                       "age",
                       "java.lang.Integer",
                       DataType.TYPE_NUMERIC );

        RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal( drl,
                                                                           Collections.EMPTY_LIST,
                                                                           dmo );

        assertNotNull( m );

        assertTrue( m.lhs[ 0 ] instanceof FromAccumulateCompositeFactPattern );

        FromAccumulateCompositeFactPattern pattern = (FromAccumulateCompositeFactPattern) m.lhs[ 0 ];
        assertNotNull( pattern.getFactPattern() );
        FactPattern factPattern = pattern.getFactPattern();
        assertEquals( "total", factPattern.getBoundName() );
        assertNotNull( factPattern.getConstraintList() );
        assertEquals( 1, factPattern.getConstraintList().getNumberOfConstraints() );
        FieldConstraint constraint = factPattern.getConstraintList().getConstraint( 0 );
        assertTrue( constraint instanceof SingleFieldConstraint );
        SingleFieldConstraint fieldConstraint = (SingleFieldConstraint) constraint;
        assertEquals( "Number", fieldConstraint.getFactType() );
        assertEquals( "intValue", fieldConstraint.getFieldName() );
        assertEquals( "Integer", fieldConstraint.getFieldType() );
        assertEquals( ">", fieldConstraint.getOperator() );
        assertEquals( "0", fieldConstraint.getValue() );
    }

    @Test
    public void testSimpleDSLExpansionLHS() {
        String drl = "rule \"rule1\"\n"
                + "when\n"
                + "Es gibt einen Vertrag\n"
                + "- Rabatt nicht mehr als 123\n"
                + "then\n"
                + "end\n";

        final String dslDefinition = "Es gibt einen Vertrag";
        final String dslFile = "[condition][vertrag]" + dslDefinition + "=vertrag : Vertrag()";

        final String dslDefinition2 = "- Rabatt nicht mehr als {rabatt}";
        final String dslFile2 = "[condition][vertrag]" + dslDefinition2 + "=rabatt < {rabatt}";

        final RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshalUsingDSL( drl,
                                                                                         new ArrayList<String>(),
                                                                                         dmo,
                                                                                         new String[]{ dslFile, dslFile2 } );

        assertNotNull( m );

        assertTrue( m.lhs[ 0 ] instanceof DSLSentence );

        DSLSentence dslSentence = (DSLSentence) m.lhs[ 0 ];
        assertEquals( dslDefinition,
                      dslSentence.getDefinition() );
        assertEquals( 0,
                      dslSentence.getValues().size() );

        DSLSentence dslSentence2 = (DSLSentence) m.lhs[ 1 ];
        assertEquals( dslDefinition2,
                      dslSentence2.getDefinition() );
        assertEquals( 1,
                      dslSentence2.getValues().size() );

        assertTrue( dslSentence2.getValues().get( 0 ) instanceof DSLVariableValue );

        DSLVariableValue dslComplexVariableValue = dslSentence2.getValues().get( 0 );
        assertEquals( "123",
                      dslComplexVariableValue.getValue() );
    }

    @Test
    public void testDSL() {
        String drl = "package org.mortgages;\n" +
                "rule \"testdsl\"\n" +
                "  dialect \"mvel\"\n" +
                "  when\n" +
                "    There is a test rated applicant older than 111 years\n" +
                "  then\n" +
                "end";

        String dslDefinition = "There is a {rating} rated applicant older than {age} years";
        String dslFile = "[when]" + dslDefinition + "= Applicant( creditRating == \"{rating}\", age > {age} )";

        when( dmo.getPackageName() ).thenReturn( "org.mortgages" );

        final RuleModel model = RuleModelDRLPersistenceImpl.getInstance().unmarshalUsingDSL( drl,
                                                                                             new ArrayList<String>(),
                                                                                             dmo,
                                                                                             new String[]{ dslFile } );

        assertEquals( 1, model.lhs.length );
        DSLSentence dslSentence = (DSLSentence) model.lhs[ 0 ];
        assertEquals( "test", dslSentence.getValues().get( 0 ).getValue() );
        assertEquals( "111", dslSentence.getValues().get( 1 ).getValue() );

    }

    @Test
    public void testDSLDollar() {
        String drl = "package org.mortgages;\n" +
                "rule \"testdsl\"\n" +
                "  dialect \"mvel\"\n" +
                "  when\n" +
                "    Price is $111\n" +
                "  then\n" +
                "end";

        String dslDefinition = "Price is ${p}";
        String dslFile = "[when]" + dslDefinition + "= Item( price == \"{p}\" )";

        when( dmo.getPackageName() ).thenReturn( "org.mortgages" );

        final RuleModel model = RuleModelDRLPersistenceImpl.getInstance().unmarshalUsingDSL( drl,
                                                                                             new ArrayList<String>(),
                                                                                             dmo,
                                                                                             new String[]{ dslFile } );

        assertEquals( 1, model.lhs.length );
        DSLSentence dslSentence = (DSLSentence) model.lhs[ 0 ];

        assertEquals( "Price is ${p}", dslSentence.getDefinition() );
        assertEquals( "111", dslSentence.getValues().get( 0 ).getValue() );

    }

    @Test
    public void testDSLExpansionLHS() {
        String drl = "rule \"rule1\"\n"
                + "when\n"
                + "The credit rating is AA\n"
                + "then\n"
                + "end\n";

        final String dslDefinition = "The credit rating is {rating:ENUM:Applicant.creditRating}";
        final String dslFile = "[when]" + dslDefinition + "=Applicant( creditRating == {rating} )";

        final RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshalUsingDSL( drl,
                                                                                         new ArrayList<String>(),
                                                                                         dmo,
                                                                                         new String[]{ dslFile } );

        assertNotNull( m );

        assertTrue( m.lhs[ 0 ] instanceof DSLSentence );

        DSLSentence dslSentence = (DSLSentence) m.lhs[ 0 ];
        assertEquals( dslDefinition,
                      dslSentence.getDefinition() );
        assertEquals( 1,
                      dslSentence.getValues().size() );
        assertTrue( dslSentence.getValues().get( 0 ) instanceof DSLComplexVariableValue );
        DSLComplexVariableValue dslComplexVariableValue = (DSLComplexVariableValue) dslSentence.getValues().get( 0 );
        assertEquals( "AA",
                      dslComplexVariableValue.getValue() );
        assertEquals( "ENUM:Applicant.creditRating",
                      dslComplexVariableValue.getId() );
    }

    @Test
    public void testDSLExpansionRHS() {
        String drl = "rule \"rule1\"\n"
                + "when\n"
                + "$a : Applicant()"
                + "then\n"
                + "Set applicant name to Bob"
                + "end\n";

        final String dslDefinition = "Set applicant name to {name:\\w+ \\w+}";
        final String dslFile = "[then]" + dslDefinition + "=$a.setName( \"{name}\" )";

        final RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshalUsingDSL( drl,
                                                                                         new ArrayList<String>(),
                                                                                         dmo,
                                                                                         new String[]{ dslFile } );

        assertNotNull( m );

        assertTrue( m.lhs[ 0 ] instanceof FactPattern );
        FactPattern pattern = (FactPattern) m.lhs[ 0 ];
        assertEquals( "Applicant",
                      pattern.getFactType() );
        assertEquals( "$a",
                      pattern.getBoundName() );

        assertTrue( m.rhs[ 0 ] instanceof DSLSentence );

        DSLSentence dslSentence = (DSLSentence) m.rhs[ 0 ];
        assertEquals( dslDefinition,
                      dslSentence.getDefinition() );
        assertEquals( 1,
                      dslSentence.getValues().size() );
        assertTrue( dslSentence.getValues().get( 0 ) instanceof DSLComplexVariableValue );
        DSLComplexVariableValue dslComplexVariableValue = (DSLComplexVariableValue) dslSentence.getValues().get( 0 );
        assertEquals( "Bob",
                      dslComplexVariableValue.getValue() );
        assertEquals( "\\w+ \\w+",
                      dslComplexVariableValue.getId() );
    }

    @Test
    public void testFunctionCalls() {
        String drl =
                "package org.mortgages\n" +
                        "import java.lang.Number\n" +
                        "import java.lang.String\n" +
                        "rule \"rule1\"\n"
                        + "dialect \"mvel\"\n"
                        + "when\n"
                        + "  s : String()\n"
                        + "then\n"
                        + "  s.indexOf( s );\n"
                        + "  s.indexOf( 0 );\n"
                        + "end\n";

        Map<String, List<MethodInfo>> methodInformation = new HashMap<String, List<MethodInfo>>();
        List<MethodInfo> mapMethodInformation = new ArrayList<MethodInfo>();
        mapMethodInformation.add( new MethodInfo( "indexOf",
                                                  Arrays.asList( new String[]{ "String" } ),
                                                  "int",
                                                  null,
                                                  "String" ) );
        mapMethodInformation.add( new MethodInfo( "indexOf",
                                                  Arrays.asList( new String[]{ "Integer" } ),
                                                  "int",
                                                  null,
                                                  "String" ) );

        methodInformation.put( "java.lang.String", mapMethodInformation );

        when( dmo.getProjectMethodInformation() ).thenReturn( methodInformation );

        final RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal( drl, Collections.EMPTY_LIST, dmo );

        assertNotNull( m );

        assertEquals( 2, m.rhs.length );
        assertTrue( m.rhs[ 0 ] instanceof ActionCallMethod );
        assertTrue( m.rhs[ 1 ] instanceof ActionCallMethod );

        ActionCallMethod actionCallMethod1 = (ActionCallMethod) m.rhs[ 0 ];
        assertEquals( 1, actionCallMethod1.getState() );
        assertEquals( "indexOf", actionCallMethod1.getMethodName() );
        assertEquals( "s", actionCallMethod1.getVariable() );
        assertEquals( 1, actionCallMethod1.getFieldValues().length );
        assertEquals( "indexOf", actionCallMethod1.getFieldValues()[ 0 ].getField() );
        assertEquals( "s", actionCallMethod1.getFieldValues()[ 0 ].getValue() );
        assertEquals( FieldNatureType.TYPE_VARIABLE, actionCallMethod1.getFieldValues()[ 0 ].getNature() );
        assertEquals( "String", actionCallMethod1.getFieldValues()[ 0 ].getType() );

        ActionCallMethod actionCallMethod2 = (ActionCallMethod) m.rhs[ 1 ];
        assertEquals( 1, actionCallMethod2.getState() );
        assertEquals( "indexOf", actionCallMethod2.getMethodName() );
        assertEquals( "s", actionCallMethod2.getVariable() );
        assertEquals( 1, actionCallMethod2.getFieldValues().length );
        assertEquals( "indexOf", actionCallMethod2.getFieldValues()[ 0 ].getField() );
        assertEquals( "0", actionCallMethod2.getFieldValues()[ 0 ].getValue() );
        assertEquals( FieldNatureType.TYPE_LITERAL, actionCallMethod2.getFieldValues()[ 0 ].getNature() );
        assertEquals( "Numeric", actionCallMethod2.getFieldValues()[ 0 ].getType() );

    }

    @Test
    public void testRHSInsertFactWithFieldAsVariable() {
        //https://bugzilla.redhat.com/show_bug.cgi?id=1077212

        String drl = "package org.mortgages\n"
                + "import org.test.Person\n"
                + "rule \"variable\"\n"
                + "dialect \"mvel\"\n"
                + "when\n"
                + "Person( $f : field1 == 44 )\n"
                + "then\n"
                + "Person fact0 = new Person();\n"
                + "fact0.setField1( $f );\n"
                + "insert( fact0 );\n"
                + "end";

        addModelField( "org.test.Person",
                       "field1",
                       "java.lang.Integer",
                       DataType.TYPE_NUMERIC_INTEGER );

        final RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal( drl,
                                                                                 new ArrayList<String>(),
                                                                                 dmo );

        assertNotNull( m );

        assertEquals( 1,
                      m.lhs.length );
        IPattern p = m.lhs[ 0 ];
        assertTrue( p instanceof FactPattern );

        FactPattern fp = (FactPattern) p;
        assertEquals( "Person",
                      fp.getFactType() );

        assertEquals( 1,
                      fp.getConstraintList().getConstraints().length );
        assertTrue( fp.getConstraint( 0 ) instanceof SingleFieldConstraint );

        SingleFieldConstraint sfp = (SingleFieldConstraint) fp.getConstraint( 0 );
        assertEquals( "Person",
                      sfp.getFactType() );
        assertEquals( "field1",
                      sfp.getFieldName() );
        assertEquals( "==",
                      sfp.getOperator() );
        assertEquals( "44",
                      sfp.getValue() );
        assertEquals( BaseSingleFieldConstraint.TYPE_LITERAL,
                      sfp.getConstraintValueType() );
        assertEquals( "$f",
                      sfp.getFieldBinding() );

        assertEquals( 1,
                      m.rhs.length );
        IAction a = m.rhs[ 0 ];
        assertTrue( a instanceof ActionInsertFact );

        ActionInsertFact ap = (ActionInsertFact) a;
        assertEquals( "Person",
                      ap.getFactType() );
        assertEquals( "fact0",
                      ap.getBoundName() );

        assertEquals( 1,
                      ap.getFieldValues().length );
        ActionFieldValue afv = ap.getFieldValues()[ 0 ];
        assertEquals( "field1",
                      afv.getField() );
        assertEquals( FieldNatureType.TYPE_VARIABLE,
                      afv.getNature() );
        assertEquals( "=$f",
                      afv.getValue() );
        assertEquals( DataType.TYPE_NUMERIC_INTEGER,
                      afv.getType() );
    }

    @Test
    public void testRHSInsertFactWithFieldAsLiteral() {
        String drl = "package org.mortgages\n"
                + "import org.test.Person\n"
                + "rule \"variable\"\n"
                + "dialect \"mvel\"\n"
                + "when\n"
                + "Person( field1 == 44 )\n"
                + "then\n"
                + "Person fact0 = new Person();\n"
                + "fact0.setField1( 55 );\n"
                + "insert( fact0 );\n"
                + "end";

        addModelField( "org.test.Person",
                       "field1",
                       "java.lang.Integer",
                       DataType.TYPE_NUMERIC_INTEGER );

        final RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal( drl,
                                                                                 new ArrayList<String>(),
                                                                                 dmo );

        assertNotNull( m );

        assertEquals( 1,
                      m.lhs.length );
        IPattern p = m.lhs[ 0 ];
        assertTrue( p instanceof FactPattern );

        FactPattern fp = (FactPattern) p;
        assertEquals( "Person",
                      fp.getFactType() );

        assertEquals( 1,
                      fp.getConstraintList().getConstraints().length );
        assertTrue( fp.getConstraint( 0 ) instanceof SingleFieldConstraint );

        SingleFieldConstraint sfp = (SingleFieldConstraint) fp.getConstraint( 0 );
        assertEquals( "Person",
                      sfp.getFactType() );
        assertEquals( "field1",
                      sfp.getFieldName() );
        assertEquals( "==",
                      sfp.getOperator() );
        assertEquals( "44",
                      sfp.getValue() );
        assertEquals( BaseSingleFieldConstraint.TYPE_LITERAL,
                      sfp.getConstraintValueType() );

        assertEquals( 1,
                      m.rhs.length );
        IAction a = m.rhs[ 0 ];
        assertTrue( a instanceof ActionInsertFact );

        ActionInsertFact ap = (ActionInsertFact) a;
        assertEquals( "Person",
                      ap.getFactType() );
        assertEquals( "fact0",
                      ap.getBoundName() );

        assertEquals( 1,
                      ap.getFieldValues().length );
        ActionFieldValue afv = ap.getFieldValues()[ 0 ];
        assertEquals( "field1",
                      afv.getField() );
        assertEquals( FieldNatureType.TYPE_LITERAL,
                      afv.getNature() );
        assertEquals( "55",
                      afv.getValue() );
        assertEquals( DataType.TYPE_NUMERIC_INTEGER,
                      afv.getType() );
    }

    @Test
    public void testRHSUpdateFactWithFieldAsVariable() {
        //https://bugzilla.redhat.com/show_bug.cgi?id=1077212

        String drl = "package org.mortgages\n"
                + "import org.test.Person\n"
                + "rule \"variable\"\n"
                + "dialect \"mvel\"\n"
                + "when\n"
                + "$p : Person( $f : field1 == 44 )\n"
                + "then\n"
                + "$p.setField1( $f );\n"
                + "update( $p );\n"
                + "end";

        addModelField( "org.test.Person",
                       "field1",
                       "java.lang.Integer",
                       DataType.TYPE_NUMERIC_INTEGER );

        final RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal( drl,
                                                                                 new ArrayList<String>(),
                                                                                 dmo );

        assertNotNull( m );

        assertEquals( 1,
                      m.lhs.length );
        IPattern p = m.lhs[ 0 ];
        assertTrue( p instanceof FactPattern );

        FactPattern fp = (FactPattern) p;
        assertEquals( "Person",
                      fp.getFactType() );
        assertEquals( "$p",
                      fp.getBoundName() );

        assertEquals( 1,
                      fp.getConstraintList().getConstraints().length );
        assertTrue( fp.getConstraint( 0 ) instanceof SingleFieldConstraint );

        SingleFieldConstraint sfp = (SingleFieldConstraint) fp.getConstraint( 0 );
        assertEquals( "Person",
                      sfp.getFactType() );
        assertEquals( "field1",
                      sfp.getFieldName() );
        assertEquals( "==",
                      sfp.getOperator() );
        assertEquals( "44",
                      sfp.getValue() );
        assertEquals( BaseSingleFieldConstraint.TYPE_LITERAL,
                      sfp.getConstraintValueType() );
        assertEquals( "$f",
                      sfp.getFieldBinding() );

        assertEquals( 1,
                      m.rhs.length );
        IAction a = m.rhs[ 0 ];
        assertTrue( a instanceof ActionUpdateField );

        ActionUpdateField ap = (ActionUpdateField) a;
        assertEquals( "$p",
                      ap.getVariable() );

        assertEquals( 1,
                      ap.getFieldValues().length );
        ActionFieldValue afv = ap.getFieldValues()[ 0 ];
        assertEquals( "field1",
                      afv.getField() );
        assertEquals( FieldNatureType.TYPE_VARIABLE,
                      afv.getNature() );
        assertEquals( "=$f",
                      afv.getValue() );
        assertEquals( DataType.TYPE_NUMERIC_INTEGER,
                      afv.getType() );
    }

    @Test
    public void testRHSUpdateFactWithFieldAsLiteral() {
        String drl = "package org.mortgages\n"
                + "import org.test.Person\n"
                + "rule \"variable\"\n"
                + "dialect \"mvel\"\n"
                + "when\n"
                + "$p : Person()\n"
                + "then\n"
                + "$p.setField1( 55 );\n"
                + "update( $p );\n"
                + "end";

        addModelField( "org.test.Person",
                       "field1",
                       "java.lang.Integer",
                       DataType.TYPE_NUMERIC_INTEGER );

        final RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal( drl,
                                                                                 new ArrayList<String>(),
                                                                                 dmo );

        assertNotNull( m );

        assertEquals( 1,
                      m.lhs.length );
        IPattern p = m.lhs[ 0 ];
        assertTrue( p instanceof FactPattern );

        FactPattern fp = (FactPattern) p;
        assertEquals( "Person",
                      fp.getFactType() );

        assertEquals( 0,
                      fp.getNumberOfConstraints() );

        assertEquals( 1,
                      m.rhs.length );
        IAction a = m.rhs[ 0 ];
        assertTrue( a instanceof ActionUpdateField );

        ActionUpdateField ap = (ActionUpdateField) a;
        assertEquals( "$p",
                      ap.getVariable() );

        assertEquals( 1,
                      ap.getFieldValues().length );
        ActionFieldValue afv = ap.getFieldValues()[ 0 ];
        assertEquals( "field1",
                      afv.getField() );
        assertEquals( FieldNatureType.TYPE_LITERAL,
                      afv.getNature() );
        assertEquals( "55",
                      afv.getValue() );
        assertEquals( DataType.TYPE_NUMERIC_INTEGER,
                      afv.getType() );
    }

    @Test
    public void testRHSInsertFactWithFieldAsVariableSamePackage() {
        //https://bugzilla.redhat.com/show_bug.cgi?id=1077212

        String drl = "package org.test\n"
                + "rule \"variable\"\n"
                + "dialect \"mvel\"\n"
                + "when\n"
                + "Person( $f : field1 == 44 )\n"
                + "then\n"
                + "Person fact0 = new Person();\n"
                + "fact0.setField1( $f );\n"
                + "insert( fact0 );\n"
                + "end";

        addModelField( "org.test.Person",
                       "field1",
                       "java.lang.Integer",
                       DataType.TYPE_NUMERIC_INTEGER );

        when( dmo.getPackageName() ).thenReturn( "org.test" );

        final RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal( drl,
                                                                                 new ArrayList<String>(),
                                                                                 dmo );

        assertNotNull( m );

        assertEquals( 1,
                      m.lhs.length );
        IPattern p = m.lhs[ 0 ];
        assertTrue( p instanceof FactPattern );

        FactPattern fp = (FactPattern) p;
        assertEquals( "Person",
                      fp.getFactType() );

        assertEquals( 1,
                      fp.getConstraintList().getConstraints().length );
        assertTrue( fp.getConstraint( 0 ) instanceof SingleFieldConstraint );

        SingleFieldConstraint sfp = (SingleFieldConstraint) fp.getConstraint( 0 );
        assertEquals( "Person",
                      sfp.getFactType() );
        assertEquals( "field1",
                      sfp.getFieldName() );
        assertEquals( "==",
                      sfp.getOperator() );
        assertEquals( "44",
                      sfp.getValue() );
        assertEquals( BaseSingleFieldConstraint.TYPE_LITERAL,
                      sfp.getConstraintValueType() );
        assertEquals( "$f",
                      sfp.getFieldBinding() );

        assertEquals( 1,
                      m.rhs.length );
        IAction a = m.rhs[ 0 ];
        assertTrue( a instanceof ActionInsertFact );

        ActionInsertFact ap = (ActionInsertFact) a;
        assertEquals( "Person",
                      ap.getFactType() );
        assertEquals( "fact0",
                      ap.getBoundName() );

        assertEquals( 1,
                      ap.getFieldValues().length );
        ActionFieldValue afv = ap.getFieldValues()[ 0 ];
        assertEquals( "field1",
                      afv.getField() );
        assertEquals( FieldNatureType.TYPE_VARIABLE,
                      afv.getNature() );
        assertEquals( "=$f",
                      afv.getValue() );
        assertEquals( DataType.TYPE_NUMERIC_INTEGER,
                      afv.getType() );
    }

    @Test
    public void testRHSInsertFactWithFieldAsLiteralSamePackage() {
        String drl = "package org.test\n"
                + "rule \"variable\"\n"
                + "dialect \"mvel\"\n"
                + "when\n"
                + "Person( field1 == 44 )\n"
                + "then\n"
                + "Person fact0 = new Person();\n"
                + "fact0.setField1( 55 );\n"
                + "insert( fact0 );\n"
                + "end";

        addModelField( "org.test.Person",
                       "field1",
                       "java.lang.Integer",
                       DataType.TYPE_NUMERIC_INTEGER );

        when( dmo.getPackageName() ).thenReturn( "org.test" );

        final RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal( drl,
                                                                                 new ArrayList<String>(),
                                                                                 dmo );

        assertNotNull( m );

        assertEquals( 1,
                      m.lhs.length );
        IPattern p = m.lhs[ 0 ];
        assertTrue( p instanceof FactPattern );

        FactPattern fp = (FactPattern) p;
        assertEquals( "Person",
                      fp.getFactType() );

        assertEquals( 1,
                      fp.getConstraintList().getConstraints().length );
        assertTrue( fp.getConstraint( 0 ) instanceof SingleFieldConstraint );

        SingleFieldConstraint sfp = (SingleFieldConstraint) fp.getConstraint( 0 );
        assertEquals( "Person",
                      sfp.getFactType() );
        assertEquals( "field1",
                      sfp.getFieldName() );
        assertEquals( "==",
                      sfp.getOperator() );
        assertEquals( "44",
                      sfp.getValue() );
        assertEquals( BaseSingleFieldConstraint.TYPE_LITERAL,
                      sfp.getConstraintValueType() );

        assertEquals( 1,
                      m.rhs.length );
        IAction a = m.rhs[ 0 ];
        assertTrue( a instanceof ActionInsertFact );

        ActionInsertFact ap = (ActionInsertFact) a;
        assertEquals( "Person",
                      ap.getFactType() );
        assertEquals( "fact0",
                      ap.getBoundName() );

        assertEquals( 1,
                      ap.getFieldValues().length );
        ActionFieldValue afv = ap.getFieldValues()[ 0 ];
        assertEquals( "field1",
                      afv.getField() );
        assertEquals( FieldNatureType.TYPE_LITERAL,
                      afv.getNature() );
        assertEquals( "55",
                      afv.getValue() );
        assertEquals( DataType.TYPE_NUMERIC_INTEGER,
                      afv.getType() );
    }

    @Test
    public void testRHSUpdateFactWithFieldAsVariableSamePackage() {
        //https://bugzilla.redhat.com/show_bug.cgi?id=1077212

        String drl = "package org.test\n"
                + "rule \"variable\"\n"
                + "dialect \"mvel\"\n"
                + "when\n"
                + "$p : Person( $f : field1 == 44 )\n"
                + "then\n"
                + "$p.setField1( $f );\n"
                + "update( $p );\n"
                + "end";

        addModelField( "org.test.Person",
                       "field1",
                       "java.lang.Integer",
                       DataType.TYPE_NUMERIC_INTEGER );

        when( dmo.getPackageName() ).thenReturn( "org.test" );

        final RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal( drl,
                                                                                 new ArrayList<String>(),
                                                                                 dmo );

        assertNotNull( m );

        assertEquals( 1,
                      m.lhs.length );
        IPattern p = m.lhs[ 0 ];
        assertTrue( p instanceof FactPattern );

        FactPattern fp = (FactPattern) p;
        assertEquals( "Person",
                      fp.getFactType() );
        assertEquals( "$p",
                      fp.getBoundName() );

        assertEquals( 1,
                      fp.getConstraintList().getConstraints().length );
        assertTrue( fp.getConstraint( 0 ) instanceof SingleFieldConstraint );

        SingleFieldConstraint sfp = (SingleFieldConstraint) fp.getConstraint( 0 );
        assertEquals( "Person",
                      sfp.getFactType() );
        assertEquals( "field1",
                      sfp.getFieldName() );
        assertEquals( "==",
                      sfp.getOperator() );
        assertEquals( "44",
                      sfp.getValue() );
        assertEquals( BaseSingleFieldConstraint.TYPE_LITERAL,
                      sfp.getConstraintValueType() );
        assertEquals( "$f",
                      sfp.getFieldBinding() );

        assertEquals( 1,
                      m.rhs.length );
        IAction a = m.rhs[ 0 ];
        assertTrue( a instanceof ActionUpdateField );

        ActionUpdateField ap = (ActionUpdateField) a;
        assertEquals( "$p",
                      ap.getVariable() );

        assertEquals( 1,
                      ap.getFieldValues().length );
        ActionFieldValue afv = ap.getFieldValues()[ 0 ];
        assertEquals( "field1",
                      afv.getField() );
        assertEquals( FieldNatureType.TYPE_VARIABLE,
                      afv.getNature() );
        assertEquals( "=$f",
                      afv.getValue() );
        assertEquals( DataType.TYPE_NUMERIC_INTEGER,
                      afv.getType() );
    }

    @Test
    public void testRHSUpdateFactWithFieldAsLiteralSamePackage() {
        String drl = "package org.test\n"
                + "rule \"variable\"\n"
                + "dialect \"mvel\"\n"
                + "when\n"
                + "$p : Person()\n"
                + "then\n"
                + "$p.setField1( 55 );\n"
                + "update( $p );\n"
                + "end";

        addModelField( "org.test.Person",
                       "field1",
                       "java.lang.Integer",
                       DataType.TYPE_NUMERIC_INTEGER );

        when( dmo.getPackageName() ).thenReturn( "org.test" );

        final RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal( drl,
                                                                                 new ArrayList<String>(),
                                                                                 dmo );

        assertNotNull( m );

        assertEquals( 1,
                      m.lhs.length );
        IPattern p = m.lhs[ 0 ];
        assertTrue( p instanceof FactPattern );

        FactPattern fp = (FactPattern) p;
        assertEquals( "Person",
                      fp.getFactType() );

        assertEquals( 0,
                      fp.getNumberOfConstraints() );

        assertEquals( 1,
                      m.rhs.length );
        IAction a = m.rhs[ 0 ];
        assertTrue( a instanceof ActionUpdateField );

        ActionUpdateField ap = (ActionUpdateField) a;
        assertEquals( "$p",
                      ap.getVariable() );

        assertEquals( 1,
                      ap.getFieldValues().length );
        ActionFieldValue afv = ap.getFieldValues()[ 0 ];
        assertEquals( "field1",
                      afv.getField() );
        assertEquals( FieldNatureType.TYPE_LITERAL,
                      afv.getNature() );
        assertEquals( "55",
                      afv.getValue() );
        assertEquals( DataType.TYPE_NUMERIC_INTEGER,
                      afv.getType() );
    }

    @Test
    public void testRHSUpdateFactWithFormula() {
        //https://bugzilla.redhat.com/show_bug.cgi?id=1079253
        String drl = "package org.mortgages;\n"
                + "import org.test.ShoppingCart\n"
                + "rule \"r1\"\n"
                + "dialect \"mvel\"\n"
                + "when\n"
                + "$sc : ShoppingCart( )\n"
                + "then\n"
                + "$sc.setCartItemPromoSavings( ($sc.cartItemPromoSavings == 0.0) ? 0.0 : $sc.cartItemPromoSavings * -1 );\n"
                + "update( $sc );\n"
                + "end\n";

        addModelField( "org.test.ShoppingCart",
                       "cartItemPromoSavings",
                       "java.lang.Double",
                       DataType.TYPE_NUMERIC_DOUBLE );

        when( dmo.getPackageName() ).thenReturn( "org.test" );

        final RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal( drl,
                                                                                 new ArrayList<String>(),
                                                                                 dmo );

        assertNotNull( m );

        assertEquals( 1,
                      m.lhs.length );
        IPattern p = m.lhs[ 0 ];
        assertTrue( p instanceof FactPattern );

        FactPattern fp = (FactPattern) p;
        assertEquals( "ShoppingCart",
                      fp.getFactType() );
        assertEquals( "$sc",
                      fp.getBoundName() );

        assertEquals( 0,
                      fp.getNumberOfConstraints() );

        assertEquals( 1,
                      m.rhs.length );
        IAction a = m.rhs[ 0 ];
        assertTrue( a instanceof ActionUpdateField );

        ActionUpdateField ap = (ActionUpdateField) a;
        assertEquals( "$sc",
                      ap.getVariable() );

        assertEquals( 1,
                      ap.getFieldValues().length );
        ActionFieldValue afv = ap.getFieldValues()[ 0 ];
        assertEquals( "cartItemPromoSavings",
                      afv.getField() );
        assertEquals( "($sc.cartItemPromoSavings == 0.0) ? 0.0 : $sc.cartItemPromoSavings * -1",
                      afv.getValue() );
        assertEquals( FieldNatureType.TYPE_FORMULA,
                      afv.getNature() );
    }

    @Test
    public void testRHSUpdateFactWithFormulaSamePackage() {
        //https://bugzilla.redhat.com/show_bug.cgi?id=1079253
        String drl = "package org.test;\n"
                + "rule \"r1\"\n"
                + "dialect \"mvel\"\n"
                + "when\n"
                + "$sc : ShoppingCart( )\n"
                + "then\n"
                + "$sc.setCartItemPromoSavings( ($sc.cartItemPromoSavings == 0.0) ? 0.0 : $sc.cartItemPromoSavings * -1 );\n"
                + "update( $sc );\n"
                + "end\n";

        addModelField( "org.test.ShoppingCart",
                       "cartItemPromoSavings",
                       "java.lang.Double",
                       DataType.TYPE_NUMERIC_DOUBLE );

        when( dmo.getPackageName() ).thenReturn( "org.test" );

        final RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal( drl,
                                                                                 new ArrayList<String>(),
                                                                                 dmo );

        assertNotNull( m );

        assertEquals( 1,
                      m.lhs.length );
        IPattern p = m.lhs[ 0 ];
        assertTrue( p instanceof FactPattern );

        FactPattern fp = (FactPattern) p;
        assertEquals( "ShoppingCart",
                      fp.getFactType() );
        assertEquals( "$sc",
                      fp.getBoundName() );

        assertEquals( 0,
                      fp.getNumberOfConstraints() );

        assertEquals( 1,
                      m.rhs.length );
        IAction a = m.rhs[ 0 ];
        assertTrue( a instanceof ActionUpdateField );

        ActionUpdateField ap = (ActionUpdateField) a;
        assertEquals( "$sc",
                      ap.getVariable() );

        assertEquals( 1,
                      ap.getFieldValues().length );
        ActionFieldValue afv = ap.getFieldValues()[ 0 ];
        assertEquals( "cartItemPromoSavings",
                      afv.getField() );
        assertEquals( "($sc.cartItemPromoSavings == 0.0) ? 0.0 : $sc.cartItemPromoSavings * -1",
                      afv.getValue() );
        assertEquals( FieldNatureType.TYPE_FORMULA,
                      afv.getNature() );
    }

    @Test
    public void testFunctionsWithVariableParameters() throws Exception {
        String drl = "package org.mortgages;\n" +
                "rule \"test\"\n" +
                " dialect \"mvel\"\n" +
                " when\n" +
                "  Calculator( s : summer)\n" +
                "  Applicant( $age : age)\n" +
                " then\n" +
                "  s.sum( $age, $age );\n" +
                "end";

        addModelField(
                "Calculator",
                "summer",
                "Summer",
                "Summer" );

        addModelField(
                "Applicant",
                "age",
                "java.lang.Integer",
                DataType.TYPE_NUMERIC_INTEGER );

        HashMap<String, List<MethodInfo>> map = new HashMap<String, List<MethodInfo>>();
        ArrayList<MethodInfo> methodInfos = new ArrayList<MethodInfo>();
        ArrayList<String> params = new ArrayList<String>();
        params.add( "Integer" );
        params.add( "Integer" );
        methodInfos.add( new MethodInfo( "sum", params, "java.lang.Integer", null, "Summer" ) );
        map.put( "Calculator", methodInfos );

        when(
                dmo.getProjectMethodInformation()
            ).thenReturn(
                map
                        );

        final RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal( drl,
                                                                                 new ArrayList<String>(),
                                                                                 dmo );

        assertNotNull( m );

        assertEquals( 2,
                      m.lhs.length );

        assertEquals( 1,
                      m.rhs.length );

        ActionCallMethod actionCallMethod = (ActionCallMethod) m.rhs[ 0 ];
        assertEquals( "sum", actionCallMethod.getMethodName() );
        assertEquals( "s", actionCallMethod.getVariable() );
        assertEquals( 2, actionCallMethod.getFieldValues().length );

        assertEquals( "sum", actionCallMethod.getFieldValue( 0 ).getField() );
        assertEquals( "$age", actionCallMethod.getFieldValue( 0 ).getValue() );
        assertEquals( 2, actionCallMethod.getFieldValue( 0 ).getNature() );
        assertEquals( "Integer", actionCallMethod.getFieldValue( 0 ).getType() );

        assertEquals( "sum", actionCallMethod.getFieldValue( 1 ).getField() );
        assertEquals( "$age", actionCallMethod.getFieldValue( 1 ).getValue() );
        assertEquals( 2, actionCallMethod.getFieldValue( 1 ).getNature() );
        assertEquals( "Integer", actionCallMethod.getFieldValue( 1 ).getType() );
    }

    @Test
    public void testListSize() throws Exception {
        String drl = "" +
                "package org.mortgages;\n" +
                "import java.lang.Number;\n" +
                "rule \"Test\"\n" +
                "  dialect \"mvel\"\n" +
                "  when\n" +
                "    Person( addresses.size() == 0 )\n" +
                "  then\n" +
                "end\n";

        addModelField( "Person",
                       "addresses",
                       "java.util.List",
                       DataType.TYPE_COLLECTION );

        addMethodInformation( "java.util.List",
                              "size",
                              new ArrayList<String>(),
                              "int",
                              null,
                              DataType.TYPE_NUMERIC_INTEGER );

        HashMap<String, String> map = new HashMap<String, String>();
        map.put( "Person#addresses",
                 "Address" );
        when( dmo.getProjectFieldParametersType() ).thenReturn( map );

        final RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal( drl,
                                                                                 new ArrayList<String>(),
                                                                                 dmo );

        assertEquals( 1,
                      m.lhs.length );
        FactPattern pattern = (FactPattern) m.lhs[ 0 ];
        assertEquals( 1,
                      pattern.getConstraintList().getConstraints().length );
        SingleFieldConstraintEBLeftSide constraint = (SingleFieldConstraintEBLeftSide) pattern.getConstraint( 0 );
        assertEquals( "size",
                      constraint.getFieldName() );
        assertEquals( "int",
                      constraint.getFieldType() );
        assertEquals( "0",
                      constraint.getValue() );
        assertEquals( "==",
                      constraint.getOperator() );
        assertEquals( 1,
                      constraint.getConstraintValueType() );

        assertTrue( constraint.getExpressionLeftSide().getParts().get( 0 ) instanceof ExpressionUnboundFact );
        assertTrue( constraint.getExpressionLeftSide().getParts().get( 1 ) instanceof ExpressionCollection );
        assertTrue( constraint.getExpressionLeftSide().getParts().get( 2 ) instanceof ExpressionMethod );

        assertEqualsIgnoreWhitespace( drl, RuleModelDRLPersistenceImpl.getInstance().marshal( m ) );
    }

    @Test
    public void testCollectWithFreeFormDRL_MethodsDefined() throws Exception {
        //https://bugzilla.redhat.com/show_bug.cgi?id=1060816
        String drl = "package org.sample.resourceassignment;\n" +
                "rule \"r1\"\n" +
                "dialect \"mvel\"\n" +
                "when\n" +
                "$trans : Transactions()\n" +
                "$transactions : java.util.List( eval( size > 0 ) ) from collect ( Transaction() from $trans.getRecCategorization().get(\"APES-01\") )\n" +
                "then\n" +
                "end";

        addModelField( "Transactions",
                       "recCategorization",
                       "java.util.Map",
                       DataType.TYPE_COLLECTION );

        addMethodInformation( "Transactions",
                              "getRecCategorization()",
                              new ArrayList<String>(),
                              "java.util.Map",
                              null,
                              DataType.TYPE_COLLECTION );

        addMethodInformation( "java.util.Map",
                              "get",
                              new ArrayList<String>() {{
                                  add( "p0" );
                              }},
                              "java.lang.String",
                              null,
                              DataType.TYPE_STRING );

        final RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal( drl,
                                                                                 new ArrayList<String>(),
                                                                                 dmo );

        assertNotNull( m );

        assertEquals( 2,
                      m.lhs.length );
        IPattern p0 = m.lhs[ 0 ];
        assertTrue( p0 instanceof FactPattern );
        FactPattern fp0 = (FactPattern) p0;
        assertEquals( "Transactions",
                      fp0.getFactType() );
        assertEquals( "$trans",
                      fp0.getBoundName() );
        assertEquals( 0,
                      fp0.getNumberOfConstraints() );

        IPattern p1 = m.lhs[ 1 ];
        assertTrue( p1 instanceof FromCollectCompositeFactPattern );
        FromCollectCompositeFactPattern fp1 = (FromCollectCompositeFactPattern) p1;
        assertEquals( "java.util.List",
                      fp1.getFactPattern().getFactType() );
        assertEquals( "$transactions",
                      fp1.getFactPattern().getBoundName() );
        assertEquals( 1,
                      fp1.getFactPattern().getNumberOfConstraints() );
        assertTrue( fp1.getFactPattern().getConstraint( 0 ) instanceof SingleFieldConstraint );
        SingleFieldConstraint fp1sfc = (SingleFieldConstraint) fp1.getFactPattern().getConstraint( 0 );
        assertEquals( "size > 0",
                      fp1sfc.getValue() );
        assertEquals( BaseSingleFieldConstraint.TYPE_PREDICATE,
                      fp1sfc.getConstraintValueType() );

        assertTrue( fp1.getRightPattern() instanceof FromCompositeFactPattern );
        FromCompositeFactPattern fp2 = (FromCompositeFactPattern) fp1.getRightPattern();
        assertNotNull( fp2.getFactPattern() );

        FactPattern fp3 = fp2.getFactPattern();
        assertEquals( "Transaction",
                      fp3.getFactType() );
        assertEquals( 0,
                      fp3.getNumberOfConstraints() );

        assertNotNull( fp2.getExpression() );
        ExpressionFormLine efl = fp2.getExpression();
        assertEquals( 3,
                      efl.getParts().size() );
        assertTrue( efl.getParts().get( 0 ) instanceof ExpressionVariable );
        ExpressionVariable ev = (ExpressionVariable) efl.getParts().get( 0 );
        assertEquals( "$trans",
                      ev.getName() );
        assertEquals( "Transactions",
                      ev.getClassType() );
        assertTrue( efl.getParts().get( 1 ) instanceof ExpressionMethod );
        ExpressionMethod em = (ExpressionMethod) efl.getParts().get( 1 );
        assertEquals( "getRecCategorization()",
                      em.getName() );
        assertEquals( "java.util.Map",
                      em.getClassType() );
        assertEquals( DataType.TYPE_COLLECTION,
                      em.getGenericType() );
        assertTrue( efl.getParts().get( 2 ) instanceof ExpressionText );
        ExpressionText et = (ExpressionText) efl.getParts().get( 2 );
        assertEquals( "get(\"APES-01\")",
                      et.getName() );
        assertEquals( "java.lang.String",
                      et.getClassType() );
        assertEquals( DataType.TYPE_STRING,
                      et.getGenericType() );

        assertEquals( 0,
                      m.rhs.length );
    }

    @Test
    public void testCollectWithFreeFormDRL_MethodsUndefined() throws Exception {
        //https://bugzilla.redhat.com/show_bug.cgi?id=1060816
        String drl = "package org.sample.resourceassignment;\n" +
                "rule \"r1\"\n" +
                "dialect \"mvel\"\n" +
                "when\n" +
                "$trans : Transactions()\n" +
                "$transactions : java.util.List( eval( size > 0 ) ) from collect ( Transaction() from $trans.getRecCategorization().get(\"APES-01\") )\n" +
                "then\n" +
                "end";

        addModelField( "Transactions",
                       "recCategorization",
                       "java.util.Map",
                       DataType.TYPE_COLLECTION );

        addMethodInformation( "java.util.Map",
                              "get",
                              new ArrayList<String>() {{
                                  add( "p0" );
                              }},
                              "java.lang.String",
                              null,
                              DataType.TYPE_STRING );

        final RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal( drl,
                                                                                 new ArrayList<String>(),
                                                                                 dmo );

        assertNotNull( m );

        assertEquals( 2,
                      m.lhs.length );
        IPattern p0 = m.lhs[ 0 ];
        assertTrue( p0 instanceof FactPattern );
        FactPattern fp0 = (FactPattern) p0;
        assertEquals( "Transactions",
                      fp0.getFactType() );
        assertEquals( "$trans",
                      fp0.getBoundName() );
        assertEquals( 0,
                      fp0.getNumberOfConstraints() );

        IPattern p1 = m.lhs[ 1 ];
        assertTrue( p1 instanceof FromCollectCompositeFactPattern );
        FromCollectCompositeFactPattern fp1 = (FromCollectCompositeFactPattern) p1;
        assertEquals( "java.util.List",
                      fp1.getFactPattern().getFactType() );
        assertEquals( "$transactions",
                      fp1.getFactPattern().getBoundName() );
        assertEquals( 1,
                      fp1.getFactPattern().getNumberOfConstraints() );
        assertTrue( fp1.getFactPattern().getConstraint( 0 ) instanceof SingleFieldConstraint );
        SingleFieldConstraint fp1sfc = (SingleFieldConstraint) fp1.getFactPattern().getConstraint( 0 );
        assertEquals( "size > 0",
                      fp1sfc.getValue() );
        assertEquals( BaseSingleFieldConstraint.TYPE_PREDICATE,
                      fp1sfc.getConstraintValueType() );

        assertTrue( fp1.getRightPattern() instanceof FromCompositeFactPattern );
        FromCompositeFactPattern fp2 = (FromCompositeFactPattern) fp1.getRightPattern();
        assertNotNull( fp2.getFactPattern() );

        FactPattern fp3 = fp2.getFactPattern();
        assertEquals( "Transaction",
                      fp3.getFactType() );
        assertEquals( 0,
                      fp3.getNumberOfConstraints() );

        assertNotNull( fp2.getExpression() );
        ExpressionFormLine efl = fp2.getExpression();
        assertEquals( 3,
                      efl.getParts().size() );
        assertTrue( efl.getParts().get( 0 ) instanceof ExpressionVariable );
        ExpressionVariable ev = (ExpressionVariable) efl.getParts().get( 0 );
        assertEquals( "$trans",
                      ev.getName() );
        assertEquals( "Transactions",
                      ev.getClassType() );
        assertTrue( efl.getParts().get( 1 ) instanceof ExpressionText );
        ExpressionText et1 = (ExpressionText) efl.getParts().get( 1 );
        assertEquals( "getRecCategorization()",
                      et1.getName() );
        assertEquals( "java.lang.String",
                      et1.getClassType() );
        assertEquals( DataType.TYPE_STRING,
                      et1.getGenericType() );
        assertTrue( efl.getParts().get( 2 ) instanceof ExpressionText );
        ExpressionText et2 = (ExpressionText) efl.getParts().get( 2 );
        assertEquals( "get(\"APES-01\")",
                      et2.getName() );
        assertEquals( "java.lang.String",
                      et2.getClassType() );
        assertEquals( DataType.TYPE_STRING,
                      et2.getGenericType() );

        assertEquals( 0,
                      m.rhs.length );
    }

    @Test
    public void testLHSInOperatorFieldNameNotContainingInLiteral() {
        String drl = "package org.test\n"
                + "rule \"in\"\n"
                + "dialect \"mvel\"\n"
                + "when\n"
                + "Person( field1 in (1, 2) )\n"
                + "then\n"
                + "end";

        addModelField( "org.test.Person",
                       "field1",
                       "java.lang.Integer",
                       DataType.TYPE_NUMERIC_INTEGER );

        when( dmo.getPackageName() ).thenReturn( "org.test" );

        final RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal( drl,
                                                                                 new ArrayList<String>(),
                                                                                 dmo );

        assertNotNull( m );

        assertEquals( 1,
                      m.lhs.length );
        IPattern p = m.lhs[ 0 ];
        assertTrue( p instanceof FactPattern );

        FactPattern fp = (FactPattern) p;
        assertEquals( "Person",
                      fp.getFactType() );

        assertEquals( 1,
                      fp.getConstraintList().getConstraints().length );
        assertTrue( fp.getConstraint( 0 ) instanceof SingleFieldConstraint );

        SingleFieldConstraint sfp = (SingleFieldConstraint) fp.getConstraint( 0 );
        assertEquals( "Person",
                      sfp.getFactType() );
        assertEquals( "field1",
                      sfp.getFieldName() );
        assertEquals( "in",
                      sfp.getOperator() );
        assertEquals( "1, 2",
                      sfp.getValue() );
        assertEquals( BaseSingleFieldConstraint.TYPE_LITERAL,
                      sfp.getConstraintValueType() );
    }

    @Test
    public void testLHSInOperatorFieldNameContainingInLiteral() {
        String drl = "package org.test\n"
                + "rule \"in\"\n"
                + "dialect \"mvel\"\n"
                + "when\n"
                + "Person( rating in (1, 2) )\n"
                + "then\n"
                + "end";

        addModelField( "org.test.Person",
                       "rating",
                       "java.lang.Integer",
                       DataType.TYPE_NUMERIC_INTEGER );

        when( dmo.getPackageName() ).thenReturn( "org.test" );

        final RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal( drl,
                                                                                 new ArrayList<String>(),
                                                                                 dmo );

        assertNotNull( m );

        assertEquals( 1,
                      m.lhs.length );
        IPattern p = m.lhs[ 0 ];
        assertTrue( p instanceof FactPattern );

        FactPattern fp = (FactPattern) p;
        assertEquals( "Person",
                      fp.getFactType() );

        assertEquals( 1,
                      fp.getConstraintList().getConstraints().length );
        assertTrue( fp.getConstraint( 0 ) instanceof SingleFieldConstraint );

        SingleFieldConstraint sfp = (SingleFieldConstraint) fp.getConstraint( 0 );
        assertEquals( "Person",
                      sfp.getFactType() );
        assertEquals( "rating",
                      sfp.getFieldName() );
        assertEquals( "in",
                      sfp.getOperator() );
        assertEquals( "1, 2",
                      sfp.getValue() );
        assertEquals( BaseSingleFieldConstraint.TYPE_LITERAL,
                      sfp.getConstraintValueType() );
    }

    @Test
    public void testLHSInOperatorFieldNameNotContainingNotInLiteral() {
        String drl = "package org.test\n"
                + "rule \"in\"\n"
                + "dialect \"mvel\"\n"
                + "when\n"
                + "Person( field1 not in (1, 2) )\n"
                + "then\n"
                + "end";

        addModelField( "org.test.Person",
                       "field1",
                       "java.lang.Integer",
                       DataType.TYPE_NUMERIC_INTEGER );

        when( dmo.getPackageName() ).thenReturn( "org.test" );

        final RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal( drl,
                                                                                 new ArrayList<String>(),
                                                                                 dmo );

        assertNotNull( m );

        assertEquals( 1,
                      m.lhs.length );
        IPattern p = m.lhs[ 0 ];
        assertTrue( p instanceof FactPattern );

        FactPattern fp = (FactPattern) p;
        assertEquals( "Person",
                      fp.getFactType() );

        assertEquals( 1,
                      fp.getConstraintList().getConstraints().length );
        assertTrue( fp.getConstraint( 0 ) instanceof SingleFieldConstraint );

        SingleFieldConstraint sfp = (SingleFieldConstraint) fp.getConstraint( 0 );
        assertEquals( "Person",
                      sfp.getFactType() );
        assertEquals( "field1",
                      sfp.getFieldName() );
        assertEquals( "not in",
                      sfp.getOperator() );
        assertEquals( "1, 2",
                      sfp.getValue() );
        assertEquals( BaseSingleFieldConstraint.TYPE_LITERAL,
                      sfp.getConstraintValueType() );
    }

    @Test
    public void testLHSInOperatorFieldNameContainingNotInLiteral() {
        String drl = "package org.test\n"
                + "rule \"in\"\n"
                + "dialect \"mvel\"\n"
                + "when\n"
                + "Person( rating not in (1, 2) )\n"
                + "then\n"
                + "end";

        addModelField( "org.test.Person",
                       "rating",
                       "java.lang.Integer",
                       DataType.TYPE_NUMERIC_INTEGER );

        when( dmo.getPackageName() ).thenReturn( "org.test" );

        final RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal( drl,
                                                                                 new ArrayList<String>(),
                                                                                 dmo );

        assertNotNull( m );

        assertEquals( 1,
                      m.lhs.length );
        IPattern p = m.lhs[ 0 ];
        assertTrue( p instanceof FactPattern );

        FactPattern fp = (FactPattern) p;
        assertEquals( "Person",
                      fp.getFactType() );

        assertEquals( 1,
                      fp.getConstraintList().getConstraints().length );
        assertTrue( fp.getConstraint( 0 ) instanceof SingleFieldConstraint );

        SingleFieldConstraint sfp = (SingleFieldConstraint) fp.getConstraint( 0 );
        assertEquals( "Person",
                      sfp.getFactType() );
        assertEquals( "rating",
                      sfp.getFieldName() );
        assertEquals( "not in",
                      sfp.getOperator() );
        assertEquals( "1, 2",
                      sfp.getValue() );
        assertEquals( BaseSingleFieldConstraint.TYPE_LITERAL,
                      sfp.getConstraintValueType() );
    }

    @Test
    public void testRHSModifyBlockSingleFieldSingleLine() throws Exception {
        //The value used in the "set" is intentionally yucky to catch extraction of the field's value errors!
        String drl = "rule \"modify1\"\n" +
                "  dialect \"mvel\"\n" +
                "  when\n" +
                "    $p : Person( )\n" +
                "  then\n" +
                "  modify( $p ) { setFirstName( \",)\" ) }\n" +
                "end";

        addModelField( "Person",
                       "firstName",
                       "java.lang.String",
                       DataType.TYPE_STRING );

        RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal( drl,
                                                                           Collections.EMPTY_LIST,
                                                                           dmo );

        assertEquals( 1,
                      m.rhs.length );
        assertTrue( m.rhs[ 0 ] instanceof ActionUpdateField );

        ActionUpdateField field = (ActionUpdateField) m.rhs[ 0 ];
        assertEquals( "$p",
                      field.getVariable() );

        assertTrue( field.getFieldValues()[ 0 ] instanceof ActionFieldValue );
        assertEquals( 1,
                      field.getFieldValues().length );

        ActionFieldValue value = field.getFieldValues()[ 0 ];
        assertEquals( "firstName",
                      value.getField() );
        assertEquals( ",)",
                      value.getValue() );
        assertEquals( FieldNatureType.TYPE_LITERAL,
                      value.getNature() );
        assertEquals( DataType.TYPE_STRING,
                      value.getType() );
    }

    @Test
    public void testRHSModifyBlockSingleFieldMultipleLines() throws Exception {
        //The value used in the "set" is intentionally yucky to catch extraction of the field's value errors!
        String drl = "rule \"modify1\"\n" +
                "  dialect \"mvel\"\n" +
                "  when\n" +
                "    $p : Person( )\n" +
                "  then\n" +
                "  modify( $p ) {\n" +
                "    setFirstName( \",)\" )\n" +
                "  }\n" +
                "end";

        addModelField( "Person",
                       "firstName",
                       "java.lang.String",
                       DataType.TYPE_STRING );

        RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal( drl,
                                                                           Collections.EMPTY_LIST,
                                                                           dmo );

        assertEquals( 1,
                      m.rhs.length );
        assertTrue( m.rhs[ 0 ] instanceof ActionUpdateField );

        ActionUpdateField field = (ActionUpdateField) m.rhs[ 0 ];
        assertEquals( "$p",
                      field.getVariable() );

        assertTrue( field.getFieldValues()[ 0 ] instanceof ActionFieldValue );
        assertEquals( 1,
                      field.getFieldValues().length );

        ActionFieldValue value = field.getFieldValues()[ 0 ];
        assertEquals( "firstName",
                      value.getField() );
        assertEquals( ",)",
                      value.getValue() );
        assertEquals( FieldNatureType.TYPE_LITERAL,
                      value.getNature() );
        assertEquals( DataType.TYPE_STRING,
                      value.getType() );
    }

    @Test
    public void testRHSModifyBlockMultipleFieldsSingleLine() throws Exception {
        //The value used in the "set" is intentionally yucky to catch extraction of the field's value errors!
        String drl = "rule \"modify1\"\n" +
                "  dialect \"mvel\"\n" +
                "  when\n" +
                "    $p : Person( )\n" +
                "  then\n" +
                "  modify( $p ) { setFirstName( \",)\" ), setLastName( \",)\" ) }\n" +
                "end";

        addModelField( "Person",
                       "firstName",
                       "java.lang.String",
                       DataType.TYPE_STRING );
        addModelField( "Person",
                       "lastName",
                       "java.lang.String",
                       DataType.TYPE_STRING );

        RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal( drl,
                                                                           Collections.EMPTY_LIST,
                                                                           dmo );

        assertEquals( 1,
                      m.rhs.length );
        assertTrue( m.rhs[ 0 ] instanceof ActionUpdateField );

        ActionUpdateField field = (ActionUpdateField) m.rhs[ 0 ];
        assertEquals( "$p",
                      field.getVariable() );

        assertTrue( field.getFieldValues()[ 0 ] instanceof ActionFieldValue );
        assertEquals( 2,
                      field.getFieldValues().length );

        ActionFieldValue value1 = field.getFieldValues()[ 0 ];
        assertEquals( "firstName",
                      value1.getField() );
        assertEquals( ",)",
                      value1.getValue() );
        assertEquals( FieldNatureType.TYPE_LITERAL,
                      value1.getNature() );
        assertEquals( DataType.TYPE_STRING,
                      value1.getType() );

        ActionFieldValue value2 = field.getFieldValues()[ 1 ];
        assertEquals( "lastName",
                      value2.getField() );
        assertEquals( ",)",
                      value2.getValue() );
        assertEquals( FieldNatureType.TYPE_LITERAL,
                      value2.getNature() );
        assertEquals( DataType.TYPE_STRING,
                      value2.getType() );
    }

    @Test
    public void testRHSModifyBlockMultipleFieldsMultipleLines() throws Exception {
        //The value used in the "set" is intentionally yucky to catch extraction of the field's value errors!
        String drl = "rule \"modify1\"\n" +
                "  dialect \"mvel\"\n" +
                "  when\n" +
                "    $p : Person( )\n" +
                "  then\n" +
                "  modify( $p ) { \n" +
                "    setFirstName( \",)\" ), \n" +
                "    setLastName( \",)\" )\n" +
                "  }\n" +
                "end";

        addModelField( "Person",
                       "firstName",
                       "java.lang.String",
                       DataType.TYPE_STRING );
        addModelField( "Person",
                       "lastName",
                       "java.lang.String",
                       DataType.TYPE_STRING );

        RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal( drl,
                                                                           Collections.EMPTY_LIST,
                                                                           dmo );

        assertEquals( 1,
                      m.rhs.length );
        assertTrue( m.rhs[ 0 ] instanceof ActionUpdateField );

        ActionUpdateField field = (ActionUpdateField) m.rhs[ 0 ];
        assertEquals( "$p",
                      field.getVariable() );

        assertTrue( field.getFieldValues()[ 0 ] instanceof ActionFieldValue );
        assertEquals( 2,
                      field.getFieldValues().length );

        ActionFieldValue value1 = field.getFieldValues()[ 0 ];
        assertEquals( "firstName",
                      value1.getField() );
        assertEquals( ",)",
                      value1.getValue() );
        assertEquals( FieldNatureType.TYPE_LITERAL,
                      value1.getNature() );
        assertEquals( DataType.TYPE_STRING,
                      value1.getType() );

        ActionFieldValue value2 = field.getFieldValues()[ 1 ];
        assertEquals( "lastName",
                      value2.getField() );
        assertEquals( ",)",
                      value2.getValue() );
        assertEquals( FieldNatureType.TYPE_LITERAL,
                      value2.getNature() );
        assertEquals( DataType.TYPE_STRING,
                      value2.getType() );
    }

    @Test
    public void testLiteralStrFieldNames() throws Exception {
        //The issue is fields that contain the "str" operator literal value
        String drl = "rule \"rule1\"\n" +
                "  dialect \"mvel\"\n" +
                "  when\n" +
                "    Room( decoration == \"tapestry\" , strangeField == 11 )\n" +
                "  then\n" +
                "end";

        addModelField( "Room",
                       "decoration",
                       "java.lang.String",
                       DataType.TYPE_STRING );
        addModelField( "Room",
                       "strangeField",
                       "java.lang.Integer",
                       DataType.TYPE_NUMERIC_INTEGER );

        RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal( drl,
                                                                           Collections.EMPTY_LIST,
                                                                           dmo );

        assertNotNull( m );
        assertEquals( "rule1",
                      m.name );

        assertEquals( 1,
                      m.lhs.length );
        IPattern p = m.lhs[ 0 ];
        assertTrue( p instanceof FactPattern );

        FactPattern fp = (FactPattern) p;
        assertEquals( "Room",
                      fp.getFactType() );

        assertEquals( 2,
                      fp.getNumberOfConstraints() );
        assertTrue( fp.getConstraint( 0 ) instanceof SingleFieldConstraint );

        SingleFieldConstraint sfp0 = (SingleFieldConstraint) fp.getConstraint( 0 );
        assertEquals( "Room",
                      sfp0.getFactType() );
        assertEquals( "decoration",
                      sfp0.getFieldName() );
        assertEquals( "==",
                      sfp0.getOperator() );
        assertEquals( "tapestry",
                      sfp0.getValue() );
        assertEquals( BaseSingleFieldConstraint.TYPE_LITERAL,
                      sfp0.getConstraintValueType() );
        assertEquals( DataType.TYPE_STRING,
                      sfp0.getFieldType() );

        SingleFieldConstraint sfp1 = (SingleFieldConstraint) fp.getConstraint( 1 );
        assertEquals( "Room",
                      sfp1.getFactType() );
        assertEquals( "strangeField",
                      sfp1.getFieldName() );
        assertEquals( "==",
                      sfp1.getOperator() );
        assertEquals( "11",
                      sfp1.getValue() );
        assertEquals( BaseSingleFieldConstraint.TYPE_LITERAL,
                      sfp1.getConstraintValueType() );
        assertEquals( DataType.TYPE_NUMERIC_INTEGER,
                      sfp1.getFieldType() );
    }

    @Test
    public void testMethodCallWithTwoParametersIntegerAndString() throws Exception {
        String drl = "package org.mortgages;\n" +
                "rule \"test\"\n" +
                " dialect \"mvel\"\n" +
                " when\n" +
                "  $t : TestClass()\n" +
                " then\n" +
                "  $t.testFunction( 123, \"hello\" );\n" +
                "end";

        final HashMap<String, List<MethodInfo>> map = new HashMap<String, List<MethodInfo>>();
        final ArrayList<MethodInfo> methodInfos = new ArrayList<MethodInfo>();
        final ArrayList<String> params = new ArrayList<String>();
        params.add( "Integer" );
        params.add( "String" );
        methodInfos.add( new MethodInfo( "testFunction",
                                         params,
                                         "java.lang.Void",
                                         null,
                                         "TestClass" ) );
        map.put( "TestClass",
                 methodInfos );

        when( dmo.getProjectMethodInformation() ).thenReturn( map );

        final RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal( drl,
                                                                                 new ArrayList<String>(),
                                                                                 dmo );

        assertNotNull( m );

        assertEquals( 1,
                      m.lhs.length );

        assertEquals( 1,
                      m.rhs.length );

        ActionCallMethod actionCallMethod = (ActionCallMethod) m.rhs[ 0 ];
        assertEquals( "testFunction",
                      actionCallMethod.getMethodName() );
        assertEquals( "$t",
                      actionCallMethod.getVariable() );
        assertEquals( 2,
                      actionCallMethod.getFieldValues().length );

        assertEquals( "testFunction",
                      actionCallMethod.getFieldValue( 0 ).getField() );
        assertEquals( "123",
                      actionCallMethod.getFieldValue( 0 ).getValue() );
        assertEquals( FieldNatureType.TYPE_LITERAL,
                      actionCallMethod.getFieldValue( 0 ).getNature() );
        assertEquals( "Integer",
                      actionCallMethod.getFieldValue( 0 ).getType() );

        assertEquals( "testFunction",
                      actionCallMethod.getFieldValue( 1 ).getField() );
        assertEquals( "hello",
                      actionCallMethod.getFieldValue( 1 ).getValue() );
        assertEquals( FieldNatureType.TYPE_LITERAL,
                      actionCallMethod.getFieldValue( 1 ).getNature() );
        assertEquals( "String",
                      actionCallMethod.getFieldValue( 1 ).getType() );
    }

    @Test
    public void testLHSNumberExpressionWithoutThisPrefix() throws Exception {
        String drl = "package org.mortgages;\n" +
                "import java.lang.Number\n" +
                "rule \"test\"\n" +
                " dialect \"mvel\"\n" +
                " when\n" +
                "  Number( intValue() > 5 )\n" +
                " then\n" +
                "end";

        final HashMap<String, List<MethodInfo>> map = new HashMap<String, List<MethodInfo>>();
        final ArrayList<MethodInfo> methodInfos = new ArrayList<MethodInfo>();
        methodInfos.add( new MethodInfo( "intValue",
                                         Collections.EMPTY_LIST,
                                         "int",
                                         null,
                                         DataType.TYPE_NUMERIC_INTEGER ) );
        map.put( "java.lang.Number",
                 methodInfos );

        when( dmo.getProjectMethodInformation() ).thenReturn( map );

        final RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal( drl,
                                                                                 new ArrayList<String>(),
                                                                                 dmo );

        assertNotNull( m );

        assertEquals( 1,
                      m.lhs.length );

        assertTrue( m.lhs[ 0 ] instanceof FactPattern );
        final FactPattern fp = (FactPattern) m.lhs[ 0 ];
        assertEquals( "Number",
                      fp.getFactType() );

        assertEquals( 1,
                      fp.getNumberOfConstraints() );
        assertTrue( fp.getConstraint( 0 ) instanceof SingleFieldConstraintEBLeftSide );
        final SingleFieldConstraintEBLeftSide exp = (SingleFieldConstraintEBLeftSide) fp.getConstraint( 0 );
        assertEquals( "int",
                      exp.getFieldType() );
        assertEquals( ">",
                      exp.getOperator() );
        assertEquals( "5",
                      exp.getValue() );

        assertEquals( 2,
                      exp.getExpressionLeftSide().getParts().size() );
        assertTrue( exp.getExpressionLeftSide().getParts().get( 0 ) instanceof ExpressionUnboundFact );
        final ExpressionUnboundFact expPart0 = (ExpressionUnboundFact) exp.getExpressionLeftSide().getParts().get( 0 );
        assertEquals( "Number",
                      expPart0.getFact().getFactType() );

        assertTrue( exp.getExpressionLeftSide().getParts().get( 1 ) instanceof ExpressionMethod );
        final ExpressionMethod expPart1 = (ExpressionMethod) exp.getExpressionLeftSide().getParts().get( 1 );
        assertEquals( "intValue",
                      expPart1.getName() );
    }

    @Test
    public void testLHSNumberExpressionWithThisPrefix() throws Exception {
        String drl = "package org.mortgages;\n" +
                "import java.lang.Number\n" +
                "rule \"test\"\n" +
                " dialect \"mvel\"\n" +
                " when\n" +
                "  Number( this.intValue() > 5 )\n" +
                " then\n" +
                "end";

        final HashMap<String, List<MethodInfo>> map = new HashMap<String, List<MethodInfo>>();
        final ArrayList<MethodInfo> methodInfos = new ArrayList<MethodInfo>();
        methodInfos.add( new MethodInfo( "intValue",
                                         Collections.EMPTY_LIST,
                                         "int",
                                         null,
                                         DataType.TYPE_NUMERIC_INTEGER ) );
        map.put( "java.lang.Number",
                 methodInfos );

        when( dmo.getProjectMethodInformation() ).thenReturn( map );

        final RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal( drl,
                                                                                 new ArrayList<String>(),
                                                                                 dmo );

        assertNotNull( m );

        assertEquals( 1,
                      m.lhs.length );

        assertTrue( m.lhs[ 0 ] instanceof FactPattern );
        final FactPattern fp = (FactPattern) m.lhs[ 0 ];
        assertEquals( "Number",
                      fp.getFactType() );

        assertEquals( 1,
                      fp.getNumberOfConstraints() );
        assertTrue( fp.getConstraint( 0 ) instanceof SingleFieldConstraintEBLeftSide );
        final SingleFieldConstraintEBLeftSide exp = (SingleFieldConstraintEBLeftSide) fp.getConstraint( 0 );
        assertEquals( "int",
                      exp.getFieldType() );
        assertEquals( ">",
                      exp.getOperator() );
        assertEquals( "5",
                      exp.getValue() );

        assertEquals( 3,
                      exp.getExpressionLeftSide().getParts().size() );
        assertTrue( exp.getExpressionLeftSide().getParts().get( 0 ) instanceof ExpressionUnboundFact );
        final ExpressionUnboundFact expPart0 = (ExpressionUnboundFact) exp.getExpressionLeftSide().getParts().get( 0 );
        assertEquals( "Number",
                      expPart0.getFact().getFactType() );

        assertTrue( exp.getExpressionLeftSide().getParts().get( 1 ) instanceof ExpressionField );
        final ExpressionField expPart1 = (ExpressionField) exp.getExpressionLeftSide().getParts().get( 1 );
        assertEquals( "this",
                      expPart1.getName() );

        assertTrue( exp.getExpressionLeftSide().getParts().get( 2 ) instanceof ExpressionMethod );
        final ExpressionMethod expPart2 = (ExpressionMethod) exp.getExpressionLeftSide().getParts().get( 2 );
        assertEquals( "intValue",
                      expPart2.getName() );
    }

    @Test
    public void testLHSNestedMethodCalls() throws Exception {
        String drl = "package org.mortgages;\n" +
                "rule \"test\"\n" +
                " dialect \"mvel\"\n" +
                " when\n" +
                "  Parent( methodToGetChild1().methodToGetChild2().field1 > 5 )\n" +
                " then\n" +
                "end";

        addMethodInformation( "Parent",
                              "methodToGetChild1",
                              Collections.EMPTY_LIST,
                              "Child1",
                              null,
                              "Child1" );
        addMethodInformation( "Child1",
                              "methodToGetChild2",
                              Collections.EMPTY_LIST,
                              "Child2",
                              null,
                              "Child2" );
        addModelField( "Child2",
                       "field1",
                       "int",
                       DataType.TYPE_NUMERIC_INTEGER );

        final RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal( drl,
                                                                                 new ArrayList<String>(),
                                                                                 dmo );

        assertNotNull( m );

        assertEquals( 1,
                      m.lhs.length );

        assertTrue( m.lhs[ 0 ] instanceof FactPattern );
        final FactPattern fp = (FactPattern) m.lhs[ 0 ];
        assertEquals( "Parent",
                      fp.getFactType() );

        assertEquals( 1,
                      fp.getNumberOfConstraints() );
        assertTrue( fp.getConstraint( 0 ) instanceof SingleFieldConstraintEBLeftSide );
        final SingleFieldConstraintEBLeftSide exp = (SingleFieldConstraintEBLeftSide) fp.getConstraint( 0 );
        assertEquals( "int",
                      exp.getFieldType() );
        assertEquals( ">",
                      exp.getOperator() );
        assertEquals( "5",
                      exp.getValue() );

        assertEquals( 4,
                      exp.getExpressionLeftSide().getParts().size() );
        assertTrue( exp.getExpressionLeftSide().getParts().get( 0 ) instanceof ExpressionUnboundFact );
        final ExpressionUnboundFact expPart0 = (ExpressionUnboundFact) exp.getExpressionLeftSide().getParts().get( 0 );
        assertEquals( "Parent",
                      expPart0.getFact().getFactType() );

        assertTrue( exp.getExpressionLeftSide().getParts().get( 1 ) instanceof ExpressionMethod );
        final ExpressionMethod expPart1 = (ExpressionMethod) exp.getExpressionLeftSide().getParts().get( 1 );
        assertEquals( "methodToGetChild1",
                      expPart1.getName() );

        assertTrue( exp.getExpressionLeftSide().getParts().get( 2 ) instanceof ExpressionMethod );
        final ExpressionMethod expPart2 = (ExpressionMethod) exp.getExpressionLeftSide().getParts().get( 2 );
        assertEquals( "methodToGetChild2",
                      expPart2.getName() );

        assertTrue( exp.getExpressionLeftSide().getParts().get( 3 ) instanceof ExpressionField );
        final ExpressionField expPart3 = (ExpressionField) exp.getExpressionLeftSide().getParts().get( 3 );
        assertEquals( "field1",
                      expPart3.getName() );
    }

    @Test
    public void testLHSMissingConstraints() {
        String drl = "package org.mortgages;\n" +
                "import java.lang.Number;\n" +
                "import org.drools.workbench.models.commons.backend.rule.classes.SearchContext;\n" +
                "import org.drools.workbench.models.commons.backend.rule.classes.ProducerMasterForRules;\n" +
                "import org.drools.workbench.models.commons.backend.rule.classes.RuleFactor;\n" +

                "rule \"SecondaryCuisineRepeatUsage\"\n" +
                "dialect \"mvel\"\n" +
                "when\n" +
                "  $searchContext : SearchContext( lastThreeCuisines != null )\n" +
                "  ProducerMasterForRules( primaryCuisine != null, primaryCuisine != $searchContext.lastThreeCuisines, secondaryCuisine != null, secondaryCuisine == $searchContext.lastThreeCuisines )\n" +
                "  $secondaryCuisineRepeatUsageFactor : RuleFactor( )\n" +
                "then\n" +
                "  modify( $secondaryCuisineRepeatUsageFactor ) {\n" +
                "    setWeightageImpact(-30)\n" +
                "  }\n" +
                "end\n";

        addModelField( "org.drools.workbench.models.commons.backend.rule.classes.SearchContext",
                       "this",
                       "org.drools.workbench.models.commons.backend.rule.classes.SearchContext",
                       DataType.TYPE_THIS );

        addModelField( "org.drools.workbench.models.commons.backend.rule.classes.SearchContext",
                       "lastThreeCuisines",
                       "java.lang.String",
                       DataType.TYPE_STRING );

        addModelField( "org.drools.workbench.models.commons.backend.rule.classes.ProducerMasterForRules",
                       "primaryCuisine",
                       "java.lang.String",
                       DataType.TYPE_STRING );

        addModelField( "org.drools.workbench.models.commons.backend.rule.classes.ProducerMasterForRules",
                       "secondaryCuisine",
                       "java.lang.String",
                       DataType.TYPE_STRING );

        final RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal( drl,
                                                                                 new ArrayList<String>(),
                                                                                 dmo );

        assertNotNull( m );

        assertEqualsIgnoreWhitespace( drl,
                                      RuleModelDRLPersistenceImpl.getInstance().marshal( m ) );
    }

    @Test
    public void testLHSNestedMethodOneParameter() {
        String drl = "package org.mortgages;\n" +
                "import java.lang.Number;\n" +
                "import java.util.List;\n" +
                "import org.drools.workbench.models.commons.backend.rule.classes.MyListContainerClass;\n" +
                "import org.drools.workbench.models.commons.backend.rule.classes.MyStringContainerClass;\n" +
                "rule \"r1\"\n" +
                "dialect \"mvel\"\n" +
                "when\n" +
                "  $foo : MyListContainerClass()\n" +
                "  $bar : MyStringContainerClass( myString == $foo.myList.get(1))\n" +
                "then\n" +
                "end\n";

        addModelField( "org.drools.workbench.models.commons.backend.rule.classes.MyListContainerClass",
                       "this",
                       "org.drools.workbench.models.commons.backend.rule.classes.MyListContainerClass",
                       DataType.TYPE_THIS );
        addModelField( "org.drools.workbench.models.commons.backend.rule.classes.MyListContainerClass",
                       "myList",
                       "java.util.List",
                       "java.util.List" );

        addModelField( "org.drools.workbench.models.commons.backend.rule.classes.MyStringContainerClass",
                       "this",
                       "org.drools.workbench.models.commons.backend.rule.classes.MyStringContainerClass",
                       DataType.TYPE_THIS );
        addModelField( "org.drools.workbench.models.commons.backend.rule.classes.MyStringContainerClass",
                       "myString",
                       "java.lang.String",
                       DataType.TYPE_STRING );

        addMethodInformation( "java.util.List",
                              "get",
                              new ArrayList<String>() {{
                                  add( "Integer" );
                              }},
                              "java.lang.Object",
                              null,
                              "java.lang.Object" );

        final RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal( drl,
                                                                                 new ArrayList<String>(),
                                                                                 dmo );

        assertNotNull( m );

        assertEqualsIgnoreWhitespace( drl,
                                      RuleModelDRLPersistenceImpl.getInstance().marshal( m ) );
    }

    @Test
    public void testLHSNestedMethodTwoParameters() {
        String drl = "package org.mortgages;\n" +
                "import java.lang.Number;\n" +
                "import java.util.List;\n" +
                "import org.drools.workbench.models.commons.backend.rule.classes.MyListContainerClass;\n" +
                "import org.drools.workbench.models.commons.backend.rule.classes.MyStringContainerClass;\n" +
                "rule \"r1\"\n" +
                "dialect \"mvel\"\n" +
                "when\n" +
                "  $foo : MyListContainerClass()\n" +
                "  $bar : MyStringContainerClass( $foo.myList.set(1, \"hello\" ) == true )\n" +
                "then\n" +
                "end\n";

        addModelField( "org.drools.workbench.models.commons.backend.rule.classes.MyListContainerClass",
                       "this",
                       "org.drools.workbench.models.commons.backend.rule.classes.MyListContainerClass",
                       DataType.TYPE_THIS );
        addModelField( "org.drools.workbench.models.commons.backend.rule.classes.MyListContainerClass",
                       "myList",
                       "java.util.List",
                       "java.util.List" );

        addModelField( "org.drools.workbench.models.commons.backend.rule.classes.MyStringContainerClass",
                       "this",
                       "org.drools.workbench.models.commons.backend.rule.classes.MyStringContainerClass",
                       DataType.TYPE_THIS );
        addModelField( "org.drools.workbench.models.commons.backend.rule.classes.MyStringContainerClass",
                       "myString",
                       "java.lang.String",
                       DataType.TYPE_STRING );

        addModelField( "java.util.List",
                       "this",
                       "java.util.List",
                       DataType.TYPE_THIS );
        addMethodInformation( "java.util.List",
                              "set",
                              new ArrayList<String>() {{
                                  add( "Integer" );
                                  add( "String" );
                              }},
                              DataType.TYPE_BOOLEAN,
                              null,
                              DataType.TYPE_BOOLEAN );

        final RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal( drl,
                                                                                 new ArrayList<String>(),
                                                                                 dmo );

        assertNotNull( m );

        assertEqualsIgnoreWhitespace( drl,
                                      RuleModelDRLPersistenceImpl.getInstance().marshal( m ) );
    }

    @Test
    //https://bugzilla.redhat.com/show_bug.cgi?id=1160658
    public void testRHSFormulaTruncation() throws Exception {
        String drl = "package org.test;\n"
                + "rule \"rule1\"\n"
                + "dialect \"mvel\"\n"
                + "when\n"
                + "  $p : Person()\n"
                + "then\n"
                + "  $p.setTextOut( $p.getType() + \"\" );\n"
                + "end";

        addModelField( "org.test.Person",
                       "this",
                       "org.test.Person",
                       DataType.TYPE_THIS );
        addModelField( "org.test.Person",
                       "textOut",
                       "java.lang.String",
                       DataType.TYPE_STRING );
        addModelField( "org.test.Person",
                       "type",
                       "java.lang.String",
                       DataType.TYPE_STRING );

        when( dmo.getPackageName() ).thenReturn( "org.test" );

        final RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal( drl,
                                                                                 new ArrayList<String>(),
                                                                                 dmo );

        assertNotNull( m );

        assertEquals( 1,
                      m.lhs.length );
        assertEquals( 1,
                      m.rhs.length );

        assertEqualsIgnoreWhitespace( drl,
                                      RuleModelDRLPersistenceImpl.getInstance().marshal( m ) );
    }

    @Test
    //https://bugzilla.redhat.com/show_bug.cgi?id=1158813
    public void testLHSFromCollectWithoutListDeclared() throws Exception {
        String drl = "package org.test;\n"
                + "rule \"rule1\"\n"
                + "dialect \"mvel\"\n"
                + "when\n"
                + "  c : Customer( )\n"
                + "  items : java.util.List( eval( size == c.items.size )) \n"
                + "  from collect ( var : Item( price > 10 )) \n"
                + "then \n"
                + "end";

        addModelField( "org.test.Customer",
                       "this",
                       "org.test.Customer",
                       DataType.TYPE_THIS );
        addModelField( "org.test.Item",
                       "this",
                       "org.test.Item",
                       DataType.TYPE_THIS );
        addModelField( "org.test.Item",
                       "price",
                       "java.lang.Double",
                       DataType.TYPE_NUMERIC_DOUBLE );

        addMethodInformation( "org.test.Customer",
                              "items",
                              new ArrayList<String>() {{
                              }},
                              DataType.TYPE_NUMERIC_INTEGER,
                              null,
                              DataType.TYPE_NUMERIC_INTEGER );

        when( dmo.getPackageName() ).thenReturn( "org.test" );

        final RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal( drl,
                                                                                 new ArrayList<String>(),
                                                                                 dmo );

        assertNotNull( m );

        assertEquals( 2,
                      m.lhs.length );
        assertEquals( 0,
                      m.rhs.length );

        assertTrue( m.lhs[ 0 ] instanceof FactPattern );
        final FactPattern fp = (FactPattern) m.lhs[ 0 ];
        assertEquals( "Customer",
                      fp.getFactType() );
        assertEquals( "c",
                      fp.getBoundName() );
        assertEquals( 0,
                      fp.getNumberOfConstraints() );

        assertTrue( m.lhs[ 1 ] instanceof FromCollectCompositeFactPattern );
        final FromCollectCompositeFactPattern fcfp = (FromCollectCompositeFactPattern) m.lhs[ 1 ];
        assertNotNull( fcfp.getFactPattern() );
        assertEquals( "java.util.List",
                      fcfp.getFactPattern().getFactType() );
        assertEquals( "items",
                      fcfp.getFactPattern().getBoundName() );
        assertEquals( 1,
                      fcfp.getFactPattern().getNumberOfConstraints() );
        assertTrue( fcfp.getFactPattern().getConstraint( 0 ) instanceof SingleFieldConstraint );
        final SingleFieldConstraint sfc0 = (SingleFieldConstraint) fcfp.getFactPattern().getConstraint( 0 );
        assertNull( sfc0.getFactType() );
        assertNull( sfc0.getFieldName() );
        assertEquals( "size == c.items.size",
                      sfc0.getValue() );
        assertEquals( BaseSingleFieldConstraint.TYPE_PREDICATE,
                      sfc0.getConstraintValueType() );

        assertNotNull( fcfp.getRightPattern() );
        assertTrue( fcfp.getRightPattern() instanceof FactPattern );
        final FactPattern rfp = (FactPattern) fcfp.getRightPattern();
        assertEquals( "Item",
                      rfp.getFactType() );
        assertEquals( "var",
                      rfp.getBoundName() );
        assertEquals( 1,
                      rfp.getNumberOfConstraints() );
        assertTrue( rfp.getConstraint( 0 ) instanceof SingleFieldConstraint );
        final SingleFieldConstraint sfc1 = (SingleFieldConstraint) rfp.getConstraint( 0 );
        assertEquals( "Item",
                      sfc1.getFactType() );
        assertEquals( "price",
                      sfc1.getFieldName() );
        assertEquals( "10",
                      sfc1.getValue() );
        assertEquals( ">",
                      sfc1.getOperator() );
        assertEquals( BaseSingleFieldConstraint.TYPE_LITERAL,
                      sfc1.getConstraintValueType() );

        assertEqualsIgnoreWhitespace( drl,
                                      RuleModelDRLPersistenceImpl.getInstance().marshal( m ) );
    }

    @Test
    //https://bugzilla.redhat.com/show_bug.cgi?id=1158813
    public void testLHSFromCollectWithListDeclared() throws Exception {
        String drl = "package org.test;\n"
                + "rule \"rule1\"\n"
                + "dialect \"mvel\"\n"
                + "when\n"
                + "  c : Customer( )\n"
                + "  items : List( eval( size == c.items.size )) \n"
                + "  from collect ( var : Item( price > 10 )) \n"
                + "then \n"
                + "end";

        addModelField( "org.test.Customer",
                       "this",
                       "org.test.Customer",
                       DataType.TYPE_THIS );
        addModelField( "org.test.Item",
                       "this",
                       "org.test.Item",
                       DataType.TYPE_THIS );
        addModelField( "org.test.Item",
                       "price",
                       "java.lang.Double",
                       DataType.TYPE_NUMERIC_DOUBLE );
        addModelField( "java.util.List",
                       "this",
                       "java.util.List",
                       DataType.TYPE_THIS );

        addMethodInformation( "org.test.Customer",
                              "items",
                              new ArrayList<String>() {{
                              }},
                              DataType.TYPE_NUMERIC_INTEGER,
                              null,
                              DataType.TYPE_NUMERIC_INTEGER );

        when( dmo.getPackageName() ).thenReturn( "org.test" );

        final RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal( drl,
                                                                                 new ArrayList<String>(),
                                                                                 dmo );

        assertNotNull( m );

        assertEquals( 2,
                      m.lhs.length );
        assertEquals( 0,
                      m.rhs.length );

        assertTrue( m.lhs[ 0 ] instanceof FactPattern );
        final FactPattern fp = (FactPattern) m.lhs[ 0 ];
        assertEquals( "Customer",
                      fp.getFactType() );
        assertEquals( "c",
                      fp.getBoundName() );
        assertEquals( 0,
                      fp.getNumberOfConstraints() );

        assertTrue( m.lhs[ 1 ] instanceof FromCollectCompositeFactPattern );
        final FromCollectCompositeFactPattern fcfp = (FromCollectCompositeFactPattern) m.lhs[ 1 ];
        assertNotNull( fcfp.getFactPattern() );
        assertEquals( "List",
                      fcfp.getFactPattern().getFactType() );
        assertEquals( "items",
                      fcfp.getFactPattern().getBoundName() );
        assertEquals( 1,
                      fcfp.getFactPattern().getNumberOfConstraints() );
        assertTrue( fcfp.getFactPattern().getConstraint( 0 ) instanceof SingleFieldConstraint );
        final SingleFieldConstraint sfc0 = (SingleFieldConstraint) fcfp.getFactPattern().getConstraint( 0 );
        assertNull( sfc0.getFactType() );
        assertNull( sfc0.getFieldName() );
        assertEquals( "size == c.items.size",
                      sfc0.getValue() );
        assertEquals( BaseSingleFieldConstraint.TYPE_PREDICATE,
                      sfc0.getConstraintValueType() );

        assertNotNull( fcfp.getRightPattern() );
        assertTrue( fcfp.getRightPattern() instanceof FactPattern );
        final FactPattern rfp = (FactPattern) fcfp.getRightPattern();
        assertEquals( "Item",
                      rfp.getFactType() );
        assertEquals( "var",
                      rfp.getBoundName() );
        assertEquals( 1,
                      rfp.getNumberOfConstraints() );
        assertTrue( rfp.getConstraint( 0 ) instanceof SingleFieldConstraint );
        final SingleFieldConstraint sfc1 = (SingleFieldConstraint) rfp.getConstraint( 0 );
        assertEquals( "Item",
                      sfc1.getFactType() );
        assertEquals( "price",
                      sfc1.getFieldName() );
        assertEquals( "10",
                      sfc1.getValue() );
        assertEquals( ">",
                      sfc1.getOperator() );
        assertEquals( BaseSingleFieldConstraint.TYPE_LITERAL,
                      sfc1.getConstraintValueType() );

        assertEqualsIgnoreWhitespace( drl,
                                      RuleModelDRLPersistenceImpl.getInstance().marshal( m ) );
    }

    @Test
    //https://bugzilla.redhat.com/show_bug.cgi?id=1134067
    public void testRHSFormulaTruncationInsertFact() throws Exception {
        String drl = "package org.test;\n"
                + "rule \"rule1\"\n"
                + "dialect \"mvel\"\n"
                + "when\n"
                + "  Person( $t : name )\n"
                + "then \n"
                + "  Person fact0 = new Person();\n"
                + "  fact0.setName( $t );\n"
                + "  insert( fact0 );\n"
                + "end";

        addModelField( "org.test.Person",
                       "this",
                       "org.test.Person",
                       DataType.TYPE_THIS );
        addModelField( "org.test.Person",
                       "name",
                       "java.lang.String",
                       DataType.TYPE_STRING );
        addModelField( "org.test.Person",
                       "type",
                       "java.lang.String",
                       DataType.TYPE_STRING );

        when( dmo.getPackageName() ).thenReturn( "org.test" );

        final RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal( drl,
                                                                                 new ArrayList<String>(),
                                                                                 dmo );

        assertNotNull( m );

        assertEquals( 1,
                      m.lhs.length );
        assertEquals( 1,
                      m.rhs.length );

        assertEqualsIgnoreWhitespace( drl,
                                      RuleModelDRLPersistenceImpl.getInstance().marshal( m ) );
    }

    @Test
    //https://issues.jboss.org/browse/GUVNOR-2144
    public void testRuleModelPersistenceHelperUnwrapParenthesis() throws Exception {
        String drl = "package org.test;\n"
                + "rule \"Load all schedules\"\n"
                + "dialect \"mvel\"\n"
                + "agenda-group \"LoadSchedules\"\n"
                + "salience 900\n"
                + "  when\n"
                + "    $bundle : Bundle( evaluated == false )\n"
                + "  then\n"
                + "    $bundle.setEvaluated( true );\n"
                + "    update( $bundle );\n"
                + "end";

        //Expected is different as we convert "update" to "modify" blocks
        String expected = "package org.test;\n"
                + "rule \"Load all schedules\"\n"
                + "dialect \"mvel\"\n"
                + "agenda-group \"LoadSchedules\"\n"
                + "salience 900\n"
                + "  when\n"
                + "    $bundle : Bundle( evaluated == false )\n"
                + "  then\n"
                + "    modify( $bundle ) {\n"
                + "      setEvaluated( true )\n"
                + "    }\n"
                + "end";

        addModelField( "org.test.Bundle",
                       "this",
                       "org.test.Bundle",
                       DataType.TYPE_THIS );
        addModelField( "org.test.Person",
                       "evaluated",
                       Boolean.class.getName(),
                       DataType.TYPE_BOOLEAN );

        when( dmo.getPackageName() ).thenReturn( "org.test" );

        final RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal( drl,
                                                                                 new ArrayList<String>(),
                                                                                 dmo );

        assertNotNull( m );

        assertEquals( 1,
                      m.lhs.length );
        assertEquals( 1,
                      m.rhs.length );
        assertEquals( 3,
                      m.attributes.length );

        assertEqualsIgnoreWhitespace( expected,
                                      RuleModelDRLPersistenceImpl.getInstance().marshal( m ) );
    }

    @Test
    //https://issues.jboss.org/browse/GUVNOR-2141
    public void testSingleFieldConstraintConnectives1() {
        String drl = "rule \"rule1\"\n"
                + "dialect \"mvel\"\n"
                + "  when\n"
                + "    Applicant( age < 55 || > 75 )\n"
                + "  then\n"
                + "end";

        RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal( drl,
                                                                           Collections.EMPTY_LIST,
                                                                           dmo );

        assertNotNull( m );
        assertEquals( "rule1",
                      m.name );

        assertEquals( 1,
                      m.lhs.length );
        IPattern p = m.lhs[ 0 ];
        assertTrue( p instanceof FactPattern );

        FactPattern fp = (FactPattern) p;
        assertEquals( "Applicant",
                      fp.getFactType() );

        assertEquals( 1,
                      fp.getConstraintList().getConstraints().length );
        assertTrue( fp.getConstraint( 0 ) instanceof SingleFieldConstraint );

        SingleFieldConstraint sfp = (SingleFieldConstraint) fp.getConstraint( 0 );
        assertEquals( "Applicant",
                      sfp.getFactType() );
        assertEquals( "age",
                      sfp.getFieldName() );
        assertEquals( "<",
                      sfp.getOperator() );
        assertEquals( "55",
                      sfp.getValue() );
        assertEquals( BaseSingleFieldConstraint.TYPE_LITERAL,
                      sfp.getConstraintValueType() );

        assertEquals( 1,
                      sfp.getConnectives().length );
        ConnectiveConstraint cc = sfp.getConnectives()[ 0 ];
        assertEquals( "Applicant",
                      cc.getFactType() );
        assertEquals( "age",
                      cc.getFieldName() );
        assertEquals( "|| >",
                      cc.getOperator() );
        assertEquals( "75",
                      cc.getValue() );
        assertEquals( BaseSingleFieldConstraint.TYPE_LITERAL,
                      cc.getConstraintValueType() );

        assertEqualsIgnoreWhitespace( drl,
                                      RuleModelDRLPersistenceImpl.getInstance().marshal( m ) );
    }

    @Test
    //https://issues.jboss.org/browse/GUVNOR-2141
    public void testSingleFieldConstraintConnectives2() {
        String drl = "rule \"rule1\"\n"
                + "dialect \"mvel\"\n"
                + "  when\n"
                + "    Applicant( age == 55 || == 75 )\n"
                + "  then\n"
                + "end";

        RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal( drl,
                                                                           Collections.EMPTY_LIST,
                                                                           dmo );

        assertNotNull( m );
        assertEquals( "rule1",
                      m.name );

        assertEquals( 1,
                      m.lhs.length );
        IPattern p = m.lhs[ 0 ];
        assertTrue( p instanceof FactPattern );

        FactPattern fp = (FactPattern) p;
        assertEquals( "Applicant",
                      fp.getFactType() );

        assertEquals( 1,
                      fp.getConstraintList().getConstraints().length );
        assertTrue( fp.getConstraint( 0 ) instanceof SingleFieldConstraint );

        SingleFieldConstraint sfp = (SingleFieldConstraint) fp.getConstraint( 0 );
        assertEquals( "Applicant",
                      sfp.getFactType() );
        assertEquals( "age",
                      sfp.getFieldName() );
        assertEquals( "==",
                      sfp.getOperator() );
        assertEquals( "55",
                      sfp.getValue() );
        assertEquals( BaseSingleFieldConstraint.TYPE_LITERAL,
                      sfp.getConstraintValueType() );

        assertEquals( 1,
                      sfp.getConnectives().length );
        ConnectiveConstraint cc = sfp.getConnectives()[ 0 ];
        assertEquals( "Applicant",
                      cc.getFactType() );
        assertEquals( "age",
                      cc.getFieldName() );
        assertEquals( "|| ==",
                      cc.getOperator() );
        assertEquals( "75",
                      cc.getValue() );
        assertEquals( BaseSingleFieldConstraint.TYPE_LITERAL,
                      cc.getConstraintValueType() );

        assertEqualsIgnoreWhitespace( drl,
                                      RuleModelDRLPersistenceImpl.getInstance().marshal( m ) );
    }

    @Test
    //https://issues.jboss.org/browse/GUVNOR-2143
    public void testNewKeywordVariableNamePrefix1() {
        String oldValue = System.getProperty( "drools.dateformat" );
        try {

            System.setProperty( "drools.dateformat",
                                "dd-MMM-yyyy" );

            String drl = "package org.test;\n"
                    + "rule \"rule1\"\n"
                    + "  dialect \"java\"\n"
                    + "  when\n"
                    + "    $bundle : Bundle( $treatmentEffectiveDt : treatmentEffectiveDt )\n"
                    + "  then\n"
                    + "    java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(\"dd-MMM-yyyy\");\n"
                    + "    DateTime newStartDate = new DateTime();\n"
                    + "    modify( $bundle ) {\n"
                    + "      setTreatmentEffectiveDt( newStartDate.toDate() )"
                    + "    }\n"
                    + "end\n";

            addModelField( "org.test.Bundle",
                           "this",
                           "org.test.Bundle",
                           DataType.TYPE_THIS );
            addModelField( "org.test.Bundle",
                           "treatmentEffectiveDt",
                           Date.class.getName(),
                           DataType.TYPE_DATE );

            when( dmo.getPackageName() ).thenReturn( "org.test" );

            RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal( drl,
                                                                               Collections.EMPTY_LIST,
                                                                               dmo );

            assertNotNull( m );
            assertEquals( "rule1",
                          m.name );

            assertEquals( 1,
                          m.lhs.length );
            IPattern p = m.lhs[ 0 ];
            assertTrue( p instanceof FactPattern );

            FactPattern fp = (FactPattern) p;
            assertEquals( "Bundle",
                          fp.getFactType() );
            assertEquals( "$bundle",
                          fp.getBoundName() );

            assertEquals( 1,
                          fp.getConstraintList().getConstraints().length );
            assertTrue( fp.getConstraint( 0 ) instanceof SingleFieldConstraint );

            SingleFieldConstraint sfp = (SingleFieldConstraint) fp.getConstraint( 0 );
            assertEquals( "Bundle",
                          sfp.getFactType() );
            assertEquals( "treatmentEffectiveDt",
                          sfp.getFieldName() );
            assertEquals( "$treatmentEffectiveDt",
                          sfp.getFieldBinding() );
            assertNull( sfp.getOperator() );
            assertNull( sfp.getValue() );
            assertEquals( BaseSingleFieldConstraint.TYPE_UNDEFINED,
                          sfp.getConstraintValueType() );

            assertEquals( 2,
                          m.rhs.length );

            assertTrue( m.rhs[ 0 ] instanceof FreeFormLine );
            FreeFormLine ffl = (FreeFormLine) m.rhs[ 0 ];
            assertEquals( "DateTime newStartDate = new DateTime();",
                          ffl.getText() );

            assertTrue( m.rhs[ 1 ] instanceof ActionUpdateField );
            ActionUpdateField auf = (ActionUpdateField) m.rhs[ 1 ];
            assertEquals( "$bundle",
                          auf.getVariable() );
            assertEquals( 1,
                          auf.getFieldValues().length );
            ActionFieldValue afv = auf.getFieldValues()[ 0 ];
            assertEquals( "treatmentEffectiveDt",
                          afv.getField() );
            assertEquals( "newStartDate.toDate()",
                          afv.getValue() );
            assertEquals( FieldNatureType.TYPE_FORMULA,
                          afv.getNature() );

            assertEqualsIgnoreWhitespace( drl,
                                          RuleModelDRLPersistenceImpl.getInstance().marshal( m ) );
        } finally {
            if ( oldValue == null ) {
                System.clearProperty( "drools.dateformat" );
            } else {
                System.setProperty( "drools.dateformat",
                                    oldValue );
            }
        }
    }

    @Test
    //https://issues.jboss.org/browse/GUVNOR-2143
    public void testNewKeywordVariableNamePrefix2() {
        String oldValue = System.getProperty( "drools.dateformat" );
        try {

            System.setProperty( "drools.dateformat",
                                "dd-MMM-yyyy" );

            String drl = "package org.test;\n"
                    + "rule \"rule1\"\n"
                    + "  dialect \"java\"\n"
                    + "  when\n"
                    + "    $a : Applicant()\n"
                    + "  then\n"
                    + "    java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(\"dd-MMM-yyyy\");\n"
                    + "    java.util.Date newStartDate = new java.util.Date();\n"
                    + "    modify( $a ) {\n"
                    + "      setApplicantDate( newStartDate )"
                    + "    }\n"
                    + "end\n";

            addModelField( "org.test.Applicant",
                           "this",
                           "org.test.Applicant",
                           DataType.TYPE_THIS );
            addModelField( "org.test.Applicant",
                           "applicantDate",
                           Date.class.getName(),
                           DataType.TYPE_DATE );

            when( dmo.getPackageName() ).thenReturn( "org.test" );

            RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal( drl,
                                                                               Collections.EMPTY_LIST,
                                                                               dmo );

            assertNotNull( m );
            assertEquals( "rule1",
                          m.name );

            assertEquals( 1,
                          m.lhs.length );
            IPattern p = m.lhs[ 0 ];
            assertTrue( p instanceof FactPattern );

            FactPattern fp = (FactPattern) p;
            assertEquals( "Applicant",
                          fp.getFactType() );
            assertEquals( "$a",
                          fp.getBoundName() );

            assertNull( fp.getConstraintList() );

            assertEquals( 2,
                          m.rhs.length );

            assertTrue( m.rhs[ 0 ] instanceof FreeFormLine );
            FreeFormLine ffl = (FreeFormLine) m.rhs[ 0 ];
            assertEquals( "java.util.Date newStartDate = new java.util.Date();",
                          ffl.getText() );

            assertTrue( m.rhs[ 1 ] instanceof ActionUpdateField );
            ActionUpdateField auf = (ActionUpdateField) m.rhs[ 1 ];
            assertEquals( "$a",
                          auf.getVariable() );
            assertEquals( 1,
                          auf.getFieldValues().length );
            ActionFieldValue afv = auf.getFieldValues()[ 0 ];
            assertEquals( "applicantDate",
                          afv.getField() );
            assertEquals( "newStartDate",
                          afv.getValue() );
            assertEquals( FieldNatureType.TYPE_FORMULA,
                          afv.getNature() );

            assertEqualsIgnoreWhitespace( drl,
                                          RuleModelDRLPersistenceImpl.getInstance().marshal( m ) );
        } finally {
            if ( oldValue == null ) {
                System.clearProperty( "drools.dateformat" );
            } else {
                System.setProperty( "drools.dateformat",
                                    oldValue );
            }
        }
    }

    @Test
    //https://issues.jboss.org/browse/GUVNOR-2143
    public void testNewKeywordVariableNamePrefix3() {
        String oldValue = System.getProperty( "drools.dateformat" );
        try {

            System.setProperty( "drools.dateformat",
                                "dd-MMM-yyyy" );

            String drl = "package org.test;\n"
                    + "rule \"rule1\"\n"
                    + "  dialect \"java\"\n"
                    + "  when\n"
                    + "    $a : Applicant()\n"
                    + "  then\n"
                    + "    java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(\"dd-MMM-yyyy\");\n"
                    + "    java.util.Date newStartDate = new java.util.Date();\n"
                    + "    modify( $a ) \n"
                    + "    { setApplicantDate( newStartDate ) }\n"
                    + "end\n";

            addModelField( "org.test.Applicant",
                           "this",
                           "org.test.Applicant",
                           DataType.TYPE_THIS );
            addModelField( "org.test.Applicant",
                           "applicantDate",
                           Date.class.getName(),
                           DataType.TYPE_DATE );

            when( dmo.getPackageName() ).thenReturn( "org.test" );

            RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal( drl,
                                                                               Collections.EMPTY_LIST,
                                                                               dmo );

            assertNotNull( m );
            assertEquals( "rule1",
                          m.name );

            assertEquals( 1,
                          m.lhs.length );
            IPattern p = m.lhs[ 0 ];
            assertTrue( p instanceof FactPattern );

            FactPattern fp = (FactPattern) p;
            assertEquals( "Applicant",
                          fp.getFactType() );
            assertEquals( "$a",
                          fp.getBoundName() );

            assertNull( fp.getConstraintList() );

            assertEquals( 2,
                          m.rhs.length );

            assertTrue( m.rhs[ 0 ] instanceof FreeFormLine );
            FreeFormLine ffl = (FreeFormLine) m.rhs[ 0 ];
            assertEquals( "java.util.Date newStartDate = new java.util.Date();",
                          ffl.getText() );

            assertTrue( m.rhs[ 1 ] instanceof ActionUpdateField );
            ActionUpdateField auf = (ActionUpdateField) m.rhs[ 1 ];
            assertEquals( "$a",
                          auf.getVariable() );
            assertEquals( 1,
                          auf.getFieldValues().length );
            ActionFieldValue afv = auf.getFieldValues()[ 0 ];
            assertEquals( "applicantDate",
                          afv.getField() );
            assertEquals( "newStartDate",
                          afv.getValue() );
            assertEquals( FieldNatureType.TYPE_FORMULA,
                          afv.getNature() );

            assertEqualsIgnoreWhitespace( drl,
                                          RuleModelDRLPersistenceImpl.getInstance().marshal( m ) );
        } finally {
            if ( oldValue == null ) {
                System.clearProperty( "drools.dateformat" );
            } else {
                System.setProperty( "drools.dateformat",
                                    oldValue );
            }
        }
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
