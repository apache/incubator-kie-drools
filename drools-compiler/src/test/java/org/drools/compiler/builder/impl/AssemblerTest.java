package org.drools.compiler.builder.impl;

import org.junit.Test;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceType;
import org.kie.internal.builder.KnowledgeBuilderResults;
import org.kie.internal.builder.ResultSeverity;
import org.kie.internal.io.ResourceFactory;

import static org.junit.Assert.assertEquals;

public class AssemblerTest {

    @Test
    public void checkAssemblerRunsBeforeRules() {
        KnowledgeBuilderImpl kBuilder = new KnowledgeBuilderImpl();
        CompositeKnowledgeBuilderImpl ckBuilder = new CompositeKnowledgeBuilderImpl(kBuilder);

        // we pick an arbitrary resource that does not have a local implementation
        // file path is also non-existent and invalid because it is not really used in the TestAssembler
        Resource fake = ResourceFactory.newFileResource("FAKE");
        ckBuilder.add(fake, ResourceType.DMN);

        // the test assembler runs _before_ rules execution and leaves a result in the list
        ckBuilder.build();


        KnowledgeBuilderResults results = kBuilder.getResults(ResultSeverity.INFO);
        assertEquals(2, results.size());
        Object[] objects = results.toArray();
        assertEquals(TestAssembler.BEFORE_RULES, objects[0]);
        assertEquals(TestAssembler.AFTER_RULES, objects[1]);
    }
}