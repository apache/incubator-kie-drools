package org.kie.kogito.codegen.process.persistence;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.kie.kogito.codegen.AbstractGenerator;
import org.kie.kogito.codegen.ApplicationSection;
import org.kie.kogito.codegen.BodyDeclarationComparator;
import org.kie.kogito.codegen.ConfigGenerator;
import org.kie.kogito.codegen.GeneratedFile;
import org.kie.kogito.codegen.di.DependencyInjectionAnnotator;
import org.kie.kogito.codegen.process.persistence.proto.Proto;
import org.kie.kogito.codegen.process.persistence.proto.ProtoGenerator;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.Modifier.Keyword;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.NullLiteralExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExplicitConstructorInvocationStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;


public class PersistenceGenerator extends AbstractGenerator {

    private final File targetDirectory;
    private final Collection<?> modelClasses;    
    private final boolean persistence;
    private final ProtoGenerator<?> protoGenerator;
    
    private List<String> parameters;
    
    private String packageName;
    private DependencyInjectionAnnotator annotator;
    
    private ClassLoader classLoader;
        
    public PersistenceGenerator(File targetDirectory, Collection<?> modelClasses, boolean persistence, ProtoGenerator<?> protoGenerator, List<String> parameters) {
        this(targetDirectory, modelClasses, persistence, protoGenerator, Thread.currentThread().getContextClassLoader(), parameters);
    }
    
    public PersistenceGenerator(File targetDirectory, Collection<?> modelClasses, boolean persistence, ProtoGenerator<?> protoGenerator, ClassLoader classLoader, List<String> parameters) {
        this.targetDirectory = targetDirectory;
        this.modelClasses = modelClasses;
        this.persistence = persistence;
        this.protoGenerator = protoGenerator;
        this.classLoader = classLoader;
        this.parameters = parameters;
    }

