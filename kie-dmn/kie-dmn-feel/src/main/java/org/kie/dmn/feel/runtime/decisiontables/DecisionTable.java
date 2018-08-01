/*
 * Copyright 2005 JBoss Inc
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

package org.kie.dmn.feel.runtime.decisiontables;

import java.util.List;

import org.kie.dmn.feel.lang.Type;
import org.kie.dmn.feel.runtime.UnaryTest;

public interface DecisionTable {

    String getName();

    List<? extends OutputClause> getOutputs();

    interface OutputClause {

        String getName();

        List<UnaryTest> getOutputValues();

        Type getType();

        boolean isCollection();
    }
}
