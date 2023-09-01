package org.kie.pmml.compiler.commons.codegenfactories;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import org.kie.pmml.api.exceptions.KiePMMLException;
import org.kie.pmml.api.models.TargetValue;
import org.kie.pmml.compiler.commons.utils.JavaParserUtils;

import static org.kie.pmml.commons.Constants.MISSING_BODY_TEMPLATE;
import static org.kie.pmml.commons.Constants.MISSING_VARIABLE_INITIALIZER_TEMPLATE;
import static org.kie.pmml.commons.Constants.MISSING_VARIABLE_IN_BODY;
import static org.kie.pmml.compiler.commons.utils.CommonCodegenUtils.getChainedMethodCallExprFrom;
import static org.kie.pmml.compiler.commons.utils.CommonCodegenUtils.getVariableDeclarator;
import static org.kie.pmml.compiler.commons.utils.JavaParserUtils.MAIN_CLASS_NOT_FOUND;

/**
 * Class meant to provide <i>helper</i> method to retrieve <code>KiePMMLTargetValue</code> code-generators
 * out of <code>KiePMMLTargetValue</code>s
 */
public class KiePMMLTargetValueFactory {

    static final String KIE_PMML_TARGETVALUE_TEMPLATE_JAVA = "KiePMMLTargetValueTemplate.tmpl";
    static final String KIE_PMML_TARGETVALUE_TEMPLATE = "KiePMMLTargetValueTemplate";
    static final String GETKIEPMMLTARGETVALUE = "getKiePMMLTargetValue";
    static final String TARGETVALUE = "targetValue";
    static final ClassOrInterfaceDeclaration TARGETVALUE_TEMPLATE;

    static {
        CompilationUnit cloneCU = JavaParserUtils.getFromFileName(KIE_PMML_TARGETVALUE_TEMPLATE_JAVA);
        TARGETVALUE_TEMPLATE = cloneCU.getClassByName(KIE_PMML_TARGETVALUE_TEMPLATE)
                .orElseThrow(() -> new KiePMMLException(MAIN_CLASS_NOT_FOUND + ": " + KIE_PMML_TARGETVALUE_TEMPLATE));
        TARGETVALUE_TEMPLATE.getMethodsByName(GETKIEPMMLTARGETVALUE).get(0).clone();
    }

    private KiePMMLTargetValueFactory() {
        // Avoid instantiation
    }

    static MethodCallExpr getKiePMMLTargetValueVariableInitializer(final TargetValue targetValueField) {
        final MethodDeclaration methodDeclaration =
                TARGETVALUE_TEMPLATE.getMethodsByName(GETKIEPMMLTARGETVALUE).get(0).clone();
        final BlockStmt targetValueBody =
                methodDeclaration.getBody().orElseThrow(() -> new KiePMMLException(String.format(MISSING_BODY_TEMPLATE, methodDeclaration)));
        final VariableDeclarator variableDeclarator =
                getVariableDeclarator(targetValueBody, TARGETVALUE).orElseThrow(() -> new KiePMMLException(String.format(MISSING_VARIABLE_IN_BODY, TARGETVALUE, targetValueBody)));
        variableDeclarator.setName(targetValueField.getName());
        final ObjectCreationExpr targetValueFieldInstance = TargetValueFactory.getTargetValueVariableInitializer(targetValueField);

        final MethodCallExpr toReturn = variableDeclarator.getInitializer()
                .orElseThrow(() -> new KiePMMLException(String.format(MISSING_VARIABLE_INITIALIZER_TEMPLATE,
                                                                      TARGETVALUE, targetValueBody)))
                .asMethodCallExpr();
        final MethodCallExpr builder = getChainedMethodCallExprFrom("builder", toReturn);
        final StringLiteralExpr nameExpr = new StringLiteralExpr(targetValueField.getName());
        builder.setArgument(0, nameExpr);
        builder.setArgument(2, targetValueFieldInstance);
        return toReturn;
    }
}
