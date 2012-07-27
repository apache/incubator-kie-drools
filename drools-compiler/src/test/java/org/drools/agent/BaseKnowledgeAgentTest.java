package org.drools.agent;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.drools.CommonTestMethodBase;
import org.drools.KnowledgeBase;
import org.drools.agent.impl.KnowledgeAgentImpl;
import org.drools.core.util.DroolsStreamUtils;
import org.drools.core.util.FileManager;
import org.drools.core.util.IoUtils;
import org.drools.core.util.StringUtils;
import org.drools.event.knowledgeagent.AfterChangeSetAppliedEvent;
import org.drools.event.knowledgeagent.AfterChangeSetProcessedEvent;
import org.drools.event.knowledgeagent.AfterResourceProcessedEvent;
import org.drools.event.knowledgeagent.BeforeChangeSetAppliedEvent;
import org.drools.event.knowledgeagent.BeforeChangeSetProcessedEvent;
import org.drools.event.knowledgeagent.BeforeResourceProcessedEvent;
import org.drools.event.knowledgeagent.KnowledgeAgentEventListener;
import org.drools.event.knowledgeagent.KnowledgeBaseUpdatedEvent;
import org.drools.event.knowledgeagent.ResourceCompilationFailedEvent;
import org.drools.io.Resource;
import org.drools.io.ResourceFactory;
import org.drools.io.impl.ResourceChangeNotifierImpl;
import org.drools.io.impl.ResourceChangeScannerImpl;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.handler.ResourceHandler;

import org.junit.After;
import org.junit.Before;
import org.drools.builder.KnowledgeBuilderConfiguration;
import org.drools.builder.KnowledgeBuilderError;
import org.drools.builder.KnowledgeBuilderErrors;
import org.drools.builder.ResourceType;

public abstract class BaseKnowledgeAgentTest extends CommonTestMethodBase {

    FileManager     fileManager;
    Server           server;
    ResourceChangeScannerImpl scanner;

    @Before
    public void setUp() throws Exception {
        this.fileManager = new FileManager();
        this.fileManager.setUp();
        ((ResourceChangeScannerImpl) ResourceFactory.getResourceChangeScannerService()).reset();

        ResourceFactory.getResourceChangeNotifierService().start();

        // we don't start the scanner, as we call it manually;
        this.scanner = (ResourceChangeScannerImpl) ResourceFactory.getResourceChangeScannerService();

        this.server = new Server( IoUtils.findPort() );
        ResourceHandler resourceHandler = new ResourceHandler();
        resourceHandler.setResourceBase( fileManager.getRootDirectory().getPath() );

        this.server.setHandler( resourceHandler );

        this.server.start();
    }

    @After
    public void tearDown() throws Exception {
        fileManager.tearDown();
        ResourceFactory.getResourceChangeNotifierService().stop();
        ((ResourceChangeNotifierImpl) ResourceFactory.getResourceChangeNotifierService()).reset();
        ((ResourceChangeScannerImpl) ResourceFactory.getResourceChangeScannerService()).reset();

        server.stop();
    }

    public int getPort() {
        return this.server.getConnectors()[0].getLocalPort();
    }

