package org.kie.dmn.core.alphasupport;

import java.util.Collection;
import java.util.List;

import org.kie.dmn.api.core.DMNType;
import org.kie.dmn.api.feel.runtime.events.FEELEvent;
import org.kie.dmn.core.compiler.alphanetbased.evaluator.ColumnValidator;
import org.kie.dmn.feel.lang.EvaluationContext;
import org.kie.dmn.feel.runtime.UnaryTest;
import org.kie.dmn.feel.runtime.events.InvalidInputEvent;

public class ColumnValidatorTemplate extends ColumnValidator {

    @Override
    protected List<UnaryTest> validationInputTests() {
        return ColumnValidatorX.getInstance().getUnaryTests();
    }

    // TODO DT-ANC this should be a string
    @Override
    protected DMNType dmnType() {
        return null;
    }

    @Override
    protected String validValues() {
        return "VALID_VALUES";
    }

    @Override
    protected String columnName() {
        return "COLUMN_NAME";
    }

    @Override
    protected String decisionTableName() {
        return "DECISION_TABLE_NAME";
    }

    private static ColumnValidatorTemplate INSTANCE;

    public static ColumnValidatorTemplate getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ColumnValidatorTemplate();
        }
        return INSTANCE;
    }
}
