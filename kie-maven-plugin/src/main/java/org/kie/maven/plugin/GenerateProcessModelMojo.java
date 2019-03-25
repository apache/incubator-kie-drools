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
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.drools.compiler.kie.builder.impl.KieBuilderImpl;
import org.drools.core.runtime.process.InternalProcessRuntime;
import org.drools.core.util.StringUtils;
import org.jbpm.assembler.BPMN2AssemblerService;
import org.jbpm.compiler.canonical.ProcessToExecModelGenerator;
import org.jbpm.workflow.instance.impl.WorkflowProcessInstanceImpl;
import org.kie.api.KieServices;
import org.kie.api.definition.process.Process;
import org.kie.api.definition.process.WorkflowProcess;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceConfiguration;
import org.kie.api.io.ResourceType;
import org.kie.api.io.ResourceWithConfiguration;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceWithConfigurationImpl;

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

        KieServices ks = KieServices.Factory.get();

        try {
            setSystemProperties(properties);

            final KieBuilderImpl kieBuilder = (KieBuilderImpl) ks.newKieBuilder(projectDir);

            final List<String> compiledClassNames = new ArrayList<>();     

            InternalKieModule kieModule = (InternalKieModule) kieBuilder.getKieModuleIgnoringErrors();
            List<String> processFiles = getBPMNFiles(kieModule);
            getLog().debug("Process Files to process: " + processFiles);

            List<Process> processes = new ArrayList<>();
            KnowledgeBuilder knowledgeBuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
            BPMN2AssemblerService assemblerService = new BPMN2AssemblerService() {

                @Override
                protected void onProcessAdded(Process process, Object kbuilder) {                   
                    super.onProcessAdded(process, kbuilder);
                    processes.add(process);
                }
            };            

            for (String bpmnFile : processFiles) {
                compileBPMNFile(kieModule, assemblerService, knowledgeBuilder, bpmnFile);
            }
            
            
            final String additionalCompilerPath = "/generated-sources/process/main/java";
            addNewCompileRoot(additionalCompilerPath);

            for (Process process : processes) {
                WorkflowProcess workFlowProcess = (WorkflowProcess) process;

                String classPrefix = StringUtils.capitalize(ProcessToExecModelGenerator.INSTANCE.exctactProcessId(process.getId()));
                String packageName = process.getPackageName().replaceAll("\\.", "/");
                
                String sourceContent = ProcessToExecModelGenerator.INSTANCE.generate(workFlowProcess);
                // create class with executable model for the process
                String processClazzName = classPrefix + "Process";
                final Path processFileNameRelative = transformPathToMavenPath( packageName + "/" + processClazzName + ".java");

                compiledClassNames.add(getCompiledClassName(processFileNameRelative));

                final Path processFileName = Paths.get(targetDirectory.getPath(), additionalCompilerPath, processFileNameRelative.toString());                
                createSourceFile(processFileName, sourceContent);
                
                if (WorkflowProcess.PUBLIC_VISIBILITY.equalsIgnoreCase(workFlowProcess.getVisibility())) {
                
                    // create model class for all variables
                    String modelClazzName = classPrefix + "Model";
                    String modelDataClazz = ProcessToExecModelGenerator.INSTANCE.generateModel(workFlowProcess);
                    
                    final Path modelFileNameRelative = transformPathToMavenPath( packageName + "/" + modelClazzName + ".java");
                    final Path modelFileName = Paths.get(targetDirectory.getPath(), additionalCompilerPath, modelFileNameRelative.toString());
                    createSourceFile(modelFileName, modelDataClazz);
                    
                
                    // create REST resource class for process
                    String resourceClazzName = classPrefix + "Resource";
                    String resourceClazz = generateResourceClass(workFlowProcess, process.getId(), modelClazzName, compiledClassNames);
                    
                    final Path resourceFileNameRelative = transformPathToMavenPath( packageName + "/" + resourceClazzName + ".java");
                    final Path resourceFileName = Paths.get(targetDirectory.getPath(), additionalCompilerPath, resourceFileNameRelative.toString());
                    createSourceFile(resourceFileName, resourceClazz);
                }
            }
            if (!compiledClassNames.isEmpty()) {
                String boostrapClass = generateProcessRuntimeBootstrap(compiledClassNames);
                final Path bootstrapFileNameRelative = transformPathToMavenPath(BOOTSTRAP_CLASS.replaceAll("\\.", File.separator) + ".java");
                final Path rbootstrapFileName = Paths.get(targetDirectory.getPath(), additionalCompilerPath, bootstrapFileNameRelative.toString());
                createSourceFile(rbootstrapFileName, boostrapClass);
            }
            getLog().info("Process Model successfully generated");
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            Thread.currentThread().setContextClassLoader(contextClassLoader);
        }

        
    }


    private List<String> getBPMNFiles(InternalKieModule kieModule) {
        return kieModule.getFileNames()
                        .stream()
                        .filter(f -> f.endsWith("bpmn") || f.endsWith("bpmn2"))
                        .collect(Collectors.toList());
    }

    private void compileBPMNFile(InternalKieModule kieModule, BPMN2AssemblerService assemblerService, KnowledgeBuilder knowledgeBuilder, String dmnFile) throws Exception {
        Resource resource = kieModule.getResource(dmnFile);
        ResourceConfiguration resourceConfiguration = kieModule.getResourceConfiguration(dmnFile);

        ResourceWithConfiguration resourceWithConfiguration =
                new ResourceWithConfigurationImpl(resource, resourceConfiguration, a -> {
                }, b -> {
                });

        assemblerService.addResources(knowledgeBuilder, Collections.singletonList(resourceWithConfiguration), ResourceType.BPMN2);        
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

    private Path transformPathToMavenPath(String generatedFile) {
        Path fileName = Paths.get(generatedFile);
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
    
    
    public String generateResourceClass(WorkflowProcess process, String processId, String dataClazzName, List<String> compiledClassNames) {
       
        CompilationUnit clazz = JavaParser.parse(this.getClass().getResourceAsStream("/class-templates/RestResourceTemplate.java"));                
        clazz.setPackageDeclaration(process.getPackageName());
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
            .addAnnotation(new SingleMemberAnnotationExpr(new Name("Consumes"), new NameExpr("MediaType.APPLICATION_JSON")))
            .addAnnotation(new SingleMemberAnnotationExpr(new Name("ApiOperation"), new StringLiteralExpr("Creates new instance of " + extractedProcessId)));
            
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
            
            MethodCallExpr mapInstances = new MethodCallExpr(filterInstances, "map").addArgument(mapLambda);
            MethodCallExpr collectInstances = new MethodCallExpr(mapInstances, "collect").addArgument(new MethodCallExpr(new NameExpr(JavaParser.parseClassOrInterfaceType(Collectors.class.getSimpleName()).getName()), "toList"));
            resourceClass.addMethod("getResources", Keyword.PUBLIC).setType("List<" + dataClazzName + ">")
            .addAnnotation("GET")
            .addAnnotation(new SingleMemberAnnotationExpr(new Name("Produces"), new NameExpr("MediaType.APPLICATION_JSON")))
            .addAnnotation(new SingleMemberAnnotationExpr(new Name("ApiOperation"), new StringLiteralExpr("Returns a list of " + extractedProcessId)))
            .createBody().addStatement(new ReturnStmt(collectInstances));
   
            // get given resource            
            MethodDeclaration get = resourceClass.addMethod("getResource", Keyword.PUBLIC).setType(dataClazzName)
            .addAnnotation("GET")
            .addAnnotation(new SingleMemberAnnotationExpr(new Name("Path"), new StringLiteralExpr("/{id}")))
            .addAnnotation(new SingleMemberAnnotationExpr(new Name("Produces"), new NameExpr("MediaType.APPLICATION_JSON")))
            .addAnnotation(new SingleMemberAnnotationExpr(new Name("ApiOperation"), new StringLiteralExpr("Returns information about specified " + extractedProcessId)));
            
            
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
            .addAnnotation(new SingleMemberAnnotationExpr(new Name("Produces"), new NameExpr("MediaType.APPLICATION_JSON")))
            .addAnnotation(new SingleMemberAnnotationExpr(new Name("ApiOperation"), new StringLiteralExpr("Cancels specified " + extractedProcessId)));
            
            delete.addAndGetParameter(Long.class, "id").addAnnotation(new SingleMemberAnnotationExpr(new Name("PathParam"), new StringLiteralExpr("id")));
            BlockStmt deleteBody = delete.createBody();            
            VariableDeclarationExpr itemField = new VariableDeclarationExpr(type, "item");            
            deleteBody.addStatement(new AssignExpr(itemField, notPresent, Operator.ASSIGN));
            deleteBody.addStatement(new MethodCallExpr(new FieldAccessExpr(new ThisExpr(), "processRuntime"), "abortProcessInstance").addArgument(new NameExpr("id")));
            deleteBody.addStatement(new ReturnStmt(new NameExpr("item")));
        
        }
        return clazz.toString();
    }
    
    protected String generateProcessRuntimeBootstrap(List<String> compiledClassNames) {
        CompilationUnit clazz = JavaParser.parse(this.getClass().getResourceAsStream("/class-templates/ProcessRuntimeTemplate.java"));                
        clazz.setPackageDeclaration(BOOTSTRAP_PACKAGE);
        clazz.addImport(ArrayList.class);
        Optional<ClassOrInterfaceDeclaration> resourceClassOptional = clazz.findFirst(ClassOrInterfaceDeclaration.class, sl -> true);

        if (resourceClassOptional.isPresent()) {

            ClassOrInterfaceDeclaration resourceClass = resourceClassOptional.get();
            
            
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
            
        }
        
        return clazz.toString();
    }

}

