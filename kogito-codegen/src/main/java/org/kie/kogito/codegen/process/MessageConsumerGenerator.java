package org.kie.kogito.codegen.process;

import static com.github.javaparser.StaticJavaParser.parse;
import static org.kie.kogito.codegen.process.CodegenUtils.interpolateArguments;
import static org.kie.kogito.codegen.process.CodegenUtils.interpolateTypes;
import static org.kie.kogito.codegen.process.CodegenUtils.isProcessField;

import org.drools.core.util.StringUtils;
import org.jbpm.compiler.canonical.TriggerMetaData;
import org.kie.api.definition.process.WorkflowProcess;
import org.kie.kogito.codegen.di.DependencyInjectionAnnotator;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier.Keyword;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.ThisExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;

public class MessageConsumerGenerator {
    private final String relativePath;

    private WorkflowProcess process;
    private final String packageName;
    private final String resourceClazzName;
    private final String processClazzName;
    private String processId;
    private String dataClazzName;
    private String modelfqcn;
    private final String processName;
    private DependencyInjectionAnnotator annotator;
    
    private TriggerMetaData trigger;
    
    public MessageConsumerGenerator(
            WorkflowProcess process,
            String modelfqcn,
            String processfqcn,
            TriggerMetaData trigger) {
        this.process = process;
        this.trigger = trigger;
        this.packageName = process.getPackageName();
        this.processId = process.getId();
        this.processName = processId.substring(processId.lastIndexOf('.') + 1);
        String classPrefix = StringUtils.capitalize(processName);
        this.resourceClazzName = classPrefix + "MessageConsumer_" + trigger.getOwnerId();
        this.relativePath = packageName.replace(".", "/") + "/" + resourceClazzName + ".java";
        this.modelfqcn = modelfqcn;
        this.dataClazzName = modelfqcn.substring(modelfqcn.lastIndexOf('.') + 1);
        this.processClazzName = processfqcn;
    }

    public MessageConsumerGenerator withDependencyInjection(DependencyInjectionAnnotator annotator) {
        this.annotator = annotator;
        return this;
    }

    public String className() {
        return resourceClazzName;
    }
    
    public String generatedFilePath() {
        return relativePath;
    }
    
    protected boolean useInjection() {
        return this.annotator != null;
    }
    
    public String generate() {
        CompilationUnit clazz = parse(
                this.getClass().getResourceAsStream("/class-templates/MessageConsumerTemplate.java"));
        clazz.setPackageDeclaration(process.getPackageName());
        clazz.addImport(modelfqcn);

        ClassOrInterfaceDeclaration template = clazz.findFirst(ClassOrInterfaceDeclaration.class).get();
        template.setName(resourceClazzName);        
        
        template.findAll(ClassOrInterfaceType.class).forEach(cls -> interpolateTypes(cls, dataClazzName));
        template.findAll(MethodDeclaration.class).stream().filter(md -> md.getNameAsString().equals("consume")).forEach(md -> interpolateArguments(md, trigger.getDataType()));
        template.findAll(MethodCallExpr.class).forEach(this::interpolateStrings);
        
        if (useInjection()) {
            annotator.withApplicationComponent(template);
            
            template.findAll(FieldDeclaration.class,
                             fd -> isProcessField(fd)).forEach(this::annotateFields);
            
            template.findAll(MethodDeclaration.class).stream().filter(md -> md.getNameAsString().equals("consume")).forEach(md -> annotator.withIncomingMessage(md, trigger.getName()));
        } else {
            template.findAll(FieldDeclaration.class,
                             fd -> isProcessField(fd)).forEach(fd -> initializeField(fd, template));
        }
        
        return clazz.toString();
    }
    
    private void initializeField(FieldDeclaration fd, ClassOrInterfaceDeclaration template) {
        BlockStmt body = new BlockStmt();
        AssignExpr assignExpr = new AssignExpr(
                                               new FieldAccessExpr(new ThisExpr(), "process"),
                                               new ObjectCreationExpr().setType(processClazzName),
                                               AssignExpr.Operator.ASSIGN);
        
        body.addStatement(assignExpr);
        template.addConstructor(Keyword.PUBLIC).setBody(body);
    }
    
    private void interpolateStrings(MethodCallExpr vv) {
        String s = vv.getNameAsString();        
        String interpolated =
                s.replace("$ModelRef$", StringUtils.capitalize(trigger.getModelRef()));
        vv.setName(interpolated);
    }
    
    private void annotateFields(FieldDeclaration fd) {       
        annotator.withNamedInjection(fd, processId);
    }
}