    public void scan(KnowledgeAgent kagent) {
        // Calls the Resource Scanner and sets up a listener and a latch so we can wait until it's finished processing, instead of using timers
        final CountDownLatch latch = new CountDownLatch( 1 );

        final List<ResourceCompilationFailedEvent> resourceCompilationFailedEvents = new ArrayList<ResourceCompilationFailedEvent>();

        KnowledgeAgentEventListener l = new KnowledgeAgentEventListener() {

            public void resourceCompilationFailed(ResourceCompilationFailedEvent event) {
                //It is not correct to throw an exception from a listener becuase
                //it will interfere with the agent's logic.
                //throw new RuntimeException("Unable to compile Knowledge"+ event );

                //It is better to use a list and then check if it is empty.
                resourceCompilationFailedEvents.add(event);
            }

            public void knowledgeBaseUpdated(KnowledgeBaseUpdatedEvent event) {
            }

            public void beforeResourceProcessed(BeforeResourceProcessedEvent event) {
            }

            public void beforeChangeSetProcessed(BeforeChangeSetProcessedEvent event) {
            }

            public void beforeChangeSetApplied(BeforeChangeSetAppliedEvent event) {
            }

            public void afterResourceProcessed(AfterResourceProcessedEvent event) {
            }

            public void afterChangeSetProcessed(AfterChangeSetProcessedEvent event) {
            }

            public void afterChangeSetApplied(AfterChangeSetAppliedEvent event) {
                latch.countDown();
            }
        };

        kagent.addEventListener( l );

        this.scanner.scan();

        try {
            latch.await( 10, TimeUnit.SECONDS );
        } catch ( InterruptedException e ) {
            throw new RuntimeException( "Unable to wait for latch countdown", e);
        }

        if ( latch.getCount() > 0 ) {
            throw new RuntimeException( "Event for KnowledgeBase update, due to scan, was never received" );
        }

        kagent.removeEventListener( l );

        if (!resourceCompilationFailedEvents.isEmpty()){
            //A compilation error occurred
            throw new RuntimeException("Unable to compile Knowledge"+ resourceCompilationFailedEvents.get(0).getKnowledgeBuilder().getErrors() );
        }

    }
    
    void applyNamedResource(KnowledgeAgentImpl kagent, String name, Resource resource, ResourceType type){
        this.processNamedResource(kagent, name, resource, type, true);
    }
    
    void unapplyNamedResource(KnowledgeAgentImpl kagent, String name){
        this.processNamedResource(kagent, name, null, null, false);
    }
    
    
    private void processNamedResource(KnowledgeAgentImpl kagent, String name, Resource resource, ResourceType type, boolean apply){
        // Calls the Resource Scanner and sets up a listener and a latch so we can wait until it's finished processing, instead of using timers
        final CountDownLatch latch = new CountDownLatch( 1 );

        KnowledgeAgentEventListener l = new KnowledgeAgentEventListener() {

            public void resourceCompilationFailed(ResourceCompilationFailedEvent event) {
                Iterator<KnowledgeBuilderError> iterator = event.getKnowledgeBuilder().getErrors().iterator();
                while (iterator.hasNext()) {
                    KnowledgeBuilderError knowledgeBuilderError = iterator.next();
                    System.out.println(knowledgeBuilderError.getMessage());
                }
                throw new RuntimeException("Unable to compile Knowledge"+ event );
            }

            public void knowledgeBaseUpdated(KnowledgeBaseUpdatedEvent event) {
            }

            public void beforeResourceProcessed(BeforeResourceProcessedEvent event) {
            }

            public void beforeChangeSetProcessed(BeforeChangeSetProcessedEvent event) {
            }

            public void beforeChangeSetApplied(BeforeChangeSetAppliedEvent event) {
            }

            public void afterResourceProcessed(AfterResourceProcessedEvent event) {
            }

            public void afterChangeSetProcessed(AfterChangeSetProcessedEvent event) {
            }

            public void afterChangeSetApplied(AfterChangeSetAppliedEvent event) {
                latch.countDown();
            }
        };

        kagent.addEventListener( l );

        if (apply){
            kagent.applyNamedResource(name, resource, type);
        }else{
            kagent.unapplyNamedResource(name);
        }
        

        try {
            latch.await( 10, TimeUnit.SECONDS );
        } catch ( InterruptedException e ) {
            throw new RuntimeException( "Unable to wait for latch countdown", e);
        }

        if ( latch.getCount() > 0 ) {
            throw new RuntimeException( "Event for KnowlegeBase update, due to scan, was never received" );
        }

        kagent.removeEventListener( l );
    }

