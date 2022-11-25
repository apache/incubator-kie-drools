package org.optaplanner.examples.pas.domain;

import org.optaplanner.examples.common.domain.AbstractPersistable;
import org.optaplanner.examples.common.swingui.components.Labeled;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@JsonIdentityInfo(scope = Night.class, generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Night extends AbstractPersistable implements Labeled {

    private int index;

    public Night() {
    }

    public Night(int index) {
        this(index, index);
    }

    public Night(long id, int index) {
        super(id);
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    @Override
    public String getLabel() {
        return (index + 1) + "-JAN";
    }

    @Override
    public String toString() {
        return Integer.toString(index);
    }

}
