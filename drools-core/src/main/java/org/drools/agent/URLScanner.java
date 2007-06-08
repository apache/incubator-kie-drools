package org.drools.agent;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.drools.RuntimeDroolsException;

public class URLScanner {

    
    
    
    public URLScanner(String uri) {
        try {
            URL url = new URL(uri);
            
            URLConnection con = url.openConnection();
            HttpURLConnection httpCon = (HttpURLConnection) con;
            httpCon.setRequestMethod( "HEAD" );
            
            
            
            System.err.println(httpCon.getHeaderFields());
            
        } catch ( IOException e ) {
            throw new RuntimeDroolsException(e);
        }
        
    }
    
    public static void main(String[] args) {
        URLScanner scan = new URLScanner("http://localhost:8888/org.drools.brms.JBRMS/package/com.billasurf.manufacturing.plant/SNAP");
    }
    
}
