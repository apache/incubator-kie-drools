package org.drools.persistence.processinstance;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.PreUpdate;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.drools.marshalling.impl.InputMarshaller;
import org.drools.marshalling.impl.MarshallerReaderContext;
import org.drools.marshalling.impl.MarshallerWriteContext;
import org.drools.marshalling.impl.OutputMarshaller;
import org.drools.process.instance.WorkItem;

@Entity
public class WorkItemInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long   workItemId;

    @Version
    @Column(name = "OPTLOCK")
    private int    version;

    private String name;
    private Date   creationDate;
    private long   processInstanceId;
    private long   state;
    private @Lob
    byte[]         workItemByteArray;
    private @Transient
    WorkItem       workItem;

    protected WorkItemInfo() {
    }

    public WorkItemInfo(WorkItem workItem) {
        this.workItem = workItem;
        this.name = workItem.getName();
        this.creationDate = new Date();
        this.processInstanceId = workItem.getProcessInstanceId();
    }

    public long getId() {
        return workItemId;
    }
    
    public int getVersion() {
        return this.version;
    }    

    public String getName() {
        return name;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public long getProcessInstanceId() {
        return processInstanceId;
    }

    public long getState() {
        return state;
    }

    public WorkItem getWorkItem() {
        if ( workItem == null ) {
            try {
                ByteArrayInputStream bais = new ByteArrayInputStream( workItemByteArray );
                MarshallerReaderContext context = new MarshallerReaderContext( bais,
                                                                               null,
                                                                               null,
                                                                               null );
                workItem = InputMarshaller.readWorkItem( context );
                context.close();
            } catch ( IOException e ) {
                e.printStackTrace();
                throw new IllegalArgumentException( "IOException while loading process instance: " + e.getMessage() );
            }
        }
        return workItem;
    }

    @PreUpdate
    public void update() {
        this.state = workItem.getState();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            MarshallerWriteContext context = new MarshallerWriteContext( baos,
                                                                         null,
                                                                         null,
                                                                         null,
                                                                         null );
            OutputMarshaller.writeWorkItem( context,
                                            workItem );
            context.close();
            this.workItemByteArray = baos.toByteArray();
        } catch ( IOException e ) {
            throw new IllegalArgumentException( "IOException while storing workItem " + workItem.getId() + ": " + e.getMessage() );
        }
    }

}
