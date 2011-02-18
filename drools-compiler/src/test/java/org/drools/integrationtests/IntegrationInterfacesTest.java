package org.drools.integrationtests;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import org.drools.Cheese;
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.io.ResourceFactory;
import org.drools.runtime.Channel;
import org.drools.runtime.StatefulKnowledgeSession;
import org.mockito.exceptions.verification.NeverWantedButInvoked;

public class IntegrationInterfacesTest {

    private KnowledgeBase getKnowledgeBase(final String resourceName) throws IOException,
                                                                     ClassNotFoundException {
        final KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newClassPathResource( resourceName,
                                                            getClass() ),
                      ResourceType.DRL );

        assertFalse( kbuilder.getErrors().toString(),
                     kbuilder.hasErrors() );

        KnowledgeBase kbase = getKnowledgeBase( kbuilder );

        return kbase;
    }

    private KnowledgeBase getKnowledgeBase(final Reader[] readers) throws IOException,
                                                                  ClassNotFoundException {
        final KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        for ( Reader reader : readers ) {
            kbuilder.add( ResourceFactory.newReaderResource( reader ),
                          ResourceType.DRL );
        }
        assertFalse( kbuilder.getErrors().toString(),
                     kbuilder.hasErrors() );
        KnowledgeBase kbase = getKnowledgeBase( kbuilder );
        return kbase;
    }

    private KnowledgeBase getKnowledgeBase(final KnowledgeBuilder kbuilder) throws IOException,
                                                                           ClassNotFoundException {
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );
        kbase = SerializationHelper.serializeObject( kbase );
        return kbase;
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testGlobals() throws Exception {
        final KnowledgeBase kbase = getKnowledgeBase( "globals_rule_test.drl" );
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        final List<Object> list = mock( List.class );
        ksession.setGlobal( "list",
                            list );
        ksession.setGlobal( "string",
                            "stilton" );

        final Cheese stilton = new Cheese( "stilton",
                                           5 );
        ksession.insert( stilton );

        ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession( ksession,
                                                                              true );
        ksession.fireAllRules();

        verify( list,
                times( 1 ) ).add( new Integer( 5 ) );
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testGlobals2() throws Exception {
        final KnowledgeBase kbase = getKnowledgeBase( "test_globalsAsConstraints.drl" );
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        final List<Object> results = mock( List.class );
        ksession.setGlobal( "results",
                            results );

        final List<String> cheeseTypes = mock( List.class );
        ksession.setGlobal( "cheeseTypes",
                            cheeseTypes );

        when( cheeseTypes.contains( "stilton" ) ).thenReturn( Boolean.TRUE );
        when( cheeseTypes.contains( "muzzarela" ) ).thenReturn( Boolean.TRUE );

        final Cheese stilton = new Cheese( "stilton",
                                           5 );
        ksession.insert( stilton );
        ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession( ksession,
                                                                              true );
        ksession.fireAllRules();

        verify( results,
                times( 1 ) ).add( "memberOf" );

        final Cheese brie = new Cheese( "brie",
                                        5 );
        ksession.insert( brie );
        ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession( ksession,
                                                                              true );
        ksession.fireAllRules();

        verify( results,
                times( 1 ) ).add( "not memberOf" );
    }

    @Test
    public void testGlobalMerge() throws Exception {
        // from JBRULES-1512
        String rule1 = "package com.sample\n" + 
                       "rule \"rule 1\"\n" + 
                       "    salience 10\n" + 
                       "    when\n" + 
                       "        l : java.util.List()\n" + 
                       "    then\n" + 
                       "        l.add( \"rule 1 executed\" );\n" + 
                       "end\n";
        String rule2 = "package com.sample\n" + 
                       "global String str;\n" + 
                       "rule \"rule 2\"\n" + 
                       "    when\n" + 
                       "        l : java.util.List()\n" + 
                       "    then\n" + 
                       "        l.add( \"rule 2 executed \" + str);\n" + 
                       "end\n";

        StringReader[] readers = new StringReader[2];
        readers[0] = new StringReader( rule1 );
        readers[1] = new StringReader( rule2 );

        final KnowledgeBase kbase = getKnowledgeBase( readers );
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        ksession.setGlobal( "str",
                            "boo" );
        List<String> list = new ArrayList<String>();
        ksession.insert( list );
        ksession.fireAllRules();
        assertEquals( "rule 1 executed",
                      list.get( 0 ) );
        assertEquals( "rule 2 executed boo",
                      list.get( 1 ) );
    }
    
    @Test
    public void testChannels() throws IOException, ClassNotFoundException {
        KnowledgeBase kbase = getKnowledgeBase( "test_Channels.drl" );
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        
        Channel someChannel = mock( Channel.class );
        ksession.registerChannel( "someChannel", someChannel );
        
        ksession.insert( new Cheese( "brie", 30 ) );
        ksession.insert( new Cheese( "stilton", 5 ) );
        
        ksession.fireAllRules();
        
        verify( someChannel ).send( "brie" );
        verify( someChannel,  never() ).send( "stilton" );
    }

}
