package org.drools.modelcompiler.builder.generator;

import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import org.drools.compiler.compiler.io.memory.MemoryFileSystem;
import org.kie.submarine.rules.RuleUnit;

public class ModuleSourceClass {

    private final String packageName;
    private final String sourceFilePath;
    private final String completePath;
    private final String targetCanonicalName;
    private final List<RuleUnitSourceClass> ruleUnits;
    private final List<RuleUnitInstanceSourceClass> ruleUnitInstances;
    private String targetTypeName;
    private boolean hasCdi;

    public ModuleSourceClass() {
        this.packageName = "org.drools.project.model";
        this.targetTypeName = "Module";
        this.targetCanonicalName = packageName + "." + targetTypeName;
        this.sourceFilePath = targetCanonicalName.replace('.', '/') + ".java";
        this.completePath = "src/main/java/" + sourceFilePath;
        this.ruleUnits = new ArrayList<>();
        this.ruleUnitInstances = new ArrayList<>();
    }

    public void addRuleUnit(RuleUnitSourceClass rusc) {
        ruleUnits.add(rusc);
    }

    public void addRuleUnitInstance(RuleUnitInstanceSourceClass ruisc) {
        ruleUnitInstances.add(ruisc);
    }

    public void write(MemoryFileSystem srcMfs) {
        ruleUnits.forEach(r -> r.withCdi(hasCdi).write(srcMfs));
        ruleUnitInstances.forEach(r -> r.write(srcMfs));
        srcMfs.write(completePath, generate().getBytes());
    }

    public String generate() {
        return compilationUnit().toString();
    }

    public CompilationUnit compilationUnit() {
        CompilationUnit compilationUnit = new CompilationUnit(packageName);
        ClassOrInterfaceDeclaration cls =
                compilationUnit.addClass(targetTypeName);

        for (RuleUnitSourceClass r : ruleUnits) {
            cls.addMember(ruleUnitFactoryMethod(r));
        }

        return compilationUnit;
    }

    public static MethodDeclaration ruleUnitFactoryMethod( RuleUnitSourceClass r) {
        return new MethodDeclaration()
                .addModifier( Modifier.Keyword.PUBLIC)
                .setName("create" + r.targetTypeName())
                .setType(r.targetCanonicalName())
                .setBody(new BlockStmt().addStatement(new ReturnStmt(
                        new ObjectCreationExpr()
                                .setType(r.targetCanonicalName()))));
    }

    public static ClassOrInterfaceType ruleUnitType( String canonicalName) {
        return new ClassOrInterfaceType(null, RuleUnit.class.getCanonicalName())
                .setTypeArguments(new ClassOrInterfaceType(null, canonicalName));
    }

    public ModuleSourceClass withCdi(boolean hasCdi) {
        this.hasCdi = hasCdi;
        return this;
    }
}
