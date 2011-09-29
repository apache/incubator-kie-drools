package org.drools.marshalling.util;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.SequenceGenerator;
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
    public Integer snapshotNumber;
   
    public MarshalledData(SessionInfo sessionInfo) { 
        this.rulesByteArray = sessionInfo.getData();
    }

    public MarshalledData() { 
        // default constructor
    }
    
}
