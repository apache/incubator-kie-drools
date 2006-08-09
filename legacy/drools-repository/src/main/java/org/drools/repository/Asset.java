package org.drools.repository;

import java.io.Serializable;
import java.util.Date;

/** The layer supertype for repository persistable classes. */
public class Asset
    implements
    Serializable {

    private Long   id;
    private String lastSavedByUser;
    private Date   lastSavedDate;

    public Long getId() {
        return id;
    }

    private void setId(Long id) {
        this.id = id;
    }

    /** If the repository is audited, this will show the user who saved the record last. */
    public String getLastSavedByUser() {
        return lastSavedByUser;
    }

    void setLastSavedByUser(String lastSavedByUser) {
        this.lastSavedByUser = lastSavedByUser;
    }

    /** If auditing is enabled, this will be set on save */
    public Date getLastSavedDate() {
        return lastSavedDate;
    }

    void setLastSavedDate(Date lastSavedDate) {
        this.lastSavedDate = lastSavedDate;
    }

}
