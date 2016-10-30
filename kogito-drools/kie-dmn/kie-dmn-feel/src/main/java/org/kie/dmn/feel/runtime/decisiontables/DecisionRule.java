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

package org.kie.dmn.feel.runtime.decisiontables;

import org.kie.dmn.feel.runtime.UnaryTest;

import java.util.ArrayList;
import java.util.List;

public class DecisionRule {
    private List<UnaryTest> inputEntry;
    private List<Object>    outputEntry;

    public List<UnaryTest> getInputEntry() {
        if ( inputEntry == null ) {
            inputEntry = new ArrayList<>();
        }
        return this.inputEntry;
    }

    public List<Object> getOutputEntry() {
        if ( outputEntry == null ) {
            outputEntry = new ArrayList<>();
        }
        return this.outputEntry;
    }
}
