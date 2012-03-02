package org.drools.integrationtests;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Serializable;
import java.io.StringReader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import org.drools.Address;
import org.drools.Cheese;
import org.drools.CommonTestMethodBase;
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseConfiguration;
import org.drools.KnowledgeBaseFactory;
import org.drools.Person;
import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.StatefulSession;
import org.drools.TestEnum;
import org.drools.WorkingMemory;
import org.drools.base.ClassFieldReader;
import org.drools.base.ClassObjectType;
import org.drools.base.extractors.MVELClassFieldReader;
import org.drools.base.mvel.MVELDebugHandler;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderConfiguration;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.common.InternalRuleBase;
import org.drools.compiler.DrlParser;
import org.drools.compiler.DroolsParserException;
import org.drools.compiler.PackageBuilder;
import org.drools.compiler.PackageBuilderConfiguration;
import org.drools.core.util.DateUtils;
import org.drools.impl.KnowledgeBaseImpl;
import org.drools.io.ResourceFactory;
import org.drools.lang.descr.PackageDescr;
import org.drools.reteoo.AlphaNode;
import org.drools.reteoo.ObjectTypeNode;
import org.drools.rule.LiteralConstraint;
import org.drools.rule.MapBackedClassLoader;
import org.drools.rule.Package;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.spi.AlphaNodeFieldConstraint;
import org.drools.spi.FieldValue;
import org.drools.type.DateFormatsImpl;
import org.junit.Ignore;
import org.junit.Test;
import org.mvel2.MVEL;
import org.mvel2.ParserContext;

public class MVELTest extends CommonTestMethodBase {
    
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

        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
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

        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
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
        
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);

        int result = ksession.fireAllRules();
        
        assertEquals(1, result);
        Collection<Object> insertedObjects = ksession.getObjects();
        assertEquals(3, insertedObjects.size());
    }
    
    @Test @Ignore // MVEL bug
    public void testSizeCheckInObject() {
        String str = ""+
        "package org.test \n" +
        "import " + Triangle.class.getCanonicalName() + "\n" +
        //"import " + Address.class.getCanonicalName() + "\n" +
        "global java.util.List list \n" +
        "rule \"show\" \n" + 
        "when  \n" + 
        "    $m : Triangle( deliveries.size == 0) \n" + 
        "then \n" + 
        "    list.add('r1'); \n" + 
        "end \n";
         KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
         kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ), ResourceType.DRL );
         
         if ( kbuilder.hasErrors() ) {
             System.out.println( kbuilder.getErrors().toString()  );
             fail( kbuilder.getErrors().toString());
         }
         
