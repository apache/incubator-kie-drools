/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.compiler.integrationtests;

import org.drools.compiler.Cheese;
import org.drools.compiler.Cheesery;
import org.drools.compiler.CommonTestMethodBase;
import org.drools.core.command.impl.ExecutableCommand;
import org.drools.core.command.runtime.BatchExecutionCommandImpl;
import org.junit.Test;
import org.kie.api.command.Command;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.Channel;
import org.kie.api.runtime.ExecutionResults;
import org.kie.api.runtime.StatelessKieSession;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.KnowledgeBaseFactory;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.command.CommandFactory;
import org.kie.internal.definition.KnowledgePackage;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.runtime.StatelessKnowledgeSession;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class StatelessSessionTest extends CommonTestMethodBase {
    final List list = new ArrayList();
    final Cheesery cheesery = new Cheesery();

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
        str += "package org.kie \n";
        str += "import org.drools.compiler.Cheese \n";
        str += "rule rule1 \n";
        str += "  when \n";
        str += "    $c : Cheese() \n";
        str += " \n";
        str += "  then \n";
        str += "    $c.setPrice( 30 ); \n";
        str += "end\n";
        
        Cheese stilton = new Cheese( "stilton", 5 );
        
        StatelessKnowledgeSession ksession = getSession2( ResourceFactory.newByteArrayResource( str.getBytes() ) );
        ExecutableCommand cmd = (ExecutableCommand) CommandFactory.newInsert( stilton, "outStilton" );
        BatchExecutionCommandImpl batch = new BatchExecutionCommandImpl(  Arrays.asList( new ExecutableCommand<?>[] { cmd } ) );
        
        ExecutionResults result = ( ExecutionResults ) ksession.execute( batch );
        stilton = ( Cheese ) result.getValue( "outStilton" );
        assertEquals( 30,
                      stilton.getPrice() );
    }
    
    @Test
    public void testSetGlobal() throws Exception {
        String str = "";
        str += "package org.kie \n";
        str += "import org.drools.compiler.Cheese \n";
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
        str += "package org.kie.test  \n";
        str += "import org.drools.compiler.Cheese \n";
        str += "query cheeses \n";
        str += "    stilton : Cheese(type == 'stilton') \n";
        str += "    cheddar : Cheese(type == 'cheddar', price == stilton.price) \n";
        str += "end\n";
        
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource(str.getBytes()), ResourceType.DRL );

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
        
        org.kie.api.runtime.rule.QueryResults results = ( org.kie.api.runtime.rule.QueryResults) batchResult.getValue( "cheeses" );
        assertEquals( 3, results.size() );
        assertEquals( 2, results.getIdentifiers().length );
        Set newSet = new HashSet();
        for ( org.kie.api.runtime.rule.QueryResultsRow result : results ) {
            list = new ArrayList();
            list.add( result.get( "stilton" ) );
            list.add( result.get( "cheddar" ));
            newSet.add( list );
        }
        assertEquals( set, newSet );
    }
    
    @Test
    public void testChannels() throws Exception {
        String str = "";
        str += "package org.kie \n";
        str += "import org.drools.compiler.Cheese \n";
        str += "rule rule1 \n";
        str += "  when \n";
        str += "    $c : Cheese() \n";
        str += " \n";
        str += "  then \n";
        str += "    channels[\"x\"].send( $c ); \n";
        str += "end\n";
        
        Cheese stilton = new Cheese( "stilton", 5 );
        Channel channel = Mockito.mock( Channel.class );
        
        StatelessKieSession ksession = getSession2( ResourceFactory.newByteArrayResource( str.getBytes() ) );
        ksession.registerChannel( "x", channel );
        
        assertEquals( 1, ksession.getChannels().size() );
        assertEquals( channel, ksession.getChannels().get( "x" ) );
        
        ksession.execute( stilton );
        
        Mockito.verify( channel ).send( stilton );
        
        ksession.unregisterChannel( "x" );
        
        assertEquals( 0, ksession.getChannels().size() );
        assertNull( ksession.getChannels().get( "x" ) );
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
