package org.drools.decisiontable;

import static org.junit.Assert.fail;

import org.drools.builder.DecisionTableConfiguration;
import org.drools.builder.DecisionTableInputType;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.io.ResourceFactory;
import org.junit.Test;

public class EmptyHeaderTest {

	@Test
	public void testEmptyConditionInXLS() {
		DecisionTableConfiguration dtconf = KnowledgeBuilderFactory
				.newDecisionTableConfiguration();
		dtconf.setInputType(DecisionTableInputType.XLS);
		KnowledgeBuilder kbuilder = KnowledgeBuilderFactory
				.newKnowledgeBuilder();
		try {
			kbuilder.add(ResourceFactory.newClassPathResource(
					"emptyCondition.xls", getClass()), ResourceType.DTABLE,
					dtconf);
		} catch (Throwable t) {
			t.printStackTrace();
			fail("NPE occured while parsing condition column header in XLS decision table.");
		}
	}

	@Test
	public void testEmptyActionInCSV() {
		DecisionTableConfiguration dtconf = KnowledgeBuilderFactory
				.newDecisionTableConfiguration();
		dtconf.setInputType(DecisionTableInputType.CSV);
		KnowledgeBuilder kbuilder = KnowledgeBuilderFactory
				.newKnowledgeBuilder();
		try {
			kbuilder.add(ResourceFactory.newClassPathResource(
					"emptyAction.csv", getClass()), ResourceType.DTABLE, dtconf);
		} catch (Throwable t) {
			t.printStackTrace();
			fail("NPE occured while parsing action column header in CSV decision table.");
		}
	}
}
