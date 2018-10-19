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

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.drools.compiler.compiler.io.memory.MemoryFileSystem;
import org.kie.dmn.api.core.AfterGeneratingSourcesListener;
import org.kie.dmn.core.api.DMNExpressionEvaluator;
import org.kie.dmn.core.ast.DMNBaseNode;
import org.kie.dmn.core.compiler.DMNCompilerContext;
import org.kie.dmn.core.compiler.DMNCompilerImpl;
import org.kie.dmn.core.impl.DMNModelImpl;
import org.kie.dmn.model.api.DRGElement;
import org.kie.dmn.model.api.DecisionTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExecModelDMNDeferredEvaluatorCompiler extends ExecModelDMNEvaluatorCompiler {
    List<AfterGeneratingSourcesListener> afterGeneratingSourcesListeners = new ArrayList<>();

    public void register(AfterGeneratingSourcesListener listener) {
        afterGeneratingSourcesListeners.add(listener);
    }


    static final Logger logger = LoggerFactory.getLogger(ExecModelDMNDeferredEvaluatorCompiler.class);

    public ExecModelDMNDeferredEvaluatorCompiler(DMNCompilerImpl compiler) {
        super(compiler);
    }

    @Override
    protected DMNExpressionEvaluator compileDecisionTable( DMNCompilerContext ctx, DMNModelImpl model, DMNBaseNode node, String dtName, DecisionTable dt ) {
        String decisionName = dt.getParent() instanceof DRGElement ? dtName : ( dt.getId() != null ? dt.getId() :  "_" + UUID.randomUUID().toString() );
        DTableModel dTableModel = new DTableModel(ctx.getFeelHelper(), model, dtName, decisionName, dt);
        AbstractModelEvaluator evaluator = generateEvaluator( ctx, dTableModel );
        if(evaluator != null) {
            evaluator.initParameters(ctx.getFeelHelper(), ctx, dTableModel, node);
        }
        return evaluator;
    }

    public AbstractModelEvaluator generateEvaluator( DMNCompilerContext ctx, DTableModel dTableModel ) {
        String pkgName = dTableModel.getNamespace();
        String clasName = dTableModel.getTableName();

        MemoryFileSystem srcMfs = new MemoryFileSystem();
        MemoryFileSystem trgMfs = new MemoryFileSystem();
        String[] fileNames = new String[GeneratorsEnum.values().length];
        List<AfterGeneratingSourcesListener.GeneratedSource> generatedSources = new ArrayList<>();

        generateSources(ctx, dTableModel, pkgName, clasName, srcMfs, fileNames, generatedSources);

        for(AfterGeneratingSourcesListener listener : afterGeneratingSourcesListeners) {
            listener.accept(generatedSources);
        }

        return null;
    }
}
