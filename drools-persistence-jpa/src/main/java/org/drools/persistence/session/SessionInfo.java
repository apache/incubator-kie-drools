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

//    @Version
//    @Column(name = "OPTLOCK")     
//    private int                version;

    private Date               startDate;
    private Date               lastModificationDate;    
    private boolean            dirty;         
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

    public void setDirty() {
        this.dirty = true;
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
        // we always increase the last modification date for each action, so we know there will be an update
        byte[] newByteArray = this.helper.getSnapshot();
        if ( !Arrays.equals( newByteArray,
                             this.rulesByteArray ) ) {
            this.lastModificationDate = new Date();
            this.rulesByteArray = newByteArray;
        }
        this.lastModificationDateShadow = this.lastModificationDate;       
        this.rulesByteArrayShadow = this.rulesByteArray;
        this.dirty = false;
    }
    
    public void rollback() {
        this.dirty = false;
        this.lastModificationDate = this.lastModificationDateShadow;
        this.rulesByteArray = this.rulesByteArrayShadow;
//        this.version = this.versionShadow;
    }

}
