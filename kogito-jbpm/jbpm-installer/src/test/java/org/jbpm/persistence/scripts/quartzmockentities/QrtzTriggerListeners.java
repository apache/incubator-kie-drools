package org.jbpm.persistence.scripts.quartzmockentities;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity(name = "qrtz_trigger_listeners")
public class QrtzTriggerListeners {

    @Id
    private Long id;

    public QrtzTriggerListeners() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