    @Override
    public ApplicationSection section() {
        return null;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public Collection<GeneratedFile> generate() {
        List<GeneratedFile> generatedFiles = new ArrayList<>();
        
        Collection dataModelClasses = protoGenerator.extractDataClasses((Collection) modelClasses, targetDirectory.toString());
        Path protoFilePath = Paths.get(targetDirectory.getParent().toString(), "src/main/resources", "/persistence/kogito-application.proto");
        
        if (!Files.exists(protoFilePath)) {
            try {
                // generate proto file based on known data model
                Proto proto = protoGenerator.generate(packageName, dataModelClasses, "import \"kogito-types.proto\";");
                protoFilePath = Paths.get(targetDirectory.toString(), "classes", "/persistence/kogito-application.proto");
            
                Files.createDirectories(protoFilePath.getParent());
                Files.write(protoFilePath, proto.toString().getBytes(StandardCharsets.UTF_8));
            } catch (Exception e) {
                throw new RuntimeException("Error during proto file generation/store", e);
            }
            
        }
        
        if (persistence) {
            ClassOrInterfaceDeclaration persistenceProviderClazz = new ClassOrInterfaceDeclaration()
                    .setName("KogitoProcessInstancesFactoryImpl")
                    .setModifiers(Modifier.Keyword.PUBLIC)
                    .addExtendedType("org.kie.kogito.persistence.KogitoProcessInstancesFactory");
            
            CompilationUnit compilationUnit = new CompilationUnit("org.kie.kogito.persistence");            
            compilationUnit.getTypes().add(persistenceProviderClazz);  
            
            persistenceProviderClazz.addConstructor(Keyword.PUBLIC).setBody(new BlockStmt().addStatement(new ExplicitConstructorInvocationStmt(false, null, NodeList.nodeList(new NullLiteralExpr()))));
            
            ConstructorDeclaration constructor = persistenceProviderClazz.addConstructor(Keyword.PUBLIC);
            
            List<Expression> paramNames = new ArrayList<>();
            for (String parameter : parameters) {
                String name = "param" + paramNames.size();
                constructor.addParameter(parameter, name);
                paramNames.add(new NameExpr(name));
            }
            BlockStmt body = new BlockStmt();
            ExplicitConstructorInvocationStmt superExp = new ExplicitConstructorInvocationStmt(false, null, NodeList.nodeList(paramNames));
            body.addStatement(superExp);
            
            constructor.setBody(body);
            
            if (useInjection()) {
                annotator.withApplicationComponent(persistenceProviderClazz);
                annotator.withInjection(constructor);
            }
            List<String> variableMarshallers = new ArrayList<>();  
            // handler process variable marshallers
            if (Files.exists(protoFilePath)) {
                MarshallerGenerator marshallerGenerator = new MarshallerGenerator(this.classLoader);
                try {
                    String protoContent = new String(Files.readAllBytes(protoFilePath));
                    
                    List<CompilationUnit> marshallers = marshallerGenerator.generate(protoContent);
                    
                    if (!marshallers.isEmpty()) {
                        
                        for (CompilationUnit marshallerClazz : marshallers) {
                            String packageName = marshallerClazz.getPackageDeclaration().map(pd -> pd.getName().toString()).orElse("");
                            String clazzName = packageName + "." + marshallerClazz.findFirst(ClassOrInterfaceDeclaration.class).map(c -> c.getName().toString()).get();
                         
                            variableMarshallers.add(clazzName);
                            
                            generatedFiles.add(new GeneratedFile(GeneratedFile.Type.CLASS,
                                              clazzName.replace('.', '/') + ".java",
                                              marshallerClazz.toString().getBytes(StandardCharsets.UTF_8)));                        
                        }                    
                    }
            
                    // handler process variable marshallers                                     
                    if (!variableMarshallers.isEmpty()) {
                        
                        MethodDeclaration protoMethod = new MethodDeclaration()
                                .addModifier(Keyword.PUBLIC)
                                .setName("proto")
                                .setType(String.class)                                
                                .setBody(new BlockStmt()
                                         .addStatement(new ReturnStmt(new StringLiteralExpr().setString(protoContent))));
                        
                        persistenceProviderClazz.addMember(protoMethod);
                        
                        ClassOrInterfaceType listType = new ClassOrInterfaceType(null, List.class.getCanonicalName());
                        BlockStmt marshallersMethodBody = new BlockStmt();
                        VariableDeclarationExpr marshallerList = new VariableDeclarationExpr(new VariableDeclarator(listType, "list", new ObjectCreationExpr(null, new ClassOrInterfaceType(null, ArrayList.class.getCanonicalName()), NodeList.nodeList())));
                        marshallersMethodBody.addStatement(marshallerList);
                        
                        for (String marshallerClazz : variableMarshallers) {
                            
                            MethodCallExpr addMarshallerMethod = new MethodCallExpr(new NameExpr("list"), "add").addArgument(new ObjectCreationExpr(null, new ClassOrInterfaceType(null, marshallerClazz), NodeList.nodeList()));
                            marshallersMethodBody.addStatement(addMarshallerMethod);
                            
                        }
                        
                        marshallersMethodBody.addStatement(new ReturnStmt(new NameExpr("list")));
                        
                        MethodDeclaration marshallersMethod = new MethodDeclaration()
                                .addModifier(Keyword.PUBLIC)
                                .setName("marshallers")
                                .setType(listType)                                
                                .setBody(marshallersMethodBody);
                        
                        persistenceProviderClazz.addMember(marshallersMethod);
                    }
                        
                    
                    String packageName = compilationUnit.getPackageDeclaration().map(pd -> pd.getName().toString()).orElse("");
                    String clazzName = packageName + "." + persistenceProviderClazz.findFirst(ClassOrInterfaceDeclaration.class).map(c -> c.getName().toString()).get();
                 
                    generatedFiles.add(new GeneratedFile(GeneratedFile.Type.CLASS,
                                                         clazzName.replace('.', '/') + ".java",
                                                         compilationUnit.toString().getBytes(StandardCharsets.UTF_8))); 
                } catch (Exception e) {
                    throw new RuntimeException("Error when generating marshallers for defined variables", e);
                }
                persistenceProviderClazz.getMembers().sort(new BodyDeclarationComparator());
            }
        }
        
        
        return generatedFiles;
    }   

    @Override
    public void updateConfig(ConfigGenerator cfg) {
    }

    @Override
    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    @Override
    public void setDependencyInjection(DependencyInjectionAnnotator annotator) {
        this.annotator = annotator;
    }

    protected boolean useInjection() {
        return this.annotator != null;
    }
}
