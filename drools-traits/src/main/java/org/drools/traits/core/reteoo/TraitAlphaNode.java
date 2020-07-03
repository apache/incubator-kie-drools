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

package org.drools.traits.core.reteoo;

import java.util.List;

import org.drools.traits.core.base.evaluators.IsAEvaluatorDefinition;
import org.drools.core.reteoo.AlphaNode;
import org.drools.core.reteoo.ObjectSource;
import org.drools.core.reteoo.PropertySpecificUtil;
import org.drools.core.reteoo.builder.BuildContext;
import org.drools.core.rule.constraint.EvaluatorConstraint;
import org.drools.core.spi.AlphaNodeFieldConstraint;
import org.drools.core.util.bitmask.BitMask;
import org.kie.api.runtime.rule.Operator;

public class TraitAlphaNode extends AlphaNode {

    public TraitAlphaNode() {
    }

    public TraitAlphaNode(int id, AlphaNodeFieldConstraint constraint, ObjectSource objectSource, BuildContext context) {
        super(id, constraint, objectSource, context);
    }

    @Override
    public BitMask calculateDeclaredMask(Class modifiedClass, List<String> settableProperties) {
        BitMask mask = constraint.getListenedPropertyMask(modifiedClass, settableProperties);
        if (isTraitEvaluator()) {
            return mask.set(PropertySpecificUtil.TRAITABLE_BIT);
        }
        return mask;
    }

    private boolean isTraitEvaluator() {
        if (constraint instanceof EvaluatorConstraint && ((EvaluatorConstraint) constraint).isSelf()) {
            Operator op = ((EvaluatorConstraint) constraint).getEvaluator().getOperator();
            return op == IsAEvaluatorDefinition.ISA || op == IsAEvaluatorDefinition.NOT_ISA;
        }
        return false;
    }
}
