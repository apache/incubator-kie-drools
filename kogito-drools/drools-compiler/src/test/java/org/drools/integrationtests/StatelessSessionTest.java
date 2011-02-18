package org.drools.integrationtests;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import org.drools.Cheese;
import org.drools.Cheesery;
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.RuleBase;
import org.drools.RuleBaseConfiguration;
import org.drools.RuleBaseFactory;
import org.drools.StatelessSession;
import org.drools.StatelessSessionResult;
import org.drools.base.CopyIdentifiersGlobalExporter;
import org.drools.base.MapGlobalResolver;
import org.drools.base.ReferenceOriginalGlobalExporter;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.command.Command;
import org.drools.command.CommandFactory;
import org.drools.command.impl.GenericCommand;
import org.drools.command.runtime.BatchExecutionCommandImpl;
import org.drools.compiler.PackageBuilder;
import org.drools.definition.KnowledgePackage;
import org.drools.impl.StatefulKnowledgeSessionImpl;
import org.drools.io.Resource;
import org.drools.io.ResourceFactory;
import org.drools.rule.Package;
import org.drools.runtime.ExecutionResults;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.StatelessKnowledgeSession;
import org.drools.runtime.StatelessKnowledgeSessionResults;
import org.drools.runtime.rule.impl.FlatQueryResults;
import org.drools.spi.GlobalResolver;

public class StatelessSessionTest {
    final List list = new ArrayList();
    final Cheesery cheesery = new Cheesery();
    final GlobalResolver globalResolver = new MapGlobalResolver();

    protected RuleBase getRuleBase() throws Exception {

        return RuleBaseFactory.newRuleBase( RuleBase.RETEOO,
                                            null );
    }

    protected RuleBase getRuleBase(final RuleBaseConfiguration config) throws Exception {

        return RuleBaseFactory.newRuleBase( RuleBase.RETEOO,
                                            config );
    }

    @Test
    public void testSingleObjectAssert() throws Exception {
        StatelessKnowledgeSession session = getSession2( "literal_rule_test.drl" );

        final Cheese stilton = new Cheese( "stilton",
                                           5 );

        session.execute( stilton );

        assertEquals( "stilton",
                      list.get( 0 ) );
    }

    @Test
    public void testArrayObjectAssert() throws Exception {
        StatelessKnowledgeSession session = getSession2( "literal_rule_test.drl" );

        final Cheese stilton = new Cheese( "stilton",
                                           5 );

        session.execute( Arrays.asList( new Object[]{stilton} ) );

        assertEquals( "stilton",
                      list.get( 0 ) );
    }

    @Test
    public void testCollectionObjectAssert() throws Exception {
        StatelessKnowledgeSession session = getSession2( "literal_rule_test.drl" );

        final Cheese stilton = new Cheese( "stilton",
                                           5 );

        List collection = new ArrayList();
        collection.add( stilton );
        session.execute( collection );

        assertEquals( "stilton",
                      list.get( 0 ) );
    }
    
    @Test
    public void testInsertObject() throws Exception {
        String str = "";
        str += "package org.drools \n";
        str += "import org.drools.Cheese \n";
        str += "rule rule1 \n";
        str += "  when \n";
        str += "    $c : Cheese() \n";
        str += " \n";
        str += "  then \n";
        str += "    $c.setPrice( 30 ); \n";
        str += "end\n";
        
        Cheese stilton = new Cheese( "stilton", 5 );
        
        StatelessKnowledgeSession ksession = getSession2( ResourceFactory.newByteArrayResource( str.getBytes() ) );
        GenericCommand cmd = ( GenericCommand ) CommandFactory.newInsert( stilton, "outStilton" );
        BatchExecutionCommandImpl batch = new BatchExecutionCommandImpl(  Arrays.asList( new GenericCommand<?>[] { cmd } ) );
        
        ExecutionResults result = ( ExecutionResults ) ksession.execute( batch );
        stilton = ( Cheese ) result.getValue( "outStilton" );
        assertEquals( 30,
                      stilton.getPrice() );
    }
    
