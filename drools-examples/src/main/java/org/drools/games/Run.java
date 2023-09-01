package org.drools.games;

import org.kie.api.definition.type.ClassReactive;

@ClassReactive
public class Run {
    private int cycle;

    public Run() {

    }

    public void incrementCycle() {
        cycle++;
    }

    public int getCycle() {
        return cycle;
    }

}
