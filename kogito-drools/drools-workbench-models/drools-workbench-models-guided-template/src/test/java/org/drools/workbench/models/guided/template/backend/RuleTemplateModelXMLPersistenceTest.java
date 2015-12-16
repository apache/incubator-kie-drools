/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.models.guided.template.backend;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.drools.workbench.models.datamodel.oracle.DataType;
import org.drools.workbench.models.datamodel.rule.ActionFieldValue;
import org.drools.workbench.models.datamodel.rule.ActionGlobalCollectionAdd;
import org.drools.workbench.models.datamodel.rule.ActionInsertFact;
import org.drools.workbench.models.datamodel.rule.ActionRetractFact;
import org.drools.workbench.models.datamodel.rule.ActionUpdateField;
import org.drools.workbench.models.datamodel.rule.BaseSingleFieldConstraint;
import org.drools.workbench.models.datamodel.rule.CompositeFactPattern;
import org.drools.workbench.models.datamodel.rule.CompositeFieldConstraint;
import org.drools.workbench.models.datamodel.rule.ConnectiveConstraint;
import org.drools.workbench.models.datamodel.rule.DSLSentence;
import org.drools.workbench.models.datamodel.rule.FactPattern;
import org.drools.workbench.models.datamodel.rule.FreeFormLine;
import org.drools.workbench.models.datamodel.rule.IAction;
import org.drools.workbench.models.datamodel.rule.IPattern;
import org.drools.workbench.models.datamodel.rule.RuleAttribute;
import org.drools.workbench.models.datamodel.rule.RuleModel;
import org.drools.workbench.models.datamodel.rule.SingleFieldConstraint;
import org.drools.workbench.models.guided.template.shared.TemplateModel;
import org.junit.Test;

import static org.junit.Assert.*;

public class RuleTemplateModelXMLPersistenceTest {

    @Test
    public void testGenerateEmptyXML() {
        final RuleTemplateModelPersistence p = RuleTemplateModelXMLPersistenceImpl.getInstance();
        final String xml = p.marshal( new TemplateModel() );
        assertNotNull( xml );
        assertFalse( xml.equals( "" ) );

        assertTrue( xml.startsWith( "<rule>" ) );
        assertTrue( xml.endsWith( "</rule>" ) );
    }

    @Test
    public void testBasics() {
        final RuleTemplateModelPersistence p = RuleTemplateModelXMLPersistenceImpl.getInstance();
        final TemplateModel m = new TemplateModel();
        m.addLhsItem( new FactPattern( "Person" ) );
        m.addLhsItem( new FactPattern( "Accident" ) );
        m.addAttribute( new RuleAttribute( "no-loop",
                                           "true" ) );

        m.addRhsItem( new ActionInsertFact( "Report" ) );
        ActionGlobalCollectionAdd ag = new ActionGlobalCollectionAdd();
        ag.setFactName( "x" );
        ag.setGlobalName( "g" );
        m.addRhsItem( ag );
        m.name = "my rule";
        final String xml = p.marshal( m );
        System.out.println( xml );
        assertTrue( xml.indexOf( "Person" ) > -1 );
        assertTrue( xml.indexOf( "Accident" ) > -1 );
        assertTrue( xml.indexOf( "no-loop" ) > -1 );
        assertTrue( xml.indexOf( "org.kie" ) == -1 );
        assertTrue( xml.indexOf( "addToGlobal" ) > -1 );

        RuleModel rm_ = RuleTemplateModelXMLPersistenceImpl.getInstance().unmarshal( xml );
        assertEquals( 2,
                      rm_.rhs.length );

    }

    @Test
    public void testMoreComplexRendering() {
        final RuleTemplateModelPersistence p = RuleTemplateModelXMLPersistenceImpl.getInstance();
        final TemplateModel m = getComplexModel();

        final String xml = p.marshal( m );
        System.out.println( xml );

        assertTrue( xml.indexOf( "org.kie" ) == -1 );

    }

