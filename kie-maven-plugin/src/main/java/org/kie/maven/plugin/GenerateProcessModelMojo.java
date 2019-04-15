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

package org.kie.maven.plugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.drools.core.io.impl.FileSystemResource;
import org.drools.core.util.StringUtils;
import org.drools.core.xml.SemanticModules;
import org.jbpm.bpmn2.xml.BPMNDISemanticModule;
import org.jbpm.bpmn2.xml.BPMNExtensionsSemanticModule;
import org.jbpm.bpmn2.xml.BPMNSemanticModule;
import org.jbpm.compiler.canonical.ModelMetaData;
import org.jbpm.compiler.canonical.ProcessToExecModelGenerator;
import org.jbpm.compiler.xml.XmlProcessReader;
import org.kie.api.definition.process.Process;
import org.kie.api.definition.process.WorkflowProcess;
import org.kie.api.io.Resource;
import org.kie.maven.plugin.process.ModelClassGenerator;
import org.kie.maven.plugin.process.ModuleGenerator;
import org.kie.maven.plugin.process.ProcessExecutableModelGenerator;
import org.kie.maven.plugin.process.ProcessGenerator;
import org.kie.maven.plugin.process.ProcessInstanceGenerator;
import org.kie.maven.plugin.process.ResourceGenerator;
import org.xml.sax.SAXException;

@Mojo(name = "generateProcessModel",
        requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME,
        requiresProject = true,
        defaultPhase = LifecyclePhase.COMPILE)
public class GenerateProcessModelMojo extends AbstractKieMojo {

    public static final String BOOTSTRAP_PACKAGE = "org.kie.bootstrap.process";
    private static final SemanticModules BPMN_SEMANTIC_MODULES = new SemanticModules();

    static {
        BPMN_SEMANTIC_MODULES.addSemanticModule(new BPMNSemanticModule());
        BPMN_SEMANTIC_MODULES.addSemanticModule(new BPMNExtensionsSemanticModule());
        BPMN_SEMANTIC_MODULES.addSemanticModule(new BPMNDISemanticModule());
    }

    @Parameter(required = true, defaultValue = "${project.basedir}/src")
    private File sourceDir;

    @Parameter(required = true, defaultValue = "${project.build.directory}")
    private File targetDirectory;

    @Parameter(required = true, defaultValue = "${project.basedir}")
    private File projectDir;

    @Parameter
    private Map<String, String> properties;

    @Parameter(required = true, defaultValue = "${project}")
    private MavenProject project;

    @Parameter(property = "generateProcessModel", defaultValue = "yes")
    private String generateProcessModel;

    @Parameter(property = "dependencyInjection", defaultValue = "true")
    private boolean dependencyInjection;

