package org.drools.modelcompiler.builder.generator;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import org.drools.compiler.lang.descr.FunctionDescr;
import org.drools.javaparser.ast.Modifier;
import org.drools.javaparser.ast.body.MethodDeclaration;
import org.drools.javaparser.ast.body.Parameter;

import static org.drools.javaparser.JavaParser.parseType;
import static org.drools.javaparser.ast.NodeList.nodeList;

public class FunctionGenerator {

    public static MethodDeclaration toFunction(FunctionDescr desc) {

        List<Parameter> parameters = new ArrayList<>();

        List<String> parameterTypes = desc.getParameterTypes();
        for (int i = 0; i < parameterTypes.size(); i++) {
            String type = parameterTypes.get(i);
            String name = desc.getParameterNames().get(i);
            parameters.add(new Parameter(parseType(type), name));
        }

        EnumSet<Modifier> modifiers = EnumSet.of(Modifier.PUBLIC, Modifier.STATIC);
        MethodDeclaration methodDeclaration = new MethodDeclaration(modifiers, desc.getName(), parseType(desc.getReturnType()), nodeList(parameters));
        methodDeclaration.setBody(DrlxParseUtil.parseBlock(desc.getBody()));

        return methodDeclaration;
    }
}
