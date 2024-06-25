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
package org.drools.traits.core.reteoo;

import java.util.List;
import java.util.Optional;

import org.drools.base.base.ObjectType;
import org.drools.base.rule.Pattern;
import org.drools.traits.core.base.evaluators.IsAEvaluatorDefinition;
import org.drools.core.reteoo.AlphaNode;
import org.drools.core.reteoo.ObjectSource;
import org.drools.base.reteoo.PropertySpecificUtil;
import org.drools.core.reteoo.builder.BuildContext;
import org.drools.mvel.EvaluatorConstraint;
import org.drools.base.rule.constraint.AlphaNodeFieldConstraint;
import org.drools.util.bitmask.BitMask;
import org.kie.api.runtime.rule.Operator;

public class TraitAlphaNode extends AlphaNode {

    public TraitAlphaNode() {
    }

    public TraitAlphaNode(int id, AlphaNodeFieldConstraint constraint, ObjectSource objectSource, BuildContext context) {
        super(id, constraint, objectSource, context);
    }

    @Override
    public BitMask calculateDeclaredMask(Pattern pattern, ObjectType objectType, List<String> settableProperties) {
        BitMask mask = constraint.getListenedPropertyMask(Optional.ofNullable(pattern), objectType, settableProperties);
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
