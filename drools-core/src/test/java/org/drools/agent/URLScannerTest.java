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
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Properties;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.common.DroolsObjectInputStream;
import org.drools.definition.KnowledgePackage;
import org.drools.definitions.impl.KnowledgePackageImp;
import org.drools.rule.Package;

public class URLScannerTest {

    @Test
    public void testFileURLCache() throws Exception {
        File dir = RuleBaseAssemblerTest.getTempDirectory();

        String url = "http://localhost:8080/foo/bar.bar/packages/IMINYRURL/LATEST";
        String fileName = URLEncoder.encode( url,
                                             "UTF-8" );

        File f = new File( dir,
                           fileName );

        Package p = new Package( "x" );

        RuleBaseAssemblerTest.writePackage( p,
                                            f );

        DroolsObjectInputStream in = new DroolsObjectInputStream( new FileInputStream( f ) );
        Package p_ = (Package) in.readObject();
        in.close();
        assertEquals( "x",
                      p_.getName() );

    }

    @Test
    public void testFileURLCacheWithKnowledgePackage() throws Exception {
        File dir = RuleBaseAssemblerTest.getTempDirectory();

        String url = "http://localhost:8080/foo/bar.bar/packages/IMINYRURL/LATEST";
        String fileName = URLEncoder.encode( url,
                                             "UTF-8" );

        File f = new File( dir,
                           fileName );

        KnowledgePackage kpackage = new KnowledgePackageImp( new Package( "x" ) );

        RuleBaseAssemblerTest.writePackage( kpackage,
                                            f );

        DroolsObjectInputStream in = new DroolsObjectInputStream( new FileInputStream( f ) );
        KnowledgePackage p_ = (KnowledgePackage) in.readObject();
        in.close();
        assertEquals( "x",
                      p_.getName() );

    }

    //    public void testGetURL() throws Exception {
    //        String url = "http://localhost:8080/foo/bar.bar/packages/IMINYRURL/LATEST";
    //        URL u = new URL(url);
    //        assertEquals(url, URLScanner.getURL( u ));
    //        //URLConnection con = u.openConnection();
    //        //con.connect();
    //    }

    @Test
    public void testGetFiles() throws Exception {

        URL u1 = new URL( "http://localhost:8080/foo/bar.bar/packages/IMINYRURL/LATEST" );
        URL u2 = new URL( "http://localhost:8080/foo/bar.bar/packages/IMINYRURL/PROD" );
        URLScanner scan = new URLScanner();

        File dir = RuleBaseAssemblerTest.getTempDirectory();
        File[] result = scan.getFiles( new URL[]{u1, u2},
                                       dir );

        assertEquals( 2,
                      result.length );
        assertEquals( dir.getPath(),
                      result[0].getParent() );

        File f1 = result[0];
        File f2 = result[1];
        assertEquals( "http%3A%2F%2Flocalhost%3A8080%2Ffoo%2Fbar.bar%2Fpackages%2FIMINYRURL%2FLATEST.pkg",
                      f1.getName() );
        assertEquals( "http%3A%2F%2Flocalhost%3A8080%2Ffoo%2Fbar.bar%2Fpackages%2FIMINYRURL%2FPROD.pkg",
                      f2.getName() );

    }

    @Test
    public void testConfig() throws Exception {
        URLScanner scan = new URLScanner();

        File dir = RuleBaseAssemblerTest.getTempDirectory();

        Properties config = new Properties();
        config.setProperty( RuleAgent.LOCAL_URL_CACHE,
                            dir.getPath() );
        config.setProperty( RuleAgent.URLS,
                            "http://goo.ber http://wee.waa" );

        scan.configure( config );

        assertNotNull( scan.lastUpdated );
        assertEquals( 2,
                      scan.urls.length );
        assertEquals( "http://goo.ber",
                      scan.urls[0].toExternalForm() );
        assertEquals( "http://wee.waa",
                      scan.urls[1].toExternalForm() );
        assertNotNull( scan.localCacheFileScanner );

        assertEquals( 2,
                      scan.localCacheFileScanner.files.length );

        assertEquals( "http%3A%2F%2Fgoo.ber.pkg",
                      scan.localCacheFileScanner.files[0].getName() );
        assertEquals( "http%3A%2F%2Fwee.waa.pkg",
                      scan.localCacheFileScanner.files[1].getName() );

    }

