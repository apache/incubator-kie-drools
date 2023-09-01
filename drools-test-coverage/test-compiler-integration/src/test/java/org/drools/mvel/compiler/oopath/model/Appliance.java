package org.drools.mvel.compiler.oopath.model;

import org.drools.core.phreak.AbstractReactiveObject;

public class Appliance extends AbstractReactiveObject {
    private boolean on;

    public Appliance() {
        on = false;
    }

    public Appliance(boolean on) {
        this.on = on;
        notifyModification();
    }

    public boolean isOn() {
        return on;
    }

    public void setOn(boolean on) {
        this.on = on;
    }
}
