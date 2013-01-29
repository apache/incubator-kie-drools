/*
 * Copyright 2012 JBoss by Red Hat.
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
package org.jbpm.shared.services.api;


import java.io.OutputStream;
import org.kie.commons.java.nio.file.Path;

/**
 *
 * @author salaboy
 */
public interface FileService {

    void init() throws FileException;
    
    public void fetchChanges();

    byte[] loadFile(String file) throws FileException;
    
    byte[] loadFile(Path file) throws FileException;
    
    Iterable<Path> listDirectories(final String path);

    Iterable<Path> loadFilesByType(final String path, final String fileType) throws FileException;
  
    boolean exists(Path file);
    
    boolean exists(String file);
    
    void move(String source, String dest);
    
    void copy(String source, String dest);
    
    Path createDirectory(String path);
    
    Path createFile(String path);
    
    boolean deleteIfExists(String path);
    
    OutputStream openFile(String path);
    
}
