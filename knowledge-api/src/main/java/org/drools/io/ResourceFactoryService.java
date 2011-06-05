/*
 * Copyright 2010 JBoss Inc
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

package org.drools.io;

import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;

import org.drools.Service;

/**
 * ResourceFactoryService is used by the ResourceFactory to "provide" it's concrete implementation.
 * 
 * This class is not considered stable and may change, the user is protected from this change by using 
 * the Factory api, which is considered stable.
 */
public interface ResourceFactoryService extends Service {
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
