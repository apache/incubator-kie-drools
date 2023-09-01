package org.kie.api.io;

import java.io.File;
import java.io.InputStream;
import java.io.Reader;

import org.kie.api.definition.KieDescr;
import org.kie.api.internal.utils.KieService;

/**
 * KieResources is a factory that provides Resource implementations for the desired IO resource
 */
public interface KieResources extends KieService {

    Resource newFileSystemResource(File file);

    Resource newFileSystemResource(File file,
                                   String encoding);

    Resource newFileSystemResource(String fileName);

    Resource newFileSystemResource(String fileName,
                                   String encoding);

    Resource newByteArrayResource(byte[] bytes);

    Resource newByteArrayResource(byte[] bytes,
                                  String encoding);

    Resource newInputStreamResource(InputStream stream);

    Resource newInputStreamResource(InputStream stream,
                                    String encoding);

    Resource newReaderResource(Reader reader);

    Resource newReaderResource(Reader reader,
                               String encoding);

    Resource newClassPathResource(String path);

    Resource newClassPathResource(String path,
                                  ClassLoader classLoader);

    Resource newClassPathResource(String path,
                                  Class<?> clazz);

    Resource newClassPathResource(String path,
                                  String encoding);

    Resource newClassPathResource(String path,
                                  String encoding,
                                  ClassLoader classLoader);

    Resource newClassPathResource(String path,
                                  String encoding,
                                  Class<?> clazz);

    Resource newDescrResource( KieDescr descr );
}
