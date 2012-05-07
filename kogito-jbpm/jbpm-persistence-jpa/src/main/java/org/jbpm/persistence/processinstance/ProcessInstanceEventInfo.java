package org.jbpm.persistence.processinstance;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Version;

@Entity
@SequenceGenerator(name="procInstEventIdSeq", sequenceName="PROC_INST_EVENT_ID_SEQ", allocationSize=1)
public class ProcessInstanceEventInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator="procInstEventIdSeq")
    private long   id;

    @Version
    @Column(name = "OPTLOCK")
    private int    version;

    private String eventType;
    private long   processInstanceId;

    protected ProcessInstanceEventInfo() {
    }
    
    public long getId() {
        return this.id;
    }
    
    public int getVersion() {
        return this.version;
    }    

    public ProcessInstanceEventInfo(long processInstanceId,
                                    String eventType) {
        this.processInstanceId = processInstanceId;
        this.eventType = eventType;
    }

    public long getProcessInstanceId() {
        return processInstanceId;
    }

    public String getEventType() {
        return eventType;
    }

}
