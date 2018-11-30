package org.kie.dmn.core.compiler.execmodelbased;

import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;

import org.drools.javaparser.JavaParser;
import org.drools.javaparser.ast.ArrayCreationLevel;
import org.drools.javaparser.ast.CompilationUnit;
import org.drools.javaparser.ast.Modifier;
import org.drools.javaparser.ast.NodeList;
import org.drools.javaparser.ast.body.ClassOrInterfaceDeclaration;
import org.drools.javaparser.ast.body.ConstructorDeclaration;
import org.drools.javaparser.ast.body.FieldDeclaration;
import org.drools.javaparser.ast.body.VariableDeclarator;
import org.drools.javaparser.ast.expr.ArrayCreationExpr;
import org.drools.javaparser.ast.expr.ArrayInitializerExpr;
import org.drools.javaparser.ast.expr.Expression;
import org.drools.javaparser.ast.expr.NameExpr;
import org.drools.javaparser.ast.expr.ObjectCreationExpr;
import org.drools.javaparser.ast.type.ArrayType;
import org.drools.javaparser.ast.type.ClassOrInterfaceType;
import org.drools.javaparser.ast.visitor.VoidVisitorAdapter;
import org.kie.dmn.core.compiler.DMNCompilerContext;
import org.kie.dmn.core.compiler.DMNFEELHelper;
import org.kie.dmn.feel.codegen.feel11.CompiledCustomFEELFunction;
import org.kie.dmn.feel.codegen.feel11.CompiledFEELExpression;
import org.kie.dmn.feel.codegen.feel11.CompiledFEELSemanticMappings;
import org.kie.dmn.feel.codegen.feel11.CompiledFEELSupport;
import org.kie.dmn.feel.lang.EvaluationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FeelExpressionSourceGenerator implements ExecModelDMNEvaluatorCompiler.SourceGenerator {

    static final Logger logger = LoggerFactory.getLogger(FeelExpressionSourceGenerator.class);

    public static final String INPUT_CLAUSE_NAMESPACE = "InputClause";

    public String generate(DMNCompilerContext ctx, DMNFEELHelper feel, DTableModel dTableModel) {
        String pkgName = dTableModel.getNamespace();
        String clasName = dTableModel.getTableName();

        CompilationUnit cu = JavaParser.parse("public class " + clasName + "FeelExpression { }");
        cu.setPackageDeclaration(pkgName);
        cu.addImport(CompiledFEELSemanticMappings.class);
        cu.addImport(CompiledCustomFEELFunction.class);
        cu.addImport(CompiledFEELExpression.class);
        cu.addImport(CompiledFEELSupport.class);
        cu.addImport(EvaluationContext.class);
        cu.addImport(CompiledFEELExpression.class);

        StringBuilder sb = new StringBuilder();
        sb.append("package ").append(pkgName).append(";\n");
        sb.append("\n");
        sb.append("import java.util.List;\n");
        sb.append("import static org.kie.dmn.feel.codegen.feel11.CompiledFEELSemanticMappings.*;\n");
        sb.append("import org.kie.dmn.feel.codegen.feel11.CompiledCustomFEELFunction;\n");
        sb.append("import org.kie.dmn.feel.codegen.feel11.CompiledFEELExpression;\n");
        sb.append("import org.kie.dmn.feel.codegen.feel11.CompiledFEELSupport;");
        sb.append("import org.kie.dmn.feel.lang.EvaluationContext;\n");
        sb.append("import ").append(CompiledFEELExpression.class.getCanonicalName()).append(";\n");
        sb.append("import static org.kie.dmn.feel.codegen.feel11.CompiledFEELSemanticMappings.*;\n");
        sb.append("\n");
        sb.append("public class ").append(clasName).append("FeelExpression {\n");
        sb.append("\n");
        sb.append(getInitRows(ctx, dTableModel, clasName, cu));
        sb.append("\n");
        sb.append(getInputClause(ctx, dTableModel, cu));
        sb.append("\n");
        sb.append("}\n");

        String source = sb.toString();
        if (logger.isDebugEnabled()) {
            logger.debug(clasName + ":\n" + source);
        }
        String source2 = cu.toString();

        return source2;
    }

    ClassOrInterfaceType COMPILED_FEEL_EXPRESSION_TYPE = JavaParser.parseClassOrInterfaceType(CompiledFEELExpression.class.getCanonicalName());
    EnumSet<Modifier> PUBLIC_STATIC_FINAL = EnumSet.of(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL);

    public String getInitRows(DMNCompilerContext ctx, DTableModel dTableModel, String className, CompilationUnit parentCU) {

        StringBuilder testArrayBuilder = new StringBuilder();
        StringBuilder testsBuilder = new StringBuilder();
        StringBuilder instancesBuilder = new StringBuilder();

        testArrayBuilder.append("    public static final CompiledFEELExpression[][] FEEL_EXPRESSION_ARRAY = new CompiledFEELExpression[][] {\n");

        ClassOrInterfaceDeclaration[][] rows = dTableModel.generateRows(ctx.toCompilerContext());

        parentCU.accept(new VoidVisitorAdapter<Void>() {
                            @Override
                            public void visit(ClassOrInterfaceDeclaration coid, Void arg) {

                                NodeList<Expression> arrayInitializer = NodeList.nodeList();

                                for (int i = 0; i < rows.length; i++) {
                                    testArrayBuilder.append("            { ");
                                    ClassOrInterfaceDeclaration[] cols = rows[i];

                                    NodeList<Expression> arrayInitializerInner = NodeList.nodeList();

                                    for (int j = 0; j < cols.length; j++) {
                                        ClassOrInterfaceDeclaration feelExpressionSource = cols[j];
                                        String testClass = className + "r" + i + "c" + j + "expression";
                                        instancesBuilder.append("    public static final CompiledFEELExpression " + testClass + "_INSTANCE = new " + testClass + "();\n");

                                        renameFeelExpressionClass(testClass, feelExpressionSource);

                                        testsBuilder.append("\n");
                                        testsBuilder.append(feelExpressionSource.toString());
                                        testsBuilder.append("\n");
                                        testArrayBuilder.append(testClass).append("_INSTANCE");

                                        NameExpr node = new NameExpr(testClass + "_INSTANCE");

                                        ClassOrInterfaceType innerClassType = JavaParser.parseClassOrInterfaceType(testClass);
                                        ObjectCreationExpr newInstanceOfInnerClass = new ObjectCreationExpr(null, innerClassType, NodeList.nodeList());
                                        VariableDeclarator variableDeclarator = new VariableDeclarator(COMPILED_FEEL_EXPRESSION_TYPE, node.getName(), newInstanceOfInnerClass);

                                        FieldDeclaration fieldDeclaration = new FieldDeclaration(PUBLIC_STATIC_FINAL, variableDeclarator);
                                        coid.addMember(fieldDeclaration);

                                        coid.addMember(feelExpressionSource);

                                        arrayInitializerInner.add(node);

                                        if (j < cols.length - 1) {
                                            testArrayBuilder.append(", ");
                                        }
                                    }
                                    if (i < rows.length - 1) {
                                        testArrayBuilder.append(" },\n");
                                    } else {
                                        testArrayBuilder.append(" }\n");
                                    }

                                    arrayInitializer.add(new ArrayInitializerExpr(arrayInitializerInner));
                                }

                                testArrayBuilder.append("    };\n");

                                NodeList<ArrayCreationLevel> arrayCreationLevels = NodeList.nodeList(new ArrayCreationLevel(), new ArrayCreationLevel());
                                ArrayInitializerExpr initializerMainArray = new ArrayInitializerExpr(arrayInitializer);
                                ArrayCreationExpr arrayCreationExpr = new ArrayCreationExpr(COMPILED_FEEL_EXPRESSION_TYPE, arrayCreationLevels, initializerMainArray);
                                VariableDeclarator variable = new VariableDeclarator(new ArrayType(new ArrayType(COMPILED_FEEL_EXPRESSION_TYPE)), "FEEL_EXPRESSION_ARRAY", arrayCreationExpr);
                                FieldDeclaration fieldDeclaration = new FieldDeclaration(PUBLIC_STATIC_FINAL, variable);
                                coid.addMember(fieldDeclaration);
                            }
                        }
                , null);

        return instancesBuilder + "\n" + testArrayBuilder + "\n" + testsBuilder;
    }

    public String getInputClause(DMNCompilerContext ctx, DTableModel dTableModel, CompilationUnit parentCU) {
        StringBuilder testsBuilder = new StringBuilder();
        StringBuilder instancesBuilder = new StringBuilder();

        List<ClassOrInterfaceDeclaration> inputClauses = dTableModel.generateInputClauses(ctx.toCompilerContext());

        parentCU.accept(new VoidVisitorAdapter<Void>() {
                            @Override
                            public void visit(ClassOrInterfaceDeclaration coid, Void arg) {

                                int i = 0;
                                for (Iterator<ClassOrInterfaceDeclaration> iterator = inputClauses.iterator(); iterator.hasNext(); ) {
                                    ClassOrInterfaceDeclaration classOrInterfaceDeclaration = iterator.next();
                                    String testClass = INPUT_CLAUSE_NAMESPACE + i;

                                    instancesBuilder.append("    public static final CompiledFEELExpression " + testClass + "_INSTANCE = new " + testClass + "();\n");

                                    renameFeelExpressionClass(testClass, classOrInterfaceDeclaration);

                                    testsBuilder.append("\n");
                                    testsBuilder.append(classOrInterfaceDeclaration.toString());
                                    testsBuilder.append("\n");
                                    i++;

                                    NameExpr node = new NameExpr(testClass + "_INSTANCE");
                                    ClassOrInterfaceType innerClassType = JavaParser.parseClassOrInterfaceType(testClass);
                                    ObjectCreationExpr newInstanceOfInnerClass = new ObjectCreationExpr(null, innerClassType, NodeList.nodeList());
                                    VariableDeclarator variableDeclarator = new VariableDeclarator(COMPILED_FEEL_EXPRESSION_TYPE, node.getName(), newInstanceOfInnerClass);

                                    FieldDeclaration fieldDeclaration = new FieldDeclaration(PUBLIC_STATIC_FINAL, variableDeclarator);
                                    coid.addMember(fieldDeclaration);

                                    coid.addMember(classOrInterfaceDeclaration);
                                }
                            }
                        }
                , null);

        return instancesBuilder + "\n" + testsBuilder;
    }

    private static void renameFeelExpressionClass(String testClass, ClassOrInterfaceDeclaration classOrInterfaceDeclaration) {
        final String finalTestClass = testClass;
        classOrInterfaceDeclaration
                .setName(finalTestClass);

        classOrInterfaceDeclaration.findAll(ConstructorDeclaration.class)
                .forEach(n -> n.replace(new ConstructorDeclaration(finalTestClass)));
    }
}