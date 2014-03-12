package org.drools.agent;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import org.drools.core.util.FileManager;
import org.drools.core.util.IoUtils;
import org.drools.core.util.StringUtils;
import org.drools.io.impl.UrlResource;
import org.drools.io.impl.ResourceChangeScannerImpl;
import org.drools.io.impl.ResourceChangeNotifierImpl;
import org.drools.io.ResourceFactory;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.handler.ResourceHandler;

import java.io.File;
import java.io.Writer;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

public class UrlResourceTest {
    private FileManager fileManager;
    private Server server;

    @Before
    public void setUp() throws Exception {
        fileManager = new FileManager();
        fileManager.setUp();
        ((ResourceChangeScannerImpl) ResourceFactory.getResourceChangeScannerService()).reset();
        ResourceFactory.getResourceChangeNotifierService().start();
        ResourceFactory.getResourceChangeScannerService().start();

        this.server = new Server(IoUtils.findPort());
        ResourceHandler resourceHandler = new ResourceHandler();
        resourceHandler.setResourceBase( fileManager.getRootDirectory().getPath() );
        System.out.println("root : " + fileManager.getRootDirectory().getPath() );

        server.setHandler( resourceHandler );

        server.start();
    }

    private int getPort(){
        return this.server.getConnectors()[0].getLocalPort();
    }

    @After
    public void tearDown() throws Exception {
        fileManager.tearDown();
        ResourceFactory.getResourceChangeNotifierService().stop();
        ResourceFactory.getResourceChangeScannerService().stop();
        ((ResourceChangeNotifierImpl) ResourceFactory.getResourceChangeNotifierService()).reset();
        ((ResourceChangeScannerImpl) ResourceFactory.getResourceChangeScannerService()).reset();

        server.stop();

        UrlResource.CACHE_DIR = null; // make sure subsequent tests will not be affected
    }


    @Test
    public void testWithCache() throws Exception {
        URL url = new URL("http://localhost:"+this.getPort()+"/rule1.drl");
        UrlResource ur = new UrlResource(url);
        File cacheDir = new File("target/test-tmp/cache");
        cacheDir.mkdirs();
        UrlResource.CACHE_DIR = cacheDir;

        File f1 = fileManager.newFile( "rule1.drl" );
        System.err.println("target file: " + f1.getAbsolutePath());
        Writer output = new BufferedWriter( new FileWriter( f1 ) );
        output.write( "Some data" );
        output.close();

        long lm = ur.getLastModified();
        assertTrue(lm > 0);

        InputStream ins = ur.getInputStream();
        assertNotNull(ins);
        ins.close();

        server.stop();

        ins = ur.getInputStream();
        assertNotNull(ins);

        assertTrue(ur.getLastModified() > 0);

        assertTrue(ins instanceof FileInputStream);
        ins.close();

        //now write some more stuff
        Thread.sleep(1000);
        f1.delete();
        output = new BufferedWriter( new FileWriter( f1 ) );
        output.write( "More data..." );
        output.close();

        server.start();

        url = new URL("http://localhost:"+this.getPort()+"/rule1.drl");
        ur = new UrlResource(url);

        ins = ur.getInputStream();
        assertNotNull(ins);
        assertTrue(ins instanceof FileInputStream); // returns FileInputStream right after cache update
        ins.close();

        long lm_ = ur.getLastModified();
        System.err.println("lm_ : " + lm_ + " lm : " + lm );

        assertTrue(lm_ > lm);

        InputStream in_= ur.getInputStream();
        BufferedReader rdr = new BufferedReader(new InputStreamReader(in_));
        String line = rdr.readLine();
        assertEquals("More data...", line);
        rdr.close();

        server.stop();

        Thread.sleep(1000);
        f1.delete();
        output = new BufferedWriter( new FileWriter( f1 ) );
        output.write( "Finally.." );
        output.close();

        //now it should be cached, so using old copy still... (server has stopped serving it up)
        ur = new UrlResource(url);
        in_= ur.getInputStream();
        rdr = new BufferedReader(new InputStreamReader(in_));
        line = rdr.readLine();
        assertEquals("More data...", line);
        rdr.close();

        Thread.sleep(1000);
        server.start();

        url = new URL("http://localhost:"+this.getPort()+"/rule1.drl");
        ur = new UrlResource(url);

        //server is started, so should have latest...
        in_= ur.getInputStream();
        rdr = new BufferedReader(new InputStreamReader(in_));
        line = rdr.readLine();
        assertEquals("Finally..", line);
        rdr.close();
    }

