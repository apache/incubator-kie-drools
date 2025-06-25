/*
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
package org.kie.dmn.model.v1_6;

import org.kie.dmn.model.api.FunctionItem;
import org.kie.dmn.model.api.UnaryTests;
import org.kie.dmn.model.impl.AbstractTItemDefinition;


public class TItemDefinition extends AbstractTItemDefinition implements URIFEELed {

    @Override
    public UnaryTests getTypeConstraint() {
        return typeConstraint;
    }

    @Override
    public void setTypeConstraint(UnaryTests value) {
        this.typeConstraint = value;
    }

    @Override
    public FunctionItem getFunctionItem() {
        return functionItem;
    }

    @Override
    public void setFunctionItem(FunctionItem value) {
        this.functionItem = value;
    }
}
