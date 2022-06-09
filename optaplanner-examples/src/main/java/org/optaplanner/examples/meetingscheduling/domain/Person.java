package org.optaplanner.examples.meetingscheduling.domain;

import org.optaplanner.examples.common.domain.AbstractPersistable;
import org.optaplanner.examples.common.swingui.components.Labeled;

public class Person extends AbstractPersistable implements Labeled {

    private String fullName;

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    @Override
    public String getLabel() {
        return fullName;
    }

    @Override
    public String toString() {
        return fullName;
    }

}