    void applyChangeSet(KnowledgeAgent kagent, String xml) {
        // Calls the Resource Scanner and sets up a listener and a latch so we can wait until it's finished processing, instead of using timers
        final CountDownLatch latch = new CountDownLatch( 1 );

        KnowledgeAgentEventListener l = new KnowledgeAgentEventListener() {

            public void resourceCompilationFailed(ResourceCompilationFailedEvent event) {
                throw new RuntimeException("Unable to compile Knowledge"+ event );
            }

            public void knowledgeBaseUpdated(KnowledgeBaseUpdatedEvent event) {
            }

            public void beforeResourceProcessed(BeforeResourceProcessedEvent event) {
            }

            public void beforeChangeSetProcessed(BeforeChangeSetProcessedEvent event) {
            }

            public void beforeChangeSetApplied(BeforeChangeSetAppliedEvent event) {
            }

            public void afterResourceProcessed(AfterResourceProcessedEvent event) {
            }

            public void afterChangeSetProcessed(AfterChangeSetProcessedEvent event) {
            }

            public void afterChangeSetApplied(AfterChangeSetAppliedEvent event) {
                latch.countDown();
            }
        };

        kagent.addEventListener( l );

        kagent.applyChangeSet( ResourceFactory.newByteArrayResource( xml.getBytes() ) );

        try {
            latch.await( 10, TimeUnit.SECONDS );
        } catch ( InterruptedException e ) {
            throw new RuntimeException( "Unable to wait for latch countdown", e);
        }

        if ( latch.getCount() > 0 ) {
            throw new RuntimeException( "Event for KnowlegeBase update, due to scan, was never received" );
        }

        kagent.removeEventListener( l );
    }

    void applyChangeSet(KnowledgeAgent kagent, Resource r) {
        // Calls the Resource Scanner and sets up a listener and a latch so we can wait until it's finished processing, instead of using timers
        final CountDownLatch latch = new CountDownLatch( 1 );

        KnowledgeAgentEventListener l = new KnowledgeAgentEventListener() {

            public void resourceCompilationFailed(ResourceCompilationFailedEvent event) {
                KnowledgeBuilderErrors errors = event.getKnowledgeBuilder().getErrors();
                if (errors != null){
                    Iterator<KnowledgeBuilderError> iterator = errors.iterator();
                    while (iterator.hasNext()) {
                        KnowledgeBuilderError knowledgeBuilderError = iterator.next();
                        System.err.println(knowledgeBuilderError.getMessage());
                    }
                }
                throw new RuntimeException("Unable to compile Knowledge"+ event );
            }

            public void knowledgeBaseUpdated(KnowledgeBaseUpdatedEvent event) {
            }

            public void beforeResourceProcessed(BeforeResourceProcessedEvent event) {
            }

            public void beforeChangeSetProcessed(BeforeChangeSetProcessedEvent event) {
            }

            public void beforeChangeSetApplied(BeforeChangeSetAppliedEvent event) {
            }

            public void afterResourceProcessed(AfterResourceProcessedEvent event) {
            }

            public void afterChangeSetProcessed(AfterChangeSetProcessedEvent event) {
            }

            public void afterChangeSetApplied(AfterChangeSetAppliedEvent event) {
                latch.countDown();
            }
        };

        kagent.addEventListener( l );

        kagent.applyChangeSet( r );

        try {
            latch.await( 10, TimeUnit.SECONDS );
        } catch ( InterruptedException e ) {
            throw new RuntimeException( "Unable to wait for latch countdown", e);
        }

        if ( latch.getCount() > 0 ) {
            throw new RuntimeException( "Event for KnowlegeBase update, due to scan, was never received" );
        }

        kagent.removeEventListener( l );
    }


    public static void writePackage(Object pkg,
                                     File p1file )throws IOException, FileNotFoundException {
        if ( p1file.exists() ) {
            // we want to make sure there is a time difference for lastModified and lastRead checks as Linux and http often round to seconds
            // http://saloon.javaranch.com/cgi-bin/ubb/ultimatebb.cgi?ubb=get_topic&f=1&t=019789
            try {
                Thread.sleep( 1000 );
            } catch (Exception e) {
                throw new RuntimeException( "Unable to sleep" );
            }
        }
        FileOutputStream out = new FileOutputStream( p1file );
        try {
            DroolsStreamUtils.streamOut( out,
                                         pkg );
        } finally {
            out.close();
        }
    }

