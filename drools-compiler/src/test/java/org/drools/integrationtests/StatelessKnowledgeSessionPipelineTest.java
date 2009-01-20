package org.drools.integrationtests;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.StatelessSessionResult;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.io.ResourceFactory;
import org.drools.runtime.Parameters;
import org.drools.runtime.StatelessKnowledgeSession;
import org.drools.runtime.StatelessKnowledgeSessionResults;
import org.drools.runtime.pipeline.Action;
import org.drools.runtime.pipeline.KnowledgeRuntimeCommand;
import org.drools.runtime.pipeline.Pipeline;
import org.drools.runtime.pipeline.PipelineFactory;
import org.drools.runtime.pipeline.ResultHandler;
import org.drools.runtime.pipeline.impl.MvelAction;
import org.drools.runtime.pipeline.impl.StatefulKnowledgeSessionInsertStage;
import org.drools.runtime.pipeline.impl.StatelessKnowledgeSessionExecuteStage;
import org.drools.runtime.pipeline.impl.StatelessKnowledgeSessionPipelineImpl;

public class StatelessKnowledgeSessionPipelineTest extends TestCase {
    public void testExecuteObjectAsDefault() {
        String str = "";
        str += "package org.sample \n";
        str += "global java.util.List list; \n";
        str += "rule rule1 \n";
        str += "  when \n";
        str += "    s : String() \n";
        str += "  then \n";
        str += "    list.add( s ); ";
        str += "end\n";
        
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ), ResourceType.DRL );

        if ( kbuilder.hasErrors() ) {
            System.out.println( kbuilder.getErrors() );
        }
        assertFalse( kbuilder.hasErrors() );
        
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );
        StatelessKnowledgeSession ksession = kbase.newStatelessKnowledgeSession();
        List list = new ArrayList();
        ksession.setGlobal( "list", list );
        
        
        StatelessKnowledgeSessionExecuteStage stage1 = new StatelessKnowledgeSessionExecuteStage();
        
        StatelessKnowledgeSessionPipelineImpl pipeline = new StatelessKnowledgeSessionPipelineImpl(ksession);
        pipeline.setReceiver( stage1 );
        
        pipeline.insert( "hello world", null );
        
        assertEquals( 1, list.size() );
        
        assertEquals( "hello world", list.get( 0 ) );           
    }
    
    public void testExecuteObject() {
        String str = "";
        str += "package org.sample \n";
        str += "global java.util.List list; \n";
        str += "rule rule1 \n";
        str += "  when \n";
        str += "    s : String() \n";
        str += "  then \n";
        str += "    list.add( s ); ";
        str += "end\n";
        
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ), ResourceType.DRL );

        if ( kbuilder.hasErrors() ) {
            System.out.println( kbuilder.getErrors() );
        }
        assertFalse( kbuilder.hasErrors() );
        
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );
        StatelessKnowledgeSession ksession = kbase.newStatelessKnowledgeSession();
        List list = new ArrayList();
        ksession.setGlobal( "list", list );
        
        
        MvelAction mvelAction = new MvelAction( "context.object = this");
        StatelessKnowledgeSessionExecuteStage stage1 = new StatelessKnowledgeSessionExecuteStage();
        mvelAction.setReceiver( stage1 );
        
        StatelessKnowledgeSessionPipelineImpl pipeline = new StatelessKnowledgeSessionPipelineImpl(ksession);
        pipeline.setReceiver( mvelAction );
        
        pipeline.insert( "hello world", null );
        
        assertEquals( 1, list.size() );
        
        assertEquals( "hello world", list.get( 0 ) );           
    }
    
    public void testExecuteIterable() {
        String str = "";
        str += "package org.sample \n";
        str += "global java.util.List list; \n";
        str += "rule rule1 \n";
        str += "  when \n";
        str += "    s : String() \n";
        str += "  then \n";
        str += "    list.add( s ); ";
        str += "end\n";
        
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ), ResourceType.DRL );

        if ( kbuilder.hasErrors() ) {
            System.out.println( kbuilder.getErrors() );
        }
        assertFalse( kbuilder.hasErrors() );
        
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );
        StatelessKnowledgeSession ksession = kbase.newStatelessKnowledgeSession();
        List list = new ArrayList();
        ksession.setGlobal( "list", list );
        
        
        MvelAction mvelAction = new MvelAction( "context.setIterable( this )");
        StatelessKnowledgeSessionExecuteStage stage1 = new StatelessKnowledgeSessionExecuteStage();
        mvelAction.setReceiver( stage1 );
        
        StatelessKnowledgeSessionPipelineImpl pipeline = new StatelessKnowledgeSessionPipelineImpl(ksession);
        pipeline.setReceiver( mvelAction );
        
        List items = new ArrayList();
        items.add( "hello world" );
        items.add( "goodbye world" );
        
        pipeline.insert( items, null );
        
        assertEquals( 2, list.size() );
        
        Collections.sort( list );        
        assertEquals( "goodbye world", list.get( 0 ) );
        assertEquals( "hello world", list.get( 1 ) );
    }    
    
    public void testExecuteObjectAsDefaultWithParameters() {
        String str = "";
        str += "package org.sample \n";
        str += "global java.util.List list; \n";
        str += "rule rule1 \n";
        str += "  when \n";
        str += "    s : String() \n";
        str += "  then \n";
        str += "    list.add( s ); ";
        str += "end\n";
        
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ), ResourceType.DRL );

        if ( kbuilder.hasErrors() ) {
            System.out.println( kbuilder.getErrors() );
        }
        assertFalse( kbuilder.hasErrors() );
        
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );
        StatelessKnowledgeSession ksession = kbase.newStatelessKnowledgeSession();
        
        MvelAction mvelAction2 = new MvelAction( "context.parameters.globalParams.setInOut( ['list' : new java.util.ArrayList()] )");
        
        StatelessKnowledgeSessionExecuteStage stage1 = new StatelessKnowledgeSessionExecuteStage();
        mvelAction2.setReceiver( stage1 );
        
        MvelAction mvelAction3 = new MvelAction( "context.resultHandler.handleResult( context.result )");
        stage1.setReceiver( mvelAction3 );
        
        StatelessKnowledgeSessionPipelineImpl pipeline = new StatelessKnowledgeSessionPipelineImpl(ksession);
        pipeline.setReceiver( mvelAction2 );
        
        ResultHandlerImpl handler = new ResultHandlerImpl();
        
        pipeline.insert( "hello world", handler );
        
        List list = (List)handler.getStatelessKnowledgeSessionResults().getValue( "list" );
        
        assertEquals( 1, list.size() );
        
        assertEquals( "hello world", list.get( 0 ) );              
    }     
    
    public void testExecuteObjectWithParameters() {
        String str = "";
        str += "package org.sample \n";
        str += "global java.util.List list; \n";
        str += "rule rule1 \n";
        str += "  when \n";
        str += "    s : String() \n";
        str += "  then \n";
        str += "    list.add( s ); ";
        str += "end\n";
        
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ), ResourceType.DRL );

        if ( kbuilder.hasErrors() ) {
            System.out.println( kbuilder.getErrors() );
        }
        assertFalse( kbuilder.hasErrors() );
        
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );
        StatelessKnowledgeSession ksession = kbase.newStatelessKnowledgeSession();
        
        MvelAction mvelAction1 = new MvelAction( "context.object = this");
        MvelAction mvelAction2 = new MvelAction( "context.parameters.globalParams.setInOut( ['list' : new java.util.ArrayList()] )");
        
        mvelAction1.setReceiver( mvelAction2 );
        
        StatelessKnowledgeSessionExecuteStage stage1 = new StatelessKnowledgeSessionExecuteStage();
        mvelAction2.setReceiver( stage1 );
        
        MvelAction mvelAction3 = new MvelAction( "context.resultHandler.handleResult( context.result )");
        stage1.setReceiver( mvelAction3 );
        
        StatelessKnowledgeSessionPipelineImpl pipeline = new StatelessKnowledgeSessionPipelineImpl(ksession);
        pipeline.setReceiver( mvelAction1 );
        
        ResultHandlerImpl handler = new ResultHandlerImpl();
        
        pipeline.insert( "hello world", handler );
        
        List list = (List)handler.getStatelessKnowledgeSessionResults().getValue( "list" );
        
        assertEquals( 1, list.size() );
        
        assertEquals( "hello world", list.get( 0 ) );              
    }    
    
    public void testExecuteIterableWithParameters() {
        String str = "";
        str += "package org.sample \n";
        str += "global java.util.List list; \n";
        str += "rule rule1 \n";
        str += "  when \n";
        str += "    s : String() \n";
        str += "  then \n";
        str += "    list.add( s ); ";
        str += "end\n";
        
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ), ResourceType.DRL );

        if ( kbuilder.hasErrors() ) {
            System.out.println( kbuilder.getErrors() );
        }
        assertFalse( kbuilder.hasErrors() );
        
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );
        StatelessKnowledgeSession ksession = kbase.newStatelessKnowledgeSession();
        
        Action executeResultHandler = PipelineFactory.newExecuteResultHandler();
        
        KnowledgeRuntimeCommand execute = PipelineFactory.newStatelessKnowledgeSessionExecute();
        execute.setReceiver( executeResultHandler );
        
        Action assignParameters = PipelineFactory.newMvelAction( "context.parameters.globalParams.setInOut( ['list' : new java.util.ArrayList()] )");
        assignParameters.setReceiver( execute );
        
        Action assignIterable = PipelineFactory.newMvelAction( "context.setIterable( this )");                
        assignIterable.setReceiver( assignParameters );
                
        Pipeline pipeline = PipelineFactory.newStatelessKnowledgeSessionPipeline(ksession);
        pipeline.setReceiver( assignIterable );
        
        ResultHandlerImpl handler = new ResultHandlerImpl();
        
        List items = new ArrayList();
        items.add( "hello world" );
        items.add( "goodbye world" );
        
        pipeline.insert( items, handler );
        
        List list = (List)handler.getStatelessKnowledgeSessionResults().getValue( "list" );
        
        assertEquals( 2, list.size() );
        
        Collections.sort( list );        
        assertEquals( "goodbye world", list.get( 0 ) );
        assertEquals( "hello world", list.get( 1 ) );         
    }       
    
    public static class ResultHandlerImpl implements ResultHandler {        
        StatelessKnowledgeSessionResults results;
        
        public void handleResult(Object object) {
           this.results = ( StatelessKnowledgeSessionResults ) object;             
        }
        
        public StatelessKnowledgeSessionResults getStatelessKnowledgeSessionResults() {
            return this.results;
        }
        
    }
}
