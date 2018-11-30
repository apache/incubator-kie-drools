package org.kie.dmn.core.compiler.execmodelbased;

import java.util.List;

import org.drools.javaparser.JavaParser;
import org.drools.javaparser.ast.CompilationUnit;
import org.drools.javaparser.ast.NodeList;
import org.drools.javaparser.ast.body.ClassOrInterfaceDeclaration;
import org.drools.javaparser.ast.body.FieldDeclaration;
import org.drools.javaparser.ast.body.VariableDeclarator;
import org.drools.javaparser.ast.expr.ArrayInitializerExpr;
import org.drools.javaparser.ast.expr.Expression;
import org.drools.javaparser.ast.expr.NameExpr;
import org.drools.javaparser.ast.expr.ObjectCreationExpr;
import org.drools.javaparser.ast.type.ClassOrInterfaceType;
import org.kie.dmn.core.compiler.DMNCompilerContext;
import org.kie.dmn.core.compiler.DMNFEELHelper;
import org.kie.dmn.feel.codegen.feel11.CompiledCustomFEELFunction;
import org.kie.dmn.feel.codegen.feel11.CompiledFEELExpression;
import org.kie.dmn.feel.codegen.feel11.CompiledFEELSemanticMappings;
import org.kie.dmn.feel.codegen.feel11.CompiledFEELSupport;
import org.kie.dmn.feel.lang.EvaluationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.kie.dmn.core.compiler.execmodelbased.JavaParserSourceGenerator.PUBLIC_STATIC_FINAL;

public class FeelExpressionSourceGenerator implements ExecModelDMNEvaluatorCompiler.SourceGenerator {

    static final Logger logger = LoggerFactory.getLogger(FeelExpressionSourceGenerator.class);

    public static final String INPUT_CLAUSE_NAMESPACE = "InputClause";

    private ClassOrInterfaceType COMPILED_FEEL_EXPRESSION_TYPE = JavaParser.parseClassOrInterfaceType(CompiledFEELExpression.class.getCanonicalName());

    private JavaParserSourceGenerator javaParserSourceGenerator;

    public String generate(DMNCompilerContext ctx, DMNFEELHelper feel, DTableModel dTableModel) {
        String pkgName = dTableModel.getNamespace();
        String className = dTableModel.getTableName();

        CompilationUnit cu = JavaParser.parse("public class " + className + "FeelExpression { }");
        javaParserSourceGenerator = new JavaParserSourceGenerator(cu, className);
        cu.setPackageDeclaration(pkgName);

        javaParserSourceGenerator.addImports(CompiledFEELSemanticMappings.class,
                                             CompiledCustomFEELFunction.class,
                                             CompiledFEELExpression.class,
                                             CompiledFEELSupport.class,
                                             EvaluationContext.class,
                                             CompiledFEELExpression.class);

        getInitRows(ctx, dTableModel, className);
        getInputClause(ctx, dTableModel);

        String source = cu.toString();
        if (logger.isDebugEnabled()) {
            logger.debug(className + ":\n" + source);
        }
        return source;
    }

    private void getInitRows(DMNCompilerContext ctx, DTableModel dTableModel, String className) {

        ClassOrInterfaceDeclaration[][] rows = dTableModel.generateRows(ctx.toCompilerContext());
        NodeList<Expression> arrayInitializer = NodeList.nodeList();

        for (int i = 0; i < rows.length; i++) {
            ClassOrInterfaceDeclaration[] columns = rows[i];

            NodeList<Expression> arrayInitializerInner = NodeList.nodeList();
            for (int j = 0; j < columns.length; j++) {
                ClassOrInterfaceDeclaration feelExpressionSource = columns[j];
                String testClass = className + "r" + i + "c" + j + "expression";

                NameExpr node = instanceName(testClass);

                javaParserSourceGenerator.addField(testClass, COMPILED_FEEL_EXPRESSION_TYPE, node.getNameAsString());
                javaParserSourceGenerator.addInnerClassWithName(feelExpressionSource, testClass);

                arrayInitializerInner.add(node);
            }

            arrayInitializer.add(new ArrayInitializerExpr(arrayInitializerInner));
        }

        javaParserSourceGenerator.addTwoDimensionalArray(arrayInitializer, "FEEL_EXPRESSION_ARRAY", COMPILED_FEEL_EXPRESSION_TYPE);
    }

    private void getInputClause(DMNCompilerContext ctx, DTableModel dTableModel) {

        List<ClassOrInterfaceDeclaration> inputClauses = dTableModel.generateInputClauses(ctx.toCompilerContext());

        for (int i = 0; i < inputClauses.size(); i++) {
            ClassOrInterfaceDeclaration classOrInterfaceDeclaration = inputClauses.get(i);
            String testClass = INPUT_CLAUSE_NAMESPACE + i;

            NameExpr node = instanceName(testClass);
            ClassOrInterfaceType innerClassType = JavaParser.parseClassOrInterfaceType(testClass);
            ObjectCreationExpr newInstanceOfInnerClass = new ObjectCreationExpr(null, innerClassType, NodeList.nodeList());
            VariableDeclarator variableDeclarator = new VariableDeclarator(COMPILED_FEEL_EXPRESSION_TYPE, node.getName(), newInstanceOfInnerClass);

            FieldDeclaration fieldDeclaration = new FieldDeclaration(PUBLIC_STATIC_FINAL, variableDeclarator);
            javaParserSourceGenerator.addMember(fieldDeclaration);

            javaParserSourceGenerator.addInnerClassWithName(classOrInterfaceDeclaration, testClass);
        }
    }

    private NameExpr instanceName(String testClass) {
        return new NameExpr(testClass + "_INSTANCE");
    }
}