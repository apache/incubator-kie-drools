package org.drools.io.impl;

import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;

import org.drools.SystemEventListener;
import org.drools.agent.impl.PrintStreamSystemEventListener;
import org.drools.io.Resource;
import org.drools.io.ResourceChangeNotifier;
import org.drools.io.ResourceChangeScanner;
import org.drools.io.ResourceFactoryService;
import org.drools.util.DelegatingSystemEventListener;

public class ResourceFactoryServiceImpl
    implements
    ResourceFactoryService {

    private ResourceChangeNotifier notifier;
    private ResourceChangeScanner  scanner;
    private Object                 lock     = new Object();    

    public ResourceChangeNotifier getResourceChangeNotifierService() {
        synchronized ( this.lock ) {
            if ( this.notifier == null ) {
                this.notifier = new ResourceChangeNotifierImpl( );
            }
            return this.notifier;
        }
    }

    public ResourceChangeScanner getResourceChangeScannerService() {
        synchronized ( this.lock ) {
            if ( scanner == null ) {
                this.scanner = new ResourceChangeScannerImpl( );
            }
            return this.scanner;
        }
    }

    public Resource newByteArrayResource(byte[] bytes) {
        return new ByteArrayResource( bytes );
    }

    public Resource newClassPathResource(String path) {
        return new ClassPathResource( path );
    }

    public Resource newClassPathResource(String path,
                                         ClassLoader classLoader) {
        return new ClassPathResource( path,
                                      classLoader );
    }

    public Resource newClassPathResource(String path,
                                         Class clazz) {
        return new ClassPathResource( path,
                                      clazz );
    }

    public Resource newFileSystemResource(File file) {
        return new FileSystemResource( file );
    }

    public Resource newFileSystemResource(String fileName) {
        return new FileSystemResource( fileName );
    }

    public Resource newInputStreamResource(InputStream stream) {
        return new InputStreamResource( stream );
    }

    public Resource newReaderResource(Reader reader) {
        return new ReaderResource( reader );
    }

    public Resource newReaderResource(Reader reader,
                                      String encoding) {
        return new ReaderResource( reader,
                                   encoding );
    }

    public Resource newUrlResource(URL url) {
        return new UrlResource( url );
    }

    public Resource newUrlResource(String path) {
        return new UrlResource( path );
    }
}
