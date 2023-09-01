package org.kie.pmml.compiler.commons.codegenfactories;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import org.dmg.pmml.FieldRef;
import org.kie.pmml.api.exceptions.KiePMMLException;
import org.kie.pmml.compiler.commons.utils.JavaParserUtils;

import static org.kie.pmml.commons.Constants.MISSING_BODY_TEMPLATE;
import static org.kie.pmml.commons.Constants.MISSING_VARIABLE_INITIALIZER_TEMPLATE;
import static org.kie.pmml.commons.Constants.MISSING_VARIABLE_IN_BODY;
import static org.kie.pmml.compiler.commons.utils.CommonCodegenUtils.getExpressionForObject;
import static org.kie.pmml.compiler.commons.utils.CommonCodegenUtils.getVariableDeclarator;
import static org.kie.pmml.compiler.commons.utils.JavaParserUtils.MAIN_CLASS_NOT_FOUND;

/**
 * Class meant to provide <i>helper</i> method to retrieve <code>KiePMMLFieldRef</code> code-generators
 * out of <code>FieldRef</code>s
 */
public class KiePMMLFieldRefFactory {

    private KiePMMLFieldRefFactory() {
        // Avoid instantiation
    }

    static final String KIE_PMML_FIELDREF_TEMPLATE_JAVA = "KiePMMLFieldRefTemplate.tmpl";
    static final String KIE_PMML_FIELDREF_TEMPLATE = "KiePMMLFieldRefTemplate";
    static final String GETKIEPMMLFIELDREF = "getKiePMMLFieldRef";
    static final String FIELD_REF = "fieldRef";
    static final ClassOrInterfaceDeclaration FIELDREF_TEMPLATE;

    static {
        CompilationUnit cloneCU = JavaParserUtils.getFromFileName(KIE_PMML_FIELDREF_TEMPLATE_JAVA);
        FIELDREF_TEMPLATE = cloneCU.getClassByName(KIE_PMML_FIELDREF_TEMPLATE)
                .orElseThrow(() -> new KiePMMLException(MAIN_CLASS_NOT_FOUND + ": " + KIE_PMML_FIELDREF_TEMPLATE));
        FIELDREF_TEMPLATE.getMethodsByName(GETKIEPMMLFIELDREF).get(0).clone();
    }

    static BlockStmt getFieldRefVariableDeclaration(final String variableName, final FieldRef fieldRef) {
        final MethodDeclaration methodDeclaration =
                FIELDREF_TEMPLATE.getMethodsByName(GETKIEPMMLFIELDREF).get(0).clone();
        final BlockStmt toReturn =
                methodDeclaration.getBody().orElseThrow(() -> new KiePMMLException(String.format(MISSING_BODY_TEMPLATE, methodDeclaration)));
        final VariableDeclarator variableDeclarator =
                getVariableDeclarator(toReturn, FIELD_REF).orElseThrow(() -> new KiePMMLException(String.format(MISSING_VARIABLE_IN_BODY, FIELD_REF, toReturn)));
        variableDeclarator.setName(variableName);
        final ObjectCreationExpr objectCreationExpr = variableDeclarator.getInitializer()
                .orElseThrow(() -> new KiePMMLException(String.format(MISSING_VARIABLE_INITIALIZER_TEMPLATE,
                                                                      FIELD_REF, toReturn)))
                .asObjectCreationExpr();

        final StringLiteralExpr nameExpr = new StringLiteralExpr(fieldRef.getField().getValue());
        final Expression mapMissingToExpr = getExpressionForObject(fieldRef.getMapMissingTo());
        objectCreationExpr.getArguments().set(0, nameExpr);
        objectCreationExpr.getArguments().set(2, mapMissingToExpr);
        return toReturn;
    }
}
