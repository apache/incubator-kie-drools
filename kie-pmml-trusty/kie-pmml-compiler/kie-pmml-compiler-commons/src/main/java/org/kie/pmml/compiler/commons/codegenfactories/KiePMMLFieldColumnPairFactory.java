package org.kie.pmml.compiler.commons.codegenfactories;


import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import org.dmg.pmml.FieldColumnPair;
import org.kie.pmml.api.exceptions.KiePMMLException;
import org.kie.pmml.compiler.commons.utils.JavaParserUtils;

import static org.kie.pmml.commons.Constants.MISSING_BODY_TEMPLATE;
import static org.kie.pmml.commons.Constants.MISSING_VARIABLE_INITIALIZER_TEMPLATE;
import static org.kie.pmml.commons.Constants.MISSING_VARIABLE_IN_BODY;
import static org.kie.pmml.compiler.commons.utils.CommonCodegenUtils.getVariableDeclarator;
import static org.kie.pmml.compiler.commons.utils.JavaParserUtils.MAIN_CLASS_NOT_FOUND;

/**
 * Class meant to provide <i>helper</i> method to retrieve <code>KiePMMLFieldColumnPair</code> code-generators
 * out of <code>FieldColumnPair</code>s
 */
public class KiePMMLFieldColumnPairFactory {

    private KiePMMLFieldColumnPairFactory() {
        // Avoid instantiation
    }

    static final String KIE_PMML_FIELDCOLUMNPAIR_TEMPLATE_JAVA = "KiePMMLFieldColumnPairTemplate.tmpl";
    static final String KIE_PMML_FIELDCOLUMNPAIR_TEMPLATE = "KiePMMLFieldColumnPairTemplate";
    static final String GETKIEPMMLFIELDCOLUMNPAIR = "getKiePMMLFieldColumnPair";
    static final String FIELDCOLUMNPAIR = "fieldColumnPair";
    static final ClassOrInterfaceDeclaration FIELDCOLUMNPAIR_TEMPLATE;


    static {
        CompilationUnit cloneCU = JavaParserUtils.getFromFileName(KIE_PMML_FIELDCOLUMNPAIR_TEMPLATE_JAVA);
        FIELDCOLUMNPAIR_TEMPLATE = cloneCU.getClassByName(KIE_PMML_FIELDCOLUMNPAIR_TEMPLATE)
                .orElseThrow(() -> new KiePMMLException(MAIN_CLASS_NOT_FOUND + ": " + KIE_PMML_FIELDCOLUMNPAIR_TEMPLATE));
        FIELDCOLUMNPAIR_TEMPLATE.getMethodsByName(GETKIEPMMLFIELDCOLUMNPAIR).get(0).clone();
    }

    static BlockStmt getFieldColumnPairVariableDeclaration(final String variableName, final FieldColumnPair fieldColumnPair) {
        final MethodDeclaration methodDeclaration = FIELDCOLUMNPAIR_TEMPLATE.getMethodsByName(GETKIEPMMLFIELDCOLUMNPAIR).get(0).clone();
        final BlockStmt toReturn = methodDeclaration.getBody().orElseThrow(() -> new KiePMMLException(String.format(MISSING_BODY_TEMPLATE, methodDeclaration)));


        final VariableDeclarator variableDeclarator = getVariableDeclarator(toReturn, FIELDCOLUMNPAIR) .orElseThrow(() -> new KiePMMLException(String.format(MISSING_VARIABLE_IN_BODY, FIELDCOLUMNPAIR, toReturn)));
        variableDeclarator.setName(variableName);
        final ObjectCreationExpr objectCreationExpr = variableDeclarator.getInitializer()
                .orElseThrow(() -> new KiePMMLException(String.format(MISSING_VARIABLE_INITIALIZER_TEMPLATE, FIELDCOLUMNPAIR, toReturn)))
        .asObjectCreationExpr();
        objectCreationExpr.getArguments().set(0, new StringLiteralExpr(fieldColumnPair.getField().getValue()));
        objectCreationExpr.getArguments().set(2, new StringLiteralExpr(fieldColumnPair.getColumn()));
        return toReturn;
    }


}
