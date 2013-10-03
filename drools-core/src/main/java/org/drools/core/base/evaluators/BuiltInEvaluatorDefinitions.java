package org.drools.core.base.evaluators;

import java.util.ArrayList;
import java.util.List;

public class BuiltInEvaluatorDefinitions {
    
    private final static List<EvaluatorDefinition> EVALUATOR_DEFINITIONS = new ArrayList<EvaluatorDefinition>();
    
    static {
        EVALUATOR_DEFINITIONS.add( new BeforeEvaluatorDefinition() );
        EVALUATOR_DEFINITIONS.add( new AfterEvaluatorDefinition() );
        EVALUATOR_DEFINITIONS.add( new MeetsEvaluatorDefinition() );
        EVALUATOR_DEFINITIONS.add( new MetByEvaluatorDefinition() );
        EVALUATOR_DEFINITIONS.add( new OverlapsEvaluatorDefinition() );
        EVALUATOR_DEFINITIONS.add( new OverlappedByEvaluatorDefinition() );
        EVALUATOR_DEFINITIONS.add( new IncludesEvaluatorDefinition() );
        EVALUATOR_DEFINITIONS.add( new DuringEvaluatorDefinition() );
        EVALUATOR_DEFINITIONS.add( new FinishesEvaluatorDefinition() );
        EVALUATOR_DEFINITIONS.add( new FinishedByEvaluatorDefinition() );
        EVALUATOR_DEFINITIONS.add( new StartsEvaluatorDefinition() );
        EVALUATOR_DEFINITIONS.add( new StartedByEvaluatorDefinition() );
        EVALUATOR_DEFINITIONS.add( new CoincidesEvaluatorDefinition() );
        EVALUATOR_DEFINITIONS.add( new StrEvaluatorDefinition() );
        EVALUATOR_DEFINITIONS.add( new IsAEvaluatorDefinition() );
    }

    public static List<EvaluatorDefinition> getEvaluatorDefinitions() {
        return EVALUATOR_DEFINITIONS;
    }
}
