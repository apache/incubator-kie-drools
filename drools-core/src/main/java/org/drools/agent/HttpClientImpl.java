package org.drools.agent;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import org.drools.common.DroolsObjectInputStream;
import org.drools.rule.Package;

public class HttpClientImpl implements IHttpClient {

    
    
    




    
    
    
    public LastUpdatedPing checkLastUpdated(URL url) throws IOException {
        URLConnection con = url.openConnection();
        HttpURLConnection httpCon = (HttpURLConnection) con;
        try {
            httpCon.setRequestMethod( "HEAD" );
            
            String lm = httpCon.getHeaderField( "lastModified" );
            LastUpdatedPing ping = new LastUpdatedPing();
            
            ping.responseMessage = httpCon.getHeaderFields().toString();
            
            if (lm != null) {
                ping.lastUpdated = Long.parseLong( lm );
            }
            
            return ping;
        } finally {        
            httpCon.disconnect();
        }
        
    }

    public Package fetchPackage(URL url) throws IOException {
        URLConnection con = url.openConnection();
        HttpURLConnection httpCon = (HttpURLConnection) con;
        try {
            httpCon.setRequestMethod( "GET" );
            InputStream in = httpCon.getInputStream();
            
            DroolsObjectInputStream oin = new DroolsObjectInputStream(in);
            try {
                return (Package) oin.readObject();
            } catch ( ClassNotFoundException e ) {            
                e.printStackTrace();
                return null;
            }
        } finally {
            httpCon.disconnect();
        }
    }
    
    public static void main(String[] args) throws Exception {
        HttpClientImpl cl = new HttpClientImpl();
        URL url = new URL("http://localhost:8888/org.drools.brms.JBRMS/package/com.billasurf.manufacturing.plant/SNAP");
        
        
        LastUpdatedPing ping = cl.checkLastUpdated( url );
        
        
        Package p = cl.fetchPackage( url );
        
        
        System.err.println(ping);
        System.err.println( ping.isError() );
    }
    
}
