package org.drools.modelcompiler.builder.generator.declaredtype;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.EnumConstantDeclaration;
import com.github.javaparser.ast.body.EnumDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.type.Type;
import org.drools.compiler.lang.descr.EnumDeclarationDescr;
import org.drools.compiler.lang.descr.EnumLiteralDescr;
import org.drools.compiler.lang.descr.TypeFieldDescr;

import static com.github.javaparser.StaticJavaParser.parseType;
import static com.github.javaparser.ast.NodeList.nodeList;
import static org.drools.core.util.StringUtils.ucFirst;

public class EnumGenerator {

    private EnumDeclaration enumDeclaration;

    private List<FieldDeclaration> fields = new ArrayList<>();

    EnumGenerator() {
    }

    public TypeDeclaration generate(EnumDeclarationDescr enumDeclarationDescr) {

        NodeList<Modifier> modifiers = nodeList();

        enumDeclaration = new EnumDeclaration(modifiers, enumDeclarationDescr.getFullTypeName());

        for (Map.Entry<String, TypeFieldDescr> field : enumDeclarationDescr.getFields().entrySet()) {
            addField(field);
        }

        for (EnumLiteralDescr enumLiteral : enumDeclarationDescr.getLiterals()) {
            addEnumerationValue(enumLiteral);
        }

        createConstructor(enumDeclarationDescr);

        return enumDeclaration;
    }

    private void createConstructor(EnumDeclarationDescr enumDeclarationDescr) {
        GeneratedConstructor fullArgumentConstructor = GeneratedConstructor.factoryEnum(enumDeclaration, enumDeclarationDescr.getFields());
        fullArgumentConstructor.generateConstructor(Collections.emptyList(), Collections.emptyList());
    }

    private void addField(Map.Entry<String, TypeFieldDescr> field) {
        Type type = parseType(field.getValue().getPattern().getObjectType());
        String key = field.getKey();
        FieldDeclaration fieldDeclaration = enumDeclaration.addField(type, key);
        fields.add(fieldDeclaration);

        createGetter(type, key);
    }

    private void createGetter(Type type, String key) {
        String accessorName = "get" + ucFirst(key);
        MethodDeclaration getterDeclaration = new MethodDeclaration(nodeList(Modifier.publicModifier()), type, accessorName);
        getterDeclaration.setBody(new BlockStmt(nodeList(new ReturnStmt(new NameExpr(key)))));
        enumDeclaration.addMember(getterDeclaration);
    }

    private void addEnumerationValue(EnumLiteralDescr enumLiteral) {
        EnumConstantDeclaration element = new EnumConstantDeclaration(enumLiteral.getName());
        for (String constructorArgument : enumLiteral.getConstructorArgs()) {
            element.addArgument(new NameExpr(constructorArgument));
        }
        enumDeclaration.addEntry(element);
    }
}
