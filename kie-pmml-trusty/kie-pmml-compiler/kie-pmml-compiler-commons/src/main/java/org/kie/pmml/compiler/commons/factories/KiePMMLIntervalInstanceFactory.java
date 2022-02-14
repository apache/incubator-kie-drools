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
package org.kie.pmml.compiler.commons.factories;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.dmg.pmml.Interval;
import org.kie.pmml.api.enums.CLOSURE;
import org.kie.pmml.commons.model.expressions.KiePMMLInterval;

/**
 * Class meant to provide <i>helper</i> method to retrieve <code>KiePMMLInterval</code> instance
 * <p>
 * out of <code>Interval</code>s
 */
public class KiePMMLIntervalInstanceFactory {

    private KiePMMLIntervalInstanceFactory() {
        // Avoid instantiation
    }

    static List<KiePMMLInterval> getKiePMMLIntervals(List<Interval> toConvert) {
        return toConvert != null ? toConvert.stream()
                .map(KiePMMLIntervalInstanceFactory::getKiePMMLInterval)
                .collect(Collectors.toList()) : Collections.emptyList();
    }

    static KiePMMLInterval getKiePMMLInterval(final Interval interval) {
        return new KiePMMLInterval(interval.getLeftMargin(), interval.getRightMargin(),
                                   CLOSURE.byName(interval.getClosure().value()));
    }
}
