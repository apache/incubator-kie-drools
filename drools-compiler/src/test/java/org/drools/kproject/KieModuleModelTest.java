package org.drools.kproject;

import org.drools.kproject.models.KieBaseModelImpl;
import org.drools.kproject.models.KieSessionModelImpl;
import org.junit.Test;
import org.kie.builder.KieBaseModel;
import org.kie.builder.KieFactory;
import org.kie.builder.KieModuleModel;
import org.kie.builder.KieSessionModel;
import org.kie.builder.ListenerModel;
import org.kie.builder.QualifierModel;
import org.kie.builder.WorkItemHandlerModel;
import org.kie.builder.KieSessionModel.KieSessionType;
import org.kie.conf.AssertBehaviorOption;
import org.kie.conf.EventProcessingOption;
import org.kie.runtime.conf.ClockTypeOption;

import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.drools.kproject.models.KieModuleModelImpl.fromXML;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

public class KieModuleModelTest {

    @Test
    public void testMarshallingUnmarshalling() {
        KieFactory kf = KieFactory.Factory.get();

        KieModuleModel kproj = kf.newKieModuleModel();

        KieBaseModel kieBaseModel1 = kproj.newKieBaseModel("KBase1")
                .setEqualsBehavior( AssertBehaviorOption.EQUALITY )
                .setEventProcessingMode( EventProcessingOption.STREAM );

        KieSessionModel ksession1 = kieBaseModel1.newKieSessionModel("KSession1")
                .setType(KieSessionType.STATEFUL)
                .setClockType( ClockTypeOption.get("realtime") );

        ksession1.newListenerModel("org.domain.FirstInterface", ListenerModel.Kind.AGENDA_EVENT_LISTENER);

        ksession1.newListenerModel("org.domain.SecondInterface", ListenerModel.Kind.WORKING_MEMORY_EVENT_LISTENER)
                .newQualifierModel("MyQualfier2");

        ksession1.newListenerModel("org.domain.ThirdInterface", ListenerModel.Kind.PROCESS_EVENT_LISTENER)
                .newQualifierModel("MyQualfier3")
                .setValue("v1");

        ksession1.newListenerModel("org.domain.FourthInterface", ListenerModel.Kind.AGENDA_EVENT_LISTENER)
                .newQualifierModel("MyQualfier4")
                .addArgument("name1", "xxxx")
                .addArgument("name2", "yyyy");

        ksession1.newWorkItemHandelerModel("org.domain.FifthInterface")
                .newQualifierModel("MyQualfier5")
                .addArgument("name1", "aaa")
                .addArgument("name2", "bbb");
        
//        KieBaseModel kieBaseModel2 = kproj.newKieBaseModel("KBase2")
//                .setEqualsBehavior( AssertBehaviorOption.EQUALITY )
//                .setEventProcessingMode( EventProcessingOption.STREAM );        

        String xml = kproj.toXML();

        // System.out.println( xml );

        KieModuleModel kprojXml = fromXML(xml);

        KieBaseModel kieBaseModelXML = kprojXml.getKieBaseModels().get("KBase1");
        assertSame(kprojXml, ((KieBaseModelImpl)kieBaseModelXML).getKModule());
        assertEquals(AssertBehaviorOption.EQUALITY, kieBaseModelXML.getEqualsBehavior());
        assertEquals(EventProcessingOption.STREAM, kieBaseModelXML.getEventProcessingMode());

        KieSessionModel kieSessionModelXML = kieBaseModelXML.getKieSessionModels().get("KSession1");
        assertSame(kieBaseModelXML, ((KieSessionModelImpl)kieSessionModelXML).getKieBaseModel());
        assertEquals("STATEFUL", kieSessionModelXML.getType());
        assertEquals(ClockTypeOption.get("realtime"), kieSessionModelXML.getClockType());

        List<ListenerModel> listeners = kieSessionModelXML.getListenerModels();

        ListenerModel listener1 = listeners.get(0);
        assertEquals("org.domain.FirstInterface", listener1.getType());
        assertEquals(ListenerModel.Kind.AGENDA_EVENT_LISTENER, listener1.getKind());
        assertNull(listener1.getQualifierModel());

        ListenerModel listener2 = listeners.get(1);
        assertEquals("org.domain.SecondInterface", listener2.getType());
        assertEquals(ListenerModel.Kind.WORKING_MEMORY_EVENT_LISTENER, listener2.getKind());
        QualifierModel qualifier2 = listener2.getQualifierModel();
        assertEquals("MyQualfier2", qualifier2.getType());

        ListenerModel listener3 = listeners.get(2);
        assertEquals("org.domain.ThirdInterface", listener3.getType());
        assertEquals(ListenerModel.Kind.PROCESS_EVENT_LISTENER, listener3.getKind());
        QualifierModel qualifier3 = listener3.getQualifierModel();
        assertEquals("MyQualfier3", qualifier3.getType());
        assertEquals("v1", qualifier3.getValue());

        ListenerModel listener4 = listeners.get(3);
        assertEquals("org.domain.FourthInterface", listener4.getType());
        assertEquals(ListenerModel.Kind.AGENDA_EVENT_LISTENER, listener4.getKind());
        QualifierModel qualifier4 = listener4.getQualifierModel();
        assertEquals("MyQualfier4", qualifier4.getType());
        assertEquals("xxxx", qualifier4.getArguments().get("name1"));
        assertEquals("yyyy", qualifier4.getArguments().get("name2"));

        WorkItemHandlerModel wihm = kieSessionModelXML.getWorkItemHandelerModels().get(0);
        assertEquals("org.domain.FifthInterface", wihm.getType());
        QualifierModel qualifier5 = wihm.getQualifierModel();
        assertEquals("MyQualfier5", qualifier5.getType());
        assertEquals("aaa", qualifier5.getArguments().get("name1"));
        assertEquals("bbb", qualifier5.getArguments().get("name2"));

    }
}
