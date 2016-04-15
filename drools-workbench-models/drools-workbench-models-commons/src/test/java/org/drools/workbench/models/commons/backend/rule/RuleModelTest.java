/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.workbench.models.commons.backend.rule;

import com.thoughtworks.xstream.XStream;
import org.drools.workbench.models.datamodel.rule.ActionRetractFact;
import org.drools.workbench.models.datamodel.rule.ActionSetField;
import org.drools.workbench.models.datamodel.rule.CompositeFactPattern;
import org.drools.workbench.models.datamodel.rule.CompositeFieldConstraint;
import org.drools.workbench.models.datamodel.rule.DSLSentence;
import org.drools.workbench.models.datamodel.rule.ExpressionField;
import org.drools.workbench.models.datamodel.rule.FactPattern;
import org.drools.workbench.models.datamodel.rule.FieldConstraint;
import org.drools.workbench.models.datamodel.rule.FromCompositeFactPattern;
import org.drools.workbench.models.datamodel.rule.IAction;
import org.drools.workbench.models.datamodel.rule.IPattern;
import org.drools.workbench.models.datamodel.rule.RuleAttribute;
import org.drools.workbench.models.datamodel.rule.RuleMetadata;
import org.drools.workbench.models.datamodel.rule.RuleModel;
import org.drools.workbench.models.datamodel.rule.SingleFieldConstraint;
import org.drools.workbench.models.datamodel.rule.SingleFieldConstraintEBLeftSide;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.junit.Assert.*;

public class RuleModelTest {

    private static final Logger logger = LoggerFactory.getLogger( RuleModelTest.class );

    @Test
    public void testAddItemLhs() {
        final RuleModel model = new RuleModel();
        final FactPattern x = new FactPattern();
        model.addLhsItem( x );
        assertEquals( 1,
                model.lhs.length );

        final FactPattern y = new FactPattern();
        model.addLhsItem( y );

        assertEquals( 2,
                model.lhs.length );
        assertEquals( x,
                model.lhs[0] );
        assertEquals( y,
                model.lhs[1] );

    }

    @Test
    public void testAddItemRhs() {
        final RuleModel model = new RuleModel();
        final IAction a0 = new ActionSetField();
        final IAction a1 = new ActionSetField();

        model.addRhsItem( a0 );

        assertEquals( 1,
                model.rhs.length );
        model.addRhsItem( a1 );

        assertEquals( 2,
                model.rhs.length );

        assertEquals( a0,
                model.rhs[0] );
        assertEquals( a1,
                model.rhs[1] );
    }

    @Test
    public void testAllVariableBindings() {
        final RuleModel model = new RuleModel();
        model.lhs = new IPattern[2];
        final FactPattern x = new FactPattern( "Car" );
        model.lhs[0] = x;
        x.setBoundName( "boundFact" );

        SingleFieldConstraint sfc = new SingleFieldConstraint( "q" );
        x.addConstraint( sfc );
        sfc.setFieldBinding( "field1" );

        SingleFieldConstraint sfc2 = new SingleFieldConstraint( "q" );
        x.addConstraint( sfc2 );
        sfc2.setFieldBinding( "field2" );

        model.lhs[1] = new CompositeFactPattern();

        List vars = model.getAllVariables();
        assertEquals( 3,
                vars.size() );
        assertEquals( "boundFact",
                vars.get( 0 ) );
        assertEquals( "field1",
                vars.get( 1 ) );
        assertEquals( "field2",
                vars.get( 2 ) );

        assertTrue( model.isVariableNameUsed( "field2" ) );

    }

    @Test
    public void testAllVariableBindings2() {
        final RuleModel model = new RuleModel();
        model.lhs = new IPattern[1];
        final FactPattern fp = new FactPattern( "Car" );
        model.lhs[0] = fp;
        fp.setBoundName( "$c" );

        SingleFieldConstraint sfc = new SingleFieldConstraintEBLeftSide( "make" );
        sfc.getExpressionValue().appendPart( new ExpressionField( "make",
                "java.lang.String",
                "String" ) );
        sfc.setFieldBinding( "$m" );
        fp.addConstraint( sfc );

        List<String> vars = model.getAllVariables();
        assertEquals( 2,
                vars.size() );
        assertEquals( "$c",
                vars.get( 0 ) );
        assertEquals( "$m",
                vars.get( 1 ) );

    }

