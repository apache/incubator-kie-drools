package org.drools.agent;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import org.drools.common.DroolsObjectInputStream;
import org.drools.rule.Package;

import junit.framework.TestCase;

public class URLScannerTest extends TestCase {

    public void testFileURLCache() throws Exception {
        File dir = RuleBaseAssemblerTest.getTempDirectory();
        
        String url = "http://localhost:8080/foo/bar.bar/packages/IMINYRURL/LATEST";
        String fileName = URLEncoder.encode( url, "UTF-8" );
        
        File f = new File(dir, fileName);
        
        Package p = new Package("x");
        
        
        RuleBaseAssemblerTest.writePackage( p, f );
        
        DroolsObjectInputStream in = new DroolsObjectInputStream(new FileInputStream(f));
        Package p_ = (Package) in.readObject();
        assertEquals("x", p.getName());


        
    }
    
    public void testGetURL() throws Exception {
        String url = "http://localhost:8080/foo/bar.bar/packages/IMINYRURL/LATEST";
        URL u = new URL(url);
        assertEquals(url, URLScanner.getURL( u ));
        //URLConnection con = u.openConnection();
        //con.connect();
    }
    
    
}
