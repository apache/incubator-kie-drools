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
import java.util.stream.Collectors;

import org.kie.kogito.codegen.context.JavaKogitoBuildContext;
import org.kie.memorycompiler.CompilationResult;
import org.kie.memorycompiler.JavaCompiler;
import org.kie.memorycompiler.JavaCompilerFactory;
import org.kie.memorycompiler.JavaConfiguration;
import org.drools.compiler.compiler.io.memory.MemoryFileSystem;
import org.kie.kogito.Application;
import org.kie.kogito.codegen.context.KogitoBuildContext;
import org.kie.kogito.codegen.decision.DecisionCodegen;
import org.kie.kogito.codegen.io.CollectedResource;
import org.kie.kogito.codegen.prediction.PredictionCodegen;
import org.kie.kogito.codegen.process.ProcessCodegen;
import org.kie.kogito.codegen.rules.IncrementalRuleCodegen;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;

public class AbstractCodegenTest {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractCodegenTest.class);

    /**
     * Order matters here because inside {@link AbstractCodegenTest#generateCode(Map)} it is the order used to invoke
     *
     * {@link ApplicationGenerator#registerGeneratorIfEnabled(Generator) }
     */
    protected enum TYPE {
        PROCESS,
        RULES,
        DECISION,
        JAVA,
        PREDICTION
    }
    private TestClassLoader classloader;
    private AddonsConfig addonsConfig = AddonsConfig.DEFAULT;

    private static final JavaCompiler JAVA_COMPILER = JavaCompilerFactory.loadCompiler( JavaConfiguration.CompilerType.NATIVE, "11");
    private static final String TEST_JAVA = "src/test/java/";
    private static final String TEST_RESOURCES = "src/test/resources";

    private static final Map<TYPE, BiFunction<KogitoBuildContext, List<String>, Generator>> generatorTypeMap = new HashMap<>();

    private static final String DUMMY_PROCESS_RUNTIME =
            "package org.drools.project.model;\n" +
            "\n" +
            "import org.kie.api.KieBase;\n" +
            "import org.kie.api.builder.model.KieBaseModel;\n" +
            "import org.kie.api.runtime.KieSession;\n" +
            "import org.drools.modelcompiler.builder.KieBaseBuilder;\n" +
            "\n" +
            "\n" +
            "public class ProjectRuntime implements org.kie.kogito.rules.KieRuntimeBuilder {\n" +
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
            "    public KieSession newKieSession(String sessionName, org.kie.kogito.rules.RuleConfig ruleConfig) {\n" +
            "        return null;\n" +
            "    }\n" +
            "\n" +
            "}";

    static {
        generatorTypeMap.put(TYPE.PROCESS, (context, strings) -> ProcessCodegen.ofCollectedResources(context, toCollectedResources(TEST_RESOURCES, strings)));
        generatorTypeMap.put(TYPE.RULES, (context, strings) -> IncrementalRuleCodegen.ofCollectedResources(context, toCollectedResources(TEST_RESOURCES, strings)));
        generatorTypeMap.put(TYPE.DECISION, (context, strings) -> DecisionCodegen.ofCollectedResources(context, toCollectedResources(TEST_RESOURCES, strings)));

        generatorTypeMap.put(TYPE.JAVA, (context, strings) -> IncrementalRuleCodegen.ofJavaResources(context, toCollectedResources(TEST_JAVA, strings)));
        generatorTypeMap.put(TYPE.PREDICTION, (context, strings) -> PredictionCodegen.ofCollectedResources(context, toCollectedResources(TEST_RESOURCES, strings)));
    }

    private static Collection<CollectedResource> toCollectedResources(String basePath, List<String> strings) {
        File[] files = strings
                .stream()
                .map(resource -> new File(basePath, resource))
                .toArray(File[]::new);
        return CollectedResource.fromFiles(Paths.get(basePath), files);
    }


    private static List<File> toFiles(List<String> strings, String path) {
        return strings
                .stream()
                .map(resource -> new File(path, resource))
                .collect(Collectors.toList());
    }


    public void withSpringContext() {
        throw new UnsupportedOperationException("To be fixed KOGITO-4000");
    }

    public void withQuarkusContext() {
        throw new UnsupportedOperationException("To be fixed KOGITO-4000");
    }

    public void withJavaContext() {
        throw new UnsupportedOperationException("To be fixed KOGITO-4000");
    }

    protected Application generateCodeProcessesOnly(String... processes) throws Exception {
        Map<TYPE, List<String>> resourcesTypeMap = new HashMap<>();
        resourcesTypeMap.put(TYPE.PROCESS, Arrays.asList(processes));
        return generateCode(resourcesTypeMap);
    }

    protected Application generateCodeRulesOnly(String... rules) throws Exception {
        Map<TYPE, List<String>> resourcesTypeMap = new HashMap<>();
        resourcesTypeMap.put(TYPE.RULES, Arrays.asList(rules));
        return generateCode(resourcesTypeMap);
    }

    protected Application generateRulesFromJava(String... javaSourceCode) throws Exception {
        Map<TYPE, List<String>> resourcesTypeMap = new HashMap<>();
        resourcesTypeMap.put(TYPE.JAVA, Arrays.asList(javaSourceCode));
        return generateCode(resourcesTypeMap);
    }

    protected Application generateCode(Map<TYPE, List<String>> resourcesTypeMap) throws Exception {
        return generateCode(resourcesTypeMap, this.getClass().getPackage().getName());
    }

    protected Application generateCode(Map<TYPE, List<String>> resourcesTypeMap, String packageName) throws Exception {
        KogitoBuildContext context = JavaKogitoBuildContext.builder()
                .withApplicationProperties(new File(TEST_RESOURCES))
                .withPackageName(packageName)
                .withAddonsConfig(addonsConfig)
                .build();

        ApplicationGenerator appGen =
                new ApplicationGenerator(context);

        for (TYPE type :  TYPE.values()) {
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
            if (!fileName.endsWith( ".java" )) {
                continue;
            }
            sources.add( fileName );
            srcMfs.write(fileName, entry.contents());
            log(new String(entry.contents()));
        }

        if (resourcesTypeMap.size() == 1 && resourcesTypeMap.containsKey(TYPE.PROCESS)) {
            sources.add( "org/drools/project/model/ProjectRuntime.java" );
            srcMfs.write( "org/drools/project/model/ProjectRuntime.java", DUMMY_PROCESS_RUNTIME.getBytes() );
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

        CompilationResult result = JAVA_COMPILER.compile(sources.toArray( new String[sources.size()] ), srcMfs, trgMfs, this.getClass().getClassLoader());
        assertThat(result).isNotNull();
        assertThat(result.getErrors()).describedAs(String.join("\n\n", Arrays.toString(result.getErrors()))).hasSize(0);

        classloader = new TestClassLoader(this.getClass().getClassLoader(), trgMfs.getMap());

        @SuppressWarnings("unchecked")
        Class<Application> app = (Class<Application>) Class.forName(packageName + ".Application", true, classloader);

        Application application = app.getDeclaredConstructor().newInstance();
        return application;
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
     * @param addonsConfig
     */
    protected void setAddonsConfig(AddonsConfig addonsConfig) {
        this.addonsConfig = addonsConfig;
    }

    private static class TestClassLoader extends URLClassLoader {

        private final Map<String, byte[]> extraClassDefs;

        public TestClassLoader(ClassLoader parent, Map<String, byte[]> extraClassDefs) {
            super(new URL[0], parent);
            this.extraClassDefs = new HashMap<>();

            for (Entry<String, byte[]> entry : extraClassDefs.entrySet()) {
                this.extraClassDefs.put(entry.getKey().replaceAll("/", ".").replaceFirst("\\.class", ""), entry.getValue());
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
