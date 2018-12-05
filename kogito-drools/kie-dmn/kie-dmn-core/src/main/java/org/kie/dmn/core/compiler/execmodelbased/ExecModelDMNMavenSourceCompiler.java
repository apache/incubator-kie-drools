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

import org.drools.compiler.compiler.io.memory.MemoryFileSystem;
import org.kie.dmn.api.core.AfterGeneratingSourcesListener;
import org.kie.dmn.api.core.GeneratedSource;
import org.kie.dmn.core.api.DMNExpressionEvaluator;
import org.kie.dmn.core.ast.DMNBaseNode;
import org.kie.dmn.core.compiler.DMNCompilerContext;
import org.kie.dmn.core.compiler.DMNCompilerImpl;
import org.kie.dmn.core.impl.DMNModelImpl;
import org.kie.dmn.model.api.DecisionTable;

public class ExecModelDMNMavenSourceCompiler extends ExecModelDMNEvaluatorCompiler {
    private GeneratorsEnum[] ALL_GENERATORS = new GeneratorsEnum[] {
            GeneratorsEnum.EVALUATOR,
            GeneratorsEnum.UNIT,
            GeneratorsEnum.EXEC_MODEL,
            GeneratorsEnum.UNARY_TESTS,
            GeneratorsEnum.FEEL_EXPRESSION,

    };

    List<AfterGeneratingSourcesListener> afterGeneratingSourcesListeners = new ArrayList<>();

    public void register(AfterGeneratingSourcesListener listener) {
        afterGeneratingSourcesListeners.add(listener);
    }

    public ExecModelDMNMavenSourceCompiler(DMNCompilerImpl compiler) {
        super(compiler);
    }

    @Override
    protected DMNExpressionEvaluator compileDecisionTable( DMNCompilerContext ctx, DMNModelImpl model, DMNBaseNode node, String dtName, DecisionTable dt ) {
        String decisionName = getDecisionTableName(dtName, dt);
        DTableModel dTableModel = new DTableModel(ctx.getFeelHelper(), model, dtName, decisionName, dt);
        generateEvaluator( ctx, dTableModel );
        return null;
    }

    public AbstractModelEvaluator generateEvaluator( DMNCompilerContext ctx, DTableModel dTableModel ) {

        MemoryFileSystem srcMfs = new MemoryFileSystem();
        String[] fileNames = new String[GeneratorsEnum.values().length];
        List<GeneratedSource> generatedSources = new ArrayList<>();

        generateSources(ctx, dTableModel, srcMfs, fileNames, generatedSources);

        for(AfterGeneratingSourcesListener listener : afterGeneratingSourcesListeners) {
            listener.accept(generatedSources);
        }

        return null;
    }

    @Override
    protected GeneratorsEnum[] getGenerators() {
        return ALL_GENERATORS;
    }
}
