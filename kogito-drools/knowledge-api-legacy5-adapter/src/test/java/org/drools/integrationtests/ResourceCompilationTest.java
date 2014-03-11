package org.drools.integrationtests;

import org.drools.builder.DecisionTableConfiguration;
import org.drools.builder.DecisionTableInputType;
import org.junit.Test;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.io.ResourceFactory;

/**
 * Illustrates knowledge-api resource compilation problems.
 */
public class ResourceCompilationTest {

    @Test
    public void testDecisionTableXls() {
        DecisionTableConfiguration dtconf = KnowledgeBuilderFactory.newDecisionTableConfiguration();
        dtconf.setInputType(DecisionTableInputType.XLS);

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newClassPathResource("sample.xls", getClass()), ResourceType.DTABLE, dtconf);

        if (kbuilder.hasErrors()) {
            throw new RuntimeException("Drools compile errors: " + kbuilder.getErrors().toString());
        }
    }

    @Test
    public void testDecisionTableCsv() {
        DecisionTableConfiguration dtconf = KnowledgeBuilderFactory.newDecisionTableConfiguration();
        dtconf.setInputType(DecisionTableInputType.CSV);

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newClassPathResource("sample.csv", getClass()), ResourceType.DTABLE, dtconf);

        if (kbuilder.hasErrors()) {
            throw new RuntimeException("Drools compile errors: " + kbuilder.getErrors().toString());
        }
    }

}
