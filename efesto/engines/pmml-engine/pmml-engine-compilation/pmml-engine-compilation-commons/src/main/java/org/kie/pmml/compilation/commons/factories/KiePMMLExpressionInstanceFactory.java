/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.pmml.compilation.commons.factories;

import org.dmg.pmml.*;
import org.kie.pmml.commons.model.expressions.KiePMMLExpression;

import java.util.List;
import java.util.stream.Collectors;

import static org.kie.pmml.commons.Constants.EXPRESSION_NOT_MANAGED;
import static org.kie.pmml.compilation.commons.factories.KiePMMLApplyInstanceFactory.getKiePMMLApply;
import static org.kie.pmml.compilation.commons.factories.KiePMMLConstantInstanceFactory.getKiePMMLConstant;
import static org.kie.pmml.compilation.commons.factories.KiePMMLDiscretizeInstanceFactory.getKiePMMLDiscretize;
import static org.kie.pmml.compilation.commons.factories.KiePMMLFieldRefInstanceFactory.getKiePMMLFieldRef;
import static org.kie.pmml.compilation.commons.factories.KiePMMLMapValuesInstanceFactory.getKiePMMLMapValues;
import static org.kie.pmml.compilation.commons.factories.KiePMMLNormContinuousInstanceFactory.getKiePMMLNormContinuous;
import static org.kie.pmml.compilation.commons.factories.KiePMMLNormDiscreteInstanceFactory.getKiePMMLNormDiscrete;
import static org.kie.pmml.compilation.commons.factories.KiePMMLTextIndexInstanceFactory.getKiePMMLTextIndex;

/**
 * Facade for actual implementations
 */
public class KiePMMLExpressionInstanceFactory {

    private KiePMMLExpressionInstanceFactory() {
        // Avoid instantiation
    }

    //  KiePMMLExpression instantiation

    public static List<KiePMMLExpression> getKiePMMLExpressions(final List<org.dmg.pmml.Expression> expressions) {
        return expressions.stream().map(KiePMMLExpressionInstanceFactory::getKiePMMLExpression).collect(Collectors.toList());
    }

    public static KiePMMLExpression getKiePMMLExpression(final org.dmg.pmml.Expression expression) {
        if (expression instanceof Apply) {
            return getKiePMMLApply((Apply) expression);
        } else if (expression instanceof Constant) {
            return getKiePMMLConstant((Constant) expression);
        } else if (expression instanceof Discretize) {
            return getKiePMMLDiscretize((Discretize) expression);
        } else if (expression instanceof FieldRef) {
            return getKiePMMLFieldRef((FieldRef) expression);
        } else if (expression instanceof MapValues) {
            return getKiePMMLMapValues((MapValues) expression);
        } else if (expression instanceof NormContinuous) {
            return getKiePMMLNormContinuous((NormContinuous) expression);
        } else if (expression instanceof NormDiscrete) {
            return getKiePMMLNormDiscrete((NormDiscrete) expression);
        } else if (expression instanceof TextIndex) {
            return getKiePMMLTextIndex((TextIndex) expression);
        } else {
            throw new IllegalArgumentException(String.format(EXPRESSION_NOT_MANAGED, expression.getClass()));
        }
    }
}
