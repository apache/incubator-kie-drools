package org.drools.persistence.session;

import java.util.Arrays;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.PostLoad;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.drools.marshalling.impl.MarshallingConfiguration;

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
    private int                versionShadow;
    @Transient
    private Date               lastModificationDateShadow;  
    @Transient
    private byte[]             rulesByteArrayShadow;

    @Transient
    JPASessionMarshallingHelper helper;
    
    public SessionInfo() {
        this.startDate = new Date();
    }

    public int getId() {
        return this.id;
    }
    
//    public int getVersion() {
//        return this.version;
//    }

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
    
    @PostLoad
    public void postLoad() {
        this.lastModificationDateShadow = this.lastModificationDate;       
        this.rulesByteArrayShadow = this.rulesByteArray;
//        this.versionShadow = this.version;
    }
    
    @PrePersist 
    @PreUpdate 
    public void update() {
        // this would not be called unless we had an last modification date change
        this.rulesByteArray  = this.helper.getSnapshot();
//        this.lastModificationDateShadow = this.lastModificationDate;       
//        this.rulesByteArrayShadow = this.rulesByteArray;
    }
    
//    public void rollback() {
//        this.lastModificationDate = this.lastModificationDateShadow;
//        this.rulesByteArray = this.rulesByteArrayShadow;
////        this.version = this.versionShadow;
//    }

}
