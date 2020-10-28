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

package org.optaplanner.core.impl.score.stream.drools.common.rules;

import static org.drools.model.DSL.groupBy;
import static org.drools.modelcompiler.dsl.flow.D.from;

import java.util.function.Function;

import org.drools.model.Variable;
import org.drools.model.view.ViewItem;

final class UniGroupBy1Map0CollectFastMutator<A, NewA> extends AbstractUniGroupByMutator {

    private final Function<A, NewA> groupKeyMapping;

    public UniGroupBy1Map0CollectFastMutator(Function<A, NewA> groupKeyMapping) {
        this.groupKeyMapping = groupKeyMapping;
    }

    @Override
    public AbstractRuleAssembler apply(AbstractRuleAssembler ruleAssembler) {
        Variable<A> input = ruleAssembler.getVariable(0);
        Variable<NewA> groupKey = ruleAssembler.createVariable("groupKey");
        ViewItem groupByPattern = groupBy(getInnerAccumulatePattern(ruleAssembler), input, groupKey,
                groupKeyMapping::apply);
        Variable<NewA> newA = ruleAssembler.createVariable("newA", from(groupKey));
        return toUni(ruleAssembler, groupByPattern, newA);
    }
}
