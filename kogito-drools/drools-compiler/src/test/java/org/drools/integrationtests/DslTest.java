package org.drools.integrationtests;

import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.drools.Cheese;
import org.drools.Person;
import org.drools.RuleBase;
import org.drools.RuleBaseConfiguration;
import org.drools.RuleBaseFactory;
import org.drools.compiler.PackageBuilder;
import org.drools.lang.Expander;
import org.drools.lang.dsl.DefaultExpanderResolver;
import org.drools.rule.Package;
import org.drools.WorkingMemory;

public class DslTest extends TestCase {
    protected RuleBase getRuleBase() throws Exception {

        return RuleBaseFactory.newRuleBase( RuleBase.RETEOO,
                                            null );
    }

    protected RuleBase getRuleBase(final RuleBaseConfiguration config) throws Exception {

        return RuleBaseFactory.newRuleBase( RuleBase.RETEOO,
                                            config );
    }


    public void testMultiLineTemplates() throws Exception {
        final Reader source = new InputStreamReader( getClass().getResourceAsStream( "rule_with_expander_multiline.dslr" ) );
        final Reader dsl = new InputStreamReader( getClass().getResourceAsStream( "test_dsl_multiline.dsl" ) );
        Expander ex =  new DefaultExpanderResolver(dsl).get("*", null);
        String r = ex.expand(source);
        assertEquals("when Car(color==\"Red\") then doSomething();", r.trim());
    }

    public void testWithExpanderDSL() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        final Reader source = new InputStreamReader( getClass().getResourceAsStream( "rule_with_expander_dsl.dslr" ) );
        final Reader dsl = new InputStreamReader( getClass().getResourceAsStream( "test_expander.dsl" ) );
        builder.addPackageFromDrl( source,
                                   dsl );

        // the compiled package
        final Package pkg = builder.getPackage();
        assertTrue( pkg.isValid() );
        assertEquals( null,
                      pkg.getErrorSummary() );
        // Check errors
        final String err = builder.getErrors().toString();
        assertEquals( "",
                      err );

        assertEquals( 0,
                      builder.getErrors().getErrors().length );

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase    = SerializationHelper.serializeObject(ruleBase);

        WorkingMemory wm = ruleBase.newStatefulSession();
        wm.insert( new Person( "Bob",
                               "http://foo.bar" ) );
        wm.insert( new Cheese( "stilton",
                               42 ) );

        final List messages = new ArrayList();
        wm.setGlobal( "messages",
                      messages );
//        wm  = SerializationHelper.serializeObject(wm);
        wm.fireAllRules();

        // should have fired
        assertEquals( 1,
                      messages.size() );

    }

    public void testWithExpanderMore() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        final Reader source = new InputStreamReader( getClass().getResourceAsStream( "rule_with_expander_dsl_more.dslr" ) );
        final Reader dsl = new InputStreamReader( getClass().getResourceAsStream( "test_expander.dsl" ) );
        builder.addPackageFromDrl( source,
                                   dsl );

        // the compiled package
        final Package pkg = builder.getPackage();
        assertTrue( pkg.isValid() );
        assertEquals( null,
                      pkg.getErrorSummary() );
        // Check errors
        final String err = builder.getErrors().toString();
        assertEquals( "",
                      err );
        assertEquals( 0,
                      builder.getErrors().getErrors().length );

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase    = SerializationHelper.serializeObject(ruleBase);

        WorkingMemory wm = ruleBase.newStatefulSession();
        wm.insert( new Person( "rage" ) );
        wm.insert( new Cheese( "cheddar",
                               15 ) );

        final List messages = new ArrayList();
        wm.setGlobal( "messages",
                      messages );
//        wm  = SerializationHelper.serializeObject(wm);
        wm.fireAllRules();

        // should have NONE, as both conditions should be false.
        assertEquals( 0,
                      messages.size() );

        wm.insert( new Person( "fire" ) );
        wm.fireAllRules();

        // still no firings
        assertEquals( 0,
                      messages.size() );

        wm.insert( new Cheese( "brie",
                               15 ) );

        wm.fireAllRules();

        // YOUR FIRED
        assertEquals( 1,
                      messages.size() );
    }

    public void testEmptyDSL() throws Exception {
        final String DSL = "# This is an empty dsl file.";
        final PackageBuilder builder = new PackageBuilder();
        final Reader drlReader = new InputStreamReader( getClass().getResourceAsStream( "literal_rule.drl" ) );
        final Reader dslReader = new StringReader( DSL );

        builder.addPackageFromDrl( drlReader,
                                   dslReader );
        Package pkg = builder.getPackage();

        pkg = SerializationHelper.serializeObject(pkg);
        assertNull( pkg );
    }

    public void testDSLWithIndividualConstraintMappings() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        final Reader source = new InputStreamReader( getClass().getResourceAsStream( "test_dslWithIndividualConstraints.dslr" ) );
        final Reader dsl = new InputStreamReader( getClass().getResourceAsStream( "test_dslWithIndividualConstraints.dsl" ) );
        builder.addPackageFromDrl( source,
                                   dsl );

        // the compiled package
        final Package pkg = builder.getPackage();
        assertTrue( pkg.getErrorSummary(),
                    pkg.isValid() );
        assertEquals( pkg.getErrorSummary(),
                      null,
                      pkg.getErrorSummary() );
        // Check errors
        assertEquals( 0,
                      builder.getErrors().getErrors().length );

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase    = SerializationHelper.serializeObject(ruleBase);

        WorkingMemory wm = ruleBase.newStatefulSession();
        List results = new ArrayList();
        wm.setGlobal( "results",
                      results );
        Cheese cheese = new Cheese( "stilton",
                                    42 );
        wm.insert( cheese );

//        wm  = SerializationHelper.serializeObject(wm);
        wm.fireAllRules();

        // should have fired
        assertEquals( 1,
                      results.size() );
        assertEquals( cheese,
                      results.get( 0 ) );

    }

}
