package org.drools.persister;

import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import junit.framework.TestCase;

import org.drools.Cell;
import org.drools.Cheese;
import org.drools.FactA;
import org.drools.FactB;
import org.drools.FactC;
import org.drools.FactHandle;
import org.drools.Person;
import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.StatefulSession;
import org.drools.WorkingMemory;
import org.drools.audit.WorkingMemoryInMemoryLogger;
import org.drools.base.ClassObjectType;
import org.drools.common.BaseNode;
import org.drools.common.InternalRuleBase;
import org.drools.common.InternalWorkingMemory;
import org.drools.compiler.PackageBuilder;
import org.drools.integrationtests.SerializationHelper;
import org.drools.marshalling.Marshaller;
import org.drools.marshalling.RuleBaseNodes;
import org.drools.reteoo.ObjectTypeNode;
import org.drools.reteoo.ReteooRuleBase;
import org.drools.reteoo.RuleTerminalNode;
import org.drools.rule.Package;
import org.drools.spi.ActivationGroup;
import org.drools.spi.AgendaGroup;

import static org.drools.integrationtests.SerializationHelper.*;

public class WorkingMemorySerialisationTest extends TestCase {

    public void testEmptyRule() throws Exception {
        String rule = "package org.test;\n";
        rule += "global java.util.List list\n";
        rule += "rule \"Rule 1\"\n";
        rule += "when\n";
        rule += "then\n";
        rule += "    list.add( \"fired\" );\n";
        rule += "end";

        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new StringReader( rule ) );
        assertTrue( builder.getErrors().isEmpty() );
        final Package pkg = builder.getPackage();

        final RuleBase ruleBase = RuleBaseFactory.newRuleBase();
        ruleBase.addPackage( pkg );

        // Make sure the rete node map is created correctly
        Map<Integer, BaseNode> nodes = RuleBaseNodes.getNodeMap( (InternalRuleBase) ruleBase );
        assertEquals( 2,
                      nodes.size() );
        assertEquals( "InitialFact",
                      ((ClassObjectType) ((ObjectTypeNode) nodes.get( 3 )).getObjectType()).getClassType().getSimpleName() );
        assertEquals( "Rule 1",
                      ((RuleTerminalNode) nodes.get( 5 )).getRule().getName() );

        StatefulSession session = ruleBase.newStatefulSession();

        List list = new ArrayList();
        session.setGlobal( "list",
                           list );

        session = getSerialisedStatefulSession( session );

        session.fireAllRules();

        session = getSerialisedStatefulSession( session );

