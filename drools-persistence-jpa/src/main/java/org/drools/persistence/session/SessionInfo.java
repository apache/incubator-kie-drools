package org.drools.persistence.session;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Transient;
import javax.persistence.Version;

@Entity
public class SessionInfo {
    private @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    int                        id;

    @Version
    @Column(name = "OPTLOCK")     
    private int                version;

    private Date               startDate;
    private Date               lastModificationDate;    
    
    @Lob
    private byte[]             rulesByteArray;

    @Transient
    JPASessionMarshallingHelper helper;
    
    public SessionInfo() {
        this.startDate = new Date();
    }

    public int getId() {
        return this.id;
    }
    
    public int getVersion() {
        return this.version;
    }

    public void setJPASessionMashallingHelper(JPASessionMarshallingHelper helper) {
        this.helper = helper;
    }

    public JPASessionMarshallingHelper getJPASessionMashallingHelper() {
        return helper;
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
    

    @PrePersist 
    @PreUpdate 
    public void update() {
        this.rulesByteArray  = this.helper.getSnapshot();
    }

}
