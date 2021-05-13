/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

package org.kie.dmn.trisotech.model.api;

import org.kie.dmn.model.api.Expression;

public interface Iterator extends Expression {

    enum IteratorType {
        FOR,
        EVERY,
        SOME
    }

    String getVariable();

    Expression getIn();

    Expression getReturn();

    IteratorType getIteratorType();

    void setVariable(String var);

    void setIn(Expression expr);

    void setReturn(Expression expr);

    void setIteratorType(IteratorType type);

}
