package org.drools.persistence.info;

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
import javax.persistence.SequenceGenerator;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.drools.core.common.InternalRuleBase;
import org.drools.core.marshalling.impl.InputMarshaller;
import org.drools.core.marshalling.impl.MarshallerReaderContext;
import org.drools.core.marshalling.impl.MarshallerWriteContext;
import org.drools.core.marshalling.impl.OutputMarshaller;
import org.drools.core.marshalling.impl.ProcessMarshaller;
import org.drools.core.marshalling.impl.ProcessMarshallerFactory;
import org.drools.core.marshalling.impl.ProtobufInputMarshaller;
import org.drools.core.marshalling.impl.ProtobufOutputMarshaller;
import org.drools.core.process.instance.WorkItem;
import org.kie.api.runtime.Environment;

@Entity
@SequenceGenerator(name="workItemInfoIdSeq", sequenceName="WORKITEMINFO_ID_SEQ")
public class WorkItemInfo  {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator="workItemInfoIdSeq")
    private Long   workItemId;

    @Version
    @Column(name = "OPTLOCK")
    private int    version;

    private String name;
    private Date   creationDate;
    private long   processInstanceId;
    private long   state;
    
    @Lob
    @Column(length=2147483647)
    private byte[] workItemByteArray;
    
    private @Transient
    WorkItem       workItem;

    private @Transient
    Environment                               env;
    
    protected WorkItemInfo() {
    }

    public WorkItemInfo(WorkItem workItem, Environment env) {
        this.workItem = workItem;
        this.name = workItem.getName();
        this.creationDate = new Date();
        this.processInstanceId = workItem.getProcessInstanceId();
        this.env = env;
    }

    public Long getId() {
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
    
    public byte [] getWorkItemByteArray() { 
       return workItemByteArray;
    }
    
    public WorkItem getWorkItem(Environment env, InternalRuleBase ruleBase) {
        this.env = env;
        if ( workItem == null ) {
            try {
                ByteArrayInputStream bais = new ByteArrayInputStream( workItemByteArray );
                MarshallerReaderContext context = new MarshallerReaderContext( bais,
                                                                               ruleBase,
                                                                               null,
                                                                               null,
                                                                               null,
                                                                                   env);
                try {
                    workItem = ProtobufInputMarshaller.readWorkItem(context);
                } catch (Exception e) {
                    context.close();
                    bais = new ByteArrayInputStream( workItemByteArray );
                    context = new MarshallerReaderContext( bais,
                            ruleBase,
                            null,
                            null,
                            null,
                            env);
                    workItem = InputMarshaller.readWorkItem( context );
                }

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
                                                                         null,
                                                                         this.env);
            ProtobufOutputMarshaller.writeWorkItem(context, workItem);

            context.close();
            this.workItemByteArray = baos.toByteArray();
        } catch ( IOException e ) {
            throw new IllegalArgumentException( "IOException while storing workItem " + workItem.getId() + ": " + e.getMessage() );
        }
    }
    
    public void setId(Long id){
        this.workItemId = id;
    }


}
