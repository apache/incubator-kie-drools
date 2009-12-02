/*
 * Copyright 2006 JBoss Inc
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

package org.drools.rule.builder.dialect.java;

import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.HashSet;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.drools.base.ClassTypeResolver;
import org.drools.base.EnabledBoolean;
import org.drools.base.TypeResolver;
import org.drools.compiler.Dialect;
import org.drools.compiler.DialectCompiletimeRegistry;
import org.drools.compiler.DrlParser;
import org.drools.compiler.PackageBuilder;
import org.drools.compiler.PackageBuilderConfiguration;
import org.drools.guvnor.client.modeldriven.brl.FieldConstraint;
import org.drools.lang.descr.AndDescr;
import org.drools.lang.descr.AttributeDescr;
import org.drools.lang.descr.FieldConstraintDescr;
import org.drools.lang.descr.LiteralRestrictionDescr;
import org.drools.lang.descr.PackageDescr;
import org.drools.lang.descr.PatternDescr;
import org.drools.lang.descr.RuleDescr;
import org.drools.rule.GroupElement;
import org.drools.rule.LiteralConstraint;
import org.drools.rule.LiteralRestriction;
import org.drools.rule.Package;
import org.drools.rule.Pattern;
import org.drools.rule.Rule;
import org.drools.rule.builder.RuleBuildContext;
import org.drools.rule.builder.RuleBuilder;
import org.drools.time.TimeUtils;
import org.drools.time.impl.DurationTimer;
import org.drools.time.impl.IntervalTimer;
import org.drools.util.DateUtils;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;

/**
 * @author etirelli
 *
 */
