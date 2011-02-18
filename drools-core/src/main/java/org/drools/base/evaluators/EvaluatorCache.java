/*
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.base.evaluators;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashMap;
import java.util.Map;

import org.drools.base.ValueType;
import org.drools.spi.Evaluator;

/**
 * A simple helper class to store Evaluators for a given set of
 * value types and operators
 */
public class EvaluatorCache implements Externalizable {

    private static final long serialVersionUID = 510l;
    private Map<ValueType, Map<Operator, Evaluator>> evaluators = new HashMap<ValueType, Map<Operator, Evaluator>>();

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
            opEvalMap = new HashMap<Operator, Evaluator>();
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
