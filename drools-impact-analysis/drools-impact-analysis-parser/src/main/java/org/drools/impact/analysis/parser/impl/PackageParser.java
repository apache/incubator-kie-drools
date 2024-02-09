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

import java.util.ArrayList;
import java.util.List;

import org.drools.compiler.compiler.PackageRegistry;
import org.drools.compiler.lang.descr.CompositePackageDescr;
import org.drools.drl.ast.descr.RuleDescr;
import org.drools.impact.analysis.model.Package;
import org.drools.impact.analysis.model.Rule;
import org.drools.impact.analysis.parser.internal.ImpactModelBuilderImpl;
import org.drools.model.codegen.execmodel.PackageModel;
import org.drools.model.codegen.execmodel.generator.RuleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.drools.model.codegen.execmodel.generator.RuleContext.DIALECT_ATTRIBUTE;

public class PackageParser {

    Logger logger = LoggerFactory.getLogger(PackageParser.class);

    private final ImpactModelBuilderImpl kbuilder;
    private final PackageModel packageModel;
    private final CompositePackageDescr packageDescr;
    private final PackageRegistry pkgRegistry;

    public PackageParser( ImpactModelBuilderImpl kbuilder, PackageModel packageModel, CompositePackageDescr packageDescr, PackageRegistry pkgRegistry ) {
        this.kbuilder = kbuilder;
        this.packageModel = packageModel;
        this.packageDescr = packageDescr;
        this.pkgRegistry = pkgRegistry;
    }

    public Package parse() {
        List<Rule> rules = new ArrayList<>();
        for (RuleDescr ruleDescr : packageDescr.getRules()) {
            rules.add( parseRule( ruleDescr ) );
        }
        return new Package( packageDescr.getName(), rules );
    }

    private Rule parseRule( RuleDescr ruleDescr ) {
        RuleContext context = new ImpactAnalysisRuleContext(kbuilder, packageModel, pkgRegistry.getTypeResolver(), ruleDescr);
        context.addGlobalDeclarations();
        context.setDialectFromAttribute( packageDescr.getAttribute( DIALECT_ATTRIBUTE ) );
        Rule rule = new Rule( packageDescr.getName(), ruleDescr.getName(), ruleDescr.getResource().getSourcePath() );

        logger.debug("Parsing : " + rule.getName());

        new LhsParser( packageModel, pkgRegistry ).parse( ruleDescr, context, rule );
        new RhsParser( pkgRegistry ).parse( ruleDescr, context, rule );

        return rule;
    }
}
