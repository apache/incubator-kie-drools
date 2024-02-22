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
package org.kie.dmn.core.impl;

import java.util.HashMap;
import java.util.Map;

import org.kie.dmn.api.core.DMNType;
import org.kie.dmn.feel.lang.Type;
import org.kie.dmn.model.api.FunctionItem;

/**
 * @see DMNType
 */
public class SimpleFnTypeImpl extends SimpleTypeImpl {

    private final Map<String, DMNType> params;
    private final DMNType returnType;
    private final FunctionItem fi;

    public SimpleFnTypeImpl(String namespace, String name, String id, Type feelType, Map<String, DMNType> params, DMNType returnType, FunctionItem fi) {
        super(namespace, name, id, false, null, null, null, feelType);
        this.params = new HashMap<>(params);
        this.returnType = returnType;
        this.fi = fi;
    }

    public Map<String, DMNType> getParams() {
        return params;
    }

    public DMNType getReturnType() {
        return returnType;
    }

    public FunctionItem getFunctionItem() {
        return fi;
    }

    public BaseDMNTypeImpl clone() {
        throw new UnsupportedOperationException();
    }
}
