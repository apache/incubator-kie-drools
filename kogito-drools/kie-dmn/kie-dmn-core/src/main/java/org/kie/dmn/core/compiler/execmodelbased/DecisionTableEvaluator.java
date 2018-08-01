/*
 * Copyright 2005 JBoss Inc
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

package org.kie.dmn.core.compiler.execmodelbased;

import java.util.ArrayList;
import java.util.List;

import org.kie.dmn.feel.lang.EvaluationContext;
import org.kie.dmn.feel.runtime.decisiontables.HitPolicy;
import org.kie.dmn.feel.util.Null;

import static org.kie.dmn.feel.util.EvalHelper.coerceNumber;

public class DecisionTableEvaluator {
    private final DTableModel dTableModel;
    private final EvaluationContext evalCtx;
    private final Object[] inputs;

    private final List<Integer> indexes = new ArrayList<>();

    public DecisionTableEvaluator( DTableModel dTableModel, EvaluationContext evalCtx ) {
        this.dTableModel = dTableModel;
        this.evalCtx = evalCtx;
        this.inputs = resolveActualInputs();
    }

    public Object getOutput(int row, int col) {
        return dTableModel.getRows().get( row ).evaluate( evalCtx, col );
    }

    private Object[] resolveActualInputs() {
        Object[] inputs = new Object[dTableModel.getColumns().size()];
        for (int i = 0; i < inputs.length; i++) {
            Object result = dTableModel.getColumns().get(i).evaluate( evalCtx );
            // Manually coerce number since the compiled expression doesn't do this
            inputs[i] = result == null ? Null.INSTANCE : coerceNumber(result);
        }
        return inputs;
    }

    public Object[] getInputs() {
        return inputs;
    }

    public List<Integer> getIndexes() {
        return indexes;
    }

    public void registerFire(int row) {
        indexes.add(row);
    }

    public boolean hasDefaultValues() {
        return dTableModel.hasDefaultValues();
    }

    public Object defaultToOutput( EvaluationContext ctx) {
        return dTableModel.defaultToOutput( ctx );
    }

    public HitPolicy getHitPolicy() {
        return dTableModel.getHitPolicy();
    }

    public EvaluationContext getEvalCtx() {
        return evalCtx;
    }
}