        assertEquals( 1,
                      ((List) session.getGlobal( "list" )).size() );
        assertEquals( "fired",
                      ((List) session.getGlobal( "list" )).get( 0 ) );
    }

    public void testDynamicEmptyRule() throws Exception {
        String rule1 = "package org.test;\n";
        rule1 += "global java.util.List list\n";
        rule1 += "rule \"Rule 1\"\n";
        rule1 += "when\n";
        rule1 += "then\n";
        rule1 += "    list.add( \"fired1\" );\n";
        rule1 += "end";

        String rule2 = "package org.test;\n";
        rule2 += "global java.util.List list\n";
        rule2 += "rule \"Rule 2\"\n";
        rule2 += "when\n";
        rule2 += "then\n";
        rule2 += "    list.add( \"fired2\" );\n";
        rule2 += "end";

        PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new StringReader( rule1 ) );
        Package pkg = builder.getPackage();

        final RuleBase ruleBase = RuleBaseFactory.newRuleBase();
        ruleBase.addPackage( pkg );

        // Make sure the rete node map is created correctly
        Map<Integer, BaseNode> nodes = RuleBaseNodes.getNodeMap( (InternalRuleBase) ruleBase );
        assertEquals( 2,
                      nodes.size() );
        assertEquals( "InitialFact",
                      ((ClassObjectType) ((ObjectTypeNode) nodes.get( 3 )).getObjectType()).getClassType().getSimpleName() );
        assertEquals( "Rule 1",
                      ((RuleTerminalNode) nodes.get( 5 )).getRule().getName() );

        StatefulSession session = ruleBase.newStatefulSession();

        List list = new ArrayList();
        session.setGlobal( "list",
                           list );
        StatefulSession session1 = getSerialisedStatefulSession( session );
        session1.fireAllRules();

        assertEquals( 1,
                      ((List) session1.getGlobal( "list" )).size() );

        WorkingMemory session2 = getSerialisedStatefulSession( session1,
                                                               true );

        session.dispose();
        session1.dispose();

        builder = new PackageBuilder();
        builder.addPackageFromDrl( new StringReader( rule2 ) );
        pkg = builder.getPackage();

        ruleBase.addPackage( pkg );

        assertEquals( 2,
                      ((List) session2.getGlobal( "list" )).size() );
        assertEquals( "fired1",
                      ((List) session2.getGlobal( "list" )).get( 0 ) );
        assertEquals( "fired2",
                      ((List) session2.getGlobal( "list" )).get( 1 ) );
    }

    public void testSinglePattern() throws Exception {
        String rule = "package org.test;\n";
        rule += "import org.drools.Person\n";
        rule += "global java.util.List list\n";
        rule += "rule \"Rule 1\"\n";
        rule += "when\n";
        rule += "    $p : Person( ) \n";
        rule += "then\n";
        rule += "    list.add( $p );\n";
        rule += "end";

        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new StringReader( rule ) );
        final Package pkg = builder.getPackage();

        final RuleBase ruleBase = RuleBaseFactory.newRuleBase();
        ruleBase.addPackage( pkg );

        // Make sure the rete node map is created correctly
        Map<Integer, BaseNode> nodes = RuleBaseNodes.getNodeMap( (InternalRuleBase) ruleBase );
        assertEquals( 2,
                      nodes.size() );
        assertEquals( "Person",
                      ((ClassObjectType) ((ObjectTypeNode) nodes.get( 3 )).getObjectType()).getClassType().getSimpleName() );
        assertEquals( "Rule 1",
                      ((RuleTerminalNode) nodes.get( 5 )).getRule().getName() );

        StatefulSession session = ruleBase.newStatefulSession();

        List list = new ArrayList();
        session.setGlobal( "list",
                           list );

        Person p = new Person( "bobba fet",
                               32 );
        session.insert( p );

        session = getSerialisedStatefulSession( session );
        session.fireAllRules();

        assertEquals( 1,
                      ((List) session.getGlobal( "list" )).size() );
        assertEquals( p,
                      ((List) session.getGlobal( "list" )).get( 0 ) );
    }

    public void testSingleRuleSingleJoinNodePattern() throws Exception {
        String rule = "package org.test;\n";
        rule += "import org.drools.Person\n";
        rule += "import org.drools.Cheese\n";
        rule += "global java.util.List list\n";
        rule += "rule \"Rule 1\"\n";
        rule += "when\n";
        rule += "    $c : Cheese( ) \n";
        rule += "    $p : Person( cheese == $c ) \n";
        rule += "then\n";
        rule += "    list.add( $p );\n";
        rule += "end";

        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new StringReader( rule ) );
        final Package pkg = builder.getPackage();

        final RuleBase ruleBase = RuleBaseFactory.newRuleBase();
        ruleBase.addPackage( pkg );

        // Make sure the rete node map is created correctly
        Map<Integer, BaseNode> nodes = RuleBaseNodes.getNodeMap( (InternalRuleBase) ruleBase );
        assertEquals( 4,
                      nodes.size() );
        assertEquals( "Cheese",
                      ((ClassObjectType) ((ObjectTypeNode) nodes.get( 3 )).getObjectType()).getClassType().getSimpleName() );
        assertEquals( "Person",
                      ((ClassObjectType) ((ObjectTypeNode) nodes.get( 6 )).getObjectType()).getClassType().getSimpleName() );
        assertEquals( "JoinNode",
                      nodes.get( 7 ).getClass().getSimpleName() );
        assertEquals( "Rule 1",
                      ((RuleTerminalNode) nodes.get( 8 )).getRule().getName() );

        StatefulSession session = ruleBase.newStatefulSession();

        List list = new ArrayList();
        session.setGlobal( "list",
                           list );

        Cheese stilton = new Cheese( "stilton",
                                     25 );
        Cheese brie = new Cheese( "brie",
                                  49 );
        Person bobba = new Person( "bobba fet",
                                   32 );
        bobba.setCheese( stilton );

        Person vadar = new Person( "darth vadar",
                                   32 );

        session.insert( stilton );
        session.insert( bobba );
        session.insert( vadar );
        session.insert( brie );

        session = getSerialisedStatefulSession( session );

        session.fireAllRules();

        assertEquals( 1,
                      ((List) session.getGlobal( "list" )).size() );
        assertEquals( bobba,
                      ((List) session.getGlobal( "list" )).get( 0 ) );

        Person c3po = new Person( "c3p0",
                                  32 );
        c3po.setCheese( stilton );
        session.insert( c3po );

        session = getSerialisedStatefulSession( session );

        session.fireAllRules();

        assertEquals( 2,
                      ((List) session.getGlobal( "list" )).size() );
        assertEquals( c3po,
                      ((List) session.getGlobal( "list" )).get( 1 ) );

        Person r2d2 = new Person( "r2d2",
                                  32 );
        r2d2.setCheese( brie );
        session.insert( r2d2 );

        System.out.println( "\n\njointpattern" );
        session = getSerialisedStatefulSession( session );

        session.fireAllRules();

        assertEquals( 3,
                      ((List) session.getGlobal( "list" )).size() );
        assertEquals( r2d2,
                      ((List) session.getGlobal( "list" )).get( 2 ) );
    }

    public void testMultiRuleMultiJoinNodePatternsWithHalt() throws Exception {
        String rule1 = "package org.test;\n";
        rule1 += "import org.drools.Person\n";
        rule1 += "import org.drools.Cheese\n";
        rule1 += "global java.util.List list\n";
        rule1 += "rule \"Rule 1\"\n";
        rule1 += "when\n";
        rule1 += "    $c : Cheese( ) \n";
        rule1 += "    $p : Person( cheese == $c ) \n";
        rule1 += "then\n";
        rule1 += "    list.add( $p );\n";
        rule1 += "end";

        String rule2 = "package org.test;\n";
        rule2 += "import org.drools.Person\n";
        rule2 += "import org.drools.Cheese\n";
        rule2 += "import org.drools.Cell\n";
        rule2 += "global java.util.List list\n";
        rule2 += "rule \"Rule 2\"\n";
        rule2 += "when\n";
        rule2 += "    $c : Cheese( ) \n";
        rule2 += "    $p : Person( cheese == $c ) \n";
        rule2 += "    Cell( value == $p.age ) \n";
        rule2 += "then\n";
        rule2 += "    list.add( $p );\n";
        rule2 += "end";

        String rule3 = "package org.test;\n";
        rule3 += "import org.drools.FactA\n";
        rule3 += "import org.drools.FactB\n";
        rule3 += "import org.drools.FactC\n";
        rule3 += "import org.drools.Person\n";
        rule3 += "global java.util.List list\n";
        rule3 += "rule \"Rule 3\"\n";
        rule3 += "when\n";
        rule3 += "    $a : FactA( field2 > 10 ) \n";
        rule3 += "    $b : FactB( f2 >= $a.field2 ) \n";
        rule3 += "    $p : Person( name == \"darth vadar\" ) \n";
        rule3 += "    $c : FactC( f2 >= $b.f2 ) \n";
        rule3 += "then\n";
        rule3 += "    list.add( $c );\n";
        rule3 += "    drools.halt();\n";
        rule3 += "end";

        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new StringReader( rule1 ) );
        builder.addPackageFromDrl( new StringReader( rule2 ) );
        builder.addPackageFromDrl( new StringReader( rule3 ) );
        final Package pkg = builder.getPackage();

        final RuleBase ruleBase = RuleBaseFactory.newRuleBase();
        ruleBase.addPackage( pkg );

        StatefulSession session = ruleBase.newStatefulSession();

        List list = new ArrayList();
        session.setGlobal( "list",
                           list );

        Cheese stilton = new Cheese( "stilton",
                                     25 );
        Cheese brie = new Cheese( "brie",
                                  49 );
        Person bobba = new Person( "bobba fet",
                                   30 );
        bobba.setCheese( stilton );
        Person vadar = new Person( "darth vadar",
                                   38 );
        Person c3po = new Person( "c3p0",
                                  17 );
        c3po.setCheese( stilton );
        Person r2d2 = new Person( "r2d2",
                                  58 );
        r2d2.setCheese( brie );

        session.insert( stilton );
        session.insert( bobba );
        session.insert( vadar );
        session.insert( brie );
        session.insert( c3po );
        session.insert( r2d2 );

        session = getSerialisedStatefulSession( session );
        session.fireAllRules();

        assertEquals( 3,
                      ((List) session.getGlobal( "list" )).size() );
        assertEquals( bobba,
                      ((List) session.getGlobal( "list" )).get( 2 ) );
        assertEquals( c3po,
                      ((List) session.getGlobal( "list" )).get( 1 ) );
        assertEquals( r2d2,
                      ((List) session.getGlobal( "list" )).get( 0 ) );

        session = getSerialisedStatefulSession( session );

        session.insert( new Cell( 30 ) );
        session.insert( new Cell( 58 ) );

        session = getSerialisedStatefulSession( session );

        session.fireAllRules();

        assertEquals( 5,
                      ((List) session.getGlobal( "list" )).size() );
        assertEquals( r2d2,
                      ((List) session.getGlobal( "list" )).get( 3 ) );
        assertEquals( bobba,
                      ((List) session.getGlobal( "list" )).get( 4 ) );

        session = getSerialisedStatefulSession( session );

        session.insert( new FactA( 15 ) );
        session.insert( new FactB( 20 ) );
        session.insert( new FactC( 27 ) );
        session.insert( new FactC( 52 ) );

        session = getSerialisedStatefulSession( session );

        session.fireAllRules();

        assertEquals( 6,
                      ((List) session.getGlobal( "list" )).size() );
        assertEquals( new FactC( 52 ),
                      ((List) session.getGlobal( "list" )).get( 5 ) );

        session = getSerialisedStatefulSession( session );

        session.fireAllRules();

        assertEquals( 7,
                      ((List) session.getGlobal( "list" )).size() );
        assertEquals( new FactC( 27 ),
                      ((List) session.getGlobal( "list" )).get( 6 ) );
    }

    public void testNot() throws Exception {
        String header = "package org.drools.test;\n";
        header += "import java.util.List;\n";
        header += "import org.drools.Person\n";
        header += "import org.drools.Cheese\n";
        header += "global java.util.List list;\n";

        String rule1 = "rule \"not rule test\"\n";
        rule1 += "salience 10\n";
        rule1 += "when\n";
        rule1 += "    Person()\n";
        rule1 += "    not Cheese( price >= 5 )\n";
        rule1 += "then\n";
        rule1 += "    list.add( new Integer( 5 ) );\n";
        rule1 += "end\n";     

        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new StringReader( header + rule1 ) );
        final Package pkg = builder.getPackage();

        final RuleBase ruleBase = RuleBaseFactory.newRuleBase();
        ruleBase.addPackage( pkg );

        StatefulSession session = ruleBase.newStatefulSession();
        List list = new ArrayList();
        session.setGlobal( "list",
                           list );

        // add a person, no cheese
        session = getSerialisedStatefulSession( session );
        Person bobba = new Person( "bobba fet",
                                   50 );
        session.insert( bobba );
        session = getSerialisedStatefulSession( session );
        session.fireAllRules();
        assertEquals( 1,
                      list.size() );

        // add another person, no cheese
        session = getSerialisedStatefulSession( session );
        Person darth = new Person( "darth vadar",
                                   200 );
        session.insert( darth );
        session = getSerialisedStatefulSession( session );
        session.fireAllRules();
        assertEquals( 2,
                      list.size() );

        // add cheese 
        session = getSerialisedStatefulSession( session );
        Cheese stilton = new Cheese( "stilton",
                                     5 );
        session.insert( stilton );
        session = getSerialisedStatefulSession( session );
        session.fireAllRules();
        assertEquals( 2,
                      list.size() );

        // remove cheese
        session = getSerialisedStatefulSession( session );
        session.retract( session.getFactHandle( stilton ) );
        session = getSerialisedStatefulSession( session );
        session.fireAllRules();
        assertEquals( 4,
                      list.size() );

        // put 2 cheeses back in
        session = getSerialisedStatefulSession( session );
        session.insert( stilton );
        session = getSerialisedStatefulSession( session );
        Cheese brie = new Cheese( "brie",
                                  18 );
        session.insert( brie );
        session.fireAllRules();
        assertEquals( 4,
                      list.size() );

        // now remove a cheese, should be no change
        session.retract( session.getFactHandle( stilton ) );
        session = getSerialisedStatefulSession( session );
        session.fireAllRules();
        assertEquals( 4,
                      list.size() );
        
        // now remove a person, should be no change
        session.retract( session.getFactHandle( bobba ) );
        session = getSerialisedStatefulSession( session );
        session.fireAllRules();
        assertEquals( 4,
                      list.size() );     
        
        //removal remaining cheese, should increase by one, as one person left
        session.retract( session.getFactHandle( brie ) );
        session = getSerialisedStatefulSession( session );
        session.fireAllRules();
        assertEquals( 5,
                      list.size() );        
    }
    
    public void testExists() throws Exception {
        String header = "package org.drools.test;\n";
        header += "import java.util.List;\n";
        header += "import org.drools.Person\n";
        header += "import org.drools.Cheese\n";
        header += "global java.util.List list;\n";

        String rule1 = "rule \"not rule test\"\n";
        rule1 += "salience 10\n";
        rule1 += "when\n";
        rule1 += "    Person()\n";
        rule1 += "    exists Cheese( price >= 5 )\n";
        rule1 += "then\n";
        rule1 += "    list.add( new Integer( 5 ) );\n";
        rule1 += "end\n";     

        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new StringReader( header + rule1 ) );
        final Package pkg = builder.getPackage();

        final RuleBase ruleBase = RuleBaseFactory.newRuleBase();
        ruleBase.addPackage( pkg );

        StatefulSession session = ruleBase.newStatefulSession();
        List list = new ArrayList();
        session.setGlobal( "list",
                           list );

        // add a person, no cheese
        session = getSerialisedStatefulSession( session );
        Person bobba = new Person( "bobba fet",
                                   50 );
        session.insert( bobba );
        session = getSerialisedStatefulSession( session );
        session.fireAllRules();
        assertEquals( 0,
                      list.size() );

        // add another person, no cheese
        session = getSerialisedStatefulSession( session );
        Person darth = new Person( "darth vadar",
                                   200 );
        session.insert( darth );
        session = getSerialisedStatefulSession( session );
        session.fireAllRules();
        assertEquals( 0,
                      list.size() );

        // add cheese 
        session = getSerialisedStatefulSession( session );
        Cheese stilton = new Cheese( "stilton",
                                     5 );
        session.insert( stilton );
        session = getSerialisedStatefulSession( session );
        session.fireAllRules();
        assertEquals( 2,
                      list.size() );

        // remove cheese
        session = getSerialisedStatefulSession( session );
        session.retract( session.getFactHandle( stilton ) );
        session = getSerialisedStatefulSession( session );
        session.fireAllRules();
        assertEquals( 2,
                      list.size() );

        // put 2 cheeses back in
        session = getSerialisedStatefulSession( session );
        session.insert( stilton );
        session = getSerialisedStatefulSession( session );
        Cheese brie = new Cheese( "brie",
                                  18 );
        session.insert( brie );
        session.fireAllRules();
        assertEquals( 4,
                      list.size() );

        // now remove a cheese, should be no change
        session.retract( session.getFactHandle( stilton ) );
        session = getSerialisedStatefulSession( session );
        session.fireAllRules();
        assertEquals( 4,
                      list.size() );
        
        // now remove a person, should be no change
        session.retract( session.getFactHandle( bobba ) );
        session = getSerialisedStatefulSession( session );
        session.fireAllRules();
        assertEquals( 4,
                      list.size() );     
        
        //removal remaining cheese, no
        session.retract( session.getFactHandle( brie ) );
        session = getSerialisedStatefulSession( session );
        session.fireAllRules();
        assertEquals( 4,
                      list.size() );    
        
        // put one cheese back in, with one person should increase by one
        session = getSerialisedStatefulSession( session );
        session.insert( stilton );
        session.fireAllRules();
        assertEquals( 5,
                      list.size() );        
    }    

    public void testTruthMaintenance() throws Exception {
        String header = "package org.drools.test;\n";
        header += "import java.util.List;\n";
        header += "import org.drools.Person\n";
        header += "import org.drools.Cheese\n";
        header += "global Cheese cheese;\n";
        header += "global Person person;\n";
        header += "global java.util.List list;\n";

        String rule1 = "rule \"not person then cheese\"\n";
        rule1 += "when \n";
        rule1 += "    not Person() \n";
        rule1 += "then \n";
        rule1 += "    if (list.size() < 3) { \n";
        rule1 += "        list.add(new Integer(0)); \n";
        rule1 += "        insertLogical( cheese ); \n" + "    }\n";
        rule1 += "    drools.halt();\n" + "end\n";

        String rule2 = "rule \"if cheese then person\"\n";
        rule2 += "when\n";
        rule2 += "    Cheese()\n";
        rule2 += "then\n";
        rule2 += "    if (list.size() < 3) {\n";
        rule2 += "        list.add(new Integer(0));\n";
        rule2 += "        insertLogical( person );\n";
        rule2 += "    }\n" + "    drools.halt();\n";
        rule2 += "end\n";

        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new StringReader( header + rule1 ) );
        builder.addPackageFromDrl( new StringReader( header + rule2 ) );
        final Package pkg = builder.getPackage();

        final RuleBase ruleBase = RuleBaseFactory.newRuleBase();
        ruleBase.addPackage( pkg );

        StatefulSession session = ruleBase.newStatefulSession();

        final List list = new ArrayList();

        final Person person = new Person( "person" );
        final Cheese cheese = new Cheese( "cheese",
                                          0 );
        session.setGlobal( "cheese",
                           cheese );
        session.setGlobal( "person",
                           person );
        session.setGlobal( "list",
                           list );
        session.fireAllRules();
        assertEquals( 1,
                      list.size() );

        session = getSerialisedStatefulSession( session );
        session.fireAllRules();
        assertEquals( 2,
                      list.size() );

        session = getSerialisedStatefulSession( session );
        session.fireAllRules();
        assertEquals( 3,
                      list.size() );

        // should not grow any further
        session = getSerialisedStatefulSession( session );
        session.fireAllRules();
        assertEquals( 3,
                      list.size() );
    }

    public void testActivationGroups() throws Exception {
        String rule1 = "package org.test;\n";
        rule1 += "import org.drools.Cheese\n";
        rule1 += "global java.util.List list\n";
        rule1 += "rule \"Rule 1\"\n";
        rule1 += "    activation-group \"activation-group-1\"\n";
        rule1 += "when\n";
        rule1 += "    $c : Cheese( ) \n";
        rule1 += "then\n";
        rule1 += "    list.add( \"rule1\" );\n";
        rule1 += "    drools.halt();\n";
        rule1 += "end";

        String rule2 = "package org.test;\n";
        rule2 += "import org.drools.Cheese\n";
        rule2 += "global java.util.List list\n";
        rule2 += "rule \"Rule 2\"\n";
        rule2 += "    salience 10\n";
        rule2 += "    activation-group \"activation-group-1\"\n";
        rule2 += "when\n";
        rule2 += "    $c : Cheese( ) \n";
        rule2 += "then\n";
        rule2 += "    list.add( \"rule2\" );\n";
        rule2 += "    drools.halt();\n";
        rule2 += "end";

        String rule3 = "package org.test;\n";
        rule3 += "import org.drools.Cheese\n";
        rule3 += "global java.util.List list\n";
        rule3 += "rule \"Rule 3\"\n";
        rule3 += "    activation-group \"activation-group-1\"\n";
        rule3 += "when\n";
        rule3 += "    $c : Cheese( ) \n";
        rule3 += "then\n";
        rule3 += "    list.add( \"rule3\" );\n";
        rule3 += "    drools.halt();\n";
        rule3 += "end";

        String rule4 = "package org.test;\n";
        rule4 += "import org.drools.Cheese\n";
        rule4 += "global java.util.List list\n";
        rule4 += "rule \"Rule 4\"\n";
        rule4 += "    activation-group \"activation-group-2\"\n";
        rule4 += "when\n";
        rule4 += "    $c : Cheese( ) \n";
        rule4 += "then\n";
        rule4 += "    list.add( \"rule4\" );\n";
        rule4 += "    drools.halt();\n";
        rule4 += "end";

        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new StringReader( rule1 ) );
        builder.addPackageFromDrl( new StringReader( rule2 ) );
        builder.addPackageFromDrl( new StringReader( rule3 ) );
        builder.addPackageFromDrl( new StringReader( rule4 ) );

        final Package pkg = builder.getPackage();

        RuleBase ruleBase = RuleBaseFactory.newRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase = SerializationHelper.serializeObject( ruleBase );
        StatefulSession session = ruleBase.newStatefulSession();
        session = getSerialisedStatefulSession( session );

        final List list = new ArrayList();
        session.setGlobal( "list",
                           list );

        final Cheese brie = new Cheese( "brie",
                                        12 );
        session.insert( brie );

        session = getSerialisedStatefulSession( session );
        session.fireAllRules();

        session = getSerialisedStatefulSession( session );
        session.fireAllRules();

        assertEquals( 2,
                      list.size() );
        assertEquals( "rule2",
                      list.get( 0 ) );
        assertEquals( "rule4",
                      list.get( 1 ) );
    }

    public void testAgendaGroups() throws Exception {
        String rule1 = "package org.test;\n";
        rule1 += "import org.drools.Cheese\n";
        rule1 += "global java.util.List list\n";
        rule1 += "rule \"Rule 1\"\n";
        rule1 += "    agenda-group \"agenda-group-1\"\n";
        rule1 += "when\n";
        rule1 += "    $c : Cheese( ) \n";
        rule1 += "then\n";
        rule1 += "    list.add( \"rule1\" );\n";
        rule1 += "    drools.halt();\n";
        rule1 += "end";

        String rule2 = "package org.test;\n";
        rule2 += "import org.drools.Cheese\n";
        rule2 += "global java.util.List list\n";
        rule2 += "rule \"Rule 2\"\n";
        rule2 += "    salience 10\n";
        rule2 += "    agenda-group \"agenda-group-1\"\n";
        rule2 += "when\n";
        rule2 += "    $c : Cheese( ) \n";
        rule2 += "then\n";
        rule2 += "    list.add( \"rule2\" );\n";
        rule2 += "    drools.halt();\n";
        rule2 += "end";

        String rule3 = "package org.test;\n";
        rule3 += "import org.drools.Cheese\n";
        rule3 += "global java.util.List list\n";
        rule3 += "rule \"Rule 3\"\n";
        rule3 += "    salience 10\n";
        rule3 += "    agenda-group \"agenda-group-2\"\n";
        rule3 += "    activation-group \"activation-group-2\"\n";
        rule3 += "when\n";
        rule3 += "    $c : Cheese( ) \n";
        rule3 += "then\n";
        rule3 += "    list.add( \"rule3\" );\n";
        rule3 += "    drools.halt();\n";
        rule3 += "end";

        String rule4 = "package org.test;\n";
        rule4 += "import org.drools.Cheese\n";
        rule4 += "global java.util.List list\n";
        rule4 += "rule \"Rule 4\"\n";
        rule4 += "    agenda-group \"agenda-group-2\"\n";
        rule4 += "    activation-group \"activation-group-2\"\n";
        rule4 += "when\n";
        rule4 += "    $c : Cheese( ) \n";
        rule4 += "then\n";
        rule4 += "    list.add( \"rule4\" );\n";
        rule4 += "    drools.halt();\n";
        rule4 += "end";

        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new StringReader( rule1 ) );
        builder.addPackageFromDrl( new StringReader( rule2 ) );
        builder.addPackageFromDrl( new StringReader( rule3 ) );
        builder.addPackageFromDrl( new StringReader( rule4 ) );

        final Package pkg = builder.getPackage();

        RuleBase ruleBase = RuleBaseFactory.newRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase = SerializationHelper.serializeObject( ruleBase );
        StatefulSession session = ruleBase.newStatefulSession();

        session = getSerialisedStatefulSession( session );

        final List list = new ArrayList();
        session.setGlobal( "list",
                           list );

        final Cheese brie = new Cheese( "brie",
                                        12 );
        session.insert( brie );

        session = getSerialisedStatefulSession( session );
        session.setFocus( "agenda-group-1" );
        session = getSerialisedStatefulSession( session );
        session.fireAllRules();
        assertEquals( "rule2",
                      list.get( 0 ) );

        session = getSerialisedStatefulSession( session );
        session.setFocus( "agenda-group-2" );
        session = getSerialisedStatefulSession( session );
        session.fireAllRules();
        assertEquals( "rule3",
                      list.get( 1 ) );

        session = getSerialisedStatefulSession( session );
        session.fireAllRules();
        assertEquals( "rule1",
                      list.get( 2 ) );
    }

    public void testRuleFlowGroups() throws Exception {
        String rule1 = "package org.test;\n";
        rule1 += "import org.drools.Cheese\n";
        rule1 += "global java.util.List list\n";
        rule1 += "rule \"Rule 1\"\n";
        rule1 += "    ruleflow-group \"ruleflow-group-1\"\n";
        rule1 += "when\n";
        rule1 += "    $c : Cheese( ) \n";
        rule1 += "then\n";
        rule1 += "    list.add( \"rule1\" );\n";
        rule1 += "    drools.halt();\n";
        rule1 += "end";

        String rule2 = "package org.test;\n";
        rule2 += "import org.drools.Cheese\n";
        rule2 += "global java.util.List list\n";
        rule2 += "rule \"Rule 2\"\n";
        rule2 += "    salience 10\n";
        rule2 += "    ruleflow-group \"ruleflow-group-1\"\n";
        rule2 += "when\n";
        rule2 += "    $c : Cheese( ) \n";
        rule2 += "then\n";
        rule2 += "    list.add( \"rule2\" );\n";
        rule2 += "    drools.halt();\n";
        rule2 += "end";

        String rule3 = "package org.test;\n";
        rule3 += "import org.drools.Cheese\n";
        rule3 += "global java.util.List list\n";
        rule3 += "rule \"Rule 3\"\n";
        rule3 += "    salience 10\n";
        rule3 += "    ruleflow-group \"ruleflow-group-2\"\n";
        rule3 += "    activation-group \"activation-group-2\"\n";
        rule3 += "when\n";
        rule3 += "    $c : Cheese( ) \n";
        rule3 += "then\n";
        rule3 += "    list.add( \"rule3\" );\n";
        rule3 += "    drools.halt();\n";
        rule3 += "end";

        String rule4 = "package org.test;\n";
        rule4 += "import org.drools.Cheese\n";
        rule4 += "global java.util.List list\n";
        rule4 += "rule \"Rule 4\"\n";
        rule4 += "    ruleflow-group \"ruleflow-group-2\"\n";
        rule4 += "    activation-group \"activation-group-2\"\n";
        rule4 += "when\n";
        rule4 += "    $c : Cheese( ) \n";
        rule4 += "then\n";
        rule4 += "    list.add( \"rule4\" );\n";
        rule4 += "    drools.halt();\n";
        rule4 += "end";

        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new StringReader( rule1 ) );
        builder.addPackageFromDrl( new StringReader( rule2 ) );
        builder.addPackageFromDrl( new StringReader( rule3 ) );
        builder.addPackageFromDrl( new StringReader( rule4 ) );

        final Package pkg = builder.getPackage();

        RuleBase ruleBase = RuleBaseFactory.newRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase = SerializationHelper.serializeObject( ruleBase );
        StatefulSession session = ruleBase.newStatefulSession();

        session = getSerialisedStatefulSession( session );

        final List list = new ArrayList();
        session.setGlobal( "list",
                           list );

        final Cheese brie = new Cheese( "brie",
                                        12 );
        session.insert( brie );

        session = getSerialisedStatefulSession( session );
        session.getAgenda().activateRuleFlowGroup( "ruleflow-group-1" );
        session = getSerialisedStatefulSession( session );
        session.fireAllRules();
        assertEquals( "rule2",
                      list.get( 0 ) );

        session = getSerialisedStatefulSession( session );
        session.getAgenda().activateRuleFlowGroup( "ruleflow-group-2" );
        session = getSerialisedStatefulSession( session );
        session.fireAllRules();
        assertEquals( "rule3",
                      list.get( 1 ) );

        session = getSerialisedStatefulSession( session );
        session.fireAllRules();
        assertEquals( "rule1",
                      list.get( 2 ) );
    }

    //    public StatefulSession getSerialisedStatefulSession(StatefulSession session) throws Exception {
    //        return getSerialisedStatefulSession( session,
    //                                             true );
    //    }
    //
    //    public StatefulSession getSerialisedStatefulSession(StatefulSession session,
    //                                                        boolean dispose) throws Exception {
    //        Marshaller marshaller = new Marshaller();
    //
    //        ReteooRuleBase ruleBase = (ReteooRuleBase) session.getRuleBase();
    //
    //        ByteArrayOutputStream baos = new ByteArrayOutputStream();
    //        ruleBase.writeStatefulSession( session,
    //                                       baos,
    //                                       marshaller );
    //
    //        byte[] b1 = baos.toByteArray();
    //        ByteArrayInputStream bais = new ByteArrayInputStream( b1 );
    //        StatefulSession session2 = ruleBase.readStatefulSession( bais,
    //                                                                 true,
    //                                                                 marshaller );
    //
    //        // write methods allways needs a new marshaller for Identity strategies
    //        marshaller = new Marshaller();
    //        baos = new ByteArrayOutputStream();
    //        ruleBase.writeStatefulSession( session2,
    //                                       baos,
    //                                       marshaller );
    //
    //        byte[] b2 = baos.toByteArray();
    //        // bytes should be the same.
    //        assertTrue( areByteArraysEqual( b1,
    //                                        b2 ) );
    //
    //        session2.setGlobalResolver( session.getGlobalResolver() );
    //
    //        if ( dispose ) {
    //            session.dispose();
    //        }
    //
    //        return session2;
    //    }

    public static boolean areByteArraysEqual(byte[] b1,
                                             byte[] b2) {
        if ( b1.length != b2.length ) {
            return false;
        }

        for ( int i = 0, length = b1.length; i < length; i++ ) {
            if ( b1[i] != b2[i] ) {
                return false;
            }
        }

        return true;
    }

}