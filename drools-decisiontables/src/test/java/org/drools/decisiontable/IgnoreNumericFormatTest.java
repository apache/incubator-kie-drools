package org.drools.decisiontable;

import java.io.IOException;
import java.math.BigDecimal;

import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.impl.KnowledgeBaseFactory;
import org.junit.Test;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.internal.builder.DecisionTableConfiguration;
import org.kie.internal.builder.DecisionTableInputType;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class IgnoreNumericFormatTest {

    @Test
    public void testPercentAndCurrencyFormat() throws IOException {
        SpreadsheetCompiler compiler = new SpreadsheetCompiler();
        String drl = compiler.compile(ResourceFactory.newClassPathResource("ignore-numeric-format.xls", getClass()).getInputStream(), InputType.XLS);
        System.out.println(drl);

        DecisionTableConfiguration dtconf = KnowledgeBuilderFactory.newDecisionTableConfiguration();
        dtconf.setInputType(DecisionTableInputType.XLSX);
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newClassPathResource("ignore-numeric-format.xls", getClass()), ResourceType.DTABLE, dtconf);
        if (kbuilder.hasErrors()) {
            fail(kbuilder.getErrors().toString());
        }
        InternalKnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addPackages(kbuilder.getKnowledgePackages());

        KieSession ksession = kbase.newKieSession();

        ValueHolder a = new ValueHolder("A");
        ValueHolder b = new ValueHolder("B");
        ValueHolder c = new ValueHolder("C");
        ValueHolder d = new ValueHolder("D");

        ksession.insert(a);
        ksession.insert(b);
        ksession.insert(c);
        ksession.insert(d);
        ksession.fireAllRules();

        assertEquals(new BigDecimal("0.0"), a.getPercentValue());
        assertEquals(new BigDecimal("0.0"), a.getCurrencyValue1());
        assertEquals(new BigDecimal("0.0"), a.getCurrencyValue2());
        assertEquals(0, a.getIntValue());
        assertEquals(0, a.getDoubleValue(), 0);

        assertEquals(new BigDecimal("0.01"), b.getPercentValue());
        assertEquals(new BigDecimal("0.5"), b.getCurrencyValue1());
        assertEquals(new BigDecimal("0.5"), b.getCurrencyValue2());
        assertEquals(-1, b.getIntValue());
        assertEquals(0.5, b.getDoubleValue(), 0);

        assertEquals(new BigDecimal("0.5"), c.getPercentValue());
        assertEquals(new BigDecimal("1.0"), c.getCurrencyValue1());
        assertEquals(new BigDecimal("1.0"), c.getCurrencyValue2());
        assertEquals(1, c.getIntValue());
        assertEquals(-1, c.getDoubleValue(), 0);

        assertEquals(new BigDecimal("1.0"), d.getPercentValue());
        assertEquals(new BigDecimal("100.0"), d.getCurrencyValue1());
        assertEquals(new BigDecimal("100.0"), d.getCurrencyValue2());
        assertEquals(100, d.getIntValue());
        assertEquals(100, d.getDoubleValue(), 0);

        ksession.dispose();
    }

}
