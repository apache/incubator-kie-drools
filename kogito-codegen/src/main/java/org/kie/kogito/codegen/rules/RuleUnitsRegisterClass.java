package org.kie.kogito.codegen.rules;

import java.util.Map;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.expr.ClassExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.MethodReferenceExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;

import org.drools.modelcompiler.builder.BodyDeclarationComparator;
import org.kie.kogito.codegen.FileGenerator;
import org.kie.kogito.rules.impl.RuleUnitRegistry;

import static org.kie.kogito.codegen.ApplicationGenerator.log;

public class RuleUnitsRegisterClass implements FileGenerator {

    static final String RULE_UNIT_REGISTER_PACKAGE = "org.drools.project.model";
    static final String RULE_UNIT_REGISTER_CLASS = "RuleUnitRegister";
    public static final String RULE_UNIT_REGISTER_FQN = RULE_UNIT_REGISTER_PACKAGE + "." + RULE_UNIT_REGISTER_CLASS;
    static final String RULE_UNIT_REGISTER_RESOURCE_CLASS = RULE_UNIT_REGISTER_FQN.replace('.', '/') + ".class";
    static final String RULE_UNIT_REGISTER_SOURCE = RULE_UNIT_REGISTER_FQN.replace('.', '/') + ".java";

    private final Map<Class<?>, String> unitsMap;

    public RuleUnitsRegisterClass( Map<Class<?>, String> unitsMap) {
        this.unitsMap = unitsMap;
    }

    @Override
    public String generatedFilePath() {
        return RULE_UNIT_REGISTER_SOURCE;
    }

    @Override
    public String generate() {
        CompilationUnit cu = new CompilationUnit();
        cu.setPackageDeclaration( RULE_UNIT_REGISTER_PACKAGE );
        cu.addImport( new ImportDeclaration( RuleUnitRegistry.class.getCanonicalName()  + ".register", true, false ) );

        ClassOrInterfaceDeclaration clazz = cu.addClass( RULE_UNIT_REGISTER_CLASS, Modifier.Keyword.PUBLIC );
        BlockStmt staticBlock = clazz.addStaticInitializer();
        unitsMap.forEach( (k, v) -> staticBlock.addStatement( new MethodCallExpr( "register",
                new ClassExpr( new ClassOrInterfaceType(null, k.getCanonicalName() ) ),
                new MethodReferenceExpr( new NameExpr( v ), null, "new" ) ) ) );
        clazz.getMembers().sort(new BodyDeclarationComparator());
        return log( cu.toString() );
    }
}
