package org.drools.agent;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.drools.RuntimeDroolsException;
import org.drools.common.DroolsObjectOutputStream;
import org.drools.rule.Package;

public class URLScanner extends PackageProvider {

    //this is the URLs we are managing
    URL[]       urls;
    String username;
    String password;

    //this is only set if we are using a local cache - only fall back on this
    //when URL connection is not available.
    FileScanner localCacheFileScanner;

    //this is used to access the remote resources
    IHttpClient httpClient = new HttpClientImpl();

    //a record of the last updated URL timestamps
    Map         lastUpdated = new HashMap();

    File        localCacheDir;

    void configure(Properties config) {   	
        username = config.getProperty( RuleAgent.USER_NAME );
        password = config.getProperty( RuleAgent.PASSWORD );

        List uriList = RuleAgent.list( config.getProperty( RuleAgent.URLS ) );
        urls = new URL[uriList.size()];
        for ( int i = 0; i < uriList.size(); i++ ) {
            String url = (String) uriList.get( i );
            try {
                urls[i] = new URL( url );
            } catch ( MalformedURLException e ) {
                throw new RuntimeException( "The URL " + url + " is not valid.",
                                            e );
            }
        }

        //if we have a local cache, check its all kosher
        String localCache = config.getProperty( RuleAgent.LOCAL_URL_CACHE );
        if ( localCache != null ) {
            localCacheDir = new File( localCache );
            if ( !localCacheDir.isDirectory() ) {
                throw new RuntimeDroolsException( "The local cache dir " + localCache + " is a file, not a directory." );
            }
            this.localCacheFileScanner = new FileScanner();
            this.localCacheFileScanner.setFiles( getFiles( urls, localCacheDir ) );
            this.localCacheFileScanner.setAgentListener( this.listener );
        }
    }
    
    public void setAgentListener(AgentEventListener listener) {
        super.setAgentListener( listener );
        if ( this.localCacheFileScanner != null ) {
            this.localCacheFileScanner.setAgentListener( listener );
        }
    }    

    File[] getFiles(URL[] urls, File cacheDir) {
        File[] fs = new File[urls.length];
        for ( int i = 0; i < urls.length; i++ ) {
            URL u = urls[i];
            File f = getLocalCacheFileForURL( cacheDir, u );
            fs[i] = f;
        }
        return fs;
    }

    static File getLocalCacheFileForURL(File cacheDir, URL u) {
        File f;
        try {
            f = new File( cacheDir,
                          URLEncoder.encode(  u.toExternalForm(), "UTF-8" ) + ".pkg" );
        } catch ( UnsupportedEncodingException e ) {
            throw new RuntimeDroolsException( e );
        }
        return f;
    }



    PackageChangeInfo loadPackageChanges() { //void updateRuleBase(RuleBase rb, boolean removeExistingPackages) {
    	PackageChangeInfo changes = null;
        try {
            changes = getChangeSet();
            return changes;
        } catch ( IOException e ) {
        	listener.exception( e );
            if (this.localCacheFileScanner != null) {
                listener.warning( "Falling back to local cache." );
                return localCacheFileScanner.loadPackageChanges();
            }
        } catch ( ClassNotFoundException e ) {
            this.listener.exception( e );
            this.listener.warning( "Was unable to load a class when loading a package. Perhaps it is missing from this application." );
        }
        return null;
    }

    private PackageChangeInfo getChangeSet() throws IOException, ClassNotFoundException {
    	
    	PackageChangeInfo info = new PackageChangeInfo();

        if ( this.urls == null ) return info;
        
        for ( int i = 0; i < urls.length; i++ ) {
            URL u = urls[i];
            if ( hasChanged( u, this.lastUpdated) ) {
                Package p = readPackage( u );
                if ( p == null ) return null;
                info.addPackage( p );
                if (localCacheDir != null) {
                    writeLocalCacheCopy(p, u, localCacheDir);
                }
            }
        }
        return info;
    }

    private void writeLocalCacheCopy(Package p, URL u, File localCacheDir) {
        File local = getLocalCacheFileForURL( localCacheDir, u );
        if (local.exists()) local.delete();

        try {
            ObjectOutput out = new DroolsObjectOutputStream(new FileOutputStream(local));
            out.writeObject( p );
            out.flush();
            out.close();
        } catch (IOException e) {
            listener.exception( e );
            listener.warning( "Was an error with the local cache directory " + localCacheDir.getPath() );
        }

    }

    private Package readPackage(URL u) throws IOException, ClassNotFoundException {
        return httpClient.fetchPackage( u, username, password );
    }

    private boolean hasChanged(URL u, Map updates) throws IOException {
        LastUpdatedPing pong = httpClient.checkLastUpdated( u );
        if (pong.isError()) {
            listener.warning( "Was an error contacting " + u.toExternalForm() + ". Reponse header: " + pong.responseMessage );
            throw new IOException("Was unable to reach server.");
        }

        String url = u.toExternalForm();
        if ( !updates.containsKey( url ) ) {
            updates.put( url,
                         new Long( pong.lastUpdated ) );
            return true;
        } else {
            Long last = (Long) updates.get( url );
            if ( last.longValue() < pong.lastUpdated ) {
                updates.put( url,
                             new Long( pong.lastUpdated ) );
                return true;
            } else {
                return false;
            }
        }
    }
    
    

    public String toString() {
        String s = "URLScanner monitoring URLs: ";
        if (this.urls != null) {
            for ( int i = 0; i < urls.length; i++ ) {
                URL url = urls[i];
                s = s + " " + url.toExternalForm();
            }
        }
        if (this.localCacheDir != null) {
            s = s + " with local cache dir of " + this.localCacheDir.getPath();
        }
        return s;
    }

}
