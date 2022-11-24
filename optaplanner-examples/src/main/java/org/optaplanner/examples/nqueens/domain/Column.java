package org.optaplanner.examples.nqueens.domain;

import org.optaplanner.examples.common.domain.AbstractPersistableJackson;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@JsonIdentityInfo(scope = Column.class, generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Column extends AbstractPersistableJackson {

    private int index;

    public Column() { // For Jackson.
    }

    public Column(int index) {
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
    public String toString() {
        return "Column-" + index;
    }

}
