package org.drools.decisiontable;

import com.sample.FactData;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

public class LineBreakXLSTest {

    @Test
    public void makeSureAdditionalCodeLineEndsAreNotAdded() {

        DecisionTableConfiguration dtconf = KnowledgeBuilderFactory.newDecisionTableConfiguration();
        dtconf.setInputType(DecisionTableInputType.XLSX);
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newClassPathResource("testrule.xlsx", getClass()), ResourceType.DTABLE, dtconf);
        if (kbuilder.hasErrors()) {
            fail(kbuilder.getErrors().toString());
        }
        InternalKnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addPackages(kbuilder.getKnowledgePackages());

        KieSession ksession = kbase.newKieSession();

        FactData fd = new FactData();
        fd.set値(-1);
        ksession.insert(fd);
        ksession.fireAllRules();

        ksession.dispose();

        assertThat(fd.getエラーメッセージ().contains("値には0以上を指定してください。\n指定された値：")).isTrue();
    }

    @Test
    public void testMultipleLinesInAction() {

        // DROOLS-3805
        DecisionTableConfiguration dtconf = KnowledgeBuilderFactory.newDecisionTableConfiguration();
        dtconf.setInputType(DecisionTableInputType.XLS);
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newClassPathResource("MultiLinesInAction.xls", getClass()), ResourceType.DTABLE, dtconf);
        if (kbuilder.hasErrors()) {
            fail(kbuilder.getErrors().toString());
        }
        InternalKnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addPackages(kbuilder.getKnowledgePackages());

        KieSession ksession = kbase.newKieSession();

        Person john = new Person("John");
        john.setAge(20);
        john.setAlive(true);
        ksession.insert(john);
        ksession.fireAllRules();

        ksession.dispose();

        assertThat(john.getAge()).isEqualTo(30);
        assertThat(john.isAlive()).isFalse();
    }

    @Test
    public void testMultipleLinesInCells() {
        // DROOLS-4358

        DecisionTableConfiguration dtconf = KnowledgeBuilderFactory.newDecisionTableConfiguration();
        dtconf.setInputType(DecisionTableInputType.XLS);
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newClassPathResource("MultiLinesInCells.xls", getClass()), ResourceType.DTABLE, dtconf);
        if (kbuilder.hasErrors()) {
            fail(kbuilder.getErrors().toString());
        }
        InternalKnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addPackages(kbuilder.getKnowledgePackages());

        KieSession ksession = kbase.newKieSession();

        Person john = new Person("John");
        john.setAge(20);
        john.setAlive(true);
        ksession.insert(john);
        ksession.fireAllRules();

        ksession.dispose();

        assertThat(john.getAge()).isEqualTo(30);
        assertThat(john.getName()).isEqualTo("ssss\nxxxx");
    }
}