    @Test
    public void testLastUpdatedError() {
        LastUpdatedPing ping = new LastUpdatedPing();
        assertTrue( ping.isError() );
        ping.responseMessage = "ABC";
        ping.lastUpdated = 42;
        assertTrue( ping.isError() );
        ping.responseMessage = "200 OK";
        assertFalse( ping.isError() );
    }

    @Test
    public void testUpdateNoLocalCache() throws Exception {
        URLScanner scan = new URLScanner();
        File dir = RuleBaseAssemblerTest.getTempDirectory();

        int numfiles = dir.list().length;

        Properties config = new Properties();
        //config.setProperty( RuleAgent.LOCAL_URL_CACHE, dir.getPath() );
        config.setProperty( RuleAgent.URLS,
                            "http://goo2.ber http://wee2.waa" );

        scan.configure( config );

        scan.httpClient = new IHttpClient() {

            public LastUpdatedPing checkLastUpdated(URL url) throws IOException {
                LastUpdatedPing ping = new LastUpdatedPing();
                ping.lastUpdated = 123;
                ping.responseMessage = "200 OK";
                return ping;
            }

            public Package fetchPackage(URL url, boolean enableBasicAuthentication, String username, String password) throws IOException {
                if ( url.toExternalForm().equals( "http://goo2.ber" ) ) {
                    return new Package( "goo2.ber" );
                } else {
                    return new Package( "wee2.waa" );
                }
            }

        };

        assertNull( scan.localCacheFileScanner );
        assertNull( scan.localCacheDir );

        RuleBase rb = RuleBaseFactory.newRuleBase();
        AgentEventListener list = getNilListener();
        PackageProvider.applyChanges( rb,
                                      false,
                                      scan.loadPackageChanges().getChangedPackages(),
                                      list );

        assertEquals( 2,
                      rb.getPackages().length );

        assertExists( new String[]{"goo2.ber", "wee2.waa"},
                      rb.getPackages() );

        assertEquals( numfiles,
                      dir.list().length );
    }

    private AgentEventListener getNilListener() {
        return new MockListener();
    }

    private void assertExists(String[] names,
                              Package[] packages) {
        for ( int i = 0; i < packages.length; i++ ) {
            String name = packages[i].getName();
            int matches = 0;
            for ( int j = 0; j < names.length; j++ ) {
                if ( name.equals( names[j] ) ) {
                    matches++;
                }
            }
            assertEquals( "Should only have one package named " + name,
                          1,
                          matches );
        }

    }

