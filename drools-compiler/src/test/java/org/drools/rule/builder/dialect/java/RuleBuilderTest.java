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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.HashSet;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import org.drools.base.ClassTypeResolver;
import org.drools.base.EnabledBoolean;
import org.drools.base.TypeResolver;
import org.drools.compiler.Dialect;
import org.drools.compiler.DialectCompiletimeRegistry;
import org.drools.compiler.DrlParser;
import org.drools.compiler.PackageBuilder;
import org.drools.compiler.PackageBuilderConfiguration;
import org.drools.core.util.DateUtils;
import org.drools.lang.descr.AndDescr;
import org.drools.lang.descr.AttributeDescr;
import org.drools.lang.descr.ExprConstraintDescr;
import org.drools.lang.descr.FieldConstraintDescr;
import org.drools.lang.descr.LiteralRestrictionDescr;
import org.drools.lang.descr.PackageDescr;
import org.drools.lang.descr.PatternDescr;
import org.drools.lang.descr.RuleDescr;
import org.drools.rule.GroupElement;
import org.drools.rule.LiteralConstraint;
import org.drools.rule.Package;
import org.drools.rule.Pattern;
import org.drools.rule.Rule;
import org.drools.rule.builder.RuleBuildContext;
import org.drools.rule.builder.RuleBuilder;
import org.drools.time.TimeUtils;
import org.drools.time.impl.IntervalTimer;
import org.drools.type.DateFormatsImpl;

import antlr.collections.List;

public class RuleBuilderTest {

