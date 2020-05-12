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

package org.optaplanner.core.impl.score.stream.drools.bi;

import java.util.function.BiFunction;

import org.optaplanner.core.api.score.stream.bi.BiConstraintCollector;
import org.optaplanner.core.impl.score.stream.drools.common.BiTuple;
import org.optaplanner.core.impl.score.stream.drools.common.DroolsAbstractGroupBy;
import org.optaplanner.core.impl.score.stream.drools.common.GroupByCollectorProcessor;
import org.optaplanner.core.impl.score.stream.drools.common.TriTuple;

final class DroolsBiToTriGroupBy<A, B, NewA, NewB, NewC>
        extends DroolsAbstractGroupBy<BiTuple<A, B>, TriTuple<NewA, NewB, NewC>> {

    private final BiFunction<A, B, NewA> groupKeyAMapping;
    private final BiFunction<A, B, NewB> groupKeyBMapping;
    private final BiConstraintCollector<A, B, ?, NewC> collector;

    public DroolsBiToTriGroupBy(BiFunction<A, B, NewA> groupKeyAMapping, BiFunction<A, B, NewB> groupKeyBMapping,
            BiConstraintCollector<A, B, ?, NewC> collector) {
        this.groupKeyAMapping = groupKeyAMapping;
        this.groupKeyBMapping = groupKeyBMapping;
        this.collector = collector;
    }

    @Override
    protected GroupByCollectorProcessor<BiTuple<A, B>, TriTuple<NewA, NewB, NewC>> newAccumulator() {
        return new DroolsBiToTriGroupByCollectorProcessor<>(groupKeyAMapping, groupKeyBMapping, collector);
    }

}
