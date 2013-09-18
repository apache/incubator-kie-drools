package org.drools.compiler.integrationtests;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Message;
import org.kie.api.builder.Message.Level;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.runtime.KieSession;

public class NotInFusion {

    @Ignore
    @Test
    public void test() {
        
        KieServices ks = KieServices.Factory.get();
        
        KieFileSystem kfs = ks.newKieFileSystem();

        kfs.write(ks.getResources().newClassPathResource("notinfusion.drl", NotInFusion.class));
        
        KieModuleModel kmoduleModel = ks.newKieModuleModel();
        kmoduleModel.newKieBaseModel("defaultKieBase")
                .addPackage("*")
                .setDefault(true)
                .setEventProcessingMode(EventProcessingOption.STREAM);
        
        kfs.writeKModuleXML(kmoduleModel.toXML());

        KieBuilder kbuilder = ks.newKieBuilder(kfs);

        kbuilder.buildAll();
        
        List<Message> res = kbuilder.getResults().getMessages(Level.ERROR);
        
        assertEquals(res.toString(), 0, res.size());
        
        KieBaseConfiguration kbaseconf = ks.newKieBaseConfiguration();
        
        kbaseconf.setOption(EventProcessingOption.STREAM);
        
        KieSession ksession = ks.newKieContainer(kbuilder.getKieModule().getReleaseId()).newKieBase(kbaseconf).newKieSession();
        
        ArrayList<String> list = new ArrayList<String>();
        
        ksession.setGlobal("list", list);
        
        ksession.fireAllRules();
        
        assertEquals(1, list.size());
    }
}
