/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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
package org.kie.pmml.assembler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dmg.pmml.pmml_4_2.descr.PMML;
import org.drools.compiler.builder.impl.KnowledgeBuilderConfigurationImpl;
import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.drools.compiler.builder.impl.errors.SrcError;
import org.drools.compiler.compiler.DescrBuildWarning;
import org.drools.compiler.compiler.DrlParser;
import org.drools.compiler.compiler.DroolsParserException;
import org.drools.compiler.compiler.JavaDialectConfiguration;
import org.drools.compiler.compiler.ParserError;
import org.drools.compiler.compiler.io.memory.MemoryFileSystem;
import org.drools.compiler.kie.builder.impl.KieFileSystemImpl;
import org.drools.compiler.lang.descr.PackageDescr;
import org.drools.core.util.ClassUtils;
import org.drools.reflective.classloader.ProjectClassLoader;
import org.kie.api.KieServices;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.internal.assembler.KieAssemblerService;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceConfiguration;
import org.kie.api.io.ResourceType;
import org.kie.api.io.ResourceWithConfiguration;
import org.kie.internal.io.ResourceFactory;
import org.kie.memorycompiler.CompilationProblem;
import org.kie.memorycompiler.CompilationResult;
import org.kie.memorycompiler.JavaCompiler;
import org.kie.memorycompiler.JavaCompilerFactory;
import org.kie.memorycompiler.JavaConfiguration;
import org.kie.memorycompiler.resources.ResourceReader;
import org.kie.pmml.pmml_4_2.PMML4Compiler;
import org.kie.pmml.pmml_4_2.PMML4Exception;
import org.kie.pmml.pmml_4_2.PMMLResource;

import static org.kie.api.pmml.PMMLConstants.LEGACY;
import static org.kie.internal.pmml.PMMLImplementationsUtil.isjPMMLAvailableToClassLoader;
import static org.kie.internal.pmml.PMMLImplementationsUtil.toEnable;

/**
 * This implementation is deprecated and will be removed in future releases.
 *
 * @see  <a href="https://github.com/kiegroup/drools/tree/master/kie-pmml-trusty">PMML Trusty</a>
 */
@Deprecated
public class PMMLAssemblerService implements KieAssemblerService {

    private ClassLoader rootClassLoader;
    private KnowledgeBuilderConfigurationImpl configuration;
    private KnowledgeBuilderImpl kbuilder;
    private static final String JAVA_ROOT = "src/main/java/";
    private static final PMML4Compiler pmmlCompiler = new PMML4Compiler();

    @Override
    public ResourceType getResourceType() {
        return LEGACY.equals(toEnable(Thread.currentThread().getContextClassLoader())) ? ResourceType.PMML : ResourceType.NOOP;
    }

    @Override
    public synchronized void addResourceAfterRules(Object kbuilder, Resource resource, ResourceType type,
                                         ResourceConfiguration configuration) throws Exception {
        this.kbuilder = (KnowledgeBuilderImpl) kbuilder;
        this.configuration = this.kbuilder.getBuilderConfiguration();
        this.rootClassLoader = this.kbuilder.getRootClassLoader();
        if (!isjPMMLAvailableToClassLoader(rootClassLoader)) {
            addPackage(resource);
        }
    }

    @Override
    public synchronized void addResourcesAfterRules(Object kbuilder, Collection<ResourceWithConfiguration> resources,
            ResourceType type) throws Exception {
        for (ResourceWithConfiguration rd : resources) {
            if (rd.getBeforeAdd() != null) {
                rd.getBeforeAdd().accept(kbuilder);
            }
            addResourceAfterRules(kbuilder, rd.getResource(), type, rd.getResourceConfiguration());
            if (rd.getAfterAdd() != null) {
                rd.getAfterAdd().accept(kbuilder);
            }
        }
    }

