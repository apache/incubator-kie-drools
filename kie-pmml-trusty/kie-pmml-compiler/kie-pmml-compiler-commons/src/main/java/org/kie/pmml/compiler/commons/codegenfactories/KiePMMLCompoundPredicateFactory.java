package org.kie.pmml.compiler.commons.codegenfactories;

import java.util.List;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import org.dmg.pmml.CompoundPredicate;
import org.dmg.pmml.Field;
import org.dmg.pmml.Predicate;
import org.kie.pmml.api.enums.BOOLEAN_OPERATOR;
import org.kie.pmml.api.exceptions.KiePMMLException;
import org.kie.pmml.compiler.commons.utils.JavaParserUtils;

import static org.kie.pmml.commons.Constants.MISSING_BODY_TEMPLATE;
import static org.kie.pmml.commons.Constants.MISSING_VARIABLE_INITIALIZER_TEMPLATE;
import static org.kie.pmml.commons.Constants.MISSING_VARIABLE_IN_BODY;
import static org.kie.pmml.commons.Constants.VARIABLE_NAME_TEMPLATE;
import static org.kie.pmml.compiler.commons.codegenfactories.KiePMMLPredicateFactory.getKiePMMLPredicate;
import static org.kie.pmml.compiler.commons.utils.CommonCodegenUtils.getChainedMethodCallExprFrom;
import static org.kie.pmml.compiler.commons.utils.CommonCodegenUtils.getVariableDeclarator;
import static org.kie.pmml.compiler.commons.utils.JavaParserUtils.MAIN_CLASS_NOT_FOUND;

/**
 * Class meant to provide <i>helper</i> method to retrieve <code>KiePMMLCompoundPredicate</code> code-generators
 * out of <code>CompoundPredicate</code>s
 */
public class KiePMMLCompoundPredicateFactory {

    static final String KIE_PMML_COMPOUND_PREDICATE_TEMPLATE_JAVA = "KiePMMLCompoundPredicateTemplate.tmpl";
    static final String KIE_PMML_COMPOUND_PREDICATE_TEMPLATE = "KiePMMLCompoundPredicateTemplate";
    static final String GETKIEPMMLCOMPOUNDPREDICATE = "getKiePMMLCompoundPredicate";
    static final String COMPOUND_PREDICATE = "compoundPredicate";
    static final ClassOrInterfaceDeclaration COMPOUND_PREDICATE_TEMPLATE;

    static {
        CompilationUnit cloneCU = JavaParserUtils.getFromFileName(KIE_PMML_COMPOUND_PREDICATE_TEMPLATE_JAVA);
        COMPOUND_PREDICATE_TEMPLATE = cloneCU.getClassByName(KIE_PMML_COMPOUND_PREDICATE_TEMPLATE)
                .orElseThrow(() -> new KiePMMLException(MAIN_CLASS_NOT_FOUND + ": " + KIE_PMML_COMPOUND_PREDICATE_TEMPLATE));
        COMPOUND_PREDICATE_TEMPLATE.getMethodsByName(GETKIEPMMLCOMPOUNDPREDICATE).get(0).clone();
    }

    private KiePMMLCompoundPredicateFactory() {
        // Avoid instantiation
    }

    static BlockStmt getCompoundPredicateVariableDeclaration(final String variableName,
                                                             final CompoundPredicate compoundPredicate,
                                                             final List<Field<?>> fields) {
        final MethodDeclaration methodDeclaration =
                COMPOUND_PREDICATE_TEMPLATE.getMethodsByName(GETKIEPMMLCOMPOUNDPREDICATE).get(0).clone();
        final BlockStmt compoundPredicateBody =
                methodDeclaration.getBody().orElseThrow(() -> new KiePMMLException(String.format(MISSING_BODY_TEMPLATE, methodDeclaration)));
        final VariableDeclarator variableDeclarator =
                getVariableDeclarator(compoundPredicateBody, COMPOUND_PREDICATE).orElseThrow(() -> new KiePMMLException(String.format(MISSING_VARIABLE_IN_BODY, COMPOUND_PREDICATE, compoundPredicateBody)));
        variableDeclarator.setName(variableName);
        final BlockStmt toReturn = new BlockStmt();
        int counter = 0;
        final NodeList<Expression> arguments = new NodeList<>();
        for (Predicate predicate : compoundPredicate.getPredicates()) {
            String nestedVariableName = String.format(VARIABLE_NAME_TEMPLATE, variableName, counter);
            arguments.add(new NameExpr(nestedVariableName));
            BlockStmt toAdd = getKiePMMLPredicate(nestedVariableName, predicate, fields);
            toAdd.getStatements().forEach(toReturn::addStatement);
            counter ++;
        }
        final BOOLEAN_OPERATOR booleanOperator = BOOLEAN_OPERATOR.byName(compoundPredicate.getBooleanOperator().value());
        final NameExpr booleanOperatorExpr = new NameExpr(BOOLEAN_OPERATOR.class.getName() + "." + booleanOperator.name());
        final MethodCallExpr initializer = variableDeclarator.getInitializer()
                .orElseThrow(() -> new KiePMMLException(String.format(MISSING_VARIABLE_INITIALIZER_TEMPLATE, COMPOUND_PREDICATE, compoundPredicateBody)))
                .asMethodCallExpr();
        final MethodCallExpr builder = getChainedMethodCallExprFrom("builder", initializer);
        builder.setArgument(1, booleanOperatorExpr);
        getChainedMethodCallExprFrom("asList", initializer).setArguments(arguments);
        compoundPredicateBody.getStatements().forEach(toReturn::addStatement);
        return toReturn;
    }
}
