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

package org.kie.submarine.codegen.rules;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.drools.compiler.compiler.io.memory.MemoryFile;
import org.drools.compiler.compiler.io.memory.MemoryFileSystem;
import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.drools.compiler.kie.builder.impl.KieBuilderImpl;
import org.drools.compiler.kie.builder.impl.MemoryKieModule;
import org.drools.modelcompiler.CanonicalKieModule;
import org.kie.api.KieServices;
import org.kie.submarine.codegen.ConfigGenerator;
import org.kie.submarine.codegen.GeneratedFile;
import org.kie.submarine.codegen.Generator;

import com.github.javaparser.ast.body.MethodDeclaration;

public class RuleCodegen implements Generator {

    private String packageName;
    private ModuleSourceClass moduleGenerator;

    public static RuleCodegen ofPath(Path path) throws IOException {
        return ofPath( path, false );
    }

    public static RuleCodegen ofPath(Path path, boolean oneClassPerRule) throws IOException {
        KieServices ks = KieServices.Factory.get();
        return new RuleCodegen((KieBuilderImpl) ks.newKieBuilder(path.toFile()), oneClassPerRule, Collections.emptyList());
    }

    public static RuleCodegen ofFiles(Path basePath, Collection<File> files) throws IOException {
        KieServices ks = KieServices.Factory.get();
        return new RuleCodegen((KieBuilderImpl) ks.newKieBuilder(basePath.toFile()), true, files);
    }

    private final boolean oneClassPerRule;

    private final KieBuilderImpl kieBuilder;
    /**
     * will compile iff returns true for the given file
     */
    private final Predicate<String> fileFilter;

    private boolean dependencyInjection;

    public RuleCodegen( KieBuilderImpl kieBuilder, boolean oneClassPerRule, Collection<File> files ) {
        this.kieBuilder = kieBuilder;
        this.oneClassPerRule = oneClassPerRule;
        if (files.isEmpty()) {
            this.fileFilter = f -> true;
        } else {
            this.fileFilter = fname ->
                    files.stream()
                            .map(f -> f.getPath())
                            .anyMatch(f -> f.contains(fname));
        }
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
        this.moduleGenerator = new ModuleSourceClass(packageName);
    }

    private MemoryFileSystem getMemoryFileSystem(InternalKieModule kieModule) {
        return kieModule instanceof CanonicalKieModule ?
                ((MemoryKieModule) ((CanonicalKieModule) kieModule).getInternalKieModule()).getMemoryFileSystem() :
                ((MemoryKieModule) kieModule).getMemoryFileSystem();
    }

    @Override
    public Collection<MethodDeclaration> factoryMethods() {
        return moduleGenerator.factoryMethods();
    }

    public List<GeneratedFile> generate() {
        kieBuilder.buildAll(
                (km, cl) ->
                        new RuleCodegenProject(km, cl)
                                .withModuleGenerator(moduleGenerator)
                                .withOneClassPerRule(oneClassPerRule)
                                .withCdi(dependencyInjection),
                s -> {
                    return !s.contains("src/test/java")
                            && !s.endsWith("bpmn")
                            && !s.endsWith("bpmn2");
                }
        );

        InternalKieModule kieModule = (InternalKieModule) kieBuilder.getKieModule();

        MemoryFileSystem mfs = getMemoryFileSystem(kieModule);

        return kieModule.getFileNames()
                .stream()
                .filter(f -> f.endsWith("java"))
                .map(mfs::getFile)
                .map(MemoryFile.class::cast)
                .map(f -> new GeneratedFile(GeneratedFile.Type.RULE, f.getPath().toPortableString(), mfs.getFileContents(f)))
                .collect(Collectors.toList());
    }

    @Override
    public void updateConfig(ConfigGenerator cfg) {
        // no config yet
    }

    public ModuleSourceClass moduleGenerator() {
        return moduleGenerator;
    }

    public void setDependencyInjection(boolean di) {
        this.dependencyInjection = di;
    }
}
