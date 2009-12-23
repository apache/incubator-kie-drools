package org.drools.command.vsm;


import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.drools.command.Context;
import org.drools.command.impl.GenericCommand;
import org.drools.command.impl.KnowledgeCommandContext;
import org.drools.runtime.KnowledgeRuntime;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.process.WorkItemHandler;

public class RegisterRemoteWorkItemHandlerCommand implements GenericCommand<Object> {
	
	private String handler;
	private String workItemName;

        public RegisterRemoteWorkItemHandlerCommand() {
        }

        public RegisterRemoteWorkItemHandlerCommand(String workItemName, String handler) {
            this.handler = handler;
            this.workItemName = workItemName;
        }
        
	public String getHandler() {
		return handler;
	}

	public void setHandler(String handler) {
		this.handler = handler;
	}

	public String getWorkItemName() {
		return workItemName;
	}

	public void setWorkItemName(String workItemName) {
		this.workItemName = workItemName;
	}

    public Object execute(Context context) {
        StatefulKnowledgeSession ksession = ((KnowledgeCommandContext) context).getStatefulKnowledgesession(); 
        WorkItemHandler workItemHandler = null;
        try {
             Class t = Class.forName(handler);
             Constructor c = t.getConstructor(KnowledgeRuntime.class);
             workItemHandler =  (WorkItemHandler) c.newInstance(ksession);
        } catch (InstantiationException ex) {
            Logger.getLogger(RegisterRemoteWorkItemHandlerCommand.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(RegisterRemoteWorkItemHandlerCommand.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(RegisterRemoteWorkItemHandlerCommand.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvocationTargetException ex) {
            Logger.getLogger(RegisterRemoteWorkItemHandlerCommand.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchMethodException ex) {
            Logger.getLogger(RegisterRemoteWorkItemHandlerCommand.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(RegisterRemoteWorkItemHandlerCommand.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(RegisterRemoteWorkItemHandlerCommand.class.getName()).log(Level.SEVERE, null, ex);
        }
        ksession.getWorkItemManager().registerWorkItemHandler(workItemName, workItemHandler);
		return null;
	}

	public String toString() {
		return "session.getWorkItemManager().registerWorkItemHandler("
			+ workItemName + ", " + handler +  ");";
	}

}