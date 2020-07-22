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

import static org.drools.model.DSL.accFunction;
import static org.drools.model.PatternDSL.PatternDef;
import static org.drools.model.PatternDSL.alphaIndexedBy;
import static org.drools.model.PatternDSL.pattern;

import java.util.Collection;
import java.util.function.BiConsumer;

import org.drools.core.base.accumulators.CollectSetAccumulateFunction;
import org.drools.model.DSL;
import org.drools.model.Index;
import org.drools.model.PatternDSL;
import org.drools.model.Variable;
import org.drools.model.view.ViewItem;

abstract class AbstractUniGroupByMutator extends AbstractGroupByMutator {

    @Override
    protected <InTuple> PatternDef bindTupleVariableOnFirstGrouping(AbstractRuleAssembler ruleAssembler, PatternDef pattern,
            Variable<InTuple> inTupleVariable) {
        return pattern.bind(inTupleVariable, a -> a);
    }

    protected <InTuple> AbstractRuleAssembler universalGroup(AbstractRuleAssembler ruleAssembler,
            BiConsumer<PatternDef, Variable<InTuple>> primaryPatternVariableBinder, Transformer<InTuple> mutator) {
        Variable<InTuple> mappedVariable = Util.createVariable(ruleAssembler.generateNextId("biMapped"));
        primaryPatternVariableBinder.accept(ruleAssembler.getLastPrimaryPattern(), mappedVariable);
        ViewItem<?> innerAccumulatePattern = getInnerAccumulatePattern(ruleAssembler);
        Variable<Collection<InTuple>> tupleCollection =
                (Variable<Collection<InTuple>>) Util.createVariable(Collection.class,
                        ruleAssembler.generateNextId("tupleCollection"));
        PatternDSL.PatternDef<Collection<InTuple>> pattern = pattern(tupleCollection)
                .expr("Non-empty", collection -> !collection.isEmpty(),
                        alphaIndexedBy(Integer.class, Index.ConstraintType.GREATER_THAN, -1, Collection::size, 0));
        ViewItem<Object> accumulate = DSL.accumulate(innerAccumulatePattern,
                accFunction(CollectSetAccumulateFunction.class, mappedVariable).as(tupleCollection));
        return mutator.apply(tupleCollection, pattern, accumulate);
    }

}
