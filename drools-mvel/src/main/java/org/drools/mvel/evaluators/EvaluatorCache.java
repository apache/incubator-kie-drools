package org.drools.mvel.evaluators;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashMap;
import java.util.Map;

import org.drools.base.base.ValueType;
import org.drools.drl.parser.impl.Operator;
import org.drools.base.rule.accessor.Evaluator;

/**
 * A simple helper class to store Evaluators for a given set of
 * value types and operators
 */
public class EvaluatorCache implements Externalizable {

    private static final long serialVersionUID = 510l;
    private Map<ValueType, Map<Operator, Evaluator>> evaluators = new HashMap<>();

    public EvaluatorCache() {
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        Map<ValueType, Map<Operator, Evaluator>>    temp = (Map<ValueType, Map<Operator, Evaluator>>)in.readObject();

        for (Map.Entry<ValueType, Map<Operator, Evaluator>> entry : temp.entrySet()) {
            evaluators.put(ValueType.determineValueType(entry.getKey().getClassType()),
                           entry.getValue());
        }
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(evaluators);
    }

    public void addEvaluator( final ValueType type, final Operator operator, final Evaluator evaluator ) {
        Map<Operator, Evaluator> opEvalMap = this.evaluators.get( type );
        if( opEvalMap == null ) {
            opEvalMap = new HashMap<>();
            this.evaluators.put( type, opEvalMap );
        }
        opEvalMap.put( operator, evaluator );
    }

    public Evaluator getEvaluator( final ValueType type, final Operator operator ) {
        Map<Operator, Evaluator> opEvalMap = this.evaluators.get( type );
        return opEvalMap != null ? opEvalMap.get( operator ) : null;
    }

    public boolean supportsType(ValueType type) {
        return this.evaluators.containsKey( type );
    }

}
