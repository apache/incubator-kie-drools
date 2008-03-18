package org.drools.integrationtests;

import junit.framework.Assert;
import junit.framework.TestCase;
import org.drools.Cheese;
import org.drools.Cheesery;
import org.drools.FactHandle;
import org.drools.Person;
import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.StatefulSession;
import org.drools.WorkingMemory;
import org.drools.compiler.DrlParser;
import org.drools.compiler.DroolsParserException;
import org.drools.compiler.PackageBuilder;
import org.drools.lang.descr.PackageDescr;
import org.drools.rule.Package;
import org.drools.util.DateUtils;
import org.mvel.MVEL;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Serializable;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MVELTest extends TestCase {
    public void testHelloWorld() throws Exception {
        // read in the source
        final Reader reader = new InputStreamReader( getClass().getResourceAsStream( "test_mvel.drl" ) );
        RuleBase ruleBase = loadRuleBase( reader );

        final WorkingMemory workingMemory = ruleBase.newStatefulSession();

        final List list = new ArrayList();
        workingMemory.setGlobal( "list",
                                 list );

        final List list2 = new ArrayList();
        workingMemory.setGlobal( "list2",
                                 list2 );


        Cheese c = new Cheese("stilton", 10) ;
        workingMemory.insert( c);
        workingMemory.fireAllRules();
        assertEquals( 2, list.size() );
        assertEquals( new Integer(30), list.get(0));
        assertEquals( new Integer(22), list.get(1));

        assertEquals( "hello world", list2.get(0));

        Date dt = DateUtils.parseDate( "10-Jul-1974" );
        assertEquals(dt, c.getUsedBy());
    }

    public void testLocalVariableMVELConsequence() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_LocalVariableMVELConsequence.drl" ) ) );
        final Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase    = SerializationHelper.serializeObject(ruleBase);

        final WorkingMemory workingMemory = ruleBase.newStatefulSession();

        final List list = new ArrayList();
        workingMemory.setGlobal( "results",
                                 list );

        workingMemory.insert( new Person( "bob", "stilton" ) );
        workingMemory.insert( new Person( "mark", "brie" ) );

        try {
            workingMemory.fireAllRules();

            assertEquals( "should have fired twice",
                          2,
                          list.size() );

        } catch (Exception e) {
            e.printStackTrace();
            fail( "Should not raise any exception");
        }

    }

    public void testDuplicateLocalVariableMVELConsequence() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_DuplicateLocalVariableMVELConsequence.drl" ) ) );

        try {
            final Package pkg = builder.getPackage();

            RuleBase ruleBase = getRuleBase();
            ruleBase.addPackage( pkg );

            ruleBase    = SerializationHelper.serializeObject(ruleBase);
            fail( "Should have raised exception because of the duplicate variable definition");
        } catch (Exception e) {
            // success
        }

    }
    
    public void testMVELSoundex() throws Exception {

        // read in the source
        final Reader reader = new InputStreamReader( getClass().getResourceAsStream( "MVEL_soundex.drl" ) );
        RuleBase ruleBase = loadRuleBase( reader );

        ruleBase = SerializationHelper.serializeObject( ruleBase );
        WorkingMemory workingMemory = ruleBase.newStatefulSession();

        workingMemory = SerializationHelper.serializeObject( workingMemory );
        Cheese c = new Cheese( "fubar",
                               2 );

        workingMemory.insert( c );
        workingMemory.fireAllRules();
        assertEquals( 42,
                      c.getPrice() );
    }
    
    public void testMVELConsequenceWithMapsAndArrays() throws Exception {
        String rule = "package org.test;\n";
        rule += "import java.util.ArrayList\n";
        rule += "import java.util.HashMap\n";
        rule += "global java.util.List list\n";
        rule += "rule \"Test Rule\"\n";
        rule += "    dialect \"mvel\"";
        rule += "when\n";
        rule += "then\n";
        rule += "    m = new HashMap();\n";
        rule += "    l = new ArrayList();\n";
        rule += "    l.add(\"first\");\n";
        rule += "    m.put(\"content\", l);\n";
        rule += "    System.out.println(m[\"content\"][0]);\n";
        rule += "    list.add(m[\"content\"][0]);\n";
        rule += "end";

        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new StringReader( rule ) );
        final Package pkg = builder.getPackage();

        final RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        final StatefulSession session = ruleBase.newStatefulSession();
        List list = new ArrayList();
        session.setGlobal( "list",
                           list );
        session.fireAllRules();

        assertEquals( 1,
                      list.size() );
        assertEquals( "first",
                      list.get( 0 ) );
    }

    /* @see JBRULES-1484 */
    public void testMVELDynamicImports() throws Exception {
        String rule = "package org.xxx;\n";

        rule += "import org.drools.*\n";

        rule += "global java.util.List list\n";
        rule += "rule \"Test Rule\"\n";
        rule += "    dialect \"mvel\"";
        rule += "when\n";
        rule += "then\n";
        rule += "    p = new Person( \"diablo\", new Cheese (\"cheddar\") );";
        rule += "    c = new Cheese( \"y\" );";
        rule += "    list.add( p );\n";
        rule += "end";

        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new StringReader( rule ) );
        final Package pkg = builder.getPackage();

        final RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        final StatefulSession session = ruleBase.newStatefulSession();
        List list = new ArrayList();
        session.setGlobal( "list",
                           list );
        session.fireAllRules();

        assertEquals( 1,
                      list.size() );

        Person p = new Person( "diablo",
                               new Cheese( "cheddar" ) );

        assertEquals( p,
                      list.get( 0 ) );
    }
    
    public void testMatchesMVEL() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_MatchesMVEL.drl" ) ) );
        final Package pkg = builder.getPackage();

        final RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        final StatefulSession session = ruleBase.newStatefulSession();

        final List results = new ArrayList();
        session.setGlobal( "results",
                           results );

        Map map = new HashMap();
        map.put( "content",
                 "hello ;=" );
        session.insert( map );

        session.fireAllRules();

        assertEquals( 1,
                      results.size() );
    }
    
    public void testNPEOnMVELAlphaPredicates() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_NPEOnMVELPredicate.drl" ) ) );
        final Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase = SerializationHelper.serializeObject( ruleBase );
        final StatefulSession session = ruleBase.newStatefulSession();

        final List list = new ArrayList();
        session.setGlobal( "results",
                           list );

        Cheese cheese = new Cheese( "stilton",
                                    10 );
        Cheesery cheesery = new Cheesery();
        cheesery.addCheese( cheese );
        Person bob = new Person( "bob",
                                 "stilton" );
        Cheese cheese2 = new Cheese();
        bob.setCheese( cheese2 );

        FactHandle p = session.insert( bob );
        FactHandle c = session.insert( cheesery );

        session.fireAllRules();

        assertEquals( "should not have fired",
                      0,
                      list.size() );

        cheese2.setType( "stilton" );

        session.update( p,
                        bob );
        session.fireAllRules();

        assertEquals( 1,
                      list.size() );

    }
    
    
    public Object compiledExecute(String ex) {
        Serializable compiled = MVEL.compileExpression(ex);
        return MVEL.executeExpression(compiled, new Object(), new HashMap());
    }

    private RuleBase loadRuleBase(final Reader reader) throws IOException,
                                                      DroolsParserException,
                                                      Exception {
        final DrlParser parser = new DrlParser();
        final PackageDescr packageDescr = parser.parse( reader );
        if ( parser.hasErrors() ) {
            Assert.fail( "Error messages in parser, need to sort this our (or else collect error messages)" );
        }
        // pre build the package
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackage( packageDescr );

        System.out.println( builder.getErrors() );

        final Package pkg = builder.getPackage();

        // add the package to a rulebase
        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase    = SerializationHelper.serializeObject(ruleBase);
        // load up the rulebase
        return ruleBase;
    }

    protected RuleBase getRuleBase() throws Exception {

        return RuleBaseFactory.newRuleBase( RuleBase.RETEOO,
                                            null );
    }
}
