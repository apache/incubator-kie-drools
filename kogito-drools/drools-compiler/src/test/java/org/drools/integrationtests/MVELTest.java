package org.drools.integrationtests;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Serializable;
import java.io.StringReader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.junit.Test;
import static org.junit.Assert.*;

import org.drools.Cheese;
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.Person;
import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.StatefulSession;
import org.drools.WorkingMemory;
import org.drools.base.mvel.MVELDebugHandler;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.compiler.DrlParser;
import org.drools.compiler.DroolsParserException;
import org.drools.compiler.PackageBuilder;
import org.drools.compiler.PackageBuilderConfiguration;
import org.drools.core.util.DateUtils;
import org.drools.io.ResourceFactory;
import org.drools.lang.descr.PackageDescr;
import org.drools.rule.Package;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.type.DateFormatsImpl;
import org.mvel2.MVEL;

public class MVELTest {
    @Test
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

        Cheese c = new Cheese( "stilton",
                               10 );
        workingMemory.insert( c );
        workingMemory.fireAllRules();
        assertEquals( 2,
                      list.size() );
        assertEquals( BigInteger.valueOf( 30 ),
                      list.get( 0 ) );
        assertEquals( Integer.valueOf( 22 ),
                      list.get( 1 ) );

        assertEquals( "hello world",
                      list2.get( 0 ) );

