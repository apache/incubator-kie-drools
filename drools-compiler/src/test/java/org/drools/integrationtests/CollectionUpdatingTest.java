package org.drools.integrationtests;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.drools.Cheese;
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.io.ResourceFactory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.rule.FactHandle;
import org.junit.Test;

public class CollectionUpdatingTest {

    @Test
    public void testFunctionException() throws Exception {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newClassPathResource("test_CollectionUpdating.drl", getClass()), ResourceType.DRL);
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase(KnowledgeBaseFactory.newKnowledgeBaseConfiguration());
        kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        Collection<Cheese> collection = new ArrayList<Cheese>();
        
        // 4 cheese into collection
        for (int i = 0; i < 4; i++) {
            collection.add(new Cheese("brie " + i, 12));
        }

        FactHandle handle = ksession.insert(collection);

        List<Cheese> results = new ArrayList<Cheese>();
        ksession.setGlobal("results", results);

        // before firing rules, no cheese
        assertEquals(0, results.size());

        ksession.fireAllRules();

        // after firing rules, 4 pieces of cheese
        assertEquals(4, results.size());

        collection.add(new Cheese("brie extra", 12));
        ksession.update(handle, collection);

        // after adding an extra cheese and updating, should be 5 pieces of cheese
        assertEquals(5, results.size());
    }

}
