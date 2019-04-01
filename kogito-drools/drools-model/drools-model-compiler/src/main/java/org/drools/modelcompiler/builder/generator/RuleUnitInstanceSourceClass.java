package org.drools.modelcompiler.builder.generator;

import org.drools.compiler.compiler.io.memory.MemoryFileSystem;
import org.drools.javaparser.ast.CompilationUnit;
import org.drools.javaparser.ast.Modifier;
import org.drools.javaparser.ast.body.ClassOrInterfaceDeclaration;
import org.drools.javaparser.ast.expr.MethodCallExpr;
import org.drools.javaparser.ast.expr.NameExpr;
import org.drools.javaparser.ast.stmt.BlockStmt;
import org.drools.javaparser.ast.type.ClassOrInterfaceType;
import org.kie.api.runtime.KieSession;
import org.kie.submarine.rules.impl.AbstractRuleUnitInstance;

public class RuleUnitInstanceSourceClass {

    private final String packageName;
    private final String typeName;
    private final String canonicalName;
    private final String targetTypeName;
    private final String targetCanonicalName;
    private final String sourceFilePath;
    private final String completePath;

    public static String qualifiedName(String packageName, String typeName) {
        return packageName + "." + typeName + "RuleUnitInstance";
    }

    public RuleUnitInstanceSourceClass(String packageName, String typeName) {
        this.packageName = packageName;
        this.typeName = typeName;
        this.canonicalName = packageName + "." + typeName;
        this.targetTypeName = typeName + "RuleUnitInstance";
        this.targetCanonicalName = packageName + "." + targetTypeName;
        this.sourceFilePath = targetCanonicalName.replace('.', '/') + ".java";
        this.completePath = "src/main/java/" + sourceFilePath;
    }

    public void write(MemoryFileSystem srcMfs) {
        srcMfs.write(completePath, generate().getBytes());
    }

    public String generate() {
        return compilationUnit().toString();
    }

    public CompilationUnit compilationUnit() {
        CompilationUnit compilationUnit = new CompilationUnit(packageName);
        compilationUnit.getTypes().add(classDeclaration());
        return compilationUnit;
    }

    public ClassOrInterfaceDeclaration classDeclaration() {
        ClassOrInterfaceDeclaration classDecl = new ClassOrInterfaceDeclaration()
                .setName(targetTypeName)
                .addModifier(Modifier.Keyword.PUBLIC);
        classDecl
                .addExtendedType(
                        new ClassOrInterfaceType(null, AbstractRuleUnitInstance.class.getCanonicalName())
                                .setTypeArguments(new ClassOrInterfaceType(null, canonicalName)))
                .addConstructor(Modifier.Keyword.PUBLIC)
                .addParameter(RuleUnitSourceClass.ruleUnitType(canonicalName), "unit")
                .addParameter(canonicalName, "value")
                .addParameter(KieSession.class.getCanonicalName(), "session")
                .setBody(new BlockStmt().addStatement(new MethodCallExpr(
                        "super",
                        new NameExpr("unit"),
                        new NameExpr("value"),
                        new NameExpr("session")
                )));
        return classDecl;
    }
}
