/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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
package org.kie.dmn.model.v1_1;

import org.kie.dmn.model.api.Binding;
import org.kie.dmn.model.api.Expression;
import org.kie.dmn.model.api.InformationItem;

public class TBinding extends KieDMNModelInstrumentedBase implements Binding {

    private InformationItem parameter;
    private Expression expression;

    @Override
    public InformationItem getParameter() {
        return parameter;
    }

    @Override
    public void setParameter(final InformationItem value) {
        this.parameter = value;
    }

    @Override
    public Expression getExpression() {
        return expression;
    }

    @Override
    public void setExpression(final Expression value) {
        this.expression = value;
    }

}
