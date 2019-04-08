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
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

import com.github.javaparser.ast.expr.NormalAnnotationExpr;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.drools.core.io.impl.FileSystemResource;
import org.drools.core.runtime.process.InternalProcessRuntime;
import org.drools.core.util.StringUtils;
import org.drools.core.xml.SemanticModules;
import org.jbpm.bpmn2.xml.BPMNDISemanticModule;
import org.jbpm.bpmn2.xml.BPMNExtensionsSemanticModule;
import org.jbpm.bpmn2.xml.BPMNSemanticModule;
import org.jbpm.compiler.canonical.ModelMetaData;
import org.jbpm.compiler.canonical.ProcessMetaData;
import org.jbpm.compiler.canonical.ProcessToExecModelGenerator;
import org.jbpm.compiler.xml.XmlProcessReader;
import org.jbpm.workflow.instance.impl.WorkflowProcessInstanceImpl;
import org.kie.api.definition.process.Process;
import org.kie.api.definition.process.WorkflowProcess;
import org.kie.api.io.Resource;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.process.WorkItemHandler;
import org.xml.sax.SAXException;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier.Keyword;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.AssignExpr.Operator;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.BooleanLiteralExpr;
import com.github.javaparser.ast.expr.CastExpr;
import com.github.javaparser.ast.expr.EnclosedExpr;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.LambdaExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.Name;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.NullLiteralExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.expr.SingleMemberAnnotationExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.expr.ThisExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.UnknownType;

@Mojo(name = "generateProcessModel",
        requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME,
        requiresProject = true,
        defaultPhase = LifecyclePhase.COMPILE)
public class GenerateProcessModelMojo extends AbstractKieMojo {

    private static final String RESOURCE_CLASS_SUFFIX = "Resource";
    private static final String BOOTSTRAP_PACKAGE = "org.kie.bootstrap.process";
    private static final String BOOTSTRAP_CLASS = BOOTSTRAP_PACKAGE + ".ProcessRuntimeProvider";
    private static final SemanticModules BPMN_SEMANTIC_MODULES = new SemanticModules();
    static {
        BPMN_SEMANTIC_MODULES.addSemanticModule(new BPMNSemanticModule());
        BPMN_SEMANTIC_MODULES.addSemanticModule(new BPMNExtensionsSemanticModule());
        BPMN_SEMANTIC_MODULES.addSemanticModule(new BPMNDISemanticModule());
    }
    
