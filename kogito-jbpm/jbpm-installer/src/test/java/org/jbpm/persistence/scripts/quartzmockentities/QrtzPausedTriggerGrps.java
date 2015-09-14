package org.jbpm.persistence.scripts.quartzmockentities;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity(name = "qrtz_paused_trigger_grps")
public class QrtzPausedTriggerGrps {

    @Id
    private Long id;

    public QrtzPausedTriggerGrps() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
