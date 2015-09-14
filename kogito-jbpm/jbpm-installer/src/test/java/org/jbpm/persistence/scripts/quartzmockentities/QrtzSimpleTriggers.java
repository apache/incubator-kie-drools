package org.jbpm.persistence.scripts.quartzmockentities;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity(name = "qrtz_simple_triggers")
public class QrtzSimpleTriggers {

    @Id
    private Long id;

    public QrtzSimpleTriggers() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
