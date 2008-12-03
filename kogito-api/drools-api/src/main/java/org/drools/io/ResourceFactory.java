package org.drools.io;

import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;
import java.util.Properties;

import org.drools.ProviderInitializationException;
import org.drools.event.io.ResourceChangeNotifier;

public class ResourceFactory {
    private static ResourceProvider resourceProvider;
       
    
    public  static ResourceChangeNotifier getResourceChangeNotifierService() { 
        return getResourceProvider().getResourceChangeNotifierService();
    }       

    public static ResourceChangeScanner getResourceChangeScannerService() {
        return getResourceProvider().getResourceChangeScannerService();
    }
    
    public static Resource newUrlResource(URL url) {
        return getResourceProvider().newUrlResource( url );
    }
    
    public static Resource newUrlResource(String path) {
        return getResourceProvider().newUrlResource( path );
    }    
    
    public static Resource newFileResource(File file) {
        return getResourceProvider().newFileSystemResource( file );
    }
    
    public static Resource newFileResource(String fileName) {
        return getResourceProvider().newFileSystemResource( fileName );
    }
        
    
    public static Resource newByteArrayResource(byte[] bytes) {
        return getResourceProvider().newByteArrayResource( bytes );
    }
    
    public static Resource newInputStreamResource(InputStream stream) {
        return getResourceProvider().newInputStreamResource( stream );
    }
    
    public static Resource newReaderResource(Reader reader) {
        return getResourceProvider().newReaderResource( reader );
    }
    
    public static Resource newReaderResource(Reader reader, String encoding) {
        return getResourceProvider().newReaderResource( reader, encoding );
    }  
    
    public static Resource newClassPathResource(String path) {
        return getResourceProvider().newClassPathResource( path );
    }
    
    public static Resource newClassPathResource(String path, Class clazz) {
        return getResourceProvider().newClassPathResource( path, clazz );
    }     
    
    public static Resource newClassPathResource(String path, ClassLoader classLoader) {
        return getResourceProvider().newClassPathResource( path, classLoader );
    }  
    
    private static synchronized void setResourceProvider(ResourceProvider provider) {
        ResourceFactory.resourceProvider = provider;
    }

    private static synchronized ResourceProvider getResourceProvider() {
        if ( resourceProvider == null ) {
            loadProvider();
        }
        return resourceProvider;
    }

    private static void loadProvider() {
        try {
            Class<ResourceProvider> cls = (Class<ResourceProvider>) Class.forName( "org.drools.io.impl.ResourceProviderImpl" );
            setResourceProvider( cls.newInstance() );
        } catch ( Exception e2 ) {
            throw new ProviderInitializationException( "Provider org.drools.io.impl.ResourceProviderImpl could not be set.",
                                                       e2 );
        }
    }    
        
}