    /**
     * This method does the work of calling the PMML compiler and then assembling the results
     * into packages that are added to the KnowledgeBuilder
     * @param resource
     * @throws DroolsParserException
     * @throws IOException
     */
    private void addPackage(Resource resource) throws DroolsParserException, IOException {
        if (pmmlCompiler != null) {
            if (pmmlCompiler.getResults().isEmpty()) {
                PMML pmml = pmmlCompiler.loadModel(PMML4Compiler.PMML, resource.getInputStream());
                addPMMLPojos(pmmlCompiler, resource, pmml);
                if (pmmlCompiler.getResults().isEmpty()) {
                    List<PackageDescr> packages = getPackageDescrs(resource, pmml);
                    if (packages != null && !packages.isEmpty()) {
                        for (PackageDescr descr : packages) {
                            this.kbuilder.addPackage(descr);
                        }
                    }
                }
            }
        }
        pmmlCompiler.getResults().forEach(result -> {
            kbuilder.addBuilderResult(result);
        });
    }

    /**
     * This method calls the PMML compiler to get PMMLResource objects which are used to
     * create one or more PackageDescr objects
     * @param resource
     * @return
     * @throws DroolsParserException
     * @throws IOException
     */
    private List<PackageDescr> getPackageDescrs(Resource resource, PMML pmml) throws DroolsParserException, IOException {
        List<PMMLResource> resources = pmmlCompiler.precompile(pmml, null);
        if (resources != null && !resources.isEmpty()) {
            return generatedResourcesToPackageDescr(resource, resources);
        }
        return null;
    }

    /**
     * Creates a list of PackageDescr objects from the PMMLResources (which includes the generated DRL)
     * @param resource
     * @param resources
     * @return
     * @throws DroolsParserException
     */
    private List<PackageDescr> generatedResourcesToPackageDescr(Resource resource, List<PMMLResource> resources)
            throws DroolsParserException {
        List<PackageDescr> pkgDescrs = new ArrayList<>();
        DrlParser parser = new DrlParser(configuration.getLanguageLevel());
        for (PMMLResource res : resources) {
            for (Map.Entry<String, String> entry : res.getRules().entrySet()) {
                String key = entry.getKey();
                String src = entry.getValue();
                PackageDescr descr = null;
                descr = parser.parse(false, src);
                if (descr != null) {
                    descr.setResource(resource);
                    pkgDescrs.add(descr);
                    dumpGeneratedRule(descr, key, src);
                } else {
                    kbuilder.addBuilderResult(new ParserError(resource, "Parser returned a null Package", 0, 0));
                }
            }
        }
        return pkgDescrs;
    }

    private void dumpGeneratedRule(PackageDescr descr, String resName, String src) {
        File dumpDir = this.configuration.getDumpDir();
        if (dumpDir != null) {
            try {
                String dirName = dumpDir.getCanonicalPath().endsWith("/") ? dumpDir.getCanonicalPath()
                        : dumpDir.getCanonicalPath() + "/";
                String outputPath = dirName + resName + ".drl";
                try (FileOutputStream fos = new FileOutputStream(outputPath)) {
                    fos.write(src.getBytes());
                } catch (IOException iox) {
                    kbuilder.addBuilderResult(new DescrBuildWarning(null, descr, descr.getResource(),
                            "Unable to write generated rules the dump directory: " + outputPath));
                }
            } catch (IOException e) {
                kbuilder.addBuilderResult(
                        new DescrBuildWarning(null, descr, descr.getResource(), "Unable to access the dump directory"));
            }
        }
    }

