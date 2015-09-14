package org.jbpm.persistence.scripts.quartzmockentities;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity(name = "qrtz_blob_triggers")
public class QrtzBlobTriggers {

    @Id
    private Long id;

    public QrtzBlobTriggers() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
