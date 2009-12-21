package org.drools.integrationtests;

import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.drools.Cheese;
import org.drools.FactA;
import org.drools.FactB;
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseConfiguration;
import org.drools.KnowledgeBaseFactory;
import org.drools.Order;
import org.drools.OrderItem;
import org.drools.Person;
import org.drools.PersonInterface;
import org.drools.Precondition;
import org.drools.Primitives;
import org.drools.RuleBase;
import org.drools.RuleBaseConfiguration;
import org.drools.RuleBaseFactory;
import org.drools.StatefulSession;
import org.drools.StockTick;
import org.drools.WorkingMemory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderConfiguration;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.common.InternalFactHandle;
import org.drools.compiler.PackageBuilder;
import org.drools.compiler.PackageBuilderConfiguration;
import org.drools.definition.KnowledgePackage;
import org.drools.definitions.impl.KnowledgePackageImp;
import org.drools.impl.StatefulKnowledgeSessionImpl;
import org.drools.io.ResourceFactory;
import org.drools.marshalling.MarshallerFactory;
import org.drools.reteoo.ReteooWorkingMemory;
import org.drools.rule.Package;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.rule.WorkingMemoryEntryPoint;
import org.drools.util.DroolsStreamUtils;

public class DynamicRulesTest extends TestCase {
    protected RuleBase getRuleBase() throws Exception {

        return RuleBaseFactory.newRuleBase( RuleBase.RETEOO,
                                            null );
    }

    protected RuleBase getRuleBase(final RuleBaseConfiguration config) throws Exception {

        return RuleBaseFactory.newRuleBase( RuleBase.RETEOO,
                                            config );
    }

    public void testDynamicRuleAdditions() throws Exception {
        Reader reader = new InputStreamReader( getClass().getResourceAsStream( "test_Dynamic1.drl" ) );

        PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( reader );
        final Package pkg1 = SerializationHelper.serializeObject( builder.getPackage() );

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg1 );
        ruleBase = SerializationHelper.serializeObject( ruleBase );
        final WorkingMemory workingMemory = ruleBase.newStatefulSession();
        workingMemory.setGlobal( "total",
                                 new Integer( 0 ) );

        final List list = new ArrayList();
        workingMemory.setGlobal( "list",
                                 list );

        // Adding person in advance. There is no Person() object
        // type node in memory yet, but the rule engine is supposed
        // to handle that correctly
        final PersonInterface bob = new Person( "bob",
                                                "stilton" );
        bob.setStatus( "Not evaluated" );
        workingMemory.insert( bob );

        final Cheese stilton = new Cheese( "stilton",
                                           5 );
        workingMemory.insert( stilton );

        final Cheese cheddar = new Cheese( "cheddar",
                                           5 );
        workingMemory.insert( cheddar );
        workingMemory.fireAllRules();

        assertEquals( 1,
                      list.size() );

        assertEquals( "stilton",
                      list.get( 0 ) );

        reader = new InputStreamReader( getClass().getResourceAsStream( "test_Dynamic2.drl" ) );
        builder = new PackageBuilder();
        builder.addPackageFromDrl( reader );
        final Package pkg2 = SerializationHelper.serializeObject( builder.getPackage() );
        ruleBase.addPackage( pkg2 );

        //        ruleBase    = SerializationHelper.serializeObject(ruleBase);
        workingMemory.fireAllRules();
        assertEquals( 3,
                      list.size() );

        assertEquals( "stilton",
                      list.get( 0 ) );

        assertTrue( "cheddar".equals( list.get( 1 ) ) || "cheddar".equals( list.get( 2 ) ) );

        assertTrue( "stilton".equals( list.get( 1 ) ) || "stilton".equals( list.get( 2 ) ) );

        list.clear();

        reader = new InputStreamReader( getClass().getResourceAsStream( "test_Dynamic3.drl" ) );
        builder = new PackageBuilder();
        builder.addPackageFromDrl( reader );
        final Package pkg3 = SerializationHelper.serializeObject( builder.getPackage() );
        ruleBase.addPackage( pkg3 );
        //        ruleBase    = SerializationHelper.serializeObject(ruleBase);

        // Package 3 has a rule working on Person instances.
        // As we added person instance in advance, rule should fire now
        workingMemory.fireAllRules();

        Assert.assertEquals( "Rule from package 3 should have been fired",
                             "match Person ok",
                             bob.getStatus() );

        assertEquals( 1,
                      list.size() );

        assertEquals( bob,
                      list.get( 0 ) );

        reader = new InputStreamReader( getClass().getResourceAsStream( "test_Dynamic4.drl" ) );
        builder = new PackageBuilder();
        builder.addPackageFromDrl( reader );
        final Package pkg4 = SerializationHelper.serializeObject( builder.getPackage() );
        ruleBase.addPackage( pkg4 );
        workingMemory.fireAllRules();
        ruleBase = SerializationHelper.serializeObject( ruleBase );

        Assert.assertEquals( "Rule from package 4 should have been fired",
                             "Who likes Stilton ok",
                             bob.getStatus() );