    private void addPMMLPojos(PMML4Compiler compiler, Resource resource, PMML pmml) {
        KieFileSystem javaSource = KieServices.Factory.get().newKieFileSystem();
        Map<String, String> javaSources = new HashMap<>();
        Map<String, String> modelSources = null;
        try {
            modelSources = compiler.getJavaClasses(pmml);
        } catch (PMML4Exception px) {
            kbuilder.addBuilderResult(new SrcError(resource, px.getMessage()));
        }
        if (compiler.getResults().isEmpty()) {
            if (modelSources != null && !modelSources.isEmpty()) {
                javaSources.putAll(modelSources);
            }

            for (Map.Entry<String, String> entry : javaSources.entrySet()) {
                String key = entry.getKey();
                String javaCode = entry.getValue();
                if (javaCode != null && !javaCode.trim().isEmpty()) {
                    Resource res = ResourceFactory.newByteArrayResource(javaCode.getBytes())
                            .setResourceType(ResourceType.JAVA);
                    String sourcePath = key.replaceAll("\\.", "/") + ".java";
                    res.setSourcePath(sourcePath);
                    javaSource.write(res);
                }
            }

            ResourceReader src = ((KieFileSystemImpl) javaSource).asMemoryFileSystem();
            List<String> javaFileNames = getJavaFileNames(src);
            if (javaFileNames != null && !javaFileNames.isEmpty()) {
                ClassLoader classLoader = rootClassLoader;
                KnowledgeBuilderConfigurationImpl kconf = new KnowledgeBuilderConfigurationImpl(classLoader);
                JavaDialectConfiguration javaConf = (JavaDialectConfiguration) kconf.getDialectConfiguration("java");
                MemoryFileSystem trgMfs = new MemoryFileSystem();
                compileJavaClasses(javaConf, rootClassLoader, javaFileNames, JAVA_ROOT, src, trgMfs);
                Map<String, byte[]> classesMap = new HashMap<>();

                for (String name : trgMfs.getFileNames()) {
                    classesMap.put(name, trgMfs.getBytes(name));
                }
                if (!classesMap.isEmpty()) {
                    ProjectClassLoader projectClassLoader = (ProjectClassLoader) rootClassLoader;
                    if ( ClassUtils.isCaseSenstiveOS() ) {
                        projectClassLoader.reinitTypes();
                    }
                    projectClassLoader.storeClasses(classesMap);
                }
            }
        }
    }

    private List<String> getJavaFileNames(ResourceReader src) {
        List<String> javaFileNames = new ArrayList<>();
        for (String fname : src.getFileNames()) {
            if (fname.endsWith(".java")) {
                javaFileNames.add(fname);
            }
        }
        return javaFileNames;
    }

    private void compileJavaClasses( JavaDialectConfiguration javaConf, ClassLoader classLoader, List<String> javaFiles,
                                     String rootFolder, ResourceReader source, MemoryFileSystem trgMfs) {
        if (!javaFiles.isEmpty()) {
            String[] sourceFiles = javaFiles.toArray(new String[javaFiles.size()]);
            File dumpDir = javaConf.getPackageBuilderConfiguration().getDumpDir();
            if (dumpDir != null) {
                String dumpDirName;
                try {
                    dumpDirName = dumpDir.getCanonicalPath().endsWith("/") ? dumpDir.getCanonicalPath()
                            : dumpDir.getCanonicalPath() + "/";
                    for (String srcFile : sourceFiles) {
                        String baseName = (srcFile.startsWith(JAVA_ROOT) ? srcFile.substring(JAVA_ROOT.length())
                                : srcFile).replaceAll("/", ".");

                        String fname = dumpDirName + baseName;
                        byte[] srcData = source.getBytes(srcFile);
                        try (FileOutputStream fos = new FileOutputStream(fname)) {
                            fos.write(srcData);
                        } catch (IOException iox) {
                            kbuilder.addBuilderResult(new SrcError(fname, iox.getMessage()));
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
            JavaCompiler javaCompiler = createCompiler(javaConf, rootFolder);
            CompilationResult res = javaCompiler.compile(sourceFiles, source, trgMfs, classLoader);

            for (CompilationProblem problem : res.getErrors()) {
                kbuilder.addBuilderResult(new SrcError(problem.getFileName(), problem.getMessage()));
            }
            for (CompilationProblem problem : res.getWarnings()) {
                kbuilder.addBuilderResult(new SrcError(problem.getFileName(), problem.getMessage()));
            }
        }
    }

    private JavaCompiler createCompiler( JavaConfiguration javaConf, String sourceFolder ) {
        JavaCompiler javaCompiler = JavaCompilerFactory.loadCompiler( javaConf );
        javaCompiler.setSourceFolder( sourceFolder );
        return javaCompiler;
    }
}
