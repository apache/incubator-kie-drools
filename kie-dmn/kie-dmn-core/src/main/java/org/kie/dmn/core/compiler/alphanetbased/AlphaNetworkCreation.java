/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

import java.util.UUID;

import org.drools.core.reteoo.AlphaNode;
import org.drools.core.reteoo.ObjectSource;
import org.drools.core.reteoo.builder.BuildUtils;
import org.drools.core.rule.Declaration;
import org.drools.core.spi.Constraint;
import org.drools.model.Index;
import org.drools.model.constraints.SingleConstraint1;
import org.drools.model.functions.Function1;
import org.drools.model.functions.Predicate1;
import org.drools.model.index.AlphaIndexImpl;
import org.drools.modelcompiler.constraints.ConstraintEvaluator;
import org.drools.modelcompiler.constraints.LambdaConstraint;

public class AlphaNetworkCreation {

    private static final BuildUtils buildUtils = new BuildUtils();

    private final NetworkBuilderContext ctx;

    public AlphaNetworkCreation(NetworkBuilderContext ctx) {
        this.ctx = ctx;
    }

    public void addResultSink(ObjectSource source, Object result) {
        source.addObjectSink(new ResultCollectorAlphaSink(getNextId(), source, ctx.buildContext, result, ctx.resultCollector));
    }

    private int getNextId() {
        return ctx.buildContext.getNextId();
    }

    public AlphaNode createAlphaNode(ObjectSource source, String id, Predicate1<TableContext> predicate) {
        return createAlphaNode(source, id, predicate, null);
    }

    @Deprecated
    public AlphaNode createAlphaNode(ObjectSource source, Predicate1<TableContext> predicate, Index index) {
        return createAlphaNode(source, UUID.randomUUID().toString(), predicate, null);
    }

    /**
     * IMPORTANT: remember to use the FEEL expression as an Identifier for the same constraint
     * <p>
     * Prefix: column name + value
     */
    public AlphaNode createAlphaNode(ObjectSource source, String id, Predicate1<TableContext> predicate, Index index) {
        SingleConstraint1<TableContext> constraint = new SingleConstraint1<>(id, ctx.variable, predicate);
        constraint.setIndex(index);
        LambdaConstraint lambda = new LambdaConstraint(new ConstraintEvaluator(new Declaration[]{ctx.declaration}, constraint));
        lambda.setType(Constraint.ConstraintType.ALPHA);
        return buildUtils.attachNode(ctx.buildContext, new AlphaNode(getNextId(), lambda, source, ctx.buildContext));
    }

    public static <I> AlphaIndexImpl<TableContext, I> createIndex(Class<I> indexedClass, Function1<TableContext, I> leftExtractor, I rightValue) {
        return new AlphaIndexImpl<>(indexedClass, Index.ConstraintType.EQUAL, 1, leftExtractor, rightValue);
    }
}
