package org.drools.modelcompiler.util.lambdareplace;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.EnumConstantDeclaration;
import com.github.javaparser.ast.body.EnumDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.LambdaExpr;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.ast.type.UnknownType;

abstract class MaterializedLambda {

    final List<LambdaParameter> lambdaParameters = new ArrayList<>();

    protected final String packageName;
    protected String className;

    LambdaExpr lambdaExpr;
    private String ruleClassName;

    MaterializedLambda(String packageName, String ruleClassName) {
        this.packageName = packageName;
        this.ruleClassName = ruleClassName;
    }

    public CreatedClass create(String expressionString, Collection<String> imports, Collection<String> staticImports) {
        Expression expression = StaticJavaParser.parseExpression(expressionString);

        if (!expression.isLambdaExpr()) {
            throw new NotLambdaException();
        }

        lambdaExpr = expression.asLambdaExpr();
        className = className(expressionString);

        parseParameters();

        CompilationUnit compilationUnit = new CompilationUnit(packageName);
        addImports(imports, staticImports, compilationUnit);

        EnumDeclaration classDeclaration = create(compilationUnit);

        createMethodDeclaration(classDeclaration);

        return new CreatedClass(compilationUnit, className, packageName);
    }

    private void addImports(Collection<String> imports, Collection<String> staticImports, CompilationUnit compilationUnit) {
        compilationUnit.addImport(ruleClassName, true, true);
        for(String i : imports) {
            compilationUnit.addImport(i);
        }
        for(String si : staticImports) {
            String replace = si;
            if(si.endsWith(".*")) { // JP doesn't want the * in the import
                replace = si.replace(".*", "");
                compilationUnit.addImport(replace, true, true);
            } else {
                compilationUnit.addImport(replace, true, false);
            }
        }
        compilationUnit.addImport("org.drools.modelcompiler.dsl.pattern.D");
    }

    private void parseParameters() {
        NodeList<Parameter> parameters = lambdaExpr.getParameters();
        for (Parameter p : parameters) {
            Type c = p.getType();
            if (c instanceof UnknownType) {
                throw new LambdaTypeNeededException(lambdaExpr.toString());
            }
            lambdaParameters.add(new LambdaParameter(p.getNameAsString(), c));
        }
    }

    void setMethodParameter(MethodDeclaration methodDeclaration) {
        for (LambdaParameter parameter : lambdaParameters) {
            methodDeclaration.addParameter(new Parameter(parameter.type, parameter.name));
        }
    }

    private EnumDeclaration create(CompilationUnit compilationUnit) {
        EnumDeclaration lambdaClass = compilationUnit.addEnum(className);
        lambdaClass.addAnnotation(org.drools.compiler.kie.builder.MaterializedLambda.class.getCanonicalName());
        lambdaClass.setImplementedTypes(createImplementedType());
        lambdaClass.addEntry(new EnumConstantDeclaration("INSTANCE"));
        return lambdaClass;
    }

    protected NodeList<ClassOrInterfaceType> createImplementedType() {
        ClassOrInterfaceType functionType = functionType();

        List<Type> typeArguments = lambdaParametersToType();
        if(!typeArguments.isEmpty()) {
            functionType.setTypeArguments(NodeList.nodeList(typeArguments));
        }
        return NodeList.nodeList(functionType);
    }

    List<Type> lambdaParametersToType() {
        return lambdaParameters.stream()
                .map(p -> p.type)
                .collect(Collectors.toList());
    }

    abstract String className(String expressionString);

    abstract ClassOrInterfaceType functionType();

    abstract void createMethodDeclaration(EnumDeclaration classDeclaration);

    static class LambdaParameter {

        String name;
        Type type;

        LambdaParameter(String name, Type type) {
            this.name = name;
            this.type = type;
        }
    }
}
