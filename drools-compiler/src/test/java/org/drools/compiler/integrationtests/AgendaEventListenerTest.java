package org.drools.compiler.integrationtests;

import java.util.concurrent.atomic.AtomicLong;

import org.drools.compiler.CommonTestMethodBase;
import org.drools.core.event.DefaultAgendaEventListener;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.builder.model.KieSessionModel;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.event.rule.AfterMatchFiredEvent;
import org.kie.api.event.rule.MatchCancelledEvent;
import org.kie.api.event.rule.MatchCreatedEvent;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.internal.runtime.conf.ForceEagerActivationOption;

public class AgendaEventListenerTest extends CommonTestMethodBase {
    @Test
    public void test1256() throws Exception {
        // DROOLS-1256
        String drl = "package org.drools.compiler.integrationtests\n" +
    
                "rule ND\n" +
                "when\n" +
                "    not ( Double() ) \n" +
                "then\n" +
                "    // do nothing. \n" +
                "end\n"+
                
                "rule ND2\n" +
                "salience 1\n" +
                "when\n" +
                "    not ( Double() ) \n" +
                "then\n" +
                "    insert( new Double(0) ); \n" +
                "end\n"
                
                ;
        KieServices ks = KieServices.Factory.get();

        KieModuleModel kproj = ks.newKieModuleModel();

        KieBaseModel kieBaseModel1 = kproj.newKieBaseModel( "kb" ).setDefault( true )
                .setEventProcessingMode( EventProcessingOption.STREAM );
        KieSessionModel ksessionModel1 = kieBaseModel1.newKieSessionModel( "ks" ).setDefault( true )
                .setType( KieSessionModel.KieSessionType.STATEFUL );

        ReleaseId releaseId1 = ks.newReleaseId( "org.kie.test", "AgendaEventListenerTest", "1.0.0" );
        createKJar( ks, kproj, releaseId1, null, drl );

        KieContainer kc = ks.newKieContainer( releaseId1 );

        KieSessionConfiguration ksConf = ks.newKieSessionConfiguration();
        ksConf.setOption( ForceEagerActivationOption.YES );
        KieSession ksession = kc.newKieSession( ksConf );
        
        final AtomicLong created   = new AtomicLong(0);
        final AtomicLong cancelled = new AtomicLong(0);
        final AtomicLong fired     = new AtomicLong(0);
        
        ksession.addEventListener(new DefaultAgendaEventListener() {
            @Override
            public void matchCreated(MatchCreatedEvent event) {
                created.incrementAndGet();
            }
            @Override
            public void matchCancelled(MatchCancelledEvent event) {
                cancelled.incrementAndGet();
            }
            @Override
            public void afterMatchFired(AfterMatchFiredEvent event) {
                fired.incrementAndGet();
            }
        });
        
        ksession.fireAllRules();
        
        System.out.println(" created: "+created.get() + " cancelled: "+cancelled.get() + " fired: "+fired.get());
        
        assertEquals(2, created.get());
        assertEquals(1, cancelled.get());
        assertEquals(1, fired.get());
    }
}
