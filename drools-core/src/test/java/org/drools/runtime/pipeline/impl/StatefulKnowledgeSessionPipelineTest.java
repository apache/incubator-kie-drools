package org.drools.runtime.pipeline.impl;

import java.util.Map;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.pipeline.ResultHandler;

import junit.framework.TestCase;

public class StatefulKnowledgeSessionPipelineTest extends TestCase {
    public void testInsertObject() {
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        
        StatefulKnowledgeSessionInsertStage stage1 = new StatefulKnowledgeSessionInsertStage();
        MvelAction mvelAction = new MvelAction( "context.resultHandler.handleResult( context.handles )");
        stage1.setReceiver( mvelAction );
        
        StatefulKnowledgeSessionPipelineImpl pipeline = new StatefulKnowledgeSessionPipelineImpl(ksession);
        pipeline.setReceiver( stage1 );
        
        assertEquals( 0, ksession.getObjects().size() );
        
        ResultHandlerImpl resultHanadle = new ResultHandlerImpl();
        pipeline.insert( "Hello", resultHanadle );
        
        assertEquals( 1, resultHanadle.getHandles().size() );              
    }
    
    public static class ResultHandlerImpl implements ResultHandler {
        Map handles;
        public void handleResult(Object object) {
           this.handles = ( Map ) object;             
        }
        public Map getHandles() {
            return this.handles;
        }
        
    }
}