    /**
     * Test method for {@link org.drools.rule.builder.RuleBuilder#build(org.drools.rule.Package, org.drools.lang.descr.RuleDescr)}.
     */
    @Test
    public void testBuild() throws Exception {
        final DrlParser parser = new DrlParser();

        final PackageBuilder pkgBuilder = new PackageBuilder();
        pkgBuilder.addPackage( new PackageDescr( "org.drools" ) );
        Package pkg = pkgBuilder.getPackage();

        final PackageDescr pkgDescr = parser.parse( new InputStreamReader( getClass().getResourceAsStream( "nestedConditionalElements.drl" ) ) );

        // just checking there is no parsing errors
        assertFalse( parser.getErrors().toString(),
                            parser.hasErrors() );
        
        pkg.addGlobal( "results", List.class );

        final RuleDescr ruleDescr = (RuleDescr) pkgDescr.getRules().get( 0 );
        final String ruleClassName = "RuleClassName.java";
        ruleDescr.setClassName( ruleClassName );
        ruleDescr.addAttribute( new AttributeDescr( "dialect",
                                                    "java" ) );
        
        pkgBuilder.addPackage( pkgDescr );

        assertTrue( pkgBuilder.getErrors().toString(),
                    pkgBuilder.getErrors().isEmpty() );

        final Rule rule = pkgBuilder.getPackage().getRule( "test nested CEs" );

        assertEquals( "There should be 2 rule level declarations",
                      2,
                      rule.getDeclarations().size() );

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

    @Test
    public void testBuildAttributes() throws Exception {
        // creates mock objects
        final RuleBuildContext context = mock( RuleBuildContext.class );
        final Rule rule = mock( Rule.class );

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
        ruleDescr.addAttribute( new AttributeDescr( "calendars",
                                                    "\"cal1\"" ) );
        ruleDescr.addAttribute( new AttributeDescr( "date-effective",
                                                    "10-Jul-1974" ) );
        ruleDescr.addAttribute( new AttributeDescr( "date-expires",
                                                    "10-Jul-2040" ) );

        // creates expected results
        final Calendar effective = Calendar.getInstance();
        effective.setTime( DateUtils.parseDate( "10-Jul-1974",
                                                new DateFormatsImpl() ) );
        final Calendar expires = Calendar.getInstance();
        expires.setTime( DateUtils.parseDate( "10-Jul-2040",
                                              new DateFormatsImpl() ) );

        // defining expectations on the mock object
        when( context.getRule() ).thenReturn( rule );
        when( context.getRuleDescr() ).thenReturn( ruleDescr );
        when( context.getPackageBuilder() ).thenReturn( new PackageBuilder() );

        // calling the build method
        RuleBuilder builder = new RuleBuilder();
        builder.buildAttributes( context );

        // check expectations
        verify( rule ).setNoLoop( true );
        verify( rule ).setAutoFocus( false );
        verify( rule ).setAgendaGroup( "my agenda" );
        verify( rule ).setActivationGroup( "my activation" );
        verify( rule ).setRuleFlowGroup( "mygroup" );
        verify( rule ).setLockOnActive( true );
        verify( rule ).setEnabled( EnabledBoolean.ENABLED_FALSE );
        verify( rule ).setTimer( new IntervalTimer( null,
                                                    null,
                                                    -1,
                                                    TimeUtils.parseTimeString( "60" ),
                                                    0 ) );
        verify( rule ).setCalendars( new String[]{"cal1"} );
        verify( rule ).setDateEffective( effective );
        verify( rule ).setDateExpires( expires );
    }

    @Test
    public void testBuildMetaAttributes() throws Exception {
        // creates mock objects
        final RuleBuildContext context = mock( RuleBuildContext.class );
        final Rule rule = mock( Rule.class );

        // creates input object
        final RuleDescr ruleDescr = new RuleDescr( "my rule" );
        ruleDescr.addAnnotation( "ruleId",
                                 "123" );
        ruleDescr.addAnnotation( "author",
                                 "Bob Doe" );
        ruleDescr.addAnnotation( "text",
                                 "\"It's a quoted\" string\"" );

        // creates expected results
        // defining expectations on the mock object
        when( context.getRule() ).thenReturn( rule );
        when( context.getRuleDescr() ).thenReturn( ruleDescr );
        when( context.getPackageBuilder() ).thenReturn( new PackageBuilder() );

        // calling the build method
        RuleBuilder builder = new RuleBuilder();
        builder.buildMetaAttributes( context );

        // check expectations
        verify( rule ).addMetaAttribute( "ruleId",
                                         "123" );
        verify( rule ).addMetaAttribute( "author",
                                         "Bob Doe" );
        verify( rule ).addMetaAttribute( "text",
                                         "\"It's a quoted\" string\"" );
    }

    @Test
    public void testBuildDurationExpression() throws Exception {
        // creates mock objects
        final RuleBuildContext context = mock( RuleBuildContext.class );
        final Rule rule = mock( Rule.class );

        // creates input object
        final RuleDescr ruleDescr = new RuleDescr( "my rule" );
        ruleDescr.addAttribute( new AttributeDescr( "duration",
                                                    "( 1h30m )" ) );
        ruleDescr.addAttribute( new AttributeDescr( "calendars",
                                                    "[\"cal1\", \"cal2\"]" ) );

        // defining expectations on the mock object
        when( context.getRule() ).thenReturn( rule );
        when( context.getRuleDescr() ).thenReturn( ruleDescr );

        // calling the build method
        RuleBuilder builder = new RuleBuilder();
        builder.buildAttributes( context );

        // check expectations
        verify( rule ).setTimer( new IntervalTimer( null,
                                                    null,
                                                    -1,
                                                    TimeUtils.parseTimeString( "1h30m" ),
                                                    0 ) );
        verify( rule ).setCalendars( new String[]{"cal1", "cal2"} );
    }

    @Test
    public void testBuildBigDecimalLiteralConstraint() throws Exception {
        final PackageDescr pkgDescr = new PackageDescr( "org.drools" );
        final RuleDescr ruleDescr = new RuleDescr( "Test Rule" );
        AndDescr andDescr = new AndDescr();
        PatternDescr patDescr = new PatternDescr( "java.math.BigDecimal",
                                                  "$bd" );
        ExprConstraintDescr fcd = new ExprConstraintDescr( "this == 10" );
        patDescr.addConstraint( fcd );
        andDescr.addDescr( patDescr );
        ruleDescr.setLhs( andDescr );
        ruleDescr.setConsequence( "" );
        pkgDescr.addRule( ruleDescr );

        final PackageBuilder pkgBuilder = new PackageBuilder();
        pkgBuilder.addPackage( pkgDescr );

        assertTrue( pkgBuilder.getErrors().toString(),
                           pkgBuilder.getErrors().isEmpty() );

        final Rule rule = pkgBuilder.getPackages()[0].getRule( "Test Rule" );
        final GroupElement and = rule.getLhs();
        final Pattern pat = (Pattern) and.getChildren().get( 0 );
        final LiteralConstraint fc = (LiteralConstraint) pat.getConstraints().get( 0 );
        assertTrue( "Wrong class. Expected java.math.BigDecimal. Found: " + fc.getField().getValue().getClass(),
                    fc.getField().getValue() instanceof BigDecimal );
    }

    @Test
    public void testInvalidDialect() throws Exception {
        final PackageDescr pkgDescr = new PackageDescr( "org.drools" );
        final RuleDescr ruleDescr = new RuleDescr( "Test Rule" );
        ruleDescr.addAttribute( new AttributeDescr( "dialect", "mvl" ) );
        ruleDescr.setConsequence( "" );
        pkgDescr.addRule( ruleDescr );

        final PackageBuilder pkgBuilder = new PackageBuilder();
        pkgBuilder.addPackage( pkgDescr );

        assertFalse( pkgBuilder.getErrors().toString(),
                     pkgBuilder.getErrors().isEmpty() );
    }    
    
    @Test
    public void testBuildBigIntegerLiteralConstraint() throws Exception {
        final PackageDescr pkgDescr = new PackageDescr( "org.drools" );
        final RuleDescr ruleDescr = new RuleDescr( "Test Rule" );
        AndDescr andDescr = new AndDescr();
        PatternDescr patDescr = new PatternDescr( "java.math.BigInteger",
                                                  "$bd" );
        ExprConstraintDescr fcd = new ExprConstraintDescr( "this==10" );
        patDescr.addConstraint( fcd );
        andDescr.addDescr( patDescr );
        ruleDescr.setLhs( andDescr );
        ruleDescr.setConsequence( "" );
        pkgDescr.addRule( ruleDescr );

        final PackageBuilder pkgBuilder = new PackageBuilder();
        pkgBuilder.addPackage( pkgDescr );

        assertTrue( pkgBuilder.getErrors().toString(),
                           pkgBuilder.getErrors().isEmpty() );

        final Rule rule = pkgBuilder.getPackages()[0].getRule( "Test Rule" );
        final GroupElement and = rule.getLhs();
        final Pattern pat = (Pattern) and.getChildren().get( 0 );
        final LiteralConstraint fc = (LiteralConstraint) pat.getConstraints().get( 0 );
        assertTrue( "Wrong class. Expected java.math.BigInteger. Found: " + fc.getField().getValue().getClass(),
                    fc.getField().getValue() instanceof BigInteger );
    }

}
