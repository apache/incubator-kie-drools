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
