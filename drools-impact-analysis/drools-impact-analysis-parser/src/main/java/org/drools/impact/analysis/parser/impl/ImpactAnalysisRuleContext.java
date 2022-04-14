/*
 * Copyright (c) 2021. Red Hat, Inc. and/or its affiliates.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.impact.analysis.parser.impl;

import java.util.HashMap;
import java.util.Map;

import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.drools.drl.ast.descr.RuleDescr;
import org.drools.util.TypeResolver;
import org.drools.modelcompiler.builder.PackageModel;
import org.drools.modelcompiler.builder.generator.RuleContext;

public class ImpactAnalysisRuleContext extends RuleContext {

    private Map<String, Object> bindVariableLiteralMap = new HashMap<>();

    public ImpactAnalysisRuleContext(KnowledgeBuilderImpl kbuilder, PackageModel packageModel, TypeResolver typeResolver, RuleDescr ruleDescr) {
        super(kbuilder, packageModel, typeResolver, ruleDescr);
    }

    public Map<String, Object> getBindVariableLiteralMap() {
        return bindVariableLiteralMap;
    }

    public void setBindVariableLiteralMap(Map<String, Object> bindVariableLiteralMap) {
        this.bindVariableLiteralMap = bindVariableLiteralMap;
    }

}
