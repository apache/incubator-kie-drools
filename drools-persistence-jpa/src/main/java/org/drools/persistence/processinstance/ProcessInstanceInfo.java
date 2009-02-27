package org.drools.persistence.processinstance;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.PreUpdate;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.drools.marshalling.impl.MarshallerReaderContext;
import org.drools.marshalling.impl.MarshallerWriteContext;
import org.drools.marshalling.impl.ProcessInstanceMarshaller;
import org.drools.marshalling.impl.ProcessMarshallerRegistry;
import org.drools.marshalling.impl.RuleFlowProcessInstanceMarshaller;
import org.drools.process.instance.impl.ProcessInstanceImpl;
import org.drools.ruleflow.instance.RuleFlowProcessInstance;
import org.drools.runtime.process.ProcessInstance;
import org.drools.workflow.instance.impl.WorkflowProcessInstanceImpl;
import org.hibernate.annotations.CollectionOfElements;

@Entity
public class ProcessInstanceInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private  Long processInstanceId;
    
    @Version
    @Column(name = "OPTLOCK")
    private int         version;
    
    private String processId;
    private Date startDate;
    private Date lastReadDate;
    private Date lastModificationDate;
    private int state;
    // TODO How do I mark a process instance info as dirty when the process
    // instance
    // has changed (so that byte array is regenerated and saved) ?
    private @Lob
    byte[] processInstanceByteArray;
    @CollectionOfElements
    private Set<String> eventTypes = new HashSet<String>();
    private @Transient
    ProcessInstance processInstance;

    ProcessInstanceInfo() {
    }

    public ProcessInstanceInfo(ProcessInstance processInstance) {
        this.processInstance = processInstance;
        this.processId = processInstance.getProcessId();
        startDate = new Date();
    }

    public long getId() {
        return processInstanceId;
    }

    public String getProcessId() {
        return processId;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getLastModificationDate() {
        return lastModificationDate;
    }

    public Date getLastReadDate() {
        return lastReadDate;
    }

    public void updateLastReadDate() {
        lastReadDate = new Date();
    }

    public int getState() {
        return state;
    }

    public ProcessInstance getProcessInstance() {
        if (processInstance == null) {
            try {
                ByteArrayInputStream bais = new ByteArrayInputStream(
                        processInstanceByteArray);
                MarshallerReaderContext context = new MarshallerReaderContext(
                        bais, null, null, null);
                ProcessInstanceMarshaller marshaller = getMarshallerFromContext(context);
                processInstance = marshaller.readProcessInstance(context);
                context.close();
            } catch (IOException e) {
                e.printStackTrace();
                throw new IllegalArgumentException(
                        "IOException while loading process instance: "
                                + e.getMessage());
            }
        }
        return processInstance;
    }

    private ProcessInstanceMarshaller getMarshallerFromContext(
            MarshallerReaderContext context) throws IOException {
        ObjectInputStream stream = context.stream;
        String processInstanceType = stream.readUTF();
        return ProcessMarshallerRegistry.INSTANCE
                .getMarshaller(processInstanceType);
    }

    private void saveProcessInstanceType(MarshallerWriteContext context,
            ProcessInstance processInstance, String processInstanceType)
            throws IOException {
        ObjectOutputStream stream = context.stream;
        // saves the processInstance type first
        stream.writeUTF(processInstanceType);
    }

    @PreUpdate
    public void update() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            MarshallerWriteContext context = new MarshallerWriteContext(baos,
                    null, null, null, null);
            String processType = ((ProcessInstanceImpl) processInstance).getProcess()
                    .getType();
            saveProcessInstanceType(context, processInstance, processType);
            ProcessInstanceMarshaller marshaller = ProcessMarshallerRegistry.INSTANCE.getMarshaller(processType);
            marshaller.writeProcessInstance(
                    context, processInstance);
            context.close();
        } catch (IOException e) {
            throw new IllegalArgumentException(
                    "IOException while storing process instance "
                            + processInstance.getId() + ": " + e.getMessage());
        }
        byte[] newByteArray = baos.toByteArray();
        if (!Arrays.equals(newByteArray, processInstanceByteArray)) {
            this.state = processInstance.getState();
            this.lastModificationDate = new Date();
            this.processInstanceByteArray = newByteArray;
            this.eventTypes.clear();
            for (String type : processInstance.getEventTypes()) {
                eventTypes.add(type);
            }
        }
    }
}