    public KnowledgeAgent createKAgent(KnowledgeBase kbase) {
        return createKAgent( kbase, true );
    }



    public KnowledgeAgent createKAgent(KnowledgeBase kbase, boolean newInstance) {
        return this.createKAgent(kbase, newInstance, null, null);
    }

    public KnowledgeAgent createKAgent( KnowledgeBase kbase, boolean newInstance, boolean useKBaseClassLoaderForCompiling ) {
        return this.createKAgent(kbase, newInstance, useKBaseClassLoaderForCompiling, null);
    }

    public KnowledgeAgent createKAgent(KnowledgeBase kbase, boolean newInstance, Boolean useKBaseClassLoaderForCompiling, KnowledgeBuilderConfiguration builderConf) {
        KnowledgeAgentConfiguration aconf = KnowledgeAgentFactory.newKnowledgeAgentConfiguration();
        aconf.setProperty( "drools.agent.scanDirectories",
                           "true" );
        aconf.setProperty( "drools.agent.scanResources",
                           "true" );
        aconf.setProperty( "drools.agent.newInstance",
                           Boolean.toString( newInstance ) );
        if (useKBaseClassLoaderForCompiling != null){
            aconf.setProperty("drools.agent.useKBaseClassLoaderForCompiling",
                ""+useKBaseClassLoaderForCompiling);
        }

        KnowledgeAgent kagent = KnowledgeAgentFactory.newKnowledgeAgent("test agent",
                                                                         kbase,
                                                                         aconf,
                                                                         builderConf);

        assertEquals( "test agent",
                      kagent.getName() );

        return kagent;

    }

    public String createHeader(String packageName) {
        return this.createHeader(packageName, null);
    }

    public String createHeader(String packageName, String[] customImports) {
        StringBuilder header = new StringBuilder();
        if ( StringUtils.isEmpty( packageName ) ) {
            header.append( "package org.drools.test\n" );
        } else {
            header.append( "package " );
            header.append( packageName );
            header.append( "\n" );
        }
        header.append( "import org.drools.Person;\n" );
        if (customImports != null){
            for (String customImport : customImports) {
                header.append( "import " );
                header.append( customImport );
                header.append( ";\n" );
            }
        }
        header.append( "global java.util.List list\n" );

        return header.toString();
    }


    public String createVersionedRule(String packageName, String[] ruleNames, String attribute, String lhs, String version) {
        return createVersionedRule( true, packageName, ruleNames, attribute, lhs, version );
    }


    public String createCustomRule(boolean header, String packageName, String[] ruleNames, String attribute, String lhs, String rhs) {
        return this.createCustomRule(header, packageName, null, ruleNames, attribute, lhs, rhs);
    }

    public String createCustomRule(boolean header, String packageName,  String[] customImports, String[] ruleNames, String attribute, String lhs, String rhs) {
        StringBuilder rule = new StringBuilder();

        if ( header ) {
            rule.append( createHeader( packageName, customImports ) );
        }

        for (String ruleName : ruleNames ) {
            rule.append( "rule " );
            rule.append( ruleName );
            rule.append( "\n" );
            if ( !StringUtils.isEmpty( attribute ) ) {
                rule.append( attribute +"\n" );
            }
            rule.append( "when\n" );
            if ( !StringUtils.isEmpty( lhs ) ) {
                rule.append( lhs );
            }
            rule.append( "then\n" );
            rule.append( rhs );
            rule.append( "end\n\n" );
        }

        return rule.toString();
    }

    public String createVersionedRule(boolean header, String packageName, String[] ruleNames, String attribute, String lhs, String version) {
        String rhs = null;
        if ( StringUtils.isEmpty( version ) ) {
            rhs = "list.add( drools.getRule().getName() );\n";
        } else {
            rhs = "list.add( drools.getRule().getName()+\"-V" + version + "\");\n";
        }
        return createCustomRule(header, packageName, ruleNames, attribute, lhs, rhs  );
    }

    public String createLhsRule(String[] ruleNames, String lhs) {
        return createVersionedRule( null, ruleNames, null, lhs, null );
    }

