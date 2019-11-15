package org.kie.dmn.core.compiler.execmodelbased;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.kie.dmn.api.core.DMNDecisionResult;
import org.kie.dmn.api.core.DMNDecisionResult.DecisionEvaluationStatus;
import org.kie.dmn.api.feel.runtime.events.FEELEvent;
import org.kie.dmn.core.impl.DMNDecisionResultImpl;
import org.kie.dmn.feel.lang.EvaluationContext;
import org.kie.dmn.feel.runtime.decisiontables.DecisionTable;
import org.kie.dmn.feel.runtime.decisiontables.HitPolicy;
import org.kie.dmn.feel.runtime.decisiontables.Indexed;
import org.kie.dmn.feel.runtime.events.DecisionTableRulesMatchedEvent;
import org.kie.dmn.feel.runtime.events.HitPolicyViolationEvent;
import org.drools.ruleunit.RuleUnit;
import org.drools.ruleunit.RuleUnitExecutor;

import static java.util.stream.Collectors.toList;

import static org.kie.dmn.feel.runtime.decisiontables.DecisionTableImpl.checkResults;

public abstract class DMNUnit implements RuleUnit {

    private EvaluationContext evalCtx;

    private DecisionTable decisionTable;

    protected Object result;

    private DecisionTableEvaluator evaluator;

    private List<FEELEvent> events;

    public Object getResult() {
        return result;
    }

    DMNDecisionResult execute( String decisionId, RuleUnitExecutor executor) {
        executor.run(this);
        return new DMNDecisionResultImpl(decisionId, decisionTable.getName(), DecisionEvaluationStatus.SUCCEEDED, getResult(), Collections.emptyList());
    }

    protected FeelValue getValue( int pos ) {
        return evaluator.getInputs()[pos];
    }

    DMNUnit setEvalCtx( EvaluationContext evalCtx ) {
        this.evalCtx = evalCtx;
        return this;
    }

    DMNUnit setDecisionTable( DecisionTable decisionTable ) {
        this.decisionTable = decisionTable;
        return this;
    }

    DMNUnit setDecisionTableEvaluator( DecisionTableEvaluator evaluator ) {
        this.evaluator = evaluator;
        return this;
    }

    DMNUnit setEvents( List<FEELEvent> events ) {
        this.events = events;
        return this;
    }

    public DecisionTableEvaluator getEvaluator() {
        return evaluator;
    }

    protected Object applyHitPolicy( List<Object>... results) {
        HitPolicy hitPolicy = evaluator.getHitPolicy();
        if (evaluator.getIndexes().isEmpty()) {
            if (evaluator.hasDefaultValues()) {
                return evaluator.defaultToOutput( evalCtx );
            }
            if( hitPolicy.getDefaultValue() != null ) {
                return hitPolicy.getDefaultValue();
            }
            events.add( new HitPolicyViolationEvent(
                    FEELEvent.Severity.WARN,
                    "No rule matched for decision table '" + decisionTable.getName() + "' and no default values were defined. Setting result to null.",
                    decisionTable.getName(),
                    Collections.EMPTY_LIST ) );
        }

        List<? extends Indexed> matches = evaluator.getIndexes().stream().map( i -> (Indexed ) () -> i ).collect( toList() );
        evalCtx.notifyEvt( () -> {
                    List<Integer> matchedIndexes = matches.stream().map( dr -> dr.getIndex() + 1 ).collect( Collectors.toList() );
                    return new DecisionTableRulesMatchedEvent(FEELEvent.Severity.INFO,
                            "Rules matched for decision table '" + decisionTable.getName() + "': " + matches.toString(),
                            decisionTable.getName(),
                            decisionTable.getName(),
                            matchedIndexes );
                }
        );


        List<Object> combinedResults = results.length == 1 ? results[0] : combineResults( results );

        Map<Integer, String> msgs = checkResults( decisionTable.getOutputs(), evalCtx, matches, combinedResults );
        if( !msgs.isEmpty() ) {
            List<Integer> offending = msgs.keySet().stream().collect( Collectors.toList());
            events.add( new HitPolicyViolationEvent(
                    FEELEvent.Severity.ERROR,
                    "Errors found evaluating decision table '" + decisionTable.getName() + "': \n"+(msgs.values().stream().collect( Collectors.joining( "\n" ) )),
                    decisionTable.getName(),
                    offending ) );
            return null;
        }

        return hitPolicy.getDti().dti( evalCtx, decisionTable, matches, combinedResults );
    }

    private List<Object> combineResults( List<Object>[] results ) {
        int resultSize = results[0].size();
        List<Object> resultsAsMap = new ArrayList<>();
        for (int i = 0; i < resultSize; i++) {
            Map<String, Object> map = new HashMap<>();
            for (int j = 0; j < results.length; j++) {
                map.put( decisionTable.getOutputs().get(j).getName(), results[j].get(i) );
            }
            resultsAsMap.add(map);
        }
        return resultsAsMap;
    }
}
