package org.drools.marshalling.util;

import static org.drools.marshalling.util.MarshallingTestUtil.*;
import java.io.UnsupportedEncodingException;
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


import org.drools.persistence.info.SessionInfo;

@Entity
@SequenceGenerator(name="marshalledDataIdSeq", sequenceName="MARSHALLEDDATA_ID_SEQ")
public class MarshalledData {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO, generator="marshalledDataIdSeq")
    public Integer id;
    
    @Lob
    public byte[] rulesByteArray;
    
    public String testMethodName;
    public Integer sessionInfoId;
    public Integer snapshotNumber;
  
    @Transient
    private static HashMap<String, AtomicInteger> testMethodSnapshotNumMap = new HashMap<String, AtomicInteger>();
    
    public MarshalledData(SessionInfo sessionInfo) { 
        this.rulesByteArray = sessionInfo.getData().clone();
        this.sessionInfoId = sessionInfo.getId();
        this.testMethodName = getTestMethodName();
        if( testMethodSnapshotNumMap.get(this.testMethodName) == null ) { 
           testMethodSnapshotNumMap.put(this.testMethodName, new AtomicInteger(-1));
        }
        this.snapshotNumber = testMethodSnapshotNumMap.get(this.testMethodName).incrementAndGet();
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
        string.append( (sessionInfoId != null ? sessionInfoId : "") + ":" );
        string.append( (snapshotNumber != null ? snapshotNumber : "") );
       
        return string.toString();
    }
    

}