public class RuleBuilderTest extends TestCase {
    // mock factory
    private Mockery mockery = new Mockery() {
                                {
                                    // need to set this to be able to mock concrete classes
                                    setImposteriser( ClassImposteriser.INSTANCE );
                                }
                            };

    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
    }

    /* (non-Javadoc)
     * @see junit.framework.TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Test method for {@link org.drools.rule.builder.RuleBuilder#build(org.drools.rule.Package, org.drools.lang.descr.RuleDescr)}.
     */
    public void testBuild() throws Exception {
        final DrlParser parser = new DrlParser();

        final PackageBuilder pkgBuilder = new PackageBuilder();
        pkgBuilder.addPackage( new PackageDescr( "org.drools" ) );
        Package pkg = pkgBuilder.getPackage();

        final PackageDescr pkgDescr = parser.parse( new InputStreamReader( getClass().getResourceAsStream( "nestedConditionalElements.drl" ) ) );

        // just checking there is no parsing errors
        Assert.assertFalse( parser.getErrors().toString(),
                            parser.hasErrors() );

        final RuleDescr ruleDescr = (RuleDescr) pkgDescr.getRules().get( 0 );
        final String ruleClassName = "RuleClassName.java";
        ruleDescr.setClassName( ruleClassName );
        ruleDescr.addAttribute( new AttributeDescr( "dialect",
                                                    "java" ) );

        final TypeResolver typeResolver = new ClassTypeResolver( new HashSet(),
                                                                 this.getClass().getClassLoader() );
        // make an automatic import for the current package
        typeResolver.addImport( pkgDescr.getName() + ".*" );
        typeResolver.addImport( "java.lang.*" );

        final RuleBuilder builder = new RuleBuilder();

        final PackageBuilderConfiguration conf = pkgBuilder.getPackageBuilderConfiguration();

        DialectCompiletimeRegistry dialectRegistry = pkgBuilder.getPackageRegistry( pkg.getName() ).getDialectCompiletimeRegistry();
        Dialect dialect = dialectRegistry.getDialect( "java" );

        RuleBuildContext context = new RuleBuildContext( pkgBuilder,
                                                         ruleDescr,
                                                         dialectRegistry,
                                                         pkg,
                                                         dialect );

        builder.build( context );

        Assert.assertTrue( context.getErrors().toString(),
                           context.getErrors().isEmpty() );

        final Rule rule = context.getRule();

        assertEquals( "There should be 2 rule level declarations",
                      2,
                      rule.getDeclarations().length );

        // second GE should be a not
        final GroupElement not = (GroupElement) rule.getLhs().getChildren().get( 1 );
        assertTrue( not.isNot() );
        // not has no outer declarations
        assertTrue( not.getOuterDeclarations().isEmpty() );
        assertEquals( 1,
                      not.getInnerDeclarations().size() );
        assertTrue( not.getInnerDeclarations().keySet().contains( "$state" ) );

        // second not
        final GroupElement not2 = (GroupElement) ((GroupElement) not.getChildren().get( 0 )).getChildren().get( 1 );
        assertTrue( not2.isNot() );
        // not has no outer declarations
        assertTrue( not2.getOuterDeclarations().isEmpty() );
        assertEquals( 1,
                      not2.getInnerDeclarations().size() );
        assertTrue( not2.getInnerDeclarations().keySet().contains( "$likes" ) );
    }

    public void testBuildAttributes() throws Exception {
        // creates mock objects
        final RuleBuildContext context = mockery.mock( RuleBuildContext.class );
        final Rule rule = mockery.mock( Rule.class );

        // creates input object
        final RuleDescr ruleDescr = new RuleDescr( "my rule" );
        ruleDescr.addAttribute( new AttributeDescr( "no-loop",
                                                    "true" ) );
        ruleDescr.addAttribute( new AttributeDescr( "auto-focus",
                                                    "false" ) );
        ruleDescr.addAttribute( new AttributeDescr( "agenda-group",
                                                    "my agenda" ) );
        ruleDescr.addAttribute( new AttributeDescr( "activation-group",
                                                    "my activation" ) );
        ruleDescr.addAttribute( new AttributeDescr( "ruleflow-group",
                                                    "mygroup" ) );
        ruleDescr.addAttribute( new AttributeDescr( "lock-on-active",
                                                    "" ) );
        ruleDescr.addAttribute( new AttributeDescr( "enabled",
                                                    "false" ) );
        ruleDescr.addAttribute( new AttributeDescr( "duration",
                                                    "60" ) );
        ruleDescr.addAttribute( new AttributeDescr( "date-effective",
                                                    "10-Jul-1974" ) );
        ruleDescr.addAttribute( new AttributeDescr( "date-expires",
                                                    "10-Jul-2040" ) );

        // creates expected results
        final Calendar effective = Calendar.getInstance();
        effective.setTime( DateUtils.parseDate( "10-Jul-1974" ) );
        final Calendar expires = Calendar.getInstance();
        expires.setTime( DateUtils.parseDate( "10-Jul-2040" ) );

        // defining expectations on the mock object
        mockery.checking( new Expectations() {
            {
                // return values for the context
                allowing( context ).getRule(); will( returnValue( rule ) );
                allowing( context ).getRuleDescr(); will( returnValue( ruleDescr ) );

                // expected values for the rule object
                oneOf( rule ).setNoLoop( true );
                oneOf( rule ).setAutoFocus( false );
                oneOf( rule ).setAgendaGroup( "my agenda" );
                oneOf( rule ).setActivationGroup( "my activation" );
                oneOf( rule ).setRuleFlowGroup( "mygroup" );
                oneOf( rule ).setLockOnActive( true );
                oneOf( rule ).setEnabled( EnabledBoolean.ENABLED_FALSE );
                oneOf( rule ).setTimer( new IntervalTimer( null , null, TimeUtils.parseTimeString( "60" ), 0 ) );
                oneOf( rule ).setDateEffective( effective );
                oneOf( rule ).setDateExpires( expires );
            }
        } );

        // calling the build method
        RuleBuilder builder = new RuleBuilder();
        builder.buildAttributes( context );

        // check expectations
        mockery.assertIsSatisfied();

    }

    public void testBuildDurationExpression() throws Exception {
        // creates mock objects
        final RuleBuildContext context = mockery.mock( RuleBuildContext.class );
        final Rule rule = mockery.mock( Rule.class );

        // creates input object
        final RuleDescr ruleDescr = new RuleDescr( "my rule" );
        ruleDescr.addAttribute( new AttributeDescr( "duration",
                                                    "( 1h30m )" ) );

        // defining expectations on the mock object
        mockery.checking( new Expectations() {
            {
                // return values for the context
                allowing( context ).getRule(); will( returnValue( rule ) );
                allowing( context ).getRuleDescr(); will( returnValue( ruleDescr ) );

                // expected values for the rule object
                oneOf( rule ).setTimer( new IntervalTimer( null , null, TimeUtils.parseTimeString( "1h30m" ), 0 ) );
            }
        } );

        // calling the build method
        RuleBuilder builder = new RuleBuilder();
        builder.buildAttributes( context );

        // check expectations
        mockery.assertIsSatisfied();

    }
    
    public void testBuildBigDecimalLiteralConstraint() throws Exception {
        final PackageDescr pkgDescr = new PackageDescr("org.drools");
        final RuleDescr ruleDescr = new RuleDescr("Test Rule");
        AndDescr andDescr = new AndDescr();
        PatternDescr patDescr = new PatternDescr("java.math.BigDecimal", "$bd");
        FieldConstraintDescr fcd = new FieldConstraintDescr("this");
        LiteralRestrictionDescr restr = new LiteralRestrictionDescr("==", "10");
        fcd.addRestriction( restr );
        patDescr.addConstraint( fcd );
        andDescr.addDescr( patDescr );
        ruleDescr.setLhs( andDescr );
        ruleDescr.setConsequence( "" );
        pkgDescr.addRule( ruleDescr );
        
        final PackageBuilder pkgBuilder = new PackageBuilder();
        pkgBuilder.addPackage( pkgDescr );

        Assert.assertTrue( pkgBuilder.getErrors().toString(),
                           pkgBuilder.getErrors().isEmpty() );

        final Rule rule = pkgBuilder.getPackages()[0].getRule("Test Rule");
        final GroupElement and = rule.getLhs();
        final Pattern pat = (Pattern) and.getChildren().get( 0 );
        final LiteralConstraint fc = (LiteralConstraint) pat.getConstraints().get( 0 );
        assertTrue("Wrong class. Expected java.math.BigDecimal. Found: " + fc.getField().getValue().getClass(), fc.getField().getValue() instanceof BigDecimal );
    }

    public void testBuildBigIntegerLiteralConstraint() throws Exception {
        final PackageDescr pkgDescr = new PackageDescr("org.drools");
        final RuleDescr ruleDescr = new RuleDescr("Test Rule");
        AndDescr andDescr = new AndDescr();
        PatternDescr patDescr = new PatternDescr("java.math.BigInteger", "$bd");
        FieldConstraintDescr fcd = new FieldConstraintDescr("this");
        LiteralRestrictionDescr restr = new LiteralRestrictionDescr("==", "10");
        fcd.addRestriction( restr );
        patDescr.addConstraint( fcd );
        andDescr.addDescr( patDescr );
        ruleDescr.setLhs( andDescr );
        ruleDescr.setConsequence( "" );
        pkgDescr.addRule( ruleDescr );
        
        final PackageBuilder pkgBuilder = new PackageBuilder();
        pkgBuilder.addPackage( pkgDescr );

        Assert.assertTrue( pkgBuilder.getErrors().toString(),
                           pkgBuilder.getErrors().isEmpty() );

        final Rule rule = pkgBuilder.getPackages()[0].getRule("Test Rule");
        final GroupElement and = rule.getLhs();
        final Pattern pat = (Pattern) and.getChildren().get( 0 );
        final LiteralConstraint fc = (LiteralConstraint) pat.getConstraints().get( 0 );
        assertTrue("Wrong class. Expected java.math.BigInteger. Found: " + fc.getField().getValue().getClass(), fc.getField().getValue() instanceof BigInteger );
    }

    
}
