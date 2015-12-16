/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.impl;

import org.drools.definition.KnowledgeDescr;
import org.drools.impl.adapters.ResourceAdapter;
import org.drools.io.Resource;
import org.drools.io.ResourceChangeNotifier;
import org.drools.io.ResourceChangeScanner;
import org.drools.io.ResourceFactoryService;

import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;

public class ResourceFactoryServiceImpl implements ResourceFactoryService {

    private final org.drools.core.io.impl.ResourceFactoryServiceImpl delegate = new org.drools.core.io.impl.ResourceFactoryServiceImpl();

    public ResourceChangeNotifier getResourceChangeNotifierService() {
        throw new UnsupportedOperationException("This operation is no longer supported");
    }

    public ResourceChangeScanner getResourceChangeScannerService() {
        throw new UnsupportedOperationException("This operation is no longer supported");
    }

    public Resource newUrlResource(URL url) {
        return new ResourceAdapter(delegate.newUrlResource(url));
    }

    public Resource newUrlResource(String path) {
        return new ResourceAdapter(delegate.newUrlResource(path));
    }

    public Resource newFileSystemResource(File file) {
        return new ResourceAdapter(delegate.newFileSystemResource(file));
    }

    public Resource newFileSystemResource(String fileName) {
        return new ResourceAdapter(delegate.newFileSystemResource(fileName));
    }

    public Resource newByteArrayResource(byte[] bytes) {
        return new ResourceAdapter(delegate.newByteArrayResource(bytes));
    }

    public Resource newInputStreamResource(InputStream stream) {
        return new ResourceAdapter(delegate.newInputStreamResource(stream));
    }

    public Resource newInputStreamResource(InputStream stream, String encoding) {
        return new ResourceAdapter(delegate.newInputStreamResource(stream, encoding));
    }

    public Resource newReaderResource(Reader reader) {
        return new ResourceAdapter(delegate.newReaderResource(reader));
    }

    public Resource newReaderResource(Reader reader, String encoding) {
        return new ResourceAdapter(delegate.newReaderResource(reader, encoding));
    }

    public Resource newClassPathResource(String path) {
        return new ResourceAdapter(delegate.newClassPathResource(path));
    }

    public Resource newClassPathResource(String path, ClassLoader classLoader) {
        return new ResourceAdapter(delegate.newClassPathResource(path, classLoader));
    }

    public Resource newClassPathResource(String path, Class<?> clazz) {
        return new ResourceAdapter(delegate.newClassPathResource(path, clazz));
    }

    public Resource newClassPathResource(String path, String encoding) {
        return new ResourceAdapter(delegate.newClassPathResource(path, encoding));
    }

    public Resource newClassPathResource(String path, String encoding, ClassLoader classLoader) {
        return new ResourceAdapter(delegate.newClassPathResource(path, encoding, classLoader));
    }

    public Resource newClassPathResource(String path, String encoding, Class<?> clazz) {
        return new ResourceAdapter(delegate.newClassPathResource(path, encoding, clazz));
    }

    public Resource newDescrResource(KnowledgeDescr descr) {
        return new ResourceAdapter(delegate.newDescrResource(descr));
    }
}
