package org.drools.compiler.integrationtests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Message;
import org.kie.api.builder.Message.Level;

public class IsATest {

    @Ignore
    @Test
    public void test() {
        
        KieServices ks = KieServices.Factory.get();
        
        KieFileSystem kfs = ks.newKieFileSystem();

        kfs.write(ks.getResources().newClassPathResource("isA.drl", IsATest.class));

        KieBuilder kbuilder = ks.newKieBuilder(kfs);

        kbuilder.buildAll();

        List<Message> res = kbuilder.getResults().getMessages(Level.ERROR);

        assertEquals(res.toString(), 0, res.size());
        
        ks.newKieContainer(kbuilder.getKieModule().getReleaseId()).getKieBase();
        
        assertNotNull( ks.newKieContainer(kbuilder.getKieModule().getReleaseId()).newKieSession() );
    }
    
    public class Person {
        
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Person(String name) {
            this.name = name;
        }
    } 
}
