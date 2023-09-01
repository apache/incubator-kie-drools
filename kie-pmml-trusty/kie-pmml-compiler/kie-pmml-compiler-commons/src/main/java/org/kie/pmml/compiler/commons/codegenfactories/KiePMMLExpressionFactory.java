package org.kie.pmml.compiler.commons.codegenfactories;

import com.github.javaparser.ast.stmt.BlockStmt;
import org.dmg.pmml.Apply;
import org.dmg.pmml.Constant;
import org.dmg.pmml.Discretize;
import org.dmg.pmml.FieldRef;
import org.dmg.pmml.MapValues;
import org.dmg.pmml.NormContinuous;
import org.dmg.pmml.NormDiscrete;
import org.dmg.pmml.TextIndex;

import static org.kie.pmml.commons.Constants.EXPRESSION_NOT_MANAGED;
import static org.kie.pmml.compiler.commons.codegenfactories.KiePMMLApplyFactory.getApplyVariableDeclaration;
import static org.kie.pmml.compiler.commons.codegenfactories.KiePMMLConstantFactory.getConstantVariableDeclaration;
import static org.kie.pmml.compiler.commons.codegenfactories.KiePMMLDiscretizeFactory.getDiscretizeVariableDeclaration;
import static org.kie.pmml.compiler.commons.codegenfactories.KiePMMLFieldRefFactory.getFieldRefVariableDeclaration;
import static org.kie.pmml.compiler.commons.codegenfactories.KiePMMLMapValuesFactory.getMapValuesVariableDeclaration;
import static org.kie.pmml.compiler.commons.codegenfactories.KiePMMLNormContinuousFactory.getNormContinuousVariableDeclaration;
import static org.kie.pmml.compiler.commons.codegenfactories.KiePMMLNormDiscreteFactory.getNormDiscreteVariableDeclaration;
import static org.kie.pmml.compiler.commons.codegenfactories.KiePMMLTextIndexFactory.getTextIndexVariableDeclaration;

/**
 * Facade for actual implementations
 */
public class KiePMMLExpressionFactory {

    private KiePMMLExpressionFactory() {
        // Avoid instantiation
    }

    // Source code generation

    public static BlockStmt getKiePMMLExpressionBlockStmt(final String variableName,
                                                          final org.dmg.pmml.Expression expression) {
        if (expression instanceof Apply) {
            return getApplyVariableDeclaration(variableName, (Apply) expression);
        } else if (expression instanceof Constant) {
            return getConstantVariableDeclaration(variableName, (Constant) expression);
        } else if (expression instanceof Discretize) {
            return getDiscretizeVariableDeclaration(variableName, (Discretize) expression);
        } else if (expression instanceof FieldRef) {
            return getFieldRefVariableDeclaration(variableName, (FieldRef) expression);
        } else if (expression instanceof MapValues) {
            return getMapValuesVariableDeclaration(variableName, (MapValues) expression);
        } else if (expression instanceof NormContinuous) {
            return getNormContinuousVariableDeclaration(variableName, (NormContinuous) expression);
        } else if (expression instanceof NormDiscrete) {
            return getNormDiscreteVariableDeclaration(variableName, (NormDiscrete) expression);
        } else if (expression instanceof TextIndex) {
            return getTextIndexVariableDeclaration(variableName, (TextIndex) expression);
        } else {
            throw new IllegalArgumentException(String.format(EXPRESSION_NOT_MANAGED, expression.getClass()));
        }
    }

}