        assertEquals( 2,
                      list.size() );

        assertEquals( bob,
                      list.get( 1 ) );

    }

    public void testDynamicRuleRemovals() throws Exception {

        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_Dynamic1.drl" ) ) );
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_Dynamic3.drl" ) ) );
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_Dynamic4.drl" ) ) );
        final Package pkg = SerializationHelper.serializeObject( builder.getPackage() );

        org.drools.reteoo.ReteooRuleBase reteooRuleBase = null;
        // org.drools.leaps.LeapsRuleBase leapsRuleBase = null;
        RuleBase ruleBase = getRuleBase();
        reteooRuleBase = (org.drools.reteoo.ReteooRuleBase) ruleBase;
        ruleBase.addPackage( pkg );
        //        ruleBase    = SerializationHelper.serializeObject(ruleBase);
        final PackageBuilder builder2 = new PackageBuilder();
        builder2.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_Dynamic2.drl" ) ) );
        ruleBase.addPackage( SerializationHelper.serializeObject( builder2.getPackage() ) );
        //        ruleBase    = SerializationHelper.serializeObject(ruleBase);

        final WorkingMemory workingMemory = ruleBase.newStatefulSession();

        final List list = new ArrayList();
        workingMemory.setGlobal( "list",
                                 list );

        final PersonInterface bob = new Person( "bob",
                                                "stilton" );
        bob.setStatus( "Not evaluated" );
        workingMemory.insert( bob );

        final Cheese stilton1 = new Cheese( "stilton",
                                            5 );
        workingMemory.insert( stilton1 );

        final Cheese stilton2 = new Cheese( "stilton",
                                            3 );
        workingMemory.insert( stilton2 );

        final Cheese stilton3 = new Cheese( "stilton",
                                            1 );
        workingMemory.insert( stilton3 );

        final Cheese cheddar = new Cheese( "cheddar",
                                           5 );
        workingMemory.insert( cheddar );

        assertEquals( 15,
                      workingMemory.getAgenda().getActivations().length );

        reteooRuleBase.removeRule( "org.drools.test",
                                   "Who likes Stilton" );
        assertEquals( 12,
                      workingMemory.getAgenda().getActivations().length );

        reteooRuleBase.removeRule( "org.drools.test",
                                   "like cheese" );

        //        reteooRuleBase.removeRule( "org.drools.test",
        //                                   "like cheese2" );

        final Cheese muzzarela = new Cheese( "muzzarela",
                                             5 );
        assertEquals( 8,
                      workingMemory.getAgenda().getActivations().length );

        workingMemory.insert( muzzarela );

        assertEquals( 9,
                      workingMemory.getAgenda().getActivations().length );

        reteooRuleBase.removePackage( "org.drools.test" );
        reteooRuleBase = SerializationHelper.serializeObject( reteooRuleBase );

        assertEquals( 0,
                      workingMemory.getAgenda().getActivations().length );
    }

    public void testDynamicRuleRemovalsUnusedWorkingMemory() throws Exception {

        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_Dynamic1.drl" ) ) );
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_Dynamic2.drl" ) ) );
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_Dynamic3.drl" ) ) );
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_Dynamic4.drl" ) ) );
        final Package pkg = builder.getPackage();

        org.drools.reteoo.ReteooRuleBase reteooRuleBase = null;

        RuleBase ruleBase = getRuleBase();
        reteooRuleBase = (org.drools.reteoo.ReteooRuleBase) ruleBase;

        ruleBase.addPackage( pkg );
        ruleBase = SerializationHelper.serializeObject( ruleBase );

        final WorkingMemory workingMemory = ruleBase.newStatefulSession();

        if ( reteooRuleBase != null ) {
            assertEquals( 1,
                          reteooRuleBase.getPackages().length );
            assertEquals( 5,
                          reteooRuleBase.getPackages()[0].getRules().length );

            reteooRuleBase.removeRule( "org.drools.test",
                                       "Who likes Stilton" );
            assertEquals( 4,
                          reteooRuleBase.getPackages()[0].getRules().length );

            reteooRuleBase.removeRule( "org.drools.test",
                                       "like cheese" );
            assertEquals( 3,
                          reteooRuleBase.getPackages()[0].getRules().length );

            reteooRuleBase.removePackage( "org.drools.test" );
            assertEquals( 0,
                          reteooRuleBase.getPackages().length );
        }
    }

    public void testDynamicFunction() throws Exception {
        PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_DynamicFunction1.drl" ) ) );

        //FIXME JBRULES-1258 serialising a package breaks function removal -- left the serialisation commented out for now
        final Package pkg = SerializationHelper.serializeObject( builder.getPackage() );
        //final Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase = SerializationHelper.serializeObject( ruleBase );
        final WorkingMemory workingMemory = ruleBase.newStatefulSession();

        final List list = new ArrayList();
        workingMemory.setGlobal( "list",
                                 list );

        final Cheese stilton = new Cheese( "stilton",
                                           5 );
        workingMemory.insert( stilton );

        workingMemory.fireAllRules();

        assertEquals( new Integer( 5 ),
                      list.get( 0 ) );

        // Check a function can be removed from a package.
        // Once removed any efforts to use it should throw an Exception
        ruleBase.removeFunction( "org.drools.test",
                                 "addFive" );

        final Cheese cheddar = new Cheese( "cheddar",
                                           5 );
        workingMemory.insert( cheddar );

        try {
            workingMemory.fireAllRules();
            fail( "Function should have been removed and NoClassDefFoundError thrown from the Consequence" );
        } catch ( final NoClassDefFoundError e ) {
        }

        // Check a new function can be added to replace an old function
        builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_DynamicFunction2.drl" ) ) );

        ruleBase.addPackage( SerializationHelper.serializeObject( builder.getPackage() ) );

        final Cheese brie = new Cheese( "brie",
                                        5 );
        workingMemory.insert( brie );

        workingMemory.fireAllRules();

        assertEquals( new Integer( 6 ),
                      list.get( 1 ) );

        builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_DynamicFunction3.drl" ) ) );

        ruleBase.addPackage( SerializationHelper.serializeObject( builder.getPackage() ) );

        final Cheese feta = new Cheese( "feta",
                                        5 );
        workingMemory.insert( feta );

        workingMemory.fireAllRules();

        assertEquals( new Integer( 5 ),
                      list.get( 2 ) );
    }

    public void testRemovePackage() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_RemovePackage.drl" ) ) );

        RuleBase ruleBase = getRuleBase();
        final String packageName = builder.getPackage().getName();
        ruleBase.addPackage( SerializationHelper.serializeObject( builder.getPackage() ) );
        ruleBase = SerializationHelper.serializeObject( ruleBase );

        StatefulSession session = ruleBase.newStatefulSession();

        session.insert( new Precondition( "genericcode",
                                          "genericvalue" ) );
        session.fireAllRules();

        RuleBase ruleBaseWM = session.getRuleBase();
        ruleBaseWM.removePackage( packageName );
        final PackageBuilder builder1 = new PackageBuilder();
        builder1.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_RemovePackage.drl" ) ) );
        ruleBaseWM.addPackage( SerializationHelper.serializeObject( builder1.getPackage() ) );
        ruleBaseWM = SerializationHelper.serializeObject( ruleBaseWM );
        session = SerializationHelper.getSerialisedStatefulSession( session,
                                                                    ruleBase );
        session.fireAllRules();

        ruleBaseWM.removePackage( packageName );
        ruleBaseWM.addPackage( SerializationHelper.serializeObject( builder1.getPackage() ) );

        ruleBaseWM.removePackage( packageName );
        ruleBaseWM.addPackage( SerializationHelper.serializeObject( builder1.getPackage() ) );
    }

    public void testDynamicRules() throws Exception {
        RuleBase ruleBase = getRuleBase();
        StatefulSession session = ruleBase.newStatefulSession();
        final Cheese a = new Cheese( "stilton",
                                     10 );
        final Cheese b = new Cheese( "stilton",
                                     15 );
        final Cheese c = new Cheese( "stilton",
                                     20 );
        session.insert( a );
        session.insert( b );
        session.insert( c );

        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_DynamicRules.drl" ) ) );
        final Package pkg = builder.getPackage();
        ruleBase.addPackage( SerializationHelper.serializeObject( pkg ) );
        ruleBase = SerializationHelper.serializeObject( ruleBase );
        session = SerializationHelper.getSerialisedStatefulSession( session,
                                                                    ruleBase );

        session.fireAllRules();
    }

    public void testDynamicRules2() throws Exception {
        RuleBase ruleBase = getRuleBase();
        StatefulSession session = ruleBase.newStatefulSession();

        // Assert some simple facts
        final FactA a = new FactA( "hello",
                                   new Integer( 1 ),
                                   new Float( 3.14 ) );
        final FactB b = new FactB( "hello",
                                   new Integer( 2 ),
                                   new Float( 6.28 ) );
        session.insert( a );
        session.insert( b );

        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_DynamicRules2.drl" ) ) );
        final Package pkg = SerializationHelper.serializeObject( builder.getPackage() );
        ruleBase.addPackage( pkg );
        ruleBase = SerializationHelper.serializeObject( ruleBase );
        session = SerializationHelper.getSerialisedStatefulSession( session,
                                                                    ruleBase );

        session.fireAllRules();
    }

    public void testRuleBaseAddRemove() throws Exception {
        RuleBase ruleBase = RuleBaseFactory.newRuleBase();

        //add and remove
        PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_Dynamic1.drl" ) ) );
        Package pkg = SerializationHelper.serializeObject( builder.getPackage() );
        ruleBase.addPackage( pkg );
        ruleBase.removePackage( pkg.getName() );
        ruleBase = SerializationHelper.serializeObject( ruleBase );

        //add and remove again
        builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_Dynamic1.drl" ) ) );
        pkg = SerializationHelper.serializeObject( builder.getPackage() );
        ruleBase.addPackage( pkg );
        ruleBase = SerializationHelper.serializeObject( ruleBase );
        ruleBase.removePackage( pkg.getName() );
    }

    public void testClassLoaderSwitchsUsingConf() throws Exception {
        try {
            // Creates first class loader and use it to load fact classes
            ClassLoader loader1 = new SubvertedClassLoader( new URL[]{getClass().getResource( "/" )},
                                                            this.getClass().getClassLoader() );
            Class cheeseClass = loader1.loadClass( "org.drools.Cheese" );

            PackageBuilderConfiguration conf = new PackageBuilderConfiguration( loader1 );
            PackageBuilder builder = new PackageBuilder( conf );
            builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_Dynamic1.drl" ) ) );

            // must set the classloader for rulebase conf too
            RuleBaseConfiguration rbconf = new RuleBaseConfiguration( loader1 );
            RuleBase ruleBase = RuleBaseFactory.newRuleBase( rbconf );
            Package pkg = SerializationHelper.serializeObject( builder.getPackage() );
            ruleBase.addPackage( pkg );
            //            ruleBase    = SerializationHelper.serializeObject(ruleBase);

            StatefulSession wm = ruleBase.newStatefulSession();
            wm.insert( cheeseClass.newInstance() );
            wm.fireAllRules();

            // Creates second class loader and use it to load fact classes
            ClassLoader loader2 = new SubvertedClassLoader( new URL[]{getClass().getResource( "/" )},
                                                            this.getClass().getClassLoader() );
            cheeseClass = loader2.loadClass( "org.drools.Cheese" );

            conf = new PackageBuilderConfiguration( loader2 );
            builder = new PackageBuilder( conf );
            builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_Dynamic1.drl" ) ) );

            rbconf = new RuleBaseConfiguration( loader2 );
            ruleBase = RuleBaseFactory.newRuleBase( rbconf );
            pkg = SerializationHelper.serializeObject( builder.getPackage() );
            ruleBase.addPackage( pkg );
            //            ruleBase    = SerializationHelper.serializeObject(ruleBase);

            wm = ruleBase.newStatefulSession();
            wm.insert( cheeseClass.newInstance() );
            wm.fireAllRules();
        } catch ( ClassCastException cce ) {
            cce.printStackTrace();
            fail( "No ClassCastException should be raised." );
        }

    }

    public void testClassLoaderSwitchsUsingContext() throws Exception {
        try {
            // Creates first class loader and use it to load fact classes
            ClassLoader original = Thread.currentThread().getContextClassLoader();
            ClassLoader loader1 = new SubvertedClassLoader( new URL[]{getClass().getResource( "/" )},
                                                            this.getClass().getClassLoader() );
            Thread.currentThread().setContextClassLoader( loader1 );
            Class cheeseClass = loader1.loadClass( "org.drools.Cheese" );

            PackageBuilder builder = new PackageBuilder();
            builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_Dynamic1.drl" ) ) );

            RuleBase ruleBase = RuleBaseFactory.newRuleBase();
            Package pkg = SerializationHelper.serializeObject( builder.getPackage() );
            ruleBase.addPackage( pkg );

            StatefulSession wm = ruleBase.newStatefulSession();
            wm.insert( cheeseClass.newInstance() );
            wm.fireAllRules();

            // Creates second class loader and use it to load fact classes
            ClassLoader loader2 = new SubvertedClassLoader( new URL[]{getClass().getResource( "/" )},
                                                            this.getClass().getClassLoader() );
            Thread.currentThread().setContextClassLoader( loader2 );
            cheeseClass = loader2.loadClass( "org.drools.Cheese" );

            builder = new PackageBuilder();
            builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_Dynamic1.drl" ) ) );

            ruleBase = RuleBaseFactory.newRuleBase();
            pkg = SerializationHelper.serializeObject( builder.getPackage() );
            ruleBase.addPackage( pkg );

            wm = ruleBase.newStatefulSession();
            wm.insert( cheeseClass.newInstance() );
            wm.fireAllRules();

            Thread.currentThread().setContextClassLoader( original );
        } catch ( ClassCastException cce ) {
            cce.printStackTrace();
            fail( "No ClassCastException should be raised." );
        }
    }

    public void testCollectDynamicRules() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_CollectDynamicRules1.drl" ) ) );
        final Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        final WorkingMemory workingMemory = ruleBase.newStatefulSession();
        List list = new ArrayList();
        workingMemory.setGlobal( "results",
                                 list );

        workingMemory.insert( new Cheese( "stilton",
                                          10 ) );
        workingMemory.insert( new Cheese( "brie",
                                          10 ) );
        workingMemory.insert( new Cheese( "stilton",
                                          10 ) );
        workingMemory.insert( new Cheese( "muzzarela",
                                          10 ) );

        final PackageBuilder builder2 = new PackageBuilder();
        builder2.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_CollectDynamicRules2.drl" ) ) );
        final Package pkg2 = builder2.getPackage();
        ruleBase.addPackage( pkg2 );
        workingMemory.fireAllRules();

        ruleBase = SerializationHelper.serializeObject( ruleBase );

        // fire all rules is automatic
        assertEquals( 1,
                      list.size() );
        assertEquals( 2,
                      ((List) list.get( 0 )).size() );

    }

    public void testDynamicNotNode() throws Exception {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newClassPathResource( "test_CollectDynamicRules1.drl",
                                                            getClass() ),
                      ResourceType.DRL );
        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        Collection<KnowledgePackage> kpkgs = SerializationHelper.serializeObject( kbuilder.getKnowledgePackages() );
        kbase.addKnowledgePackages( kpkgs );
        kbase = SerializationHelper.serializeObject( kbase );

        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        List results = new ArrayList();
        ksession.setGlobal( "results",
                            results );

        final Cheese a = new Cheese( "stilton",
                                     10 );
        final Cheese b = new Cheese( "stilton",
                                     15 );
        final Cheese c = new Cheese( "stilton",
                                     20 );
        ksession.insert( a );
        ksession.insert( b );
        ksession.insert( c );

        kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newClassPathResource( "test_DynamicNotNode.drl",
                                                            getClass() ),
                      ResourceType.DRL );
        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }
        kpkgs = SerializationHelper.serializeObject( kbuilder.getKnowledgePackages() );
        kbase.addKnowledgePackages( kpkgs );
        kbase = SerializationHelper.serializeObject( kbase );

        ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession( ksession,
                                                                              MarshallerFactory.newIdentityMarshallingStrategy(),
                                                                              false );

        results = (List) ksession.getGlobal( "results" );

        ksession.fireAllRules();

        assertEquals( 0,
                      results.size() );

        kbase.removeKnowledgePackage( "org.drools" );

        ksession.retract( ksession.getFactHandle( b ) );

        kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newClassPathResource( "test_DynamicNotNode.drl",
                                                            getClass() ),
                      ResourceType.DRL );
        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }
        kpkgs = SerializationHelper.serializeObject( kbuilder.getKnowledgePackages() );
        kbase.addKnowledgePackages( kpkgs );
        kbase = SerializationHelper.serializeObject( kbase );

        ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession( ksession,
                                                                              MarshallerFactory.newIdentityMarshallingStrategy(),
                                                                              false );

        results = (List) ksession.getGlobal( "results" );
        ksession.fireAllRules();

        assertEquals( 1,
                      results.size() );
    }

    public void testDynamicRulesAddRemove() {
        try {
            RuleBase ruleBase = RuleBaseFactory.newRuleBase();

            PackageBuilder tomBuilder = new PackageBuilder();
            tomBuilder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_DynamicRulesTom.drl" ) ) );
            ruleBase.addPackage( tomBuilder.getPackage() );

            StatefulSession session = ruleBase.newStatefulSession();
            List results = new ArrayList();
            session.setGlobal( "results",
                               results );

            InternalFactHandle h1 = (InternalFactHandle) session.insert( new Person( "tom",
                                                                                     1 ) );
            InternalFactHandle h2 = (InternalFactHandle) session.insert( new Person( "fred",
                                                                                     2 ) );
            InternalFactHandle h3 = (InternalFactHandle) session.insert( new Person( "harry",
                                                                                     3 ) );
            InternalFactHandle h4 = (InternalFactHandle) session.insert( new Person( "fred",
                                                                                     4 ) );
            InternalFactHandle h5 = (InternalFactHandle) session.insert( new Person( "ed",
                                                                                     5 ) );
            InternalFactHandle h6 = (InternalFactHandle) session.insert( new Person( "tom",
                                                                                     6 ) );
            InternalFactHandle h7 = (InternalFactHandle) session.insert( new Person( "sreeni",
                                                                                     7 ) );
            InternalFactHandle h8 = (InternalFactHandle) session.insert( new Person( "jill",
                                                                                     8 ) );
            InternalFactHandle h9 = (InternalFactHandle) session.insert( new Person( "ed",
                                                                                     9 ) );
            InternalFactHandle h10 = (InternalFactHandle) session.insert( new Person( "tom",
                                                                                      10 ) );

            session.fireAllRules();

            assertEquals( 3,
                          results.size() );
            assertTrue( results.contains( h1.getObject() ) );
            assertTrue( results.contains( h6.getObject() ) );
            assertTrue( results.contains( h10.getObject() ) );
            results.clear();

            PackageBuilder fredBuilder = new PackageBuilder();
            fredBuilder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_DynamicRulesFred.drl" ) ) );
            ruleBase.addPackage( fredBuilder.getPackage() );
            session.fireAllRules();

            assertEquals( 2,
                          results.size() );
            assertTrue( results.contains( h2.getObject() ) );
            assertTrue( results.contains( h4.getObject() ) );
            results.clear();

            ruleBase.removePackage( "tom" );

            PackageBuilder edBuilder = new PackageBuilder();
            edBuilder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_DynamicRulesEd.drl" ) ) );
            ruleBase.addPackage( edBuilder.getPackage() );
            session.fireAllRules();

            assertEquals( 2,
                          results.size() );
            assertTrue( results.contains( h5.getObject() ) );
            assertTrue( results.contains( h9.getObject() ) );
            results.clear();

            ((Person) h3.getObject()).setName( "ed" );
            session.update( h3,
                            h3.getObject() );
            session.fireAllRules();

            assertEquals( 1,
                          results.size() );
            assertTrue( results.contains( h3.getObject() ) );
        } catch ( Exception e ) {
            e.printStackTrace();
            fail( "Should not raise any exception: " + e.getMessage() );
        }
    }

    public void testDynamicRuleRemovalsSubNetwork() throws Exception {

        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_DynamicRulesWithSubnetwork1.drl" ) ) );
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_DynamicRulesWithSubnetwork.drl" ) ) );
        final Package pkg = SerializationHelper.serializeObject( builder.getPackage() );

        org.drools.reteoo.ReteooRuleBase reteooRuleBase = null;
        final RuleBase ruleBase = getRuleBase();
        reteooRuleBase = (org.drools.reteoo.ReteooRuleBase) ruleBase;
        ruleBase.addPackage( pkg );
        final PackageBuilder builder2 = new PackageBuilder();
        builder2.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_DynamicRulesWithSubnetwork2.drl" ) ) );
        ruleBase.addPackage( SerializationHelper.serializeObject( builder2.getPackage() ) );

        final StatefulSession session = ruleBase.newStatefulSession();

        final List list = new ArrayList();
        session.setGlobal( "results",
                           list );

        Order order = new Order();

        OrderItem item1 = new OrderItem( order,
                                         1,
                                         "Adventure Guide Brazil",
                                         OrderItem.TYPE_BOOK,
                                         24 );
        order.addItem( item1 );
        session.insert( item1 );

        OrderItem item2 = new OrderItem( order,
                                         2,
                                         "Prehistoric Britain",
                                         OrderItem.TYPE_BOOK,
                                         15 );
        order.addItem( item2 );
        session.insert( item2 );

        OrderItem item3 = new OrderItem( order,
                                         3,
                                         "Holiday Music",
                                         OrderItem.TYPE_CD,
                                         9 );
        order.addItem( item3 );
        session.insert( item3 );

        OrderItem item4 = new OrderItem( order,
                                         4,
                                         "Very Best of Mick Jagger",
                                         OrderItem.TYPE_CD,
                                         11 );
        order.addItem( item4 );
        session.insert( item4 );

        session.insert( order );

        assertEquals( 11,
                      session.getAgenda().getActivations().length );

        reteooRuleBase.removeRule( "org.drools",
                                   "Apply Discount on all books" );
        assertEquals( 10,
                      session.getAgenda().getActivations().length );

        reteooRuleBase.removeRule( "org.drools",
                                   "like book" );

        final OrderItem item5 = new OrderItem( order,
                                               5,
                                               "Sinatra : Vegas",
                                               OrderItem.TYPE_CD,
                                               5 );
        assertEquals( 8,
                      session.getAgenda().getActivations().length );

        session.insert( item5 );

        assertEquals( 10,
                      session.getAgenda().getActivations().length );

        reteooRuleBase.removePackage( "org.drools" );

        assertEquals( 0,
                      session.getAgenda().getActivations().length );
    }

    public void testDynamicRuleRemovalsUnusedWorkingMemorySubNetwork() throws Exception {

        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_DynamicRulesWithSubnetwork1.drl" ) ) );
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_DynamicRulesWithSubnetwork2.drl" ) ) );
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_DynamicRulesWithSubnetwork.drl" ) ) );
        final Package pkg = builder.getPackage();

        org.drools.reteoo.ReteooRuleBase reteooRuleBase = null;

        final RuleBase ruleBase = getRuleBase();
        reteooRuleBase = (org.drools.reteoo.ReteooRuleBase) ruleBase;

        ruleBase.addPackage( pkg );

        final WorkingMemory workingMemory = ruleBase.newStatefulSession();

        if ( reteooRuleBase != null ) {
            assertEquals( 1,
                          reteooRuleBase.getPackages().length );
            assertEquals( 4,
                          reteooRuleBase.getPackages()[0].getRules().length );

            reteooRuleBase.removeRule( "org.drools",
                                       "Apply Discount on all books" );
            assertEquals( 3,
                          reteooRuleBase.getPackages()[0].getRules().length );

            reteooRuleBase.removeRule( "org.drools",
                                       "like book" );
            assertEquals( 2,
                          reteooRuleBase.getPackages()[0].getRules().length );

            reteooRuleBase.removePackage( "org.drools" );
            assertEquals( 0,
                          reteooRuleBase.getPackages().length );
        }
    }

    public void testRemovePackageSubNetwork() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_DynamicRulesWithSubnetwork.drl" ) ) );

        final RuleBase ruleBase = getRuleBase();
        final String packageName = builder.getPackage().getName();
        ruleBase.addPackage( SerializationHelper.serializeObject( builder.getPackage() ) );

        final WorkingMemory workingMemory = ruleBase.newStatefulSession();
        List results = new ArrayList();
        workingMemory.setGlobal( "results",
                                 results );

        Order order = new Order();

        OrderItem item1 = new OrderItem( order,
                                         1,
                                         "Adventure Guide Brazil",
                                         OrderItem.TYPE_BOOK,
                                         24 );
        OrderItem item2 = new OrderItem( order,
                                         2,
                                         "Prehistoric Britain",
                                         OrderItem.TYPE_BOOK,
                                         15 );
        OrderItem item3 = new OrderItem( order,
                                         3,
                                         "Holiday Music",
                                         OrderItem.TYPE_CD,
                                         9 );
        OrderItem item4 = new OrderItem( order,
                                         4,
                                         "Very Best of Mick Jagger",
                                         OrderItem.TYPE_CD,
                                         11 );
        OrderItem item5 = new OrderItem( order,
                                         5,
                                         "The Master and Margarita",
                                         OrderItem.TYPE_BOOK,
                                         29 );

        order.addItem( item1 );
        order.addItem( item2 );
        order.addItem( item3 );
        order.addItem( item4 );
        order.addItem( item5 );

        workingMemory.insert( order );
        workingMemory.fireAllRules();
        assertEquals( 1,
                      results.size() );
        assertEquals( 3,
                      ((List) results.get( 0 )).size() );
        results.clear();

        final RuleBase ruleBaseWM = workingMemory.getRuleBase();
        ruleBaseWM.removePackage( packageName );
        final PackageBuilder builder1 = new PackageBuilder();
        builder1.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_DynamicRulesWithSubnetwork.drl" ) ) );
        ruleBaseWM.addPackage( SerializationHelper.serializeObject( builder1.getPackage() ) );
        workingMemory.fireAllRules();
        results = (List) workingMemory.getGlobal( "results" );
        assertEquals( 1,
                      results.size() );
        assertEquals( 3,
                      ((List) results.get( 0 )).size() );
        results.clear();

        ruleBaseWM.removePackage( packageName );
        ruleBaseWM.addPackage( SerializationHelper.serializeObject( builder1.getPackage() ) );
        workingMemory.fireAllRules();
        assertEquals( 1,
                      results.size() );
        assertEquals( 3,
                      ((List) results.get( 0 )).size() );
        results.clear();

        ruleBaseWM.removePackage( packageName );
        ruleBaseWM.addPackage( SerializationHelper.serializeObject( builder1.getPackage() ) );
        workingMemory.fireAllRules();
        assertEquals( 1,
                      results.size() );
        assertEquals( 3,
                      ((List) results.get( 0 )).size() );
        results.clear();
    }

    public void testRuleBaseAddRemoveSubNetworks() throws Exception {
        try {
            RuleBase ruleBase = RuleBaseFactory.newRuleBase();

            //add and remove
            PackageBuilder builder = new PackageBuilder();
            builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_DynamicRulesWithSubnetwork.drl" ) ) );
            Package pkg = SerializationHelper.serializeObject( builder.getPackage() );
            ruleBase.addPackage( pkg );
            ruleBase.removePackage( pkg.getName() );

            //add and remove again
            builder = new PackageBuilder();
            builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_DynamicRulesWithSubnetwork.drl" ) ) );
            pkg = SerializationHelper.serializeObject( builder.getPackage() );
            ruleBase.addPackage( pkg );
            ruleBase.removePackage( pkg.getName() );
        } catch ( Exception e ) {
            e.printStackTrace();
            fail( "Should not raise any exception" );
        }
    }

    public void testDynamicRuleAdditionsWithEntryPoints() throws Exception {
        Reader reader = new InputStreamReader( getClass().getResourceAsStream( "test_DynamicWithEntryPoint.drl" ) );
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newReaderResource( reader ),
                      ResourceType.DRL );

        assertFalse( kbuilder.getErrors().toString(),
                     kbuilder.hasErrors() );

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        // now lets add some knowledge to the kbase
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        List<StockTick> results = new ArrayList<StockTick>();
        ksession.setGlobal( "results",
                            results );

        WorkingMemoryEntryPoint ep = ksession.getWorkingMemoryEntryPoint( "in-channel" );
        ep.insert( new StockTick( 1,
                                  "RHT",
                                  20,
                                  10000 ) );
        ep.insert( new StockTick( 2,
                                  "RHT",
                                  21,
                                  15000 ) );
        ep.insert( new StockTick( 3,
                                  "RHT",
                                  22,
                                  20000 ) );

        ksession.fireAllRules();
        assertEquals( 3,
                      results.size() );

    }

    public void testIsolatedClassLoaderWithEnumsPkgBuilder() throws Exception {
        try {
            // Creates first class loader and use it to load fact classes
            ClassLoader loader1 = new SubvertedClassLoader( new URL[]{getClass().getResource( "/testEnum.jar" )},
                                                            this.getClass().getClassLoader() );

            // create a builder with the given classloader
            KnowledgeBuilderConfiguration conf = KnowledgeBuilderFactory.newKnowledgeBuilderConfiguration( null,
                                                                                                           loader1 );
            KnowledgeBuilder builder = KnowledgeBuilderFactory.newKnowledgeBuilder( conf );
            builder.add( ResourceFactory.newInputStreamResource( getClass().getResourceAsStream( "test_EnumSerialization.drl" ) ),
                         ResourceType.DRL );
            Collection<KnowledgePackage> pkgs = builder.getKnowledgePackages();
            KnowledgePackage pkg = pkgs.iterator().next();
            
            // serialize out
            byte[] out = DroolsStreamUtils.streamOut( ((KnowledgePackageImp) pkg).pkg );

            // adding original packages to a kbase just to make sure they are fine
            KnowledgeBaseConfiguration kbaseConf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration( null, loader1 );
            KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase( kbaseConf );
            kbase.addKnowledgePackages( pkgs );
            
            // now, create another classloader and make sure it has access to the classes
            ClassLoader loader2 = new SubvertedClassLoader( new URL[]{getClass().getResource( "/testEnum.jar" )},
                                                            this.getClass().getClassLoader() );
            
            // create another builder
            KnowledgeBuilderConfiguration conf2 = KnowledgeBuilderFactory.newKnowledgeBuilderConfiguration( null,
                                                                                                            loader2 );
            KnowledgeBuilder builder2 = KnowledgeBuilderFactory.newKnowledgeBuilder( conf2 );
            builder2.add( ResourceFactory.newByteArrayResource( out ),
                         ResourceType.PKG );
            Collection<KnowledgePackage> pkgs2 = builder2.getKnowledgePackages();
            
            // create another kbase
            KnowledgeBaseConfiguration kbaseConf2 = KnowledgeBaseFactory.newKnowledgeBaseConfiguration( null, loader2 );
            KnowledgeBase kbase2 = KnowledgeBaseFactory.newKnowledgeBase( kbaseConf2 );
            kbase2.addKnowledgePackages( pkgs2 );

        } catch ( ClassCastException cce ) {
            cce.printStackTrace();
            fail( "No ClassCastException should be raised." );
        }

    }

    public void testIsolatedClassLoaderWithEnumsContextClassloader() throws Exception {
        try {
            // Creates first class loader and use it to load fact classes
            ClassLoader loader1 = new SubvertedClassLoader( new URL[]{getClass().getResource( "/testEnum.jar" )},
                                                            this.getClass().getClassLoader() );
            //loader1.loadClass( "org.drools.Primitives" );
            //loader1.loadClass( "org.drools.TestEnum" );

            // create a builder with the given classloader
            KnowledgeBuilderConfiguration conf = KnowledgeBuilderFactory.newKnowledgeBuilderConfiguration( null,
                                                                                                           loader1 );
            KnowledgeBuilder builder = KnowledgeBuilderFactory.newKnowledgeBuilder( conf );
            builder.add( ResourceFactory.newInputStreamResource( getClass().getResourceAsStream( "test_EnumSerialization.drl" ) ),
                         ResourceType.DRL );
            Collection<KnowledgePackage> pkgs = builder.getKnowledgePackages();
            KnowledgePackage pkg = pkgs.iterator().next();
            
            // serialize out
            byte[] out = DroolsStreamUtils.streamOut( pkg );

            // adding original packages to a kbase just to make sure they are fine
            KnowledgeBaseConfiguration kbaseConf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration( null, loader1 );
            KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase( kbaseConf );
            kbase.addKnowledgePackages( pkgs );
            
            // now, create another classloader and make sure it has access to the classes
            ClassLoader loader2 = new SubvertedClassLoader( new URL[]{getClass().getResource( "/testEnum.jar" )},
                                                            this.getClass().getClassLoader() );
            //loader2.loadClass( "org.drools.Primitives" );
            //loader2.loadClass( "org.drools.TestEnum" );
            
            // set context classloader and use it
            ClassLoader ccl = Thread.currentThread().getContextClassLoader();
            Thread.currentThread().setContextClassLoader( loader2 );
            KnowledgePackage pkg2 = (KnowledgePackage) DroolsStreamUtils.streamIn( out );
            Collection<KnowledgePackage> pkgs2 = Collections.singleton( pkg2 );
            Thread.currentThread().setContextClassLoader( ccl );
            
            // create another kbase
            KnowledgeBaseConfiguration kbaseConf2 = KnowledgeBaseFactory.newKnowledgeBaseConfiguration( null, loader2 );
            KnowledgeBase kbase2 = KnowledgeBaseFactory.newKnowledgeBase( kbaseConf2 );
            kbase2.addKnowledgePackages( pkgs2 );

        } catch ( ClassCastException cce ) {
            cce.printStackTrace();
            fail( "No ClassCastException should be raised." );
        }

    }

    public class SubvertedClassLoader extends URLClassLoader {

        private static final long serialVersionUID = 400L;

        public SubvertedClassLoader(final URL[] urls,
                                    final ClassLoader parentClassLoader) {
            super( urls,
                   parentClassLoader );
        }

        protected synchronized Class loadClass(String name,
                                               boolean resolve) throws ClassNotFoundException {
            // First, check if the class has already been loaded
            Class c = findLoadedClass( name );
            if ( c == null ) {
                try {
                    c = findClass( name );
                } catch ( ClassNotFoundException e ) {
                    c = super.loadClass( name,
                                         resolve );
                }
            }
            return c;
        }
    }
}