    public String createLhsRule(String ruleName, String lhs) {
        return createVersionedRule( null, new String[] { ruleName }, null, lhs, null );
    }

    public String createLhsRule(String packageName, String ruleName, String lhs) {
        return createVersionedRule( packageName, new String[] { ruleName }, null, lhs, null );
    }

    public String createLhsRule(String packageName, String[] ruleNames, String lhs) {
        return createVersionedRule( packageName, ruleNames, null, lhs, null );
    }

    public String createVersionedRule(String ruleName, String version) {
        return createVersionedRule( null, new String[] { ruleName }, null, null, version );
    }

    public String createDefaultRule(String ruleName) {
        return createDefaultRule( new String[] { ruleName },
                                  null );
    }

    public String createDefaultRule(String[] rulesNames) {
        return createDefaultRule( rulesNames,
                                  null );
    }

    public String createDefaultRule(String ruleName,
                                    String packageName) {
        return createVersionedRule( packageName,  new String[] { ruleName }, null, null, null );
    }

    public String createDefaultRule(String[] ruleNames,
                                    String packageName) {
        return createVersionedRule( packageName, ruleNames, null, null, null );
    }

    public String createAttributeRule(String ruleName,
                                      String attribute) {
        return createVersionedRule( null, new String[] { ruleName }, attribute, null, null );
    }

    public String createCommonDSLRRule(String[] ruleNames) {
        StringBuilder sb = new StringBuilder();

        sb.append("package org.drools.test\n");
        sb.append("import org.drools.Person\n\n");
        sb.append("global java.util.List list\n\n");

        for (String ruleName : ruleNames ) {
            sb.append("rule ");
            sb.append(ruleName);
            sb.append("\n");
            sb.append("when\n");
            sb.append("There is a Person\n");
            sb.append("then\n");
            sb.append("    add rule's name to list\n");
            sb.append("end\n\n");
        }

        return sb.toString();
    }

    public String createCommonDSLRRule(String ruleName) {
        return createCommonDSLRRule( new String[] { ruleName } );
    }

    public String createCommonDSL(String restriction) {
        StringBuilder sb = new StringBuilder();
        sb.append("[condition][]There is a Person = Person(");
        if (restriction != null) {
            sb.append(restriction);
        }
        sb.append(")\n");
        sb.append("[consequence][]add rule's name to list = list.add( drools.getRule().getName() );\n");
        return sb.toString();
    }

    public String createCommonFunction(String functionName, String valueToAdd) {
      StringBuilder sb = new StringBuilder();

      sb.append("package org.drools.test\n");
      sb.append("import org.drools.Person\n\n");
      sb.append("global java.util.List list\n\n");

      sb.append("function void  ");
      sb.append(functionName);
      sb.append("(java.util.List myList,String source){\n");
      sb.append(" myList.add(\"");
      sb.append(valueToAdd);
      sb.append(" from \"+source);\n");
      sb.append("}\n");

      return sb.toString();
    }

    public String createCommonDeclaration(String typeName, String[] annotations, String[] fields) {
      StringBuilder sb = new StringBuilder();

      sb.append("package org.drools.test\n");
      sb.append("global java.util.List list\n\n");

      sb.append("declare  ");
      sb.append(typeName);

      if (annotations != null){
          for (String annotation : annotations) {
              sb.append("\n");
              sb.append(annotation);
          }
      }

      sb.append("\n");

      if (fields != null){
          for (String field : fields) {
              sb.append("\n");
              sb.append(field);
          }
      }

      sb.append("\n");
      sb.append("end\n");

      return sb.toString();
    }

    public String createCommonQuery(String name, String[] patterns){
        StringBuilder sb = new StringBuilder();
        sb.append(this.createHeader("org.drools.test"));
        sb.append("\n");
        sb.append("query \"");
        sb.append(name);
        sb.append("\"\n");

        for (String pattern : patterns) {
            sb.append(pattern);
            sb.append("\n");
        }

        sb.append("end\n");

        return sb.toString();
    }

}
