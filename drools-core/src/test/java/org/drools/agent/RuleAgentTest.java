/**
 * Copyright 2010 JBoss Inc
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

package org.drools.agent;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import static org.junit.Assert.*;

import org.drools.KnowledgeBase;
import org.drools.RuleBase;
import org.drools.RuleBaseConfiguration;
import org.drools.RuleBaseConfiguration.SequentialAgenda;
import org.drools.common.InternalRuleBase;
import org.drools.definition.KnowledgePackage;
import org.drools.definitions.impl.KnowledgePackageImp;
import org.drools.rule.Package;

public class RuleAgentTest {

    @Test
    public void testLists() {
        String s = "\tfoo.bar\n baz.bar\t whee ";
        List result = RuleAgent.list( s );
        assertEquals( 3,
                      result.size() );
        assertEquals( "foo.bar",
                      result.get( 0 ) );
        assertEquals( "baz.bar",
                      result.get( 1 ) );
        assertEquals( "whee",
                      result.get( 2 ) );

        s = null;
        result = RuleAgent.list( s );
        assertNotNull( result );
        assertEquals( 0,
                      result.size() );

        s = "\"yeah man\" \"another one\"";
        result = RuleAgent.list( s );
        assertEquals( 2,
                      result.size() );
        assertEquals( "yeah man",
                      result.get( 0 ) );
        assertEquals( "another one",
                      result.get( 1 ) );

        s = "\"yeah man\"";
        result = RuleAgent.list( s );
        assertEquals( 1,
                      result.size() );
        assertEquals( "yeah man",
                      result.get( 0 ) );

        s = "YEAH";
        result = RuleAgent.list( s );
        assertEquals( 1,
                      result.size() );
        assertEquals( "YEAH",
                      result.get( 0 ) );

    }

    @Test
    public void testFiles() throws Exception {
        File dir = RuleBaseAssemblerTest.getTempDirectory();

        Package p1 = new Package( "p1" );
        File p1f = new File( dir,
                             "p1.pkg" );
        RuleBaseAssemblerTest.writePackage( p1,
                                            p1f );

        Package p2 = new Package( "p2" );
        File p2f = new File( dir,
                             "p2.pkg" );
        RuleBaseAssemblerTest.writePackage( p2,
                                            p2f );

        String path = dir.getPath() + "/" + "p1.pkg " + dir.getPath() + "/" + "p2.pkg";

        Properties props = new Properties();
        props.setProperty( "file",
                           path );
        RuleAgent ag = RuleAgent.newRuleAgent( props );
        RuleBase rb = ag.getRuleBase();
        assertNotNull( rb );
        assertEquals( 2,
                      rb.getPackages().length );

        assertFalse( ag.isPolling() );

        props.setProperty( "poll",
                           "1" );
        ag = RuleAgent.newRuleAgent( props );
        assertTrue( ag.isPolling() );

        ag.stopPolling();
        assertFalse( ag.isPolling() );

    }

    @Test
    public void testFilesWithKnowledgePackage() throws Exception {
        File dir = RuleBaseAssemblerTest.getTempDirectory();

        KnowledgePackage kpackage1 = new KnowledgePackageImp( new Package( "p1" ) );
        File p1f = new File( dir,
                             "p1.pkg" );
        RuleBaseAssemblerTest.writePackage( kpackage1,
                                            p1f );

        KnowledgePackage kpackage2 = new KnowledgePackageImp( new Package( "p2" ) );
        File p2f = new File( dir,
                             "p2.pkg" );
        RuleBaseAssemblerTest.writePackage( kpackage2,
                                            p2f );

        String path = dir.getPath() + "/" + "p1.pkg " + dir.getPath() + "/" + "p2.pkg";

        Properties props = new Properties();
        props.setProperty( "file",
                           path );
        RuleAgent ag = RuleAgent.newRuleAgent( props );
        RuleBase rb = ag.getRuleBase();
        assertNotNull( rb );
        assertEquals( 2,
                      rb.getPackages().length );

        assertFalse( ag.isPolling() );

        props.setProperty( "poll",
                           "1" );
        ag = RuleAgent.newRuleAgent( props );
        assertTrue( ag.isPolling() );

        ag.stopPolling();
        assertFalse( ag.isPolling() );

    }

    //    public void testSpaces() throws Exception {
    //        File dir = RuleBaseAssemblerTest.getTempDirectory();
    //        File dir_ = new File(dir, "whee waah");
    //        dir_.mkdir();
    //        System.err.println(dir_.getPath());
    //
    //
    //        File x = new File("/tmp/__temp_test_drools_packages/whee waah");
    //        assertTrue(x.exists());
    //    }

    @Test
    public void testPollingFilesRuleBaseUpdate() throws Exception {
        //RuleBaseAssemblerTest.clearTempDirectory();
        final File dir = RuleBaseAssemblerTest.getTempDirectory();

        Random rnd = new Random( System.currentTimeMillis() );

        final Package p1 = new Package( "p1" );
        final File p1f = new File( dir,
                                   rnd.nextLong() + ".pkg" );
        RuleBaseAssemblerTest.writePackage( p1,
                                            p1f );

        String path = p1f.getPath();

        Properties props = new Properties();
        props.setProperty( "file",
                           path );

        RuleAgent ag = RuleAgent.newRuleAgent( props );

        RuleBase rb = ag.getRuleBase();
        assertEquals( 1,
                      rb.getPackages().length );
        assertEquals( 0,
                      rb.getPackages()[0].getGlobals().size() );

        p1.addGlobal( "goo",
                      String.class );

        Thread.sleep( 1000 );

        RuleBaseAssemblerTest.writePackage( p1,
                                            p1f );

        RuleBase rb_ = ag.getRuleBase();
        assertSame( rb,
                    rb_ );
        assertEquals( 1,
                      rb.getPackages().length );
        assertEquals( 0,
                      rb.getPackages()[0].getGlobals().size() );

        Thread.sleep( 1000 );

        ag.refreshRuleBase();

        RuleBase rb2 = ag.getRuleBase();
        assertSame( rb,
                    rb2 );

        assertEquals( 1,
                      rb2.getPackages().length );
        assertEquals( 1,
                      rb2.getPackages()[0].getGlobals().size() );

        //now check subsequent changes
        p1.addGlobal( "goo2",
                      String.class );
        //        System.err.println("-->WRITING CHANGE");
        Thread.sleep( 1000 );
        RuleBaseAssemblerTest.writePackage( p1,
                                            p1f );
        //        System.err.println("-->WROTE CHANGE");
        Thread.sleep( 1000 );
        ag.refreshRuleBase();

        RuleBase rb2_ = ag.getRuleBase();
        assertSame( rb2_,
                    rb2 );
        assertEquals( 1,
                      rb2_.getPackages().length );
        assertEquals( 2,
                      rb2_.getPackages()[0].getGlobals().size() );

        ag.refreshRuleBase();

        RuleBase rb3 = ag.getRuleBase();
        assertSame( rb3,
                    rb2 );

        assertEquals( 1,
                      rb3.getPackages().length );
        assertEquals( 2,
                      rb3.getPackages()[0].getGlobals().size() );

        ag.refreshRuleBase();
        ag.refreshRuleBase();

        assertEquals( 1,
                      rb3.getPackages().length );
        assertEquals( 2,
                      rb3.getPackages()[0].getGlobals().size() );

    }

    @Test
    public void testPollingFilesRuleBaseReplace() throws Exception {
        File dir = RuleBaseAssemblerTest.getTempDirectory();

        Package p1 = new Package( "p1" );
        File p1f = new File( dir,
                             "p43_.pkg" );
        RuleBaseAssemblerTest.writePackage( p1,
                                            p1f );

        Package p2 = new Package( "p2" );
        File p2f = new File( dir,
                             "p44_.pkg" );
        RuleBaseAssemblerTest.writePackage( p2,
                                            p2f );

        String path = dir.getPath() + "/" + "p43_.pkg " + dir.getPath() + "/p44_.pkg";

        Properties props = new Properties();
        props.setProperty( "file",
                           path );

        props.setProperty( "newInstance",
                           "true" );
        RuleAgent ag = RuleAgent.newRuleAgent( props );

        assertTrue( ag.isNewInstance() );

        RuleBase rb = ag.getRuleBase();
        assertEquals( 2,
                      rb.getPackages().length );

        RuleBase rb_ = ag.getRuleBase();
        assertSame( rb,
                    rb_ );

        ag.refreshRuleBase();

        assertSame( rb,
                    ag.getRuleBase() );
        Thread.sleep( 1000 );
        //only change one
        RuleBaseAssemblerTest.writePackage( p1,
                                            p1f );
        Thread.sleep( 1000 );
        ag.refreshRuleBase();

        rb_ = ag.getRuleBase();

        assertNotSame( rb,
                       rb_ );

        //check we will have 2
        assertEquals( 2,
                      rb_.getPackages().length );

        ag.refreshRuleBase();
        ag.refreshRuleBase();

        RuleBase rb__ = ag.getRuleBase();
        assertSame( rb_,
                    rb__ );

    }

    @Test
    public void testPollingFilesRuleBaseReplaceWithKnowledgePackages() throws Exception {
        File dir = RuleBaseAssemblerTest.getTempDirectory();

        KnowledgePackage kpackage1 = new KnowledgePackageImp( new Package( "p1" ) );
        File p1f = new File( dir,
                             "p43_.pkg" );
        RuleBaseAssemblerTest.writePackage( kpackage1,
                                            p1f );

        KnowledgePackage kpackage2 = new KnowledgePackageImp( new Package( "p2" ));
        File p2f = new File( dir,
                             "p44_.pkg" );
        RuleBaseAssemblerTest.writePackage( kpackage2,
                                            p2f );

        String path = dir.getPath() + "/" + "p43_.pkg " + dir.getPath() + "/p44_.pkg";

        Properties props = new Properties();
        props.setProperty( "file",
                           path );

        props.setProperty( "newInstance",
                           "true" );
        RuleAgent ag = RuleAgent.newRuleAgent( props );

        assertTrue( ag.isNewInstance() );

        RuleBase rb = ag.getRuleBase();
        assertEquals( 2,
                      rb.getPackages().length );

        RuleBase rb_ = ag.getRuleBase();
        assertSame( rb,
                    rb_ );

        ag.refreshRuleBase();

        assertSame( rb,
                    ag.getRuleBase() );
        Thread.sleep( 1000 );
        //only change one
        RuleBaseAssemblerTest.writePackage( kpackage1,
                                            p1f );
        Thread.sleep( 1000 );
        ag.refreshRuleBase();

        rb_ = ag.getRuleBase();

        assertNotSame( rb,
                       rb_ );

        //check we will have 2
        assertEquals( 2,
                      rb_.getPackages().length );

        ag.refreshRuleBase();
        ag.refreshRuleBase();

        RuleBase rb__ = ag.getRuleBase();
        assertSame( rb_,
                    rb__ );

    }

    //    // FIXME - for some reason failing on hudson
    //    @Test @Ignore
    //    public void testPollingFilesRuleBaseReplace2() throws Exception {
    //        File dir = RuleBaseAssemblerTest.getTempDirectory();
    //
    //        Package p1 = new Package( "p1" );
    //        File p1f = new File( dir,
    //                             "p43_.pkg" );
    //        RuleBaseAssemblerTest.writePackage( p1,
    //                                            p1f );
    //
    //        Package p2 = new Package( "p2" );
    //        File p2f = new File( dir,
    //                             "p44_.pkg" );
    //        RuleBaseAssemblerTest.writePackage( p2,
    //                                            p2f );
    //
    //        String path = dir.getPath() + "/" + "p43_.pkg " + dir.getPath() + "/p44_.pkg";
    //
    //        Properties props = new Properties();
    //        props.setProperty( "file",
    //                           path );
    //
    //        props.setProperty( "newInstance",
    //                           "true" );
    //        
    //        props.setProperty( "poll", "1" );
    //        
    //        KnowledgeAgent kagent = KnowledgeAgentFactory.newKnowledgeAgent( "agent1", props );
    //        
    //        KnowledgeBase kbase = kagent.getKnowledgeBase();
    //        
    //        assertEquals( 2,
    //                      kbase.getKnowledgePackages().size() );        
    //
    //        KnowledgeBase kbase_ = kagent.getKnowledgeBase();
    //        assertSame( kbase,
    //                    kbase_ );
    //
    //        //only change one
    //        RuleBaseAssemblerTest.writePackage( p1,
    //                                            p1f );
    //        int i = 0;
    //        while ( i < 20 && kagent.getKnowledgeBase() == kbase ) {
    //            // this will sleep for a max of 10 seconds, it'll check every 500ms to see if a new kbase exists
    //            // if it exists, it will break the loop.
    //            Thread.sleep( 500 );
    //            i++;
    //        }
    //        
    //        kbase_ = kagent.getKnowledgeBase();
    //        assertNotSame( kbase,
    //                    kbase_ );
    //
    //        //check we will have 2
    //        assertEquals( 2,
    //                      kbase.getKnowledgePackages().size() );
    //
    //    }    

    @Test
    public void testPollingFilesRuleBaseRemoveNewInstanceFalse() throws Exception {
        File dir = RuleBaseAssemblerTest.getTempDirectory();

        Package p1 = new Package( "p1" );
        File p1f = new File( dir,
                             "p43_.pkg" );
        RuleBaseAssemblerTest.writePackage( p1,
                                            p1f );

        Package p2 = new Package( "p2" );
        File p2f = new File( dir,
                             "p44_.pkg" );
        RuleBaseAssemblerTest.writePackage( p2,
                                            p2f );

        String path = dir.getPath() + "/" + "p43_.pkg " + dir.getPath() + "/p44_.pkg";

        Properties props = new Properties();
        props.setProperty( "file",
                           path );

        props.setProperty( "newInstance",
                           "false" );
        RuleAgent ag = RuleAgent.newRuleAgent( props );

        assertFalse( ag.isNewInstance() );

        RuleBase rb = ag.getRuleBase();
        assertEquals( 2,
                      rb.getPackages().length );

        boolean success = p2f.delete();
        assertTrue( success );

        ag.refreshRuleBase();

        assertEquals( 1,
                      rb.getPackages().length );
        Thread.sleep( 1000 );
        //only change one
        RuleBaseAssemblerTest.writePackage( p1,
                                            p1f );
        Thread.sleep( 1000 );
        ag.refreshRuleBase();

        //check we will have 2
        assertEquals( 1,
                      rb.getPackages().length );

        ag.refreshRuleBase();
        ag.refreshRuleBase();

        assertEquals( 1,
                      rb.getPackages().length );

    }

    @Test
    public void testPollingFilesRuleBaseRemoveNewInstanceTrue() throws Exception {
        File dir = RuleBaseAssemblerTest.getTempDirectory();

        Package p1 = new Package( "p1" );
        File p1f = new File( dir,
                             "p43_.pkg" );
        RuleBaseAssemblerTest.writePackage( p1,
                                            p1f );

        Package p2 = new Package( "p2" );
        File p2f = new File( dir,
                             "p44_.pkg" );
        RuleBaseAssemblerTest.writePackage( p2,
                                            p2f );

        String path = dir.getPath() + "/" + "p43_.pkg " + dir.getPath() + "/p44_.pkg";

        Properties props = new Properties();
        props.setProperty( "file",
                           path );

        props.setProperty( "newInstance",
                           "true" );
        RuleAgent ag = RuleAgent.newRuleAgent( props );

        assertTrue( ag.isNewInstance() );

        RuleBase rb = ag.getRuleBase();
        assertEquals( 2,
                      rb.getPackages().length );

        boolean success = p2f.delete();
        assertTrue( success ); // <-- does not work on windows

        ag.refreshRuleBase();

        RuleBase rb_ = ag.getRuleBase();

        assertNotSame( rb,
                       rb_ );

        assertEquals( 1,
                      rb_.getPackages().length );
        Thread.sleep( 1000 );
        //only change one
        RuleBaseAssemblerTest.writePackage( p1,
                                            p1f );
        Thread.sleep( 1000 );
        ag.refreshRuleBase();

        RuleBase rb__ = ag.getRuleBase();

        assertNotSame( rb,
                       rb__ );

        //check we will have 2
        assertEquals( 1,
                      rb__.getPackages().length );

        ag.refreshRuleBase();
        ag.refreshRuleBase();

        RuleBase rb___ = ag.getRuleBase();
        assertEquals( 1,
                      rb___.getPackages().length );

    }

    @Test
    public void testDirectory() throws Exception {
        File dir = RuleBaseAssemblerTest.getTempDirectory();

        Package p1 = new Package( "p1" );
        File p1f = new File( dir,
                             "p43_.pkg" );

        File junk = new File( dir,
                              "xxx.poo" );
        FileOutputStream ojunk = new FileOutputStream( junk );
        ojunk.write( "ignore me".getBytes() );
        ojunk.flush();
        ojunk.close();

        RuleBaseAssemblerTest.writePackage( p1,
                                            p1f );

        Properties props = new Properties();
        props.setProperty( RuleAgent.DIRECTORY,
                           dir.getPath() );
        props.setProperty( RuleAgent.CONFIG_NAME,
                           "goo" );

        RuleAgent ag = RuleAgent.newRuleAgent( props );

        ag.refreshRuleBase();

        RuleBase rb = ag.getRuleBase();
        assertNotNull( rb );
        //        assertEquals(1, rb.getPackages().length);
    }

    @Test
    public void testCustomRuleBaseConfiguration() throws Exception {
        final File dir = RuleBaseAssemblerTest.getTempDirectory();

        Random rnd = new Random( System.currentTimeMillis() );

        final Package p1 = new Package( "p1" );
        final File p1f = new File( dir,
                                   rnd.nextLong() + ".pkg" );
        RuleBaseAssemblerTest.writePackage( p1,
                                            p1f );

        String path = p1f.getPath();

        Properties props = new Properties();
        props.setProperty( "file",
                           path );

        // Check a default value for the RuleBase's RuleBaseConfiguration
        RuleAgent agent = RuleAgent.newRuleAgent( props );
        RuleBaseConfiguration conf = ((InternalRuleBase) agent.getRuleBase()).getConfiguration();
        assertEquals( false,
                      conf.isSequential() );

        // Pass in a RuleBaseConfiguration and make sure the RuleBase was created with it
        conf = new RuleBaseConfiguration();
        conf.setSequential( true );
        agent = RuleAgent.newRuleAgent( props,
                                        conf );
        conf = ((InternalRuleBase) agent.getRuleBase()).getConfiguration();
        assertEquals( true,
                      conf.isSequential() );
    }

    @Test
    public void testLoadSampleConfig() {
        RuleAgent ag = new RuleAgent( new RuleBaseConfiguration() );
        Properties props = ag.loadFromProperties( "/sample-agent-config.properties" );
        assertEquals( "10",
                      props.getProperty( RuleAgent.POLL_INTERVAL ) );
        assertEquals( "/home/packages",
                      props.getProperty( RuleAgent.DIRECTORY ) );
        assertEquals( "true",
                      props.getProperty( RuleAgent.NEW_INSTANCE ) );
        assertEqualsIgnoreWhitespace( "/foo/bar.pkg /wee/waa.pkg /wee/waa2.pkg",
                                      props.getProperty( RuleAgent.FILES ) );
    }
    
    @Test
    public void testLoadBasicAuthenticationSampleConfig() {
        RuleAgent ag = new RuleAgent( new RuleBaseConfiguration() );
        Properties props = ag.loadFromProperties( "/basic-authentication-sample-agent-config.properties" );
        assertEquals( "30",
                      props.getProperty( RuleAgent.POLL_INTERVAL ) );
        assertEquals( "http://localhost:8080/guvnor-webapp/org.drools.guvnor.Guvnor/package/defaultPackage/LATEST",
                props.getProperty( RuleAgent.URLS ) );
        assertEquals( "d:/drools",
                      props.getProperty( RuleAgent.LOCAL_URL_CACHE ) );
        assertEquals( "true",
                      props.getProperty( RuleAgent.NEW_INSTANCE ) );
        assertEquals( "true",
                props.getProperty( RuleAgent.ENABLE_BASIC_AUTHENTICATION ) );
        assertEquals( "admin",
                props.getProperty( RuleAgent.USER_NAME ) );
        assertEquals( "admin",
                props.getProperty( RuleAgent.PASSWORD ) );
    }

    private void assertEqualsIgnoreWhitespace(final String expected,
                                              final String actual) {
        final String cleanExpected = expected.replaceAll( "\\s+",
                                                          "" );
        final String cleanActual = actual.replaceAll( "\\s+",
                                                      "" );

        assertEquals( cleanExpected,
                      cleanActual );
    }

    @Test
    public void testEventListenerSetup() throws Exception {
        RuleAgent ag = new RuleAgent( new RuleBaseConfiguration() );
        assertNotNull( ag.listener );

        final String[] name = new String[1];

        AgentEventListener list = new AgentEventListener() {
            public void debug(String message) {
            }

            public void exception(String message, Throwable e) {
            }

            public void exception(Throwable e) {
            }

            public void info(String message) {
            }

            public void warning(String message) {
            }

            public void setAgentName(String n) {
                name[0] = n;
            }

            public void debug(String message,
                              Object object) {
                // TODO Auto-generated method stub

            }

            public void info(String message,
                             Object object) {
                // TODO Auto-generated method stub

            }

            public void warning(String message,
                                Object object) {
                // TODO Auto-generated method stub

            }
        };

        File dir = RuleBaseAssemblerTest.getTempDirectory();

        Package p1 = new Package( "p1" );
        File p1f = new File( dir,
                             "p42_.pkg" );
        RuleBaseAssemblerTest.writePackage( p1,
                                            p1f );

        String path = dir.getPath() + "/" + "p42_.pkg";

        Properties props = new Properties();
        props.setProperty( "file",
                           path );
        props.setProperty( "poll",
                           "1" );
        props.setProperty( "name",
                           "poo" );
        ag = RuleAgent.newRuleAgent( props,
                                     list );

        assertEquals( list,
                      ag.listener );
        assertEquals( "poo",
                      name[0] );
        ag.stopPolling();
    }

    @Test
    public void testPollSetup() throws Exception {
        //this is the only method that will actually run the polling timer

        Properties props = new Properties();
        //props.setProperty( "file", "/foo/bar" );
        props.setProperty( "poll",
                           "1" );
        MockRuleAgent ag = new MockRuleAgent();
        ag.init( props );

        assertTrue( ag.isPolling() );
        assertTrue( ag.refreshCalled );
        ag.refreshCalled = false;
        assertFalse( ag.refreshCalled );
        Thread.sleep( 100 );
        assertFalse( ag.refreshCalled );
        Thread.sleep( 1500 );
        assertTrue( ag.refreshCalled );
        ag.refreshCalled = false;
        Thread.sleep( 100 );
        assertFalse( ag.refreshCalled );
        Thread.sleep( 1500 );
        assertTrue( ag.refreshCalled );
        ag.stopPolling();

    }

    @Test
    public void testProviderMap() throws Exception {

        assertEquals( 3,
                      RuleAgent.PACKAGE_PROVIDERS.size() );
        assertTrue( RuleAgent.PACKAGE_PROVIDERS.containsKey( "url" ) );
        assertTrue( RuleAgent.PACKAGE_PROVIDERS.containsKey( "file" ) );
        assertTrue( RuleAgent.PACKAGE_PROVIDERS.containsKey( "dir" ) );
        assertFalse( RuleAgent.PACKAGE_PROVIDERS.containsKey( "XXX" ) );
        assertTrue( RuleAgent.PACKAGE_PROVIDERS.get( "url" ).equals( URLScanner.class ) );

    }

    @Test
    public void testLoadUpFromProperties() throws Exception {
        AnotherRuleAgentMock ag = new AnotherRuleAgentMock();
        Map oldMap = ag.PACKAGE_PROVIDERS;

        ag.PACKAGE_PROVIDERS = new HashMap();
        ag.PACKAGE_PROVIDERS.put( RuleAgent.URLS,
                                  MockProvider.class );
        ag.PACKAGE_PROVIDERS.put( RuleAgent.FILES,
                                  MockProvider.class );
        ag.PACKAGE_PROVIDERS.put( RuleAgent.DIRECTORY,
                                  MockProvider.class );

        Properties props = new Properties();
        props.load( this.getClass().getResourceAsStream( "/rule-agent-config.properties" ) );
        MockEventListener evl = new MockEventListener();
        ag.listener = evl;

        ag.init( props );

        assertTrue( ag.newInstance );
        assertEquals( 3,
                      ag.provs.size() );
        assertEquals( 30,
                      ag.secondsToRefresh );
        assertEquals( "MyConfig",
                      evl.name );
        assertFalse( evl.exceptionCalled );
        assertFalse( evl.warningCalled );
        assertTrue( evl.infoCalled );

        ag.PACKAGE_PROVIDERS = oldMap;
    }

    @Test
    public void testLoadRuleBaseConfigurationProperties() throws Exception {
        AnotherRuleAgentMock ag = new AnotherRuleAgentMock();
        Map oldMap = ag.PACKAGE_PROVIDERS;

        RuleAgent.PACKAGE_PROVIDERS = new HashMap();
        RuleAgent.PACKAGE_PROVIDERS.put( RuleAgent.URLS,
                                         MockProvider.class );
        RuleAgent.PACKAGE_PROVIDERS.put( RuleAgent.FILES,
                                         MockProvider.class );
        RuleAgent.PACKAGE_PROVIDERS.put( RuleAgent.DIRECTORY,
                                         MockProvider.class );

        Properties props = new Properties();
        MockEventListener evl = new MockEventListener();
        ag.listener = evl;

        props.load( this.getClass().getResourceAsStream( "/rule-agent-config.properties" ) );
        ag.init( props,
                 true );

        assertTrue( ag.getRuleBaseConfiguration().isMaintainTms() );
        assertFalse( ag.getRuleBaseConfiguration().isSequential() );
        assertTrue( ag.getRuleBaseConfiguration().getSequentialAgenda().equals( SequentialAgenda.SEQUENTIAL ) );
        assertTrue( ag.getRuleBaseConfiguration().isShareAlphaNodes() );
        assertTrue( ag.getRuleBaseConfiguration().isShareBetaNodes() );

        props.load( this.getClass().getResourceAsStream( "/rule-base-rule-agent-config.properties" ) );

        ag.init( props,
                 true );

        assertTrue( ag.getRuleBaseConfiguration().isMaintainTms() );
        assertFalse( ag.getRuleBaseConfiguration().isSequential() );
        assertTrue( ag.getRuleBaseConfiguration().getSequentialAgenda().equals( SequentialAgenda.DYNAMIC ) );
        assertFalse( ag.getRuleBaseConfiguration().isShareAlphaNodes() );
        assertTrue( ag.getRuleBaseConfiguration().isShareBetaNodes() );

        ag.PACKAGE_PROVIDERS = oldMap;
    }

    class AnotherRuleAgentMock extends RuleAgent {

        public int     secondsToRefresh;
        public List    provs;
        public boolean newInstance;

        public AnotherRuleAgentMock() {
            super( new RuleBaseConfiguration() );
        }

        synchronized void configure(boolean newInstance,
                                    List provs,
                                    int secondsToRefresh) {
            this.newInstance = newInstance;
            this.provs = provs;
            this.secondsToRefresh = secondsToRefresh;
        }

    }

    class MockEventListener
        implements
        AgentEventListener {

        public String name;
        boolean       exceptionCalled = false;
        boolean       infoCalled      = false;
        boolean       warningCalled;

        public void debug(String message) {

        }

        public void exception(String message, Throwable e) {
            this.exceptionCalled = true;
        }

        public void exception(Throwable e) {
            this.exceptionCalled = true;
        }

        public void info(String message) {
            if ( message != null ) this.infoCalled = true;
        }

        public void setAgentName(String name) {
            this.name = name;
        }

        public void warning(String message) {
            this.warningCalled = false;

        }

        public void debug(String message,
                          Object object) {
            // TODO Auto-generated method stub

        }

        public void info(String message,
                         Object object) {
            // TODO Auto-generated method stub

        }

        public void warning(String message,
                            Object object) {
            // TODO Auto-generated method stub

        }

    }

}
