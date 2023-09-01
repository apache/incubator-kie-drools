package org.kie.pmml.compiler.commons.codegenfactories;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import org.dmg.pmml.InlineTable;
import org.dmg.pmml.Row;
import org.kie.pmml.api.exceptions.KiePMMLException;
import org.kie.pmml.compiler.commons.utils.JavaParserUtils;

import static org.kie.pmml.commons.Constants.MISSING_BODY_TEMPLATE;
import static org.kie.pmml.commons.Constants.MISSING_VARIABLE_INITIALIZER_TEMPLATE;
import static org.kie.pmml.commons.Constants.MISSING_VARIABLE_IN_BODY;
import static org.kie.pmml.commons.Constants.VARIABLE_NAME_TEMPLATE;
import static org.kie.pmml.compiler.commons.codegenfactories.KiePMMLRowFactory.getRowVariableDeclaration;
import static org.kie.pmml.compiler.commons.utils.CommonCodegenUtils.getVariableDeclarator;
import static org.kie.pmml.compiler.commons.utils.JavaParserUtils.MAIN_CLASS_NOT_FOUND;

/**
 * Class meant to provide <i>helper</i> method to retrieve <code>KiePMMLInlineTable</code> code-generators
 * out of <code>InlineTable</code>s
 */
public class KiePMMLInlineTableFactory {

    private KiePMMLInlineTableFactory() {
        // Avoid instantiation
    }

    static final String KIE_PMML_INLINETABLE_TEMPLATE_JAVA = "KiePMMLInlineTableTemplate.tmpl";
    static final String KIE_PMML_INLINETABLE_TEMPLATE = "KiePMMLInlineTableTemplate";
    static final String GETKIEPMMLINLINETABLE = "getKiePMMLInlineTable";
    static final String INLINETABLE = "inlineTable";
    static final ClassOrInterfaceDeclaration INLINETABLE_TEMPLATE;


    static {
        CompilationUnit cloneCU = JavaParserUtils.getFromFileName(KIE_PMML_INLINETABLE_TEMPLATE_JAVA);
        INLINETABLE_TEMPLATE = cloneCU.getClassByName(KIE_PMML_INLINETABLE_TEMPLATE)
                .orElseThrow(() -> new KiePMMLException(MAIN_CLASS_NOT_FOUND + ": " + KIE_PMML_INLINETABLE_TEMPLATE));
        INLINETABLE_TEMPLATE.getMethodsByName(GETKIEPMMLINLINETABLE).get(0).clone();
    }

    static BlockStmt getInlineTableVariableDeclaration(final String variableName, final InlineTable inlineTable) {
        final MethodDeclaration methodDeclaration = INLINETABLE_TEMPLATE.getMethodsByName(GETKIEPMMLINLINETABLE).get(0).clone();
        final BlockStmt inlineTableBody = methodDeclaration.getBody().orElseThrow(() -> new KiePMMLException(String.format(MISSING_BODY_TEMPLATE, methodDeclaration)));
        final VariableDeclarator variableDeclarator = getVariableDeclarator(inlineTableBody, INLINETABLE) .orElseThrow(() -> new KiePMMLException(String.format(MISSING_VARIABLE_IN_BODY, INLINETABLE, inlineTableBody)));
        variableDeclarator.setName(variableName);
        final BlockStmt toReturn = new BlockStmt();
        int counter = 0;
        final NodeList<Expression> arguments = new NodeList<>();
        for (Row row : inlineTable.getRows()) {
            String nestedVariableName = String.format(VARIABLE_NAME_TEMPLATE, variableName, counter);
            arguments.add(new NameExpr(nestedVariableName));
            BlockStmt toAdd = getRowVariableDeclaration(nestedVariableName, row);
            toAdd.getStatements().forEach(toReturn::addStatement);
            counter ++;
        }
        final ObjectCreationExpr objectCreationExpr = variableDeclarator.getInitializer()
                .orElseThrow(() -> new KiePMMLException(String.format(MISSING_VARIABLE_INITIALIZER_TEMPLATE, INLINETABLE, toReturn)))
                .asObjectCreationExpr();
        objectCreationExpr.getArguments().set(0, new StringLiteralExpr(variableName));
        objectCreationExpr.getArguments().get(2).asMethodCallExpr().setArguments(arguments);
        inlineTableBody.getStatements().forEach(toReturn::addStatement);
        return toReturn;
    }
}
