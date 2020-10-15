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

package org.kie.kogito.codegen.process;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import org.drools.core.io.impl.FileSystemResource;
import org.drools.core.util.StringUtils;
import org.drools.core.xml.SemanticModules;
import org.jbpm.bpmn2.xml.BPMNDISemanticModule;
import org.jbpm.bpmn2.xml.BPMNExtensionsSemanticModule;
import org.jbpm.bpmn2.xml.BPMNSemanticModule;
import org.jbpm.compiler.canonical.ModelMetaData;
import org.jbpm.compiler.canonical.ProcessMetaData;
import org.jbpm.compiler.canonical.ProcessToExecModelGenerator;
import org.jbpm.compiler.canonical.TriggerMetaData;
import org.jbpm.compiler.canonical.UserTaskModelMetaData;
import org.jbpm.compiler.xml.XmlProcessReader;
import org.jbpm.serverless.workflow.parser.ServerlessWorkflowParser;
import org.kie.api.definition.process.Process;
import org.kie.api.definition.process.WorkflowProcess;
import org.kie.api.io.Resource;
import org.kie.kogito.codegen.AbstractGenerator;
import org.kie.kogito.codegen.AddonsConfig;
import org.kie.kogito.codegen.ApplicationGenerator;
import org.kie.kogito.codegen.ApplicationSection;
import org.kie.kogito.codegen.ConfigGenerator;
import org.kie.kogito.codegen.DefaultResourceGeneratorFactory;
import org.kie.kogito.codegen.GeneratedFile;
import org.kie.kogito.codegen.GeneratedFile.Type;
import org.kie.kogito.codegen.ResourceGeneratorFactory;
import org.kie.kogito.codegen.di.DependencyInjectionAnnotator;
import org.kie.kogito.codegen.io.CollectedResource;
import org.kie.kogito.codegen.process.config.ProcessConfigGenerator;
import org.kie.kogito.codegen.process.events.CloudEventsMessageProducerGenerator;
import org.kie.kogito.codegen.process.events.CloudEventsResourceGenerator;
import org.kie.kogito.codegen.process.events.TopicsInformationResourceGenerator;
import org.kie.kogito.rules.units.UndefinedGeneratedRuleUnitVariable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import static java.util.stream.Collectors.toList;
import static org.kie.kogito.codegen.ApplicationGenerator.log;

/**
 * Entry point to process code generation
 */
