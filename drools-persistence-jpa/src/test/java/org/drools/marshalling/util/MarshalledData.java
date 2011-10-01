package org.drools.marshalling.util;

import static org.drools.marshalling.util.MarshallingTestUtil.*;
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
        // OCRAM: do snapshot numbers need to be specific to marshalled class type? 
        if( testMethodSnapshotNumMap.get(this.testMethodName) == null ) { 
            testMethodSnapshotNumMap.put(this.testMethodName, new AtomicInteger(-1));
        }
        this.snapshotNumber = testMethodSnapshotNumMap.get(this.testMethodName).incrementAndGet();
            
        String className = marshalledClassInstance.getClass().getName();
        this.marshalledObjectClassName = className;
        
        // OCRAM: use this.. (Workitem/ProcessInstance)
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
        else if( className.equals("org.jbpm.persistence.processinstance.ProcessInstanceInfo") ) { 
            Class processInstanceInfoClass = null;
            try {
                processInstanceInfoClass = Class.forName(className);
                Method getByteArrayMethod = processInstanceInfoClass.getMethod("getProcessInstanceByteArray", null);
                Object byteArrayObject = getByteArrayMethod.invoke(marshalledClassInstance, null);
                this.rulesByteArray = (byte []) byteArrayObject;
                
                Method getIdMethod = processInstanceInfoClass.getMethod("getId", null);
                Object idObject = getIdMethod.invoke(marshalledClassInstance, null);
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
        StringBuffer string = new StringBuffer();
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