    @Test
    public void testAttributes() {
        final RuleModel m = new RuleModel();
        final RuleAttribute at = new RuleAttribute( "salience",
                "42" );
        m.addAttribute( at );
        assertEquals( 1,
                m.attributes.length );
        assertEquals( at,
                m.attributes[0] );

        final RuleAttribute at2 = new RuleAttribute( "agenda-group",
                "x" );
        m.addAttribute( at2 );
        assertEquals( 2,
                m.attributes.length );
        assertEquals( at2,
                m.attributes[1] );

        m.removeAttribute( 0 );
        assertEquals( 1,
                m.attributes.length );
        assertEquals( at2,
                m.attributes[0] );
    }

    @Test
    public void testBoundFactFinder() {
        final RuleModel model = new RuleModel();

        assertNull( model.getLHSBoundFact( "x" ) );
        model.lhs = new IPattern[3];

        final FactPattern x = new FactPattern( "Car" );
        model.lhs[0] = x;
        x.setBoundName( "x" );

        assertNotNull( model.getLHSBoundFact( "x" ) );
        assertEquals( x,
                model.getLHSBoundFact( "x" ) );

        final FactPattern y = new FactPattern( "Car" );
        model.lhs[1] = y;
        y.setBoundName( "y" );

        final FactPattern other = new FactPattern( "House" );
        model.lhs[2] = other;

        assertEquals( y,
                model.getLHSBoundFact( "y" ) );
        assertEquals( x,
                model.getLHSBoundFact( "x" ) );

        model.rhs = new IAction[1];
        final ActionSetField set = new ActionSetField();
        set.setVariable( "x" );
        model.rhs[0] = set;

        assertTrue( model.isBoundFactUsed( "x" ) );
        assertFalse( model.isBoundFactUsed( "y" ) );

        assertEquals( 3,
                model.lhs.length );
        assertFalse( model.removeLhsItem( 0 ) );
        assertEquals( 3,
                model.lhs.length );

        final ActionRetractFact fact = new ActionRetractFact( "q" );
        model.rhs[0] = fact;
        assertTrue( model.isBoundFactUsed( "q" ) );
        assertFalse( model.isBoundFactUsed( "x" ) );

        final XStream xt = new XStream();
        xt.alias( "rule",
                RuleModel.class );
        xt.alias( "fact",
                FactPattern.class );
        xt.alias( "retract",
                ActionRetractFact.class );

        //See https://issues.jboss.org/browse/GUVNOR-1115
        xt.aliasPackage( "org.drools.guvnor.client",
                "org.drools.ide.common.client" );

        final String brl = xt.toXML( model );

        logger.debug( brl );
    }

    @Test
    public void testGetVariableNameForRHS() {
        RuleModel m = new RuleModel();
        m.name = "blah";

        FactPattern pat = new FactPattern( "Person" );
        pat.setBoundName( "pat" );

        m.addLhsItem( pat );

        List l = m.getAllVariables();
        assertEquals( 1,
                l.size() );
        assertEquals( "pat",
                l.get( 0 ) );

    }

    @Test
    public void testIsDSLEnhanced() throws Exception {
        RuleModel m = new RuleModel();

        assertFalse( m.hasDSLSentences() );

        m.addLhsItem( new FactPattern() );
        assertFalse( m.hasDSLSentences() );

        m.addRhsItem( new ActionSetField( "q" ) );

        assertFalse( m.hasDSLSentences() );

        m.addLhsItem( new DSLSentence() );
        assertTrue( m.hasDSLSentences() );

        m.addRhsItem( new DSLSentence() );
        assertTrue( m.hasDSLSentences() );

        m = new RuleModel();

        m.addLhsItem( new DSLSentence() );
        assertTrue( m.hasDSLSentences() );

        m = new RuleModel();
        m.addRhsItem( new DSLSentence() );
        assertTrue( m.hasDSLSentences() );

    }

    @Test
    public void testMetaData() {
        final RuleModel m = new RuleModel();

        final RuleMetadata rm = new RuleMetadata( "foo",
                "bar" );

        // test add
        m.addMetadata( rm );
        assertEquals( 1,
                m.metadataList.length );
        assertEquals( rm,
                m.metadataList[0] );

        // should be able to find it
        RuleMetadata gm = m.getMetaData( "foo" );
        assertNotNull( gm );

        // test add and remove
        final RuleMetadata rm2 = new RuleMetadata( "foo2",
                "bar2" );
        m.addMetadata( rm2 );
        assertEquals( 2,
                m.metadataList.length );
        assertEquals( rm2,
                m.metadataList[1] );
        assertEquals( "@foo(bar)",
                rm.toString() );

        m.removeMetadata( 0 );
        assertEquals( 1,
                m.metadataList.length );
        assertEquals( rm2,
                m.metadataList[0] );
        assertEquals( "@foo2(bar2)",
                (m.metadataList[0]).toString() );

        // should be able to find it now that it was removed
        gm = m.getMetaData( "foo" );
        assertNull( gm );

        // test add via update method
        m.updateMetadata( rm );
        gm = m.getMetaData( "foo" );
        assertNotNull( gm );

        // test update of existing element
        rm.setValue( "bar2" );
        m.updateMetadata( rm );
        gm = m.getMetaData( "foo" );
        assertNotNull( gm );
        assertEquals( "bar2",
                gm.getValue() );

    }

