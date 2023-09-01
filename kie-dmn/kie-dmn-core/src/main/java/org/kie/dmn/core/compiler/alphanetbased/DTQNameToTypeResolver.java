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
package org.kie.dmn.core.compiler.alphanetbased;

import javax.xml.namespace.QName;

import org.kie.dmn.api.core.DMNType;
import org.kie.dmn.core.compiler.DMNCompilerImpl;
import org.kie.dmn.core.impl.BaseDMNTypeImpl;
import org.kie.dmn.core.impl.DMNModelImpl;
import org.kie.dmn.feel.lang.Type;
import org.kie.dmn.model.api.DecisionTable;
import org.kie.dmn.model.api.NamedElement;

public class DTQNameToTypeResolver {

    private final DMNCompilerImpl compiler;
    private final DMNModelImpl model;
    private final NamedElement node;
    private final DecisionTable decisionTable;

    public DTQNameToTypeResolver(DMNCompilerImpl compiler, DMNModelImpl model, NamedElement node, DecisionTable decisionTable) {
        this.compiler = compiler;
        this.model = model;
        this.node = node;
        this.decisionTable = decisionTable;
    }

    public Type resolve(QName qname) {
        DMNType resolveTypeRef = compiler.resolveTypeRef(model, node, decisionTable, qname);
        return ((BaseDMNTypeImpl) resolveTypeRef).getFeelType();
    }
}
