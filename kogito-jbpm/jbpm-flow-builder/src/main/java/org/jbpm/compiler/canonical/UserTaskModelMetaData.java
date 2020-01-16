package org.jbpm.compiler.canonical;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.comments.LineComment;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.CastExpr;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.expr.ThisExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import org.drools.core.util.StringUtils;
import org.jbpm.process.core.context.variable.Variable;
import org.jbpm.process.core.context.variable.VariableScope;
import org.jbpm.workflow.core.node.HumanTaskNode;

import static com.github.javaparser.StaticJavaParser.parse;
import static com.github.javaparser.StaticJavaParser.parseClassOrInterfaceType;

public class UserTaskModelMetaData {

    private static final String TASK_INTPUT_CLASS_SUFFIX = "TaskInput";
    private static final String TASK_OUTTPUT_CLASS_SUFFIX = "TaskOutput";
    
    protected static final List<String> INTERNAL_FIELDS = Arrays.asList("TaskName", "NodeName", "ActorId", "GroupId", "Priority", "Comment", "Skippable", "Content", "Locale");

    private final String packageName;

    private final VariableScope variableScope;
    private final HumanTaskNode humanTaskNode;
    private final String processId;

    private String inputModelClassName;
    private String inputMoodelClassSimpleName;

    private String outputModelClassName;
    private String outputMoodelClassSimpleName;

    public UserTaskModelMetaData(String packageName, VariableScope variableScope, HumanTaskNode humanTaskNode, String processId) {
        this.packageName = packageName;
        this.variableScope = variableScope;
        this.humanTaskNode = humanTaskNode;
        this.processId = processId;

        this.inputMoodelClassSimpleName = StringUtils.capitalize(ProcessToExecModelGenerator.extractProcessId(processId) + "_" + humanTaskNode.getId() + "_" + TASK_INTPUT_CLASS_SUFFIX);
        this.inputModelClassName = packageName + '.' + inputMoodelClassSimpleName;

        this.outputMoodelClassSimpleName = StringUtils.capitalize(ProcessToExecModelGenerator.extractProcessId(processId) + "_" + humanTaskNode.getId() + "_" + TASK_OUTTPUT_CLASS_SUFFIX);
        this.outputModelClassName = packageName + '.' + outputMoodelClassSimpleName;

    }

    public String generateInput() {
        CompilationUnit modelClass = compilationUnitInput();
        return modelClass.toString();
    }

    public String generateOutput() {
        CompilationUnit modelClass = compilationUnitOutput();
        return modelClass.toString();
    }

    
    public String getInputModelClassName() {
        return inputModelClassName;
    }

    
    public void setInputModelClassName(String inputModelClassName) {
        this.inputModelClassName = inputModelClassName;
    }

    
    public String getInputMoodelClassSimpleName() {
        return inputMoodelClassSimpleName;
    }

    
    public void setInputMoodelClassSimpleName(String inputMoodelClassSimpleName) {
        this.inputMoodelClassSimpleName = inputMoodelClassSimpleName;
    }

    
    public String getOutputModelClassName() {
        return outputModelClassName;
    }

    
    public void setOutputModelClassName(String outputModelClassName) {
        this.outputModelClassName = outputModelClassName;
    }

    
    public String getOutputMoodelClassSimpleName() {
        return outputMoodelClassSimpleName;
    }

    
    public void setOutputMoodelClassSimpleName(String outputMoodelClassSimpleName) {
        this.outputMoodelClassSimpleName = outputMoodelClassSimpleName;
    }
    
    public String getName() {
        return (String) humanTaskNode.getWork().getParameters().getOrDefault("TaskName", humanTaskNode.getName());
    }
    
    public long getId() {
        return humanTaskNode.getId();
    }

