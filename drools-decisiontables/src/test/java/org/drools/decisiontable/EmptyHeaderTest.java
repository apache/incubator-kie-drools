package org.drools.decisiontable;

import org.drools.template.parser.DecisionTableParseException;
import org.junit.Test;
import org.kie.internal.builder.DecisionTableConfiguration;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;

import static org.kie.api.io.ResourceType.DTABLE;
import static org.kie.internal.builder.DecisionTableInputType.CSV;
import static org.kie.internal.builder.DecisionTableInputType.XLS;
import static org.kie.internal.io.ResourceFactory.newClassPathResource;

public class EmptyHeaderTest {

    @Test(expected = DecisionTableParseException.class)
    public void testEmptyConditionInXLS() {
        DecisionTableConfiguration dtconf = KnowledgeBuilderFactory.newDecisionTableConfiguration();
        dtconf.setInputType(XLS);
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory .newKnowledgeBuilder();
        
        kbuilder.add(newClassPathResource("emptyCondition.drl.xls", getClass()), DTABLE, dtconf);
    }

    @Test(expected = DecisionTableParseException.class)
    public void testEmptyActionInCSV() {
        DecisionTableConfiguration dtconf = KnowledgeBuilderFactory.newDecisionTableConfiguration();
        dtconf.setInputType(CSV);
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        
        kbuilder.add(newClassPathResource("emptyAction.drl.csv", getClass()), DTABLE, dtconf);
    }
}
