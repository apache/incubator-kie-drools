package org.drools.adventures;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.lf5.util.StreamUtils;
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseConfiguration;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.common.InternalFactHandle;
import org.drools.common.InternalRuleBase;
import org.drools.conf.AssertBehaviorOption;
import org.drools.impl.KnowledgeBaseImpl;
import org.drools.io.ResourceFactory;
import org.drools.runtime.Channel;
import org.drools.runtime.KnowledgeSessionConfiguration;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.rule.FactHandle;
import org.drools.runtime.rule.QueryResults;
import org.drools.runtime.rule.QueryResultsRow;
import org.drools.runtime.rule.Variable;
import org.mvel2.MVEL;
import org.mvel2.integration.VariableResolverFactory;
import org.mvel2.integration.impl.MapVariableResolverFactory;

public class DeathMansion {
    public void test1() throws Exception {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newClassPathResource( "model.drl", getClass() ),
                      ResourceType.DRL );      
        if ( kbuilder.hasErrors() ) {
            System.out.println( kbuilder.getErrors().toString() );
            System.exit(1);
        }
        
        kbuilder.add( ResourceFactory.newClassPathResource( "./commands/commands-model.drl", getClass() ),
                      ResourceType.DRL );        
        if ( kbuilder.hasErrors() ) {
            System.out.println( kbuilder.getErrors().toString() );
            System.exit(1);
        }
        
        kbuilder.add( ResourceFactory.newClassPathResource( "queries.drl", getClass() ),
                      ResourceType.DRL );      
        if ( kbuilder.hasErrors() ) {
            System.out.println( kbuilder.getErrors().toString() );
            System.exit(1);
        }
        
        kbuilder.add( ResourceFactory.newClassPathResource( "commands.drl", getClass() ),
                      ResourceType.DRL );          
        if ( kbuilder.hasErrors() ) {
            System.out.println( kbuilder.getErrors().toString() );
            System.exit(1);
        }


        KnowledgeBaseConfiguration kbaseConf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        kbaseConf.setOption( AssertBehaviorOption.EQUALITY );
        
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase( kbaseConf );
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        
//        KnowledgeSessionConfiguration ksessionConf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
//        ksessionConf.setOption( AssertBehaviorOption.EQUALITY );
        
        Counter c = new Counter();
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        ksession.setGlobal( "counter",c );
        
//        ksession.getChannels().put( "io", new Channel() {            
//            public void send(Object arg) {
//                System.out.println(arg);
//            }
//        } );
        
        
        Thread.currentThread().setContextClassLoader( (((InternalRuleBase)((KnowledgeBaseImpl) kbase).ruleBase).getRootClassLoader() )  );        
        Map vars = new HashMap();
        vars.put("c", c);
        Map<String,Map> map = (Map<String,Map>) MVEL.executeExpression( MVEL.compileExpression(new String( StreamUtils.getBytes(  getClass().getResource( "data.mvel" ).openStream() ) ) ), vars);
        
        for ( Object o : map.get( "rooms" ).values() ) {
            ksession.insert( o );
        }
        
        for ( Object o : map.get( "doors" ).values() ) {
            ksession.insert( o );
        }         
        
        for ( Object o : map.get( "characters" ).values() ) {
            ksession.insert( o );
        }           
        
        for ( Object o : map.get( "items" ).values() ) {
            ksession.insert( o );
        } 
        
        for ( Object o : map.get( "locations" ).values() ) {
            ksession.insert( o );
        }       
        
        Map<String, Integer> things = ( Map<String, Integer> ) map.get( "vars" );
               
        String baseStr = "import  org.drools.adventures.*;  import org.drools.adventures.commands.*;\n";
        
        MapVariableResolverFactory f = new MapVariableResolverFactory( map.get( "vars") );       
        
        FactHandle fh = null;
        
        fh = ksession.insert(new Request(  MVEL.eval( baseStr + "new LookCommand( characters['hero'] )", f) ) );
        ksession.fireAllRules();
        
        fh = ksession.insert(new Request(  MVEL.eval( baseStr + "new MoveCommand( characters['hero'], rooms['basement'] )", f) ) );
        ksession.fireAllRules();
        
        fh = ksession.insert( new Request( MVEL.eval( baseStr + "new MoveCommand( characters['hero'], rooms['kitchen'] )", f) ) );        
        ksession.fireAllRules();

        fh = ksession.insert(new Request(  MVEL.eval( baseStr + "new LookCommand( characters['hero'] )", f) ) );
        ksession.fireAllRules();                      
        
        fh = ksession.insert( new Request( MVEL.eval( baseStr + "new MoveCommand( characters['hero'], rooms['basement'] )", f ) ) );
        ksession.fireAllRules( );     
        
        fh = ksession.insert(new Request(  MVEL.eval( baseStr + "new LookCommand( characters['hero'] )", f) ) );
        ksession.fireAllRules();        
        
        
        fh = ksession.insert(new Request(  MVEL.eval( baseStr + "new PickupCommand( characters['hero'], items['torch'] )", f) ) );
        ksession.fireAllRules( );
        
        fh = ksession.insert( new Request( MVEL.eval( baseStr + "new PickupCommand( characters['hero'], items['mace'] )", f ) ) );        
        ksession.fireAllRules();        

        fh = ksession.insert(new Request(  MVEL.eval( baseStr + "new LookCommand( characters['hero'] )", f) ) );
        ksession.fireAllRules();
        
        fh = ksession.insert(new Request(  MVEL.eval( baseStr + "new MoveCommand( characters['hero'], rooms['kitchen'] )", f ) ) );        
        ksession.fireAllRules();
               
        fh = ksession.insert(new Request(  MVEL.eval( baseStr + "new LookCommand( characters['hero'] )", f) ) );
        ksession.fireAllRules();
        
        fh = ksession.insert( new Request( MVEL.eval( baseStr + "new DropCommand( characters['hero'], items['mace'] )", f ) ) );        
        ksession.fireAllRules();        

        fh = ksession.insert(new Request(  MVEL.eval( baseStr + "new LookCommand( characters['hero'] )", f) ) );
        ksession.fireAllRules();        
    }  
    
}
