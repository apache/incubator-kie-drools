/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.kogito.grafana.dmn;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.kie.kogito.grafana.model.functions.BaseExpression;
import org.kie.kogito.grafana.model.functions.BiGrafanaOperation;
import org.kie.kogito.grafana.model.functions.GrafanaFunction;
import org.kie.kogito.grafana.model.functions.GrafanaOperation;
import org.kie.kogito.grafana.model.functions.IncreaseFunction;
import org.kie.kogito.grafana.model.functions.SumFunction;
import org.kie.kogito.grafana.model.panel.common.YAxis;

public class LocalTimeType extends AbstractDmnType {
    private static final String DMN_TYPE = "time";

    public LocalTimeType() {
        super(LocalTime.class, DMN_TYPE);
        BaseExpression firstBaseExpression = new BaseExpression(DMN_TYPE, "sum");
        BaseExpression secondBaseExpression = new BaseExpression(DMN_TYPE, "count");

        GrafanaFunction firstOperand = new SumFunction(new IncreaseFunction(firstBaseExpression, "1m"));
        GrafanaFunction secondOperand = new SumFunction(new IncreaseFunction(secondBaseExpression, "1m"));

        setGrafanaFunction(new BiGrafanaOperation(GrafanaOperation.DIVISION, firstOperand, secondOperand));

        List<YAxis> yaxes = new ArrayList<>();
        yaxes.add(new YAxis("dthms", true));
        yaxes.add(new YAxis("ms", false));
        setYAxes(yaxes);
    }
}
