package org.kie.pmml.compiler.commons.codegenfactories;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import org.dmg.pmml.NormDiscrete;
import org.kie.pmml.api.exceptions.KiePMMLException;
import org.kie.pmml.compiler.commons.utils.JavaParserUtils;

import static org.kie.pmml.commons.Constants.MISSING_BODY_TEMPLATE;
import static org.kie.pmml.commons.Constants.MISSING_VARIABLE_INITIALIZER_TEMPLATE;
import static org.kie.pmml.commons.Constants.MISSING_VARIABLE_IN_BODY;
import static org.kie.pmml.compiler.commons.utils.CommonCodegenUtils.getExpressionForObject;
import static org.kie.pmml.compiler.commons.utils.CommonCodegenUtils.getVariableDeclarator;
import static org.kie.pmml.compiler.commons.utils.JavaParserUtils.MAIN_CLASS_NOT_FOUND;

/**
 * Class meant to provide <i>helper</i> method to retrieve <code>KiePMMLNormDiscrete</code> code-generators
 * out of <code>NormDiscrete</code>s
 */
public class KiePMMLNormDiscreteFactory {

    static final String KIE_PMML_NORMDISCRETE_TEMPLATE_JAVA = "KiePMMLNormDiscreteTemplate.tmpl";
    static final String KIE_PMML_NORMDISCRETE_TEMPLATE = "KiePMMLNormDiscreteTemplate";
    static final String GETKIEPMMLNORMDISCRETE = "getKiePMMLNormDiscrete";
    static final String NORM_DISCRETE = "normDiscrete";
    static final ClassOrInterfaceDeclaration NORMDISCRETE_TEMPLATE;

    static {
        CompilationUnit cloneCU = JavaParserUtils.getFromFileName(KIE_PMML_NORMDISCRETE_TEMPLATE_JAVA);
        NORMDISCRETE_TEMPLATE = cloneCU.getClassByName(KIE_PMML_NORMDISCRETE_TEMPLATE)
                .orElseThrow(() -> new KiePMMLException(MAIN_CLASS_NOT_FOUND + ": " + KIE_PMML_NORMDISCRETE_TEMPLATE));
        NORMDISCRETE_TEMPLATE.getMethodsByName(GETKIEPMMLNORMDISCRETE).get(0).clone();
    }

    private KiePMMLNormDiscreteFactory() {
        // Avoid instantiation
    }

    static BlockStmt getNormDiscreteVariableDeclaration(final String variableName,
                                                        final NormDiscrete normDiscrete) {
        final MethodDeclaration methodDeclaration =
                NORMDISCRETE_TEMPLATE.getMethodsByName(GETKIEPMMLNORMDISCRETE).get(0).clone();
        final BlockStmt toReturn =
                methodDeclaration.getBody().orElseThrow(() -> new KiePMMLException(String.format(MISSING_BODY_TEMPLATE, methodDeclaration)));
        final VariableDeclarator variableDeclarator =
                getVariableDeclarator(toReturn, NORM_DISCRETE).orElseThrow(() -> new KiePMMLException(String.format(MISSING_VARIABLE_IN_BODY, NORM_DISCRETE, toReturn)));
        variableDeclarator.setName(variableName);
        final ObjectCreationExpr objectCreationExpr = variableDeclarator.getInitializer()
                .orElseThrow(() -> new KiePMMLException(String.format(MISSING_VARIABLE_INITIALIZER_TEMPLATE,
                                                                      NORM_DISCRETE, toReturn)))
                .asObjectCreationExpr();

        final StringLiteralExpr nameExpr = new StringLiteralExpr(normDiscrete.getField().getValue());

        final Expression mapMissingToExpr = getExpressionForObject(normDiscrete.getMapMissingTo());
        objectCreationExpr.getArguments().set(0, nameExpr);
        objectCreationExpr.getArguments().set(2, new StringLiteralExpr((String)normDiscrete.getValue()));
        objectCreationExpr.getArguments().set(3, mapMissingToExpr);
        return toReturn;
    }

}
