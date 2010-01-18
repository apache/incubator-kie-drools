package org.drools.io;

import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;

import org.drools.KnowledgeBaseFactoryService;
import org.drools.util.ServiceRegistryImpl;

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
    private static ResourceFactoryService factoryService;

    /**
     * A Service that can be started, to provide notifications of changed Resources.
     * 
     * @return
     */
    public static ResourceChangeNotifier getResourceChangeNotifierService() {
        return getFactoryService().getResourceChangeNotifierService();
    }

    /**
     * As service, that scans the disk for changes, this acts as a Monitor for the Notifer service.
     * 
     * @return
     */
     public static ResourceChangeScanner getResourceChangeScannerService() {
        return getFactoryService().getResourceChangeScannerService();
    }

    public static Resource newUrlResource(URL url) {
        return getFactoryService().newUrlResource( url );
    }

    public static Resource newUrlResource(String path) {
        return getFactoryService().newUrlResource( path );
    }

    public static Resource newFileResource(File file) {
        return getFactoryService().newFileSystemResource( file );
    }

    public static Resource newFileResource(String fileName) {
        return getFactoryService().newFileSystemResource( fileName );
    }

    public static Resource newByteArrayResource(byte[] bytes) {
        return getFactoryService().newByteArrayResource( bytes );
    }

    public static Resource newInputStreamResource(InputStream stream) {
        return getFactoryService().newInputStreamResource( stream );
    }

    public static Resource newReaderResource(Reader reader) {
        return getFactoryService().newReaderResource( reader );
    }

    public static Resource newReaderResource(Reader reader,
                                             String encoding) {
        return getFactoryService().newReaderResource( reader,
                                                        encoding );
    }

    public static Resource newClassPathResource(String path) {
        return getFactoryService().newClassPathResource( path );
    }

    public static Resource newClassPathResource(String path,
                                                Class clazz) {
        return getFactoryService().newClassPathResource( path,
                                                           clazz );
    }

    public static Resource newClassPathResource(String path,
                                                ClassLoader classLoader) {
        return getFactoryService().newClassPathResource( path,
                                                           classLoader );
    }

    private static synchronized void setFactoryService(ResourceFactoryService factoryService) {
        ResourceFactory.factoryService = factoryService;
    }

    private static synchronized ResourceFactoryService getFactoryService() {
        if ( factoryService == null ) {
            loadFactoryService();
        }
        return factoryService;
    }

    private static void loadFactoryService() {
        setFactoryService( ServiceRegistryImpl.getInstance().get( ResourceFactoryService.class ) );
    }

}
