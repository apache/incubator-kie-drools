/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.codegen.rules;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.drools.model.codegen.execmodel.QueryModel;
import org.drools.ruleunits.api.RuleUnit;
import org.drools.ruleunits.api.conf.RuleUnitConfig;
import org.drools.ruleunits.impl.AbstractRuleUnitDescription;
import org.drools.ruleunits.impl.GeneratedRuleUnitDescription;
import org.kie.internal.ruleunit.RuleUnitDescription;
import org.kie.kogito.codegen.api.context.KogitoBuildContext;

import com.github.javaparser.ast.type.ClassOrInterfaceType;

import static java.util.stream.Collectors.toList;

public class RuleUnitGenerator {

    private final RuleUnitDescription ruleUnit;
    private final String ruleUnitPackageName;
    private final String typeName;
    private final String generatedSourceFile;
    private final KogitoBuildContext context;
    private final String targetCanonicalName;
    private final String targetTypeName;
    private RuleUnitConfig config;
    private List<QueryGenerator> queryGenerators;

    public RuleUnitGenerator(KogitoBuildContext context, RuleUnitDescription ruleUnit, String generatedSourceFile) {
        this.ruleUnit = ruleUnit;
        this.ruleUnitPackageName = ruleUnit.getPackageName();
        this.typeName = ruleUnit.getSimpleName();
        this.generatedSourceFile = generatedSourceFile;
        this.context = context;
        this.targetTypeName = typeName + "RuleUnit";
        this.targetCanonicalName = ruleUnitPackageName + "." + targetTypeName;
        // merge config from the descriptor with configs from application.conf
        // application.conf overrides any other config
        this.config = ((AbstractRuleUnitDescription) ruleUnit).getConfig();
    }

    // override config for this rule unit with the given values
    public RuleUnitGenerator mergeConfig(RuleUnitConfig ruleUnitConfig) {
        this.config = config.merged(ruleUnitConfig);
        return this;
    }

    public Collection<QueryGenerator> queries() {
        return this.queryGenerators;
    }

    public String targetCanonicalName() {
        return targetCanonicalName;
    }

    public String typeName() {
        return typeName;
    }

    public Optional<RuleUnitPojoGenerator> pojo(RuleUnitHelper ruleUnitHelper) {
        if (ruleUnit instanceof GeneratedRuleUnitDescription) {
            return Optional.of(new RuleUnitPojoGenerator((GeneratedRuleUnitDescription) ruleUnit, ruleUnitHelper));
        } else {
            return Optional.empty();
        }
    }

    public static ClassOrInterfaceType ruleUnitType(String canonicalName) {
        return new ClassOrInterfaceType(null, RuleUnit.class.getCanonicalName())
                .setTypeArguments(new ClassOrInterfaceType(null, canonicalName));
    }

    public RuleUnitGenerator withQueries(Collection<QueryModel> queries) {
        this.queryGenerators = queries.stream()
                .filter(query -> !query.hasParameters())
                .map(query -> new QueryGenerator(context, ruleUnit, query))
                .collect(toList());

        return this;
    }

    public RuleUnitDescription getRuleUnitDescription() {
        return ruleUnit;
    }
}
