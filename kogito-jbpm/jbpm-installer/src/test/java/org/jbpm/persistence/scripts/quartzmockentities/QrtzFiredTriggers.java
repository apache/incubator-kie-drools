package org.jbpm.persistence.scripts.quartzmockentities;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity(name = "qrtz_fired_triggers")
public class QrtzFiredTriggers {

    @Id
    private Long id;

    public QrtzFiredTriggers() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
