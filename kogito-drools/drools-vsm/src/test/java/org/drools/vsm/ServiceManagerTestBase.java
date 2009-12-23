package org.drools.vsm;

import junit.framework.TestCase;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseProvider;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderProvider;
import org.drools.builder.ResourceType;
import org.drools.command.runtime.rule.FireAllRulesCommand;
import org.drools.io.ResourceFactory;
import org.drools.io.impl.ClassPathResource;
import org.drools.runtime.ExecutionResults;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.process.ProcessInstance;
import org.drools.task.query.TaskSummary;
import org.drools.task.service.ContentData;
import org.drools.task.service.HumanTaskServiceImpl;
import org.drools.vsm.remote.StatefulKnowledgeSessionRemoteClient;
import org.drools.vsm.task.responseHandlers.BlockingTaskOperationMessageResponseHandler;
import org.drools.vsm.task.responseHandlers.BlockingTaskSummaryMessageResponseHandler;


public class ServiceManagerTestBase extends TestCase {

	private static final int DEFAULT_WAIT_TIME = 5000;
	
    protected ServiceManager client;
	private HumanTaskService htClient;

    public void testFireAllRules() throws Exception {
        String str = "";
        str += "package org.drools \n";
        str += "global java.util.List list \n";
        str += "rule rule1 \n";
        str += "    dialect \"java\" \n";
        str += "when \n";
        str += "then \n";
        str += "    System.out.println( \"hello1!!!\" ); \n";
        str += "end \n";
        str += "rule rule2 \n";
        str += "    dialect \"java\" \n";
        str += "when \n";
        str += "then \n";
        str += "    System.out.println( \"hello2!!!\" ); \n";
        str += "end \n";

        KnowledgeBuilderProvider kbuilderFactory = this.client.getKnowledgeBuilderFactory();
        KnowledgeBuilder kbuilder = kbuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ),
                      ResourceType.DRL );

        if ( kbuilder.hasErrors() ) {
            System.out.println( "Errors: " + kbuilder.getErrors() );
        }

        KnowledgeBaseProvider kbaseFactory = this.client.getKnowledgeBaseFactory();
        KnowledgeBase kbase = kbaseFactory.newKnowledgeBase();

        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        int fired = ksession.fireAllRules();
        assertEquals( 2,
                      fired );
    }

    public void testExecute() throws Exception {
        String str = "";
        str += "package org.drools \n";
        str += "global java.util.List list \n";
        str += "rule rule1 \n";
        str += "    dialect \"java\" \n";
        str += "when \n";
        str += "then \n";
        str += "    System.out.println( \"hello1!!!\" ); \n";
        str += "end \n";
        str += "rule rule2 \n";
        str += "    dialect \"java\" \n";
        str += "when \n";
        str += "then \n";
        str += "    System.out.println( \"hello2!!!\" ); \n";
        str += "end \n";

        KnowledgeBuilderProvider kbuilderFactory = this.client.getKnowledgeBuilderFactory();
        KnowledgeBuilder kbuilder = kbuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ),
                      ResourceType.DRL );

        if ( kbuilder.hasErrors() ) {
            System.out.println( "Errors: " + kbuilder.getErrors() );
        }

        KnowledgeBaseProvider kbaseFactory = this.client.getKnowledgeBaseFactory();
        KnowledgeBase kbase = kbaseFactory.newKnowledgeBase();

        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        ExecutionResults results = ksession.execute( new FireAllRulesCommand( "fired" ) );

        assertEquals( 2,
                      (int) (Integer) results.getValue( "fired" ) );
    }

    public void testNamedService() throws Exception {
        String str = "";
        str += "package org.drools \n";
        str += "global java.util.List list \n";
        str += "rule rule1 \n";
        str += "    dialect \"java\" \n";
        str += "when \n";
        str += "then \n";
        str += "    System.out.println( \"hello1!!!\" ); \n";
        str += "end \n";
        str += "rule rule2 \n";
        str += "    dialect \"java\" \n";
        str += "when \n";
        str += "then \n";
        str += "    System.out.println( \"hello2!!!\" ); \n";
        str += "end \n";

        KnowledgeBuilderProvider kbuilderFactory = this.client.getKnowledgeBuilderFactory();
        KnowledgeBuilder kbuilder = kbuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ),
                      ResourceType.DRL );

        if ( kbuilder.hasErrors() ) {
            System.out.println( "Errors: " + kbuilder.getErrors() );
        }

        KnowledgeBaseProvider kbaseFactory = this.client.getKnowledgeBaseFactory();
        KnowledgeBase kbase = kbaseFactory.newKnowledgeBase();

        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        this.client.register( "ksession1",
                              ksession );

        ExecutionResults results = this.client.lookup( "ksession1" ).execute( new FireAllRulesCommand( "fired" ) );

        assertEquals( 2,
                      (int) (Integer) results.getValue( "fired" ) );
    }

    public void testVsmPipeline() throws Exception {
        String str = "";
        str += "package org.drools \n";
        str += "global java.util.List list \n";
        str += "rule rule1 \n";
        str += "    dialect \"java\" \n";
        str += "when \n";
        str += "then \n";
        str += "    System.out.println( \"hello1!!!\" ); \n";
        str += "end \n";
        str += "rule rule2 \n";
        str += "    dialect \"java\" \n";
        str += "when \n";
        str += "then \n";
        str += "    System.out.println( \"hello2!!!\" ); \n";
        str += "end \n";

        KnowledgeBuilderProvider kbuilderFactory = this.client.getKnowledgeBuilderFactory();
        KnowledgeBuilder kbuilder = kbuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ),
                      ResourceType.DRL );

        if ( kbuilder.hasErrors() ) {
            System.out.println( "Errors: " + kbuilder.getErrors() );
        }

        KnowledgeBaseProvider kbaseFactory = this.client.getKnowledgeBaseFactory();
        KnowledgeBase kbase = kbaseFactory.newKnowledgeBase();

        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        this.client.register( "ksession1",
                              ksession );

        //        ExecutionResults results = this.client.lookup( "ksession1" ).execute( new FireAllRulesCommand( "fired" ) );
        //       
        //        assertEquals( 2, (int ) ( Integer) results.getValue( "fired" ) );
    }

    public void testHumanTasks() throws Exception {

    	KnowledgeBuilderProvider kbuilderFactory = this.client.getKnowledgeBuilderFactory();
    	KnowledgeBuilder kbuilder = kbuilderFactory.newKnowledgeBuilder();
    	kbuilder.add( new ClassPathResource("rules/humanTasks.rf"),
    			ResourceType.DRF );

    	if ( kbuilder.hasErrors() ) {
    		System.out.println( "Errors: " + kbuilder.getErrors() );
    	}

    	KnowledgeBaseProvider kbaseFactory = this.client.getKnowledgeBaseFactory();
    	KnowledgeBase kbase = kbaseFactory.newKnowledgeBase();

    	kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

    	StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
    	
    	((StatefulKnowledgeSessionRemoteClient)ksession).registerWorkItemHandler("Human Task", "org.drools.vsm.task.CommandBasedVSMWSHumanTaskHandler");
    	ProcessInstance processInstance = ksession.startProcess("org.drools.test.humanTasks");
    	HumanTaskServiceProvider humanTaskServiceFactory = this.client.getHumanTaskService();
    	htClient = humanTaskServiceFactory.newHumanTaskServiceClient();
    	
    	Thread.sleep(1000);

    	ksession.fireAllRules();

    	System.out.println("First Task Execution");
    	assertEquals(true , executeNextTask("lucaz"));
    	Thread.sleep(8000);
    	System.out.println("Second Task Execution");
    	assertEquals(true , executeNextTask("lucaz"));
    	System.out.println("Inexistent Task Execution");
    	Thread.sleep(8000);
    	assertEquals(false, executeNextTask("lucaz"));
    	
    }

    private boolean executeNextTask(String user) {

    	BlockingTaskSummaryMessageResponseHandler responseHandler = new BlockingTaskSummaryMessageResponseHandler();
    	((HumanTaskServiceImpl)htClient).getTasksAssignedAsPotentialOwner(user, "en-UK", responseHandler);
    	responseHandler.waitTillDone(DEFAULT_WAIT_TIME);

    	if (responseHandler.getResults().size()==0)
    		return false;

    	TaskSummary task = responseHandler.getResults().get(0);
    	ContentData data = new ContentData(); 
    	data.setContent("next step".getBytes());

    	BlockingTaskOperationMessageResponseHandler startResponseHandler = new BlockingTaskOperationMessageResponseHandler();
    	((HumanTaskServiceImpl)htClient).start(task.getId(), user, startResponseHandler );
    	startResponseHandler.waitTillDone(DEFAULT_WAIT_TIME);
    	
    	System.out.println("Started Task " + task.getId());
    	
    	BlockingTaskOperationMessageResponseHandler completeResponseHandler = new BlockingTaskOperationMessageResponseHandler();
    	((HumanTaskServiceImpl)htClient).complete(task.getId(), user, null , completeResponseHandler);
    	completeResponseHandler.waitTillDone(DEFAULT_WAIT_TIME);
    	System.out.println("Completed Task " + task.getId());
    	
    	return true;
    }

}
