/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.core.io.impl;

import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;

import org.kie.api.definition.KieDescr;
import org.kie.api.io.KieResources;
import org.kie.api.io.Resource;

public class ResourceFactoryServiceImpl implements KieResources {

    public Resource newByteArrayResource(byte[] bytes) {
        return new ByteArrayResource( bytes );
    }

    public Resource newByteArrayResource(byte[] bytes,
                                         String encoding) {
        return new ByteArrayResource( bytes, encoding );
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
                                         Class<?> clazz) {
        return new ClassPathResource( path,
                                      clazz );
    }

    public Resource newClassPathResource(String path,
                                         String encoding) {
        return new ClassPathResource( path,
                                      encoding );
    }

    public Resource newClassPathResource(String path,
                                         String encoding,
                                         ClassLoader classLoader) {
        return new ClassPathResource( path,
                                      encoding,
                                      classLoader );
    }

    public Resource newClassPathResource(String path,
                                         String encoding,
                                         Class<?> clazz) {
        return new ClassPathResource( path,
                                      encoding,
                                      clazz );
    }
    
    public Resource newFileSystemResource(File file) {
        return new FileSystemResource( file );
    }

    public Resource newFileSystemResource(File file,
                                          String encoding) {
        return new FileSystemResource( file, encoding );
    }

    public Resource newFileSystemResource(String fileName) {
        return new FileSystemResource( fileName );
    }

    public Resource newFileSystemResource(String fileName,
                                          String encoding) {
        return new FileSystemResource( fileName, encoding );
    }

    public Resource newInputStreamResource(InputStream stream) {
        return new InputStreamResource( stream );
    }

    public Resource newInputStreamResource(InputStream stream,
                                           String encoding) {
        return new InputStreamResource( stream,
                                        encoding);
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

    public Resource newUrlResource(URL url,
                                   String encoding) {
        return new UrlResource( url, encoding );
    }

    public Resource newUrlResource(String path) {
        return new UrlResource( path );
    }

    public Resource newUrlResource(String path,
                                   String encoding) {
        return new UrlResource( path, encoding );
    }

    public Resource newDescrResource( KieDescr descr ) {
        return new DescrResource( descr );
    }
}
