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

package org.drools.model.codegen.execmodel.processors;

import java.util.Map;
import java.util.Set;

import org.drools.compiler.builder.impl.processors.RuleValidator;
import org.drools.compiler.compiler.PackageRegistry;
import org.drools.drl.ast.descr.PackageDescr;
import org.drools.drl.ast.descr.RuleDescr;
import org.drools.drl.parser.ParserError;
import org.kie.internal.builder.KnowledgeBuilderConfiguration;

public class ModelRuleValidator extends RuleValidator {

    // extra rule names which need to be checked for duplicates.
    // Non-executable model doesn't need this because included kbase assets are added to the packageDescr
    // Executable model requires this because included kbase assets are modeled as separated kjar, so not added to the packageDescr
    private final Map<String, Set<String>> includedRuleNameMap;

    public ModelRuleValidator(PackageRegistry pkgRegistry, PackageDescr packageDescr, KnowledgeBuilderConfiguration configuration, Map<String, Set<String>> includedRuleNameMap) {
        super(pkgRegistry, packageDescr, configuration);
        this.includedRuleNameMap = includedRuleNameMap;
    }

    @Override
    public void process() {
        super.process();

        // Check with included rule names, because exec-model doesn't add assets of included kbase to the packageDescr
        String packageName = packageDescr.getNamespace();
        if (includedRuleNameMap.containsKey(packageName)) {
            Set<String> ruleNames = includedRuleNameMap.get(packageName);
            for (final RuleDescr rule : packageDescr.getRules()) {
                final String name = rule.getUnitQualifiedName();
                if (ruleNames.contains(name)) {
                    this.results.add(new ParserError(rule.getResource(),
                            "Duplicate rule name: " + name,
                            rule.getLine(),
                            rule.getColumn(),
                            packageName));
                }
            }
        }
    }
}