    @Test
    public void testWithoutCache() throws Exception {
        UrlResource ur = new UrlResource(new URL("http://localhost:"+this.getPort()+"/rule1.drl"));
        UrlResource.CACHE_DIR = null;

        File f1 = fileManager.newFile( "rule1.drl" );
        System.err.println("target file: " + f1.getAbsolutePath());
        Writer output = new BufferedWriter( new FileWriter( f1 ) );
        output.write( "Some data" );
        output.close();

        long lm = ur.getLastModified();
        assertTrue(lm > 0);

        InputStream ins = ur.getInputStream();
        assertNotNull(ins);
        ins.close();

        server.stop();
        assertEquals(0, ur.getLastModified());
    }

    @Test
    public void testWithCacheForSameInstance() throws Exception {
        URL url = new URL("http://localhost:"+this.getPort()+"/rule1.drl");
        UrlResource ur = new UrlResource(url);
        File cacheDir = new File("target/test-tmp/cache");
        cacheDir.mkdirs();
        UrlResource.CACHE_DIR = cacheDir;

        File f1 = fileManager.newFile( "rule1.drl" );
        System.err.println("target file: " + f1.getAbsolutePath());
        Writer output = new BufferedWriter( new FileWriter( f1 ) );
        output.write( "Some data" );
        output.close();

        long lm = ur.getLastModified();
        assertTrue(lm > 0);

        InputStream ins = ur.getInputStream();
        assertNotNull(ins);
        ins.close();

        server.stop();

        ins = ur.getInputStream();
        assertNotNull(ins);

        assertTrue(ur.getLastModified() > 0);

        assertTrue(ins instanceof FileInputStream);
        ins.close();

        //now write some more stuff
        Thread.sleep(1000);
        f1.delete();
        output = new BufferedWriter( new FileWriter( f1 ) );
        output.write( "More data..." );
        output.close();

        server.start();

        url = new URL("http://localhost:"+this.getPort()+"/rule1.drl");
        ur = new UrlResource(url);

        ins = ur.getInputStream();
        assertNotNull(ins);
        assertTrue(ins instanceof FileInputStream); // returns FileInputStream right after cache update
        ins.close();

        long lm_ = ur.getLastModified();
        System.err.println("lm_ : " + lm_ + " lm : " + lm );

        assertTrue(lm_ > lm);

        InputStream in_= ur.getInputStream();
        BufferedReader rdr = new BufferedReader(new InputStreamReader(in_));
        String line = rdr.readLine();
        assertEquals("More data...", line);
        rdr.close();

        server.stop();

        Thread.sleep(1000);
        f1.delete();
        output = new BufferedWriter( new FileWriter( f1 ) );
        output.write( "Finally.." );
        output.close();

        //now it should be cached, so using old copy still... (server has stopped serving it up)
        ur = new UrlResource(url);
        in_= ur.getInputStream();
        rdr = new BufferedReader(new InputStreamReader(in_));
        line = rdr.readLine();
        assertEquals("More data...", line);
        rdr.close();

        Thread.sleep(1000);
        server.start();

        url = new URL("http://localhost:"+this.getPort()+"/rule1.drl");
        ur = new UrlResource(url);

        //server is started, so should have latest...
        in_= ur.getInputStream();
        rdr = new BufferedReader(new InputStreamReader(in_));
        line = rdr.readLine();
        assertEquals("Finally..", line);
        rdr.close();
    }
}
