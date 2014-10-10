package org.drools.compiler.kproject;

import org.drools.compiler.kproject.models.KieBaseModelImpl;
import org.drools.compiler.kproject.models.KieSessionModelImpl;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.model.FileLoggerModel;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.builder.model.KieSessionModel;
import org.kie.api.builder.model.KieSessionModel.KieSessionType;
import org.kie.api.builder.model.ListenerModel;
import org.kie.api.builder.model.WorkItemHandlerModel;
import org.kie.api.conf.DeclarativeAgendaOption;
import org.kie.api.conf.EqualityBehaviorOption;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.runtime.conf.BeliefSystemTypeOption;
import org.kie.api.runtime.conf.ClockTypeOption;
import org.kie.internal.builder.conf.LanguageLevelOption;
import org.kie.internal.builder.conf.PropertySpecificOption;

import java.util.List;

import static org.drools.compiler.kproject.models.KieModuleModelImpl.fromXML;
import static org.junit.Assert.*;

public class KieModuleModelTest {

    @Test
    public void testMarshallingUnmarshalling() {
        KieServices ks = KieServices.Factory.get();

        KieModuleModel kproj = ks.newKieModuleModel()
                                 .setConfigurationProperty(LanguageLevelOption.PROPERTY_NAME, LanguageLevelOption.DRL6_STRICT.toString())
                                 .setConfigurationProperty(PropertySpecificOption.PROPERTY_NAME, PropertySpecificOption.ALWAYS.toString());

        KieBaseModel kieBaseModel1 = kproj.newKieBaseModel("KBase1")
                .setEqualsBehavior( EqualityBehaviorOption.EQUALITY )
                .setEventProcessingMode( EventProcessingOption.STREAM )
                .setDeclarativeAgenda( DeclarativeAgendaOption.ENABLED )
                .addInclude("OtherKBase")
                .addPackage("org.kie.pkg1")
                .addPackage("org.kie.pkg2");

        KieSessionModel ksession1 = kieBaseModel1.newKieSessionModel("KSession1")
                .setType(KieSessionType.STATEFUL)
                .setClockType(ClockTypeOption.get("realtime"))
                .setBeliefSystem(BeliefSystemTypeOption.get("jtms"))
                .setFileLogger("drools.log", 10, true)
                .setDefault(true);

        ksession1.newListenerModel("org.domain.FirstInterface", ListenerModel.Kind.AGENDA_EVENT_LISTENER);

        ksession1.newListenerModel("org.domain.SecondInterface", ListenerModel.Kind.RULE_RUNTIME_EVENT_LISTENER)
                .newQualifierModel("MyQualfier2");

        ksession1.newListenerModel("org.domain.ThirdInterface", ListenerModel.Kind.PROCESS_EVENT_LISTENER)
                .newQualifierModel("MyQualfier3")
                .setValue("v1");

        ksession1.newListenerModel("org.domain.FourthInterface", ListenerModel.Kind.AGENDA_EVENT_LISTENER)
                .newQualifierModel("MyQualfier4")
                .addArgument("name1", "xxxx")
                .addArgument("name2", "yyyy");

        ksession1.newWorkItemHandlerModel("name","org.domain.FifthInterface")
                .newQualifierModel("MyQualfier5")
                .addArgument("name1", "aaa")
                .addArgument("name2", "bbb");
        
//        KieBaseModel kieBaseModel2 = kproj.newKieBaseModel("KBase2")
//                .setEqualsBehavior( EqualityBehaviorOption.EQUALITY )
//                .setEventProcessingMode( EventProcessingOption.STREAM );        

        String xml = kproj.toXML();

        // System.out.println( xml );

        KieModuleModel kprojXml = fromXML(xml);

        assertEquals(LanguageLevelOption.DRL6_STRICT.toString(), kprojXml.getConfigurationProperty(LanguageLevelOption.PROPERTY_NAME));
        assertEquals(PropertySpecificOption.ALWAYS.toString(), kprojXml.getConfigurationProperty(PropertySpecificOption.PROPERTY_NAME));

        KieBaseModel kieBaseModelXML = kprojXml.getKieBaseModels().get("KBase1");
        assertSame(kprojXml, ((KieBaseModelImpl)kieBaseModelXML).getKModule());
        assertEquals(EqualityBehaviorOption.EQUALITY, kieBaseModelXML.getEqualsBehavior());
        assertEquals(EventProcessingOption.STREAM, kieBaseModelXML.getEventProcessingMode());
        assertEquals(DeclarativeAgendaOption.ENABLED, kieBaseModelXML.getDeclarativeAgenda());
        assertFalse(kieBaseModelXML.isDefault());
        assertEquals("org.kie.pkg1", kieBaseModelXML.getPackages().get(0));
        assertEquals("org.kie.pkg2", kieBaseModelXML.getPackages().get(1));
        assertEquals("OtherKBase", ((KieBaseModelImpl) kieBaseModelXML).getIncludes().iterator().next());

        KieSessionModel kieSessionModelXML = kieBaseModelXML.getKieSessionModels().get("KSession1");
        assertSame(kieBaseModelXML, ((KieSessionModelImpl)kieSessionModelXML).getKieBaseModel());
        assertEquals(KieSessionType.STATEFUL, kieSessionModelXML.getType());
        assertEquals(ClockTypeOption.get("realtime"), kieSessionModelXML.getClockType());
        assertEquals(BeliefSystemTypeOption.get("jtms"), kieSessionModelXML.getBeliefSystem());

        FileLoggerModel fileLogger = kieSessionModelXML.getFileLogger();
        assertEquals("drools.log", fileLogger.getFile());
        assertEquals(10, fileLogger.getInterval());
        assertEquals(true, fileLogger.isThreaded());

        assertTrue(kieSessionModelXML.isDefault());

        List<ListenerModel> listeners = kieSessionModelXML.getListenerModels();

        ListenerModel listener2 = listeners.get(0);
        assertEquals("org.domain.SecondInterface", listener2.getType());
        assertEquals(ListenerModel.Kind.RULE_RUNTIME_EVENT_LISTENER, listener2.getKind());
        // QualifierModel qualifier2 = listener2.getQualifierModel();
        // assertEquals("MyQualfier2", qualifier2.getType());

        ListenerModel listener1 = listeners.get(1);
        assertEquals("org.domain.FirstInterface", listener1.getType());
        assertEquals(ListenerModel.Kind.AGENDA_EVENT_LISTENER, listener1.getKind());
        // assertNull(listener1.getQualifierModel());

        ListenerModel listener4 = listeners.get(2);
        assertEquals("org.domain.FourthInterface", listener4.getType());
        assertEquals(ListenerModel.Kind.AGENDA_EVENT_LISTENER, listener4.getKind());
        // QualifierModel qualifier4 = listener4.getQualifierModel();
        // assertEquals("MyQualfier4", qualifier4.getType());
        // assertEquals("xxxx", qualifier4.getArguments().get("name1"));
        // assertEquals("yyyy", qualifier4.getArguments().get("name2"));

        ListenerModel listener3 = listeners.get(3);
        assertEquals("org.domain.ThirdInterface", listener3.getType());
        assertEquals(ListenerModel.Kind.PROCESS_EVENT_LISTENER, listener3.getKind());
        // QualifierModel qualifier3 = listener3.getQualifierModel();
        // assertEquals("MyQualfier3", qualifier3.getType());
        // assertEquals("v1", qualifier3.getValue());

        WorkItemHandlerModel wihm = kieSessionModelXML.getWorkItemHandlerModels().get(0);
        assertEquals("org.domain.FifthInterface", wihm.getType());
        // QualifierModel qualifier5 = wihm.getQualifierModel();
        // assertEquals("MyQualfier5", qualifier5.getType());
        // assertEquals("aaa", qualifier5.getArguments().get("name1"));
        // assertEquals("bbb", qualifier5.getArguments().get("name2"));
    }
}
