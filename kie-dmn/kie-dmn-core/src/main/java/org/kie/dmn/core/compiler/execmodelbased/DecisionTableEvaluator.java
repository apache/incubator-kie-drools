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
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.kie.dmn.api.feel.runtime.events.FEELEvent;
import org.kie.dmn.core.compiler.DMNFEELHelper;
import org.kie.dmn.feel.lang.EvaluationContext;
import org.kie.dmn.feel.runtime.decisiontables.HitPolicy;

public class DecisionTableEvaluator {
    private final DTableModel dTableModel;
    private final EvaluationContext evalCtx;
    private final FeelValue[] inputs;
    private final List<FEELEvent> events;
    private final EvaluationContext[] columnEvalCtxs;

    private final List<Integer> indexes = new ArrayList<>();

    public DecisionTableEvaluator( DMNFEELHelper feel, DTableModel dTableModel, EvaluationContext evalCtx, List<FEELEvent> events ) {
        this.dTableModel = dTableModel;
        this.evalCtx = evalCtx;
        this.events = events;
        this.inputs = new FeelValue[dTableModel.getColumns().size()];
        this.columnEvalCtxs = new EvaluationContext[dTableModel.getColumns().size()];
        initInputs(feel);
    }

    public Object getOutput(int row, int col) {
        return dTableModel.getRows().get( row ).evaluate( evalCtx, col );
    }

    private Object[] initInputs(DMNFEELHelper feel) {
        Map<String, Object> allValues = evalCtx.getAllValues();
        for (int i = 0; i < inputs.length; i++) {
            Object result = dTableModel.getColumns().get(i).evaluate( evalCtx );
            inputs[i] = new FeelValue(result);

            columnEvalCtxs[i] = feel.newEvaluationContext( Collections.singletonList( events::add ), allValues);
            columnEvalCtxs[i].enterFrame();
            columnEvalCtxs[i].setValue( "?", result );
        }
        return inputs;
    }

    public FeelValue[] getInputs() {
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

    public EvaluationContext getEvalCtx(int col) {
        return columnEvalCtxs[col];
    }
}
