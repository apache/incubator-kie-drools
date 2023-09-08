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
package org.drools.impact.analysis.parser.impl;

import java.util.HashMap;
import java.util.Map;

import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.drools.drl.ast.descr.RuleDescr;
import org.drools.model.codegen.execmodel.PackageModel;
import org.drools.model.codegen.execmodel.generator.RuleContext;
import org.drools.util.TypeResolver;

public class ImpactAnalysisRuleContext extends RuleContext {

    private Map<String, Object> bindVariableLiteralMap = new HashMap<>();

    public ImpactAnalysisRuleContext(KnowledgeBuilderImpl kbuilder, PackageModel packageModel, TypeResolver typeResolver, RuleDescr ruleDescr) {
        super(kbuilder, kbuilder, packageModel, typeResolver, ruleDescr);
    }

    public Map<String, Object> getBindVariableLiteralMap() {
        return bindVariableLiteralMap;
    }

    public void setBindVariableLiteralMap(Map<String, Object> bindVariableLiteralMap) {
        this.bindVariableLiteralMap = bindVariableLiteralMap;
    }

}
