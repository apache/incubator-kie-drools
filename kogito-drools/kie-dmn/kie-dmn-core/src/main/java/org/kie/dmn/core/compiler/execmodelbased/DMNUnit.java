package org.kie.dmn.core.compiler.execmodelbased;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.kie.api.runtime.rule.RuleUnit;
import org.kie.api.runtime.rule.RuleUnitExecutor;
import org.kie.dmn.api.core.DMNDecisionResult;
import org.kie.dmn.api.core.DMNDecisionResult.DecisionEvaluationStatus;
import org.kie.dmn.api.feel.runtime.events.FEELEvent;
import org.kie.dmn.core.impl.DMNDecisionResultImpl;
import org.kie.dmn.feel.lang.EvaluationContext;
import org.kie.dmn.feel.runtime.decisiontables.DecisionTable;
import org.kie.dmn.feel.runtime.decisiontables.HitPolicy;
import org.kie.dmn.feel.runtime.decisiontables.Indexed;
import org.kie.dmn.feel.runtime.events.HitPolicyViolationEvent;

import static java.util.stream.Collectors.toList;

public abstract class DMNUnit implements RuleUnit {

    private Object[] inputs;
    private EvaluationContext evalCtx;

    private HitPolicy hitPolicy;
    private DecisionTable decisionTable;

    protected Object result;

    private List<Integer> indexes = new ArrayList<>();

    private List<FEELEvent> events;

    public List<Integer> getIndexes() {
        return indexes;
    }

    // TODO
    public Set<String> getRequirements() {
        return Collections.emptySet();
    }

    public Object getResult() {
        return result;
    }

    public DMNDecisionResult execute( RuleUnitExecutor executor) {
        executor.run(this);
        DMNDecisionResultImpl result = new DMNDecisionResultImpl("x", "x", DecisionEvaluationStatus.SUCCEEDED, getResult(), Collections.emptyList());
        return result;
    }

    protected Object getValue( int pos ) {
        return inputs[pos];
    }

    public DMNUnit setInputs( Object[] inputs ) {
        this.inputs = inputs;
        return this;
    }

    public DMNUnit setEvalCtx( EvaluationContext evalCtx ) {
        this.evalCtx = evalCtx;
        return this;
    }

    public DMNUnit setHitPolicy( HitPolicy hitPolicy ) {
        this.hitPolicy = hitPolicy;
        return this;
    }

    public DMNUnit setDecisionTable( DecisionTable decisionTable ) {
        this.decisionTable = decisionTable;
        return this;
    }

    protected Object applyHitPolicy(List<Object>... results) {
        if (indexes.isEmpty()) {
            if( hitPolicy.getDefaultValue() != null ) {
                return hitPolicy.getDefaultValue();
            }
            events = new ArrayList<>();
            events.add( new HitPolicyViolationEvent(
                    FEELEvent.Severity.WARN,
                    "No rule matched for decision table '" + decisionTable.getName() + "' and no default values were defined. Setting result to null.",
                    decisionTable.getName(),
                    Collections.EMPTY_LIST ) );
        }

        List<? extends Indexed> matches = indexes.stream().map( i -> (Indexed ) () -> i ).collect( toList() );
        if (results.length == 1) {
            return hitPolicy.getDti().dti( evalCtx, decisionTable, matches, results[0] );
        }

        int resultSize = results[0].size();
        List<Object> resultsAsMap = new ArrayList<>();
        for (int i = 0; i < resultSize; i++) {
            Map<String, Object> map = new HashMap<>();
            for (int j = 0; j < results.length; j++) {
                map.put( decisionTable.getOutputs().get(j).getName(), results[j].get(i) );
            }
            resultsAsMap.add(map);
        }
        return hitPolicy.getDti().dti( evalCtx, decisionTable, matches, resultsAsMap );
    }

    public List<FEELEvent> getEvents() {
        return events == null ? Collections.emptyList() : events;
    }
}