    @Test
    public void testRemoveItemLhs() {
        final RuleModel model = new RuleModel();

        model.lhs = new IPattern[3];
        final FactPattern x = new FactPattern( "Car" );
        model.lhs[0] = x;
        x.setBoundName( "x" );

        final FactPattern y = new FactPattern( "Car" );
        model.lhs[1] = y;
        y.setBoundName( "y" );

        final FactPattern other = new FactPattern( "House" );
        model.lhs[2] = other;

        assertEquals( 3,
                model.lhs.length );
        assertEquals( x,
                model.lhs[0] );

        model.removeLhsItem( 0 );

        assertEquals( 2,
                model.lhs.length );
        assertEquals( y,
                model.lhs[0] );
    }

    @Test
    public void testRemoveItemRhs() {
        final RuleModel model = new RuleModel();

        model.rhs = new IAction[3];
        final ActionRetractFact r0 = new ActionRetractFact( "x" );
        final ActionRetractFact r1 = new ActionRetractFact( "y" );
        final ActionRetractFact r2 = new ActionRetractFact( "z" );

        model.rhs[0] = r0;
        model.rhs[1] = r1;
        model.rhs[2] = r2;

        model.removeRhsItem( 1 );

        assertEquals( 2,
                model.rhs.length );
        assertEquals( r0,
                model.rhs[0] );
        assertEquals( r2,
                model.rhs[1] );
    }

    @Test
    public void testScopedVariablesWithCompositeFact() {
        RuleModel m = new RuleModel();
        FactPattern p = new FactPattern();

        CompositeFieldConstraint cf = new CompositeFieldConstraint();
        cf.addConstraint( new SingleFieldConstraint( "x" ) );
        p.addConstraint( cf );

        SingleFieldConstraint sf = new SingleFieldConstraint( "q" );
        sf.setFieldBinding( "abc" );
        p.addConstraint( sf );

        SingleFieldConstraint sf2 = new SingleFieldConstraint( "q" );
        sf2.setFieldBinding( "qed" );
        cf.addConstraint( sf2 );
        m.addLhsItem( p );

        List vars = m.getAllVariables();
        assertEquals( 1,
                vars.size() );
        assertEquals( "abc",
                vars.get( 0 ) );
    }

    @Test
    public void testAddItemLhsAtSpecificPosition() {
        final RuleModel model = new RuleModel();

        final FactPattern a = new FactPattern();
        model.addLhsItem( a );

        assertEquals( 1,
                model.lhs.length );

        final FactPattern b = new FactPattern();
        model.addLhsItem( b );

        assertEquals( 2,
                model.lhs.length );

        final FactPattern c = new FactPattern();
        model.addLhsItem( c,
                true );

        assertEquals( 3,
                model.lhs.length );

        assertEquals( a,
                model.lhs[0] );
        assertEquals( b,
                model.lhs[1] );
        assertEquals( c,
                model.lhs[2] );

        final FactPattern d = new FactPattern();
        model.addLhsItem( d,
                false );

        assertEquals( 4,
                model.lhs.length );

        assertEquals( d,
                model.lhs[0] );
        assertEquals( a,
                model.lhs[1] );
        assertEquals( b,
                model.lhs[2] );
        assertEquals( c,
                model.lhs[3] );

        final FactPattern e = new FactPattern();
        model.addLhsItem( e,
                2 );

        assertEquals( 5,
                model.lhs.length );

        assertEquals( d,
                model.lhs[0] );
        assertEquals( a,
                model.lhs[1] );
        assertEquals( e,
                model.lhs[2] );
        assertEquals( b,
                model.lhs[3] );
        assertEquals( c,
                model.lhs[4] );

        //test auto-bound
        final FactPattern f = new FactPattern();
        final FactPattern g = new FactPattern();
        model.addLhsItem( f,
                -1 );
        model.addLhsItem( g,
                100 );

        assertEquals( 7,
                model.lhs.length );

        assertEquals( f,
                model.lhs[0] );
        assertEquals( d,
                model.lhs[1] );
        assertEquals( a,
                model.lhs[2] );
        assertEquals( e,
                model.lhs[3] );
        assertEquals( b,
                model.lhs[4] );
        assertEquals( c,
                model.lhs[5] );
        assertEquals( g,
                model.lhs[6] );

        model.moveLhsItemDown( 5 );
        model.moveLhsItemUp( 3 );

        assertEquals( 7,
                model.lhs.length );

        assertEquals( f,
                model.lhs[0] );
        assertEquals( d,
                model.lhs[1] );
        assertEquals( e,
                model.lhs[2] );
        assertEquals( a,
                model.lhs[3] );
        assertEquals( b,
                model.lhs[4] );
        assertEquals( g,
                model.lhs[5] );
        assertEquals( c,
                model.lhs[6] );

        model.moveLhsItemUp( 0 );
        model.moveLhsItemDown( 6 );

        assertEquals( 7,
                model.lhs.length );

        assertEquals( f,
                model.lhs[0] );
        assertEquals( d,
                model.lhs[1] );
        assertEquals( e,
                model.lhs[2] );
        assertEquals( a,
                model.lhs[3] );
        assertEquals( b,
                model.lhs[4] );
        assertEquals( g,
                model.lhs[5] );
        assertEquals( c,
                model.lhs[6] );

    }

