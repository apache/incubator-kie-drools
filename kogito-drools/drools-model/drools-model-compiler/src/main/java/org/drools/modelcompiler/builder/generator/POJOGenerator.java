package org.drools.modelcompiler.builder.generator;

import java.util.EnumSet;

import org.drools.compiler.lang.descr.TypeDeclarationDescr;
import org.drools.compiler.lang.descr.TypeFieldDescr;
import org.drools.javaparser.JavaParser;
import org.drools.javaparser.ast.Modifier;
import org.drools.javaparser.ast.body.ClassOrInterfaceDeclaration;
import org.drools.javaparser.ast.body.FieldDeclaration;
import org.drools.javaparser.ast.type.ClassOrInterfaceType;

public class POJOGenerator {

    public static ClassOrInterfaceDeclaration toClassDeclaration(TypeDeclarationDescr typeDeclaration) {

        EnumSet<Modifier> classModifiers = EnumSet.of(Modifier.PUBLIC);
        ClassOrInterfaceDeclaration generatedClass = new ClassOrInterfaceDeclaration(classModifiers, false, typeDeclaration.getTypeName());

        for (TypeFieldDescr kv : typeDeclaration.getFields().values()) {
            String fieldName = kv.getFieldName();
            String typeName = kv.getPattern().getObjectType();

            ClassOrInterfaceType returnType = JavaParser.parseClassOrInterfaceType(typeName);

            FieldDeclaration field = generatedClass.addField(returnType, fieldName, Modifier.PRIVATE);
            field.createSetter();
            field.createGetter();
        }

        return generatedClass;
    }
}
