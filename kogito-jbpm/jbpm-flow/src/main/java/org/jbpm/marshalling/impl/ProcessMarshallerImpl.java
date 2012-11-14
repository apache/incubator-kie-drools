package org.jbpm.marshalling.impl;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.drools.common.InternalWorkingMemory;
import org.drools.marshalling.impl.InputMarshaller;
import org.drools.marshalling.impl.MarshallerReaderContext;
import org.drools.marshalling.impl.MarshallerWriteContext;
import org.drools.marshalling.impl.PersisterEnums;
import org.drools.marshalling.impl.ProcessMarshaller;
import org.drools.process.instance.WorkItemManager;
import org.drools.process.instance.impl.WorkItemImpl;
import org.kie.runtime.process.ProcessInstance;
import org.kie.runtime.process.WorkItem;
import org.jbpm.process.instance.InternalProcessRuntime;
import org.jbpm.process.instance.timer.TimerInstance;
import org.jbpm.process.instance.timer.TimerManager;
import org.jbpm.process.instance.timer.TimerManager.ProcessJobContext;

public class ProcessMarshallerImpl implements ProcessMarshaller {

    public void writeProcessInstances(MarshallerWriteContext context) throws IOException {
        ObjectOutputStream stream = context.stream;
        List<org.kie.runtime.process.ProcessInstance> processInstances = new ArrayList<org.kie.runtime.process.ProcessInstance>( context.wm.getProcessInstances() );
        Collections.sort( processInstances,
                          new Comparator<org.kie.runtime.process.ProcessInstance>() {
                              public int compare(org.kie.runtime.process.ProcessInstance o1,
                            		  org.kie.runtime.process.ProcessInstance o2) {
                                  return (int) (o1.getId() - o2.getId());
                              }
                          } );

        for ( org.kie.runtime.process.ProcessInstance processInstance : processInstances ) {
            stream.writeShort(PersisterEnums.PROCESS_INSTANCE);
            String processType = processInstance.getProcess().getType();
            stream.writeUTF(processType);
            ProcessMarshallerRegistry.INSTANCE.getMarshaller(processType)
            	.writeProcessInstance(context, processInstance);
        }
        stream.writeShort( PersisterEnums.END );
    }

    public void writeProcessTimers(MarshallerWriteContext outCtx) throws IOException {
        outCtx.writersByClass.put( ProcessJobContext.class, new TimerManager.ProcessTimerOutputMarshaller() );
        
        // this is deprecated, will delete soon (mdp)
//        ObjectOutputStream stream = context.stream;
//
        TimerManager timerManager = ((InternalProcessRuntime) ((InternalWorkingMemory) outCtx.wm).getProcessRuntime()).getTimerManager();
        long timerId = timerManager.internalGetTimerId();
        outCtx.writeLong( timerId );
//        
//        // need to think on how to fix this
//        // stream.writeObject( timerManager.getTimerService() );
//        
//        List<TimerInstance> timers = new ArrayList<TimerInstance>( timerManager.getTimers() );
//        Collections.sort( timers,
//                          new Comparator<TimerInstance>() {
//                              public int compare(TimerInstance o1,
//                                                 TimerInstance o2) {
//                                  return (int) (o2.getId() - o1.getId());
//                              }
//                          } );
//        for ( TimerInstance timer : timers ) {
//            stream.writeShort( PersisterEnums.TIMER );
//            writeTimer( context,
//                        timer );
//        }
        
        outCtx.writeShort( PersisterEnums.END );
    }

    public static void writeTimer(MarshallerWriteContext context,
                           TimerInstance timer) throws IOException {
        ObjectOutputStream stream = context.stream;
        stream.writeLong( timer.getId() );
        stream.writeLong( timer.getTimerId() );
        stream.writeLong( timer.getDelay() );
        stream.writeLong( timer.getPeriod() );
        stream.writeLong( timer.getProcessInstanceId() );
        stream.writeLong( timer.getActivated().getTime() );
        Date lastTriggered = timer.getLastTriggered();
        if ( lastTriggered != null ) {
            stream.writeBoolean( true );
            stream.writeLong( timer.getLastTriggered().getTime() );
        } else {
            stream.writeBoolean( false );
        }
    }

    public void writeWorkItems(MarshallerWriteContext context) throws IOException {
        ObjectOutputStream stream = context.stream;

        List<WorkItem> workItems = new ArrayList<WorkItem>(
    		((WorkItemManager) context.wm.getWorkItemManager()).getWorkItems() );
        Collections.sort( workItems,
                          new Comparator<WorkItem>() {
                              public int compare(WorkItem o1,
                                                 WorkItem o2) {
                                  return (int) (o2.getId() - o1.getId());
                              }
                          } );
        for ( WorkItem workItem : workItems ) {
            stream.writeShort( PersisterEnums.WORK_ITEM );
            writeWorkItem( context,
                           workItem );
        }
        stream.writeShort( PersisterEnums.END );
    }

