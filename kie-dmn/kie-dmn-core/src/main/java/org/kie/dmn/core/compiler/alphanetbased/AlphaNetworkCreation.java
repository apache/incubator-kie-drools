/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.dmn.core.compiler.alphanetbased;

import org.drools.core.reteoo.ObjectSource;
import org.drools.core.reteoo.builder.BuildContext;
import org.drools.core.reteoo.builder.BuildUtils;
import org.drools.model.Index;
import org.drools.model.functions.Function1;
import org.drools.model.index.AlphaIndexImpl;

public class AlphaNetworkCreation {

    private static final BuildUtils buildUtils = new BuildUtils();

    private final BuildContext buildContext;

    public AlphaNetworkCreation(BuildContext buildContext) {
        this.buildContext = buildContext;
    }

    public int getNextId() {
        return buildContext.getNextNodeId();
    }

    public <T extends Class<?>> void  addResultSink(ObjectSource source,
                                                    int row,
                                                    String columnName,
                                                    String outputEvaluationClass) {
        DMNResultCollectorAlphaSink objectSink = new DMNResultCollectorAlphaSink(getNextId(),
                                                                                 source,
                                                                                 buildContext,
                                                                                 row,
                                                                                 columnName,
                                                                                 outputEvaluationClass
        );
        source.addObjectSink(objectSink);
    }

    public CanBeInlinedAlphaNode shareAlphaNode(CanBeInlinedAlphaNode candidateAlphaNode) {
        return buildUtils.attachNode(buildContext, candidateAlphaNode);
    }

    public static <I> AlphaIndexImpl<PropertyEvaluator, I> createIndex(Class<I> indexedClass, Function1<PropertyEvaluator, I> leftExtractor, I rightValue) {
        return new AlphaIndexImpl<>(indexedClass, Index.ConstraintType.EQUAL, 1, leftExtractor, rightValue);
    }
}