    private CompilationUnit compilationUnitInput() {
        // task input handling
        CompilationUnit compilationUnit = parse(this.getClass().getResourceAsStream("/class-templates/TaskInputTemplate.java"));
        compilationUnit.setPackageDeclaration(packageName);
        Optional<ClassOrInterfaceDeclaration> processMethod = compilationUnit.findFirst(ClassOrInterfaceDeclaration.class, sl1 -> true);

        if (!processMethod.isPresent()) {
            throw new RuntimeException("Cannot find class declaration in the template");
        }
        ClassOrInterfaceDeclaration modelClass = processMethod.get();
        compilationUnit.addOrphanComment(new LineComment("Task input model for user task '" + humanTaskNode.getName() + "' in process '" + processId + "'"));
        
        modelClass.setName(inputMoodelClassSimpleName);

        // setup of static fromMap method body
        ClassOrInterfaceType modelType = new ClassOrInterfaceType(null, modelClass.getNameAsString());
        BlockStmt staticFromMap = new BlockStmt();
        VariableDeclarationExpr itemField = new VariableDeclarationExpr(modelType, "item");
        staticFromMap.addStatement(new AssignExpr(itemField, new ObjectCreationExpr(null, modelType, NodeList.nodeList()), AssignExpr.Operator.ASSIGN));
        NameExpr item = new NameExpr("item");
        FieldAccessExpr idField = new FieldAccessExpr(item, "_id");
        staticFromMap.addStatement(new AssignExpr(idField, new NameExpr("id"), AssignExpr.Operator.ASSIGN));

        FieldAccessExpr nameField = new FieldAccessExpr(item, "_name");
        staticFromMap.addStatement(new AssignExpr(nameField, new NameExpr("name"), AssignExpr.Operator.ASSIGN));

        for (Entry<String, String> entry : humanTaskNode.getInMappings().entrySet()) {
            Variable variable = variableScope.findVariable(entry.getValue());

            if (variable == null) {
                throw new IllegalStateException("Task " + humanTaskNode.getName() +" (input) " + entry.getKey() + " reference not existing variable " + entry.getValue());
            }

            FieldDeclaration fd = new FieldDeclaration().addVariable(
                                                                     new VariableDeclarator()
                                                                                             .setType(variable.getType().getStringType())
                                                                                             .setName(entry.getKey()))
                                                        .addModifier(Modifier.Keyword.PRIVATE);
            modelClass.addMember(fd);

            fd.createGetter();
            fd.createSetter();

            // fromMap static method body
            FieldAccessExpr field = new FieldAccessExpr(item, entry.getKey());

            ClassOrInterfaceType type = parseClassOrInterfaceType(variable.getType().getStringType());
            staticFromMap.addStatement(new AssignExpr(field, new CastExpr(
                                                                          type,
                                                                          new MethodCallExpr(
                                                                                             new NameExpr("params"),
                                                                                             "get")
                                                                                                   .addArgument(new StringLiteralExpr(entry.getKey()))), AssignExpr.Operator.ASSIGN));
        }

        for (Entry<String, Object> entry : humanTaskNode.getWork().getParameters().entrySet()) {

            if (entry.getValue() == null || INTERNAL_FIELDS.contains(entry.getKey())) {
                continue;
            }

            FieldDeclaration fd = new FieldDeclaration().addVariable(
                                                                     new VariableDeclarator()
                                                                                             .setType(entry.getValue().getClass().getCanonicalName())
                                                                                             .setName(entry.getKey()))
                                                        .addModifier(Modifier.Keyword.PRIVATE);
            modelClass.addMember(fd);

            fd.createGetter();
            fd.createSetter();

            // fromMap static method body
            FieldAccessExpr field = new FieldAccessExpr(item, entry.getKey());

            ClassOrInterfaceType type = parseClassOrInterfaceType(entry.getValue().getClass().getCanonicalName());
            staticFromMap.addStatement(new AssignExpr(field, new CastExpr(
                                                                          type,
                                                                          new MethodCallExpr(
                                                                                             new NameExpr("params"),
                                                                                             "get")
                                                                                                   .addArgument(new StringLiteralExpr(entry.getKey()))), AssignExpr.Operator.ASSIGN));
        }
        Optional<MethodDeclaration> staticFromMapMethod = modelClass.findFirst(
                                                                               MethodDeclaration.class, sl -> sl.getName().asString().equals("fromMap") && sl.isStatic());
        if (staticFromMapMethod.isPresent()) {
            MethodDeclaration fromMap = staticFromMapMethod.get();
            fromMap.setType(modelClass.getNameAsString());
            staticFromMap.addStatement(new ReturnStmt(new NameExpr("item")));
            fromMap.setBody(staticFromMap);
        }
        return compilationUnit;
    }