public class ProcessCodegen extends AbstractGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProcessCodegen.class);

    private static final SemanticModules BPMN_SEMANTIC_MODULES = new SemanticModules();
    public static final Set<String> SUPPORTED_BPMN_EXTENSIONS = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(".bpmn", ".bpmn2")));
    private static final String YAML_PARSER = "yml";
    private static final String JSON_PARSER = "json";
    public static final Map<String, String> SUPPORTED_SW_EXTENSIONS;

    static {
        BPMN_SEMANTIC_MODULES.addSemanticModule(new BPMNSemanticModule());
        BPMN_SEMANTIC_MODULES.addSemanticModule(new BPMNExtensionsSemanticModule());
        BPMN_SEMANTIC_MODULES.addSemanticModule(new BPMNDISemanticModule());

        Map<String, String> extMap = new HashMap<>();
        extMap.put(".sw.yml", YAML_PARSER);
        extMap.put(".sw.yaml", YAML_PARSER);
        extMap.put(".sw.json", JSON_PARSER);
        SUPPORTED_SW_EXTENSIONS = Collections.unmodifiableMap(extMap);
    }

    private ClassLoader contextClassLoader;
    private ResourceGeneratorFactory resourceGeneratorFactory;
    private String packageName;

    public static ProcessCodegen ofCollectedResources(Collection<CollectedResource> resources) {
        List<Process> processes = resources.stream()
                .map(CollectedResource::resource)
                .flatMap(resource -> {
                    if (SUPPORTED_BPMN_EXTENSIONS.stream().anyMatch(resource.getSourcePath()::endsWith)) {
                        return parseProcessFile(resource).stream();
                    } else {
                        return SUPPORTED_SW_EXTENSIONS.entrySet()
                                .stream()
                                .filter(e -> resource.getSourcePath().endsWith(e.getKey()))
                                .map(e -> parseWorkflowFile(resource, e.getValue()));
                    }
                })
                .collect(toList());

        return ofProcesses(processes);
    }

    private static ProcessCodegen ofProcesses(List<Process> processes) {
        return new ProcessCodegen(processes);
    }

    static List<Process> parseProcesses(Collection<File> processFiles) {
        List<Process> processes = new ArrayList<>();
        for (File processSourceFile : processFiles) {
            try {
                FileSystemResource r = new FileSystemResource(processSourceFile);
                if (SUPPORTED_BPMN_EXTENSIONS.stream().anyMatch(processSourceFile.getPath()::endsWith)) {
                    processes.addAll(parseProcessFile(r));
                } else {
                    SUPPORTED_SW_EXTENSIONS.entrySet()
                            .stream()
                            .filter(e -> processSourceFile.getPath().endsWith(e.getKey()))
                            .forEach(e -> processes.add(parseWorkflowFile(r, e.getValue())));
                }
                if (processes.isEmpty()) {
                    throw new IllegalArgumentException("Unable to process file with unsupported extension: " + processSourceFile);
                }
            } catch (RuntimeException e) {
                throw new ProcessCodegenException(processSourceFile.getAbsolutePath(), e);
            }
        }
        return processes;
    }

    private static Process parseWorkflowFile(Resource r, String parser) {
        try {
            ServerlessWorkflowParser workflowParser = new ServerlessWorkflowParser(parser);
            return workflowParser.parseWorkFlow(r.getReader());
        } catch (IOException e) {
            throw new ProcessParsingException("Could not parse file " + r.getSourcePath(), e);
        } catch (RuntimeException e) {
            throw new ProcessCodegenException(r.getSourcePath(), e);
        }
    }

    private static Collection<? extends Process> parseProcessFile(Resource r) {
        try {
            XmlProcessReader xmlReader = new XmlProcessReader(
                    BPMN_SEMANTIC_MODULES,
                    Thread.currentThread().getContextClassLoader());
            return xmlReader.read(r.getReader());
        } catch (SAXException | IOException e) {
            throw new ProcessParsingException("Could not parse file " + r.getSourcePath(), e);
        } catch (RuntimeException e) {
            throw new ProcessCodegenException(r.getSourcePath(), e);
        }
    }

    private String applicationCanonicalName;
    private DependencyInjectionAnnotator annotator;

    private ProcessContainerGenerator moduleGenerator;

    private final Map<String, WorkflowProcess> processes;
    private final List<GeneratedFile> generatedFiles = new ArrayList<>();

    private AddonsConfig addonsConfig = AddonsConfig.DEFAULT;

    public ProcessCodegen(Collection<? extends Process> processes) {
        this.processes = new HashMap<>();
        for (Process process : processes) {
            this.processes.put(process.getId(), (WorkflowProcess) process);
        }

        // set default package name
        setPackageName(ApplicationGenerator.DEFAULT_PACKAGE_NAME);
        contextClassLoader = Thread.currentThread().getContextClassLoader();

        //FIXME: once all endpoint generators are implemented it should be changed to ResourceGeneratorFactory, to
        // consider Spring generators.
        resourceGeneratorFactory = new DefaultResourceGeneratorFactory();
    }

    public static String defaultWorkItemHandlerConfigClass(String packageName) {
        return packageName + ".WorkItemHandlerConfig";
    }

    public static String defaultProcessListenerConfigClass(String packageName) {
        return packageName + ".ProcessEventListenerConfig";
    }

    public void setPackageName(String packageName) {
        this.moduleGenerator = new ProcessContainerGenerator(packageName);
        this.applicationCanonicalName = packageName + ".Application";
        this.packageName = packageName;
    }

    public void setDependencyInjection(DependencyInjectionAnnotator annotator) {
        this.annotator = annotator;
        this.moduleGenerator.withDependencyInjection(annotator);
    }

    public ProcessContainerGenerator moduleGenerator() {
        return moduleGenerator;
    }

    public ProcessCodegen withAddons(AddonsConfig addonsConfig) {
        this.addonsConfig = addonsConfig;
        return this;
    }

    public ProcessCodegen withClassLoader(ClassLoader projectClassLoader) {
        this.contextClassLoader = projectClassLoader;
        return this;
    }

    public List<GeneratedFile> generate() {
        if (processes.isEmpty()) {
            return Collections.emptyList();
        }

        List<ProcessGenerator> ps = new ArrayList<>();
        List<ProcessInstanceGenerator> pis = new ArrayList<>();
        List<ProcessExecutableModelGenerator> processExecutableModelGenerators = new ArrayList<>();
        List<AbstractResourceGenerator> rgs = new ArrayList<>(); // REST resources
        List<MessageDataEventGenerator> mdegs = new ArrayList<>(); // message data events
        List<MessageConsumerGenerator> megs = new ArrayList<>(); // message endpoints/consumers
        List<MessageProducerGenerator> mpgs = new ArrayList<>(); // message producers

        List<String> publicProcesses = new ArrayList<>();

        Map<String, ModelMetaData> processIdToModel = new HashMap<>();

        Map<String, ModelClassGenerator> processIdToModelGenerator = new HashMap<>();
        Map<String, InputModelClassGenerator> processIdToInputModelGenerator = new HashMap<>();
        Map<String, OutputModelClassGenerator> processIdToOutputModelGenerator = new HashMap<>();

        Map<String, List<UserTaskModelMetaData>> processIdToUserTaskModel = new HashMap<>();
        Map<String, ProcessMetaData> processIdToMetadata = new HashMap<>();

        // first we generate all the data classes from variable declarations
        for (WorkflowProcess workFlowProcess : processes.values()) {
            ModelClassGenerator mcg = new ModelClassGenerator(context(), workFlowProcess);
            processIdToModelGenerator.put(workFlowProcess.getId(), mcg);
            processIdToModel.put(workFlowProcess.getId(), mcg.generate());

            InputModelClassGenerator imcg = new InputModelClassGenerator(context(), workFlowProcess);
            processIdToInputModelGenerator.put(workFlowProcess.getId(), imcg);

            OutputModelClassGenerator omcg = new OutputModelClassGenerator(context(), workFlowProcess);
            processIdToOutputModelGenerator.put(workFlowProcess.getId(), omcg);
        }

        // then we generate user task inputs and outputs if any
        for (WorkflowProcess workFlowProcess : processes.values()) {
            UserTasksModelClassGenerator utcg = new UserTasksModelClassGenerator(workFlowProcess);
            processIdToUserTaskModel.put(workFlowProcess.getId(), utcg.generate());
        }

        // then we can instantiate the exec model generator
        // with the data classes that we have already resolved
        ProcessToExecModelGenerator execModelGenerator =
                new ProcessToExecModelGenerator(contextClassLoader);

        // collect all process descriptors (exec model)
        for (WorkflowProcess workFlowProcess : processes.values()) {
            ProcessExecutableModelGenerator execModelGen =
                    new ProcessExecutableModelGenerator(workFlowProcess, execModelGenerator);
            String packageName = workFlowProcess.getPackageName();
            String id = workFlowProcess.getId();
            try {
                ProcessMetaData generate = execModelGen.generate();
                processIdToMetadata.put(id, generate);
                processExecutableModelGenerators.add(execModelGen);
            } catch (UndefinedGeneratedRuleUnitVariable e) {
                LOGGER.error(e.getMessage() + "\nRemember: in this case rule unit variables are usually named after process variables.");
                throw new ProcessCodegenException(id, packageName, e);
            } catch (RuntimeException e) {
                LOGGER.error(e.getMessage());
                throw new ProcessCodegenException(id, packageName, e);
            }
        }

        // generate Process, ProcessInstance classes and the REST resource
        for (ProcessExecutableModelGenerator execModelGen : processExecutableModelGenerators) {
            String classPrefix = StringUtils.ucFirst(execModelGen.extractedProcessId());
            WorkflowProcess workFlowProcess = execModelGen.process();
            ModelClassGenerator modelClassGenerator =
                    processIdToModelGenerator.get(execModelGen.getProcessId());

            ProcessGenerator p = new ProcessGenerator(
                    workFlowProcess,
                    execModelGen,
                    classPrefix,
                    modelClassGenerator.className(),
                    applicationCanonicalName
            )
                    .withDependencyInjection(annotator)
                    .withAddons(addonsConfig);

            ProcessInstanceGenerator pi = new ProcessInstanceGenerator(
                    workFlowProcess.getPackageName(),
                    classPrefix,
                    modelClassGenerator.generate());

            ProcessMetaData metaData = processIdToMetadata.get(workFlowProcess.getId());

            //Creating and adding the ResourceGenerator
            resourceGeneratorFactory.create(context(),
                                            workFlowProcess,
                                            modelClassGenerator.className(),
                                            execModelGen.className(),
                                            applicationCanonicalName)
                    .map(r -> r
                            .withDependencyInjection(annotator)
                            .withUserTasks(processIdToUserTaskModel.get(workFlowProcess.getId()))
                            .withSignals(metaData.getSignals())
                            .withTriggers(metaData.isStartable(), metaData.isDynamic()))
                    .ifPresent(rgs::add);

            if (metaData.getTriggers() != null) {

                for (TriggerMetaData trigger : metaData.getTriggers()) {

                    // generate message consumers for processes with message start events
                    if (trigger.getType().equals(TriggerMetaData.TriggerType.ConsumeMessage)) {

                        MessageDataEventGenerator msgDataEventGenerator = new MessageDataEventGenerator(workFlowProcess,
                                                                                                        trigger)
                                .withDependencyInjection(annotator);
                        mdegs.add(msgDataEventGenerator);

                        megs.add(new MessageConsumerGenerator(
                                workFlowProcess,
                                modelClassGenerator.className(),
                                execModelGen.className(),
                                applicationCanonicalName,
                                msgDataEventGenerator.className(),
                                trigger)
                                         .withDependencyInjection(annotator));
                    } else if (trigger.getType().equals(TriggerMetaData.TriggerType.ProduceMessage)) {

                        MessageDataEventGenerator msgDataEventGenerator = new MessageDataEventGenerator(workFlowProcess,
                                                                                                        trigger)
                                .withDependencyInjection(annotator);
                        mdegs.add(msgDataEventGenerator);

                        // this is not cool, we should have a way to process addons
                        // generators without adding conditions to the main generators
                        // see: https://issues.redhat.com/browse/KOGITO-1767
                        if (addonsConfig.useKnativeEventing()) {
                            mpgs.add(new CloudEventsMessageProducerGenerator(
                                    workFlowProcess,
                                    modelClassGenerator.className(),
                                    execModelGen.className(),
                                    msgDataEventGenerator.className(),
                                    trigger)
                                             .withDependencyInjection(annotator));
                        } else {
                            mpgs.add(new MessageProducerGenerator(
                                    workFlowProcess,
                                    modelClassGenerator.className(),
                                    execModelGen.className(),
                                    msgDataEventGenerator.className(),
                                    trigger)
                                             .withDependencyInjection(annotator));
                        }
                    }
                }
            }

            moduleGenerator.addProcess(p);

            ps.add(p);
            pis.add(pi);
        }

        for (ModelClassGenerator modelClassGenerator : processIdToModelGenerator.values()) {
            ModelMetaData mmd = modelClassGenerator.generate();
            storeFile(Type.MODEL, modelClassGenerator.generatedFilePath(),
                      mmd.generate());
        }

        for (InputModelClassGenerator modelClassGenerator : processIdToInputModelGenerator.values()) {
            ModelMetaData mmd = modelClassGenerator.generate();
            storeFile(Type.MODEL, modelClassGenerator.generatedFilePath(),
                      mmd.generate());
        }

        for (OutputModelClassGenerator modelClassGenerator : processIdToOutputModelGenerator.values()) {
            ModelMetaData mmd = modelClassGenerator.generate();
            storeFile(Type.MODEL, modelClassGenerator.generatedFilePath(),
                      mmd.generate());
        }

        for (List<UserTaskModelMetaData> utmd : processIdToUserTaskModel.values()) {

            for (UserTaskModelMetaData ut : utmd) {
                storeFile(Type.MODEL, UserTasksModelClassGenerator.generatedFilePath(ut.getInputModelClassName()), ut.generateInput());

                storeFile(Type.MODEL, UserTasksModelClassGenerator.generatedFilePath(ut.getOutputModelClassName()), ut.generateOutput());
            }
        }

        for (AbstractResourceGenerator resourceGenerator : rgs) {
            storeFile(Type.REST, resourceGenerator.generatedFilePath(),
                      resourceGenerator.generate());
        }

        for (MessageDataEventGenerator messageDataEventGenerator : mdegs) {
            storeFile(Type.CLASS, messageDataEventGenerator.generatedFilePath(),
                      messageDataEventGenerator.generate());
        }

        for (MessageConsumerGenerator messageConsumerGenerator : megs) {
            storeFile(Type.MESSAGE_CONSUMER, messageConsumerGenerator.generatedFilePath(),
                      messageConsumerGenerator.generate());
        }

        for (MessageProducerGenerator messageProducerGenerator : mpgs) {
            storeFile(Type.MESSAGE_PRODUCER, messageProducerGenerator.generatedFilePath(),
                      messageProducerGenerator.generate());
        }

        for (ProcessGenerator p : ps) {
            storeFile(Type.PROCESS, p.generatedFilePath(), p.generate());

            p.getAdditionalClasses().forEach(cp -> {
                String packageName = cp.getPackageDeclaration().map(pd -> pd.getName().toString()).orElse("");
                String clazzName = cp.findFirst(ClassOrInterfaceDeclaration.class).map(cls -> cls.getName().toString()).get();
                String path = (packageName + "." + clazzName).replace('.', '/') + ".java";
                storeFile(Type.CLASS, path, cp.toString());
            });
        }

        if (this.addonsConfig.useKnativeEventing()) {
            LOGGER.info("Knative Eventing addon enabled, generating CloudEvent HTTP listener");
            final CloudEventsResourceGenerator ceGenerator =
                    new CloudEventsResourceGenerator(processExecutableModelGenerators, annotator);
            storeFile(Type.REST, ceGenerator.generatedFilePath(), ceGenerator.generate());
        }

        final TopicsInformationResourceGenerator topicsGenerator = new TopicsInformationResourceGenerator(processExecutableModelGenerators);
        storeFile(Type.REST, topicsGenerator.generatedFilePath(), topicsGenerator.generate());

        for (ProcessInstanceGenerator pi : pis) {
            storeFile(Type.PROCESS_INSTANCE, pi.generatedFilePath(), pi.generate());
        }

        for (ProcessExecutableModelGenerator processGenerator : processExecutableModelGenerators) {
            if (processGenerator.isPublic()) {
                publicProcesses.add(processGenerator.extractedProcessId());
                this.addLabel(processGenerator.label(), "process"); // add the label id of the process with value set to process as resource type
            }
        }

        return generatedFiles;
    }

    @Override
    public void updateConfig(ConfigGenerator cfg) {
        if (!processes.isEmpty()) {
            cfg.withProcessConfig(
                    new ProcessConfigGenerator(packageName));
        }
    }

    private void storeFile(Type type, String path, String source) {
        generatedFiles.add(new GeneratedFile(type, path, log(source).getBytes(StandardCharsets.UTF_8)));
    }

    public List<GeneratedFile> getGeneratedFiles() {
        return generatedFiles;
    }

    @Override
    public ApplicationSection section() {
        return moduleGenerator;
    }
}
