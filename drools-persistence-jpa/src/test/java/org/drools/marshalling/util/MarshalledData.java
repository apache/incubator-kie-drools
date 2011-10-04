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

import static org.junit.Assert.*;
import static org.drools.marshalling.util.MarshallingTestUtil.*;
import static org.drools.marshalling.util.MarshallingDBUtil.*;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.SequenceGenerator;
import javax.persistence.Transient;
import javax.persistence.Version;

import junit.framework.Assert;


import org.drools.persistence.info.SessionInfo;
import org.drools.persistence.info.WorkItemInfo;

@Entity
@SequenceGenerator(name="marshalledDataIdSeq", sequenceName="MARSHALLEDDATA_ID_SEQ")
public class MarshalledData {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO, generator="marshalledDataIdSeq")
    public Integer id;
    
    @Lob
    public byte[] rulesByteArray;
    
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
        // OCRAM: do snapshot numbers need to be specific to marshalled class type? 
        if( testMethodSnapshotNumMap.get(this.testMethodName) == null ) { 
            testMethodSnapshotNumMap.put(this.testMethodName, new AtomicInteger(-1));
        }
        this.snapshotNumber = testMethodSnapshotNumMap.get(this.testMethodName).incrementAndGet();
            
        String className = marshalledClassInstance.getClass().getName();
        this.marshalledObjectClassName = className;
        
        if( className.equals(SessionInfo.class.getName()) ) { 
            SessionInfo sessionInfo = (SessionInfo) marshalledClassInstance;
            this.rulesByteArray = sessionInfo.getData();
            this.marshalledObjectId = sessionInfo.getId().longValue();
        }
        else if( className.equals(WorkItemInfo.class.getName()) ) { 
           WorkItemInfo workItemInfo = (WorkItemInfo) marshalledClassInstance;
           this.rulesByteArray = workItemInfo.getWorkItemByteArray();
           this.marshalledObjectId = workItemInfo.getId();
        }
        else if( PROCESS_INSTANCE_INFO_CLASS_NAME.equals(className) ) { 
            Class<?> processInstanceInfoClass = null;
            try {
                this.rulesByteArray = getProcessInstanceInfoByteArray(marshalledClassInstance);
                
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
        if( rulesByteArray != null ) { 
            string.append(byteArrayHashCode(rulesByteArray));
        }
        string.append( ":" );
        string.append( (testMethodName != null ? testMethodName : "") + ":" );
        string.append( (snapshotNumber != null ? snapshotNumber : "") + ":" );
        string.append( (marshalledObjectClassName != null ? marshalledObjectClassName : "") + ":" );
        string.append( (marshalledObjectId != null ? marshalledObjectId : "") );
       
        return string.toString();
    }
    

}
