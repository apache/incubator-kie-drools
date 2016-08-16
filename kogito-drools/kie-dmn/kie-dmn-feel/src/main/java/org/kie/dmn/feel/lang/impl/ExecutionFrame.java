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

package org.kie.dmn.feel.lang.impl;

import org.kie.dmn.feel.util.EvalHelper;

import java.util.HashMap;
import java.util.Map;

public class ExecutionFrame {
    private ExecutionFrame parentFrame;

    private Map<String, Object> variables = new HashMap<>();

    public ExecutionFrame(ExecutionFrame parentFrame) {
        this.parentFrame = parentFrame;
    }

    public ExecutionFrame getParentFrame() {
        return parentFrame;
    }

    public void setParentFrame(ExecutionFrame parentFrame) {
        this.parentFrame = parentFrame;
    }

    public Object getValue(String symbol) {
        symbol = EvalHelper.normalizeVariableName( symbol );
        if ( variables.containsKey( symbol ) ) {
            return variables.get( symbol );
        }
        if ( parentFrame != null ) {
            return parentFrame.getValue( symbol );
        }
        return null;
    }

    public Object getValue(String[] symbol) {
        return null;
    }

    public void setValue(String symbol, Object value) {
        this.variables.put( EvalHelper.normalizeVariableName( symbol ), value );
    }
}