    public static void writeWorkItem(MarshallerWriteContext context,
                                     WorkItem workItem) throws IOException {
         writeWorkItem(context, workItem, true);
    }

    public static void writeWorkItem(MarshallerWriteContext context,
                                     WorkItem workItem, boolean includeVariables) throws IOException {
        ObjectOutputStream stream = context.stream;
        stream.writeLong( workItem.getId() );
        stream.writeLong( workItem.getProcessInstanceId() );
        stream.writeUTF( workItem.getName() );
        stream.writeInt( workItem.getState() );

        if(includeVariables){
	        Map<String, Object> parameters = workItem.getParameters();
	        stream.writeInt( parameters.size() );
	        for ( Map.Entry<String, Object> entry : parameters.entrySet() ) {
	            stream.writeUTF( entry.getKey() );
	            stream.writeObject( entry.getValue() );
	        }
	    }
    }

    public List<ProcessInstance> readProcessInstances(MarshallerReaderContext context) throws IOException {
        ObjectInputStream stream = context.stream;
        List<ProcessInstance> processInstanceList = new ArrayList<ProcessInstance>();
        while ( stream.readShort() == PersisterEnums.PROCESS_INSTANCE ) {
        	String processType = stream.readUTF();
        	ProcessInstance processInstance 
        	    = ProcessMarshallerRegistry.INSTANCE.getMarshaller(processType).readProcessInstance(context);
        	processInstanceList.add(processInstance);
        }
        return processInstanceList;
    }

    public void readWorkItems(MarshallerReaderContext context) throws IOException {
        InternalWorkingMemory wm = context.wm;
        ObjectInputStream stream = context.stream;
        while ( stream.readShort() == PersisterEnums.WORK_ITEM ) {
            WorkItem workItem = readWorkItem( context );
            ((WorkItemManager) wm.getWorkItemManager()).internalAddWorkItem( (org.drools.process.instance.WorkItem) workItem );
        }
    }

    public static WorkItem readWorkItem(MarshallerReaderContext context) throws IOException {
       return readWorkItem(context, true);
    }

    public static WorkItem readWorkItem(MarshallerReaderContext context, boolean includeVariables) throws IOException {
        ObjectInputStream stream = context.stream;

        WorkItemImpl workItem = new WorkItemImpl();
        workItem.setId( stream.readLong() );
        workItem.setProcessInstanceId( stream.readLong() );
        workItem.setName( stream.readUTF() );
        workItem.setState( stream.readInt() );

        if(includeVariables){
        int nbParameters = stream.readInt();

        for ( int i = 0; i < nbParameters; i++ ) {
            String name = stream.readUTF();
            try {
                Object value = stream.readObject();
                workItem.setParameter( name,
                                       value );
            } catch ( ClassNotFoundException e ) {
                throw new IllegalArgumentException( "Could not reload parameter " + name );
            }
        }
        }

        return workItem;
    }

    public void readProcessTimers(MarshallerReaderContext inCtx) throws IOException, ClassNotFoundException {
        inCtx.readersByInt.put( (int) PersisterEnums.PROCESS_TIMER,  new TimerManager.ProcessTimerInputMarshaller());
        
        ObjectInputStream stream = inCtx.stream;

        TimerManager timerManager = ((InternalProcessRuntime) ((InternalWorkingMemory) inCtx.wm).getProcessRuntime()).getTimerManager();
        timerManager.internalSetTimerId( stream.readLong() );

        
        int token;
        while ((token = inCtx.readShort()) != PersisterEnums.END) {
            switch( token ) {
                case PersisterEnums.TIMER : {
                    TimerInstance timer = readTimer( inCtx );
                    timerManager.internalAddTimer( timer );   
                    break;
                }
                case PersisterEnums.DEFAULT_TIMER: {
                    InputMarshaller.readTimer( inCtx );
                    break;
                }
            }   
        }          
    }

    public static TimerInstance readTimer(MarshallerReaderContext context) throws IOException {
        ObjectInputStream stream = context.stream;

        TimerInstance timer = new TimerInstance();
        timer.setId( stream.readLong() );
        timer.setTimerId( stream.readLong() );
        timer.setDelay( stream.readLong() );
        timer.setPeriod( stream.readLong() );
        timer.setProcessInstanceId( stream.readLong() );
        timer.setActivated( new Date( stream.readLong() ) );
        if ( stream.readBoolean() ) {
            timer.setLastTriggered( new Date( stream.readLong() ) );
        }
        return timer;
    }

    public void init(MarshallerReaderContext context) {
        // nothing to do
        
    }

}
    