    private CompilationUnit compilationUnitOutput() {
        CompilationUnit compilationUnit = parse(this.getClass().getResourceAsStream("/class-templates/TaskOutputTemplate.java"));
        compilationUnit.setPackageDeclaration(packageName);
        Optional<ClassOrInterfaceDeclaration> processMethod = compilationUnit.findFirst(ClassOrInterfaceDeclaration.class, sl1 -> true);

        if (!processMethod.isPresent()) {
            throw new RuntimeException("Cannot find class declaration in the template");
        }
        ClassOrInterfaceDeclaration modelClass = processMethod.get();
        compilationUnit.addOrphanComment(new LineComment("Task output model for user task '" + humanTaskNode.getName() + "' in process '" + processId + "'"));        
        modelClass.setName(outputMoodelClassSimpleName);

        // setup of the toMap method body
        BlockStmt toMapBody = new BlockStmt();
        ClassOrInterfaceType toMap = new ClassOrInterfaceType(null, new SimpleName(Map.class.getSimpleName()), NodeList.nodeList(new ClassOrInterfaceType(null, String.class.getSimpleName()), new ClassOrInterfaceType(
                                                                                                                                                                                                                        null,
                                                                                                                                                                                                                        Object.class.getSimpleName())));
        VariableDeclarationExpr paramsField = new VariableDeclarationExpr(toMap, "params");
        toMapBody.addStatement(new AssignExpr(paramsField, new ObjectCreationExpr(null, new ClassOrInterfaceType(null, HashMap.class.getSimpleName()), NodeList.nodeList()), AssignExpr.Operator.ASSIGN));

        for (Entry<String, String> entry : humanTaskNode.getOutMappings().entrySet()) {
            if (entry.getValue() == null || INTERNAL_FIELDS.contains(entry.getKey())) {
                continue;
            }
            Variable variable = variableScope.findVariable(entry.getValue());

            if (variable == null) {
                throw new IllegalStateException("Task " + humanTaskNode.getName() +" (output) " + entry.getKey() + " reference not existing variable " + entry.getValue());
            }

            FieldDeclaration fd = new FieldDeclaration().addVariable(
                                                                     new VariableDeclarator()
                                                                                             .setType(variable.getType().getStringType())
                                                                                             .setName(entry.getKey()))
                                                        .addModifier(Modifier.Keyword.PRIVATE);
            modelClass.addMember(fd);

            fd.createGetter();
            fd.createSetter();

            // toMap method body
            MethodCallExpr putVariable = new MethodCallExpr(new NameExpr("params"), "put");
            putVariable.addArgument(new StringLiteralExpr(entry.getKey()));
            putVariable.addArgument(new FieldAccessExpr(new ThisExpr(), entry.getKey()));
            toMapBody.addStatement(putVariable);
        }

        Optional<MethodDeclaration> toMapMethod = modelClass.findFirst(MethodDeclaration.class, sl -> sl.getName().asString().equals("toMap"));

        toMapBody.addStatement(new ReturnStmt(new NameExpr("params")));
        toMapMethod.ifPresent(methodDeclaration -> methodDeclaration.setBody(toMapBody));
        return compilationUnit;
    }
}