    @Test
    public void testRoundTrip() {
        final TemplateModel m = getComplexModel();

        final String xml = RuleTemplateModelXMLPersistenceImpl.getInstance().marshal( m );

        final TemplateModel m2 = RuleTemplateModelXMLPersistenceImpl.getInstance().unmarshal( xml );
        assertNotNull( m2 );
        assertEquals( m.name,
                      m2.name );
        assertEquals( m.lhs.length,
                      m2.lhs.length );
        assertEquals( m.rhs.length,
                      m2.rhs.length );
        assertEquals( 1,
                      m.attributes.length );

        final RuleAttribute at = m.attributes[ 0 ];
        assertEquals( "no-loop",
                      at.getAttributeName() );
        assertEquals( "true",
                      at.getValue() );

        final String newXML = RuleTemplateModelXMLPersistenceImpl.getInstance().marshal( m2 );
        assertEquals( xml,
                      newXML );

    }

    @Test
    public void testCompositeConstraintsRoundTrip() throws Exception {
        TemplateModel m = new TemplateModel();
        m.name = "with composite";

        FactPattern p1 = new FactPattern( "Person" );
        p1.setBoundName( "p1" );
        m.addLhsItem( p1 );

        FactPattern p = new FactPattern( "Goober" );
        m.addLhsItem( p );
        CompositeFieldConstraint comp = new CompositeFieldConstraint();
        comp.setCompositeJunctionType( CompositeFieldConstraint.COMPOSITE_TYPE_OR );
        p.addConstraint( comp );

        final SingleFieldConstraint X = new SingleFieldConstraint();
        X.setFieldName( "goo" );
        X.setConstraintValueType( SingleFieldConstraint.TYPE_LITERAL );
        X.setValue( "foo" );
        X.setOperator( "==" );
        X.setConnectives( new ConnectiveConstraint[ 1 ] );
        X.getConnectives()[ 0 ] = new ConnectiveConstraint();
        X.getConnectives()[ 0 ].setConstraintValueType( ConnectiveConstraint.TYPE_LITERAL );
        X.getConnectives()[ 0 ].setOperator( "|| ==" );
        X.getConnectives()[ 0 ].setValue( "bar" );
        comp.addConstraint( X );

        final SingleFieldConstraint Y = new SingleFieldConstraint();
        Y.setFieldName( "goo2" );
        Y.setConstraintValueType( SingleFieldConstraint.TYPE_LITERAL );
        Y.setValue( "foo" );
        Y.setOperator( "==" );
        comp.addConstraint( Y );

        CompositeFieldConstraint comp2 = new CompositeFieldConstraint();
        comp2.setCompositeJunctionType( CompositeFieldConstraint.COMPOSITE_TYPE_AND );
        final SingleFieldConstraint Q1 = new SingleFieldConstraint();
        Q1.setFieldName( "goo" );
        Q1.setOperator( "==" );
        Q1.setValue( "whee" );
        Q1.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );

        comp2.addConstraint( Q1 );

