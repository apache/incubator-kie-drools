package org.kie.pmml.compiler.commons.codegenfactories;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import org.dmg.pmml.True;
import org.kie.pmml.api.exceptions.KiePMMLException;
import org.kie.pmml.compiler.commons.utils.JavaParserUtils;

import static org.kie.pmml.commons.Constants.MISSING_BODY_TEMPLATE;
import static org.kie.pmml.commons.Constants.MISSING_VARIABLE_INITIALIZER_TEMPLATE;
import static org.kie.pmml.commons.Constants.MISSING_VARIABLE_IN_BODY;
import static org.kie.pmml.compiler.commons.utils.CommonCodegenUtils.getVariableDeclarator;
import static org.kie.pmml.compiler.commons.utils.JavaParserUtils.MAIN_CLASS_NOT_FOUND;

/**
 * Class meant to provide <i>helper</i> method to retrieve <code>KiePMMLTruePredicate</code> code-generators
 * out of <code>True</code>s
 */
public class KiePMMLTruePredicateFactory {

    private KiePMMLTruePredicateFactory() {
        // Avoid instantiation
    }

    static final String KIE_PMML_TRUEPREDICATE_TEMPLATE_TEMPLATE_JAVA = "KiePMMLTruePredicateTemplate.tmpl";
    static final String KIE_PMML_TRUEPREDICATE_TEMPLATE = "KiePMMLTruePredicateTemplate";
    static final String GETKIEPMMLTRUEPREDICATE = "getKiePMMLTruePredicate";
    static final String TRUEPREDICATE = "truePredicate";
    static final ClassOrInterfaceDeclaration TRUEPREDICATE_TEMPLATE;


    static {
        CompilationUnit cloneCU = JavaParserUtils.getFromFileName(KIE_PMML_TRUEPREDICATE_TEMPLATE_TEMPLATE_JAVA);
        TRUEPREDICATE_TEMPLATE = cloneCU.getClassByName(KIE_PMML_TRUEPREDICATE_TEMPLATE)
                .orElseThrow(() -> new KiePMMLException(MAIN_CLASS_NOT_FOUND + ": " + KIE_PMML_TRUEPREDICATE_TEMPLATE));
        TRUEPREDICATE_TEMPLATE.getMethodsByName(GETKIEPMMLTRUEPREDICATE).get(0).clone();
    }

    static BlockStmt getTruePredicateVariableDeclaration(final String variableName, final True truePredicate) {
        final MethodDeclaration methodDeclaration = TRUEPREDICATE_TEMPLATE.getMethodsByName(GETKIEPMMLTRUEPREDICATE).get(0).clone();
        final BlockStmt toReturn = methodDeclaration.getBody().orElseThrow(() -> new KiePMMLException(String.format(MISSING_BODY_TEMPLATE, methodDeclaration)));
        final VariableDeclarator variableDeclarator = getVariableDeclarator(toReturn, TRUEPREDICATE) .orElseThrow(() -> new KiePMMLException(String.format(MISSING_VARIABLE_IN_BODY, TRUEPREDICATE, toReturn)));
        variableDeclarator.setName(variableName);
        final ObjectCreationExpr objectCreationExpr = variableDeclarator.getInitializer()
                .orElseThrow(() -> new KiePMMLException(String.format(MISSING_VARIABLE_INITIALIZER_TEMPLATE, TRUEPREDICATE, toReturn)))
        .asObjectCreationExpr();

        final StringLiteralExpr nameExpr = new StringLiteralExpr(variableName);
        objectCreationExpr.getArguments().set(0, nameExpr);
        return toReturn;
    }
}
