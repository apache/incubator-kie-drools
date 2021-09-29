package org.kie.dmn.core.compiler.alphanetbased.evaluator;

import java.util.ArrayList;
import java.util.List;

import org.kie.dmn.core.impl.BaseDMNTypeImpl;
import org.kie.dmn.core.impl.DMNModelImpl;
import org.kie.dmn.model.api.DecisionTable;
import org.kie.dmn.model.api.OutputClause;

import static org.kie.dmn.core.compiler.DMNEvaluatorCompiler.inferTypeRef;

public class OutputClausesWithType {

    private final DMNModelImpl dmnModel;
    private final DecisionTable decisionTable;

    public OutputClausesWithType(DMNModelImpl dmnModel, DecisionTable decisionTable) {
        this.dmnModel = dmnModel;
        this.decisionTable = decisionTable;
    }

    public List<OutputClauseWithType> inferTypeForOutputClauses(List<OutputClause> outputClauses) {
        List<OutputClauseWithType> outputClausesWithTypes = new ArrayList<>();
        for (OutputClause outputClause : outputClauses) {
            BaseDMNTypeImpl typeRef = inferTypeRef(dmnModel, decisionTable, outputClause);
            outputClausesWithTypes.add(new OutputClauseWithType(outputClause, typeRef));
        }
        return outputClausesWithTypes;
    }

    public static class OutputClauseWithType {

        private final OutputClause outputClause;
        private final BaseDMNTypeImpl dmnBaseType;

        public OutputClauseWithType(OutputClause outputClause, BaseDMNTypeImpl dmnBaseType) {
            this.outputClause = outputClause;
            this.dmnBaseType = dmnBaseType;
        }

        public OutputClause getOutputClause() {
            return outputClause;
        }

        public BaseDMNTypeImpl getDmnBaseType() {
            return dmnBaseType;
        }
    }
}
