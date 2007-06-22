package org.drools.agent;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Properties;

import org.drools.RuleBase;
import org.drools.RuntimeDroolsException;


public class URLScanner extends PackageProvider {
    
    private List uriList;
    private File localCacheDir;
    private URL[] urls;
    
    void configure(Properties config) {
        uriList = RuleAgent.list( config.getProperty( RuleAgent.URLS ) );
        urls = new URL[uriList.size()];
        for (int i = 0; i < uriList.size(); i++ ) {
            String url = (String) uriList.get( i );
            try {
                urls[i] = new URL(url);
            } catch ( MalformedURLException e ) {
                throw new RuntimeException("The URL " + url + " is not valid.", e);
            }
        }
        String localCache = config.getProperty( RuleAgent.LOCAL_URL_CACHE );
        if (localCache != null) {
            localCacheDir = new File(localCache);
            if (!localCacheDir.isDirectory()) {
                throw new RuntimeDroolsException("The local cache dir " + localCache + " is a file, not a directory.");
            }

        }
    }

    
    /**
     * Return the full url in string form.  
     */
    static String getURL(URL u) {
        return u.getProtocol() + "://" + u.getHost() + ":" + u.getPort()  + u.getPath();
    }

    void updateRuleBase(RuleBase rb, boolean removeExistingPackages) {
        URL url;
    }
        


}
