/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.efesto.compilationmanager.api.model;

import java.nio.file.Path;
import java.util.Map;
import java.util.ServiceLoader;

import org.kie.efesto.common.api.io.IndexFile;
import org.kie.efesto.common.api.listener.EfestoListener;
import org.kie.efesto.common.api.model.EfestoContext;
import org.kie.efesto.compilationmanager.api.service.KieCompilerService;

/**
 *
 * Wrap MemoryCompilerClassLoader and convey generated classes to be used by other CompilationManager or RuntimeManager
 *
 */
public interface EfestoCompilationContext<T extends EfestoListener> extends EfestoContext<T> {

    Map<String, byte[]> compileClasses(Map<String, String> sourcesMap);

    void loadClasses(Map<String, byte[]> compiledClassesMap);
    ServiceLoader<KieCompilerService> getKieCompilerServiceLoader();

    byte[] getCode(String name);

    default Map<String, IndexFile> createIndexFiles(Path targetDirectory) {
        throw new UnsupportedOperationException();
    }
}
