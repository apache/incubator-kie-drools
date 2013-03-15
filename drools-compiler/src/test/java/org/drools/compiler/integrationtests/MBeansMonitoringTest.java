package org.drools.compiler.integrationtests;

import java.io.StringReader;
import java.lang.management.ManagementFactory;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.kie.KnowledgeBase;
import org.kie.KieBaseConfiguration;
import org.kie.KnowledgeBaseFactory;
import org.kie.builder.KnowledgeBuilder;
import org.kie.builder.KnowledgeBuilderFactory;
import org.kie.conf.EventProcessingOption;
import org.kie.conf.MBeansOption;
import org.kie.io.ResourceFactory;
import org.kie.io.ResourceType;

public class MBeansMonitoringTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testEventOffset() throws InterruptedException,
                                 AttributeNotFoundException,
                                 InstanceNotFoundException,
                                 MalformedObjectNameException,
                                 MBeanException,
                                 ReflectionException,
                                 NullPointerException {
        String drl = "package org.drools.test\n" +
        		     "import org.drools.compiler.StockTick\n" +
                     "declare StockTick\n" +
                     "    @role(event)\n" +
                     "    @expires(10s)\n" +
                     "end\n" +
                     "rule X\n" +
                     "when\n" +
                     "    StockTick()\n" +
                     "then\n" +
                     "end";
        KieBaseConfiguration conf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        conf.setOption( EventProcessingOption.STREAM );
        conf.setOption( MBeansOption.ENABLED );

        KnowledgeBase kbase = loadKnowledgeBase( "monitoredKbase",
                                                 drl,
                                                 conf );

        MBeanServer mbserver = ManagementFactory.getPlatformMBeanServer();
        ObjectName kbOn = new ObjectName("org.drools.kbases:type=monitoredKbase");
        mbserver.invoke( kbOn, "startInternalMBeans", new Object[0], new String[0] );
        
        Object expOffset = mbserver.getAttribute( new ObjectName( "org.drools.kbases:type=monitoredKbase,group=EntryPoints,EntryPoint=DEFAULT,ObjectType=org.drools.compiler.StockTick"), "ExpirationOffset" );
        Assert.assertEquals( 10001, ((Number)expOffset).longValue() );
    }

    private KnowledgeBase loadKnowledgeBase( String id,
                                             String drl,
                                             KieBaseConfiguration conf ) {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newReaderResource( new StringReader( drl ) ),
                      ResourceType.DRL );
        Assert.assertFalse( kbuilder.getErrors().toString(),
                            kbuilder.hasErrors() );

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase( id,
                                                                     conf );
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );
        return kbase;
    }

}
