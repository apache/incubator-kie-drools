package org.drools.persistence.processinstance.variabletypes;

import javax.persistence.Entity;
import javax.persistence.Transient;

/**
 * 
 * @author <a href="mailto:kris_verlaenen@hotmail.com">Kris Verlaenen</a>
 * @author salaboy
 */
@Entity
public class JPAPersistedVariable extends VariableInstanceInfo {

	private static final long serialVersionUID = 300L;

	@Transient
	private Object entity;
	private String entityClass;
	private Long entityId;

	public String getEntityClass() {
		return entityClass;
	}

	public void setEntityClass(String entityClass) {
		this.entityClass = entityClass;
	}

	public Long getEntityId() {
		return entityId;
	}

	public void setEntityId(Long varid) {
		this.entityId = varid;
	}

	public Object getEntity() {
		return entity;
	}

	public void setEntity(Object entity) {
		this.entity = entity;
	}
	
	public String toString() {
		return super.toString() + " entityId=" + entityId + " entityClass=" + entityClass;
	}
	
    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj)) {
            return false;
        }
        final JPAPersistedVariable other = (JPAPersistedVariable) obj;
        if (this.entityId != other.entityId && (this.entityId == null || !this.entityId.equals(other.entityId))) {
            return false;
        }
        if ((this.entityClass == null) ? (other.entityClass != null) : !this.entityClass.equals(other.entityClass)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash = 41 * hash + (this.entityId != null ? this.entityId.hashCode() : 0);
        hash = 41 * hash + (this.entityClass != null ? this.entityClass.hashCode() : 0);
        return hash;
    }
}
