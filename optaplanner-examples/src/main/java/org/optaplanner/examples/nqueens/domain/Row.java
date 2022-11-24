package org.optaplanner.examples.nqueens.domain;

import org.optaplanner.examples.common.domain.AbstractPersistableJackson;
import org.optaplanner.examples.common.swingui.components.Labeled;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@JsonIdentityInfo(scope = Row.class, generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Row extends AbstractPersistableJackson implements Labeled {

    private int index;

    public Row() { // For Jackson.
    }

    public Row(int index) {
        super(index);
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
        return "Row " + index;
    }

    @Override
    public String toString() {
        return "Row-" + index;
    }

}