    @Test
    public void testAddItemRhsAtSpecificPosition() {
        final RuleModel model = new RuleModel();

        final ActionSetField a = new ActionSetField();
        model.addRhsItem( a );

        assertEquals( 1,
                model.rhs.length );

        final ActionSetField b = new ActionSetField();
        model.addRhsItem( b );

        assertEquals( 2,
                model.rhs.length );

        final ActionSetField c = new ActionSetField();
        model.addRhsItem( c,
                true );

        assertEquals( 3,
                model.rhs.length );

        assertEquals( a,
                model.rhs[0] );
        assertEquals( b,
                model.rhs[1] );
        assertEquals( c,
                model.rhs[2] );

        final ActionSetField d = new ActionSetField();
        model.addRhsItem( d,
                false );

        assertEquals( 4,
                model.rhs.length );

        assertEquals( d,
                model.rhs[0] );
        assertEquals( a,
                model.rhs[1] );
        assertEquals( b,
                model.rhs[2] );
        assertEquals( c,
                model.rhs[3] );

        final ActionSetField e = new ActionSetField();
        model.addRhsItem( e,
                2 );

        assertEquals( 5,
                model.rhs.length );

        assertEquals( d,
                model.rhs[0] );
        assertEquals( a,
                model.rhs[1] );
        assertEquals( e,
                model.rhs[2] );
        assertEquals( b,
                model.rhs[3] );
        assertEquals( c,
                model.rhs[4] );

        //test auto-bound
        final ActionSetField f = new ActionSetField();
        final ActionSetField g = new ActionSetField();
        model.addRhsItem( f,
                -1 );
        model.addRhsItem( g,
                100 );

        assertEquals( 7,
                model.rhs.length );

        assertEquals( f,
                model.rhs[0] );
        assertEquals( d,
                model.rhs[1] );
        assertEquals( a,
                model.rhs[2] );
        assertEquals( e,
                model.rhs[3] );
        assertEquals( b,
                model.rhs[4] );
        assertEquals( c,
                model.rhs[5] );
        assertEquals( g,
                model.rhs[6] );

        model.moveRhsItemDown( 5 );
        model.moveRhsItemUp( 3 );

        assertEquals( 7,
                model.rhs.length );

        assertEquals( f,
                model.rhs[0] );
        assertEquals( d,
                model.rhs[1] );
        assertEquals( e,
                model.rhs[2] );
        assertEquals( a,
                model.rhs[3] );
        assertEquals( b,
                model.rhs[4] );
        assertEquals( g,
                model.rhs[5] );
        assertEquals( c,
                model.rhs[6] );

        model.moveRhsItemUp( 0 );
        model.moveRhsItemDown( 6 );

        assertEquals( 7,
                model.rhs.length );

        assertEquals( f,
                model.rhs[0] );
        assertEquals( d,
                model.rhs[1] );
        assertEquals( e,
                model.rhs[2] );
        assertEquals( a,
                model.rhs[3] );
        assertEquals( b,
                model.rhs[4] );
        assertEquals( g,
                model.rhs[5] );
        assertEquals( c,
                model.rhs[6] );

    }

