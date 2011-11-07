/*
 * Copyright 2011 Red Hat Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.marshalling.util;

import static org.drools.marshalling.util.MarshallingTestUtil.*;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.SequenceGenerator;
import javax.persistence.Transient;

import junit.framework.Assert;

import org.drools.KnowledgeBase;
import org.drools.core.util.DroolsStreamUtils;
import org.drools.persistence.info.SessionInfo;
import org.drools.persistence.info.WorkItemInfo;

@Entity
@SequenceGenerator(name="marshalledDataIdSeq", sequenceName="MARSHALLEDDATA_ID_SEQ")
public class MarshalledData {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO, generator="marshalledDataIdSeq")
    public Integer id;
    
    @Lob
    public byte[] byteArray;
    
    @Lob
    public byte[] serializedKnowledgeBase;
    
    public String testMethodName;
    public Integer snapshotNumber;
  
    public String marshalledObjectClassName;
    public Long marshalledObjectId;
    
    @Transient
    private static HashMap<String, AtomicInteger> testMethodSnapshotNumMap = new HashMap<String, AtomicInteger>();
    
    public MarshalledData(Object marshalledClassInstance) { 
        this.testMethodName = getTestMethodName();
        initializeObject(marshalledClassInstance);
    }
    
    public MarshalledData(String testMethodName, Object marshalledClassInstance) { 
        if( testMethodName != null ) { 
            this.testMethodName = getTestMethodName();
        }
        else { 
            this.testMethodName = testMethodName;
        }
        
        initializeObject(marshalledClassInstance);
    }
    
    private void initializeObject(Object marshalledClassInstance) { 
        // snapshot number
        if( testMethodSnapshotNumMap.get(this.testMethodName) == null ) { 
            testMethodSnapshotNumMap.put(this.testMethodName, new AtomicInteger(-1));
        }
        this.snapshotNumber = testMethodSnapshotNumMap.get(this.testMethodName).incrementAndGet();

        // marshalled object class name
        String className = marshalledClassInstance.getClass().getName();
        this.marshalledObjectClassName = className;
       
        // Object specific actions
        if( className.equals(SessionInfo.class.getName()) ) { 
            SessionInfo sessionInfo = (SessionInfo) marshalledClassInstance;
            this.byteArray = sessionInfo.getData();
            this.marshalledObjectId = sessionInfo.getId().longValue();
            try { 
                storeAssociatedKnowledgeBase(sessionInfo);
            } catch(IOException ioe ) { 
                Assert.fail("Unable to retrieve marshalled data or id for " + className + " object: [" 
                        + ioe.getClass().getSimpleName() + ", " + ioe.getMessage() );
            }
        }
        else if( className.equals(WorkItemInfo.class.getName()) ) { 
           WorkItemInfo workItemInfo = (WorkItemInfo) marshalledClassInstance;
           this.byteArray = workItemInfo.getWorkItemByteArray();
           this.marshalledObjectId = workItemInfo.getId();
        }
        else if( PROCESS_INSTANCE_INFO_CLASS_NAME.equals(className) ) { 
            Class<?> processInstanceInfoClass = null;
            try {
                this.byteArray = getProcessInstanceInfoByteArray(marshalledClassInstance);
                
                processInstanceInfoClass = Class.forName(className);
                Method getIdMethod = processInstanceInfoClass.getMethod("getId", (Class []) null);
                Object idObject = getIdMethod.invoke(marshalledClassInstance, (Object []) null);
                this.marshalledObjectId = (Long) idObject;
            } catch (Exception e) {
                Assert.fail("Unable to retrieve marshalled data or id for " + className + " object: [" 
                        + e.getClass().getSimpleName() + ", " + e.getMessage() );
            } 
        }
    }

    private void storeAssociatedKnowledgeBase(SessionInfo sessionInfo) throws IOException { 
       KnowledgeBase kbase = sessionInfo.getJPASessionMashallingHelper().getKbase();
       this.serializedKnowledgeBase = DroolsStreamUtils.streamOut(kbase);
    }
    
    public static Integer getCurrentTestMethodSnapshotNumber() { 
       String testMethodName = getTestMethodName();
       if( testMethodSnapshotNumMap.get(testMethodName) != null ) { 
          return testMethodSnapshotNumMap.get(testMethodName).intValue(); 
       }
       return null;
    }
    
    public MarshalledData() { 
        // default constructor
    }
    
    public String getTestMethodAndSnapshotNum() { 
       return this.testMethodName + ":" + this.snapshotNumber;
    }
    
    public String toString() { 
        StringBuilder string = new StringBuilder();
        string.append( (id != null ? id : "") + ":");
        if( byteArray != null ) { 
            string.append(byteArrayHashCode(byteArray));
        }
        string.append( ":" );
        string.append( (testMethodName != null ? testMethodName : "") + ":" );
        string.append( (snapshotNumber != null ? snapshotNumber : "") + ":" );
        string.append( (marshalledObjectClassName != null ? marshalledObjectClassName : "") + ":" );
        string.append( (marshalledObjectId != null ? marshalledObjectId : "") );
       
        return string.toString();
    }
    

}