    private final String additionalCompilerPath = "/generated-sources/process/main/java";

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        if (BPMNModelMode.shouldGenerateBPMNModel(generateProcessModel)) {
            generateProcessModel();
        }
    }

    private void generateProcessModel() throws MojoExecutionException {
        project.addCompileSourceRoot(
                Paths.get(
                        targetDirectory.getPath(),
                        additionalCompilerPath).toString());

        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();

        try {
            setSystemProperties(properties);

            List<File> processFiles = getBPMNFiles();
            if (processFiles.isEmpty()) {
                return;
            }
            Map<String, String> labels = new HashMap<>();

            getLog().debug("Process Files to process: " + processFiles);
            Map<String, WorkflowProcess> processes = parseProcesses(processFiles);

            List<ProcessGenerator> ps = new ArrayList<>();
            List<ProcessInstanceGenerator> pis = new ArrayList<>();
            List<ProcessExecutableModelGenerator> processExecutableModelGenerators = new ArrayList<>();
            List<ResourceGenerator> rgs = new ArrayList<>();
            Map<String, ModelMetaData> processIdToModel = new HashMap<>();
            Map<String, ModelClassGenerator> processIdToModelGenerator = new HashMap<>();

            ModuleGenerator moduleSourceClass = new ModuleGenerator(project.getGroupId())
                    .withCdi(dependencyInjection);

            // first we generate all the data classes from variable declarations
            for (WorkflowProcess workFlowProcess : processes.values()) {
                ModelClassGenerator mcg = new ModelClassGenerator(workFlowProcess);
                processIdToModelGenerator.put(workFlowProcess.getId(), mcg);
                processIdToModel.put(workFlowProcess.getId(), mcg.generate());
            }

            // then we can instantiate the exec model generator
            // with the data classes that we have already resolved
            ProcessToExecModelGenerator execModelGenerator =
                    new ProcessToExecModelGenerator(processIdToModel);

            // collect all process descriptors (exec model)
            for (WorkflowProcess workFlowProcess : processes.values()) {
                ProcessExecutableModelGenerator execModelGen =
                        new ProcessExecutableModelGenerator(workFlowProcess, execModelGenerator);
                execModelGen.generate();
                processExecutableModelGenerators.add(execModelGen);
            }

            // generate Process, ProcessInstance classes and the REST resource
            for (ProcessExecutableModelGenerator execModelGen : processExecutableModelGenerators) {
                String classPrefix = StringUtils.capitalize(execModelGen.extractedProcessId());
                WorkflowProcess workFlowProcess = execModelGen.process();
                ModelClassGenerator modelClassGenerator =
                        processIdToModelGenerator.get(execModelGen.getProcessId());

                ProcessGenerator p = new ProcessGenerator(
                        workFlowProcess,
                        execModelGen,
                        processes,
                        classPrefix,
                        modelClassGenerator.className(),
                        moduleSourceClass.targetCanonicalName())
                        .withCdi(dependencyInjection);

                ProcessInstanceGenerator pi = new ProcessInstanceGenerator(
                        workFlowProcess.getPackageName(),
                        classPrefix,
                        modelClassGenerator.generate());

                // do not generate REST endpoint if the process is not "public"
                if (execModelGen.isPublic()) {
                    // create REST resource class for process
                    ResourceGenerator resourceGenerator = new ResourceGenerator(
                            workFlowProcess,
                            modelClassGenerator.className())
                            .withCdi(dependencyInjection);

                    rgs.add(resourceGenerator);
                }

                moduleSourceClass.addProcess(p);

                ps.add(p);
                pis.add(pi);
            }

            List<String> publicProcesses = new ArrayList<>();

            for (ModelClassGenerator modelClassGenerator : processIdToModelGenerator.values()) {
                ModelMetaData mmd = modelClassGenerator.generate();
                Files.write(pathOf(modelClassGenerator.generatedFilePath()),
                            mmd.getGeneratedClassModel().getBytes());
            }

            for (ResourceGenerator resourceGenerator : rgs) {
                Files.write(pathOf(resourceGenerator.generatedFilePath()),
                            resourceGenerator.generate().getBytes());
            }

            for (ProcessGenerator p : ps) {
                Files.write(pathOf(p.generatedFilePath()), p.generate().getBytes());
            }

            for (ProcessInstanceGenerator pi : pis) {
                Files.write(pathOf(pi.generatedFilePath()), pi.generate().getBytes());
            }

            String workItemHandlerConfigClass = project.getGroupId() + ".WorkItemHandlerConfig";
            Path p = Paths.get(sourceDir.getPath(),
                               "main/java",
                               workItemHandlerConfigClass.replace('.', '/') + ".java");
            if (Files.exists(p)) {
                moduleSourceClass.setWorkItemHandlerClass(workItemHandlerConfigClass);
            }

            Files.write(pathOf(moduleSourceClass.generatedFilePath()),
                        moduleSourceClass.generate().getBytes());

            getLog().info("Process Model successfully generated");

            for (ProcessExecutableModelGenerator legacyProcessGenerator : processExecutableModelGenerators) {
                if (legacyProcessGenerator.isPublic()) {
                    publicProcesses.add(legacyProcessGenerator.extractedProcessId());
                    labels.put(legacyProcessGenerator.label(), "process");// add the label id of the process with value set to process as resource type
                }
            }

            writeLabelsImageMetadata(targetDirectory.getPath(), labels);
        } catch (Exception e) {
            throw new MojoExecutionException("An error was caught during process generation", e);
        } finally {
            Thread.currentThread().setContextClassLoader(contextClassLoader);
        }
    }

    private Map<String, WorkflowProcess> parseProcesses(List<File> processFiles) throws IOException, MojoExecutionException {
        Map<String, WorkflowProcess> processes = new HashMap<>();

        for (File bpmnFile : processFiles) {
            getLog().info(bpmnFile.getName());
            FileSystemResource r = new FileSystemResource(bpmnFile);
            Collection<? extends Process> ps = parseProcessFile(r);
            for (Process p : ps) {
                processes.put(p.getId(), (WorkflowProcess) p);
            }
        }
        return processes;
    }

    private Path pathOf(String end) {
        Path path = Paths.get(targetDirectory.getPath(), additionalCompilerPath, end);
        path.getParent().toFile().mkdirs();
        return path;
    }

    private Collection<? extends Process> parseProcessFile(Resource r) throws IOException, MojoExecutionException {
        try {
            XmlProcessReader xmlReader = new XmlProcessReader(
                    BPMN_SEMANTIC_MODULES,
                    Thread.currentThread().getContextClassLoader());
            return xmlReader.read(r.getReader());
        } catch (SAXException e) {
            throw new MojoExecutionException("Could not parse file " + r.getSourcePath(), e);
        }
    }

    private List<File> getBPMNFiles() throws IOException {
        return Files.walk(sourceDir.toPath())
                .filter(p -> p.toString().endsWith(".bpmn") || p.toString().endsWith(".bpmn2"))
                .map(Path::toFile)
                .collect(Collectors.toList());
    }
}

