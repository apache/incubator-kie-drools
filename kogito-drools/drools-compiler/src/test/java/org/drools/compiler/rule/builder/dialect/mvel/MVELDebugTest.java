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