    @Test
    public void testSetGlobal() throws Exception {
        String str = "";
        str += "package org.drools \n";
        str += "import org.drools.Cheese \n";
        str += "global java.util.List list1 \n";
        str += "global java.util.List list2 \n";
        str += "global java.util.List list3 \n";
        str += "rule rule1 \n";
        str += "  when \n";
        str += "    $c : Cheese() \n";
        str += " \n";
        str += "  then \n";
        str += "    $c.setPrice( 30 ); \n";
        str += "    list1.add( $c ); \n";
        str += "    list2.add( $c ); \n";
        str += "    list3.add( $c ); \n";
        str += "end\n";
        
        Cheese stilton = new Cheese( "stilton", 5 );
        List list1 = new ArrayList();
        List list2 = new ArrayList();
        List list3 = new ArrayList();
        
        StatelessKnowledgeSession ksession = getSession2( ResourceFactory.newByteArrayResource( str.getBytes() ) );
        Command setGlobal1 = CommandFactory.newSetGlobal( "list1", list1 );
        Command setGlobal2 = CommandFactory.newSetGlobal( "list2", list2, true );
        Command setGlobal3 = CommandFactory.newSetGlobal( "list3", list3, "outList3" );
        Command insert = CommandFactory.newInsert( stilton  );
        
        List cmds = new ArrayList();
        cmds.add( setGlobal1 );
        cmds.add( setGlobal2 );
        cmds.add( setGlobal3 );
        cmds.add(  insert );
        
        ExecutionResults result = ( ExecutionResults ) ksession.execute( CommandFactory.newBatchExecution( cmds ) );
        
        assertEquals( 30,
                      stilton.getPrice() );
        
        assertNull( result.getValue( "list1" ) );
        
        list2 = ( List ) result.getValue( "list2" );
        assertEquals( 1, list2.size() );
        assertSame( stilton, list2.get( 0 ) );
        
          
        
        list3 = ( List ) result.getValue( "outList3" );
        assertEquals( 1, list3.size() );
        assertSame( stilton, list3.get( 0 ) );
    }
    
    @Test
    public void testQuery() throws Exception {
        String str = "";
        str += "package org.drools.test  \n";
        str += "import org.drools.Cheese \n";
        str += "query cheeses \n";
        str += "    stilton : Cheese(type == 'stilton') \n";
        str += "    cheddar : Cheese(type == 'cheddar', price == stilton.price) \n";
        str += "end\n";
        
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ), ResourceType.DRL );

        if  ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }
        
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );
        
        kbase = SerializationHelper.serializeObject( kbase );

        StatelessKnowledgeSession ksession = kbase.newStatelessKnowledgeSession();
        Cheese stilton1 = new Cheese( "stilton", 1);
        Cheese cheddar1 = new Cheese( "cheddar", 1);
        Cheese stilton2 = new Cheese( "stilton", 2);
        Cheese cheddar2 = new Cheese( "cheddar", 2);
        Cheese stilton3 = new Cheese( "stilton", 3);
        Cheese cheddar3 = new Cheese( "cheddar", 3);
        
        Set set = new HashSet();
        List list = new ArrayList();
        list.add(stilton1);
        list.add(cheddar1);
        set.add( list );
        
        list = new ArrayList();
        list.add(stilton2);
        list.add(cheddar2);
        set.add( list );
        
        list = new ArrayList();
        list.add(stilton3);
        list.add(cheddar3);
        set.add( list );
        
        List<Command> cmds = new ArrayList<Command>();
        cmds.add( CommandFactory.newInsert( stilton1 ) );
        cmds.add( CommandFactory.newInsert( stilton2 ) );
        cmds.add( CommandFactory.newInsert( stilton3 ) );
        cmds.add( CommandFactory.newInsert( cheddar1 ) );
        cmds.add( CommandFactory.newInsert( cheddar2 ) );
        cmds.add( CommandFactory.newInsert( cheddar3 ) );
        
        cmds.add(  CommandFactory.newQuery( "cheeses", "cheeses" ) );
        
        ExecutionResults batchResult = (ExecutionResults) ksession.execute( CommandFactory.newBatchExecution( cmds ) );
        
        org.drools.runtime.rule.QueryResults results = ( org.drools.runtime.rule.QueryResults) batchResult.getValue( "cheeses" );
        assertEquals( 3, results.size() );
        assertEquals( 2, results.getIdentifiers().length );
        Set newSet = new HashSet();
        for ( org.drools.runtime.rule.QueryResultsRow result : results ) {
            list = new ArrayList();
            list.add( result.get( "stilton" ) );
            list.add( result.get( "cheddar" ));
            newSet.add( list );
        }
        assertEquals( set, newSet );
    }
    
    // @TODO need to figure out if we need to support "out" params 
