package org.jbpm.runtime.manager.impl.jpa;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Version;

@Entity
@SequenceGenerator(name="contextMappingInfoIdSeq", sequenceName="CONTEXT_MAPPING_INFO_ID_SEQ")
@NamedQueries(value=
    {@NamedQuery(name="FindContextMapingByContextId", 
                query="from ContextMappingInfo where contextId = :contextId"),
                @NamedQuery(name="FindContextMapingByKSessionId", 
                query="from ContextMappingInfo where ksessionId = :ksessionId")})
public class ContextMappingInfo implements Serializable {

    private static final long serialVersionUID = 533985957655465840L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator="contextMappingInfoIdSeq")
    private Long mappingId;

    @Version
    @Column(name = "OPTLOCK")
    private int version;
    
    @Column(name="CONTEXT_ID", nullable=false)
    private String contextId;
    @Column(name="KSESSION_ID", nullable=false)
    private Integer ksessionId;
    
    public ContextMappingInfo() {
        
    }

    public ContextMappingInfo(String contextId, Integer ksessionId) {
        this.contextId = contextId;
        this.ksessionId = ksessionId;
    }

    public Long getMappingId() {
        return mappingId;
    }

    public void setMappingId(Long mappingId) {
        this.mappingId = mappingId;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getContextId() {
        return contextId;
    }

    public void setContextId(String contextId) {
        this.contextId = contextId;
    }

    public Integer getKsessionId() {
        return ksessionId;
    }

    public void setKsessionId(Integer ksessionId) {
        this.ksessionId = ksessionId;
    }
    
    

}
