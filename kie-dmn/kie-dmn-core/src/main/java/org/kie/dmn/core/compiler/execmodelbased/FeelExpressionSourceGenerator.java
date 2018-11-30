package org.kie.dmn.core.compiler.execmodelbased;

import java.util.ArrayList;
import java.util.List;

import org.drools.javaparser.ast.NodeList;
import org.drools.javaparser.ast.body.ClassOrInterfaceDeclaration;
import org.drools.javaparser.ast.expr.ArrayInitializerExpr;
import org.drools.javaparser.ast.expr.Expression;
import org.drools.javaparser.ast.expr.NameExpr;
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

    private Class<?> COMPILED_FEEL_EXPRESSION_TYPE = CompiledFEELExpression.class;

    private JavaParserSourceGenerator sourceGenerator;

    public String generate(DMNCompilerContext ctx, DMNFEELHelper feel, DTableModel dTableModel) {
        String pkgName = dTableModel.getNamespace();
        String className = dTableModel.getTableName();

        sourceGenerator = new JavaParserSourceGenerator(className, "FeelExpression", pkgName);
        sourceGenerator.addImports(CompiledFEELSemanticMappings.class,
                                   CompiledCustomFEELFunction.class,
                                   CompiledFEELExpression.class,
                                   CompiledFEELSupport.class,
                                   EvaluationContext.class,
                                   CompiledFEELExpression.class);

        generateInitRows(ctx, dTableModel, className);
        generateInputClauses(ctx, dTableModel);

        String source = sourceGenerator.getSource();
        if (logger.isDebugEnabled()) {
            logger.debug(className + ":\n" + source);
        }
        return source;
    }

    private void generateInitRows(DMNCompilerContext ctx, DTableModel dTableModel, String className) {

        ClassOrInterfaceDeclaration[][] rows = dTableModel.generateRows(ctx.toCompilerContext());
        List<Expression> arrayInitializer = new ArrayList<>();

        for (int i = 0; i < rows.length; i++) {
            ClassOrInterfaceDeclaration[] columns = rows[i];

            NodeList<Expression> arrayInitializerInner = NodeList.nodeList();
            for (int j = 0; j < columns.length; j++) {
                String testClass = className + "r" + i + "c" + j + "expression";

                NameExpr node = instanceName(testClass);

                sourceGenerator.addField(testClass, COMPILED_FEEL_EXPRESSION_TYPE, node.getNameAsString());
                sourceGenerator.addInnerClassWithName(columns[j], testClass);

                arrayInitializerInner.add(node);
            }

            arrayInitializer.add(new ArrayInitializerExpr(arrayInitializerInner));
        }

        sourceGenerator.addTwoDimensionalArray(arrayInitializer, "FEEL_EXPRESSION_ARRAY", COMPILED_FEEL_EXPRESSION_TYPE);
    }

    private void generateInputClauses(DMNCompilerContext ctx, DTableModel dTableModel) {

        List<ClassOrInterfaceDeclaration> inputClauses = dTableModel.generateInputClauses(ctx.toCompilerContext());

        for (int i = 0; i < inputClauses.size(); i++) {
            String testClass = INPUT_CLAUSE_NAMESPACE + i;

            sourceGenerator.addField(testClass, COMPILED_FEEL_EXPRESSION_TYPE, instanceName(testClass).getNameAsString());
            sourceGenerator.addInnerClassWithName(inputClauses.get(i), testClass);
        }
    }

    private NameExpr instanceName(String testClass) {
        return new NameExpr(testClass + "_INSTANCE");
    }
}