package org.optaplanner.examples.nqueens.domain;

import org.optaplanner.examples.common.domain.AbstractPersistable;
import org.optaplanner.examples.common.swingui.components.Labeled;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("Row")
public class Row extends AbstractPersistable implements Labeled {

    private int index;

    public Row() {
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