//    public void testInAndOutParams() throws Exception {
//        StatelessKnowledgeSession session = getSession2( "testStatelessKnowledgeSessionInOutParams.drl" );
//
//
//        final Cheese stilton = new Cheese( "stilton",
//                                           5 );
//        
//        final Cheese cheddar = new Cheese( "cheddar",
//                                           25 );
//        
//        // notice I don't export Cheessery
//        Parameters parameters = session.newParameters();
//        Map<String, Object> globalsIn = new HashMap<String, Object>();
//        globalsIn.put( "inString", "string" );
//        parameters.getGlobalParams().setIn( globalsIn );
//        parameters.getGlobalParams().setOut( Arrays.asList(  new String[]{"list"} ) );
//        
//        Map<String, Object> factIn = new HashMap<String, Object>();
//        factIn.put( "inCheese", cheddar );
//        parameters.getFactParams().setIn( factIn );
//        parameters.getFactParams().setOut( Arrays.asList(  new String[]{ "outCheese"} ) );
// 
//        StatelessKnowledgeSessionResults results = session.executeObjectWithParameters( stilton,
//                                                                                        parameters );
//
//        assertEquals( 2, results.getIdentifiers().size() );
//        assertTrue( results.getIdentifiers().contains( "list" ));
//        assertTrue( results.getIdentifiers().contains( "outCheese" ));
//        
//        assertEquals( new Cheese( "brie", 50), results.getValue( "outCheese" ) );
//
//        assertEquals( "rule1 cheddar",
//                      ((List) results.getValue( "list" )).get( 0 ) );
//        
//        assertEquals( "rule2 stilton",
//                      ((List) results.getValue( "list" )).get( 1 ) );
//        
//        assertEquals( "rule3 brie",
//                      ((List) results.getValue( "list" )).get( 2 ) );
//        
//        assertEquals( "rule4 string",
//                      ((List) results.getValue( "list" )).get( 3 ) );
//
//        // cheesery should be null
//        assertNull( results.getValue( "cheesery" ) );
//        
//    }
//    
//    public void testInOutAndOutParams() throws Exception {
//        StatelessKnowledgeSession session = getSession2( "testStatelessKnowledgeSessionInOutParams.drl" );
//
//
//        final Cheese stilton = new Cheese( "stilton",
//                                           5 );
//        
//        final Cheese cheddar = new Cheese( "cheddar",
//                                           25 );
//        
//        // notice I don't export Cheessery
//        Parameters parameters = session.newParameters();
//        Map<String, Object> globalsInOut = new HashMap<String, Object>();
//        globalsInOut.put( "inString", "string" );
//        parameters.getGlobalParams().setInOut( globalsInOut );
//        parameters.getGlobalParams().setOut( Arrays.asList(  new String[]{"list"} ) );
//        
//        Map<String, Object> factInOut = new HashMap<String, Object>();
//        factInOut.put( "inCheese", cheddar );
//        parameters.getFactParams().setInOut( factInOut );
//        parameters.getFactParams().setOut( Arrays.asList(  new String[]{ "outCheese"} ) );
// 
//        StatelessKnowledgeSessionResults results = session.executeObjectWithParameters( stilton,
//                                                                                        parameters );
//
//        assertEquals( 4, results.getIdentifiers().size() );
//        assertTrue( results.getIdentifiers().contains( "list" ));
//        assertTrue( results.getIdentifiers().contains( "inString" ));
//        assertTrue( results.getIdentifiers().contains( "inCheese" ));
//        assertTrue( results.getIdentifiers().contains( "outCheese" ));
//        
//        assertEquals( new Cheese( "brie", 50), results.getValue( "outCheese" ) );
//
//        assertEquals( "rule1 cheddar",
//                      ((List) results.getValue( "list" )).get( 0 ) );
//        
//        assertEquals( "rule2 stilton",
//                      ((List) results.getValue( "list" )).get( 1 ) );
//        
//        assertEquals( "rule3 brie",
//                      ((List) results.getValue( "list" )).get( 2 ) );
//        
//        assertEquals( "rule4 string",
//                      ((List) results.getValue( "list" )).get( 3 ) );
//
//        // cheesery should be null
//        assertNull( results.getValue( "cheesery" ) );
//        
//    }
    
    @Test
    public void testAsynSingleOjbectcAssert() throws Exception {
        StatelessSession session = getSession();

        final Cheese stilton = new Cheese( "stilton",
                                           5 );

        session.asyncExecute( stilton );

        Thread.sleep( 300 );

        assertEquals( "stilton",
                      list.get( 0 ) );
    }

    @Test
    public void testAsynArrayOjbectcAssert() throws Exception {
        StatelessSession session = getSession();

        final Cheese stilton = new Cheese( "stilton",
                                           5 );

        session.asyncExecute( new Object[]{stilton} );

        Thread.sleep( 100 );

        assertEquals( "stilton",
                      list.get( 0 ) );
    }

    @Test
    public void testAsynCollectionOjbectcAssert() throws Exception {
        StatelessSession session = getSession();

        final Cheese stilton = new Cheese( "stilton",
                                           5 );

        List collection = new ArrayList();
        collection.add( stilton );
        session.execute( collection );

        Thread.sleep( 100 );

        assertEquals( "stilton",
                      list.get( 0 ) );
    }
    
    @Test
    public void testCopyIdentifierGlobalExporterOneValue() throws Exception {
        StatelessSession session = getSession();

        // notice I don't export Cheessery
        session.setGlobalExporter( new CopyIdentifiersGlobalExporter( new String[]{"list"} ) );

        StatelessSessionResult result = session.executeWithResults( (Object) null );

        assertSame( this.list,
                    result.getGlobal( "list" ) );

        // cheesery should be null
        assertNull( result.getGlobal( "cheesery" ) );
        
        assertNotSame( this.globalResolver, result.getGlobalResolver() );
    }
    
    @Test
    public void testCopyIdentifierGlobalExporterTwoValues() throws Exception {
        StatelessSession session = getSession();

        session.setGlobalExporter( new CopyIdentifiersGlobalExporter( new String[]{"list", "cheesery"} ) );

        StatelessSessionResult result = session.executeWithResults( (Object) null );

        assertSame( this.list,
                    result.getGlobal( "list" ) );

        // cheesery should be null
        assertSame( this.cheesery,
                    result.getGlobal( "cheesery" ) );
        
        assertNotSame( this.globalResolver, result.getGlobalResolver() );
    }
    
    @Test
    public void testCopyIdentifierGlobalExporterAllValues() throws Exception {
        StatelessSession session = getSession();

        // I've not specified any identifiers, so it should do them alll
        session.setGlobalExporter( new CopyIdentifiersGlobalExporter() );

        StatelessSessionResult result = session.executeWithResults( (Object) null );

        assertSame( this.list,
                    result.getGlobal( "list" ) );

        // cheesery should be null
        assertSame( this.cheesery,
                    result.getGlobal( "cheesery" ) );
        
        assertNotSame( this.globalResolver, result.getGlobalResolver() );
    }
    
    @Test
    public void testReferenceOriginalGlobalExporter() throws Exception {
        StatelessSession session = getSession();

        // I've not specified any identifiers, so it should do them alll
        session.setGlobalExporter( new ReferenceOriginalGlobalExporter() );

        StatelessSessionResult result = session.executeWithResults( (Object) null );

        assertSame( this.list,
                    result.getGlobal( "list" ) );

        // cheesery should be null
        assertSame( this.cheesery,
                    result.getGlobal( "cheesery" ) );
        
        assertSame( this.globalResolver, result.getGlobalResolver() );
    }

    private StatelessSession getSession() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "literal_rule_test.drl" ) ) );
        final Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase    = SerializationHelper.serializeObject(ruleBase);
        StatelessSession session = ruleBase.newStatelessSession();
        
        session    = SerializationHelper.serializeObject(session);
        session.setGlobalResolver( this.globalResolver );

        session.setGlobal( "list",
                           this.list );
        session.setGlobal( "cheesery",
                           this.cheesery );
        return session;
    }
    
    private StatelessKnowledgeSession getSession2(String fileName) throws Exception {
        return getSession2( ResourceFactory.newClassPathResource( fileName, getClass() ) );
    }
        
    private StatelessKnowledgeSession getSession2(Resource resource) throws Exception {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( resource, ResourceType.DRL );
        
        if (kbuilder.hasErrors() ) {
            System.out.println( kbuilder.getErrors() );
        }
        
        assertFalse( kbuilder.hasErrors() );
        Collection<KnowledgePackage> pkgs = kbuilder.getKnowledgePackages();

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        
       
        kbase.addKnowledgePackages( pkgs );
        kbase    = SerializationHelper.serializeObject( kbase );
        StatelessKnowledgeSession session = kbase.newStatelessKnowledgeSession();

        session.setGlobal( "list",
                           this.list );
        session.setGlobal( "cheesery",
                           this.cheesery );
        return session;
    }
}
