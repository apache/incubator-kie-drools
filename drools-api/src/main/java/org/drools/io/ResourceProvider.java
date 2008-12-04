package org.drools.io;

import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;


public interface ResourceProvider {
    ResourceChangeNotifier getResourceChangeNotifierService();       

    ResourceChangeScanner getResourceChangeScannerService();
           
    Resource newUrlResource(URL url);
    
    Resource newUrlResource(String path);    
    
    Resource newFileSystemResource(File file);
    
    Resource newFileSystemResource(String fileName);
            
    Resource newByteArrayResource(byte[] bytes);
    
    Resource newInputStreamResource(InputStream stream);
    
    Resource newReaderResource(Reader reader);
    
    Resource newReaderResource(Reader reader, String encoding);
    
    Resource newClassPathResource(String path);
        
    Resource newClassPathResource(String path, ClassLoader classLoader);
    
    Resource newClassPathResource(String path, Class clazz);
}
