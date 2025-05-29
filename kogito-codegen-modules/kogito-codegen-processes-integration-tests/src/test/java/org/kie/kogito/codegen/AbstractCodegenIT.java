/*
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
import org.junit.platform.commons.util.ClassLoaderUtils;
import org.kie.kogito.Application;
import org.kie.kogito.codegen.api.AddonsConfig;
import org.kie.kogito.codegen.api.Generator;
import org.kie.kogito.codegen.api.context.KogitoBuildContext;
import org.kie.kogito.codegen.api.context.impl.JavaKogitoBuildContext;
import org.kie.kogito.codegen.api.io.CollectedResource;
import org.kie.kogito.codegen.core.ApplicationGenerator;
import org.kie.kogito.codegen.core.io.CollectedResourceProducer;
import org.kie.kogito.codegen.process.ProcessCodegen;
import org.kie.kogito.codegen.usertask.UserTaskCodegen;
import org.kie.memorycompiler.CompilationResult;
import org.kie.memorycompiler.JavaCompiler;
import org.kie.memorycompiler.JavaCompilerFactory;
import org.kie.memorycompiler.JavaConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;

public abstract class AbstractCodegenIT {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractCodegenIT.class);

    /**
     * Order matters here because inside {@link AbstractCodegenIT#generateCode(Map)} it is the order used to invoke
     * <p>
     * {@link ApplicationGenerator#registerGeneratorIfEnabled(Generator) }
     */
    protected enum TYPE {
        USER_TASK,
        PROCESS,
        RULES,
        DECISION,
        JAVA,
        OPENAPI
    }

    private TestClassLoader classloader;
    private AddonsConfig addonsConfig = AddonsConfig.DEFAULT;

    private static final JavaCompiler JAVA_COMPILER = JavaCompilerFactory.loadCompiler(JavaConfiguration.CompilerType.NATIVE, "11");
    public static final String TEST_JAVA = "src/test/java/";
    public static final String TEST_RESOURCES = "src/test/resources";

    public static final Map<TYPE, BiFunction<KogitoBuildContext, List<String>, Generator>> generatorTypeMap = new HashMap<>();

    private static final String DUMMY_PROCESS_RUNTIME =
            "package org.drools.project.model;\n" +
                    "\n" +
                    "import org.kie.api.KieBase;\n" +
                    "import org.kie.api.builder.model.KieBaseModel;\n" +
                    "import org.kie.api.runtime.KieSession;\n" +
                    "import org.kie.api.runtime.StatelessKieSession;\n" +
                    "import org.drools.modelcompiler.KieBaseBuilder;\n" +
                    "\n" +
                    "\n" +
                    "public class ProjectRuntime implements org.kie.api.runtime.KieRuntimeBuilder {\n" +
                    "\n" +
                    "    public static final ProjectRuntime INSTANCE = new ProjectRuntime();\n" +
                    "\n" +
                    "    @Override\n" +
                    "    public KieBase getKieBase() {\n" +
                    "        return null;\n" +
                    "    }\n" +
                    "\n" +
                    "    @Override\n" +
                    "    public KieBase getKieBase(String name) {\n" +
                    "        return null;\n" +
                    "    }\n" +
                    "\n" +
                    "    @Override\n" +
                    "    public KieSession newKieSession() {\n" +
                    "        return null;\n" +
                    "    }\n" +
                    "\n" +
                    "    @Override\n" +
                    "    public KieSession newKieSession(String sessionName) {\n" +
                    "        return null;\n" +
                    "    }\n" +
                    "\n" +
                    "    @Override\n" +
                    "    public StatelessKieSession newStatelessKieSession() {\n" +
                    "        return null;\n" +
                    "    }\n" +
                    "\n" +
                    "    @Override\n" +
                    "    public StatelessKieSession newStatelessKieSession(String sessionName) {\n" +
                    "        return null;\n" +
                    "    }\n" +
                    "}";

    static {
        generatorTypeMap.put(TYPE.PROCESS, (context, strings) -> ProcessCodegen.ofCollectedResources(context, toCollectedResources(TEST_RESOURCES, strings)));
        generatorTypeMap.put(TYPE.USER_TASK, (context, strings) -> UserTaskCodegen.ofCollectedResources(context, toCollectedResources(TEST_RESOURCES, strings)));
    }

    public static Collection<CollectedResource> toCollectedResources(String basePath, List<String> strings) {
        File[] files = strings
                .stream()
                .map(resource -> new File(basePath, resource))
                .toArray(File[]::new);
        return CollectedResourceProducer.fromFiles(Paths.get(basePath), files);
    }

    public static Collection<CollectedResource> toCollectedResources(List<String> strings) {
        return toCollectedResources(TEST_RESOURCES, strings);
    }

    protected Application generateCodeProcessesOnly(String... processes) throws Exception {
        Map<TYPE, List<String>> resourcesTypeMap = new HashMap<>();
        resourcesTypeMap.put(TYPE.PROCESS, Arrays.asList(processes));
        resourcesTypeMap.put(TYPE.USER_TASK, Arrays.asList(processes));
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

        if (!resourcesTypeMap.isEmpty() && resourcesTypeMap.containsKey(TYPE.PROCESS) && !resourcesTypeMap.containsKey(TYPE.RULES)) {
            sources.add("org/drools/project/model/ProjectRuntime.java");
            srcMfs.write("org/drools/project/model/ProjectRuntime.java", DUMMY_PROCESS_RUNTIME.getBytes());
        }

        if (LOGGER.isInfoEnabled()) {
            Path temp = Files.createTempDirectory("KOGITO_TESTS");
            LOGGER.info("Dumping generated files in " + temp);
            for (GeneratedFile entry : generatedFiles) {
                Path fpath = temp.resolve(entry.relativePath());
                fpath.getParent().toFile().mkdirs();
                Files.write(fpath, entry.contents());
            }
        }

        CompilationResult result = JAVA_COMPILER.compile(sources.toArray(new String[sources.size()]), srcMfs, trgMfs, ClassLoaderUtils.getDefaultClassLoader());
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

    protected ClassLoader testClassLoader() {
        return classloader;
    }

    protected void log(String content) {
        LOGGER.debug(content);
    }

    /**
     * Use this setter to override AddonsConfig used during the generation
     * NOTE: this setter has only effect if invoked before any of the generate*() methods
     * 
     * @param addonsConfig
     */
    protected void setAddonsConfig(AddonsConfig addonsConfig) {
        this.addonsConfig = addonsConfig;
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
