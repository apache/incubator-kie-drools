package org.drools.io;

import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;

import org.drools.Service;

/**
 * ResourceProvider is used by the ResourceFactory to "provide" it's concrete implementation.
 * 
 * This class is not considered stable and may change, the user is protected from this change by using 
 * the Factory api, which is considered stable.
 *
 */
public interface ResourceProvider extends Service {
    ResourceChangeNotifier getResourceChangeNotifierService();

    ResourceChangeScanner getResourceChangeScannerService();

    Resource newUrlResource(URL url);

    Resource newUrlResource(String path);

    Resource newFileSystemResource(File file);

    Resource newFileSystemResource(String fileName);

    Resource newByteArrayResource(byte[] bytes);

    Resource newInputStreamResource(InputStream stream);

    Resource newReaderResource(Reader reader);

    Resource newReaderResource(Reader reader,
                               String encoding);

    Resource newClassPathResource(String path);

    Resource newClassPathResource(String path,
                                  ClassLoader classLoader);

    Resource newClassPathResource(String path,
                                  Class clazz);
}
