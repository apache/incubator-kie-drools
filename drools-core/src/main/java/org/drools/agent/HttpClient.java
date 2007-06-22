package org.drools.agent;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;

import org.drools.RuntimeDroolsException;
import org.drools.common.DroolsObjectInputStream;
import org.drools.rule.Package;

public class HttpClient {

    
    
    
    public HttpClient(String uri) throws Exception {
        try {
            
            checkLastUpdated( uri );
            URL url = new URL(uri);
            
            URLConnection con = url.openConnection();
            HttpURLConnection httpCon = (HttpURLConnection) con;
            httpCon.setRequestMethod( "GET" );
            InputStream in = httpCon.getInputStream();
            
            DroolsObjectInputStream oin = new DroolsObjectInputStream(in);
            Package p = (Package) oin.readObject();
            in.close();
            
            
            System.err.println(p.getName());
            
        } catch ( IOException e ) {
            throw new RuntimeDroolsException(e);
        }
        
    }

    private void checkLastUpdated(String uri) throws MalformedURLException,
                                             IOException,
                                             ProtocolException {
        URL url = new URL(uri);
        
        URLConnection con = url.openConnection();
        HttpURLConnection httpCon = (HttpURLConnection) con;
        httpCon.setRequestMethod( "HEAD" );
        
        
        
        //if this is null, then its not cool
        System.err.println(httpCon.getHeaderField( "lastModified" ));
        
        //can check for '200 OK' to make sure its kosher.
        System.err.println(httpCon.getHeaderFields());
        
        String status = httpCon.getHeaderField( null );
        
        
        System.err.println(status);
        
        httpCon.disconnect();
    }
    
    public static void main(String[] args) throws Exception {
        HttpClient scan = new HttpClient("http://localhost:8888/org.drools.brms.JBRMS/package/com.billasurf.manufacturing.plant/SNAP");
        
    }
    
}
