package org.drools.vsm;

import java.util.Collection;
import java.util.Map;

import org.drools.KnowledgeBase;
import org.drools.command.Command;
import org.drools.command.ExecuteCommand;
import org.drools.command.KnowledgeContextResolveFromContextCommand;
import org.drools.command.runtime.rule.FireAllRulesCommand;
import org.drools.event.process.ProcessEventListener;
import org.drools.event.rule.AgendaEventListener;
import org.drools.event.rule.WorkingMemoryEventListener;
import org.drools.runtime.Environment;
import org.drools.runtime.ExecutionResults;
import org.drools.runtime.ExitPoint;
import org.drools.runtime.Globals;
import org.drools.runtime.ObjectFilter;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.process.ProcessInstance;
import org.drools.runtime.process.WorkItemManager;
import org.drools.runtime.rule.Agenda;
import org.drools.runtime.rule.AgendaFilter;
import org.drools.runtime.rule.FactHandle;
import org.drools.runtime.rule.QueryResults;
import org.drools.runtime.rule.WorkingMemoryEntryPoint;
import org.drools.time.SessionClock;
import org.drools.vsm.responsehandlers.BlockingMessageResponseHandler;

public class StatefulKnowledgeSessionClient
    implements
    StatefulKnowledgeSession {

    private ServiceManagerClient serviceManager;
    private String               instanceId;

    public StatefulKnowledgeSessionClient(String instanceId,
                                          ServiceManagerClient serviceManager) {
        this.instanceId = instanceId;
        this.serviceManager = serviceManager;
    }
    
    public String getInstanceId() {
        return this.instanceId;
    }

    public void dispose() {
        // TODO Auto-generated method stub

    }

    public int getId() {
        // TODO Auto-generated method stub
        return 0;
    }

    public int fireAllRules() {
        String commandId = "ksession.fireAllRules" + serviceManager.getNextId();
        String kresultsId = "kresults_" + serviceManager.getSessionId();

        Message msg = new Message( serviceManager.getSessionId(),
                                   serviceManager.counter.incrementAndGet(),
                                   false,
                                   null,
                                   new KnowledgeContextResolveFromContextCommand( new FireAllRulesCommand( commandId ),
                                                                                  null,
                                                                                  null,
                                                                                  instanceId,
                                                                                  kresultsId ) );

        BlockingMessageResponseHandler handler = new BlockingMessageResponseHandler();

        try {
            serviceManager.client.handler.addResponseHandler( msg.getResponseId(),
                                                              handler );

            serviceManager.client.session.write( msg );

            Object object = handler.getMessage().getPayload();

            if ( object == null ) {
                throw new RuntimeException( "Response was not correctly received" );
            }

            return (Integer) ((ExecutionResults) object).getValue( commandId );
        } catch ( Exception e ) {
            throw new RuntimeException( "Unable to execute message",
                                        e );
        }
    }

    public int fireAllRules(int max) {
        // TODO Auto-generated method stub
        return 0;
    }

    public int fireAllRules(AgendaFilter agendaFilter) {
        // TODO Auto-generated method stub
        return 0;
    }

    public void fireUntilHalt() {
        // TODO Auto-generated method stub

    }

    public void fireUntilHalt(AgendaFilter agendaFilter) {
        // TODO Auto-generated method stub

    }

    public ExecutionResults execute(Command command) {
        String commandId = "ksession.execute" + serviceManager.getNextId();
        String kresultsId = "kresults_" + serviceManager.getSessionId();

        Message msg = new Message( serviceManager.getSessionId(),
                                   serviceManager.counter.incrementAndGet(),
                                   false,
                                   null,
                                   new KnowledgeContextResolveFromContextCommand( new ExecuteCommand( commandId,
                                                                                                      command ),
                                                                                  null,
                                                                                  null,
                                                                                  instanceId,
                                                                                  kresultsId ) );

        BlockingMessageResponseHandler handler = new BlockingMessageResponseHandler();

        try {
            serviceManager.client.handler.addResponseHandler( msg.getResponseId(),
                                                              handler );

            serviceManager.client.session.write( msg );

            Object object = handler.getMessage().getPayload();

            if ( object == null ) {
                throw new RuntimeException( "Response was not correctly received" );
            }

            System.out.println( "object" + object );

            return (ExecutionResults) ((ExecutionResults) object).getValue( commandId );
        } catch ( Exception e ) {
            throw new RuntimeException( "Unable to execute message",
                                        e );
        }
    }

    public Environment getEnvironment() {
        // TODO Auto-generated method stub
        return null;
    }

    public Object getGlobal(String identifier) {
        // TODO Auto-generated method stub
        return null;
    }

    public Globals getGlobals() {
        // TODO Auto-generated method stub
        return null;
    }

    public KnowledgeBase getKnowledgeBase() {
        // TODO Auto-generated method stub
        return null;
    }

    public <T extends SessionClock> T getSessionClock() {
        // TODO Auto-generated method stub
        return null;
    }

    public void registerExitPoint(String name,
                                  ExitPoint exitPoint) {
        // TODO Auto-generated method stub

    }

    public void setGlobal(String identifier,
                          Object object) {
        // TODO Auto-generated method stub

    }

    public void unregisterExitPoint(String name) {
        // TODO Auto-generated method stub

    }

    public Agenda getAgenda() {
        // TODO Auto-generated method stub
        return null;
    }

    public QueryResults getQueryResults(String query) {
        // TODO Auto-generated method stub
        return null;
    }

    public QueryResults getQueryResults(String query,
                                        Object[] arguments) {
        // TODO Auto-generated method stub
        return null;
    }

    public WorkingMemoryEntryPoint getWorkingMemoryEntryPoint(String name) {
        // TODO Auto-generated method stub
        return null;
    }

    public Collection< ? extends WorkingMemoryEntryPoint> getWorkingMemoryEntryPoints() {
        // TODO Auto-generated method stub
        return null;
    }

    public void halt() {
        // TODO Auto-generated method stub

    }

    public FactHandle getFactHandle(Object object) {
        // TODO Auto-generated method stub
        return null;
    }

    public <T extends FactHandle> Collection<T> getFactHandles() {
        // TODO Auto-generated method stub
        return null;
    }

    public <T extends FactHandle> Collection<T> getFactHandles(ObjectFilter filter) {
        // TODO Auto-generated method stub
        return null;
    }

    public Object getObject(FactHandle factHandle) {
        // TODO Auto-generated method stub
        return null;
    }

    public Collection<Object> getObjects() {
        // TODO Auto-generated method stub
        return null;
    }

    public Collection<Object> getObjects(ObjectFilter filter) {
        // TODO Auto-generated method stub
        return null;
    }

    public FactHandle insert(Object object) {
        // TODO Auto-generated method stub
        return null;
    }

    public void retract(FactHandle handle) {
        // TODO Auto-generated method stub

    }

    public void update(FactHandle handle,
                       Object object) {
        // TODO Auto-generated method stub

    }

    public void abortProcessInstance(long id) {
        // TODO Auto-generated method stub

    }

    public ProcessInstance getProcessInstance(long id) {
        // TODO Auto-generated method stub
        return null;
    }

    public Collection<ProcessInstance> getProcessInstances() {
        // TODO Auto-generated method stub
        return null;
    }

    public WorkItemManager getWorkItemManager() {
        // TODO Auto-generated method stub
        return null;
    }

    public void signalEvent(String type,
                            Object event) {
        // TODO Auto-generated method stub

    }

    public ProcessInstance startProcess(String processId) {
        // TODO Auto-generated method stub
        return null;
    }

    public ProcessInstance startProcess(String processId,
                                        Map<String, Object> parameters) {
        // TODO Auto-generated method stub
        return null;
    }

    public void addEventListener(WorkingMemoryEventListener listener) {
        // TODO Auto-generated method stub

    }

    public void addEventListener(AgendaEventListener listener) {
        // TODO Auto-generated method stub

    }

    public Collection<AgendaEventListener> getAgendaEventListeners() {
        // TODO Auto-generated method stub
        return null;
    }

    public Collection<WorkingMemoryEventListener> getWorkingMemoryEventListeners() {
        // TODO Auto-generated method stub
        return null;
    }

    public void removeEventListener(WorkingMemoryEventListener listener) {
        // TODO Auto-generated method stub

    }

    public void removeEventListener(AgendaEventListener listener) {
        // TODO Auto-generated method stub

    }

    public void addEventListener(ProcessEventListener listener) {
        // TODO Auto-generated method stub

    }

    public Collection<ProcessEventListener> getProcessEventListeners() {
        // TODO Auto-generated method stub
        return null;
    }

    public void removeEventListener(ProcessEventListener listener) {
        // TODO Auto-generated method stub

    }
    
    public String getEntryPointId() {
        // TODO Auto-generated method stub
        return null;
    }

    public long getFactCount() {
        // TODO Auto-generated method stub
        return 0;
    }

}
