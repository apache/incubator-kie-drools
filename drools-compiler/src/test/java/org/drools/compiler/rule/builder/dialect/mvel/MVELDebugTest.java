/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.compiler.rule.builder.dialect.mvel;

import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.drools.core.definitions.InternalKnowledgePackage;
import org.junit.Test;
import org.kie.internal.builder.conf.LanguageLevelOption;

import static org.junit.Assert.*;

import org.drools.core.base.mvel.MVELConsequence;
import org.drools.compiler.compiler.DrlParser;
import org.drools.compiler.lang.descr.PackageDescr;
import org.drools.compiler.lang.descr.RuleDescr;
import org.mvel2.compiler.CompiledExpression;

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
        InternalKnowledgePackage pkg = builder.getPackage();
        MVELConsequence consequence = (MVELConsequence) pkg.getRule("myRule").getConsequence();
        String sourceName = ((CompiledExpression) consequence.getCompExpr()).getSourceName();
        System.out.println(sourceName);
        String ruleName = ruleDescr.getNamespace() + "." + ruleDescr.getClassName();
        System.out.println(ruleName);
        assertEquals(sourceName, ruleName);
    }

}