    @Test
    public void testUpdateWithLocalCache() {
        URLScanner scan = new URLScanner();
        scan.listener = new MockListener();
        File dir = RuleBaseAssemblerTest.getTempDirectory();

        Properties config = new Properties();
        config.setProperty( RuleAgent.LOCAL_URL_CACHE,
                            dir.getPath() );
        config.setProperty( RuleAgent.URLS,
                            "http://goo.ber http://wee.waa" );

        scan.configure( config );

        scan.httpClient = new IHttpClient() {

            public LastUpdatedPing checkLastUpdated(URL url) throws IOException {
                LastUpdatedPing ping = new LastUpdatedPing();
                ping.lastUpdated = 123;
                ping.responseMessage = "200 OK";
                return ping;
            }

            public Package fetchPackage(URL url, boolean enableBasicAuthentication, String username, String password) throws IOException {
                if ( url.toExternalForm().equals( "http://goo.ber" ) ) {
                    return new Package( "goo.ber" );
                } else {
                    return new Package( "wee.waa" );
                }
            }

        };

        assertNotNull( scan.localCacheFileScanner );
        assertNotNull( scan.localCacheDir );

        RuleBase rb = RuleBaseFactory.newRuleBase();
        PackageProvider.applyChanges( rb,
                                      false,
                                      scan.loadPackageChanges().getChangedPackages(),
                                      getNilListener() );

        assertEquals( 2,
                      rb.getPackages().length );
        assertTrue( "goo.ber".equals( rb.getPackages()[0].getName() ) || "goo.ber".equals( rb.getPackages()[1].getName() ) );
        assertTrue( "wee.waa".equals( rb.getPackages()[0].getName() ) || "wee.waa".equals( rb.getPackages()[1].getName() ) );

        //assertEquals( 2, dir.list().length );

        //now check with failures:
        scan.httpClient = new IHttpClient() {

            public LastUpdatedPing checkLastUpdated(URL url) throws IOException {
                LastUpdatedPing ping = new LastUpdatedPing();

                if ( url.toExternalForm().equals( "http://wee.waa" ) ) {
                    ping.lastUpdated = -1;
                    ping.responseMessage = "XXX";

                } else {
                    ping.lastUpdated = 123;
                    ping.responseMessage = "200 OK";
                }
                return ping;
            }

            public Package fetchPackage(URL url, boolean enableBasicAuthentication, String username, String password) throws IOException {
                throw new IOException( "poo" );
            }

        };

        rb = RuleBaseFactory.newRuleBase();
        assertEquals( 0,
                      rb.getPackages().length );
        PackageProvider.applyChanges( rb,
                                      true,
                                      scan.loadPackageChanges().getChangedPackages(),
                                      getNilListener() );

        assertEquals( 2,
                      rb.getPackages().length );

        final boolean[] fetchCalled = new boolean[1];

        fetchCalled[0] = false;

        //now check with IOExceptions
        scan.httpClient = new IHttpClient() {

            public LastUpdatedPing checkLastUpdated(URL url) throws IOException {
                LastUpdatedPing ping = new LastUpdatedPing();
                ping.lastUpdated = 1234;
                ping.responseMessage = "200 OK";
                return ping;
            }

            public Package fetchPackage(URL url, boolean enableBasicAuthentication, String username, String password) throws IOException {
                fetchCalled[0] = true;
                throw new IOException( "poo" );
            }

        };

        PackageChangeInfo changes = scan.loadPackageChanges();
        assertEquals( 0,
                      changes.getChangedPackages().size() );
        assertEquals( true,
                      fetchCalled[0] );
        assertEquals( 2,
                      ((MockListener) scan.listener).exceptions.size() );

    }

    @Test
    public void testColdStartWithError() throws Exception {
        //this will show starting up and reading packages from the dir when the remote one doesn't respond
        URLScanner scan = new URLScanner();
        scan.listener = new MockListener();
        File dir = RuleBaseAssemblerTest.getTempDirectory();

        Package p1 = new Package( "goo.ber" );
        Package p2 = new Package( "wee.waa" );

        File f1 = URLScanner.getLocalCacheFileForURL( dir,
                                                      new URL( "http://goo.ber" ) );
        File f2 = URLScanner.getLocalCacheFileForURL( dir,
                                                      new URL( "http://wee.waa" ) );

        RuleBaseAssemblerTest.writePackage( p1,
                                            f1 );
        RuleBaseAssemblerTest.writePackage( p2,
                                            f2 );

        Properties config = new Properties();
        config.setProperty( RuleAgent.LOCAL_URL_CACHE,
                            dir.getPath() );
        config.setProperty( RuleAgent.URLS,
                            "http://goo.ber http://wee.waa" );

        scan.configure( config );

        scan.httpClient = new IHttpClient() {

            public LastUpdatedPing checkLastUpdated(URL url) throws IOException {
                throw new IOException();
            }

            public Package fetchPackage(URL url, boolean enableBasicAuthentication, String username, String password) throws IOException {
                throw new IOException();
            }

        };

        assertNotNull( scan.localCacheFileScanner );
        assertNotNull( scan.localCacheDir );

        RuleBase rb = RuleBaseFactory.newRuleBase();
        PackageProvider.applyChanges( rb,
                                      true,
                                      scan.loadPackageChanges().getChangedPackages(),
                                      getNilListener() );
        assertEquals( 2,
                      rb.getPackages().length );

    }

}
