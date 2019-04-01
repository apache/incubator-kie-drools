package org.drools.modelcompiler.builder.generator;

import java.util.ArrayList;
import java.util.List;

import org.drools.compiler.compiler.io.memory.MemoryFileSystem;
import org.drools.javaparser.ast.CompilationUnit;
import org.drools.javaparser.ast.Modifier;
import org.drools.javaparser.ast.body.ClassOrInterfaceDeclaration;
import org.drools.javaparser.ast.body.MethodDeclaration;
import org.drools.javaparser.ast.expr.ObjectCreationExpr;
import org.drools.javaparser.ast.stmt.BlockStmt;
import org.drools.javaparser.ast.stmt.ReturnStmt;
import org.drools.javaparser.ast.type.ClassOrInterfaceType;
import org.kie.submarine.rules.RuleUnit;

public class ModuleSourceClass {

    private final String packageName;
    private final String sourceFilePath;
    private final String completePath;
    private final String targetCanonicalName;
    private final List<RuleUnitSourceClass> ruleUnits;
    private final List<RuleUnitInstanceSourceClass> ruleUnitInstances;
    private String targetTypeName;

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
        ruleUnits.forEach(r -> r.write(srcMfs));
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

    public static MethodDeclaration ruleUnitFactoryMethod(RuleUnitSourceClass r) {
        return new MethodDeclaration()
                .addModifier(Modifier.Keyword.PUBLIC)
                .setName("create" + r.targetTypeName())
                .setType(r.targetCanonicalName())
                .setBody(new BlockStmt().addStatement(new ReturnStmt(
                        new ObjectCreationExpr()
                                .setType(r.targetCanonicalName()))));
    }

    public static ClassOrInterfaceType ruleUnitType(String canonicalName) {
        return new ClassOrInterfaceType(null, RuleUnit.class.getCanonicalName())
                .setTypeArguments(new ClassOrInterfaceType(null, canonicalName));
    }
}
