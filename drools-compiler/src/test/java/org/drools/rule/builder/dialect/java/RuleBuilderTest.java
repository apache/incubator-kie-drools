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
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.codehaus.jfdi.interpreter.ClassTypeResolver;
import org.codehaus.jfdi.interpreter.TypeResolver;
import org.drools.base.ClassFieldExtractorCache;
import org.drools.compiler.DrlParser;
import org.drools.compiler.PackageBuilderConfiguration;
import org.drools.lang.descr.AttributeDescr;
import org.drools.lang.descr.PackageDescr;
import org.drools.lang.descr.RuleDescr;
import org.drools.rule.GroupElement;
import org.drools.rule.Package;
import org.drools.rule.Rule;
import org.drools.rule.builder.RuleBuilder;
import org.drools.spi.FunctionResolver;

/**
 * @author etirelli
 *
 */
public class RuleBuilderTest extends TestCase {

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
    public void testBuild() {
        try {
            final DrlParser parser = new DrlParser();
            final PackageDescr pkgDescr = parser.parse( new InputStreamReader( getClass().getResourceAsStream( "nestedConditionalElements.drl" ) ) );

            // just checking there is no parsing errors
            Assert.assertFalse( parser.getErrors().toString(),
                                parser.hasErrors() );

            Package pkg = new Package( "org.drools" );

            RuleDescr ruleDescr = (RuleDescr) pkgDescr.getRules().get( 0 );
            final String ruleClassName = "RuleClassName.java";
            ruleDescr.setClassName( ruleClassName );

            TypeResolver typeResolver = new ClassTypeResolver( new ArrayList(),
                                                               this.getClass().getClassLoader() );
            // make an automatic import for the current package
            typeResolver.addImport( pkgDescr.getName() + ".*" );
            typeResolver.addImport( "java.lang.*" );

            final RuleBuilder builder = new RuleBuilder( typeResolver,
                                                         new ClassFieldExtractorCache(),
                                                         new JavaDialect(null, new PackageBuilderConfiguration() ) );

            builder.build( pkg,
                           ruleDescr );

            Assert.assertTrue( builder.getErrors().toString(),
                               builder.getErrors().isEmpty() );

            final Rule rule = builder.getRule();
            
            assertEquals( "There should be 2 rule level declarations", 2, rule.getDeclarations().length );
            
            // second GE should be a not
            GroupElement not = (GroupElement) rule.getLhs().getChildren().get( 1 );
            assertTrue( not.isNot() );
            // not has no outer declarations
            assertTrue( not.getOuterDeclarations().isEmpty() );
            assertEquals( 1, not.getInnerDeclarations().size() );
            assertTrue( not.getInnerDeclarations().keySet().contains( "$state" ) );
            
            // second not
            GroupElement not2 = (GroupElement) ((GroupElement) not.getChildren().get( 0 )).getChildren().get( 1 );
            assertTrue( not2.isNot() );
            // not has no outer declarations
            assertTrue( not2.getOuterDeclarations().isEmpty() );
            assertEquals( 1, not2.getInnerDeclarations().size() );
            assertTrue( not2.getInnerDeclarations().keySet().contains( "$likes" ) );
            
        } catch ( Exception e ) {
            e.printStackTrace();
            fail( "This test is not supposed to throw any exception: " + e.getMessage() );
        }

    }
    
    public void testBuildAttributes() throws Exception {
        RuleBuilder builder = new RuleBuilder(null,  null, new JavaDialect(null, new PackageBuilderConfiguration()) );
        Rule rule = new Rule("myrule");
        List attributes = new ArrayList();
        
        attributes.add( new AttributeDescr("no-loop", "true") );
        attributes.add( new AttributeDescr("enabled", "false") );
        attributes.add( new AttributeDescr("ruleflow-group", "mygroup") );
        builder.setAttributes( rule, attributes );
        
        assertTrue(rule.getNoLoop());
        assertFalse(rule.isEffective());
        assertEquals("mygroup", rule.getRuleFlowGroup() );
        
        attributes = new ArrayList();
        attributes.add(new AttributeDescr("date-effective", "10-Jul-1974"));
        attributes.add(new AttributeDescr("date-expires", "10-Jul-2040") );
        
        rule = new Rule("myrule");
        
        builder.setAttributes( rule, attributes );
        
        Field eff = rule.getClass().getDeclaredField( "dateEffective" );
        eff.setAccessible( true );       
        Calendar effectiveDate = (Calendar) eff.get( rule );
        assertNotNull(effectiveDate);
        
        assertEquals(1974, effectiveDate.get( Calendar.YEAR ));
        
        Field exp = rule.getClass().getDeclaredField( "dateExpires" );
        exp.setAccessible( true );
        Calendar expiryDate = (Calendar) exp.get( rule );
        
        assertEquals(2040, expiryDate.get(Calendar.YEAR));
        
        assertNotNull(expiryDate);
        
    }

}
