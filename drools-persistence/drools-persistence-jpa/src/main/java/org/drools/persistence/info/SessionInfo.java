package org.drools.persistence.info;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.SequenceGenerator;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.drools.persistence.api.PersistentSession;
import org.drools.persistence.SessionMarshallingHelper;

@Entity
@SequenceGenerator(name="sessionInfoIdSeq", sequenceName="SESSIONINFO_ID_SEQ")
public class SessionInfo implements PersistentSession {
    
    private @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator="sessionInfoIdSeq")
    Long                        id;

    @Version
    @Column(name = "OPTLOCK")     
    private int                version;

    private Date               startDate;
    private Date               lastModificationDate;
    
    @Lob
    @Column(length=2147483647)
    private byte[]             rulesByteArray;

    @Transient
    SessionMarshallingHelper helper;
    
    public SessionInfo() {
        this.startDate = new Date();
    }

    public Long getId() {
        return this.id;
    }
    
    public int getVersion() {
        return this.version;
    }

    public void setJPASessionMashallingHelper(SessionMarshallingHelper helper) {
        this.helper = helper;
    }

    public SessionMarshallingHelper getJPASessionMashallingHelper() {
        return helper;
    }
    
    public void setData( byte[] data) {
        this.rulesByteArray = data;
    }
    
    public byte[] getData() {
        return this.rulesByteArray;
    }
    
    public Date getStartDate() {
        return this.startDate;
    }

    public Date getLastModificationDate() {
        return this.lastModificationDate;
    }

    public void setLastModificationDate(Date date) {
        this.lastModificationDate = date;
    }

    @Override
    public void transform() {
        this.rulesByteArray  = this.helper.getSnapshot();
    }

    public void setId(Long ksessionId) {
        this.id = ksessionId;
    }

}
