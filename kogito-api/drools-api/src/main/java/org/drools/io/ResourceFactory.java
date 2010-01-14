package org.drools.io;

import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;

import org.drools.KnowledgeBaseProvider;
import org.drools.util.internal.ServiceRegistryImpl;

/**
 * <p>
 * Convenience Factory to provide Resource implementations for the desired IO resource.
 * </p>
 * 
 * <pre>
 * KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
 * kbuilder.add( ResourceFactory.newUrlResource( "htp://www.domain.org/myflow.drf" ),
 *                ResourceType.DRF );
 * </pre
 * 
 * <pre>
 * KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
 * kbuilder.add( ResourceFactory.newClassPathResource( "htp://www.domain.org/myrules.drl", getClass() ),
 *               ResourceType.DRL );
 * </pre
 *
 */
public class ResourceFactory {
    private static ResourceProvider resourceProvider;

    /**
     * A Service that can be started, to provide notifications of changed Resources.
     * 
     * @return
     */
    public static ResourceChangeNotifier getResourceChangeNotifierService() {
        return getResourceProvider().getResourceChangeNotifierService();
    }

    /**
     * As service, that scans the disk for changes, this acts as a Monitor for the Notifer service.
     * 
     * @return
     */
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

    public static Resource newReaderResource(Reader reader,
                                             String encoding) {
        return getResourceProvider().newReaderResource( reader,
                                                        encoding );
    }

    public static Resource newClassPathResource(String path) {
        return getResourceProvider().newClassPathResource( path );
    }

    public static Resource newClassPathResource(String path,
                                                Class clazz) {
        return getResourceProvider().newClassPathResource( path,
                                                           clazz );
    }

    public static Resource newClassPathResource(String path,
                                                ClassLoader classLoader) {
        return getResourceProvider().newClassPathResource( path,
                                                           classLoader );
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
        setResourceProvider( ServiceRegistryImpl.getInstance().get( ResourceProvider.class ) );
    }

}
