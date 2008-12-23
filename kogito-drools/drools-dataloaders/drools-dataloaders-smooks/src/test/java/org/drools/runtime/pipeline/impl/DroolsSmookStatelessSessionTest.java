package org.drools.runtime.pipeline.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.stream.StreamSource;

import junit.framework.TestCase;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.definition.pipeline.Expression;
import org.drools.definition.pipeline.PipelineFactory;
import org.drools.definition.pipeline.Splitter;
import org.drools.definition.pipeline.Transformer;
import org.drools.io.ResourceFactory;
import org.drools.runtime.StatelessKnowledgeSession;
import org.drools.runtime.dataloader.StatelessKnowledgeSessionDataLoader;
import org.drools.runtime.dataloader.impl.StatelessKnowledgeSessionDataLoaderImpl;
import org.milyn.Smooks;
import org.milyn.io.StreamUtils;

public class DroolsSmookStatelessSessionTest extends TestCase {

    public void testSmooksDirectRoot() throws Exception {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();

        kbuilder.add( ResourceFactory.newClassPathResource( "test_SmooksDirectRoot.drl",
                                                            DroolsSmookStatefulSessionTest.class ),
                      ResourceType.DRL );

        assertFalse( kbuilder.hasErrors() );

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        StatelessKnowledgeSession ksession = kbase.newStatelessKnowledgeSession();
        List list = new ArrayList();
        ksession.setGlobal( "list",
                            list );

        // Instantiate Smooks with the config...
        Smooks smooks = new Smooks( getClass().getResourceAsStream( "smooks-config.xml" ) );

        Transformer transformer = PipelineFactory.newSmooksTransformer( smooks, "orderItem" );       
        transformer.addReceiver( PipelineFactory.newStatelessKnowledgeSessionReceiverAdapter() );

        StatelessKnowledgeSessionDataLoader dataLoader = new StatelessKnowledgeSessionDataLoaderImpl( ksession,
                                                                                                  transformer );
        dataLoader.executeObject( new StreamSource( getClass().getResourceAsStream( "SmooksDirectRoot.xml" ) ) );

        assertEquals( 1,
                      list.size() );

        assertEquals( "example.OrderItem",
                      list.get( 0 ).getClass().getName() );
    }

    public void testSmooksNestedIterable() throws Exception {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();

        kbuilder.add( ResourceFactory.newClassPathResource( "test_SmooksNestedIterable.drl",
                                                            DroolsSmookStatefulSessionTest.class ),
                      ResourceType.DRL );

        assertFalse( kbuilder.hasErrors() );

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        StatelessKnowledgeSession ksession = kbase.newStatelessKnowledgeSession();
        List list = new ArrayList();
        ksession.setGlobal( "list",
                            list );

        // Instantiate Smooks with the config...
        Smooks smooks = new Smooks( getClass().getResourceAsStream( "smooks-config.xml" ) );

        Transformer transformer = PipelineFactory.newSmooksTransformer( smooks, "root" );               
        Expression expression = PipelineFactory.newMvelExpression( "children" );
        transformer.addReceiver( expression );
        Splitter splitter = PipelineFactory.newIterateSplitter();
        expression.addReceiver( splitter );
        splitter.addReceiver( PipelineFactory.newStatelessKnowledgeSessionReceiverAdapter() );

        StatelessKnowledgeSessionDataLoader dataLoader = new StatelessKnowledgeSessionDataLoaderImpl( ksession,
                                                                                                  transformer );
        dataLoader.executeIterable( new StreamSource( getClass().getResourceAsStream( "SmooksNestedIterable.xml" ) ) );

        assertEquals( 2,
                      list.size() );

        assertEquals( "example.OrderItem",
                      list.get( 0 ).getClass().getName() );
        assertEquals( "example.OrderItem",
                      list.get( 1 ).getClass().getName() );

        assertNotSame( list.get( 0 ),
                       list.get( 1 ) );
    }   

    private static byte[] readInputMessage(InputStream stream) {
        try {
            return StreamUtils.readStream( stream );
        } catch ( IOException e ) {
            e.printStackTrace();
            return "<no-message/>".getBytes();
        }
    }
}
