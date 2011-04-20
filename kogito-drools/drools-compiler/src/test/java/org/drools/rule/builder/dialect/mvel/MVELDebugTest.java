package org.drools.rule.builder.dialect.mvel;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import org.drools.base.mvel.MVELConsequence;
import org.drools.compiler.DrlParser;
import org.drools.compiler.PackageBuilder;
import org.drools.lang.descr.PackageDescr;
import org.drools.lang.descr.RuleDescr;
import org.drools.rule.Package;
import org.mvel2.compiler.CompiledExpression;

public class MVELDebugTest {

    @Test
    public void testDebug() throws Exception {
        String rule = "package com.sample; dialect \"mvel\" rule myRule when then\n System.out.println( \"test\" ); end";
        PackageBuilder builder = new PackageBuilder();
        DrlParser parser = new DrlParser();
        PackageDescr packageDescr = parser.parse(rule);
        RuleDescr ruleDescr = packageDescr.getRules().get(0);
        builder = new PackageBuilder( );
        builder.addPackage(packageDescr);
        Package pkg = builder.getPackage();
        MVELConsequence consequence = (MVELConsequence) pkg.getRule("myRule").getConsequence();
        String sourceName = ((CompiledExpression) consequence.getCompExpr()).getSourceName();
        System.out.println(sourceName);
        String ruleName = ruleDescr.getNamespace() + "." + ruleDescr.getClassName();
        System.out.println(ruleName);
        assertEquals(sourceName, ruleName);
    }

}
