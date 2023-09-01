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
package org.drools.mvel.compiler.rule.builder.dialect.mvel;

import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.drools.drl.parser.DrlParser;
import org.drools.drl.ast.descr.PackageDescr;
import org.drools.drl.ast.descr.RuleDescr;
import org.drools.base.definitions.InternalKnowledgePackage;
import org.drools.mvel.expr.MVELConsequence;
import org.junit.Test;
import org.kie.internal.builder.conf.LanguageLevelOption;

import static org.assertj.core.api.Assertions.assertThat;

public class MVELDebugTest {

    @Test
    public void testDebug() throws Exception {
        String rule = "package com.sample; dialect \"mvel\" rule myRule when then\n System.out.println( \"test\" ); end";
        KnowledgeBuilderImpl builder = new KnowledgeBuilderImpl();
        DrlParser parser = new DrlParser(LanguageLevelOption.DRL5);
        PackageDescr packageDescr = parser.parse(null, rule);
        RuleDescr ruleDescr = packageDescr.getRules().get(0);
        builder = new KnowledgeBuilderImpl( );
        builder.addPackage(packageDescr);
        InternalKnowledgePackage pkg = builder.getPackage("com.sample");
        MVELConsequence consequence = (MVELConsequence) pkg.getRule("myRule").getConsequence();
        String ruleName = ruleDescr.getNamespace() + "." + ruleDescr.getClassName();
        System.out.println(ruleName);
        assertThat(ruleName).isEqualTo("com.sample.Rule_myRule");
    }

}
