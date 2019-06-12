/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.codegen.rules.legacy;

import java.io.File;
import java.util.function.BiFunction;

import org.drools.compiler.commons.jci.readers.DiskResourceReader;
import org.drools.compiler.commons.jci.readers.ResourceReader;
import org.drools.compiler.compiler.io.memory.MemoryFileSystem;
import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.drools.compiler.kie.builder.impl.KieMetaInfoBuilder;
import org.drools.compiler.kie.builder.impl.KieModuleKieProject;
import org.drools.compiler.kie.builder.impl.MemoryKieModule;
import org.drools.compiler.kie.builder.impl.ResultsImpl;
import org.drools.compiler.kproject.ReleaseIdImpl;
import org.kie.api.builder.Message.Level;
import org.kie.api.builder.Results;
import org.kie.api.builder.model.KieModuleModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.drools.compiler.kie.builder.impl.KieBuilderImpl.setDefaultsforEmptyKieModule;
/**
 *  A severely limited version of {@link org.drools.compiler.kie.builder.impl.KieBuilderImpl}
 */
public class KogitoBuilder {

    private static final Logger log = LoggerFactory.getLogger(KogitoBuilder.class);

    private ResultsImpl results;
    private final ResourceReader srcMfs;

    private final ClassLoader classLoader;
    private MemoryKieModule memoryKieModule;

    public KogitoBuilder(File file) {
        this(file, null);
    }

    public KogitoBuilder(File file,
                         ClassLoader classLoader) {
        this.classLoader = classLoader;
        this.srcMfs = new DiskResourceReader(file);
    }

    public Results getResults() {
        return results;
    }

    public MemoryKieModule getMemoryKieModule() {
        return memoryKieModule;
    }

    public void buildAll(
            BiFunction<InternalKieModule, ClassLoader, KieModuleKieProject> kprojectSupplier,
            KieModuleModel kieModule) {
        results = new ResultsImpl();
        setDefaultsforEmptyKieModule(kieModule);

        MemoryFileSystem trgMfs = new MemoryFileSystem();
        for (String fileName : srcMfs.getFileNames()) {
            copySourceToTarget(fileName, srcMfs, trgMfs);
        }

        MemoryKieModule memoryKieModule = new MemoryKieModule(
                new ReleaseIdImpl("org.kie.kogito", "noname", "0.0.0"),
                kieModule, trgMfs);

        KieModuleKieProject kProject = kprojectSupplier.apply(memoryKieModule, classLoader);

        kProject.init();
        kProject.verify(results); // starts the compilation process
        kProject.writeProjectOutput(trgMfs, results);

        //new KieMetaInfoBuilder(memoryKieModule).writeKieModuleMetaInfo(trgMfs);

        if (getResults().hasMessages(Level.ERROR)) {
            throw new RuntimeException("Unable to get KieModule, Errors Existed: " + getResults());
        }

        this.memoryKieModule = memoryKieModule;
    }

    private String copySourceToTarget(String fileName, ResourceReader srcMfs, MemoryFileSystem trgMfs) {
        byte[] bytes = srcMfs.getBytes(fileName);
        if (bytes != null) {
            trgMfs.write(fileName, bytes, true);
        } else {
            trgMfs.remove(fileName);
        }
        return fileName;
    }
}