    @Test
    public void testBoundFromCompositeFactFinder() {
        final RuleModel model = new RuleModel();

        model.lhs = new IPattern[1];

        final FromCompositeFactPattern fcfp = new FromCompositeFactPattern();
        final FactPattern x = new FactPattern( "Car" );
        x.setBoundName( "x" );
        final SingleFieldConstraint a = new SingleFieldConstraint( "name" );
        a.setFieldBinding( "a" );
        a.setFieldType( "String" );
        x.addConstraint( a );
        fcfp.setFactPattern( x );

        model.lhs[0] = fcfp;

        assertEquals( x,
                model.getLHSBoundFact( "x" ) );

        assertEquals( 1,
                model.getLHSBoundFacts().size() );
        assertEquals( "x",
                model.getLHSBoundFacts().get( 0 ) );

        assertEquals( a,
                model.getLHSBoundField( "a" ) );

        assertEquals( "Car",
                model.getLHSBindingType( "x" ) );
        assertEquals( "String",
                model.getLHSBindingType( "a" ) );

        assertEquals( x,
                model.getLHSParentFactPatternForBinding( "a" ) );

        assertEquals( 2,
                model.getAllLHSVariables().size() );
        assertTrue( model.getAllLHSVariables().contains( "x" ) );
        assertTrue( model.getAllLHSVariables().contains( "a" ) );

        model.rhs = new IAction[1];
        final ActionSetField set = new ActionSetField();
        set.setVariable( "x");
        model.rhs[0] = set;

        assertTrue( model.isBoundFactUsed( "x" ) );

        assertEquals( 1,
                model.lhs.length );
        assertFalse( model.removeLhsItem( 0 ) );
        assertEquals( 1,
                model.lhs.length );
    }

    @Test
    public void testBoundVariableFromOR() throws Exception {
        final RuleModel model = new RuleModel();

        model.lhs = new IPattern[1];

        CompositeFactPattern compositeFactPattern = new CompositeFactPattern(CompositeFactPattern.COMPOSITE_TYPE_OR);
        model.lhs[0] = compositeFactPattern;
        FactPattern pattern1 = new FactPattern();
        pattern1.setBoundName("test");
        compositeFactPattern.addFactPattern(pattern1);
        FactPattern pattern2 = new FactPattern();
        SingleFieldConstraint constraint = new SingleFieldConstraint();
        pattern2.addConstraint(constraint);
        compositeFactPattern.addFactPattern(pattern2);

        List<String> boundVariablesInScope = model.getBoundVariablesInScope(constraint);

        assertEquals(1,boundVariablesInScope.size());
    }

    @Test
    public void testBoundFieldVariableFromOR() throws Exception {
        final RuleModel model = new RuleModel();

        model.lhs = new IPattern[1];

        CompositeFactPattern compositeFactPattern = new CompositeFactPattern(CompositeFactPattern.COMPOSITE_TYPE_OR);
        model.lhs[0] = compositeFactPattern;
        FactPattern pattern1 = new FactPattern();
        SingleFieldConstraint constraint1 = new SingleFieldConstraint();
        pattern1.addConstraint(constraint1);
        constraint1.setFieldBinding("test");
        compositeFactPattern.addFactPattern(pattern1);
        FactPattern pattern2 = new FactPattern();
        SingleFieldConstraint constraint2 = new SingleFieldConstraint();
        pattern2.addConstraint(constraint2);
        compositeFactPattern.addFactPattern(pattern2);

        SingleFieldConstraint lhsBoundField = model.getLHSBoundField("test");

        assertEquals(constraint1,lhsBoundField);
    }

    @Test
    public void testLHSBindingTypeFromOR() throws Exception {
        final RuleModel model = new RuleModel();

        model.lhs = new IPattern[1];

        CompositeFactPattern compositeFactPattern = new CompositeFactPattern(CompositeFactPattern.COMPOSITE_TYPE_OR);
        model.lhs[0] = compositeFactPattern;
        FactPattern pattern1 = new FactPattern();
        SingleFieldConstraint constraint1 = new SingleFieldConstraint();
        pattern1.addConstraint(constraint1);
        constraint1.setFieldType("String");
        constraint1.setFieldBinding("test");
        compositeFactPattern.addFactPattern(pattern1);
        FactPattern pattern2 = new FactPattern();
        SingleFieldConstraint constraint2 = new SingleFieldConstraint();
        pattern2.addConstraint(constraint2);
        compositeFactPattern.addFactPattern(pattern2);

        assertEquals("String", model.getLHSBindingType("test"));
    }
}