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
package org.drools.base.reteoo;

import org.kie.api.runtime.rule.FactHandle;

public class AccumulateContextEntry {
    private Object key;
    private FactHandle resultFactHandle;
    private BaseTuple resultLeftTuple;
    private boolean propagated;
    private Object functionContext;
    private boolean toPropagate;
    private boolean empty = true;

    public AccumulateContextEntry(Object key) {
        this.key = key;
    }

    public FactHandle getResultFactHandle() {
        return resultFactHandle;
    }

    public void setResultFactHandle(FactHandle resultFactHandle) {
        this.resultFactHandle = resultFactHandle;
    }

    public BaseTuple getResultLeftTuple() {
        return resultLeftTuple;
    }

    public void setResultLeftTuple(BaseTuple resultLeftTuple) {
        this.resultLeftTuple = resultLeftTuple;
    }

    public boolean isPropagated() {
        return propagated;
    }

    public void setPropagated(boolean propagated) {
        this.propagated = propagated;
    }

    public boolean isToPropagate() {
        return toPropagate;
    }

    public void setToPropagate(boolean toPropagate) {
        this.toPropagate = toPropagate;
    }

    public Object getFunctionContext() {
        return functionContext;
    }

    public void setFunctionContext(Object context) {
        this.functionContext = context;
    }

    public Object getKey() {
        return this.key;
    }

    public boolean isEmpty() {
        return empty;
    }

    public void setEmpty(boolean empty) {
        this.empty = empty;
    }
}