        Date dt = DateUtils.parseDate( "10-Jul-1974",
                                       new DateFormatsImpl() );
        assertEquals( dt,
                      c.getUsedBy() );
    }

    @Test
    public void testIncrementOperator() throws Exception {
        String str = "";
        str += "package org.drools \n";
        str += "global java.util.List list \n";
        str += "rule rule1 \n";
        str += "    dialect \"mvel\" \n";
        str += "when \n";
        str += "    $I : Integer() \n";
        str += "then \n";
        str += "    i = $I.intValue(); \n";
        str += "    i += 5; \n";
        str += "    list.add( i ); \n";
        str += "end \n";

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();

        kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ),
                      ResourceType.DRL );

        assertFalse( kbuilder.hasErrors() );

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        List list = new ArrayList();
        ksession.setGlobal( "list",
                            list );
        ksession.insert( 5 );

        ksession.fireAllRules();

        assertEquals( 1,
                      list.size() );
        assertEquals( 10,
                      list.get( 0 ) );
    }

    @Test
    public void testEvalWithBigDecimal() throws Exception {
        String str = "";
        str += "package org.drools \n";
        str += "import java.math.BigDecimal; \n";
        str += "global java.util.List list \n";
        str += "rule rule1 \n";
        str += "    dialect \"mvel\" \n";
        str += "when \n";
        str += "    $bd : BigDecimal() \n";
        str += "    eval( $bd.compareTo( BigDecimal.ZERO ) > 0 ) \n";
        str += "then \n";
        str += "    list.add( $bd ); \n";
        str += "end \n";

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();

        kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ),
                      ResourceType.DRL );

        if ( kbuilder.hasErrors() ) {
            System.err.println( kbuilder.getErrors() );
        }
        assertFalse( kbuilder.hasErrors() );

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        List list = new ArrayList();
        ksession.setGlobal( "list",
                            list );
        ksession.insert( new BigDecimal( 1.5 ) );

        ksession.fireAllRules();

        assertEquals( 1,
                      list.size() );
        assertEquals( new BigDecimal( 1.5 ),
                      list.get( 0 ) );
    }

    @Test
    public void testLocalVariableMVELConsequence() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_LocalVariableMVELConsequence.drl" ) ) );
        final Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase = SerializationHelper.serializeObject( ruleBase );

        final WorkingMemory workingMemory = ruleBase.newStatefulSession();

        final List list = new ArrayList();
        workingMemory.setGlobal( "results",
                                 list );

        workingMemory.insert( new Person( "bob",
                                          "stilton" ) );
        workingMemory.insert( new Person( "mark",
                                          "brie" ) );

        try {
            workingMemory.fireAllRules();

            assertEquals( "should have fired twice",
                          2,
                          list.size() );

        } catch ( Exception e ) {
            e.printStackTrace();
            fail( "Should not raise any exception" );
        }

    }

    @Test
    public void testMVELUsingGlobalsInDebugMode() throws Exception {
        MVELDebugHandler.setDebugMode( true );
        try {
            final PackageBuilder builder = new PackageBuilder();
            builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_MVELGlobalDebug.drl" ) ) );
            final Package pkg = builder.getPackage();
            RuleBase ruleBase = getRuleBase();
            ruleBase.addPackage( pkg );
            ruleBase = SerializationHelper.serializeObject( ruleBase );
            final StatefulSession session = ruleBase.newStatefulSession();
            session.dispose();
            MVELDebugHandler.setDebugMode( false );
        } catch ( Exception e ) {
            MVELDebugHandler.setDebugMode( false );
            e.printStackTrace();
            fail( "Should not raise exceptions" );
        }

    }

    @Test
    public void testDuplicateLocalVariableMVELConsequence() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_DuplicateLocalVariableMVELConsequence.drl" ) ) );

        assertTrue( builder.hasErrors() );
    }

    @Test
    public void testArrays() throws Exception {
        String text = "package test_mvel;\n";
        text += "import org.drools.integrationtests.TestObject;\n";
        text += "import function org.drools.integrationtests.TestObject.array;\n";;
        text += "no-loop true\n";
        text += "dialect \"mvel\"\n";
        text += "rule \"1\"\n";
        text += "salience 1\n";
        text += "when\n";
        text += "    $fact: TestObject()\n";
        text += "    eval($fact.checkHighestPriority(\"mvel\", 2))\n";
        text += "    eval($fact.stayHasDaysOfWeek(\"mvel\", false, new String[][]{{\"2008-04-01\", \"2008-04-10\"}}))\n";
        text += "then\n";
        text += "    $fact.applyValueAddPromo(1,2,3,4,\"mvel\");\n";
        text += "end";
        
        RuleBase ruleBase = RuleBaseFactory.newRuleBase( );
        // get the java dialect
        ruleBase.addPackage( compileRule( text.replaceAll( "mvel",
                                                          "java" ) ) );
        // get the mvel dialect
        ruleBase.addPackage( compileRule( text ) );

        List<String> list = new ArrayList<String>();
        
        ruleBase.newStatelessSession().execute( new TestObject( list ) );
        
        assertEquals( 6, list.size() );
        
        assertEquals("TestObject.checkHighestPriority: java|2", list.get(0));
        assertEquals("TestObject.stayHasDaysOfWeek: java|false|[2008-04-01, 2008-04-10]", list.get(1));
        assertEquals("TestObject.checkHighestPriority: mvel|2", list.get(2));
        assertEquals("TestObject.stayHasDaysOfWeek: mvel|false|[2008-04-01, 2008-04-10]", list.get(3));
        assertEquals("TestObject.applyValueAddPromo: 1|2|3|4|mvel", list.get(4));
        assertEquals("TestObject.applyValueAddPromo: 1|2|3|4|java", list.get(5));
    }
    
    @Test
    public void testPackageImports() throws Exception {
        String str = "";
        str += "package org.drools \n";
        str += "dialect \"mvel\"\n";
        str += "import org.acme.healthcare.* \n";
        str += "import org.acme.insurance.* \n";
        str += "import org.acme.sensors.SensorReading \n";
        str += "rule rule1 \n";
        str += "  when \n";
        str += "    eval(true)\n";
        str += "  then \n";
        str += "    insert(new Claim());         // from org.acme.healthcare.* \n";
        str += "    insert(new Policy());        // from org.acme.insurance.* \n";
        str += "    insert(new SensorReading()); // from org.acme.sensor.SensorReading \n";
        str += "end\n";
        
        final KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ), ResourceType.DRL );
        if (kbuilder.hasErrors()) {
          throw new RuntimeException(kbuilder.getErrors().toString());
        }
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );
        
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        int result = ksession.fireAllRules();
        
        assertEquals(1, result);
        Collection<Object> insertedObjects = ksession.getObjects();
        assertEquals(3, insertedObjects.size());
    }
    
    @Test
    public void testNestedEnum() {
        String str = ""+
           "package org.test \n" +
           "import " + Triangle.class.getCanonicalName() + "\n" +
           "global java.util.List list \n" +
           "rule \"show\" \n" + 
           "when  \n" + 
           "    $t: Triangle(t == Triangle.Type.ACUTE) \n" + 
           "then \n" + 
           "    list.add($t.getT()); \n" + 
           "end \n";
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ), ResourceType.DRL );
        
        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }
        
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );
 
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        List list = new ArrayList();
        ksession.setGlobal( "list", list );
        Triangle t = new Triangle( Triangle.Type.ACUTE);
        ksession.insert( t );
        
        ksession.fireAllRules();    
        
        assertEquals(Triangle.Type.ACUTE, list.get(0) );
    }
    
    public static class Triangle {
        public static enum Type {
            ACUTE, OBTUSE;
        }
        
        private Type t;
        
        public Triangle(Type t) {
            this.t = t;
        }

        public Type getT() {
            return t;
        }

        public void setT(Type t) {
            this.t = t;
        }
    }
    

    private Package compileRule(String drl) throws Exception {
        PackageBuilder builder = new PackageBuilder( new PackageBuilderConfiguration() );

        builder.addPackageFromDrl( new StringReader( drl ) );
        Package pkg = builder.getPackage();

        if ( !pkg.isValid() ) {
            throw new DroolsParserException( pkg.getErrorSummary() );
        }
        return pkg;
    }
    
    public Object compiledExecute(String ex) {
        Serializable compiled = MVEL.compileExpression( ex );
        return MVEL.executeExpression( compiled,
                                       new Object(),
                                       new HashMap() );
    }

    private RuleBase loadRuleBase(final Reader reader) throws IOException,
                                                      DroolsParserException,
                                                      Exception {
        final DrlParser parser = new DrlParser();
        final PackageDescr packageDescr = parser.parse( reader );
        if ( parser.hasErrors() ) {
            fail( "Error messages in parser, need to sort this our (or else collect error messages)\n" + parser.getErrors()  );
        }
        // pre build the package
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackage( packageDescr );

        if ( builder.hasErrors() ) {
            fail( builder.getErrors().toString() );
        }

        final Package pkg = builder.getPackage();

        // add the package to a rulebase
        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase = SerializationHelper.serializeObject( ruleBase );
        // load up the rulebase
        return ruleBase;
    }

    protected RuleBase getRuleBase() throws Exception {

        return RuleBaseFactory.newRuleBase( RuleBase.RETEOO,
                                            null );
    }
}