//         KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
//         kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );
//    
//         StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
//         List list = new ArrayList();
//         ksession.setGlobal( "list", list );
//         
//         Person p = new Person("yoda");
//         p.setAddress( new Address("s1") );
//         
//         ksession.insert( p );
//         
//         ksession.fireAllRules();    
//         
//         assertEquals( "r1", list.get(0) );
//         
//         // Check it was built with MVELReturnValueExpression constraint
//         List<ObjectTypeNode> nodes = ((InternalRuleBase)((KnowledgeBaseImpl)kbase).ruleBase).getRete().getObjectTypeNodes();
//         ObjectTypeNode node = null;
//         for ( ObjectTypeNode n : nodes ) {
//             if ( ((ClassObjectType)n.getObjectType()).getClassType() == Person.class ) {
//                 node = n;
//                 break;
//             }
//         }
//         
//         AlphaNode alphanode = (AlphaNode) node.getSinkPropagator().getSinks()[0];        
//         assertTrue( (( VariableConstraint ) alphanode.getConstraint()).getFieldExtractor() instanceof ClassFieldReader );
//         ReturnValueRestriction r = (ReturnValueRestriction) (( VariableConstraint ) alphanode.getConstraint()).getRestriction();
//         
//         assertTrue( r.getExpression() instanceof ReturnValueExpression );
//         assertTrue( r.getExpression() instanceof MVELReturnValueExpression );  
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
 
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        List list = new ArrayList();
        ksession.setGlobal( "list", list );
        Triangle t = new Triangle( Triangle.Type.ACUTE);
        ksession.insert( t );
        
        ksession.fireAllRules();    
        
        assertEquals(Triangle.Type.ACUTE, list.get(0) );
    }
    
    @Test
    public void testNestedEnumWithMap() {
        String str = ""+
           "package org.test \n" +
           "import " + DMap.class.getCanonicalName() + " \n" +
           "import " + Triangle.class.getCanonicalName() + "\n" +
           "global java.util.List list \n" +
           "rule \"show\" \n" + 
           "when  \n" + 
           "    $m : DMap( this[Triangle.Type.ACUTE] == 'xxx') \n" + 
           "then \n" + 
           "    list.add('r1'); \n" + 
           "end \n";
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ), ResourceType.DRL );
        
        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }
        
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );
 
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        List list = new ArrayList();
        ksession.setGlobal( "list", list );
        
        DMap m = new DMap();
        m.put(  Triangle.Type.ACUTE, "xxx" );
        
        ksession.insert( m );
        
        ksession.fireAllRules();    
        
        assertEquals( "r1", list.get(0) );
    } 
    
    @Test
    public void testNewConstructor() {
        String str = ""+
           "package org.test \n" +
           "import " + Person.class.getCanonicalName() + "\n" +
           "import " + Address.class.getCanonicalName() + "\n" +
           "global java.util.List list \n" +
           "rule \"show\" \n" + 
           "when  \n" + 
           "    $m : Person( address == new Address('s1')) \n" + 
           "then \n" + 
           "    list.add('r1'); \n" + 
           "end \n";
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ), ResourceType.DRL );
        
        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }
        
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );
 
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        List list = new ArrayList();
        ksession.setGlobal( "list", list );
        
        Person p = new Person("yoda");
        p.setAddress( new Address("s1") );
        
        ksession.insert( p );
        
        ksession.fireAllRules();    
        
        assertEquals( "r1", list.get(0) );
        
        // Check it was built with MVELReturnValueExpression constraint
        List<ObjectTypeNode> nodes = ((InternalRuleBase)((KnowledgeBaseImpl)kbase).ruleBase).getRete().getObjectTypeNodes();
        ObjectTypeNode node = null;
        for ( ObjectTypeNode n : nodes ) {
            if ( ((ClassObjectType)n.getObjectType()).getClassType() == Person.class ) {
                node = n;
                break;
            }
        }
        
        AlphaNode alphanode = (AlphaNode) node.getSinkPropagator().getSinks()[0];
        AlphaNodeFieldConstraint constraint = alphanode.getConstraint();

        if (constraint instanceof LiteralConstraint) {
            assertTrue( (( LiteralConstraint )constraint).getFieldExtractor() instanceof ClassFieldReader );
            FieldValue r = (( LiteralConstraint )constraint).getField();
            assertEquals( p.getAddress(), r.getValue() );
        }
    }         
    
    @Test
    public void testArrayAccessorWithGenerics() {
        String str = ""+
           "package org.test \n" +
           "import " + Person.class.getCanonicalName() + "\n" +
           "import " + Address.class.getCanonicalName() + "\n" +
           "global java.util.List list \n" +
           "rule \"show\" \n" + 
           "when  \n" + 
           "    $m : Person( addresses[0] == new Address('s1'), addresses[0].street == new Address('s1').street ) \n" + 
           "then \n" + 
           "    list.add('r1'); \n" + 
           "end \n";
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ), ResourceType.DRL );
        
        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }
        
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );
 
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        List list = new ArrayList();
        ksession.setGlobal( "list", list );
        
        Person p = new Person("yoda");
        p.addAddress( new Address("s1") );
        
        ksession.insert( p );
        
        ksession.fireAllRules();    
        
        assertEquals( "r1", list.get(0) );
        
        // Check it was built with MVELReturnValueExpression constraint
        List<ObjectTypeNode> nodes = ((InternalRuleBase)((KnowledgeBaseImpl)kbase).ruleBase).getRete().getObjectTypeNodes();
        ObjectTypeNode node = null;
        for ( ObjectTypeNode n : nodes ) {
            if ( ((ClassObjectType)n.getObjectType()).getClassType() == Person.class ) {
                node = n;
                break;
            }
        }
        
        AlphaNode alphanode = (AlphaNode) node.getSinkPropagator().getSinks()[0];        
        AlphaNodeFieldConstraint constraint = alphanode.getConstraint();

        if (constraint instanceof LiteralConstraint) {
            assertTrue( (( LiteralConstraint )constraint).getFieldExtractor() instanceof MVELClassFieldReader );
            assertEquals( new Address("s1"), (( LiteralConstraint )constraint).getField().getValue() );
        }

        alphanode = (AlphaNode) alphanode.getSinkPropagator().getSinks()[0];
        constraint = alphanode.getConstraint();

        if (constraint instanceof LiteralConstraint) {
            assertTrue( (( LiteralConstraint )constraint).getFieldExtractor() instanceof MVELClassFieldReader );
            assertEquals( new Address("s1").getStreet(), (( LiteralConstraint )constraint).getField().getValue() );
        }
    }    
    
    @Test
    public void testArrayAccessorWithStaticFieldAccess() {
        String str = ""+
           "package org.test \n" +
           "import " + Person.class.getCanonicalName() + "\n" +
           "import " + Address.class.getCanonicalName() + "\n" +
           "import " + Triangle.class.getCanonicalName() + "\n" +
           "global java.util.List list \n" +
           "rule \"show\" \n" + 
           "when  \n" + 
           "    $m : Person( addresses[Triangle.ZERO] == new Address('s1'), addresses[Triangle.ZERO].street == new Address('s1').street ) \n" + 
           "then \n" + 
           "    list.add('r1'); \n" + 
           "end \n";
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ), ResourceType.DRL );
        
        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }
        
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );
 
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        List list = new ArrayList();
        ksession.setGlobal( "list", list );
        
        Person p = new Person("yoda");
        p.addAddress( new Address("s1") );
        
        ksession.insert( p );
        
        ksession.fireAllRules();    
        
        assertEquals( "r1", list.get(0) );
        
        // Check it was built with MVELReturnValueExpression constraint
        List<ObjectTypeNode> nodes = ((InternalRuleBase)((KnowledgeBaseImpl)kbase).ruleBase).getRete().getObjectTypeNodes();
        ObjectTypeNode node = null;
        for ( ObjectTypeNode n : nodes ) {
            if ( ((ClassObjectType)n.getObjectType()).getClassType() == Person.class ) {
                node = n;
                break;
            }
        }
        
        AlphaNode alphanode = (AlphaNode) node.getSinkPropagator().getSinks()[0];        
        AlphaNodeFieldConstraint constraint = alphanode.getConstraint();

        if (constraint instanceof LiteralConstraint) {
            assertTrue( (( LiteralConstraint ) alphanode.getConstraint()).getFieldExtractor() instanceof MVELClassFieldReader );
            assertEquals( new Address("s1"), (( LiteralConstraint ) alphanode.getConstraint()).getField().getValue() );
        }

        alphanode = (AlphaNode) alphanode.getSinkPropagator().getSinks()[0];
        constraint = alphanode.getConstraint();
        if (constraint instanceof LiteralConstraint) {
            assertTrue( (( LiteralConstraint ) alphanode.getConstraint()).getFieldExtractor() instanceof MVELClassFieldReader );
            assertEquals( new Address("s1").getStreet(), (( LiteralConstraint ) alphanode.getConstraint()).getField().getValue() );
        }
    }       
    
    @Test
    public void testMapAccessorWithStaticFieldAccess() {
        String str = ""+
           "package org.test \n" +
           "import " + Person.class.getCanonicalName() + "\n" +
           "import " + Address.class.getCanonicalName() + "\n" +
           "import " + TestEnum.class.getCanonicalName() + "\n" +
           "global java.util.List list \n" +
           "rule \"show\" \n" + 
           "when  \n" + 
           "    $m : Person( namedAddresses[TestEnum.ONE] == new Address('s1'), namedAddresses[TestEnum.ONE].street == new Address('s1').street ) \n" + 
           "then \n" + 
           "    list.add('r1'); \n" + 
           "end \n";
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ), ResourceType.DRL );
        
        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }
        
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );
 
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        List list = new ArrayList();
        ksession.setGlobal( "list", list );
        
        Person p = new Person("yoda");
        p.getNamedAddresses().put( TestEnum.ONE,  new Address("s1") );
        
        ksession.insert( p );
        
        ksession.fireAllRules();    
        
        assertEquals( "r1", list.get(0) );
        
        // Check it was built with MVELReturnValueExpression constraint
        List<ObjectTypeNode> nodes = ((InternalRuleBase)((KnowledgeBaseImpl)kbase).ruleBase).getRete().getObjectTypeNodes();
        ObjectTypeNode node = null;
        for ( ObjectTypeNode n : nodes ) {
            if ( ((ClassObjectType)n.getObjectType()).getClassType() == Person.class ) {
                node = n;
                break;
            }
        }
        
        AlphaNode alphanode = (AlphaNode) node.getSinkPropagator().getSinks()[0];        
        AlphaNodeFieldConstraint constraint = alphanode.getConstraint();

        if (constraint instanceof LiteralConstraint) {
            assertTrue( (( LiteralConstraint ) alphanode.getConstraint()).getFieldExtractor() instanceof MVELClassFieldReader );
            assertEquals( new Address("s1"), (( LiteralConstraint ) alphanode.getConstraint()).getField().getValue() );
        }

        alphanode = (AlphaNode) alphanode.getSinkPropagator().getSinks()[0];
        constraint = alphanode.getConstraint();

        if (constraint instanceof LiteralConstraint) {
            assertTrue( (( LiteralConstraint ) alphanode.getConstraint()).getFieldExtractor() instanceof MVELClassFieldReader );
            assertEquals( new Address("s1").getStreet(), (( LiteralConstraint ) alphanode.getConstraint()).getField().getValue() );
        }
    }     
    
    @Test
    public void testArrayAccessorWithoutGenerics() {
        String str = ""+
           "package org.test \n" +
           "import " + Person.class.getCanonicalName() + "\n" +
           "import " + Address.class.getCanonicalName() + "\n" +
           "global java.util.List list \n" +
           "rule \"show\" \n" + 
           "when  \n" + 
           "    $m : Person( addressesNoGenerics[0].street == new Address('s1').street) \n" + 
           "then \n" + 
           "    list.add('r1'); \n" + 
           "end \n";
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ), ResourceType.DRL );
        
        // This should fail as there are no generics for the List 
        assertTrue( kbuilder.hasErrors() );
        
    }         
    
    public static class DMap extends HashMap {
        
    }

    @Test
    @Ignore("Added 30-APR-2011 -Rikkola-")
    public void testNestedEnumFromJar() {
        String str = ""+
           "package org.test \n" +
           "import org.drools.examples.eventing.EventRequest \n" +
           "global java.util.List list \n" +
           "rule 'zaa'\n  " +
           "when \n  " +
           "request: EventRequest( status == EventRequest.Status.ACTIVE )\n   " +
           "then \n " +
           "request.setStatus(EventRequest.Status.ACTIVE); \n  " +
           "end";


        JarInputStream jis = null;
        try {
            jis = new JarInputStream( this.getClass().getResourceAsStream( "/eventing-example.jar" ) );
        } catch (IOException e) {
            fail("Failed to load the jar");
        }
        MapBackedClassLoader loader = createClassLoader( jis );

        KnowledgeBuilderConfiguration knowledgeBuilderConfiguration = KnowledgeBuilderFactory.newKnowledgeBuilderConfiguration(null, loader);
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder(knowledgeBuilderConfiguration);
        kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ), ResourceType.DRL );

        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }

        KnowledgeBaseConfiguration knowledgeBaseConfiguration = KnowledgeBaseFactory.newKnowledgeBaseConfiguration(null,loader);

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase(knowledgeBaseConfiguration);
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        List list = new ArrayList();
        ksession.setGlobal( "list", list );
        Triangle t = new Triangle( Triangle.Type.ACUTE);
        ksession.insert( t );

        ksession.fireAllRules();

        assertEquals(Triangle.Type.ACUTE, list.get(0) );
    }

    public static MapBackedClassLoader createClassLoader(JarInputStream jis) {
        ClassLoader parentClassLoader = Thread.currentThread().getContextClassLoader();

        final ClassLoader p = parentClassLoader;

        MapBackedClassLoader loader = AccessController.doPrivileged(new PrivilegedAction<MapBackedClassLoader>() {
            public MapBackedClassLoader run() {
                return new MapBackedClassLoader(p);
            }
        });

        try {
            JarEntry entry = null;
            byte[] buf = new byte[1024];
            int len = 0;
            while ((entry = jis.getNextJarEntry()) != null) {
                if (!entry.isDirectory() && !entry.getName().endsWith(".java")) {
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    while ((len = jis.read(buf)) >= 0) {
                        out.write(buf, 0, len);
                    }

                    loader.addResource(entry.getName(), out.toByteArray());
                }
            }

        } catch (IOException e) {
            fail("failed to read the jar");
        }
        return loader;
    }

    public static class Triangle {
        public static final int ZERO = 0;
        
        private List<Map<String, Object>> deliveries;
        
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
        
        public List<Map<String, Object>> getDeliveries() {
            return deliveries;
        }
    
        public void setDeliveries(List<Map<String, Object>> deliveries) {
                this.deliveries = deliveries;
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

    @Test
    public void test1() {
    	ParserContext pc = new ParserContext();
    	pc.addInput("x", String.class);
    	pc.setStrongTyping(true);
    	Object o = MVEL.compileExpression("x.startsWith('d')", pc);
    	Map vars = new HashMap();
    	vars.put("x", "d");
    	MVEL.executeExpression(o, vars);
    	System.out.println( o );
    }

    @Test
    public void testTokensInString(){
        //should query antldr DFA63 class but don't know how
        String [] operators = {"," ,"=" , "|=", "*"};
        //test various in consequence
        String strBegin = "" +
            "package org.drools \n" +
            "import org.drools.Cheese \n" +
            "dialect \"mvel\"\n" +
            "rule rule1 \n" +
            "when \n" +
            "$c:Cheese(type==\"swiss\") \n" +
            "then \n"+
            "modify($c){ type = \"swiss";

        String strEnd= "good\"};\n" +
                "end\n";
        StringBuffer failures = new StringBuffer();
        for(String oper:operators){
            KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
            String rule = strBegin+oper+strEnd;
            System.out.print(rule);
            kbuilder.add(ResourceFactory.newByteArrayResource(rule.getBytes()), ResourceType.DRL);
            if (kbuilder.hasErrors()) {
                failures.append(kbuilder.getErrors().toString());
            }
        }
        String failStr = failures.toString();
        if(failStr.length()>0){
            fail(failStr);
        }
    }
}
