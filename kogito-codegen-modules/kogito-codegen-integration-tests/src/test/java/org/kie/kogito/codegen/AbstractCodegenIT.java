/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.kogito.codegen;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiFunction;

import org.drools.codegen.common.GeneratedFile;
import org.drools.compiler.compiler.io.memory.MemoryFileSystem;
import org.drools.util.PortablePath;
import org.kie.kogito.Application;
import org.kie.kogito.codegen.api.AddonsConfig;
import org.kie.kogito.codegen.api.Generator;
import org.kie.kogito.codegen.api.context.KogitoBuildContext;
import org.kie.kogito.codegen.api.context.impl.JavaKogitoBuildContext;
import org.kie.kogito.codegen.api.io.CollectedResource;
import org.kie.kogito.codegen.core.ApplicationGenerator;
import org.kie.kogito.codegen.core.io.CollectedResourceProducer;
import org.kie.kogito.codegen.decision.DecisionCodegen;
import org.kie.kogito.codegen.prediction.PredictionCodegenFactory;
import org.kie.kogito.codegen.rules.RuleCodegen;
import org.kie.memorycompiler.CompilationResult;
import org.kie.memorycompiler.JavaCompiler;
import org.kie.memorycompiler.JavaCompilerFactory;
import org.kie.memorycompiler.JavaConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;

public class AbstractCodegenIT {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractCodegenIT.class);

    /**
     * Order matters here because inside {@link AbstractCodegenIT#generateCode(Map)} it is the order used to invoke
     *
     * {@link ApplicationGenerator#registerGeneratorIfEnabled(Generator) }
     */
    protected enum TYPE {
        RULES,
        DECISION,
        JAVA,
        PREDICTION
    }

    private TestClassLoader classloader;
    private AddonsConfig addonsConfig = AddonsConfig.DEFAULT;

    private static final JavaCompiler JAVA_COMPILER = JavaCompilerFactory.loadCompiler(JavaConfiguration.CompilerType.NATIVE, "11");
    private static final String TEST_JAVA = "src/test/java/";
    private static final String TEST_RESOURCES = "src/test/resources";

    private static final Map<TYPE, BiFunction<KogitoBuildContext, List<String>, Generator>> generatorTypeMap = new HashMap<>();

    static {
        generatorTypeMap.put(TYPE.RULES, (context, strings) -> RuleCodegen.ofCollectedResources(context, toCollectedResources(TEST_RESOURCES, strings)));
        generatorTypeMap.put(TYPE.DECISION, (context, strings) -> DecisionCodegen.ofCollectedResources(context, toCollectedResources(TEST_RESOURCES, strings)));
        generatorTypeMap.put(TYPE.JAVA, (context, strings) -> RuleCodegen.ofJavaResources(context,
                toCollectedResources(TEST_JAVA, strings)));
        generatorTypeMap.put(TYPE.PREDICTION,
                (context, strings) -> PredictionCodegenFactory.ofCollectedResources(context,
                        toCollectedResources(TEST_RESOURCES, strings)));
    }

    private static Collection<CollectedResource> toCollectedResources(String basePath, List<String> strings) {
        File[] files = strings
                .stream()
                .map(resource -> new File(basePath, resource))
                .toArray(File[]::new);
        return CollectedResourceProducer.fromFiles(Paths.get(basePath), files);
    }

    protected Application generateRulesFromJava(String... javaSourceCode) throws Exception {
        Map<TYPE, List<String>> resourcesTypeMap = new HashMap<>();
        resourcesTypeMap.put(TYPE.JAVA, Arrays.asList(javaSourceCode));
        return generateCode(resourcesTypeMap);
    }

    protected Application generateCode(Map<TYPE, List<String>> resourcesTypeMap) throws Exception {
        return generateCode(resourcesTypeMap, this.newContext());
    }

    protected Application generateCode(Map<TYPE, List<String>> resourcesTypeMap, KogitoBuildContext context) throws Exception {
        ApplicationGenerator appGen = new ApplicationGenerator(context);

        for (TYPE type : TYPE.values()) {
            if (resourcesTypeMap.containsKey(type) && !resourcesTypeMap.get(type).isEmpty()) {
                appGen.registerGeneratorIfEnabled(generatorTypeMap.get(type).apply(context, resourcesTypeMap.get(type)));
            }
        }

        Collection<GeneratedFile> generatedFiles = appGen.generate();

        MemoryFileSystem srcMfs = new MemoryFileSystem();
        MemoryFileSystem trgMfs = new MemoryFileSystem();

        List<String> sources = new ArrayList<>();
        for (GeneratedFile entry : generatedFiles) {
            String fileName = entry.relativePath();
            if (!fileName.endsWith(".java")) {
                continue;
            }
            sources.add(fileName);
            srcMfs.write(fileName, entry.contents());
            log(new String(entry.contents()));
        }

        if (LOGGER.isDebugEnabled()) {
            Path temp = Files.createTempDirectory("KOGITO_TESTS");
            LOGGER.debug("Dumping generated files in " + temp);
            for (GeneratedFile entry : generatedFiles) {
                Path fpath = temp.resolve(entry.relativePath());
                fpath.getParent().toFile().mkdirs();
                Files.write(fpath, entry.contents());
            }
        }

        CompilationResult result = JAVA_COMPILER.compile(sources.toArray(new String[0]), srcMfs, trgMfs, this.getClass().getClassLoader());
        assertThat(result).isNotNull();
        assertThat(result.getErrors()).describedAs(String.join("\n\n", Arrays.toString(result.getErrors()))).isEmpty();

        classloader = new TestClassLoader(this.getClass().getClassLoader(), trgMfs.getMap());

        @SuppressWarnings("unchecked")
        Class<Application> app = (Class<Application>) Class.forName(context.getPackageName() + ".Application", true, classloader);

        return app.getDeclaredConstructor().newInstance();
    }

    protected KogitoBuildContext newContext() {
        return JavaKogitoBuildContext.builder()
                .withApplicationProperties(new File(TEST_RESOURCES))
                .withPackageName(this.getClass().getPackage().getName())
                .withAddonsConfig(addonsConfig)
                .build();
    }

    protected void log(String content) {
        LOGGER.debug(content);
    }

    private static class TestClassLoader extends URLClassLoader {

        private final Map<String, byte[]> extraClassDefs;

        public TestClassLoader(ClassLoader parent, Map<PortablePath, byte[]> extraClassDefs) {
            super(new URL[0], parent);
            this.extraClassDefs = new HashMap<>();

            for (Entry<PortablePath, byte[]> entry : extraClassDefs.entrySet()) {
                this.extraClassDefs.put(entry.getKey().asString().replace('/', '.').replaceFirst("\\.class", ""), entry.getValue());
            }
        }

        @Override
        protected Class<?> findClass(final String name) throws ClassNotFoundException {
            byte[] classBytes = this.extraClassDefs.remove(name);
            if (classBytes != null) {
                return defineClass(name, classBytes, 0, classBytes.length);
            }
            return super.findClass(name);
        }
    }
}