        final SingleFieldConstraint Q2 = new SingleFieldConstraint();
        Q2.setFieldName( "gabba" );
        Q2.setOperator( "==" );
        Q2.setValue( "whee" );
        Q2.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );

        comp2.addConstraint( Q2 );

        //now nest it
        comp.addConstraint( comp2 );

        final SingleFieldConstraint Z = new SingleFieldConstraint();
        Z.setFieldName( "goo3" );
        Z.setConstraintValueType( SingleFieldConstraint.TYPE_LITERAL );
        Z.setValue( "foo" );
        Z.setOperator( "==" );

        p.addConstraint( Z );

        ActionInsertFact ass = new ActionInsertFact( "Whee" );
        m.addRhsItem( ass );

        String xml = RuleTemplateModelXMLPersistenceImpl.getInstance().marshal( m );
        //System.err.println(xml);

        RuleModel m2 = RuleTemplateModelXMLPersistenceImpl.getInstance().unmarshal( xml );
        assertNotNull( m2 );
        assertEquals( "with composite",
                      m2.name );

        assertEquals( m2.lhs.length,
                      m.lhs.length );
        assertEquals( m2.rhs.length,
                      m.rhs.length );

    }

    @Test
    public void testFreeFormLine() {
        TemplateModel m = new TemplateModel();
        m.name = "with composite";
        m.lhs = new IPattern[ 1 ];
        m.rhs = new IAction[ 1 ];

        FreeFormLine fl = new FreeFormLine();
        fl.setText( "Person()" );
        m.lhs[ 0 ] = fl;

        FreeFormLine fr = new FreeFormLine();
        fr.setText( "fun()" );
        m.rhs[ 0 ] = fr;

        String xml = RuleTemplateModelXMLPersistenceImpl.getInstance().marshal( m );
        assertNotNull( xml );

        RuleModel m_ = RuleTemplateModelXMLPersistenceImpl.getInstance().unmarshal( xml );
        assertEquals( 1,
                      m_.lhs.length );
        assertEquals( 1,
                      m_.rhs.length );

        assertEquals( "Person()",
                      ( (FreeFormLine) m_.lhs[ 0 ] ).getText() );
        assertEquals( "fun()",
                      ( (FreeFormLine) m_.rhs[ 0 ] ).getText() );

    }

    /**
     * This will verify that we can load an old BRL change. If this fails, then
     * backwards compatibility is broken.
     */
    @Test
    public void testBackwardsCompatibility() throws Exception {
        RuleModel m2 = RuleTemplateModelXMLPersistenceImpl.getInstance().unmarshal( loadResource( "existing_brl.xml" ) );
        assertNotNull( m2 );
        assertEquals( 3,
                      m2.rhs.length );
    }

    public static String loadResource( final String name ) throws Exception {

        //        System.err.println( getClass().getResource( name ) );
        final InputStream in = RuleTemplateModelXMLPersistenceTest.class.getResourceAsStream( name );

        final Reader reader = new InputStreamReader( in );

        final StringBuilder text = new StringBuilder();

        final char[] buf = new char[ 1024 ];
        int len = 0;

        while ( ( len = reader.read( buf ) ) >= 0 ) {
            text.append( buf,
                         0,
                         len );
        }

        return text.toString();
    }

    private TemplateModel getComplexModel() {
        final TemplateModel m = new TemplateModel();

        m.addAttribute( new RuleAttribute( "no-loop",
                                           "true" ) );

        final FactPattern pat = new FactPattern( "Person" );
        pat.setBoundName( "p1" );
        final SingleFieldConstraint con = new SingleFieldConstraint();
        con.setFactType( "Person" );
        con.setFieldBinding( "f1" );
        con.setFieldName( "age" );
        con.setOperator( "<" );
        con.setValue( "42" );
        pat.addConstraint( con );

        m.addLhsItem( pat );

        final CompositeFactPattern comp = new CompositeFactPattern( "not" );
        comp.addFactPattern( new FactPattern( "Cancel" ) );
        m.addLhsItem( comp );

        final ActionUpdateField set = new ActionUpdateField();
        set.setVariable( "p1" );
        set.addFieldValue( new ActionFieldValue( "status",
                                                 "rejected",
                                                 DataType.TYPE_STRING ) );
        m.addRhsItem( set );

        final ActionRetractFact ret = new ActionRetractFact( "p1" );
        m.addRhsItem( ret );

        final DSLSentence sen = new DSLSentence();
        sen.setDefinition( "Send an email to {administrator}" );

        m.addRhsItem( sen );
        return m;
    }

    @Test
    public void testLoadEmpty() {
        RuleModel m = RuleTemplateModelXMLPersistenceImpl.getInstance().unmarshal( null );
        assertNotNull( m );

        m = RuleTemplateModelXMLPersistenceImpl.getInstance().unmarshal( "" );
        assertNotNull( m );
    }

}
