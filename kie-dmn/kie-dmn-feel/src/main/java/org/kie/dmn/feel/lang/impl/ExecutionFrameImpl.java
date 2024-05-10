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
package org.kie.dmn.feel.lang.impl;

import java.util.HashMap;
import java.util.Map;

import org.kie.dmn.feel.util.EvalHelper;
import org.kie.dmn.feel.util.EvalHelper.PropertyValueResult;
import org.kie.dmn.feel.util.StringEvalHelper;

public class ExecutionFrameImpl
        implements ExecutionFrame {

    private ExecutionFrame parentFrame;

    private Map<String, Object> variables;
    private Object rootObject;

    public ExecutionFrameImpl(ExecutionFrame parentFrame) {
        this.parentFrame = parentFrame;
        this.variables = new HashMap<>();
    }

    public ExecutionFrameImpl(ExecutionFrame parentFrame, int size) {
        this.parentFrame = parentFrame;
        this.variables = new HashMap<>(size);
    }

    public ExecutionFrame getParentFrame() {
        return parentFrame;
    }

    public void setParentFrame(ExecutionFrame parentFrame) {
        this.parentFrame = parentFrame;
    }

    @Override
    public Object getValue(String symbol) {
        symbol = StringEvalHelper.normalizeVariableName(symbol );
        if (rootObject != null) {
            PropertyValueResult dv = EvalHelper.getDefinedValue(rootObject, symbol);
            if (dv.isDefined()) {
                return dv.getValueResult().getOrElse(null);
            }
        }
        if ( variables.containsKey( symbol ) ) {
            return variables.get( symbol );
        }
        if ( parentFrame != null ) {
            return parentFrame.getValue( symbol );
        }
        return null;
    }

    @Override
    public boolean isDefined(String symbol) {
        symbol = StringEvalHelper.normalizeVariableName( symbol );
        if (rootObject != null) {
            if (EvalHelper.getDefinedValue(rootObject, symbol).isDefined()) {
                return true;
            } else {
                // do nothing! it might be shaded at this level for "item" or being in the parent frame.
            }
        }
        if ( variables.containsKey( symbol ) ) {
            return true;
        }
        if ( parentFrame != null ) {
            return parentFrame.isDefined( symbol );
        }
        return false;
    }

    @Override
    public void setValue(String symbol, Object value) {
        this.variables.put( StringEvalHelper.normalizeVariableName( symbol ), value );
    }

    @Override
    public Map<String, Object> getAllValues() {
        return this.variables;
    }

    @Override
    public void setRootObject(Object v) {
        this.rootObject = v;
    }

    @Override
    public Object getRootObject() {
        return rootObject;
    }
}