    private static final List<String> DEFAULT_HANDLERS = Arrays.asList("Log", "Human Task");

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

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        if (BPMNModelMode.shouldGenerateBPMNModel(generateProcessModel)) {
            generateProcessModel();
        }
    }

    private void generateProcessModel() throws MojoExecutionException {
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();

        try {
            setSystemProperties(properties);

            final List<String> compiledClassNames = new ArrayList<>();
            final Set<String> workItems = new HashSet<>();
            List<File> processFiles = getBPMNFiles();
            Map<String, String> labels = new HashMap<>();

            getLog().debug("Process Files to process: " + processFiles);
            List<Process> processes = new ArrayList<>();

            for (File bpmnFile : processFiles) {
                getLog().info(bpmnFile.getName());
                FileSystemResource r = new FileSystemResource(bpmnFile);
                processes.addAll(parseProcessFile(r));
            }
            
            final String additionalCompilerPath = "/generated-sources/process/main/java";
            addNewCompileRoot(additionalCompilerPath);
            List<String> publicProcesses = new ArrayList<>();
            
            for (Process process : processes) {
                WorkflowProcess workFlowProcess = (WorkflowProcess) process;

                ProcessMetaData processMetaData = ProcessToExecModelGenerator.INSTANCE.generate(workFlowProcess);
                workItems.addAll(processMetaData.getWorkItems());
                // create class with executable model for the process
                String processClazzName = processMetaData.getProcessClassName();
                final Path processFileNameRelative = transformPathToMavenPath(processClazzName, ".java");

                compiledClassNames.add(getCompiledClassName(processFileNameRelative));

                final Path processFileName = Paths.get(targetDirectory.getPath(), additionalCompilerPath, processFileNameRelative.toString());                
                createSourceFile(processFileName, processMetaData.getGeneratedClassModel());
                
                if (WorkflowProcess.PUBLIC_VISIBILITY.equalsIgnoreCase(workFlowProcess.getVisibility())) {
                
                    // create model class for all variables                    
                    ModelMetaData modelMetaData = ProcessToExecModelGenerator.INSTANCE.generateModel(workFlowProcess);
                    
                    final Path modelFileNameRelative = transformPathToMavenPath(  modelMetaData.getModelClassName(), ".java");
                    final Path modelFileName = Paths.get(targetDirectory.getPath(), additionalCompilerPath, modelFileNameRelative.toString());
                    createSourceFile(modelFileName, modelMetaData.getGeneratedClassModel());
                    
                
                    // create REST resource class for process
                    String resourceClazzName = StringUtils.capitalize(processMetaData.getExtractedProcessId()) + "Resource";
                    String resourceClazz = generateResourceClass(workFlowProcess, processMetaData.getProcessId(), modelMetaData.getModelClassSimpleName(), modelMetaData.getModelClassName(), compiledClassNames);
                    
                    final Path resourceFileNameRelative = transformPathToMavenPath( project.getGroupId() + "." + resourceClazzName, ".java");
                    final Path resourceFileName = Paths.get(targetDirectory.getPath(), additionalCompilerPath, resourceFileNameRelative.toString());
                    createSourceFile(resourceFileName, resourceClazz);
                    
                    labels.put(LABEL_PREFIX + processMetaData.getExtractedProcessId(), Optional.ofNullable(workFlowProcess.getMetaData().get("Description")).map(d -> d.toString()).orElse("Executes " + process.getName()));
                    publicProcesses.add(processMetaData.getExtractedProcessId());
                }
            }
            if (!compiledClassNames.isEmpty()) {
                String boostrapClass = generateProcessRuntimeBootstrap(compiledClassNames, workItems);
                final Path bootstrapFileNameRelative = transformPathToMavenPath(BOOTSTRAP_CLASS, ".java");
                final Path rbootstrapFileName = Paths.get(targetDirectory.getPath(), additionalCompilerPath, bootstrapFileNameRelative.toString());
                createSourceFile(rbootstrapFileName, boostrapClass);
            }
            getLog().info("Process Model successfully generated");
            labels.put(LABEL_PREFIX + "processes", publicProcesses.stream().collect(Collectors.joining(",")));
            writeLabelsImageMetadata(targetDirectory.getPath(), labels);
        } catch (Exception e) {
            throw new MojoExecutionException("An error was caught during process generation", e);
        } finally {
            Thread.currentThread().setContextClassLoader(contextClassLoader);
        }


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

    private void createSourceFile(Path newFile, String sourceContent) {
        try {
            Files.deleteIfExists(newFile);
            Files.createDirectories(newFile.getParent());
            Path newFilePath = Files.createFile(newFile);
            Files.write(newFilePath, sourceContent.getBytes());
            getLog().info("Generating file " + newFilePath);
        } catch (IOException e) {
            throw new RuntimeException("Unable to write file", e);
        }
    }

    private String getCompiledClassName(Path fileNameRelative) {
        return fileNameRelative.toString()
                                .replace("/", ".")
                                .replace(".java", "");
    }

    private Path transformPathToMavenPath(String generatedFile, String ext) {
        Path fileName = Paths.get(generatedFile.replaceAll("\\.", "/") + ext);
        Path originalFilePath = Paths.get("src/main/java");
        final Path fileNameRelative;
        if(fileName.startsWith(originalFilePath)) {
            fileNameRelative = originalFilePath.relativize(fileName);
        } else {
            fileNameRelative = fileName;
        }
        return fileNameRelative;
    }

    private void addNewCompileRoot(String droolsModelCompilerPath) {
        final String newCompileSourceRoot = targetDirectory.getPath() + droolsModelCompilerPath;
        project.addCompileSourceRoot(newCompileSourceRoot);
    }
    
    
    public String generateResourceClass(WorkflowProcess process, String processId, String dataClazzName, String modelfqcn, List<String> compiledClassNames) {
       
        CompilationUnit clazz = JavaParser.parse(this.getClass().getResourceAsStream("/class-templates/RestResourceTemplate.java"));
        clazz.setPackageDeclaration(process.getPackageName());
        clazz.addImport(modelfqcn);
        Optional<ClassOrInterfaceDeclaration> resourceClassOptional = clazz.findFirst(ClassOrInterfaceDeclaration.class, sl -> true);

        if (resourceClassOptional.isPresent()) {
            String extractedProcessId = ProcessToExecModelGenerator.INSTANCE.exctactProcessId(process.getId());
            String documentation = (String) process.getMetaData().get("Documentation");
            if (documentation == null) {
                documentation = "Covers life cycle operation of " + extractedProcessId;
            }

            ClassOrInterfaceDeclaration resourceClass = resourceClassOptional.get();

            resourceClass.setName(StringUtils.capitalize(extractedProcessId) + RESOURCE_CLASS_SUFFIX);
            resourceClass.addAnnotation(new SingleMemberAnnotationExpr(new Name("Path"), new StringLiteralExpr("/" + extractedProcessId)))
            .addAnnotation(new SingleMemberAnnotationExpr(new Name("Api"), new StringLiteralExpr(documentation)));

            MethodCallExpr bootstrapMethod = new MethodCallExpr(new NameExpr(BOOTSTRAP_CLASS), "getProcessRuntime");
            resourceClass.addFieldWithInitializer(InternalProcessRuntime.class, "processRuntime", bootstrapMethod, Keyword.PRIVATE);
            resourceClass.addFieldWithInitializer(String.class, "processId", new StringLiteralExpr(processId), Keyword.PRIVATE);

            ClassOrInterfaceType type = JavaParser.parseClassOrInterfaceType(dataClazzName);
            ClassOrInterfaceType processInstanceType = JavaParser.parseClassOrInterfaceType(ProcessInstance.class.getSimpleName());
            ClassOrInterfaceType workFlowInstanceType = JavaParser.parseClassOrInterfaceType(WorkflowProcessInstanceImpl.class.getSimpleName());
            ClassOrInterfaceType optionalType = JavaParser.parseClassOrInterfaceType(Optional.class.getSimpleName());

            // creates new resource
            MethodDeclaration create = resourceClass.addMethod("createResource", Keyword.PUBLIC)
            .setType(type)
            .addAnnotation("POST")
            .addAnnotation(new SingleMemberAnnotationExpr(new Name("Produces"), new NameExpr("MediaType.APPLICATION_JSON")))
            .addAnnotation(new SingleMemberAnnotationExpr(new Name("Consumes"), new NameExpr("MediaType.APPLICATION_JSON")));
            NormalAnnotationExpr createOperationExpression = new NormalAnnotationExpr(new Name("ApiOperation"), new NodeList<>());
            createOperationExpression.addPair("value", "\"Creates new instance of " + extractedProcessId + "\"");
            createOperationExpression.addPair("tags", "{\"" + dataClazzName + "\"}");
            create = create.addAnnotation(createOperationExpression);

            create.addParameter(type, "resource");
            BlockStmt bodyCreate = create.createBody();

            IfStmt nullResource = new IfStmt(new BinaryExpr(new NameExpr("resource"), new NullLiteralExpr(), com.github.javaparser.ast.expr.BinaryExpr.Operator.EQUALS)
                                             , new ExpressionStmt(new AssignExpr(new NameExpr("resource"),
                                                              new ObjectCreationExpr(null, type, NodeList.nodeList()), AssignExpr.Operator.ASSIGN)),
                                             null);
            bodyCreate.addStatement(nullResource);

            MethodCallExpr startProcess = new MethodCallExpr(new FieldAccessExpr(new ThisExpr(), "processRuntime"), "startProcess");
            startProcess.addArgument(new FieldAccessExpr(new ThisExpr(), "processId"));
            startProcess.addArgument(new MethodCallExpr(new NameExpr("resource"), "toMap"));
            VariableDeclarationExpr piField = new VariableDeclarationExpr(processInstanceType, "pi");
            bodyCreate.addStatement(new AssignExpr(piField, startProcess, Operator.ASSIGN));

            MethodCallExpr fromMap = new MethodCallExpr(new NameExpr(type.getName()), "fromMap");
            fromMap.addArgument(new MethodCallExpr(new NameExpr("pi"), "getId"));
            fromMap.addArgument(new MethodCallExpr(new EnclosedExpr(new CastExpr(workFlowInstanceType, new NameExpr("pi"))), "getVariables"));
            bodyCreate.addStatement(new ReturnStmt(fromMap));

            // get all resources
            MethodCallExpr getInstances = new MethodCallExpr(new FieldAccessExpr(new ThisExpr(), "processRuntime"), "getProcessInstances");
            MethodCallExpr streamInstances = new MethodCallExpr(getInstances, "stream");

            BlockStmt filterBody = new BlockStmt();
            MethodCallExpr getProcessId = new MethodCallExpr(new NameExpr("pi"), "getProcessId");
            MethodCallExpr equlasByProcessId = new MethodCallExpr(getProcessId, "equals").addArgument(new FieldAccessExpr(new ThisExpr(), "processId"));
            filterBody.addStatement(new ReturnStmt(equlasByProcessId));
            LambdaExpr filterLambda = new LambdaExpr(
                    new com.github.javaparser.ast.body.Parameter(new UnknownType(), "pi"),
                    filterBody
            );
            MethodCallExpr filterInstances = new MethodCallExpr(streamInstances, "filter").addArgument(filterLambda);

            BlockStmt mapBody = new BlockStmt();
            mapBody.addStatement(new ReturnStmt(fromMap));
            LambdaExpr mapLambda = new LambdaExpr(
                    new com.github.javaparser.ast.body.Parameter(new UnknownType(), "pi"),
                    mapBody
            );

            NormalAnnotationExpr collectOperationExpression = new NormalAnnotationExpr(new Name("ApiOperation"), new NodeList<>());
            collectOperationExpression.addPair("value", "\"Returns a list of " + extractedProcessId + "\"");
            collectOperationExpression.addPair("tags", "{\"" + dataClazzName + "\"}");
            MethodCallExpr mapInstances = new MethodCallExpr(filterInstances, "map").addArgument(mapLambda);
            MethodCallExpr collectInstances = new MethodCallExpr(mapInstances, "collect").addArgument(new MethodCallExpr(new NameExpr(JavaParser.parseClassOrInterfaceType(Collectors.class.getSimpleName()).getName()), "toList"));
            resourceClass.addMethod("getResources", Keyword.PUBLIC).setType("List<" + dataClazzName + ">")
            .addAnnotation("GET")
            .addAnnotation(new SingleMemberAnnotationExpr(new Name("Produces"), new NameExpr("MediaType.APPLICATION_JSON")))
            .addAnnotation(collectOperationExpression)
            .createBody().addStatement(new ReturnStmt(collectInstances));

            // get given resource            
            MethodDeclaration get = resourceClass.addMethod("getResource", Keyword.PUBLIC).setType(dataClazzName)
            .addAnnotation("GET")
            .addAnnotation(new SingleMemberAnnotationExpr(new Name("Path"), new StringLiteralExpr("/{id}")))
            .addAnnotation(new SingleMemberAnnotationExpr(new Name("Produces"), new NameExpr("MediaType.APPLICATION_JSON")));
            NormalAnnotationExpr getOperationExpression = new NormalAnnotationExpr(new Name("ApiOperation"), new NodeList<>());
            getOperationExpression.addPair("value", "\"Returns information about specified " + extractedProcessId + "\"") ;
            getOperationExpression.addPair("tags", "{\"" + dataClazzName + "\"}");
            get = get.addAnnotation(getOperationExpression);

            MethodCallExpr getInstance = new MethodCallExpr(new FieldAccessExpr(new ThisExpr(), "processRuntime"), "getProcessInstance").addArgument(new NameExpr("id")).addArgument(new BooleanLiteralExpr(true));
            MethodCallExpr ofNullable = new MethodCallExpr(new NameExpr(optionalType.getName()), "ofNullable").addArgument(getInstance);
            MethodCallExpr mapInstance = new MethodCallExpr(ofNullable, "map").addArgument(mapLambda);
            MethodCallExpr notPresent = new MethodCallExpr(mapInstance, "orElse").addArgument(new NullLiteralExpr());

            get.addAndGetParameter(Long.class, "id").addAnnotation(new SingleMemberAnnotationExpr(new Name("PathParam"), new StringLiteralExpr("id")));
            get.createBody().addStatement(new ReturnStmt(notPresent));

            // delete given resource
            MethodDeclaration delete = resourceClass.addMethod("deleteResource", Keyword.PUBLIC).setType(dataClazzName)
            .addAnnotation("DELETE")
            .addAnnotation(new SingleMemberAnnotationExpr(new Name("Path"), new StringLiteralExpr("/{id}")))
            .addAnnotation(new SingleMemberAnnotationExpr(new Name("Produces"), new NameExpr("MediaType.APPLICATION_JSON")));
            NormalAnnotationExpr deleteOperationExpression = new NormalAnnotationExpr(new Name("ApiOperation"), new NodeList<>());
            deleteOperationExpression.addPair("value", "\"Cancels specified " + extractedProcessId + "\"");
            deleteOperationExpression.addPair("tags", "{\"" + dataClazzName + "\"}");
            delete = delete.addAnnotation(deleteOperationExpression);

            delete.addAndGetParameter(Long.class, "id").addAnnotation(new SingleMemberAnnotationExpr(new Name("PathParam"), new StringLiteralExpr("id")));
            BlockStmt deleteBody = delete.createBody();
            VariableDeclarationExpr itemField = new VariableDeclarationExpr(type, "item");
            deleteBody.addStatement(new AssignExpr(itemField, notPresent, Operator.ASSIGN));
            deleteBody.addStatement(new MethodCallExpr(new FieldAccessExpr(new ThisExpr(), "processRuntime"), "abortProcessInstance").addArgument(new NameExpr("id")));
            deleteBody.addStatement(new ReturnStmt(new NameExpr("item")));

        }
        return clazz.toString();
    }

    protected String generateProcessRuntimeBootstrap(List<String> compiledClassNames, Set<String> workItems) {
        CompilationUnit clazz = JavaParser.parse(this.getClass().getResourceAsStream("/class-templates/ProcessRuntimeTemplate.java"));
        clazz.setPackageDeclaration(BOOTSTRAP_PACKAGE);
        clazz.addImport(ArrayList.class);
        Optional<ClassOrInterfaceDeclaration> resourceClassOptional = clazz.findFirst(ClassOrInterfaceDeclaration.class, sl -> true);

        if (resourceClassOptional.isPresent()) {

            ClassOrInterfaceDeclaration resourceClass = resourceClassOptional.get();

            // set processes found
            MethodDeclaration getProcessesMethod = resourceClass.findFirst(MethodDeclaration.class, md -> md.getNameAsString().equals("getProcesses")).get();
            BlockStmt body = new BlockStmt();

            ClassOrInterfaceType listType = new ClassOrInterfaceType(null, new SimpleName(List.class.getSimpleName()), NodeList.nodeList(new ClassOrInterfaceType(null, Process.class.getSimpleName())));
            VariableDeclarationExpr processesField = new VariableDeclarationExpr(listType, "processes");

            body.addStatement(new AssignExpr(processesField, new ObjectCreationExpr(null, new ClassOrInterfaceType(null, ArrayList.class.getSimpleName()), NodeList.nodeList()), AssignExpr.Operator.ASSIGN));

            for (String processClass : compiledClassNames) {
                MethodCallExpr addProcess = new MethodCallExpr(new NameExpr("processes"), "add").addArgument(new MethodCallExpr(new NameExpr(processClass), "process"));
                body.addStatement(addProcess);
            }

            body.addStatement(new ReturnStmt(new NameExpr("processes")));
            getProcessesMethod.setBody(body);
            
            
            // set work item handlers if found
            workItems.removeAll(DEFAULT_HANDLERS);
            if (!workItems.isEmpty()) {
                Properties handlers = new Properties();
                
                try (FileInputStream in = new FileInputStream(new File(projectDir, "src/main/resources/workitem-handlers.properties"))){
                    
                    handlers.load(in);
                } catch (Exception e) {
                    getLog().info("Work item handlers config file was not found");
                }
            
            
                MethodDeclaration getHandlersMethod = resourceClass.findFirst(MethodDeclaration.class, md -> md.getNameAsString().equals("getWorkItemHandlers")).get();
                BlockStmt hanldersBody = new BlockStmt();
    
                ClassOrInterfaceType handlersListType = new ClassOrInterfaceType(null, new SimpleName(List.class.getSimpleName()), NodeList.nodeList(new ClassOrInterfaceType(null, WorkItemHandler.class.getSimpleName())));
                VariableDeclarationExpr handlersField = new VariableDeclarationExpr(handlersListType, "handlers");
    
                hanldersBody.addStatement(new AssignExpr(handlersField, new ObjectCreationExpr(null, new ClassOrInterfaceType(null, ArrayList.class.getSimpleName()), NodeList.nodeList()), AssignExpr.Operator.ASSIGN));
    
                for (String workItem : workItems) {
                    String handlerClass = handlers.getProperty(workItem);
                    if (handlerClass == null) {
                        throw new IllegalArgumentException("Cannot find work work item handler for " + workItem);
                    }
                    MethodCallExpr addHandler = new MethodCallExpr(new NameExpr("handlers"), "add").addArgument(new ObjectCreationExpr(null, new ClassOrInterfaceType(null, handlerClass), NodeList.nodeList()));
                    hanldersBody.addStatement(addHandler);
                }
    
                hanldersBody.addStatement(new ReturnStmt(new NameExpr("handlers")));
                getHandlersMethod.setBody(hanldersBody);
            }
        }

        return clazz.toString();
    }

}

