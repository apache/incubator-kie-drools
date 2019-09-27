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

package org.kie.dmn.core.compiler.alphanetbased;

import java.util.ArrayList;
import java.util.List;

import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.drools.core.base.ClassObjectType;
import org.drools.core.common.DefaultFactHandle;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.impl.KnowledgeBaseFactory;
import org.drools.core.reteoo.AlphaNode;
import org.drools.core.reteoo.EntryPointNode;
import org.drools.core.reteoo.LeftInputAdapterNode;
import org.drools.core.reteoo.ModifyPreviousTuples;
import org.drools.core.reteoo.ObjectSource;
import org.drools.core.reteoo.ObjectTypeNode;
import org.drools.core.reteoo.builder.BuildContext;
import org.drools.core.reteoo.compiled.CompiledNetwork;
import org.drools.core.rule.Declaration;
import org.drools.core.rule.Pattern;
import org.drools.core.spi.PropagationContext;
import org.drools.model.SingleConstraint;
import org.drools.model.Variable;
import org.drools.model.constraints.SingleConstraint1;
import org.drools.modelcompiler.constraints.ConstraintEvaluator;
import org.drools.modelcompiler.constraints.LambdaConstraint;
import org.kie.dmn.feel.lang.EvaluationContext;

import static org.drools.compiler.reteoo.compiled.ObjectTypeNodeCompiler.compile;
import static org.drools.core.reteoo.builder.BuildUtils.attachNode;
import static org.drools.model.DSL.declarationOf;

public class CompiledAlphaNetwork {

    private final ResultCollector resultCollector = new ResultCollector();

    private CompiledNetwork compiledNetwork;

    public Object evaluate( EvaluationContext evalCtx ) {
        resultCollector.results.clear();
        compiledNetwork.assertObject( new DefaultFactHandle( 1L, evalCtx ), null, null );
        return applyHitPolicy( resultCollector.results );
    }

    private Object applyHitPolicy( List<Object> results ) {
        return results.get(0);
    }

    public static CompiledAlphaNetwork generateCompiledNetwork() {
        CompiledAlphaNetwork network = new CompiledAlphaNetwork();

        InternalKnowledgeBase kBase = (InternalKnowledgeBase) KnowledgeBaseFactory.newKnowledgeBase();
        BuildContext ctx = new BuildContext(kBase);
        EntryPointNode entryPoint = ctx.getKnowledgeBase().getRete().getEntryPointNodes().values().iterator().next();
        ClassObjectType objectType = new ClassObjectType( EvaluationContext.class );
        Variable<EvaluationContext> ctxVar = declarationOf( EvaluationContext.class, "$ctx" );

        ObjectTypeNode otn = new ObjectTypeNode( ctx.getNextId(), entryPoint, objectType, ctx );
        ctx.setObjectSource( otn );

        SingleConstraint constraint = new SingleConstraint1(ctxVar, x -> {
            System.out.println(x);
            return true;
        });

        Pattern pattern = new Pattern( 1, objectType, "$ctx" );
        Declaration declaration = pattern.getDeclaration();

        LambdaConstraint c1 = new LambdaConstraint(new ConstraintEvaluator(new Declaration[] { declaration }, constraint));
        AlphaNode alpha1 = attachNode( ctx, new AlphaNode( ctx.getNextId(), c1, otn, ctx ) );
        alpha1.addObjectSink( new ResultCollectorAlphaSink( ctx.getNextId(), alpha1, ctx, "Approved", network.resultCollector ) );

        LambdaConstraint c2 = new LambdaConstraint(new ConstraintEvaluator(new Declaration[0], SingleConstraint.FALSE));
        AlphaNode alpha2 = attachNode( ctx, new AlphaNode( ctx.getNextId(), c2, otn, ctx ) );
        alpha2.addObjectSink( new ResultCollectorAlphaSink( ctx.getNextId(), alpha2, ctx, "Declined", network.resultCollector ) );

        network.compiledNetwork = compile(new KnowledgeBuilderImpl(kBase), otn);
        network.compiledNetwork.setObjectTypeNode(otn);
        return network;
    }

    static class ResultCollector {
        final List<Object> results = new ArrayList<>();
    }

    public static class ResultCollectorAlphaSink extends LeftInputAdapterNode {

        private final Object result;
        private final ResultCollector resultCollector;

        public ResultCollectorAlphaSink( int id, ObjectSource source, BuildContext context, Object result, ResultCollector resultCollector ) {
            super( id, source, context );
            this.result = result;
            this.resultCollector = resultCollector;
        }

        @Override
        public void assertObject( InternalFactHandle factHandle, PropagationContext propagationContext, InternalWorkingMemory workingMemory ) {
            resultCollector.results.add(result);
        }

        @Override
        public void modifyObject( InternalFactHandle factHandle, ModifyPreviousTuples modifyPreviousTuples, PropagationContext context, InternalWorkingMemory workingMemory ) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void byPassModifyToBetaNode( InternalFactHandle factHandle, ModifyPreviousTuples modifyPreviousTuples, PropagationContext context, InternalWorkingMemory workingMemory ) {
            throw new UnsupportedOperationException();
        }
    }
}
