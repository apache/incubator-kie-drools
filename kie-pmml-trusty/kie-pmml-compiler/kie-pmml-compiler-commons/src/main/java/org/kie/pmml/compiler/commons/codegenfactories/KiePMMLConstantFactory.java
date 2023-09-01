package org.kie.pmml.compiler.commons.codegenfactories;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import org.dmg.pmml.Constant;
import org.kie.pmml.api.exceptions.KiePMMLException;
import org.kie.pmml.compiler.commons.utils.JavaParserUtils;

import static org.kie.pmml.commons.Constants.MISSING_BODY_TEMPLATE;
import static org.kie.pmml.commons.Constants.MISSING_VARIABLE_INITIALIZER_TEMPLATE;
import static org.kie.pmml.commons.Constants.MISSING_VARIABLE_IN_BODY;
import static org.kie.pmml.compiler.commons.utils.CommonCodegenUtils.getExpressionForDataType;
import static org.kie.pmml.compiler.commons.utils.CommonCodegenUtils.getExpressionForObject;
import static org.kie.pmml.compiler.commons.utils.CommonCodegenUtils.getVariableDeclarator;
import static org.kie.pmml.compiler.commons.utils.JavaParserUtils.MAIN_CLASS_NOT_FOUND;

/**
 * Class meant to provide <i>helper</i> method to retrieve <code>KiePMMLConstant</code> code-generators
 * out of <code>Constant</code>s
 */
public class KiePMMLConstantFactory {

    private KiePMMLConstantFactory() {
        // Avoid instantiation
    }

    static final String KIE_PMML_CONSTANT_TEMPLATE_JAVA = "KiePMMLConstantTemplate.tmpl";
    static final String KIE_PMML_CONSTANT_TEMPLATE = "KiePMMLConstantTemplate";
    static final String GETKIEPMMLCONSTANT = "getKiePMMLConstant";
    static final String CONSTANT = "constant";
    static final ClassOrInterfaceDeclaration CONSTANT_TEMPLATE;

    static {
        CompilationUnit cloneCU = JavaParserUtils.getFromFileName(KIE_PMML_CONSTANT_TEMPLATE_JAVA);
        CONSTANT_TEMPLATE = cloneCU.getClassByName(KIE_PMML_CONSTANT_TEMPLATE)
                .orElseThrow(() -> new KiePMMLException(MAIN_CLASS_NOT_FOUND + ": " + KIE_PMML_CONSTANT_TEMPLATE));
        CONSTANT_TEMPLATE.getMethodsByName(GETKIEPMMLCONSTANT).get(0).clone();
    }

    static BlockStmt getConstantVariableDeclaration(final String variableName, final Constant constant) {
        final MethodDeclaration methodDeclaration =
                CONSTANT_TEMPLATE.getMethodsByName(GETKIEPMMLCONSTANT).get(0).clone();
        final BlockStmt toReturn =
                methodDeclaration.getBody().orElseThrow(() -> new KiePMMLException(String.format(MISSING_BODY_TEMPLATE, methodDeclaration)));
        final VariableDeclarator variableDeclarator =
                getVariableDeclarator(toReturn, CONSTANT).orElseThrow(() -> new KiePMMLException(String.format(MISSING_VARIABLE_IN_BODY, CONSTANT, toReturn)));
        variableDeclarator.setName(variableName);
        final ObjectCreationExpr objectCreationExpr = variableDeclarator.getInitializer()
                .orElseThrow(() -> new KiePMMLException(String.format(MISSING_VARIABLE_INITIALIZER_TEMPLATE, CONSTANT
                        , toReturn)))
                .asObjectCreationExpr();

        final StringLiteralExpr nameExpr = new StringLiteralExpr(variableName);
        final Expression valueExpr = getExpressionForObject(constant.getValue());
        final Expression dataTypeExpression = getExpressionForDataType(constant.getDataType());

        objectCreationExpr.getArguments().set(0, nameExpr);
        objectCreationExpr.getArguments().set(2, valueExpr);
        objectCreationExpr.getArguments().set(3, dataTypeExpression);
        return toReturn;
    }
}
