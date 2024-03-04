/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.persistence.info;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Date;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Transient;
import jakarta.persistence.Version;

import org.drools.core.marshalling.MarshallerReaderContext;
import org.drools.core.marshalling.MarshallerWriteContext;
import org.drools.core.process.WorkItem;
import org.drools.core.process.impl.WorkItemImpl;
import org.drools.kiesession.rulebase.InternalKnowledgeBase;
import org.drools.persistence.api.PersistentWorkItem;
import org.drools.serialization.protobuf.ProtobufInputMarshaller;
import org.drools.serialization.protobuf.ProtobufMarshallerReaderContext;
import org.drools.serialization.protobuf.ProtobufMarshallerWriteContext;
import org.drools.serialization.protobuf.ProtobufOutputMarshaller;
import org.kie.api.marshalling.ObjectMarshallingStrategy;
import org.kie.api.runtime.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Entity
@SequenceGenerator(name="workItemInfoIdSeq", sequenceName="WORKITEMINFO_ID_SEQ")
public class WorkItemInfo implements PersistentWorkItem {

    private static final Logger logger = LoggerFactory.getLogger(WorkItemInfo.class);

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator="workItemInfoIdSeq")
    private Long   workItemId;

    @Version
    @Column(name = "OPTLOCK")
    private int    version;

    private String name;
    private Date   creationDate;
    private String processInstanceId;
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

    public String getProcessInstanceId() {
        return processInstanceId;
    }

    public long getState() {
        return state;
    }
    
    public byte [] getWorkItemByteArray() { 
       return workItemByteArray;
    }
    
    public WorkItem getWorkItem(Environment env, InternalKnowledgeBase kBase) {
        this.env = env;
        if ( workItem == null ) {
            try {
                ByteArrayInputStream bais = new ByteArrayInputStream( workItemByteArray );
                MarshallerReaderContext context = new ProtobufMarshallerReaderContext( bais,
                                                                               kBase,
                                                                               null,
                                                                               null,
                                                                               null,
                                                                               env);
                try {
                    workItem = ProtobufInputMarshaller.readWorkItem(context);
                } catch (Exception e) {
                    // for backward compatibility to be able to restore 5.x data
                    try {
                        context.close();
                        bais = new ByteArrayInputStream( workItemByteArray );
                        context = new ProtobufMarshallerReaderContext( bais,
                                                               kBase,
                                                               null,
                                                               null,
                                                               null,
                                                               env);

                        workItem = readWorkItem(context);
                    } catch (IOException e1) {
                        logger.error("Unable to read work item with InputMarshaller", e1);
                        // throw the original exception produced by failed protobuf op
                        throw new RuntimeException("Unable to read work item ", e);
                    }
                }

                context.close();
            } catch ( IOException e ) {
                logger.error("Exception", e);
                throw new IllegalArgumentException( "IOException while loading work item: " + e.getMessage() );
            }
        }
        return workItem;
    }

    private static WorkItem readWorkItem( MarshallerReaderContext context ) throws IOException {
        ObjectInputStream stream = (ObjectInputStream) context;

        WorkItemImpl workItem = new WorkItemImpl();
        workItem.setId( stream.readLong() );
        workItem.setProcessInstanceId( stream.readUTF() );
        workItem.setName( stream.readUTF() );
        workItem.setState( stream.readInt() );

        //WorkItem Paramaters
        int nbVariables = stream.readInt();
        if (nbVariables > 0) {

            for (int i = 0; i < nbVariables; i++) {
                String name = stream.readUTF();
                try {
                    int index = stream.readInt();
                    ObjectMarshallingStrategy strategy = null;
                    // Old way of retrieving strategy objects
                    if (index >= 0) {
                        strategy = context.getResolverStrategyFactory().getStrategy( index );
                        if (strategy == null) {
                            throw new IllegalStateException( "No strategy of with index " + index + " available." );
                        }
                    }
                    // New way
                    else if (index == -2) {
                        String strategyClassName = stream.readUTF();
                        // fix for backwards compatibility (5.x -> 6.x)
                        if ("org.drools.marshalling.impl.SerializablePlaceholderResolverStrategy".equals(strategyClassName)) {
                            strategyClassName = "org.drools.core.marshalling.impl.SerializablePlaceholderResolverStrategy";
                        }
                        strategy = context.getResolverStrategyFactory().getStrategyObject( strategyClassName );
                        if (strategy == null) {
                            throw new IllegalStateException( "No strategy of type " + strategyClassName + " available." );
                        }
                    } else {
                        throw new IllegalStateException( "Wrong index of strategy field read: " + index + "!");
                    }

                    Object value = strategy.read( stream );
                    workItem.setParameter( name,
                            value );
                } catch (ClassNotFoundException e) {
                    throw new IllegalArgumentException(
                            "Could not reload variable " + name );
                }
            }
        }

        return workItem;
    }

//    @PreUpdate
    @Override
    public void transform() {
        this.state = workItem.getState();



        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            MarshallerWriteContext context = new ProtobufMarshallerWriteContext( baos,
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
